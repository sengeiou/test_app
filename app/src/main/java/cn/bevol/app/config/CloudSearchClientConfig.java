package cn.bevol.app.config;

import cn.bevol.util.Log.LogMethod;
import com.aliyun.opensearch.CloudsearchClient;
import com.aliyun.opensearch.object.KeyTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
@ConfigurationProperties(prefix = "client.cloudsearch")
public class CloudSearchClientConfig{

    private Map<String, Object> opts;

    /**
     * 用户的client id。
     *
     * 此信息由网站中提供。
     */
    private String clientId;

    /**
     * 用户的秘钥。
     *
     * 此信息由网站中提供。
     */
    private String clientSecret;

    /**
     * 请求API的base URI.
     */
    private String baseURI;

    /**
     * 当前API的版本号。
     */
    private String version = "v2";

    /**
     * 请求的domain地址。
     */
    private String host;

    /**
     * 用户类型，包含opensearch老用户和阿里云用户
     */
    private KeyTypeEnum keyType = KeyTypeEnum.ALIYUN;

    /**
     * 用户阿里云网站中的accesskey,keyYype为ALIYUN使用 此信息阿里云网站中提供
     */
    private String accesskey;

    /**
     * 用户阿里云网站中的secret,keyYype为ALIYUN使用 此信息阿里云网站中提供
     */
    private String secret;

    private String openSearchIndexPre;

    private String hotEntity;

    @Bean
    @LogMethod
    public CloudsearchClient getClient(){
        try {
            CloudsearchClient client = new CloudsearchClient(this.clientId, this.clientSecret, this.host, this.opts, KeyTypeEnum.ALIYUN);
            return client;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setOpts(Map<String, Object> opts) {
        this.opts = opts;
    }

    public Map<String, Object> getOpts() {
        return opts;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setKeyType(KeyTypeEnum keyType) {
        this.keyType = keyType;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setOpenSearchIndexPre(String openSearchIndexPre) {
        this.openSearchIndexPre = openSearchIndexPre;
    }

    public String getOpenSearchIndexPre() {
        return openSearchIndexPre;
    }

    public void setHotEntity(String hotEntity) {
        this.hotEntity = hotEntity;
    }

    public String getHotEntity() {
        return hotEntity;
    }
}
