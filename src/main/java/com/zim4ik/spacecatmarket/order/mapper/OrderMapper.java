package com.zim4ik.spacecatmarket.order.mapper;

import com.zim4ik.spacecatmarket.order.dto.OrderDTO;
import com.zim4ik.spacecatmarket.order.dto.OrderItemDTO;
import com.zim4ik.spacecatmarket.order.model.Order;
import com.zim4ik.spacecatmarket.order.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {

    OrderDTO orderToOrderDto(Order order);

    @Mapping(target = "productId", source = "product.id")
    OrderItemDTO orderItemToOrderItemDto(OrderItem orderItem);

}
