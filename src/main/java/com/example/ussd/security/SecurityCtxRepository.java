package com.example.ussd.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class SecurityCtxRepository implements SecurityContextRepository {

    private final AuthProvider authProvider;

    public SecurityCtxRepository(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder exchange) {
        String token = exchange.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        token = token != null ? token : exchange.getRequest().getParameter("auth");
        if (token == null)
            return new SecurityContextImpl();
        if (token.startsWith("Bearer "))
            token = token.substring(7);

        String address = exchange.getRequest().getRemoteAddr();
        if (address == null)
            return new SecurityContextImpl();

        JwtAuthToken auth = new JwtAuthToken(token, address, new AuthenticatedUser(), null);
        Authentication verifiedAuth = authProvider.authenticate(auth);
        return verifiedAuth.isAuthenticated()
                ? new SecurityContextImpl(verifiedAuth)
                : new SecurityContextImpl();
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return true;
    }
}
