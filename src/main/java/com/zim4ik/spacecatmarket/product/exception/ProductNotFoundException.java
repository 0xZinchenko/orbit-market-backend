package com.zim4ik.spacecatmarket.product.exception;

import com.zim4ik.spacecatmarket.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(Long id) {
        super("Product with id: "  + id + " not found");
    }
}
