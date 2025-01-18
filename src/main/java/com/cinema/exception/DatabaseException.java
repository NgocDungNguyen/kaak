package com.cinema.exception;

public class DatabaseException extends CinemaException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}