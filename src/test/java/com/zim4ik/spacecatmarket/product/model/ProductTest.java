package com.zim4ik.spacecatmarket.product.model;

import com.zim4ik.spacecatmarket.category.model.Category;
import com.zim4ik.spacecatmarket.product.exception.InvalidProductException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void create_withValidData_createsProduct() {
        Product product = Product.create("Galaxy Cat", BigDecimal.valueOf(100));

        assertThat(product.getName()).isEqualTo("Galaxy Cat");
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void create_withNullName_throwsInvalidProductException() {
        assertThatThrownBy(() -> Product.create(null, BigDecimal.valueOf(100)))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Name is required");
    }

    @Test
    void create_withBlankName_throwsInvalidProductException() {
        assertThatThrownBy(() -> Product.create("   ", BigDecimal.valueOf(100)))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Name is required");
    }

    @Test
    void create_withNameTooLong_throwsInvalidProductException() {
        String tooLongName = "a".repeat(256);

        assertThatThrownBy(() -> Product.create(tooLongName, BigDecimal.valueOf(100)))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Name is too long");
    }

    @Test
    void create_withNullPrice_throwsInvalidProductException() {
        assertThatThrownBy(() -> Product.create("Galaxy Cat", null))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Price must not be null and cannot be negative");
    }

    @Test
    void create_withNegativePrice_throwsInvalidProductException() {
        assertThatThrownBy(() -> Product.create("Galaxy Cat", BigDecimal.valueOf(-1)))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Price must not be null and cannot be negative");
    }

    @Test
    void assignCategory_setsCategory() {
        Product product = Product.create("Galaxy Cat", BigDecimal.valueOf(100));
        Category category = Category.create("Snacks", "Cosmic snacks");

        product.assignCategory(category);

        assertThat(product.getCategory()).isEqualTo(category);
    }
}
