package com.zim4ik.spacecatmarket.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zim4ik.spacecatmarket.category.dto.CategoryDTO;
import com.zim4ik.spacecatmarket.category.exception.CategoryNotFoundException;
import com.zim4ik.spacecatmarket.category.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void createCategory_withValidBody_returns201() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "Snacks", "Cosmic snacks for space cats");
        CategoryDTO response = new CategoryDTO(1L, "Snacks", "Cosmic snacks for space cats");
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Snacks"));
    }

    @Test
    void createCategory_withBlankName_returns400WithFieldError() throws Exception {
        CategoryDTO request = new CategoryDTO(null, " ", "Cosmic snacks for space cats");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").isArray());
    }

    @Test
    void createCategory_withBlankDescription_returns400WithFieldError() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "Snacks", " ");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.description").isArray());
    }

    @Test
    void createCategory_withTooLongName_returns400WithFieldError() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "a".repeat(257), "Cosmic snacks for space cats");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").isArray());
    }

    @Test
    void getAllCategories_returns200WithList() throws Exception {
        CategoryDTO category = new CategoryDTO(1L, "Snacks", "Cosmic snacks for space cats");
        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getCategoryById_whenFound_returns200() throws Exception {
        CategoryDTO category = new CategoryDTO(1L, "Snacks", "Cosmic snacks for space cats");
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Snacks"));
    }

    @Test
    void getCategoryById_whenNotFound_returns404() throws Exception {
        when(categoryService.getCategoryById(99L)).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(get("/api/v1/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Category with id: 99 not found"));
    }

    @Test
    void updateCategory_withValidBody_returns200() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "Toys", "Cosmic toys for space cats");
        CategoryDTO response = new CategoryDTO(1L, "Toys", "Cosmic toys for space cats");
        when(categoryService.updateCategory(eq(1L), any(CategoryDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Toys"));
    }

    @Test
    void updateCategory_whenNotFound_returns404() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "Toys", "Cosmic toys for space cats");
        when(categoryService.updateCategory(eq(99L), any(CategoryDTO.class)))
                .thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(put("/api/v1/categories/99")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategory_withInvalidBody_returns400() throws Exception {
        CategoryDTO request = new CategoryDTO(null, "", "Cosmic toys for space cats");

        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategory_whenFound_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_whenNotFound_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new CategoryNotFoundException(99L))
                .when(categoryService).deleteCategory(99L);

        mockMvc.perform(delete("/api/v1/categories/99"))
                .andExpect(status().isNotFound());
    }
}
