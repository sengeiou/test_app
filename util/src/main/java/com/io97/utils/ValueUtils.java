package com.io97.utils;

/**
 * Creator: Owen
 * Date: 2015/3/16
 */
public class ValueUtils {
    public static void validate(Integer v) {
        if (v == null) {
            throw new IllegalStateException("v is null");
        }
        if (v <= 0) {
            throw new IllegalStateException(" v <= 0");
        }
    }
}
