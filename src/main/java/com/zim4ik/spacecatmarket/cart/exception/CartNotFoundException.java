package com.zim4ik.spacecatmarket.cart.exception;

import com.zim4ik.spacecatmarket.exception.NotFoundException;

public class CartNotFoundException extends NotFoundException {

    public CartNotFoundException(Long id) {
        super("Cart with id: " + id + " not found");
    }
}
