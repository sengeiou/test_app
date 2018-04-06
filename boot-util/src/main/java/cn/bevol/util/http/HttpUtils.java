package cn.bevol.util.http;

import com.google.common.base.Joiner;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    private static final int SO_TIMEOUT = 3000;
    private static final String ENCODING = "UTF-8";

     public static String get(String url, List<NameValuePair> nvps) {
        url += "?" + Joiner.on("&").join(nvps);
       return   get(url);
    }

    public static String get(String url) {
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            HttpResponse response = HttpClientFactory.get().execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    return IOUtils.toString(response.getEntity().getContent(), ENCODING);
                }
                return null;
            } else
                return "response code : " + response.getStatusLine().getStatusCode();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String get(String url,Integer timeoutMS) {
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            HttpResponse response = HttpClientFactory.get(timeoutMS).execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    return IOUtils.toString(response.getEntity().getContent(), ENCODING);
                }
                return null;
            } else
                return "fault";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * post forms
     *
     * @param url
     * @param postData
     * @return
     */
    public static String post(String url, Map<String, String> postData) {
        if (postData != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : postData.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            return post(url, formparams);
        }
        return "fault";
    }

    public static String post(String url, Map<String, String> postData,int timeoutMS) {
        if (postData != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : postData.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            return post(url, formparams,timeoutMS);
        }
        return "fault";
    }

    /**
     * 只需要触发的post
     * @param url
     * @param postData
     * @return
     */
    public static void TriggerPost(String url, Map<String, String> postData){
        if (postData != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : postData.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post2(url, formparams);
        }
    }

    public static String post(String url, Object postData) {
        if (postData != null) {
            ObjectMapper m = new ObjectMapper();
            JavaType genericMap = m.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            Map<String, Object> props = m.convertValue(postData, genericMap);
            List<NameValuePair> formparams = new LinkedList<NameValuePair>();
            for (Map.Entry<String, Object> entry : props.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }

            return post(url, formparams);
        }
        return "fault";
    }

    public static String post(String url, Object postData,int timeoutMS) {
        if (postData != null) {
            ObjectMapper m = new ObjectMapper();
            JavaType genericMap = m.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            Map<String, Object> props = m.convertValue(postData, genericMap);
            List<NameValuePair> formparams = new LinkedList<NameValuePair>();
            for (Map.Entry<String, Object> entry : props.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }

            return post(url, formparams, timeoutMS);
        }
        return "fault";
    }

    public static String post(String url, List<NameValuePair> postData) {
        return post(url, postData, 4000);
    }

    /**
     * 打印异常的post
     * @param url
     * @param postData
     * @param timeoutMS
     * @return
     */
    public static String post(String url, List<NameValuePair> postData,int timeoutMS) {
        HttpPost post = null;
        try {
            post = new HttpPost(url);

            if (null != postData && !postData.isEmpty()) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, Consts.UTF_8);
                post.setEntity(entity);
            }
            HttpResponse response = HttpClientFactory.get(timeoutMS).execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    return IOUtils.toString(response.getEntity().getContent(), ENCODING);
                }
                return "success";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "fault";
    }

    /**
     * 不打印异常的仅需要触发post
     * @param url
     * @param postData
     * @return
     */
    public static String post2(String url, List<NameValuePair> postData) {
        HttpPost post = null;
        try {
            post = new HttpPost(url);

            if (null != postData && !postData.isEmpty()) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, Consts.UTF_8);
                post.setEntity(entity);
            }
            HttpResponse response = HttpClientFactory.get(20).execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    return IOUtils.toString(response.getEntity().getContent(), ENCODING);
                }
                return "success";
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return "success";
    }
    
    public static String post1(String url, Object postData) throws Exception {
    	if (postData != null) {
            ObjectMapper m = new ObjectMapper();
            JavaType genericMap = m.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            Map<String, Object> props = m.convertValue(postData, genericMap);
            List<NameValuePair> formparams = new LinkedList<NameValuePair>();
            for (Map.Entry<String, Object> entry : props.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }

            return post1(url, formparams);
        }
        return "fault";
    }
    public static String post1(String url, Object postData,int timeoutMs) throws Exception {
        if (postData != null) {
            ObjectMapper m = new ObjectMapper();
            JavaType genericMap = m.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            Map<String, Object> props = m.convertValue(postData, genericMap);
            List<NameValuePair> formparams = new LinkedList<NameValuePair>();
            for (Map.Entry<String, Object> entry : props.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }

            return post1(url, formparams,timeoutMs);
        }
        return "fault";
    }

    public static String post1(String url, List<NameValuePair> postData) throws Exception {
        HttpPost post = null;
            post = new HttpPost(url);
            if (null != postData && !postData.isEmpty()) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, Consts.UTF_8);
                post.setEntity(entity);
            }
            HttpResponse response = HttpClientFactory.get().execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    return IOUtils.toString(response.getEntity().getContent(), ENCODING);
                }
                return "success";
            }
        return "fault"+response.getStatusLine().getStatusCode();
    }


    /**
     * post string content
     *
     * @param url
     * @param postData
     * @return
     */
    public static String post(String url, String postData) {
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            HttpEntity entity = new StringEntity(postData, ENCODING);
            post.setEntity(entity);
            HttpResponse response = HttpClientFactory.get().execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                    return IOUtils.toString(response.getEntity().getContent(), ENCODING);
                }
                return "success";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "fault";
    }


    public static String doPost(String url, Map<String, String> fields) throws HttpException, IOException {
        // 构造HttpClient的实例
        HttpClient httpClient = new HttpClient(); // 创建GET方法的实例
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(800000);
        PostMethod getMethod = new PostMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        getMethod.addRequestHeader("Content-type", "text/html; charset=utf-8");
        getMethod.addRequestHeader("Accept", "*/*");
        getMethod.addRequestHeader("Accept-Encoding", "gzip, deflate");
        getMethod.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
        getMethod.addRequestHeader("Cache-Control", "max-age=0");
        getMethod.addRequestHeader("Cookie", "PHPSESSID=2c08mpvuuf5tm3ur04ge5ac4v1");
        getMethod.addRequestHeader("Host", "www.caimiapp.com");
        getMethod.addRequestHeader("Proxy-Connection", "keep-alive");
        getMethod.addRequestHeader("Upgrade-Insecure-Requests", "1");
        getMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
        if (fields != null)
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                getMethod.addParameter(entry.getKey(), entry.getValue() + "");
            }
        String result = "";
        try {
            httpClient.setTimeout(3000);
            // 执行getMethod
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode == HttpStatus.SC_OK) {

//                result = new String(getMethod.getResponseBodyAsString());

                InputStream txtis = getMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        txtis));

                String tempbf;
                StringBuffer html = new StringBuffer(100);
                while ((tempbf = br.readLine()) != null) {
                    html.append(tempbf);
                }
                return html.toString();
                //todo !!2016-06-21 19:27:18,142 WARN [pool-7-thread-1] org.apache.commons.httpclient.HttpMethodBase.HttpMethodBase#getResponseBody [HttpMethodBase.java:682] Going to buffer response body of large or unknown size. Using getResponseBodyAsStream instead is recommended.
            } else {
                System.err.println("Method failed: " + getMethod.getStatusLine());
            }

        } finally {
            // 释放连接
            getMethod.releaseConnection();
        }
        return result;
    }
    
   /* public static void main(String[] args) {
		//微信回调地址
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        //请求url
        Map<String,String> postData=new HashMap<String,String>();
        postData.put("appid", "");
        postData.put("secret", "");
        postData.put("code", "");
        postData.put("grant_type", "");
      //  https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx55456de48120a75f&secret=5e51f7203e2cb686571e823afa8117a9&code=021glDwD1l3pq100vWtD1tbKwD1glDwS&grant_type=authorization_code
      try {
		String   resultJson = HttpUtils.post1(url, postData);
		System.out.println(resultJson);
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}*/
}
