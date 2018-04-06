package com.io97.cache;

import net.sf.ehcache.Cache;


public class NoCacheProvider implements CacheProvider {

    public Cache getCache(String name) {
        return null;
    }

    public void put(CacheKey key, Object value) {
    }

    public Object get(CacheKey key) {
        return null;
    }

    public boolean remove(CacheKey key) {
        return false;
    }

    @Override
    public boolean removeMatch(CacheKey bevol) {
        return false;
    }

}
