package com.redisdemoproject.controller;

import com.redisdemoproject.mapper.UserMapper;
import com.redisdemoproject.model.Filters;
import com.redisdemoproject.model.QueryOptions;
import com.redisdemoproject.model.response.RestUser;
import com.redisdemoproject.model.spec.Role;
import com.redisdemoproject.model.spec.UserCreateSpec;
import com.redisdemoproject.service.UserService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Controller("/api/{version}/users")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
public class UserController {
    private static final String VERSION01 = "1";
    private final UserService userService;

    @Get()
    public List<RestUser> getAll() {
        return userService.findAll()
                .stream()
                .map(UserMapper.INSTANCE::toRestUser)
                .toList();
    }

    @Get("/{id}")
    public RestUser getById(@PathVariable UUID id) {
        return UserMapper.INSTANCE.toRestUser(userService.getById(id));
    }

    @Put("/{id}")
    public RestUser update(@PathVariable UUID id, @Body @Valid UserCreateSpec spec) {
        return UserMapper.INSTANCE.toRestUser(userService.updateUser(id, spec));
    }

    @Get("/query")
    public List<RestUser> query(
            @QueryValue @Nullable String name,
            @QueryValue @Nullable String email,
            @QueryValue @Nullable Role role,
            @Min(1) @QueryValue int pageSize,
            @Min(0) @QueryValue int offset) {

        QueryOptions.QueryOptionsBuilder queryBuilder = QueryOptions.builder();
        Map<String, String> filters = new HashMap<>();

        Optional.ofNullable(name).ifPresent(val ->
                filters.put(Filters.NAME, String.valueOf(val)));

        Optional.ofNullable(email).ifPresent(val ->
                filters.put(Filters.EMAIL, String.valueOf(val)));

        Optional.ofNullable(role).ifPresent(val ->
                filters.put(Filters.ROLE, String.valueOf(val)));

        queryBuilder.filters(filters);
        Optional.of(pageSize).ifPresent(queryBuilder::pageSize);
        Optional.of(offset).ifPresent(queryBuilder::offset);

        return userService.queryAll(queryBuilder.build()).stream()
                .map(UserMapper.INSTANCE::toRestUser)
                .toList();
    }

    @Delete("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
