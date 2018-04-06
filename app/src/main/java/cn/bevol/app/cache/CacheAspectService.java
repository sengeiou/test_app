package cn.bevol.app.cache;

import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.log.MonitorService;
import cn.bevol.util.JsonUtils;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * @author mysens
 */
@Aspect
@Order(2)
@Component
public class CacheAspectService {

    @Resource
    private RedisCacheProvider cacheProvider;

    /**
     * 定义MakeCache注解的切点
     */
    @Pointcut("@annotation(cn.bevol.app.cache.MakeCache)")
    public void makeCachePoint(){}

    @Around("makeCachePoint()")
    public Object aroundCache(final ProceedingJoinPoint joinPoint) {
        Object returnValue = null;
        Signature signature =  joinPoint.getSignature();
        //获取方法声明
        MethodSignature methodSignature = (MethodSignature) signature;
        //获取目标方法
        Method targetMethod = methodSignature.getMethod();
        //获取传参
        Object[] params = joinPoint.getArgs();
        //获取返回类型
        final Class returnType = methodSignature.getReturnType();

        if(targetMethod.isAnnotationPresent(MakeCache.class)) {
            //获取注解参数
            String cacheType = targetMethod.getAnnotation(MakeCache.class).type();
            String namespace = targetMethod.getAnnotation(MakeCache.class).namespace();
            String queue = targetMethod.getAnnotation(MakeCache.class).queue();
            String[] keys = targetMethod.getAnnotation(MakeCache.class).keys().length>0?
                    targetMethod.getAnnotation(MakeCache.class).keys():
                    targetMethod.getAnnotation(MakeCache.class).value();



            //创建缓存key名
            String[] cacheKeys = Arrays.copyOf(keys, keys.length+params.length);
            for(int i=0; i<params.length;i++){
                cacheKeys[keys.length+i] = params[i].toString();
            }
            String cacheKey;
            if(CACHE_NAME.FOREVER_CACHE_QUEUE.equals(queue)){
                //永久key
                cacheKey = CACHE_NAME.createInstanceForeverKey(cacheKeys);
            }else{
                //其他key
                cacheKey = CACHE_NAME.createInstanceKey(cacheKeys);
            }

            returnValue = new CacheableTemplate<Object>(cacheProvider) {
                @Override
                protected Object getFromRepository() {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable throwable) {
                        //记录错误日志
                        MonitorService.doLog(joinPoint, throwable);
                        //获取异常返回值
                        return MonitorService.getReturnValue(returnType);
                    }
                }

                @Override
                protected boolean canPutToCache(Object object) {
                    if(returnType == ReturnData.class  ) {
                        ReturnData returnData = (ReturnData) object;
                        return (returnData!=null && returnData.getRet()==0);
                    } else if(returnType == ReturnListData.class ){
                        ReturnListData returnListData = (ReturnListData) object;
                        return (returnListData!=null && returnListData.getRet()==0 && returnListData.getResult().size()>0);
                    } else if(returnType == Map.class ) {
                        Map map = (Map) object;
                        if(map!=null&&map.containsKey("ret")){
                            return (int) map.get("ret") == 0;
                        }
                        return (map!=null&&map.size()>0);
                    } else if(returnType == String.class){
                        String string = (String) object;
                        return !StringUtils.isBlank(string);
                    }  else {
                        //其他类型不允许
                        return object!=null;
                    }
                }
            }.execute(new CacheKey(namespace, queue, cacheKey), true);
            try {
                return JsonUtils.toObject(JsonUtils.toGson(returnValue), returnType);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("makeCache注解使用异常！");
            }
        }else{
            throw new RuntimeException("makeCache注解使用异常！");
        }
    }
}
