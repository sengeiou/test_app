package com.io97.cache.redis;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

public class Configure {

    private static Resource configFile = null;
    private static Properties config = null;

    static {  
        try {  
            URL configureFileUrl = Configure.class.getResource("/redis-cache.properties");
            if (configureFileUrl != null) {
            	try {
                    configFile = new InputStreamResource(new FileInputStream(configureFileUrl.getFile()));
            	} catch(Exception ex){
            		  ex.printStackTrace();
                      configFile = new InputStreamResource(Configure.class.getResourceAsStream("/redis-cache.properties"));
            	}
            } else {
                configFile = new InputStreamResource(Configure.class.getResourceAsStream("/redis-cache.properties"));
            }

            config = PropertiesLoaderUtils.loadProperties(configFile);
            for (Object key : config.keySet()) {
                System.out.println(String.valueOf(key));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
}
