package com.redisdemoproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private UUID id;
    private UUID taskId;
    private String message;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
