package cn.bevol.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ${chj}. on 2018-01-26.
 */
@Configuration
@ConfigurationProperties(prefix = "env")
public class EnvironmentConfig {
    private String model;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
