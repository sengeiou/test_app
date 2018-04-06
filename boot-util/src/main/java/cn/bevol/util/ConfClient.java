package cn.bevol.util;

import cn.bevol.util.http.HttpUtils;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * Created by owen on 16-9-19.
 */
public class ConfClient {
    //    private String CONF_SERVER_HOST="http://127.0.0.1:8080";
    private String CONF_SERVER_HOST = "http://api.conf.internal.bevol.cn";
    private String module = "";

    public ConfClient(String module) {
        setModule(module);
    }


    /**
     * 通过key获取对应的string值
     *
     * @param key
     * @return 如果异常或者不存在情况返回 null
     */
    public String getResourceString(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        Object resource = getResource(key + ".string");
        return resource == null ? null : resource.toString();
    }


    private Object getResource(String wholeKey) {
        if (StringUtils.isEmpty(wholeKey)) {
            return null;
        }

        HashMap<String, String> value = new HashMap<String, String>();
        value.put("key", getModule() + "." + wholeKey);

        try {

            String resultJson = HttpUtils.post(CONF_SERVER_HOST + "/resource", value, 3000);
            JsonUtils.getSimpleMapIntegerProperty(resultJson, "ret");
            Integer ret = JsonUtils.getSimpleMapIntegerProperty(resultJson, "ret");
            if (ret == null) {
                return null;
            } else if (ret != 0) {
                return null;
            } else {
                Object tm = JsonUtils.getSimpleMapBeanProperty(resultJson, "result");
                return ((LinkedTreeMap) tm).get("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 获取key获取对应的string值
     *
     * @param key
     * @return 如果异常或者不存在情况返回 null
     */
    public Integer getResourceNum(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        try {

            Object resource = getResource(key + ".num");
            return resource == null ? null : Integer.parseInt(resource.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public boolean setResourceString(String key, String value) {
        return setResourceString(key, value, null);
    }

    public boolean setResourceString(String key, String value, String name) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }

        if (StringUtils.isEmpty(value)) {
            return false;
        }


        try {

            HashMap<String, String> post = new HashMap<String, String>();
            post.put("key", getModule() + "." + key + ".string");
            post.put("value", value);
            if (!StringUtils.isEmpty(name)) {
                post.put("name", name);
            }

            String resultJson = HttpUtils.post(CONF_SERVER_HOST + "/resource/upsert", post);
            Integer ret = JsonUtils.getSimpleMapIntegerProperty(resultJson, "ret");

            if (ret == null) {
                return false;
            } else if (ret != 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public boolean setResourceNum(String key, Integer value) {
        return setResourceNum(key, value, null);
    }

    public boolean setResourceNum(String key, Integer value, String name) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }

        if (value == null) {
            return false;
        }

        try {

            HashMap<String, String> post = new HashMap<String, String>();
            post.put("key", getModule() + "." + key + ".num");
            post.put("value", value.toString());

            if (!StringUtils.isEmpty(name)) {
                post.put("name", name);
            }

            String resultJson = HttpUtils.post(CONF_SERVER_HOST + "/resource/upsert", post);
            Integer ret = JsonUtils.getSimpleMapIntegerProperty(resultJson, "ret");

            if (ret == null) {
                return false;
            } else if (ret != 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public static void main(String[] arg) throws NoSuchFieldException {
        System.out.println(new ConfClient("api").setResourceNum("index_page_default_num", 145, "desc中午你"));//api.name18.num
        System.out.println(new ConfClient("api").getResourceNum("index_page_default_num"));
        System.out.println(new ConfClient("api").getResourceString("name78"));
        System.out.println(new ConfClient("api").setResourceString("name888", "12", "desc中午你"));//api.name18.string
        System.out.println(new ConfClient("api").getResourceString("name888"));
        final ConfClient c = new ConfClient("api");
    }
}
