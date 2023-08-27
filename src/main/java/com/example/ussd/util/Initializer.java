package com.example.ussd.util;


import com.example.ussd.model.Permissions;
import com.example.ussd.model.UserCore;
import com.example.ussd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Initializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println("Running init");
        insertUsers();
        System.out.println("Done init");
    }

    private void insertUsers() {
        if (userRepository.findByUsername("admin") == null){
            UserCore userCore = new UserCore("admin", passwordEncoder.encode("Da2SVDkq!^fUBo8zkdcYlf95j6rltBwHRAqogfHVsKHfUEBrhZ"));
            userCore.addAuthority(Permissions.VIEW_DASHBOARD.toString());
            userCore.addAuthority(Permissions.USER_MANAGEMENT.toString());
            userRepository.save(userCore);
        }
        if (userRepository.findByUsername("user") == null){
            UserCore userCore = new UserCore("user", passwordEncoder.encode("SVDkq!^fUBo8zkdcYlf95j6rltBwHRAqogfHVsKHfUEBrhZ"));
            userCore.addAuthority(Permissions.VIEW_DASHBOARD.toString());
            userRepository.save(userCore);
        }
    }
}
