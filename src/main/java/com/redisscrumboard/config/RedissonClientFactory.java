package com.redisscrumboard.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redisscrumboard.model.Task;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JacksonCodec;
import org.redisson.codec.JsonCodec;
import org.redisson.config.Config;
import org.redisson.config.Protocol;

@Slf4j
@Factory
public class RedissonClientFactory {

    @Singleton
    RedissonClient redissonClient() {
        log.info("Creating Redis Client Bean");
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6380");
        return  Redisson.create(config);
    }
    @Singleton
    @Named("object-mapper-for-redis")
    ObjectMapper createObjectMapper() {
        log.info("object mapper bean created");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

//    @Singleton
//    private JsonCodec getJsonCode(@Named("object-mapper-for-redis") ObjectMapper mapper ) {
//        return  new JacksonCodec<>(mapper, Task.class);
//    }
}
