package com.redisdemoproject.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

import static com.redisdemoproject.utils.DateTimeFormat.DATE_TIME_FORMAT;

public record RestComment(
        @JsonProperty("comment_id") UUID id,
        @JsonProperty("task_id") UUID taskId,
        @JsonProperty("task_comment") String message,
        @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "UTC")
        @JsonProperty("created_at") Instant createdAt,
        @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "UTC")
        @JsonProperty("updated_at") Instant updatedAt
) { }
