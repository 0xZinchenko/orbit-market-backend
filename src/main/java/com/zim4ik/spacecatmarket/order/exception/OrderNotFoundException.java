package com.zim4ik.spacecatmarket.order.exception;

import com.zim4ik.spacecatmarket.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long id) {
        super("Order with id: " + id + " not found");
    }
}
