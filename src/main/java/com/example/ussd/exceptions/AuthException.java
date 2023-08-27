package com.example.ussd.exceptions;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
@NoArgsConstructor
public class AuthException extends RuntimeException {

    private String userId;
    private String message;

    public AuthException(String message) {
        super(message);
        this.message = message;
    }

    public static AuthException createWith(String userId, String message) {
        AuthException a = new AuthException();
        a.userId = userId;
        a.message = message;
        return a;
    }

    public String toJson() {
        return "{\"userId\":\"" + userId + "\"," +
                "\"message\":\"" + message + "\"}";
    }
    public Error returnObject(){
        return new Error(this.message,"401");
    }

    @Override
    public String toString() {
        return "AuthException{" +
                "userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
