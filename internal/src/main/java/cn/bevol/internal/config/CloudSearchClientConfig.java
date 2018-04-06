package cn.bevol.internal.config;

import com.aliyun.opensearch.CloudsearchClient;
import com.aliyun.opensearch.object.KeyTypeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

/**
 * @author mysens
 * @date 17-12-29 下午4:34
 */
@Configuration
public class CloudSearchClientConfig {

    @Bean
    public CloudsearchClient cloudsearchClient() throws UnknownHostException {
        return new CloudsearchClient("lMZwMNFhiR5o1TfO",
                "Qt6ZzNDaoGtwwcHWWGAn1bhdP3qvFC",
                "http://opensearch-cn-hangzhou.aliyuncs.com",
                null,
                KeyTypeEnum.ALIYUN);
    }

}
