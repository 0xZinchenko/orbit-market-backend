package com.zim4ik.spacecatmarket.order.service;

import com.zim4ik.spacecatmarket.order.dto.OrderDTO;
import com.zim4ik.spacecatmarket.order.dto.OrderItemDTO;
import com.zim4ik.spacecatmarket.order.exception.OrderNotFoundException;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderServiceNaturalIdTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Test
    void getOrderByNumber_whenOrderExists_findsItByNaturalId() {
        ProductDTO product = productService.createProduct(
                new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), null));
        OrderDTO created = orderService.createOrder(
                new OrderDTO(null, null, List.of(new OrderItemDTO(product.id(), 2))));

        OrderDTO found = orderService.getOrderByNumber(created.orderNumber());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.orderNumber()).isEqualTo(created.orderNumber());
    }

    @Test
    void getOrderByNumber_whenMissing_throwsOrderNotFoundException() {
        assertThatThrownBy(() -> orderService.getOrderByNumber("does-not-exist"))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
