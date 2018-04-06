package cn.bevol.statics.cache;


public class CacheKey {

    private String cacheNamespace;

    private String cacheName;

    private String internalKey;

    public CacheKey(String cacheNamespace, String cacheName, String internalKey) {
        this.cacheNamespace = cacheNamespace;
        this.cacheName = cacheName;
        this.internalKey = internalKey;
    }

    public String getCacheNamespace() {
        return cacheNamespace;
    }

    public String getCacheName() {
        return cacheName;
    }

    public String getInternalKey() {
        return internalKey;
    }

    public String getCacheKey() {
        return this.cacheNamespace + ":" + this.cacheName + ":" + this.internalKey;
    }
}
