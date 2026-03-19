package com.redisdemoproject.service;

import com.redisdemoproject.mapper.TaskMapper;
import com.redisdemoproject.model.QueryOptions;
import com.redisdemoproject.model.Task;
import com.redisdemoproject.model.spec.TaskCreateSpec;
import com.redisdemoproject.repository.TaskRepo;
import io.micronaut.http.annotation.Body;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public class TaskService {
    private static final int OFFSET = 0;
    private static final int MIN_PAGE_SIZE = 1;
    private final TaskRepo taskRepo;

    public Task createTask(@Valid @Body TaskCreateSpec spec, Authentication authentication) {
        Map<String, Object> attributes = authentication.getAttributes();
        String userId =  String.valueOf(attributes.get("id"));
        Task task = TaskMapper.INSTANCE.toEntity(spec);
        task.setCreatedBy(UUID.fromString(userId));
        task.setUpdatedBy(UUID.fromString(userId));
        return taskRepo.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepo.findAll(10, OFFSET);  // pageSize=10, offset=0
    }

    public Task getTaskById(UUID id) {
        return taskRepo.getById(id);
    }

    public Task updateTask(UUID id, TaskCreateSpec spec, Authentication authentication) {
        Map<String, Object> attributes = authentication.getAttributes();
        String userId =  String.valueOf(attributes.get("id"));
        Task existing = taskRepo.getById(id);

        Task updated = Task.builder()
                .id(existing.getId())
                .desc(spec.desc())
                .status(existing.getStatus())
                .priority(spec.priority())
                .dueAt(spec.dueAt())
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now())
                .updatedBy(UUID.fromString(userId))
                .build();

        return taskRepo.update(id, updated);
    }

    public void deleteTaskById(UUID id) {
        taskRepo.delete(id);
    }

    public List<Task> queryTasks(QueryOptions queryOptions) {
        return taskRepo.query(queryOptions);
    }
}
