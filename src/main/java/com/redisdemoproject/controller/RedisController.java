package com.redisdemoproject.controller;

import com.redisdemoproject.service.RedisService;
import io.micronaut.http.annotation.*;
import lombok.RequiredArgsConstructor;

@Controller("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @Get("/ping")
    public String pingRedis() {
        return redisService.ping();
    }

    @Post("/set-key-value")
    public String set(@QueryValue String key, @QueryValue String value) {
        redisService.save(key, value);
        return "Saved";
    }

    @Get("/{key}")
    public String get(@PathVariable String key) {
        return redisService.get(key);
    }
}
