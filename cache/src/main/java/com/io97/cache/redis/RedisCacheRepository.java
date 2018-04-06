package com.io97.cache.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.concurrent.TimeUnit;


@Repository
public class RedisCacheRepository {

    private static final Integer DEFAULT_CACHE_TIME = 15 * 60;

    @Autowired
    private RedisTemplate<String, Object> redisCacheTemplate;

    public Object get(String key) {
        Assert.hasText(key);
        return redisCacheTemplate.opsForValue().get(key);
    }

    public void put(String key, Object obj) {
        Assert.hasText(key);
        Assert.notNull(obj);

        redisCacheTemplate.opsForValue().set(key, obj);
        //设置缓存时间
        String cacheTimeInSeconds = Configure.get(getNamespaceAndCacheName(key), String.valueOf(DEFAULT_CACHE_TIME));
        if (!"0".equals(cacheTimeInSeconds)) {
            redisCacheTemplate.expire(key, Long.parseLong(cacheTimeInSeconds), TimeUnit.SECONDS);
        }
    }

    public boolean remove(String key) {
        Assert.hasText(key);
        redisCacheTemplate.delete(key);
        return true;
    }

    private String getNamespaceAndCacheName(String key) {
        String namespaceAndCachename = key.substring(0, key.lastIndexOf(":"));
        return namespaceAndCachename.replaceFirst(":", ".");
    }

    public Set<String> findKeys(String keyPattern) {
        return redisCacheTemplate.keys(keyPattern);
    }

    public void removeMatch(String keyPattern) {
        try {
            Set<String> keys = findKeys(keyPattern);
            if (keys != null && !keys.isEmpty())
                redisCacheTemplate.delete(keys);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
