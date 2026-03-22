package com.redisdemoproject.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redisdemoproject.model.Comment;
import com.redisdemoproject.model.QueryOptions;
import com.redisdemoproject.utils.RedisQueryBuilder;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RJsonBucket;
import org.redisson.api.RSearch;
import org.redisson.api.RedissonClient;
import org.redisson.api.search.index.FieldIndex;
import org.redisson.api.search.index.IndexOptions;
import org.redisson.api.search.index.IndexType;
import org.redisson.api.search.index.SortMode;
import org.redisson.client.RedisException;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JacksonCodec;
import org.redisson.codec.JsonCodec;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Singleton
public class CommentRepository implements Repository<Comment>{

    public static final String INDEX = "comment_idx";
    public static final String PREFIX = "comment:";
    public static final String MATCH_ALL = "*";
    private static final String JSON_PATH = ".";
    private final RedissonClient redissonClient;
    private final JsonCodec jsonCodec;
    private final RSearch redissonSearch;

    public CommentRepository(RedissonClient redissonClient,
                          @Named("object-mapper-for-redis") ObjectMapper objectMapper) {
        this.redissonClient = redissonClient;
        this.jsonCodec =  new JacksonCodec<>(objectMapper, Comment.class);
        this.redissonSearch = redissonClient.getSearch(StringCodec.INSTANCE);
        updateIndex();
    }

    @Override
    public Comment save(Comment entity) {
        RJsonBucket<Comment> commentBucket = redissonClient.getJsonBucket(PREFIX + entity.getId(), jsonCodec);
        commentBucket.setIfAbsent(entity);
        return entity;
    }

    @Override
    public Comment update(UUID id, Comment entity) {
        RJsonBucket<Comment> oldCommentBucket = redissonClient.getJsonBucket(PREFIX + id, jsonCodec);
        var updated = redissonClient.getJsonBucket(PREFIX+ id, jsonCodec).compareAndSet(JSON_PATH, oldCommentBucket.get(), entity);
        if(!updated){
            throw new RuntimeException("Conflict Detected");
        }
        return entity;
    }

    @Override
    public Comment getById(UUID id) {
        RJsonBucket<Comment> commentBucket = redissonClient.getJsonBucket(PREFIX + id, jsonCodec);
        if(!commentBucket.isExists()){
            throw new NoSuchElementException("Comment Not found with id :"+ id);
        }
        return (Comment) commentBucket.get();
    }

    public List<Comment> getByTaskId(UUID taskId) {
        String query = String.format("@name:{%s}", taskId);
        var searchResult = redissonSearch
                .search(INDEX, query, org.redisson.api.search.query.QueryOptions.defaults()
                        .limit(0, 10));
        return searchResult.getDocuments().stream()
                .map(doc ->{
                    RJsonBucket<Comment> commentBucket =
                            redissonClient.getJsonBucket(doc.getId(), jsonCodec);
                    return commentBucket.get();
                }).toList();
    }

    @Override
    public List<Comment> findAll(int pageSize, int offset) {
        var searchResult = redissonSearch
                .search(INDEX, MATCH_ALL, org.redisson.api.search.query.QueryOptions.defaults()
                        .limit(offset, pageSize));

        return searchResult.getDocuments().stream()
                .map(doc -> {
                    RJsonBucket<Comment> CommentBucket =
                            redissonClient.getJsonBucket(doc.getId(), jsonCodec);
                    return CommentBucket.get();
                })
                .toList();
    }

    @Override
    public List<Comment> query(QueryOptions queryOptions) {
        String query = RedisQueryBuilder.buildQuery(queryOptions);
        var searchResult = redissonSearch
                .search(INDEX, query, org.redisson.api.search.query.QueryOptions.defaults()
                        .limit(queryOptions.getOffset(), queryOptions.getPageSize()));

        return searchResult.getDocuments().stream()
                .map(doc -> {
                    RJsonBucket<Comment> CommentBucket =
                            redissonClient.getJsonBucket(doc.getId(), jsonCodec);
                    return CommentBucket.get();
                })
                .toList();
    }

    @Override
    public void delete(UUID id) {
        redissonClient.getJsonBucket(PREFIX+id, jsonCodec).delete();
    }

    private FieldIndex[] getIdxSchema() {
        return new FieldIndex[] {
                FieldIndex.tag("$.id").caseSensitive().as("id"),
                FieldIndex.tag("$.taskId").caseSensitive().as("task_id"),
                FieldIndex.tag("$.message").caseSensitive().as("message"),
                FieldIndex.numeric("$.createdAt").as("created_at").sortMode(SortMode.NORMALIZED),
                FieldIndex.numeric("$.updatedAt").as("updated_at").sortMode(SortMode.NORMALIZED),
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
                    .prefix(List.of("comment:"));

            redissonSearch.createIndex(INDEX, indexOptions, getIdxSchema());
            log.info(" Index '{}' created successfully", INDEX);
        } catch (RedisException e) {

            log.warn("⚠️ Could not create index: {}", e.getMessage());
        }
    }
}

