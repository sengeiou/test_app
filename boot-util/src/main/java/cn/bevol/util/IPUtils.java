package cn.bevol.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;


public class IPUtils {


    private static final String TAOBAO_IP_SERVICE_BASE = "http://ip.taobao.com/service/getIpInfo.php?ip=";

    /**
     * 从taobao的IP库获取IP地址的相关详细信息
     *
     * @param ip
     * @return 返回的数据结构如下
     * {
     * code=0, //0表示正常
     * data={
     * country=中国,
     * country_id=CN,
     * area=华北,
     * area_id=100000,
     * region=北京市,
     * region_id=110000,
     * city=北京市,
     * city_id=110000,
     * county=,
     * county_id=-1,
     * isp=电信,
     * isp_id=100017,
     * ip=106.120.244.37
     * }
     * }
     */
    public static final IpDetail detailAboutIp(String ip) {

        if (isValidIp(ip)) {
            BufferedReader br = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(TAOBAO_IP_SERVICE_BASE + ip);
                HttpResponse response = client.execute(get);
                StringBuilder result = new StringBuilder();
                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    char[] cbuf = new char[1024];
                    while (br.read(cbuf) != -1) {
                        result.append(cbuf);
                    }
                }

                if (StringUtils.isNotBlank(result.toString()))
                    return JsonUtils.toObject(result.toString(), IpDetail.class);
                return null;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private static boolean isValidIp(String ip) {
        String ipSagment = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])";
        String regex = ipSagment + "\\." + ipSagment + "\\." + ipSagment + "\\." + ipSagment;
        return Pattern.matches(regex, ip);
    }

    public static void main(String[] args) {
        IpDetail ipDetail = IPUtils.detailAboutIp("106.120.244.37");
        System.out.println(ipDetail.getCountry());
        System.out.println(ipDetail.getArea());
        System.out.println(ipDetail.getRegion());
        System.out.println(ipDetail.getCity());
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static class IpDetail {

        private String code;

        private Detail data;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Detail getData() {
            return data;
        }

        public void setData(Detail data) {
            this.data = data;
        }

        public String getCountry() {
            return this.data.getCountry();
        }

        public String getArea() {
            return this.data.getArea();
        }

        public String getRegion() {
            return this.data.getRegion();
        }

        public String getCity() {
            return this.data.getCity();
        }

        public String getIp() {
            return this.data.getIp();
        }

        public static class Detail {

            private String country;

            private String country_id;

            private String area;

            private String area_id;

            private String region;

            private String region_id;

            private String city;

            private String city_id;

            private String county;

            private String county_id;

            private String isp;

            private String isp_id;

            private String ip;

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getIsp() {
                return isp;
            }

            public void setIsp(String isp) {
                this.isp = isp;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }

            public String getCountry_id() {
                return country_id;
            }

            public void setCountry_id(String country_id) {
                this.country_id = country_id;
            }

            public String getArea_id() {
                return area_id;
            }

            public void setArea_id(String area_id) {
                this.area_id = area_id;
            }

            public String getRegion_id() {
                return region_id;
            }

            public void setRegion_id(String region_id) {
                this.region_id = region_id;
            }

            public String getCity_id() {
                return city_id;
            }

            public void setCity_id(String city_id) {
                this.city_id = city_id;
            }

            public String getCounty() {
                return county;
            }

            public void setCounty(String county) {
                this.county = county;
            }

            public String getCounty_id() {
                return county_id;
            }

            public void setCounty_id(String county_id) {
                this.county_id = county_id;
            }

            public String getIsp_id() {
                return isp_id;
            }

            public void setIsp_id(String isp_id) {
                this.isp_id = isp_id;
            }

        }
    }
}
