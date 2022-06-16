package com.lianzheng.h5.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisHelper {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    public void put(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void put(String key, String value, int second) {
        stringRedisTemplate.opsForValue().set(key, value, second, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }


    public void putList(String key, String value) {
        stringRedisTemplate.opsForList().leftPush(key, value);
    }

    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    public void putObject(String key, Object obj) {
        redisTemplate.opsForValue().set(key, obj);
    }

    public void putObject(String key, Object obj, long timeout) {
        redisTemplate.opsForValue().set(key, obj, timeout, TimeUnit.SECONDS);
    }

    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void removeObject(String key) {
        redisTemplate.delete(key);
    }
}
