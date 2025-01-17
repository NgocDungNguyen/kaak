package com.restaurant.exception.business;

public class OrderException extends BusinessException {

    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
