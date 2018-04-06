package cn.bevol.app.log.interceptor;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.util.cache.CACHE_NAME;
import com.google.gson.Gson;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mysens
 */
public class CacheApiInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisCacheProvider redisCacheProvider;
    /**
     * 请求公用的参数
     */
    public static ThreadLocal<CacheKey> thdata=new ThreadLocal<CacheKey>();
    private Map<String, String> queueMap;

    {
        queueMap = new HashMap<>();
        queueMap.put("5m", CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE);
        queueMap.put("30m", CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE);
        queueMap.put("2d", CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE);
        queueMap.put("fe", CACHE_NAME.FOREVER_CACHE_QUEUE);
    }

    /**
     * 拦截被缓存的接口
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        String[] urlParse = url.split("/");
        String queue = queueMap.get(urlParse[urlParse.length-1].substring(1));
        CacheKey cacheKey = new CacheKey(
                CACHE_NAME.NAMESPACE,
                queue,
                CACHE_NAME.createInstanceKey(url)
        );
        Object cache = redisCacheProvider.get(cacheKey);

        if(cache == null){
            //没有缓存，继续程序
            thdata.set(cacheKey);
            return true;
        }else{
            //有缓存返回结果
            String returnValue = new Gson().toJson(cache);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/json");
            response.getWriter().write(returnValue);
            return false;
        }
    }
}
