package com.redisscrumboard.model.response;

import com.fasterxml.jackson.annotation.*;
import com.redisscrumboard.model.Priority;
import com.redisscrumboard.model.TaskType;
import com.redisscrumboard.utils.DateTimeFormat;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;

import static com.redisscrumboard.utils.DateTimeFormat.DATE_TIME_FORMAT;


public record RestTaskResponse(
        @JsonProperty("id") String id,
        @JsonProperty("description") String desc,
        @JsonProperty("priority") Priority priority,
        @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "UTC")
        @JsonProperty("due_at") Instant dueAt,
        @JsonProperty("task_type") TaskType taskType,
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "task_type",
                visible = true
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = RestTaskDetailsResponse.RestBugResponse.class, name = "BUG"),
                @JsonSubTypes.Type(value = RestTaskDetailsResponse.RestFeatureResponse.class, name = "FEATURE")
        })
        @JsonUnwrapped
        @JsonProperty("task_details") RestTaskDetailsResponse taskDetails,
        @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "UTC")
        @JsonProperty("created_at")  Instant createdAt
) {
}
