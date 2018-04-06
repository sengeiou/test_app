package cn.bevol.internal.service;



import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.io97.cache.CacheKey;
import com.io97.cache.redis.RedisCacheProvider;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.entity.service.BaseService;
import cn.bevol.entity.service.EntityService;

/**
 * 清理缓存
 * @author chenHaiJian
 *
 */

@Service
public class BackCleanCacheService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(BackTagService.class);
	@Autowired
    RedisCacheProvider cacheProvider;
	
	@Resource
    EntityService entityService;
	
	/**
     * 一键清除常用的缓存(index,init)
     *
     * @param key
     */
    public void cleanHomeListByKey() {
    	//更新init接口版本值
    	entityService.colCofValue();
    	
    	String type=CACHE_NAME.TIME;
    	//要清理的缓存的key值:init,index,文章的列表和banner,修行社bannner,消息的banner,福利社列表的缓存,文章详情
    	String[] keys={"index_","initApp_","config","open_app","sns_i","find_ls","alyg_ls","find_info"};
    	for(int i=0;i<keys.length;i++){
    		cacheProvider.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MATCH_ALL_CACHE_QUEUE,
    				type+keys[i]
            ));
    	}
    }
	
 }