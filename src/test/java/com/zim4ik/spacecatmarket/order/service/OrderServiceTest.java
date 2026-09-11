package com.zim4ik.spacecatmarket.order.service;

import com.zim4ik.spacecatmarket.order.dto.OrderDTO;
import com.zim4ik.spacecatmarket.order.exception.OrderNotFoundException;
import com.zim4ik.spacecatmarket.order.mapper.OrderMapper;
import com.zim4ik.spacecatmarket.order.model.Order;
import com.zim4ik.spacecatmarket.order.repository.OrderRepository;
import com.zim4ik.spacecatmarket.product.model.Product;
import com.zim4ik.spacecatmarket.product.repository.ProductRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Product product;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        product = Product.create("Galaxy Cat", BigDecimal.valueOf(100));
        order = Order.create(List.of(product));
        orderDTO = new OrderDTO(1L, List.of(1L));
    }

    @Test
    void createOrder_savesOrderWithResolvedProducts() {
        when(productRepository.findAllById(List.of(1L))).thenReturn(List.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.orderToOrderDto(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertThat(result).isEqualTo(orderDTO);
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
    void updateOrder_whenFound_updatesProducts() {
        Product newProduct = Product.create("Comet Cat", BigDecimal.valueOf(50));
        OrderDTO updateDTO = new OrderDTO(1L, List.of(2L));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(newProduct));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.orderToOrderDto(order)).thenReturn(updateDTO);

        OrderDTO result = orderService.updateOrder(updateDTO);

        assertThat(result).isEqualTo(updateDTO);
        assertThat(order.getProducts()).containsExactly(newProduct);
    }

    @Test
    void updateOrder_whenNotFound_throwsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        OrderDTO missingDTO = new OrderDTO(99L, List.of(1L));

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
}
