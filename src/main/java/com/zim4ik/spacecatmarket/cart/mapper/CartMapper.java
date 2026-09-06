package com.zim4ik.spacecatmarket.cart.mapper;

import com.zim4ik.spacecatmarket.cart.dto.CartDTO;
import com.zim4ik.spacecatmarket.cart.model.Cart;
import com.zim4ik.spacecatmarket.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CartMapper {

    @Mapping(target = "productIds", source = "products", qualifiedByName = "productsToProductIds")
    CartDTO cartToCartDto(Cart cart);

    @Named("productsToProductIds")
    default List<Long> productsToProductIds(List<Product> products) {
        return products.stream()
                .map(Product::getId)
                .toList();
    }

}
