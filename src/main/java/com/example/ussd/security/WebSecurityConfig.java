package com.example.ussd.security;


import com.example.ussd.model.Permissions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthProvider authManager;
    private final SecurityCtxRepository securityCtxRepository;

    private static class CorsConfig implements CorsConfigurationSource {
        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.applyPermitDefaultValues();
            corsConfig.addAllowedMethod(HttpMethod.POST);
            corsConfig.addAllowedMethod(HttpMethod.GET);
            corsConfig.addAllowedMethod(HttpMethod.DELETE);
            corsConfig.setAllowedOrigins(Arrays.asList("https://172.16.32.37:2233", "https://197.243.3.212:2244"));
            return corsConfig;
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(new CorsConfig()).and()
                .csrf().disable()
                .authenticationProvider(authManager)
                .securityContext()
                .securityContextRepository(securityCtxRepository)
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth").permitAll()
                .antMatchers("api/test").hasAuthority(Permissions.USER_MANAGEMENT.name())
                .anyRequest().denyAll()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable();
    }

//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return authManager.authenticate();
//    }
}
