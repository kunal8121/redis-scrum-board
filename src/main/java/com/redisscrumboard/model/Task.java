package com.redisscrumboard.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.redisscrumboard.model.response.RestTaskDetailsResponse;
import io.micronaut.core.annotation.Introspected;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import static com.redisscrumboard.utils.DateTimeFormat.DATE_TIME_FORMAT;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class Task {
    private UUID id;
    private String desc;
    private Priority priority;
    private TaskType taskType;
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "taskType",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TaskDetails.Bug.class, name = "BUG"),
            @JsonSubTypes.Type(value = TaskDetails.Feature.class, name = "FEATURE")
    })
    private TaskDetails taskDetails;

    private Instant createdAt;

    private Instant dueAt;

    private Instant updatedAt;
    private Status status;
}

