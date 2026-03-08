package com.redisdemoproject.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redisdemoproject.model.QueryOptions;
import com.redisdemoproject.model.Task;
import com.redisdemoproject.model.User;
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
public class UserRepository implements Repository<User>{

    public static final String INDEX = "user_idx";
    public static final String PREFIX = "user:";
    public static final String MATCH_ALL = "*";
    private static final String JSON_PATH = ".";
    private final RedissonClient redissonClient;
    private final JsonCodec jsonCodec;
    private final RSearch redissonSearch;

    public UserRepository(RedissonClient redissonClient,
                          @Named("object-mapper-for-redis") ObjectMapper objectMapper) {
        this.redissonClient = redissonClient;
        this.jsonCodec =  new JacksonCodec<>(objectMapper, User.class);
        this.redissonSearch = redissonClient.getSearch(StringCodec.INSTANCE);
        updateIndex();
    }

    @Override
    public User save(User entity) {
        RJsonBucket<User> userBucket = redissonClient.getJsonBucket(PREFIX + entity.getId(), jsonCodec);
        userBucket.setIfAbsent(entity);
        return entity;
    }

    @Override
    public User update(UUID id, User entity) {
        RJsonBucket<User> oldUserBucket = redissonClient.getJsonBucket(PREFIX + id, jsonCodec);
        var updated = redissonClient.getJsonBucket(PREFIX+ id, jsonCodec).compareAndSet(JSON_PATH, oldUserBucket.get(), entity);
        if(!updated){
            throw new RuntimeException("Conflict Detected");
        }
        return entity;
    }

    @Override
    public User getById(UUID id) {
        RJsonBucket<User> userBucket = redissonClient.getJsonBucket(PREFIX + id, jsonCodec);
        if(!userBucket.isExists()){
            throw new NoSuchElementException("User Not found with id :"+ id);
        }
        return (User) userBucket.get();
    }

    @Override
    public List<User> findAll(int pageSize, int offset) {
        var searchResult = redissonSearch
                .search(INDEX, MATCH_ALL, org.redisson.api.search.query.QueryOptions.defaults()
                        .limit(offset, pageSize));

        return searchResult.getDocuments().stream()
                .map(doc -> {
                    RJsonBucket<User> userBucket =
                            redissonClient.getJsonBucket(doc.getId(), jsonCodec);
                    return userBucket.get();
                })
                .toList  ();
    }

    @Override
    public List<User> query(QueryOptions queryOptions) {
        String query = RedisQueryBuilder.buildQuery(queryOptions);
        var searchResult = redissonSearch
                .search(INDEX, query, org.redisson.api.search.query.QueryOptions.defaults()
                        .limit(queryOptions.getOffset(), queryOptions.getPageSize()));

        return searchResult.getDocuments().stream()
                .map(doc -> {
                    RJsonBucket<User> userBucket =
                            redissonClient.getJsonBucket(doc.getId(), jsonCodec);
                    return userBucket.get();
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
                FieldIndex.tag("$.name").caseSensitive().as("name"),
                FieldIndex.tag("$.email").caseSensitive().as("email"),
                FieldIndex.tag("$.role").caseSensitive().as("role"),
                FieldIndex.numeric("$.yearsOfExperience").as("years_of_experience").sortMode(SortMode.NORMALIZED),
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
                    .prefix(List.of("user:"));

            redissonSearch.createIndex(INDEX, indexOptions, getIdxSchema());
            log.info(" Index '{}' created successfully", INDEX);
        } catch (RedisException e) {

            log.warn("⚠️ Could not create index: {}", e.getMessage());
        }
    }
}
