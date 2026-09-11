package com.zim4ik.spacecatmarket.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.dto.ProductPriceDTO;
import com.zim4ik.spacecatmarket.product.exception.ProductNotFoundException;
import com.zim4ik.spacecatmarket.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct_withValidBody_returns201() throws Exception {
        ProductDTO request = new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(199.99), null);
        ProductDTO response = new ProductDTO(1L, "Galaxy Cat", BigDecimal.valueOf(199.99), null);
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Galaxy Cat"));
    }

    @Test
    void createProduct_withBlankName_returns400WithFieldError() throws Exception {
        ProductDTO request = new ProductDTO(null, " ", BigDecimal.valueOf(10), null);

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").isArray());
    }

    @Test
    void createProduct_withNameMissingCosmicWord_returns400WithFieldError() throws Exception {
        ProductDTO request = new ProductDTO(null, "Regular Cat", BigDecimal.valueOf(10), null);

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").isArray());
    }

    @Test
    void createProduct_withNegativePrice_returns400WithFieldError() throws Exception {
        ProductDTO request = new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(-1), null);

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.price").isArray());
    }

    @Test
    void createProduct_withNullPrice_returns400WithFieldError() throws Exception {
        ProductDTO request = new ProductDTO(null, "Galaxy Cat", null, null);

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.price").isArray());
    }

    @Test
    void getAllProducts_returns200WithList() throws Exception {
        ProductDTO product = new ProductDTO(1L, "Galaxy Cat", BigDecimal.valueOf(100), null);
        when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getProductById_whenFound_returns200() throws Exception {
        ProductDTO product = new ProductDTO(1L, "Galaxy Cat", BigDecimal.valueOf(100), null);
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Galaxy Cat"));
    }

    @Test
    void getProductById_whenNotFound_returns404() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product with id: 99 not found"));
    }

    @Test
    void updateProduct_withValidBody_returns200() throws Exception {
        ProductDTO request = new ProductDTO(null, "Comet Cat", BigDecimal.valueOf(150), null);
        ProductDTO response = new ProductDTO(1L, "Comet Cat", BigDecimal.valueOf(150), null);
        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Comet Cat"));
    }

    @Test
    void updateProduct_whenNotFound_returns404() throws Exception {
        ProductDTO request = new ProductDTO(null, "Comet Cat", BigDecimal.valueOf(150), null);
        when(productService.updateProduct(eq(99L), any(ProductDTO.class)))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(put("/api/v1/products/99")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_withInvalidBody_returns400() throws Exception {
        ProductDTO request = new ProductDTO(null, "", BigDecimal.valueOf(150), null);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductPrice_returns200WithConvertedPrice() throws Exception {
        ProductPriceDTO priceDTO = new ProductPriceDTO(1L, BigDecimal.valueOf(100), "EUR", BigDecimal.valueOf(92));
        when(productService.getProductPriceInCurrency(1L, "EUR")).thenReturn(priceDTO);

        mockMvc.perform(get("/api/v1/products/1/price").param("currency", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedPrice").value(92));
    }

    @Test
    void deleteProduct_whenFound_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_whenNotFound_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new ProductNotFoundException(99L))
                .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/v1/products/99"))
                .andExpect(status().isNotFound());
    }
}
