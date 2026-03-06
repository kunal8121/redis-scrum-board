package com.redisdemoproject.service;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;

@Singleton
@RequiredArgsConstructor
public class RedisService {

    private final RedissonClient redisson;

    public String ping() {
      try{
          RBucket<String> bucket = redisson.getBucket("ping");
          return bucket.get();
      } catch (RedisException e) {
          throw  e;
      }

    }

    public void save(String key, String value) {
        redisson.getBucket(key).set(value);
    }

    public String get(String key) {
        return (String) redisson.getBucket(key).get();
    }
}
