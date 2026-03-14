package com.redisdemoproject.service;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Singleton
@RequiredArgsConstructor
public class BcryptPasswordDecoder implements PasswordEncoder {
    private final PasswordEncoder encoder;

    @Override
    public String encode(CharSequence rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encoder.encode(rawPassword).equals(encodedPassword);
    }
}
