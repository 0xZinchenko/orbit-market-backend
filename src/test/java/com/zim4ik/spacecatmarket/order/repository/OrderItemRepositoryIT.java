package com.zim4ik.spacecatmarket.order.repository;

import com.zim4ik.spacecatmarket.category.dto.CategoryDTO;
import com.zim4ik.spacecatmarket.category.service.CategoryService;
import com.zim4ik.spacecatmarket.order.dto.OrderDTO;
import com.zim4ik.spacecatmarket.order.dto.OrderItemDTO;
import com.zim4ik.spacecatmarket.order.repository.projection.ProductPurchaseCountProjection;
import com.zim4ik.spacecatmarket.order.service.OrderService;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderItemRepositoryIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Test
    void findMostPurchasedProducts_ordersByTotalQuantityDescending() {
        ProductDTO galaxyCat = productService.createProduct(
                new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), null));
        ProductDTO cometCat = productService.createProduct(
                new ProductDTO(null, "Comet Cat", BigDecimal.valueOf(50), null));

        orderService.createOrder(new OrderDTO(null, null, List.of(
                new OrderItemDTO(galaxyCat.id(), 2),
                new OrderItemDTO(cometCat.id(), 1))));
        orderService.createOrder(new OrderDTO(null, null, List.of(
                new OrderItemDTO(galaxyCat.id(), 5))));

        List<ProductPurchaseCountProjection> result = orderService.getMostPurchasedProducts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductId()).isEqualTo(galaxyCat.id());
        assertThat(result.get(0).getProductName()).isEqualTo("Galaxy Cat");
        assertThat(result.get(0).getTotalQuantity()).isEqualTo(7L);
        assertThat(result.get(1).getProductId()).isEqualTo(cometCat.id());
        assertThat(result.get(1).getTotalQuantity()).isEqualTo(1L);
    }

    @Test
    void findMostPurchasedProductsByCategory_filtersByCategory() {
        CategoryDTO snacks = categoryService.createCategory(
                new CategoryDTO(null, "Snacks", "Cosmic snacks for space cats"));
        ProductDTO galaxyCat = productService.createProduct(
                new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), snacks.id()));
        ProductDTO cometCat = productService.createProduct(
                new ProductDTO(null, "Comet Cat", BigDecimal.valueOf(50), null));

        orderService.createOrder(new OrderDTO(null, null, List.of(
                new OrderItemDTO(galaxyCat.id(), 3),
                new OrderItemDTO(cometCat.id(), 10))));

        List<ProductPurchaseCountProjection> result = orderService.getMostPurchasedProductsByCategory(snacks.id());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(galaxyCat.id());
        assertThat(result.get(0).getTotalQuantity()).isEqualTo(3L);
    }
}
