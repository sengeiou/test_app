package cn.bevol.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author mysens
 * @date 17-12-25 下午3:36
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig{
    private String password;
    private String host;
    private int port;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate template = new RedisTemplate();
        StringRedisSerializer redisSerializer = new StringRedisSerializer();
        template.setConnectionFactory(factory);
//        template.setValueSerializer(redisSerializer);
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
