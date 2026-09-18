package com.zim4ik.spacecatmarket.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemDTO(Long productId,

                            @NotNull
                            @Positive
                            Integer quantity) {
}
