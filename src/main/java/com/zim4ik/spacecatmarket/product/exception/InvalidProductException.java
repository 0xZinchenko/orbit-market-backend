package com.zim4ik.spacecatmarket.product.exception;

import com.zim4ik.spacecatmarket.exception.DomainValidationException;

public class InvalidProductException extends DomainValidationException {

    public InvalidProductException(String message) {
        super(message);
    }
}
