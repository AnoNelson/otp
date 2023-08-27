package com.example.ussd.controller;


import com.example.ussd.model.SimpleUserAuth;
import com.example.ussd.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
public class MainController {

    private final AuthenticationService authenticationService;

    @PostMapping("/api/auth")
    public ResponseEntity<?> auth(@RequestBody @Valid SimpleUserAuth userAuth, HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.authenticateUser(userAuth, request), HttpStatus.OK);
    }

    @PostMapping("/api/test")
    public ResponseEntity<?> testAuth() {
        return new ResponseEntity<>(Arrays.asList("name", "data").toArray(),HttpStatus.OK);
    }

}
