package com.example.ussd.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ussd.exceptions.AuthForbiddenException;
import com.example.ussd.exceptions.InvalidRequestOrigin;
import com.example.ussd.exceptions.InvalidSubjectException;
import com.example.ussd.exceptions.ResponseException;
import com.example.ussd.model.Permissions;
import com.example.ussd.model.UserCore;
import lombok.Builder;
import org.apache.catalina.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    public final long LONGEVITY_JWT = 60 * 60 * 1000;
    private final long LONGEVITY_OTP = 5 * 60 * 1000;
    private final int KEY_SIZE = 32;
    private final String ISSUER_NAME = "Otp";
    private final String CLAIM_ROLE = "roles";
    private final String CLAIM_USERNAME = "username";
    private final String letters = "abcdefghijklmpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String numbers = "0123456789";
    private final int MAX_OTP_TRIES = 4;
    private final String specials = "!@#&*()-=_+[]<>?:";
    private final List<String> types = Arrays.asList(letters, numbers, specials);

    public final Map<String, JWTData> jwtMapping = new HashMap<>();


    @Builder
    public static class JWTData {
        public String key;
        public String address;
        public String otp;
        public int tries;
        public Date expirationDate;
    }
    public int clearExpiredKeys() {
        Date current = new Date();
        int count = 0;
        if (!jwtMapping.keySet().isEmpty()) {
            for (String key : jwtMapping.keySet()) {
                JWTData data = jwtMapping.get(key);
                if (current.after(data.expirationDate)) {
                    jwtMapping.remove(key);
                    count++;
                }
            }
        }
        return count;
    }

    public JWTData removeKey(String userId) {
        return jwtMapping.remove(userId);
    }

    public String generateKey() {
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < KEY_SIZE; i++) {
            String temp = types.get(rand.nextInt(types.size()));
            builder.append(temp.charAt(rand.nextInt(temp.length())));
        }
        return builder.toString();
    }
    public Map<String, String> generateTemporaryTokenForOTP(UserCore userCore, HttpServletRequest request) {
        String otp = UserCore.GENERATE_OTP();
        Date expirationDate = new Date(System.currentTimeMillis() + LONGEVITY_OTP);
        String token = performGenerateToken(userCore.getId(), userCore.getUsername(), List.of(new SimpleGrantedAuthority(Permissions.OTP_SUBMIT.name())), expirationDate, otp, request);
        return Map.of(token, otp);
    }

    public String authenticateJwtOtp(UserCore userBase,String id, String otp, HttpServletRequest request) {
        JWTData jwtData = jwtMapping.get(id);

        if (jwtData == null) throw new AuthForbiddenException("Max tries.");
        if (jwtData.otp == null || !jwtData.otp.equals(otp)) {
            jwtData.tries++;
            if (jwtData.tries >= MAX_OTP_TRIES)
                jwtMapping.remove(id);
            throw new ResponseException("Invalid OTP");
        }
        Date expirationDate = new Date(System.currentTimeMillis() + LONGEVITY_JWT);
        List<SimpleGrantedAuthority> authorities = !userBase.isFirstAuth()
                ? userBase.getAuthorityList()
                : Collections.singletonList(new SimpleGrantedAuthority(Permissions.PASSWORD_RESET.name()));
        return performGenerateToken(userBase.getId(), userBase.getUsername(), authorities, expirationDate, null, request);
    }

    public DecodedJWT validate(String token, String address) {
        if (token == null)
            throw new InvalidSubjectException("Null token");
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT.decode(token);
        } catch (Exception e) {
            throw new JWTVerificationException("Invalid JWT");
        }
        String subject = decodedJWT.getSubject();
        JWTData data = jwtMapping.get(subject);
        if (subject == null || data == null)
            throw new InvalidSubjectException("Invalid subject " + subject);

        if (!data.address.equals(address))
            throw new InvalidRequestOrigin("Invalid Request origin");

        return JWT.require(Algorithm.HMAC256(data.key.getBytes(StandardCharsets.UTF_8)))
                .build()
                .verify(decodedJWT);
    }

    public String getUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim(CLAIM_USERNAME).as(String.class);
    }

    public List<SimpleGrantedAuthority> getRoles(DecodedJWT decodedJWT) {
        System.out.println(decodedJWT.getClaim("roles"));
        return decodedJWT.getClaim(CLAIM_ROLE).asList(String.class)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String generateToken(UserCore userCore, HttpServletRequest request) {
        Date expirationDate = new Date(System.currentTimeMillis() + LONGEVITY_OTP);
        return performGenerateToken(userCore.getId(), userCore.getUsername(), userCore.getAuthorityList(), expirationDate, null, request);
    }

    public String performGenerateToken(String userId, String username, List<SimpleGrantedAuthority> authorities, Date expirationDate, String otp, HttpServletRequest request) {
        String inetSocketAddress = request.getRemoteAddr();
        if (inetSocketAddress == null)
            return null;
        String address = inetSocketAddress;
        String key = generateKey();
        Algorithm algorithm = Algorithm.HMAC256(key.getBytes(StandardCharsets.UTF_8));
        String jwt = JWT.create()
                .withSubject(userId)
                .withIssuer(ISSUER_NAME)
                .withIssuedAt(new Date())
                .withExpiresAt(expirationDate)
                .withClaim(CLAIM_ROLE, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim(CLAIM_USERNAME, username)
                .sign(algorithm);
        jwtMapping.put(userId, JWTData.builder().otp(otp).address(address).key(key).expirationDate(expirationDate).build());
        return jwt;
    }

}
