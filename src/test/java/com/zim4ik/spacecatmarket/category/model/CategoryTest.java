package com.zim4ik.spacecatmarket.category.model;

import com.zim4ik.spacecatmarket.category.exception.InvalidCategoryException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Test
    void create_withValidData_createsCategory() {
        Category category = Category.create("Snacks", "Cosmic snacks for space cats");

        assertThat(category.getName()).isEqualTo("Snacks");
        assertThat(category.getDescription()).isEqualTo("Cosmic snacks for space cats");
    }

    @Test
    void create_withNullName_throwsInvalidCategoryException() {
        assertThatThrownBy(() -> Category.create(null, "Cosmic snacks for space cats"))
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessage("Name is required");
    }

    @Test
    void create_withBlankName_throwsInvalidCategoryException() {
        assertThatThrownBy(() -> Category.create("   ", "Cosmic snacks for space cats"))
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessage("Name is required");
    }

    @Test
    void create_withNameTooLong_throwsInvalidCategoryException() {
        String tooLongName = "a".repeat(256);

        assertThatThrownBy(() -> Category.create(tooLongName, "Cosmic snacks for space cats"))
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessage("Name is too long");
    }

    @Test
    void updateDescription_setsDescription() {
        Category category = Category.create("Snacks", "Old description");

        category.updateDescription("New description");

        assertThat(category.getDescription()).isEqualTo("New description");
    }
}
