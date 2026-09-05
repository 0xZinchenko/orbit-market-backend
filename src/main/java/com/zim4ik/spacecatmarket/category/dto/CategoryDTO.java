package com.zim4ik.spacecatmarket.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDTO(Long id,

                          @NotBlank
                          @Size(max = 256)
                          String name,

                          @NotBlank
                          String description) {
}
