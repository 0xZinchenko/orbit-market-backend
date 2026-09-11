package com.zim4ik.spacecatmarket.product.service;

import com.zim4ik.spacecatmarket.category.exception.CategoryNotFoundException;
import com.zim4ik.spacecatmarket.category.model.Category;
import com.zim4ik.spacecatmarket.category.repository.CategoryRepository;
import com.zim4ik.spacecatmarket.currency.client.CurrencyClient;
import com.zim4ik.spacecatmarket.currency.dto.ExchangeRateResponse;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.dto.ProductPriceDTO;
import com.zim4ik.spacecatmarket.product.exception.ProductNotFoundException;
import com.zim4ik.spacecatmarket.product.mapper.ProductMapper;
import com.zim4ik.spacecatmarket.product.model.Product;
import com.zim4ik.spacecatmarket.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CurrencyClient currencyClient;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = Product.create("Galaxy Cat", BigDecimal.valueOf(100));
        productDTO = new ProductDTO(1L, "Galaxy Cat", BigDecimal.valueOf(100), null);
    }

    @Test
    void createProduct_withoutCategory_savesProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.productToProductDto(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(productDTO);

        assertThat(result).isEqualTo(productDTO);
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void createProduct_withExistingCategory_assignsCategoryAndSaves() {
        Category category = Category.create("Snacks", "Cosmic snacks");
        ProductDTO dtoWithCategory = new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), 5L);
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.productToProductDto(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(dtoWithCategory);

        assertThat(result).isEqualTo(productDTO);
        verify(categoryRepository).findById(5L);
    }

    @Test
    void createProduct_withMissingCategory_throwsCategoryNotFoundException() {
        ProductDTO dtoWithCategory = new ProductDTO(null, "Galaxy Cat", BigDecimal.valueOf(100), 404L);
        when(categoryRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(dtoWithCategory))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void getAllProducts_returnsMappedList() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.productToProductDto(product)).thenReturn(productDTO);

        List<ProductDTO> result = productService.getAllProducts();

        assertThat(result).containsExactly(productDTO);
    }

    @Test
    void getProductById_whenFound_returnsMappedProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDto(product)).thenReturn(productDTO);

        ProductDTO result = productService.getProductById(1L);

        assertThat(result).isEqualTo(productDTO);
    }

    @Test
    void getProductById_whenNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void updateProduct_whenFound_updatesNamePriceAndCategory() {
        ProductDTO updateDTO = new ProductDTO(null, "Comet Cat", BigDecimal.valueOf(200), null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.productToProductDto(product)).thenReturn(updateDTO);

        ProductDTO result = productService.updateProduct(1L, updateDTO);

        assertThat(result).isEqualTo(updateDTO);
        assertThat(product.getName()).isEqualTo("Comet Cat");
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    void updateProduct_whenNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(99L, productDTO))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_whenFound_deletesProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_whenNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository, never()).delete(any());
    }

    @Test
    void getProductPriceInCurrency_whenProductFound_returnsConvertedPrice() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(currencyClient.getExchangeRate("USD", "EUR"))
                .thenReturn(new ExchangeRateResponse("USD", "EUR", BigDecimal.valueOf(0.9)));

        ProductPriceDTO result = productService.getProductPriceInCurrency(1L, "EUR");

        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.originalPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(result.currency()).isEqualTo("EUR");
        assertThat(result.convertedPrice()).isEqualByComparingTo(BigDecimal.valueOf(90.0));
    }

    @Test
    void getProductPriceInCurrency_whenProductNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductPriceInCurrency(99L, "EUR"))
                .isInstanceOf(ProductNotFoundException.class);

        verify(currencyClient, never()).getExchangeRate(any(), any());
    }
}
