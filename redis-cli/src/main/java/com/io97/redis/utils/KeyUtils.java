package com.io97.redis.utils;

import com.io97.redis.Constants;

public class KeyUtils {

    public static String buildKey(String type, String namespace,
                                  String instanceName, String instanceId) {
        return namespace.toLowerCase() + Constants.COLON
                + instanceName.toLowerCase() + Constants.COLON
                + instanceId.toLowerCase() + Constants.COLON
                + type.toLowerCase();
    }

    public static String buildLongKey(Object... allField) {
        String keys = "";
        for (Object field : allField) {
            keys += (field == null ? "null" : field.toString().toLowerCase())
                    + Constants.COLON;
        }
        return keys;
    }

    public static String UV_STRING = "uv";
    public static String PV_STRING = "pv";
    public static String TOTAL_STRING = "total";
}
