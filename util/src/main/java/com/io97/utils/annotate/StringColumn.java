package com.io97.utils.annotate;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * 存储层 数据普通字段
 *
 * Creator: Owen
 * Date: 2015/5/13
 */
@Target({FIELD})
@Retention(SOURCE)

public @interface StringColumn {
    //描述
    String desc();
}
