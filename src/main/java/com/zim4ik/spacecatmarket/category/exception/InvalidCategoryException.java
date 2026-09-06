package com.zim4ik.spacecatmarket.category.exception;

import com.zim4ik.spacecatmarket.exception.DomainValidationException;

public class InvalidCategoryException extends DomainValidationException {

    public InvalidCategoryException(String message) {
        super(message);
    }
}
