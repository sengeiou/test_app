package com.io97.utils;

import com.io97.utils.http.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Pattern;

public class MobileUtils {



    private static final String IP138_SERVICE_BASE = "http://www.ip138.com:8080/search.asp?action=mobile&mobile=";

    private static final String SERVICE_BASE = "http://api.k780.com:88/?app=phone.get&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json&phone=";

    private static final String SHOUJI_SERVICE_BASE = "http://api.showji.com/Locating/www.showji.c.om.aspx?output=json&m=";

    private static final String SHOUJI_360 = "http://cx.shouji.360.cn/phonearea.php?number=";

    public static boolean isValidMobile(String mobile){
        String regex = "(13\\d|14\\d|15\\d|17\\d|18\\d)\\d{8}";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(mobile).matches();
    }

    public static MobileDetail detailAboutMobile(String mobile){
        if(isValidMobile(mobile)){
            MobileDetail result = detailAboutMobileFrom360(mobile);
            if (null == result) {
                result = detailAboutMobileFromK780(mobile);
            }
            if(result == null)      {
                result = detailAboutMobileFromIP138(mobile);
            }

            return result;
        }
        return null;
    }

    /**
     * 从ip138抓取手机号归属地信息
     * @param mobile
     */
    public static MobileDetail detailAboutMobileFromIP138(String mobile){
        BufferedReader br = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(IP138_SERVICE_BASE + mobile);
            HttpResponse response = client.execute(get);
            StringBuilder result = new StringBuilder();
            if(response!=null && response.getStatusLine().getStatusCode()==200){
                br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "gb2312"));
                char[] cbuf = new char[1024];
                while(br.read(cbuf)!=-1){
                    result.append(cbuf);
                }
            }
            if(result.length() > 0){
                Document html = Jsoup.parse(result.toString());
                Elements elements = html.select(".tdc2");
                if(elements!=null && elements.size() >= 3){
                    MobileDetail md = new MobileDetail();
                    String provinceAndCity = String.valueOf(elements.get(1).text());
                    String type = String.valueOf(elements.get(2).text());
                    if(StringUtils.isNotBlank(provinceAndCity)){
                        String splitChar = String.valueOf('\u00a0');
                        String[] tmp = provinceAndCity.split(splitChar);
                        if(tmp!=null && tmp.length==2){
                            md.setProvince(tmp[0]);
                            md.setCity(tmp[1]);
                        }
                    }
                    md.setMobile(mobile);
                    md.setType(type);
                    return md;
                }
            }
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MobileDetail detailAboutMobileFromShouji(String mobile){
        BufferedReader br = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(SHOUJI_SERVICE_BASE + mobile);
            HttpResponse response = client.execute(get);
            StringBuilder result = new StringBuilder();
            if(response!=null && response.getStatusLine().getStatusCode()==200){
                br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                char[] cbuf = new char[1024];
                while(br.read(cbuf)!=-1){
                    result.append(cbuf);
                }
            }
            if(result.length() > 0){
                Map<String, String> tmp = JsonUtils.toMap(result.toString().toLowerCase());
                MobileDetail md = new MobileDetail();
                md.setMobile(mobile);
                md.setProvince(tmp.get("province"));
                md.setCity(tmp.get("city"));
                md.setType(tmp.get("corp"));
                return md;
            }
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MobileDetail detailAboutMobileFromK780(String mobile){
        BufferedReader br = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(SERVICE_BASE + mobile);
            HttpResponse response = client.execute(get);
            StringBuilder result = new StringBuilder();
            if(response!=null && response.getStatusLine().getStatusCode()==200){
                br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                char[] cbuf = new char[1024];
                while(br.read(cbuf)!=-1){
                    result.append(cbuf);
                }
            }
            if(result.length() > 0){
                Map top = JsonUtils.toMap(result.toString().toLowerCase());
                Map<String, String> tmp = (Map<String, String>)top.get("result");
                MobileDetail md = new MobileDetail();
                md.setMobile(mobile);
                md.setType(tmp.get("operators"));
                String address = tmp.get("att");
                if(StringUtils.isNotBlank(address)){
                    String[] attr = address.split(",");
                    if(attr.length==2){
                        md.setProvince(attr[1]);
                        md.setCity(attr[1]);
                    }
                    else if(attr.length==3){
                        md.setProvince(attr[1]);
                        md.setCity(attr[2]);
                    }
                }
                return md;
            }
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static MobileDetail detailAboutMobileFrom360(String mobile) {
        try {
            String json = HttpUtils.get(SHOUJI_360 + mobile);
            PhoneArea phoneArea = JsonUtils.toObject(json,PhoneArea.class);
            if (null == phoneArea || 0 != phoneArea.getCode() || null == phoneArea.getData()) {
                return null;
            }
            MobileDetail mobileDetail = new MobileDetail();
            mobileDetail.setMobile(trim(mobile));
            mobileDetail.setProvince(trim(phoneArea.getData().province));
            mobileDetail.setCity(trim(phoneArea.getData().city));
            return mobileDetail;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String trim(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return str.trim();
    }

    public static void main(String[] args){
        MobileDetail md1 = detailAboutMobileFrom360("13908491123");
        System.out.println(md1.getCity());
    }

    public static class PhoneArea {
        private int code;
        private Data data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static class Data {
            private String province;
            private String city;
            private String sp;

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getSp() {
                return sp;
            }

            public void setSp(String sp) {
                this.sp = sp;
            }
        }
    }



    public static class MobileDetail {
        private String mobile;

        private String province;

        private String city;

        private String type;

        public String getProvince() {
            return province;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }


}
