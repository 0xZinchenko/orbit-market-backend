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
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private static final String BASE_CURRENCY = "USD";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CurrencyClient currencyClient;


    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = Product.create(productDTO.name(),
                productDTO.price());
        product.assignCategory(resolveCategory(productDTO.categoryId()));
        Product saveProduct = productRepository.save(product);
        ProductDTO dto = productMapper.productToProductDto(saveProduct);
        return dto;
    }

    public List<ProductDTO> getAllProducts() {
            return productRepository.findAll()
                    .stream()
                    .map(productMapper::productToProductDto)
                    .toList();
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::productToProductDto)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.updateName(productDTO.name());
        product.changePrice(productDTO.price());
        product.assignCategory(resolveCategory(productDTO.categoryId()));

        Product updatedProduct = productRepository.save(product);

        return productMapper.productToProductDto(updatedProduct);
    }


    @Transactional
    @PreAuthorize("hasRole('SERVICE')")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id)
                );

        productRepository.delete(product);

    }

    public ProductPriceDTO getProductPriceInCurrency(Long id, String currency) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        ExchangeRateResponse exchangeRate = currencyClient.getExchangeRate(BASE_CURRENCY, currency);
        BigDecimal convertedPrice = product.getPrice().multiply(exchangeRate.rate());

        return new ProductPriceDTO(id, product.getPrice(), currency, convertedPrice);
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
}
