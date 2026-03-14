package com.redisdemoproject.model.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.*;
import org.mapstruct.Mapping;

@Introspected
public record UserCreateSpec (
        @JsonProperty("name")
        @NotBlank
        String name,
        @JsonProperty("password")
        @NotBlank(message = "Password cannot be blank")
        String password,
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
                message = "Email format is incorrect"
        )
        @JsonProperty("email")
        @NotBlank
        String emailId,
        @Max(30)
        @NotNull
        @Positive(message = "Years Cant be negative")
        @JsonProperty("year_of_experience")
        Integer yearsOfExperience) {}
