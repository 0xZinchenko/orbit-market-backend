package com.zim4ik.spacecatmarket.order.repository;

import com.zim4ik.spacecatmarket.order.dto.OrderDTO;
import com.zim4ik.spacecatmarket.order.dto.OrderItemDTO;
import com.zim4ik.spacecatmarket.order.exception.OrderNotFoundException;
import com.zim4ik.spacecatmarket.order.service.OrderService;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.service.ProductService;
import com.zim4ik.spacecatmarket.testsupport.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class OrderRepositoryIT extends AbstractPostgresIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Test
    void createReadUpdateDelete_roundTripsThroughRealPostgres() {
        ProductDTO product = productService.createProduct(
                new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), null));

        OrderDTO created = orderService.createOrder(
                new OrderDTO(null, null, List.of(new OrderItemDTO(product.id(), 2))));
        assertThat(created.id()).isNotNull();
        assertThat(created.orderNumber()).isNotBlank();

        OrderDTO fetched = orderService.getOrderById(created.id());
        assertThat(fetched.items()).hasSize(1);
        assertThat(fetched.items().get(0).quantity()).isEqualTo(2);

        ProductDTO secondProduct = productService.createProduct(
                new ProductDTO(null, "Comet Cat", BigDecimal.valueOf(50), null));
        OrderDTO updated = orderService.updateOrder(
                new OrderDTO(created.id(), null, List.of(new OrderItemDTO(secondProduct.id(), 5))));
        assertThat(updated.items()).hasSize(1);
        assertThat(updated.items().get(0).productId()).isEqualTo(secondProduct.id());
        assertThat(updated.items().get(0).quantity()).isEqualTo(5);

        orderService.deleteOrder(created.id());
        assertThatThrownBy(() -> orderService.getOrderById(created.id()))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
