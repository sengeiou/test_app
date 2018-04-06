package com.io97.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) {
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, object);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
        return writer.toString();
    }

    public static Map toMap(String json) throws JsonParseException,
            JsonMappingException, IOException {
        return toObject(json, Map.class);
    }

    public static <T> T toObject(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        return (T) mapper.readValue(json, clazz);
    }

    public static <L, T> List<T> toCollection(String json, Class<L> listClazz, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructParametricType(listClazz, clazz);
        return mapper.readValue(json, type);
    }


    private static final Gson gs = new Gson();

    /**
     *根据类型转换string  to  对应对象
     example
     JsonUtils.toObject(test, new TypeToken<UpcSearchNameItemResult>() {
     }.getType());
     * @param json
     * @param typeOfT
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json, Type typeOfT) {
        return gs.fromJson(json, typeOfT);
    }

    public static String toGson(Object obj) {
        try {
            return gs.toJson(obj);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String toGson(Object obj, Type type) {
        try {
            return gs.toJson(obj, type);
        } catch (Exception ex) {
            return null;
        }
    }


    /**
     *     *根据类型转换string  to  object
     example
     JsonUtils.toObject(test, new TypeToken<UpcSearchNameItemResult>() {
     }.getType());
     * @param jsonStr
     * @param type
     * @return
     */
    public static Object gsonToObject(String jsonStr, Type type) {
        try {
            return gs.fromJson(jsonStr, type);
//            return gs.toJson(obj,type);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getSimpleMapStringProperty(String jsonStr, String key) {
        Map map = gs.fromJson(jsonStr, Map.class);
        if (map == null)
            return null;
        try {
            return map.get(key) == null ? null : map.get(key).toString();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Integer getSimpleMapIntegerProperty(String jsonStr, String key) {
        Map map = gs.fromJson(jsonStr, Map.class);
        if (map == null)
            return null;
        try {

            return map.get(key) == null ? null : Integer.parseInt(map.get(key)
                    .toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static Object getSimpleMapBeanProperty(String jsonStr, String key) {
        Map map = gs.fromJson(jsonStr, Map.class);
        if (map == null)
            return null;
        try {
            return map.get(key) == null ? null : map.get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Long getSimpleMapLongProperty(String jsonStr, String key) {
        Map map = gs.fromJson(jsonStr, Map.class);
        if (map == null)
            return null;
        try {

            return map.get(key) == null ? null : Long.parseLong(map.get(key)
                    .toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String stringToJson(Object object) {
        return gs.toJson(object);
    }
//
//	public static void main(String[] args) {
//		System.out.println(JsonUtils.getSimpleMapStringProperty(
//				"{'IMEI':'1','phone':'15212221221'}", "phone"));
//		Integer.parseInt(1231313 + "");
//		System.out.println(JsonUtils.getSimpleMapLongProperty(
//				"{'IMEI':'1','phone':'15212221221'}", "phone"));
//	}
}
