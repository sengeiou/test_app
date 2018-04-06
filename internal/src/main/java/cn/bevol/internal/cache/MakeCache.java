package cn.bevol.internal.cache;


import cn.bevol.util.cache.CACHE_NAME;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MakeCache {
     String type() default "redis";
     String namespace() default CACHE_NAME.NAMESPACE;
     String queue() default CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE;
     String[] keys() default {};
     @AliasFor("keys")
     String[] value() default {};
}
