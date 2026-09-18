package com.zim4ik.spacecatmarket.order.dto;

import java.util.List;

public record OrderDTO(Long id, String orderNumber, List<OrderItemDTO> items) {
}
