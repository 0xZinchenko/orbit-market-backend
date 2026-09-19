package com.zim4ik.spacecatmarket.security.apikey;

import com.zim4ik.spacecatmarket.product.controller.ProductController;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.service.ProductService;
import com.zim4ik.spacecatmarket.security.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ApiKeyAuthenticationWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getProducts_withoutAnyCredentials_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProducts_withInvalidApiKey_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header(ApiKeyAuthenticationFilter.API_KEY_HEADER, "wrong-key"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProducts_withValidApiKey_returns200() throws Exception {
        when(productService.getAllProducts()).thenReturn(
                List.of(new ProductDTO(1L, "Galaxy Cat", BigDecimal.valueOf(100), null)));

        mockMvc.perform(get("/api/v1/products")
                        .header(ApiKeyAuthenticationFilter.API_KEY_HEADER, "local-dev-key"))
                .andExpect(status().isOk());
    }
}
