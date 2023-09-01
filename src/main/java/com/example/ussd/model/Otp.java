package com.example.ussd.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class Otp {
    @NotBlank
    private String otp;
}
