package com.redisdemoproject.model;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

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
    private Instant dueAt;
    private Status status;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
}

