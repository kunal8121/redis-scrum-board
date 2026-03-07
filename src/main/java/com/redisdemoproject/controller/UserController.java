package com.redisdemoproject.controller;

import com.redisdemoproject.model.Filters;
import com.redisdemoproject.model.QueryOptions;
import com.redisdemoproject.model.User;
import com.redisdemoproject.model.spec.Role;
import com.redisdemoproject.model.spec.UserCreateSpec;
import com.redisdemoproject.service.UserService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Controller("/api/{version}/users")
@RequiredArgsConstructor
public class UserController {
    private static final String VERSION01 = "1";
    private final UserService userService;

    @Get()
    public List<User> getAll(){
            return userService.findAll();
    }

    @Get("/{id}")
    public User getById(@PathVariable UUID id){
        return userService.getById(id);
    }

    @Put("/{id}")
    public User update(@PathVariable UUID id, @Body @Valid UserCreateSpec spec) {
        return userService.updateUser(id, spec);
    }

    @Get("/query")
    public List<User> query(
            @QueryValue @Nullable  String name,
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

        return userService.queryAll(queryBuilder.build());
    }

    @Delete("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
