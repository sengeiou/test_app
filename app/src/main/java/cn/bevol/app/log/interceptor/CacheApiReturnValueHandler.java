package cn.bevol.app.log.interceptor;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;

/**
 * @author mysens
 */
public class CacheApiReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

    @Resource
    private RedisCacheProvider redisCacheProvider;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return true;
    }

    /**
     * 对符合url要求的地址，放入缓存
     * @param returnValue
     * @param returnType
     * @param mavContainer
     * @param webRequest
     * @throws Exception
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

    }

    @Override
    public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
        if(CacheApiInterceptor.thdata != null) {
            CacheKey cacheKey = CacheApiInterceptor.thdata.get();
            if (cacheKey != null) {
                redisCacheProvider.put(cacheKey, returnValue);
            }
            CacheApiInterceptor.thdata.remove();
        }
        return false;
    }
}
