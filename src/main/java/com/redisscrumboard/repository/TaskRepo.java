package com.redisdemoproject.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redisdemoproject.model.Task;
import com.redisdemoproject.utils.RedisQueryBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jdk.jfr.Name;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmClassRenameType;
import org.redisson.api.RJsonBucket;
import org.redisson.api.RSearch;
import org.redisson.api.RedissonClient;
import org.redisson.api.search.index.FieldIndex;
import org.redisson.api.search.index.IndexOptions;
import org.redisson.api.search.index.IndexType;
import org.redisson.api.search.index.SortMode;
import org.redisson.api.search.query.QueryOptions;
import org.redisson.client.RedisException;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JacksonCodec;
import org.redisson.codec.JsonCodec;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Singleton
public class TaskRepo implements Repository<Task> {

    public static final String INDEX = "task_idx";
    public static final String PREFIX = "task:";
    public static final String MATCH_ALL = "*";
    public static final String CREATED_AT = "createdAt";
    private final RedissonClient redissonClient;
    private final JsonCodec jsonCodec;
    private final RSearch redissonSearch;

    @Inject
    public TaskRepo(RedissonClient redissonClient,
                    @Named("object-mapper-for-redis") ObjectMapper objectMapper) {
        this.redissonClient = redissonClient;
        this.jsonCodec = new JacksonCodec<>(objectMapper, Task.class);
        this.redissonSearch = redissonClient.getSearch(StringCodec.INSTANCE);
        updateIndex();
    }

    @Override
    public Task save(Task entity) {
        RJsonBucket<Task> taskBucket = redissonClient.getJsonBucket(PREFIX + entity.getId(), jsonCodec);
        taskBucket.setIfAbsent(entity);
        return entity;
    }

    @Override
    public Task update(String id,Task entity) {
        RJsonBucket<Task> oldTaskBucket = redissonClient.getJsonBucket(PREFIX + id, jsonCodec);
        if (!oldTaskBucket.isExists()) {
            throw  new NoSuchElementException("Element Not Found with id : %s".formatted(id));
        }
        Task oldTask =  oldTaskBucket.get();

        Task task = Task.builder()
                .id(oldTask.getId())
                .desc(entity.getDesc())
                .priority(entity.getPriority())
                .dueAt(entity.getDueAt())
                .updatedAt(Instant.now())
                .build();

        RJsonBucket<Task> taskBucket= redissonClient.getJsonBucket(PREFIX+id, jsonCodec);
        taskBucket.set(task);
        return task;
    }


    @Override
    public Task getById(UUID id) {
        var result = redissonClient.getJsonBucket(PREFIX + id, jsonCodec);
        if (!result.isExists()) {
            throw  new NoSuchElementException("Element Not Found with id : %s".formatted(id));
        }
        return (Task) result.get();
    }

    @Override
    public List<Task> findAll(int pageSize, int offset) {
        var searchResult = redissonSearch
                .search(INDEX, MATCH_ALL, QueryOptions.defaults()
                        .limit(offset, pageSize));

        return searchResult.getDocuments().stream()
                .map(doc -> {
                    RJsonBucket<Task> taskBucket =
                            redissonClient.getJsonBucket(doc.getId(), jsonCodec);
                    return taskBucket.get();
                })
                .toList  ();
    }

    @Override
    public List<Task> query(com.redisscrumboard.model.QueryOptions queryOptions) {
        String queryString = RedisQueryBuilder.buildQuery(queryOptions);
        var searchResult = redissonSearch
                .search(INDEX, queryString, QueryOptions.defaults()
                        .limit(queryOptions.getOffset(), queryOptions.getPageSize())
                );


        return searchResult.getDocuments().stream()
                .map(doc -> {
                    RJsonBucket<Task> taskBucket =
                            redissonClient.getJsonBucket(doc.getId(), jsonCodec);
                    return taskBucket.get();
                })
                .toList();
    }


    @Override
    public void delete(String id) {
        redissonClient.getJsonBucket(PREFIX+id, jsonCodec).delete();
    }

    private FieldIndex[] getIdxSchema() {
        return new FieldIndex[] {
                FieldIndex.tag("$.id").caseSensitive().as("id"),
                FieldIndex.tag("$.desc").caseSensitive().as("description"),
                FieldIndex.tag("$.priority").caseSensitive().as("priority").sortMode(SortMode.NORMALIZED),
                FieldIndex.tag("$.status").caseSensitive().as("status"),
                FieldIndex.numeric("$.createdAt").as("created_at").sortMode(SortMode.NORMALIZED),
                FieldIndex.numeric("$.dueAt").as("due_at").sortMode(SortMode.NORMALIZED),
                FieldIndex.numeric("$.updatedAt").as("updated_at").sortMode(SortMode.NORMALIZED)
        };
    }

    private void updateIndex() {
        try {
            redissonSearch.dropIndex(INDEX);
            log.info("Existing index dropped");
        } catch (RedisException e) {
            log.debug("No existing index to drop");
        }

        try {
            IndexOptions indexOptions = IndexOptions.defaults()
                    .on(IndexType.JSON)
                    .prefix(List.of("task:"));

            redissonSearch.createIndex(INDEX, indexOptions, getIdxSchema());
            log.info(" Index '{}' created successfully", INDEX);
        } catch (RedisException e) {

            log.warn("⚠️ Could not create index: {}", e.getMessage());
        }
    }
}
