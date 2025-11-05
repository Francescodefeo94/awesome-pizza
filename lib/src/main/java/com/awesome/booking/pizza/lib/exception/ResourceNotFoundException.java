package com.awesome.booking.pizza.lib.exception;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
