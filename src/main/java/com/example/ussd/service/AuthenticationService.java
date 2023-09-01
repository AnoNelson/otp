package com.example.ussd.service;


import com.example.ussd.exceptions.AuthException;
import com.example.ussd.model.*;
import com.example.ussd.repository.UserRepository;
import com.example.ussd.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    public final static String CERT_WEBSITE_EMAIL = "CERT-WEB";
    private final static int MAX_RISK = 3;
    private final JwtService jwtService;
    private final UserRepository userCoreRepo;
    private final PasswordEncoder encoder;

    public Boolean logOutUser(String userId) {
        JwtService.JWTData data = jwtService.removeKey(userId);
        return data != null;
    }

    public OtpResponse authenticateUser(SimpleUserAuth userAuth, HttpServletRequest request) {
        UserCore userCore = userCoreRepo.findByUsername(userAuth.getUsername());
        if (userCore == null)
            throw new AuthException("User not found");

        if (!userCore.isAccountEnabled() || userCore.isAccountLocked() || userCore.isAccountExpired()) {
            throw new AuthException("The account is disabled");
        }
        if (userAuth.getPassword() == null || !encoder.matches(userAuth.getPassword(), userCore.getPassword())) {
            userCore.setRisk(userCore.getRisk() + 1);
            if (userCore.getRisk() + 1 > MAX_RISK)
                userCore.setAccountEnabled(false);
            userCoreRepo.save(userCore);
            throw new AuthException("Invalid credentials");
        }
        userCore.setRisk(0);
        userCoreRepo.save(userCore);
        jwtService.clearExpiredKeys();
        Map<String, String> tokenMap = jwtService.generateTemporaryTokenForOTP(userCore, request);
        String token = tokenMap.keySet().stream().findFirst().orElse(null);
        return new OtpResponse(token,OtpResponse.Action.OTP.name());
    }

    public OtpResponse authenticateOTP(String userId, Otp otp, HttpServletRequest request) {
        UserCore userCore = userCoreRepo.getById(userId);
        String token = jwtService.authenticateJwtOtp(userCore, userId, otp.getOtp(), request);
        return !userCore.isFirstAuth()
                ? new OtpResponse(token, OtpResponse.Action.CONTINUE.name())
                : new OtpResponse(token, OtpResponse.Action.CHANGE_PASSWORD.name());
    }

}
