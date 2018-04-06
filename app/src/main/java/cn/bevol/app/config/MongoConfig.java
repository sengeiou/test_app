package cn.bevol.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author mysens
 * @date 17-12-25 下午3:17
 */
@Configuration
public class MongoConfig{

    /*@Bean
    public MongoTemplate mongoTemple(MongoDbFactory mongoDbFactory){
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        CustomConversions conversions = new CustomConversions(Collections.emptyList());

        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();

        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(conversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();

        return new MongoTemplate(mongoDbFactory, converter);
    }*/

    @Bean
    public MongoTemplate mongoTemple(MongoDbFactory mongoDbFactory){
        return new MongoTemplate(mongoDbFactory);
    }
}
