package com.redisdemoproject.service;

import com.redisdemoproject.mapper.TaskMapper;
import com.redisdemoproject.model.Task;
import com.redisdemoproject.model.QueryOptions;
import com.redisdemoproject.model.spec.TaskCreateSpec;
import com.redisdemoproject.repository.TaskRepo;
import io.micronaut.http.annotation.Body;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Singleton
public class TaskService {
    private final TaskRepo taskRepo;
    private static final int OFFSET = 0;
    private static final int MIN_PAGE_SIZE = 1;

    public Task createTask(@Valid @Body TaskCreateSpec spec) {
        Task task = TaskMapper.INSTANCE.toEntity(spec);
        return taskRepo.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepo.findAll(10, OFFSET);  // pageSize=10, offset=0
    }

    public Task getTaskById(UUID id) {
        return taskRepo.getById(id);
    }

    public Task updateTask(UUID id, TaskCreateSpec spec) {

        Task existing = taskRepo.getById(id);

        Task updated = Task.builder()
            .id(existing.getId())
            .desc(existing.getDesc())
            .status(existing.getStatus())
            .priority(existing.getPriority())
            .dueAt(existing.getDueAt())
            .createdAt(existing.getCreatedAt())
            .updatedAt(Instant.now())
            .build();

        return taskRepo.update(String.valueOf(id), updated);
    }

    public void deleteTaskById(UUID id) {
        taskRepo.delete(String.valueOf(id));
    }

    public List<Task> queryTasks(QueryOptions queryOptions) {
          return taskRepo.query(queryOptions);
    }
}
