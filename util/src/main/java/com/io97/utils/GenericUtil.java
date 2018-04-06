package com.io97.utils;

/**
 * Created by lin9yuan on 15-3-9.
 */


import com.google.gson.reflect.TypeToken;
import com.io97.utils.http.WebClient;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericUtil {
    public static <J, V, T> T getVos(final int size, final String url, final Converter<J, V> converter) {
        JsonVo<List<J>> jsonVo = getJsonVos(
                url,
                new TypeToken<JsonVo<List<J>>>() {
                }.getType()
        );
        List<J> jos = getData(jsonVo);
        List<V> vos = convert(jos, converter);
        return (T) getSub(size, vos);
    }

    public static <T> T getJsonVos(final String url, final Type t) {
        return (T) JsonUtils.toObject(
               WebClient.get(url),
                t);
    }



    public static <T, U> T getData(final JsonVo<U> jsonVo) {
        if (isSuccess(jsonVo))
            return (T) jsonVo.getData();
        return (T) new ArrayList<U>();
    }

    public static <T, S> List<T> convert(final List<S> source, final Converter<S, T> converter) {
        if (null == source || source.isEmpty()) return new ArrayList<T>();
        List<T> target = new ArrayList<T>(source.size());
        for (S s : source) {
            target.add(converter.convert(s));
        }
        return target;
    }

    private static <T> List<T> getSub(final int size, final List<T> vos) {
        if (size < vos.size()) {
            return vos.subList(0, size);
        }
        return vos;
    }

    private static final String RESULT_SUCCESS = "success";

    private static boolean isSuccess(final JsonVo jsonVo) {
        if (null == jsonVo) return false;
        return RESULT_SUCCESS.equals(jsonVo.getRes());
    }

    public static void main(String[] args) {
        String url = "http://test.api.tiebaobei.com/api/dict/categorys";
    }

    private static class JsonVo<T> {
        private String res;
        private T data;

        public String getRes() {
            return res;
        }

        public void setRes(String res) {
            this.res = res;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}

