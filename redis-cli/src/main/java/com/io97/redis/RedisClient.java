package com.io97.redis;

import com.io97.redis.repository.RedisRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class RedisClient {

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private RedisRepository redisRepository;

    public void putValue(final String key, final String value) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                redisRepository.putValue(key, value);
            }
        });
    }

    public void increaseValue(final String key, final long delta) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                redisRepository.increaseValue(key, delta);
            }
        });
    }

    public void publishMessage(final String channel, final String message) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                redisRepository.publishMessage(channel, message);
            }
        });
    }

    public String getValue(String key) {
        return redisRepository.getValue(key);
    }

    public String getMapField(String key, String field) {
        return redisRepository.getMapField(key, field);
    }

    public void putMapField(final String key, final String field, final String value) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                redisRepository.putMapField(key, field, value);
            }
        });
    }

    public void increaseMapField(final String key, final String field, final long delta) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                redisRepository.increaseMapField(key, field, delta);
            }
        });
    }

    public void removeMapField(final String key, final String field) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                redisRepository.removeMapField(key, field);
            }
        });
    }

    public List<String> getMapFields(String key, Collection<String> fields) {
        return redisRepository.getMapFields(key, fields);
    }

    public Map<String, String> getMap(String key) {
        return redisRepository.getMap(key);
    }

    public void putMap(final String key, final Map<String, String> values) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                redisRepository.putMap(key, values);
            }
        });
    }

    public void remove(final String key) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                redisRepository.remove(key);
            }
        });
    }

    public long sumField(String key, Collection<String> fields) {
        return redisRepository.sumField(key, fields);
    }

}
