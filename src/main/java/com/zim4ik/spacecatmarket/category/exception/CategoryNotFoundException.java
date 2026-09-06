package com.zim4ik.spacecatmarket.category.exception;

import com.zim4ik.spacecatmarket.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long id) {
        super("Category with id: " + id + " not found");
    }
}
