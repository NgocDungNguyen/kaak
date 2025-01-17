package com.restaurant.exception.validation;

import com.restaurant.exception.RestaurantException;

public class ValidationException extends RestaurantException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
