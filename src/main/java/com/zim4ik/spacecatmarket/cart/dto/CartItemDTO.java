package com.zim4ik.spacecatmarket.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemDTO(Long productId,

                           @NotNull
                           @Positive
                           Integer quantity) {
}
