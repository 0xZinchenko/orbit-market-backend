package com.zim4ik.spacecatmarket.order.service;

import com.zim4ik.spacecatmarket.order.dto.OrderDTO;
import com.zim4ik.spacecatmarket.order.dto.OrderItemDTO;
import com.zim4ik.spacecatmarket.order.exception.OrderNotFoundException;
import com.zim4ik.spacecatmarket.order.mapper.OrderMapper;
import com.zim4ik.spacecatmarket.order.model.Order;
import com.zim4ik.spacecatmarket.order.model.OrderItem;
import com.zim4ik.spacecatmarket.order.repository.OrderItemRepository;
import com.zim4ik.spacecatmarket.order.repository.OrderRepository;
import com.zim4ik.spacecatmarket.order.repository.projection.ProductPurchaseCountProjection;
import com.zim4ik.spacecatmarket.product.exception.ProductNotFoundException;
import com.zim4ik.spacecatmarket.product.model.Product;
import com.zim4ik.spacecatmarket.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final EntityManager entityManager;

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        List<OrderItem> items = orderDTO.items().stream()
                .map(this::toOrderItem)
                .toList();
        Order order = Order.create(items);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.orderToOrderDto(savedOrder);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::orderToOrderDto)
                .toList();
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::orderToOrderDto)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public OrderDTO getOrderByNumber(String orderNumber) {
        Order order = entityManager.unwrap(Session.class)
                .byNaturalId(Order.class)
                .using("orderNumber", orderNumber)
                .load();

        if (order == null) {
            throw new OrderNotFoundException(orderNumber);
        }

        return orderMapper.orderToOrderDto(order);
    }

    @Transactional
    public OrderDTO updateOrder(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.id())
                .orElseThrow(() -> new OrderNotFoundException(orderDTO.id()));

        List<OrderItem> items = orderDTO.items().stream()
                .map(this::toOrderItem)
                .toList();
        order.updateItems(items);

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.orderToOrderDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(id)
                );

        orderRepository.delete(order);
    }

    public List<ProductPurchaseCountProjection> getMostPurchasedProducts() {
        return orderItemRepository.findMostPurchasedProducts();
    }

    public List<ProductPurchaseCountProjection> getMostPurchasedProductsByCategory(Long categoryId) {
        return orderItemRepository.findMostPurchasedProductsByCategory(categoryId);
    }

    private OrderItem toOrderItem(OrderItemDTO dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException(dto.productId()));
        return OrderItem.create(product, dto.quantity());
    }
}
