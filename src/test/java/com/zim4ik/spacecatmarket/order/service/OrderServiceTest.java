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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private OrderService orderService;

    private Product product;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        product = Product.create("Galaxy Cat", BigDecimal.valueOf(100));
        order = Order.create(List.of(OrderItem.create(product, 2)));
        orderDTO = new OrderDTO(1L, order.getOrderNumber(), List.of(new OrderItemDTO(1L, 2)));
    }

    @Test
    void createOrder_savesOrderWithResolvedItems() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.orderToOrderDto(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertThat(result).isEqualTo(orderDTO);
    }

    @Test
    void createOrder_whenProductMissing_throwsProductNotFoundException() {
        OrderDTO dto = new OrderDTO(null, null, List.of(new OrderItemDTO(404L, 1)));
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(ProductNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void getAllOrders_returnsMappedList() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDto(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getAllOrders();

        assertThat(result).containsExactly(orderDTO);
    }

    @Test
    void getOrderById_whenFound_returnsMappedOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderDto(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.getOrderById(1L);

        assertThat(result).isEqualTo(orderDTO);
    }

    @Test
    void getOrderById_whenNotFound_throwsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(99L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void updateOrder_whenFound_updatesItems() {
        Product newProduct = Product.create("Comet Cat", BigDecimal.valueOf(50));
        OrderDTO updateDTO = new OrderDTO(1L, order.getOrderNumber(), List.of(new OrderItemDTO(2L, 3)));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(2L)).thenReturn(Optional.of(newProduct));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.orderToOrderDto(order)).thenReturn(updateDTO);

        OrderDTO result = orderService.updateOrder(updateDTO);

        assertThat(result).isEqualTo(updateDTO);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getProduct()).isEqualTo(newProduct);
        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void updateOrder_whenNotFound_throwsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        OrderDTO missingDTO = new OrderDTO(99L, null, List.of(new OrderItemDTO(1L, 1)));

        assertThatThrownBy(() -> orderService.updateOrder(missingDTO))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_whenFound_deletesOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_whenNotFound_throwsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteOrder(99L))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository, never()).delete(any());
    }

    @Test
    void getMostPurchasedProducts_delegatesToRepository() {
        ProductPurchaseCountProjection projection = mock(ProductPurchaseCountProjection.class);
        when(orderItemRepository.findMostPurchasedProducts()).thenReturn(List.of(projection));

        List<ProductPurchaseCountProjection> result = orderService.getMostPurchasedProducts();

        assertThat(result).containsExactly(projection);
    }

    @Test
    void getMostPurchasedProductsByCategory_delegatesToRepository() {
        ProductPurchaseCountProjection projection = mock(ProductPurchaseCountProjection.class);
        when(orderItemRepository.findMostPurchasedProductsByCategory(5L)).thenReturn(List.of(projection));

        List<ProductPurchaseCountProjection> result = orderService.getMostPurchasedProductsByCategory(5L);

        assertThat(result).containsExactly(projection);
    }
}
