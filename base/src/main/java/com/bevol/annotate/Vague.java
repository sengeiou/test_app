package com.bevol.annotate;

/**
 *
 * 存储层 数据模糊查询字段
 *
 * Creator: Owen
 * 数据库
 * Date: 2015/5/13
 */
public @interface Vague {
    //描述
    String desc();
    //分隔符
    String separate() default ";";
}
