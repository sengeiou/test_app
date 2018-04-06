package com.bevol.annotate;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * 存储层 数据外键字段
 *
 * Creator: Owen
 * Date: 2015/5/13
 */
@Target({FIELD})
@Retention(SOURCE)

public @interface ForeignKey {
    //描述
    String desc();
    //外键所对应表结构类
    Class foreignClass();
    //外键所对应的字段
    String foreignColumn() default "id";
}
