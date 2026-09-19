package com.zim4ik.spacecatmarket.product.repository;

import com.zim4ik.spacecatmarket.category.dto.CategoryDTO;
import com.zim4ik.spacecatmarket.category.service.CategoryService;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.exception.ProductNotFoundException;
import com.zim4ik.spacecatmarket.product.service.ProductService;
import com.zim4ik.spacecatmarket.testsupport.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@WithMockUser(authorities = "ROLE_SERVICE")
class ProductRepositoryIT extends AbstractPostgresIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void createReadUpdateDelete_roundTripsThroughRealPostgres() {
        CategoryDTO category = categoryService.createCategory(
                new CategoryDTO(null, "Snacks", "Cosmic snacks for space cats"));

        ProductDTO created = productService.createProduct(
                new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), category.id()));
        assertThat(created.id()).isNotNull();

        ProductDTO fetched = productService.getProductById(created.id());
        assertThat(fetched.name()).isEqualTo("Galaxy Cat");

        ProductDTO updated = productService.updateProduct(created.id(),
                new ProductDTO(null, "Comet Cat", BigDecimal.valueOf(150), category.id()));
        assertThat(updated.name()).isEqualTo("Comet Cat");
        assertThat(updated.price()).isEqualByComparingTo(BigDecimal.valueOf(150));

        productService.deleteProduct(created.id());
        assertThatThrownBy(() -> productService.getProductById(created.id()))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void createProduct_withDuplicateNameInSameCategory_violatesUniqueConstraint() {
        CategoryDTO category = categoryService.createCategory(
                new CategoryDTO(null, "Snacks", "Cosmic snacks for space cats"));
        productService.createProduct(
                new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), category.id()));

        assertThatThrownBy(() -> productService.createProduct(
                new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(200), category.id())))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
