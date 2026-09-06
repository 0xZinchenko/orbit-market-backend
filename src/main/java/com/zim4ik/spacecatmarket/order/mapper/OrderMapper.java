package com.zim4ik.spacecatmarket.order.mapper;

import com.zim4ik.spacecatmarket.order.dto.OrderDTO;
import com.zim4ik.spacecatmarket.order.model.Order;
import com.zim4ik.spacecatmarket.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {

    @Mapping(target = "productIds", source = "products", qualifiedByName = "productsToProductIds")
    OrderDTO orderToOrderDto(Order order);

    @Named("productsToProductIds")
    default List<Long> productsToProductIds(List<Product> products) {
        return products.stream()
                .map(Product::getId)
                .toList();
    }

}
