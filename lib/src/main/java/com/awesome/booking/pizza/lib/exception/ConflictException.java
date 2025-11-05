package com.awesome.booking.pizza.lib.exception;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message);
    }
}
