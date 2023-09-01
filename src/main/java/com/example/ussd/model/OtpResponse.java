package com.example.ussd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OtpResponse {
    public enum Action {
        CONTINUE, OTP, CHANGE_PASSWORD, UPLOAD_CERT
    }

    private String token;
    private String action;
}
