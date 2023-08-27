package com.example.ussd.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserAuth {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public boolean isEmpty() {
        return (this.username == null || this.username.isEmpty()) &&
                (this.password == null || this.password.isEmpty());
    }

}
