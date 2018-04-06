package cn.bevol.util.annotate;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 存储层 数据关系表对象
 *
 * Creator: Owen
 * Date: 2015/5/13
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Relationship {
    //表名
    String name() default "";
}
