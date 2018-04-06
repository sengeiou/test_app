package cn.bevol.app.cache;


public abstract class CacheableTemplate<T> {

    private CacheProvider cacheProvider;

    public CacheableTemplate(CacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    public T execute(CacheKey cacheKey) {
        return execute(cacheKey, true);
    }

    public T execute(CacheKey cacheKey, boolean useCache) {
        if (useCache) {
            T result=null;
            try {
                result = getFromCache(cacheKey);
            }catch (Exception ex){
                ex.printStackTrace();//避免服务异常,直接使用底层查询:ex redis链接等问题
                //todo 邮件或者手机短信报警
                return getFromRepository();
            }
            if (result != null)
                return result;
            else {
                result = getFromRepository();
                if (result != null) {
                    putToCache(cacheKey, result);
                }
                return result;
            }
        } else
            return getFromRepository();
    }

    protected T getFromCache(CacheKey cacheKey) {
        return (T) cacheProvider.get(cacheKey);
    }

    protected abstract T getFromRepository();

    protected void putToCache(CacheKey cacheKey, T value) {
        if (canPutToCache(value))
            cacheProvider.put(cacheKey, value);
    }

    public void push(CacheKey cacheKey, T value) {
            cacheProvider.put(cacheKey, value);
    }

    protected boolean canPutToCache(T value) {
        return (value != null);
    }
}
