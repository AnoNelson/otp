package com.example.ussd.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthForbiddenException extends RuntimeException {

    private String userId;
    private String message;

    public AuthForbiddenException(String message) {
        super(message);
    }

    public static AuthForbiddenException createWith(String userId, String message) {
        AuthForbiddenException a = new AuthForbiddenException();
        a.userId = userId;
        a.message = message;
        return a;
    }

    public String toJson() {
        return "{\"userId\":\"" + userId + "\"," +
                "\"message\":\"" + message + "\"}";
    }
}
