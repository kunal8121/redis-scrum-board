package com.redisdemoproject.authentication;

import com.redisdemoproject.model.User;
import com.redisdemoproject.repository.UserRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class CustomAuthProvider implements HttpRequestAuthenticationProvider<Object> {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public @NonNull AuthenticationResponse authenticate(
            @Nullable HttpRequest<Object> requestContext,
            @NonNull AuthenticationRequest<String, String> authRequest) {

        String username = authRequest.getIdentity();
        String password = authRequest.getSecret();

        // Fetch user details from the repository based on the username
        List<User>  userWithSameName = userRepository.findByUsername(username);

        var user = userWithSameName.stream().filter(u -> encoder.matches(password, u.getPassword())).findFirst();

        if (user.isPresent()) {
            // If user is found and password matches, return a successful authentication response
            log.info("User Authenticated successfully: {}", username);
            return AuthenticationResponse.success(username);
        } else {
            // If user is not found or password does not match, return an authentication failure response
            return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }
    }
}
