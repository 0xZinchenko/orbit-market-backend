package com.zim4ik.spacecatmarket.product.dto;

import java.math.BigDecimal;

public record ProductPriceDTO(Long productId, BigDecimal originalPrice, String currency, BigDecimal convertedPrice) {
}
