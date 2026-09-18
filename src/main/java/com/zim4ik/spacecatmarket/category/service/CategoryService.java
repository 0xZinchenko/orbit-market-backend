package com.zim4ik.spacecatmarket.category.service;

import com.zim4ik.spacecatmarket.category.dto.CategoryDTO;
import com.zim4ik.spacecatmarket.category.exception.CategoryNotFoundException;
import com.zim4ik.spacecatmarket.category.mapper.CategoryMapper;
import com.zim4ik.spacecatmarket.category.model.Category;
import com.zim4ik.spacecatmarket.category.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = Category.create(categoryDTO.name(), categoryDTO.description());
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDto(savedCategory);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::categoryToCategoryDto)
                .toList();
    }

    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::categoryToCategoryDto)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        category.updateName(categoryDTO.name());
        category.updateDescription(categoryDTO.description());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.categoryToCategoryDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepository.delete(category);
    }
}
