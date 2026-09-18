package com.zim4ik.spacecatmarket.cart.mapper;

import com.zim4ik.spacecatmarket.cart.dto.CartDTO;
import com.zim4ik.spacecatmarket.cart.dto.CartItemDTO;
import com.zim4ik.spacecatmarket.cart.model.Cart;
import com.zim4ik.spacecatmarket.cart.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CartMapper {

    CartDTO cartToCartDto(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    CartItemDTO cartItemToCartItemDto(CartItem cartItem);

}
