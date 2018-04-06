package cn.bevol.app.cache.redis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class Configure implements CommandLineRunner{

    private static String defaults;
    //private static Map bevol;
    private static String fiveM;
    private static String oneDay;
    private static String forever;
    private static String thirtyM;

    private static Properties config = null;

    /*static {
        try {
            *//*URL configureFileUrl = Configure.class.getResource("/redis-cache.properties");
            if (configureFileUrl != null) {
            	try {
                    configFile = new InputStreamResource(new FileInputStream(configureFileUrl.getFile()));
            	} catch(Exception ex){
            		  ex.printStackTrace();
                      configFile = new InputStreamResource(Configure.class.getResourceAsStream("/redis-cache.properties"));
            	}
            } else {
                configFile = new InputStreamResource(Configure.class.getResourceAsStream("/redis-cache.properties"));
            }*//*
            config=new Properties();
            config.put("bevol.foreve",Configure.forever);
            config.put("bevol.5_m",Configure.fiveM);
            config.put("bevol.1440_m",Configure.oneDay);
            config.put("default",Configure.defaults);
            for (Object key : config.keySet()) {
                System.out.println(String.valueOf(key));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    public void setConfig(){
        config=new Properties();
        config.put("bevol.foreve",Configure.forever);
        config.put("bevol.5_m",Configure.fiveM);
        config.put("bevol.1440_m",Configure.oneDay);
        config.put("default",Configure.defaults);
        /*for (Object key : config.keySet()) {
            System.out.println(String.valueOf(key));
        }*/
    }

    public static String get(String key) {
        return config.getProperty(key);
    }

    public static String getDefaultCacheTime() {
        return config.getProperty("default");
    }

    public static String get(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    public static String getDefaults() {
        return defaults;
    }

    public static void setDefaults(String defaults) {
        Configure.defaults = defaults;
    }

    public static String getFiveM() {
        return fiveM;
    }

    public static void setFiveM(String fiveM) {
        Configure.fiveM = fiveM;
    }

    public static String getOneDay() {
        return oneDay;
    }

    public static void setOneDay(String oneDay) {
        Configure.oneDay = oneDay;
    }

    public static String getForever() {
        return forever;
    }

    public static void setForever(String forever) {
        Configure.forever = forever;
    }

    @Override
    public void run(String... args) throws Exception {
        this.setConfig();
    }

    public static String getThirtyM() {
        return thirtyM;
    }

    public static void setThirtyM(String thirtyM) {
        Configure.thirtyM = thirtyM;
    }
}
