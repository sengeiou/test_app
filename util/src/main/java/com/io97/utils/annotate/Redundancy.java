package com.io97.utils.annotate;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * 存储层 数据冗余字段
 *
 * Creator: Owen
 * Date: 2015/5/13
 */
@Target({FIELD})
@Retention(SOURCE)

public @interface Redundancy {
    //描述
    String desc();
    //值类型
    ValueType valueType() default ValueType.SINGLE;
    //分隔符
    String separate() default ";";
    //对应column
    String corColumn();

    public static enum ValueType {
        SINGLE("单值，用于id等"), MULTI("单值，用于id等")
        ;
        public final String desc;

        ValueType(String desc) {
            this.desc = desc;
        }
    }
}
