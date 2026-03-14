package com.redisdemoproject.controller;

import com.redisdemoproject.mapper.UserMapper;
import com.redisdemoproject.model.User;
import com.redisdemoproject.model.response.RestUser;
import com.redisdemoproject.model.spec.UserCreateSpec;
import com.redisdemoproject.service.UserService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller("api/{version}/signup")
@Secured(SecurityRule.IS_ANONYMOUS)
@ExecuteOn(TaskExecutors.BLOCKING)
@RequiredArgsConstructor
public class SignUpController {
    private static final String VERSION = "1";
    private final UserService userService;

    @Post()
    public RestUser registerUser(@Body @Valid UserCreateSpec spec) {
        return UserMapper.INSTANCE.toRestUser(userService.registerUser(spec));
    }
}
