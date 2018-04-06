package cn.bevol.config.cache;

import org.springframework.stereotype.Service;

@Service
public interface CacheProvider {

    public void put(CacheKey key, Object value);

    public Object get(CacheKey key);

    public boolean remove(CacheKey key);

    public boolean removeMatch(CacheKey bevol);
}
