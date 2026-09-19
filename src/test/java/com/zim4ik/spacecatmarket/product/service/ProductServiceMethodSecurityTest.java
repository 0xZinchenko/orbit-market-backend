package com.zim4ik.spacecatmarket.product.service;

import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProductServiceMethodSecurityTest {

    @Autowired
    private ProductService productService;

    @Test
    @WithMockUser(authorities = "ROLE_SERVICE")
    void deleteProduct_withServiceRole_succeeds() {
        ProductDTO created = productService.createProduct(
                new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), null));

        assertThatCode(() -> productService.deleteProduct(created.id()))
                .doesNotThrowAnyException();
    }

    @Test
    @WithMockUser
    void deleteProduct_withoutServiceRole_throwsAccessDeniedException() {
        ProductDTO created = productService.createProduct(
                new ProductDTO(null, "Comet Cat", BigDecimal.valueOf(50), null));

        assertThatThrownBy(() -> productService.deleteProduct(created.id()))
                .isInstanceOf(AccessDeniedException.class);
    }
}
