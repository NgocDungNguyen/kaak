package com.restaurant.exception.business;

import com.restaurant.exception.RestaurantException;

public class BusinessException extends RestaurantException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
