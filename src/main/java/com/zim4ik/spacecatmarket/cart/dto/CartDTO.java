package com.zim4ik.spacecatmarket.cart.dto;

import java.util.List;

public record CartDTO(Long id, List<Long> productIds) {
}
