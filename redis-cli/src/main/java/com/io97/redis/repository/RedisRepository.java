package com.io97.redis.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class RedisRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getValue(String key) {
        Assert.hasText(key);
        return redisTemplate.opsForValue().get(key);
    }

    public boolean putValue(String key, String value) {
        Assert.hasText(key);
        redisTemplate.opsForValue().set(key, value);
        return true;
    }

    public boolean increaseValue(String key, long delta) {
        Assert.hasText(key);
        try {
            redisTemplate.opsForValue().increment(key, delta);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public String getMapField(String key, String field) {
        Assert.hasText(key);
        Assert.hasText(field);
        return redisTemplate.<String, String>opsForHash().get(key, field);
    }

    public boolean putMapField(String key, String field, String value) {
        Assert.hasText(key);
        Assert.hasText(field);
        redisTemplate.<String, String>opsForHash().put(key, field, value);
        return true;
    }

    public boolean increaseMapField(String key, String field, long delta) {
        Assert.hasText(key);
        Assert.hasText(field);
        try {
            redisTemplate.<String, String>opsForHash().increment(key, field, delta);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public boolean removeMapField(String key, String field) {
        Assert.hasText(key);
        Assert.hasText(field);
        redisTemplate.<String, String>opsForHash().delete(key, field);
        return true;
    }

    public List<String> getMapFields(String key, Collection<String> fields) {
        Assert.hasText(key);
        Assert.notNull(fields);
        return redisTemplate.<String, String>opsForHash().multiGet(key, fields);
    }

    public Map<String, String> getMap(String key) {
        Assert.hasText(key);
        return redisTemplate.<String, String>opsForHash().entries(key);
    }

    public boolean putMap(String key, Map<String, String> values) {
        Assert.hasText(key);
        Assert.notNull(values);
        redisTemplate.<String, String>opsForHash().putAll(key, values);
        return true;
    }

    public List<String> getList(String key, Integer start, Integer end) {
        Assert.hasText(key);
        return redisTemplate.opsForList().range(key, start, end);
    }

    public boolean addToListHead(String key, String value) {
        Assert.hasText(key);
        Assert.notNull(value);
        redisTemplate.opsForList().leftPush(key, value);
        return true;
    }

    public boolean addToListTail(String key, String value) {
        Assert.hasText(key);
        Assert.notNull(value);
        redisTemplate.opsForList().rightPush(key, value);
        return true;
    }

    public Long ListLength(String key) {
        Assert.hasText(key);
        return redisTemplate.opsForList().size(key);
    }

    public boolean remove(String key) {
        Assert.hasText(key);
        redisTemplate.delete(key);
        return true;
    }

    public long sumField(String key, Collection<String> fields) {
        Assert.hasText(key);
        Assert.notNull(fields);

        List<String> returns = getMapFields(key, fields);
        long result = 0;
        for (String l : returns) {
            try {
                result = result + Long.parseLong(l);
            } catch (Exception ex) {
                continue;
            }
        }
        return result;
    }

    public boolean publishMessage(String channel, String message) {
        Assert.hasText(channel);
        Assert.notNull(message);
        redisTemplate.convertAndSend(channel, message);
        return true;
    }
}
