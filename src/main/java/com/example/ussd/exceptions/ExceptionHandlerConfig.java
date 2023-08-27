package com.example.ussd.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@RestController
public class ExceptionHandlerConfig {

    @ExceptionHandler
    public ResponseEntity<?> configureException(GlobalException ex, WebRequest web){
        ExceptionObject obej = new ExceptionObject(ex.getMessage());
        return new ResponseEntity<Object>(obej, HttpStatus.BAD_REQUEST);
    }

}