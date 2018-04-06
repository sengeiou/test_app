package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修行社首页
 * @author hualong
 *
 */
@Service
public class SnsService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(SnsService.class);
    @Resource
    private EntityService entityService;
    @Resource
    private HotListService hotListService;

    @Resource
    private FindService findService;
    
    @Resource
    private IndexService indexService;
    
    @Resource
    private BaseService baseService;
    
    
    @Autowired
    RedisCacheProvider cacheProvider;

    @Resource
    private UserPartService userPartService;

    
    
	public ReturnData index(final int type, final int pager, final int pageSize) {
		return new CacheableTemplate<ReturnData>(cacheProvider) {
            @Override
            protected ReturnData getFromRepository() {
				try {
					Map m=new HashMap();
					if(pager<=1) {
						int rows=3;
						//修行社banners
	                    List<Map> xxsBanners=entityService.getConfigList("xxs_banners");
	                    //修行社banner广告处理
	                    xxsBanners=indexService.getList(xxsBanners,6,"1");
	                    baseService.compareGoodsMids(xxsBanners);
	                    m.put("xxsBanners", xxsBanners);
	                    
	                    //修行社按钮文案
	                    List xxsButton=entityService.getConfigList("xxs_button");
	                    m.put("xxsButton", xxsButton);
						
						//热门话题
						m.put("hostLists", hotListService.findLists(1,1, rows).getResult());
						
						//发现列表
						m.put("findLists", findService.findListByOpenSearch(0, null, null, null, 1, rows).getResult());
					}
					//用户精选(心得),包含自由/福利社/普通心得
					m.put("userParts", userPartService.list(type,pager, pageSize));
					return new ReturnData(m);
				} catch (Exception e) {
			        logger.error("method:findList arg:{pager:" + pager + ",pageSize:" + pageSize + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
			    }
	        return ReturnData.ERROR;
            } 
            @Override
            protected boolean canPutToCache(ReturnData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet()== 0   );
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_SNS_INDEX_PREFIX,type+"",pager+"",pageSize+"")), true);

	}
	
 
}
