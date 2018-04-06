package cn.bevol.internal.service;

import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.cache.CacheKey;
import cn.bevol.internal.cache.CacheableTemplate;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.mapper.GoodsOldMapper;
import cn.bevol.internal.dao.mapper.SearchOldMapper;
import cn.bevol.internal.entity.dto.SeachComposition;
import cn.bevol.internal.entity.model.Goods;
import cn.bevol.util.cache.CACHE_NAME;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private SearchOldMapper searchOldMapper;

    @Autowired
    RedisCacheProvider cacheProvider;

    @Autowired
    GoodsOldMapper goodsOldMapper;
    
    @Resource
    EntityService entityService;

    /**
     * 排除不需要的成分
     * @return
     */
    @Deprecated
    public Map ruleOutComposition() {
        return new CacheableTemplate<Map>(cacheProvider) {
            @Override
            protected Map getFromRepository() {
                try {
                    List<SeachComposition> entityComposition = searchOldMapper.ruleOutComposition();
                    Map map = new HashMap();
                    if (null != entityComposition) {
                        map.put("composition", entityComposition);
                        return map;
                    }
                    return (Map) new ReturnData(-1,"数据为空");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    return (Map) ReturnData.ERROR;
                }
            }

            @Override
            protected boolean canPutToCache(Map returnValue) {
                return (returnValue.get("composition") != null);
            }
        }.execute(new CacheKey("bevol", CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "SearchService.ruleOutComposition"), true);
    }


    /**
     * 查找需要的产品
     * @return
     */
    @Deprecated
    public List ruleOutGoods() {
        List list=new ArrayList();
        Map map1 = new HashMap();
        map1.put("id","6");
        map1.put("name","洁面");
        list.add(map1);

        Map map2 = new HashMap();
        map2.put("id","7");
        map2.put("name","化妆水");
        list.add(map2);

        Map map3 = new HashMap();
        map3.put("id","8");
        map3.put("name","面霜/乳液");
        list.add(map3);

        Map map4 = new HashMap();
        map4.put("id","9");
        map4.put("name","精华");
        list.add(map4); 

        Map map5 = new HashMap();
        map5.put("id","10");
        map5.put("name","眼部护理");
        list.add(map5);

        Map map6 = new HashMap();
        map6.put("id","11");
        map6.put("name","面膜");
        list.add(map6);

        Map map7 = new HashMap();
        map7.put("id","12");
        map7.put("name","卸妆");
        list.add(map7);

        Map map8 = new HashMap();
        map8.put("id","13");
        map8.put("name","防晒");
        list.add(map8);

        Map map9 = new HashMap();
        map9.put("id","14");
        map9.put("name","美白");
        list.add(map9);

        Map map10 = new HashMap();
        map10.put("id","15");
        map10.put("name","去角质");
        list.add(map10);

        Map map11 = new HashMap();
        map11.put("id","16");
        map11.put("name","抗痘");
        list.add(map11);

        Map map12 = new HashMap();
        map12.put("id","20");
        map12.put("name","隔离／妆前");
        list.add(map12);

        Map map13 = new HashMap();
        map13.put("id","47");
        map13.put("name","护唇");
        list.add(map13);

        Map map14 = new HashMap();
        map14.put("id","30");
        map14.put("name","美体");
        list.add(map14);


        Map map16 = new HashMap();
        map16.put("id","38");
        map16.put("name","洗护");
        list.add(map16);
        return list;
    }

    @Deprecated
    public List<Goods> getGoodsByCategory(final String category){
    	return new CacheableTemplate<List<Goods>>(cacheProvider) {
            @Override
            protected List<Goods> getFromRepository() {
                try {
                	List<Goods> goodss=goodsOldMapper.getGoodsByCategory(category);
                    return goodss;
                } catch (Exception e) {
                    logger.error("method:getGoodsByCategory arg:{category:" + category+ "   desc:" + e.getMessage());
                }
				return null;
            }
            @Override
            protected boolean canPutToCache(List<Goods> returnValue) {
                return (returnValue != null && returnValue.size()>= 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "SearchService.getGoodsByCategory"), true);
    	
    }
    
    /**
     * app5之前
     * @return
     */
    @Deprecated
    public List ruleOutGoods2() {
    	String key="ruleOutGoods";
    	try{
    		String value=entityService.getConfig(key);
        	if(!StringUtils.isBlank(value)){
        		 JSONArray json = JSONArray.fromObject(value);
                 List list = new ArrayList();
                 if (json.size() > 0) {
                     for (int i = 0; i < json.size(); i++) {
                         Map map = new HashMap();
                         JSONObject job = json.getJSONObject(i);
                         map.put("id", job.get("id"));
                         map.put("name", job.get("name") );
                         list.add(map);
                     }
                 }
                 return list;
        	}
    	}catch(Exception e){
    		 logger.error("method:ruleOutGoods2 arg:{}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
             return new ArrayList();
    	}
    	return null;
    }
    

    @Deprecated
    public List<Map<String,Object>> getAllSafter(){
    	return new CacheableTemplate<List<Map<String,Object>>>(cacheProvider) {
            @Override
            protected List<Map<String,Object>> getFromRepository() {
                try {
            		List<Map<String,Object>> safetyList=goodsOldMapper.getAllSafter();
                    return safetyList;
                } catch (Exception e) {
                    logger.error("method:getAllSafter arg:{" + "   desc:" + e.getMessage());
                }
				return null;
            }
            @Override
            protected boolean canPutToCache(List<Map<String,Object>> returnValue) {
                return (returnValue != null && returnValue.size()>= 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "SearchService.getAllSafter"), true);
    }

}
