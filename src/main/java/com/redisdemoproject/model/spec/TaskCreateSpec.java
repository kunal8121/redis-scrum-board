package com.redisdemoproject.model.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.redisdemoproject.model.Priority;
import com.redisdemoproject.model.TaskType;

import java.time.Instant;

import io.micronaut.core.annotation.Introspected;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Introspected

public record TaskCreateSpec(
        @JsonProperty("description") @NotBlank String desc,
        @JsonProperty("priority") @NotNull Priority priority,
        @JsonProperty("due_at") @NotNull Instant dueAt,
        @JsonProperty("task_type") @NotNull TaskType taskType,
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "task_type",
                visible = true
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = TaskDetailsSpec.BugDetailsSpec.class, name = "BUG"),
                @JsonSubTypes.Type(value = TaskDetailsSpec.FeatureDetailsSpec.class, name = "FEATURE")
        })
        @JsonProperty("task_details") TaskDetailsSpec taskDetails
        ){}
