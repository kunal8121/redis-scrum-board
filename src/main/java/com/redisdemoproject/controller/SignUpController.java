package com.redisdemoproject.controller;

import com.redisdemoproject.model.User;
import com.redisdemoproject.model.spec.UserCreateSpec;
import com.redisdemoproject.service.UserService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller("api/{version}/signup")
@RequiredArgsConstructor
public class SignUpController {
    private static final String VERSION = "1";
    private final UserService userService;

    @Post()
    public User registerUser(@Body @Valid UserCreateSpec spec) {
        return userService.registerUser(spec);
    }
}
