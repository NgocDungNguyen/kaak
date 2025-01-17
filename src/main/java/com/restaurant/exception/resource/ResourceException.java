package com.restaurant.exception.resource;

import com.restaurant.exception.RestaurantException;

public class ResourceException extends RestaurantException {

    public ResourceException(String message) {
        super(message);
    }

    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
