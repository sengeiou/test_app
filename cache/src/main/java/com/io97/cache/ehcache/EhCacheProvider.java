package com.io97.cache.ehcache;

import com.io97.cache.CacheKey;
import com.io97.cache.CacheProvider;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EhCacheProvider implements CacheProvider {

    private static final Logger logger = LoggerFactory.getLogger(EhCacheProvider.class);

    public Cache getCache(String name) {
        return CacheManager.getInstance().getCache(name);
    }

    public void put(CacheKey key, Object value) {
        Cache cache = getCache(key.getCacheName());
        if (null == cache) {
            logger.error("can not getCache. name: " + key.getCacheName());
            return;
        }
        Element element = new Element(key.getInternalKey(), value);
        cache.put(element);
    }

    public Object get(CacheKey key) {
        Cache cache = getCache(key.getCacheName());
        if (null == cache) {
            logger.error("can not getCache. name: " + key.getCacheName());
            return null;
        }
        Element element = cache.get(key.getInternalKey());
        if (element != null)
            return element.getObjectValue();
        else
            return null;
    }

    public boolean remove(CacheKey key) {
        Cache cache = getCache(key.getCacheName());
        if (null == cache) {
            logger.error("can not getCache. name: " + key.getCacheName());
            return false;
        }
        return cache.remove(key.getInternalKey());
    }

    @Override
    public boolean removeMatch(CacheKey bevol) {
        return true;//todo impl
    }

}
