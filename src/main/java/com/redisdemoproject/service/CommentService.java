package com.redisdemoproject.service;

import com.redisdemoproject.mapper.CommentMapper;
import com.redisdemoproject.model.Comment;
import com.redisdemoproject.model.response.RestComment;
import com.redisdemoproject.model.spec.CommentSpec;
import com.redisdemoproject.repository.CommentRepository;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment create(CommentSpec spec, Authentication authentication) {
        Map<String, Object> attributes = authentication.getAttributes();
        String userId = String.valueOf(attributes.get("id"));
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .taskId(spec.taskID())
                .message(spec.message())
                .createdAt(Instant.now())
                .createdBy(UUID.fromString(userId))
                .createdAt(Instant.now())
                .build();

        return commentRepository.save(comment);
    }

    public Comment update(UUID id, @Valid CommentSpec commentSpec) {
        Comment oldComment = commentRepository.getById(id);
        Comment updatedComment = Comment.builder()
                .id(oldComment.getId())
                .taskId(oldComment.getTaskId())
                .message(commentSpec.message())
                .createdAt(oldComment.getCreatedAt())
                .createdBy(oldComment.getCreatedBy())
                .updatedAt(Instant.now())
                .build();

        return commentRepository.update(id, updatedComment);
    }

    public void delete(UUID id) {
        commentRepository.delete(id);
    }
}
