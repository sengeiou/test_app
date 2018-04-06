package cn.bevol.statics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author mysens
 * @date 17-12-25 下午3:36
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate template = new RedisTemplate();
        StringRedisSerializer redisSerializer = new StringRedisSerializer();
        template.setConnectionFactory(factory);
        template.setKeySerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }

}
