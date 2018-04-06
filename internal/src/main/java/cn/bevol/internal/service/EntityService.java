package cn.bevol.internal.service;

import cn.bevol.internal.cache.CacheKey;
import cn.bevol.internal.cache.CacheableTemplate;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.mapper.*;
import cn.bevol.internal.entity.*;
import cn.bevol.internal.entity.constant.api.EntityMeta;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.cache.CACHE_NAME;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EntityService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(EntityService.class);

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Autowired
    GoodsService goodsService;
    @Autowired
    MongoTemplate idsTemplate;

    @Autowired
    RedisCacheProvider cacheProvider;

    @Autowired
    private EntityFindOldMapper findOldMapper;
    
    @Autowired
    private RecoveryOldMapper recoveryOldMapper;

    @Autowired
    private FindOldMapper newfindOldMapper;

    @Autowired
    private GoodsOldMapper goodsOldMapper;

    @Autowired
    private CompositionOldMapper compositionOldMapper;
    
    @Autowired
    private ListsOldMapper listsOldMapper;
    @Autowired
    private GoodsExtOldMapper goodsExtOldMapper;
    
    @Autowired
    CacheService cacheService;


    /**
     * hq_config
     * 根据key获取配置表信息 (app中文案,banner等)
     * 三十分钟缓存
     *
     * @return
     */
    public String getConfig(final String key) {
    	return new CacheableTemplate<String>(cacheProvider) {
            @Override
            protected String getFromRepository() {
                try {
                	String value=findOldMapper.getConfigValue(key);
                	return value;
                } catch (Exception e) {
                	Map map=new HashMap();
                	map.put("method", "EntityService.getConfig");
                	map.put("key", key);
                	new LogException(e,map);
                }
                return null;
            }
            @Override
            protected boolean canPutToCache(String returnValue) {
                return (returnValue != null);
            } 
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_CONFIG_PREFIX,key)), true);
    }
    
    
    /**
     * 获取config表中的 value map类型
     * 缓存
     *
     * @return
     */
    public JSONObject getConfigMap( String key) {
        String value=getConfig(key);
        JSONObject  jasonObject = JSONObject.fromObject(value);
        return jasonObject; 
     }


    /**
     * 根据key修改config表中的 value
     * todo清缓存缓存
     *
     * @return
     */
    public ReturnData updateValue(String key,String value) {
    	try{
    		if(StringUtils.isNotBlank(key)){
        		int i=findOldMapper.updateConfigValue(key,value);
        		return ReturnData.SUCCESS;
        	}
    	}catch (Exception e) {
    		Map map=new HashMap();
        	map.put("method", "EntityService.updateValue");
        	map.put("key", key);
        	map.put("value", value);
        	new LogException(e,map);
        }
    	return ReturnData.ERROR;
     }
    
    /**
     * 用于init接口变更之后调用
     * @return
     */
    public ReturnData colCofValue() {
    	try{
    		String key="update_init";
    		//清除缓存
    		cacheService.cleanCacheListByKey(CACHE_NAME.INSTANCE_CONFIG_PREFIX+"_"+key);
    		int update= Integer.parseInt(this.getConfigMap(key).get("update")+"");
	    	update+=1;
	    	String value="{update:"+update+"}";
	    	ReturnData rd=this.updateValue(key,value);
	    	return rd;
    	}catch (Exception e) {
    		Map map=new HashMap();
        	map.put("method", "EntityService.colCofValue");
        	new LogException(e,map);
        }
    	return ReturnData.ERROR;
     }
    
    

    /**
     * 违禁词
     *	如果有违禁词替换为***
     * @return
     */
    public String keywordInfiltration2(String content,String value) {
    	String[] values=value.split(",");
    	String repStr="***";
    	if(content!=null && values!=null && values.length>0){
        	for(int i=0;i<values.length;i++){
        		if(content.indexOf(values[i])!=-1){
        			content=content.replace(values[i], repStr);
        			//return -1;
        		}
        	}
        }
    	return content;
    }
    


	/**
	 * 对评论 的操作
	 * @param tname
	 * @param opt
	 * @param entityId
	 * @return
	 */
    private static Map<String,String> enField=new HashMap<String,String>();
    static{
    	enField.put("allow_comment", "allowComment");
    }
    
    /**
     * 对实体的操作
     * @param tname: goods,composition,find,lists
     * @param state: 实体的属性
     * @param entityId: 实体id
     * @param val: 1实体属性不能被用户操作 0正常
     * @return
     */
	public ReturnData entityChangeState(String tname, String state, long entityId, int val) {
		EntityBase eb=this.getEntityById(tname, entityId);
		if(eb!=null) {
			String fd=enField.get(state);
			if(fd!=null) {
				//防止没有次对象
		        Map map = mongoTemplate.findAndModify(new Query(Criteria.where("id").is(entityId)), new Update().set(fd, val), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class, EntityMeta.ENTITY_TABLE_PREFIX+tname);
		        
		        //改变扩展表的字段
		        
		        int i=goodsExtOldMapper.updateField("allow_comment", val, "goods_id", eb.getId());
		        //清空缓存
		        cacheService.cleanCacheListByKey("GoodsService.getByGoodsByMid_"+eb.getMid());
		        cacheService.cleanCacheListByKey("GoodsService.getGoodsExplain_"+eb.getMid());
		        cacheService.cleanCacheListByKey("GoodsService.getByGoodsByid_"+eb.getId());
		        
		        return ReturnData.SUCCESS;
			}
		}
		return ReturnData.ERROR;
	}
	
	


    /**
     * 获取完整的路径
     * type 1评论 2举报/纠错/反馈 3心得 4用户上传的产品临时图片
     * @param type
     * @param image
     * @return
     */
	public ReturnData getImageUrl(Integer type, String image) {
		try {
			if(null!=type){
				String tname="";
				if(type==1){
					tname="comment";
				}else if(type==2){
					tname="feedback";
				}else if(type==3){
					tname="user_part/lists";
				}else if(type==4){
					tname="goods_upload/images";
				}
				String imageUrl= CommonUtils.getImageSrc(tname, image);
				if(StringUtils.isNotBlank(imageUrl)){
					return new ReturnData(imageUrl);
				}
			}
		} catch (Exception e) {
			Map map=new HashMap();
        	map.put("image", image);
        	map.put("type", type);
        	map.put("method", "EntityService.getImageUrl");
        	new LogException(e,map);
		}
		return null;
	}


	
}
