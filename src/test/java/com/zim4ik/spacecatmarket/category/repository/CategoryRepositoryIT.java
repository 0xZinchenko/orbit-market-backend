package com.zim4ik.spacecatmarket.category.repository;

import com.zim4ik.spacecatmarket.category.dto.CategoryDTO;
import com.zim4ik.spacecatmarket.category.exception.CategoryNotFoundException;
import com.zim4ik.spacecatmarket.category.service.CategoryService;
import com.zim4ik.spacecatmarket.testsupport.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class CategoryRepositoryIT extends AbstractPostgresIT {

    @Autowired
    private CategoryService categoryService;

    @Test
    void createReadUpdateDelete_roundTripsThroughRealPostgres() {
        CategoryDTO created = categoryService.createCategory(
                new CategoryDTO(null, "Snacks", "Cosmic snacks for space cats"));
        assertThat(created.id()).isNotNull();

        CategoryDTO fetched = categoryService.getCategoryById(created.id());
        assertThat(fetched.name()).isEqualTo("Snacks");

        CategoryDTO updated = categoryService.updateCategory(created.id(),
                new CategoryDTO(null, "Toys", "Cosmic toys for space cats"));
        assertThat(updated.name()).isEqualTo("Toys");
        assertThat(updated.description()).isEqualTo("Cosmic toys for space cats");

        categoryService.deleteCategory(created.id());
        assertThatThrownBy(() -> categoryService.getCategoryById(created.id()))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
