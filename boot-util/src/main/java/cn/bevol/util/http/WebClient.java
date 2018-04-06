package cn.bevol.util.http;


import cn.bevol.util.JsonUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Project:
 * Author: Owen
 * Date: 14-3-12
 */
public class WebClient {
    private static final String USER_AGENT = "io_browser";

    /**
     * @param url         非空
     * @param charsetEnum 默认用UTF8
     * @return
     */
    public static String get(final String url, CharsetEnum charsetEnum) {
        if (StringUtils.isEmpty(url)) throw new NullPointerException("url is empty");
        if (null == charsetEnum) charsetEnum = CharsetEnum.UTF8;
        return bytes2String(url2Bytes(fixUrl(url)), charsetEnum);
    }

    private static final String PRE = "http://";
    private static final String PRE_S = "https://";

    private static String fixUrl(final String url) {
        if (url.startsWith(PRE) || url.startsWith(PRE_S)) return url;
        return PRE + url;
    }

    /**
     * @param url 非空
     * @return 用UTF8编码
     */
    public static String get(String url) {
        return get(url, CharsetEnum.UTF8);
    }

    public static String bytes2String(byte[] bytes, CharsetEnum charsetEnum) {
        try {
            return new String(bytes, charsetEnum.getStr());
        } catch (UnsupportedEncodingException e) {
//            Logger.error("Unsupported", e);
            return "";
        }
    }

    public static byte[] url2Bytes(String url) {
        if (StringUtils.isEmpty(url)) return new byte[0];
        //构造HttpClient的实例
        HttpClient httpClient = getClient();
        //        httpClient.getParams().getParameter("http.useragent");

        //创建GET方法的实例
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        try {
            //执行getMethod
            int statusCode = httpClient.executeMethod(getMethod);
            //跳转
            if (HttpStatus.SC_MULTIPLE_CHOICES == statusCode || HttpStatus.SC_MOVED_PERMANENTLY == statusCode) {
                get(getMethod.getRequestHeader("Location").getValue());
            }
            if (statusCode != HttpStatus.SC_OK) {
//                Logger.error("Method failed: " + getMethod.getStatusLine());
                return new byte[0];
            }
            //读取、处理内容
            return getMethod.getResponseBody();
        } catch (IOException e) {
            //发生网络异常
//            Logger.error("IO", e);
        } finally {
            //释放连接
            getMethod.releaseConnection();
        }
        return new byte[0];
    }


    /**
     * @param url
     * @param datas
     * @param files
     * @param charsetEnum
     * @return
     */
    public static String post(final String url, Map<String, String> datas, Map<String, File> files, CharsetEnum charsetEnum) {
        if (StringUtils.isEmpty(url)) throw new NullPointerException("url is empty");
        if (null == charsetEnum) charsetEnum = CharsetEnum.UTF8;
        datas = datas == null ? new HashMap<String, String>() : datas;
        files = files == null ? new HashMap<String, File>() : files;
        return bytes2String(post(fixUrl(url), datas, files), charsetEnum);
    }

    public static void main(String[] args) {
        Map strParam = new HashMap();
        strParam.put("markLocations", "center");
        strParam.put("appId", "1");

        Map fileParam = new HashMap();
        File file1 = new File("/home/lin9yuan/Desktop/test_img/1fbe8a347c77d7ec6e2db74861442856.jpg");
        System.out.println("*********************" + file1.isFile());
        fileParam.put("file1", file1);

        System.out.println("*********************");
        String jsonStr = WebClient.post("http://dev.file.cehome.com/uploadImage.pl", strParam, fileParam, CharsetEnum.UTF8);
        System.out.println(jsonStr);
        try {
            Map map = JsonUtils.toObject(jsonStr, HashMap.class);

            for (Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
                String key = iter.next().toString();
                System.out.println("*********************" + key);
                System.out.println("*********************" + map.get(key));
                if (key.equals("urls1")) {
                    Map mapSize = JsonUtils.toObject(map.get(key).toString(), HashMap.class);
                    System.out.println("*********************" + mapSize.get("1200"));
                    System.out.println("*********************" + mapSize.get("900"));
                    System.out.println("*********************" + mapSize.get("640"));
                    System.out.println("*********************" + mapSize.get("240"));
                    System.out.println("*********************" + mapSize.get("120"));
                    System.out.println("*********************" + mapSize.get("org"));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("*********************");
    }


    public static byte[] post(String url, Map<String, String> stringParams, Map<String, File> fileParams) {
        if (StringUtils.isEmpty(url)) return new byte[0];
        PostMethod filePost = new PostMethod(url);
//        filePost.setRequestHeader("Content-Type", "multipart/form-data");
        Part[] parts = new Part[stringParams.size() + fileParams.size()];
        int i = 0;
        if (stringParams != null) {
            for (Iterator iter = stringParams.keySet().iterator(); iter.hasNext(); ) {
                String nextKey = (String) iter.next();
                String nextValue = stringParams.get(nextKey).toString();
                parts[i++] = new StringPart(nextKey, nextValue);
            }
        }
        if (fileParams != null) {
            for (Iterator iter = fileParams.keySet().iterator(); iter.hasNext(); ) {
                String nextKey = (String) iter.next();
                File nextValue = (File) fileParams.get(nextKey);
                try {
                    parts[i++] = new FilePart(nextKey, nextValue);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
        return excute(filePost);
    }

    public static byte[] post(String url, Map<String, String> params) {
        if (StringUtils.isEmpty(url)) return new byte[0];
        PostMethod post = new PostMethod(url);

        if (params != null && !params.isEmpty()) {
            NameValuePair[] data = new NameValuePair[params.size()];
            Iterator keys = params.keySet().iterator();
            int i = 0;
            while (keys.hasNext()) {
                String key = keys.next().toString();
                params.get(key);
                data[i++] = new NameValuePair(key, params.get(key));
            }
            post.setRequestBody(data);
        }
        return excute(post);
    }


    public static byte[] excute(EntityEnclosingMethod mothod) {
        try {
            //构造HttpClient的实例
            HttpClient httpClient = getClient();
            //执行getMethod
            int statusCode = httpClient.executeMethod(mothod);
            //跳转
            if (HttpStatus.SC_MULTIPLE_CHOICES == statusCode || HttpStatus.SC_MOVED_PERMANENTLY == statusCode) {
                get(mothod.getRequestHeader("Location").getValue());
            }
            if (statusCode != HttpStatus.SC_OK) {
//                Logger.error("Method failed: " + mothod.getStatusLine());
                return new byte[0];
            }
            //读取、处理内容
            return mothod.getResponseBody();
        } catch (IOException e) {
            //发生网络异常
//            Logger.error("IO", e);
        } finally {
            //释放连接
            mothod.releaseConnection();
        }
        return new byte[0];
    }

    public static HttpClient getClient() {
        HttpClientParams params = new HttpClientParams();
        params.setParameter(HttpMethodParams.USER_AGENT, USER_AGENT);
        //            params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        params.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        int maxThreadsTotal = 30;
        int maxThreadsPerHost = 3;
        HttpConnectionManagerParams cps = new HttpConnectionManagerParams();
        cps.setConnectionTimeout(10 * 1000);
        cps.setSoTimeout(10 * 1000);
        cps.setMaxTotalConnections(maxThreadsTotal);
        if (maxThreadsTotal > maxThreadsPerHost) {
            cps.setDefaultMaxConnectionsPerHost(maxThreadsPerHost);
        } else {
            cps.setDefaultMaxConnectionsPerHost(maxThreadsTotal);
        }
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(100);
        connectionManager.getParams().setMaxTotalConnections(3000);
        HttpClient client = new HttpClient(params, connectionManager);
        cps.setConnectionTimeout(10 * 1000);
        cps.setSoTimeout(10 * 1000);
        //        client.getHostConfiguration().setHost("ent.sina.com.cn");
        return client;
    }

    public enum CharsetEnum {
        GBK("gbk"),
        UTF8("UTF-8"),
        GB2312("gb2312"),;


        private final String str;

        CharsetEnum(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }
}
