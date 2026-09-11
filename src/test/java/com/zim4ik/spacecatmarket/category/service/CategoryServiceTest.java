package com.zim4ik.spacecatmarket.category.service;

import com.zim4ik.spacecatmarket.category.dto.CategoryDTO;
import com.zim4ik.spacecatmarket.category.exception.CategoryNotFoundException;
import com.zim4ik.spacecatmarket.category.mapper.CategoryMapper;
import com.zim4ik.spacecatmarket.category.model.Category;
import com.zim4ik.spacecatmarket.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = Category.create("Snacks", "Cosmic snacks for space cats");
        categoryDTO = new CategoryDTO(1L, "Snacks", "Cosmic snacks for space cats");
    }

    @Test
    void createCategory_savesAndReturnsMappedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.categoryToCategoryDto(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertThat(result).isEqualTo(categoryDTO);
    }

    @Test
    void getAllCategories_returnsMappedList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.categoryToCategoryDto(category)).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertThat(result).containsExactly(categoryDTO);
    }

    @Test
    void getCategoryById_whenFound_returnsMappedCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.categoryToCategoryDto(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertThat(result).isEqualTo(categoryDTO);
    }

    @Test
    void getCategoryById_whenNotFound_throwsCategoryNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void updateCategory_whenFound_updatesNameAndDescription() {
        CategoryDTO updateDTO = new CategoryDTO(null, "Toys", "Cosmic toys for space cats");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.categoryToCategoryDto(category)).thenReturn(updateDTO);

        CategoryDTO result = categoryService.updateCategory(1L, updateDTO);

        assertThat(result).isEqualTo(updateDTO);
        assertThat(category.getName()).isEqualTo("Toys");
        assertThat(category.getDescription()).isEqualTo("Cosmic toys for space cats");
    }

    @Test
    void updateCategory_whenNotFound_throwsCategoryNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(99L, categoryDTO))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_whenFound_deletesCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_whenNotFound_throwsCategoryNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(categoryRepository, never()).delete(any());
    }
}
