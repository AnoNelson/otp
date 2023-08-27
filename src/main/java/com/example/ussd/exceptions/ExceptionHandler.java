package com.example.ussd.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(ResponseException.class)
    public ResponseEntity<String> handleResponseException(ResponseException responseException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseException.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResponseConflictException.class)
    public ResponseEntity<String> handleResponseConflictException(ResponseConflictException responseConflictException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseConflictException.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AuthException.class)
    public ResponseEntity<?> authExceptionHandler(AuthException authException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authException.returnObject());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleBindException(BindException e) {
        return getBindExceptionResponse(e.getBindingResult());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleWebExchangeBindException(WebExchangeBindException e) {
        return getBindExceptionResponse(e.getBindingResult());
    }

    private ResponseEntity<Map<String, String>> getBindExceptionResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors()
                .forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
