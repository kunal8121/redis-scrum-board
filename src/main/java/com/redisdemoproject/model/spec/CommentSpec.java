package com.redisdemoproject.model.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentSpec (
        @JsonProperty("task_id") @NotNull UUID taskID,
        @JsonProperty("task_comment") @NotBlank String message) {}
