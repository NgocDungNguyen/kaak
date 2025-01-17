package com.restaurant.exception.database;

import com.restaurant.exception.RestaurantException;

public class DatabaseException extends RestaurantException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
