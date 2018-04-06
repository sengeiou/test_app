package cn.bevol.app.config;

import com.taobao.api.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "client.taobao")
public class TaobaoClientConfig{
    private String serverUrl;
    private String appKey;
    private String appSecret;

    @Bean
    public DefaultTaobaoClient getTaobaoClient() {
        this.appKey = this.appKey;
        this.appSecret = this.appSecret;
        this.serverUrl = this.serverUrl;
        return new DefaultTaobaoClient(this.serverUrl,this.appKey,this.appSecret);
    }


    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

}
