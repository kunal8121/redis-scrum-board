package com.redisdemoproject.authentication;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Factory
public class PasswordEncoderFactory {

    @Singleton
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
