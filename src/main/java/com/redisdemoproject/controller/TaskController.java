package com.redisdemoproject.controller;

import com.redisdemoproject.mapper.TaskMapper;
import com.redisdemoproject.model.Task;
import com.redisdemoproject.model.Filters;
import com.redisdemoproject.model.QueryOptions;
import com.redisdemoproject.model.response.RestTaskResponse;
import com.redisdemoproject.model.spec.TaskCreateSpec;
import com.redisdemoproject.service.TaskService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.version.annotation.Version;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.validation.Valid;

import java.util.*;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Controller("/api/{version}/task")
@ExecuteOn(TaskExecutors.BLOCKING)
@RequiredArgsConstructor
public class TaskController {
    private static final String VERSION01 = "1";
    private final TaskService taskService;

    @Version(VERSION01)
    @Post()
    public RestTaskResponse create(@Body @Valid TaskCreateSpec spec) {
        Task task =  taskService.createTask(spec);
        return TaskMapper.INSTANCE.toRest(task); 
    }

    @Version(VERSION01)
    @Get()
    public List<Task> getAll() {
        return taskService.getAllTasks();
    }

    @Version(VERSION01)
    @Get("/query")
    public List<Task> query(
            @Nullable @QueryValue UUID id,
            @Nullable @QueryValue String desc,
            @Nullable @QueryValue String priority,
            @Nullable @QueryValue String status,
            @Nullable @QueryValue String createdAt,
            @Nullable @QueryValue String updatedAt,
            @Nullable @QueryValue String dueAt,
            @Min(1) @QueryValue int pageSize,
            @Min(0) @QueryValue int offSet
    ) {

        QueryOptions.QueryOptionsBuilder queryBuilder = QueryOptions.builder();
        Map<String, String> filters = new HashMap<>();

        Optional.ofNullable(id).ifPresent(val ->
             filters.put(Filters.ID, String.valueOf(val)));

        Optional.ofNullable(desc).ifPresent(val ->
                filters.put(Filters.STATUS, String.valueOf(val)));

        Optional.ofNullable(priority).ifPresent(val ->
                filters.put(Filters.PRIORITY, String.valueOf(val)));

        Optional.ofNullable(status).ifPresent(val ->
                filters.put(Filters.DESCRIPTION, String.valueOf(val)));

        Optional.ofNullable(createdAt).ifPresent(val ->
                filters.put(Filters.CREATED_AT, String.valueOf(val)));

        Optional.ofNullable(updatedAt).ifPresent(val ->
                filters.put(Filters.DUE_AT, String.valueOf(val)));

        Optional.ofNullable(dueAt).ifPresent(val ->
                filters.put(Filters.UPDATED_AT, String.valueOf(val)));

        queryBuilder.filters(filters);
        Optional.of(pageSize).ifPresent(queryBuilder::pageSize);
        Optional.of(offSet).ifPresent(queryBuilder::offset);

        return taskService.queryTasks(queryBuilder.build());

    }

    @Version(VERSION01)
    @Get("{id}")
    public Task getTask(@PathVariable UUID id) {
        return taskService.getTaskById(id);
    }

    @Version(VERSION01)
    @Put("{id}")
    public Task update(@Valid @Body TaskCreateSpec spec, @PathVariable UUID id) {
        return taskService.updateTask(id, spec);
    }

    @Delete("{id}")
    public void delete(@PathVariable UUID id) {
        taskService.deleteTaskById(id);
    }
}
