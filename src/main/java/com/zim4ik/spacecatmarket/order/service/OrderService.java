package com.zim4ik.spacecatmarket.order.service;

import com.zim4ik.spacecatmarket.order.dto.OrderDTO;
import com.zim4ik.spacecatmarket.order.exception.OrderNotFoundException;
import com.zim4ik.spacecatmarket.order.mapper.OrderMapper;
import com.zim4ik.spacecatmarket.order.model.Order;
import com.zim4ik.spacecatmarket.order.repository.OrderRepository;
import com.zim4ik.spacecatmarket.product.model.Product;
import com.zim4ik.spacecatmarket.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderDTO createOrder(OrderDTO orderDTO) {
        List<Product> products = productRepository.findAllById(orderDTO.productIds());
        Order order = Order.create(products);
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

    public OrderDTO updateOrder(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.id())
                .orElseThrow(() -> new OrderNotFoundException(orderDTO.id()));

        List<Product> products = productRepository.findAllById(orderDTO.productIds());
        order.updateProducts(products);

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.orderToOrderDto(updatedOrder);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        orderRepository.delete(order);
    }
}
