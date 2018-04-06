package cn.bevol.statics.config;

import cn.bevol.statics.entity.items.MpAccount;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mysens
 * @date 18-1-2 下午2:45
 */
@Configuration
@ConfigurationProperties("bevol.wx")
public class WXConfig {

    private String requiredUrl;
    private String account;
    private String appid;
    private String accessId;
    private String appsecret;

    public String getRequiredUrl() {
        return requiredUrl;
    }

    public void setRequiredUrl(String requiredUrl) {
        this.requiredUrl = requiredUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    @Bean
    public MpAccount createMpAccount(){
        MpAccount mpAccount = new MpAccount();
        mpAccount.setUrl(requiredUrl);
        mpAccount.setAccount(account);
        mpAccount.setAppid(appid);
        mpAccount.setToken(accessId);
        mpAccount.setAppsecret(appsecret);
        return mpAccount;
    }
}
