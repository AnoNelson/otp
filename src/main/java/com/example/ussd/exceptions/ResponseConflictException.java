package com.example.ussd.exceptions;

public class ResponseConflictException extends RuntimeException {
    public ResponseConflictException(String message) {
        super(message);
    }
}
