package cn.bevol.config.service;

import cn.bevol.config.cache.CACHE_NAME;
import cn.bevol.config.cache.CacheKey;
import cn.bevol.config.cache.CacheableTemplate;
import cn.bevol.config.cache.ehcache.EhCacheProvider;
import cn.bevol.config.constant.conf.Module;
import cn.bevol.config.model.ConfData;
import cn.bevol.util.DateUtils;
import cn.bevol.util.db.Paged;
import cn.bevol.util.response.GlobalResponseCode;
import cn.bevol.util.response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by owen on 16-9-1.
 */
@Service
public class SystemConfService {

    private static Logger logger = LoggerFactory.getLogger(SystemConfService.class);
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    EhCacheProvider cacheProvider;

    public Map<String, Object> getResource(final String key) {

        return new CacheableTemplate<Map>(cacheProvider) {
            @Override
            protected Map getFromRepository() {

                try {
                    Criteria cr = Criteria.where("key").is(key);
                    ConfData conf = mongoTemplate.findAndModify(new Query(cr), new Update().inc("accessNum", 1), ConfData.class);
                    if (conf != null && !StringUtils.isEmpty(conf.getValue()))
                        return ResponseBuilder.buildResult(conf);
                } catch (Exception ex) {
                    logger.error("method:getConf arg:{" + "key:" + key + "}" + "   desc:" + ex.getMessage());
                    return ResponseBuilder.error(GlobalResponseCode.SYSTEM_ERROR);
                }

                return ResponseBuilder.buildFailureMessage("资源不存在或者为空");

            }

            @Override
            protected boolean canPutToCache(Map returnValue) {
                return (returnValue != null
                        && !returnValue.isEmpty()
                        && "0".equals(returnValue.get("ret")));
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MINUTE_1,
                CACHE_NAME.INSTANCE_CONF + key), true);


    }


    public Map upsertResource(String key, String value, String name) {
        try {
            if ((!StringUtils.isEmpty(key))
                    && (!StringUtils.isEmpty(value))
                    ) {

                Update update = new Update().set("key", key).set("value", value).set("updateTime", DateUtils.nowInSeconds());

                //没有 name不更新
                if (!StringUtils.isEmpty(name)) {
                    update.set("name", name);
                }

                Criteria cr = Criteria.where("key").is(key);
                mongoTemplate.upsert(new Query(cr),
                        update,
                        ConfData.class);

                //redis 作为容器时需要
                cacheProvider.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MINUTE_1,
                        CACHE_NAME.INSTANCE_CONF));
                return ResponseBuilder.buildSuccessMessage();
            }
            return ResponseBuilder.error(GlobalResponseCode.OPERATION_NOT_PERMIT);


        } catch (Exception ex) {
            logger.error("method:upsertResource arg:{" + "key:" + key + ",value:" + value + ",name:" + name + "}" + "   desc:" + ex.getMessage());
            return ResponseBuilder.error(GlobalResponseCode.SYSTEM_ERROR);
        }
    }

    public Map findResourceByPage(String module, Integer pageNo, Integer pageSize) {

        try {

            Query query = new Query().skip(pageNo * pageSize).limit(pageSize);
            if (!StringUtils.isEmpty(module)) {
                query.addCriteria(Criteria.where("key").regex("^" + module + "*"));
            }
            List<ConfData> confList = mongoTemplate.find(query, ConfData.class);
            Paged<ConfData> page = new Paged<ConfData>();
            page.setCurPage(pageNo);
            page.setPageSize(pageSize);
            page.setResult(confList);
            page.setTotal((int) mongoTemplate.count(query, ConfData.class));
            page.init();
            return ResponseBuilder.buildResult(page);
        } catch (Exception ex) {
            logger.error("method:findResourceByPage arg:{" + "module:" + module + ",pageNo:" + pageNo + ",pageSize:" + pageSize + "}" + "   desc:" + ex.getMessage());
            return ResponseBuilder.error(GlobalResponseCode.SYSTEM_ERROR);
        }
    }

    public Map findAllModule() {
        return ResponseBuilder.buildResult(Module.modules);
    }
}
