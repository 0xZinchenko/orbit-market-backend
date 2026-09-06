package com.zim4ik.spacecatmarket.product.dto;

import com.zim4ik.spacecatmarket.product.validation.CosmicWordCheck;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductDTO(Long id,

                         @NotBlank
                         @Size(max = 256)
                         @CosmicWordCheck
                         String name,

                         @NotNull
                         @PositiveOrZero
                         BigDecimal price,

                         Long categoryId) {
}
