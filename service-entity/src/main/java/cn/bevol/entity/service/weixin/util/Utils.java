package cn.bevol.entity.service.weixin.util;

import java.util.HashMap;
import java.util.Map;

import flexjson.JSONDeserializer;

public class Utils {

    public static Map<String, String> getMap (String json,String subKey){

        Map<String,Map<String,String>> jsonNode=  new JSONDeserializer<Map<String,Map<String,String>>>().deserialize(json, HashMap.class);
        if(jsonNode!=null) {
            return jsonNode.get(subKey);
        }
        return null;
    }

    private static int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }

    public static String getRandomString(int length){
        StringBuilder sb = new StringBuilder();
        String string = "abcdefghijklmnopqrstuvwxyz";
        int len = string.length();
        for (int i = 0; i < length; i++) {
            sb.append(string.charAt(getRandom(len-1)));
        }
        return sb.toString();
    }
}
