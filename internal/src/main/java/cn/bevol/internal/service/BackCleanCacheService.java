package cn.bevol.internal.service;


import cn.bevol.internal.cache.CacheKey;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.util.cache.CACHE_NAME;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
     */
    public void cleanHomeListByKey() {
    	//更新init接口版本值
    	entityService.colCofValue();
    	
    	String type= CACHE_NAME.TIME;
    	//要清理的缓存的key值:init,index,文章的列表和banner,修行社bannner,消息的banner,福利社列表的缓存,文章详情
    	String[] keys={"index_","initApp_","config","open_app","sns_i","find_ls","alyg_ls","find_info"};
    	for(int i=0;i<keys.length;i++){
    		cacheProvider.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MATCH_ALL_CACHE_QUEUE,
    				type+keys[i]
            ));
    	}
    }
	
 }