package com.redisdemoproject.model.response;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Data;

@Introspected
@Builder
public record RestUser(
    String id,
    String name,
    String email,
    String role
) {}
