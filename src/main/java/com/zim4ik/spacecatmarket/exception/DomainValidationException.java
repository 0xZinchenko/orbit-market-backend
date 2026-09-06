package com.zim4ik.spacecatmarket.exception;

public abstract class DomainValidationException extends RuntimeException {

    protected DomainValidationException(String message) {
        super(message);
    }
}
