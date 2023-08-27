package com.example.ussd.exceptions;

public class InvalidRequestOrigin extends RuntimeException {
    public InvalidRequestOrigin(String message) {
        super(message);
    }
}
