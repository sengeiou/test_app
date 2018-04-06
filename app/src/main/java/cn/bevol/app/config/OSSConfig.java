package cn.bevol.app.config;

import com.aliyun.oss.OSSClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mysens
 * @date 18-1-2 下午1:46
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OSSConfig {

    private  String pcAccessId;
    private  String mAccessId;
    private  String sourceAccessId;
    private  String pcAccessKey;
    private  String mAccessKey;
    private  String sourceAccessKey;
    private  String pcEndpoint;
    private  String mEndpoint;
    private  String sourceEndpoint;
    private static String pcBucket;
    private static String mBucket;
    private static String sourceBucket;

    public String getPcAccessId() {
        return pcAccessId;
    }

    public void setPcAccessId(String pcAccessId) {
        this.pcAccessId = pcAccessId;
    }

    public String getmAccessId() {
        return mAccessId;
    }

    public void setmAccessId(String mAccessId) {
        this.mAccessId = mAccessId;
    }

    public String getSourceAccessId() {
        return sourceAccessId;
    }

    public void setSourceAccessId(String sourceAccessId) {
        this.sourceAccessId = sourceAccessId;
    }

    public String getPcAccessKey() {
        return pcAccessKey;
    }

    public void setPcAccessKey(String pcAccessKey) {
        this.pcAccessKey = pcAccessKey;
    }

    public String getmAccessKey() {
        return mAccessKey;
    }

    public void setmAccessKey(String mAccessKey) {
        this.mAccessKey = mAccessKey;
    }

    public String getSourceAccessKey() {
        return sourceAccessKey;
    }

    public void setSourceAccessKey(String sourceAccessKey) {
        this.sourceAccessKey = sourceAccessKey;
    }

    public String getPcEndpoint() {
        return pcEndpoint;
    }

    public void setPcEndpoint(String pcEndpoint) {
        this.pcEndpoint = pcEndpoint;
    }

    public String getmEndpoint() {
        return mEndpoint;
    }

    public void setmEndpoint(String mEndpoint) {
        this.mEndpoint = mEndpoint;
    }

    public String getSourceEndpoint() {
        return sourceEndpoint;
    }

    public void setSourceEndpoint(String sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }

    public static String getPcBucket() {
        return pcBucket;
    }

    public static void setPcBucket(String pcBucket) {
        OSSConfig.pcBucket = pcBucket;
    }

    public static String getmBucket() {
        return mBucket;
    }

    public static void setmBucket(String mBucket) {
        OSSConfig.mBucket = mBucket;
    }

    public static String getSourceBucket() {
        return sourceBucket;
    }

    public static void setSourceBucket(String sourceBucket) {
        OSSConfig.sourceBucket = sourceBucket;
    }




    @Bean("pc_client")
    public OSSClient createPCClient(){
        return new OSSClient(
                this.getPcEndpoint(),
                this.getPcAccessId(),
                this.getPcAccessKey());
    }

    @Bean("m_client")
    public OSSClient createMClient(){
        return new OSSClient(
                this.getmEndpoint(),
                this.getmAccessId(),
                this.getmAccessKey());
    }

    @Bean("source_client")
    public OSSClient createSourceClient(){
        return new OSSClient(
                this.getSourceEndpoint(),
                this.getSourceAccessId(),
                this.getSourceAccessKey());
    }
}
