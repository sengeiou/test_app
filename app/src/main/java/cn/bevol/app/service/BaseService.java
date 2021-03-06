package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.app.entity.constant.api.EntityMeta;
import cn.bevol.model.entityAction.EntityActionBase;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.GlobalConfig;
import cn.bevol.model.entity.MongoBase;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import com.mongodb.WriteResult;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BaseService {
	
    @Autowired
	RedisCacheProvider cacheProvider;
    
	private static Logger logger = LoggerFactory.getLogger(BaseService.class);
	public static Configuration config =null;
	static{
		try {
			config= new PropertiesConfiguration("bevol-dp.ini");
		} catch (ConfigurationException e) {
			Map map=new HashMap();
    		map.put("method", "BaseService.bevol-dp.ini");
    		new LogException(e,map);
		}
	}

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    private GoodsOldMapper goodsOldMapper;

    private static Map<String, String> kivu = new HashMap<String, String>();
    private static Map<String, String> kuvi = new HashMap<String, String>();
    //cookies????????????
    private static Map<String, String> userviu = new HashMap<String, String>();
    private static Map<String, UserInfo> users = new HashMap<String, UserInfo>();


    /**
     * ?????????????????????????????????
     * @param tname
     * @param id
     * @return  ????????????null
     */
    public EntityBase getEntityById(String tname, long id) {
        String entityTname = "entity_" + tname;
        EntityBase eb=   mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), EntityBase.class, entityTname);
       
        //??????????????????????????????
        if(eb!=null&&eb.getId()>0&& StringUtils.isBlank(eb.getAlias())&&tname.equals("goods")) {
    		Goods goods=goodsOldMapper.getById(eb.getId());
        	mongoTemplate.findAndModify(new Query(new Criteria().where("id").is(eb.getId())), new Update().set("title", goods.getTitle()).set("alias", goods.getAlias()), EntityActionBase.class, entityTname);
        	eb.setTitle(goods.getTitle());
        	eb.setAlias(goods.getAlias());
        }
        return eb;
        
    }
    
    
    /**
     * ????????????id
     */
    public Long getUniqueId() {
        Map map = mongoTemplate.findAndModify(new Query(Criteria.where("name").is("unique_id")), new Update().inc("id", 1), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class,  EntityMeta.UNIQUE_ID);
        if(map==null||map.get("name")==null) return null;
        Long id= Long.parseLong(map.get("id")+"");
        return id;
        
    }
    


    
    /**
     * ?????????????????????????????????
     * @param id
     * @param id
     * @return  ????????????null
     */
    public EntityBase getEntityById(Class clazz, long id) {
        EntityBase eb= (EntityBase) mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), clazz);
        return eb;
        
    }
    

 

    public Map getId(String tname, String collection) {
        Map map = mongoTemplate.findAndModify(new Query(Criteria.where("name").is(tname)), new Update().inc("id", 1), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class, collection + EntityMeta.ENTITY_INC_SUFFIX);
        return map;
    }

    public Long getId(String collection) {
        Map map = mongoTemplate.findAndModify(new Query(Criteria.where("name").is(collection)), new Update().inc("id", 1), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class, collection +EntityMeta.ENTITY_INC_SUFFIX);
        return Long.parseLong(map.get("id") + "");
    }

    
    /**
     *  ????????????
     *  ????????????id ???????????????
     * @param id    ??????id
     * @param tname ?????????
     * @param field ??????
     * @return
     */
    public Long entityInc(long id, String tname, String field, int state) {
        tname = "entity_" + tname;
        Map map = mongoTemplate.findAndModify(new Query(Criteria.where("id").is(id)), new Update().set("id", id).inc(field, state), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class, tname);
        return Long.parseLong(map.get(field) + "");
    }

    
    /**
     * ???????????????????????????
     * ?????????,?????????,????????????
     * ????????????,??????push,??????????????????
     * @return
     */
    public GlobalConfig getGlobalConfig() {
		return new CacheableTemplate<GlobalConfig>(cacheProvider) {
			@Override
			protected GlobalConfig getFromRepository() {
				try {
			    	GlobalConfig map = getGlobalConfigAll();
			    	return map;
				} catch (Exception e) {
					Map map=new HashMap();
		    		map.put("method", "BaseService.getGlobalConfig");
		    		new LogException(e,map);
					return null;
				}
			}
			@Override
			protected boolean canPutToCache(GlobalConfig returnValue) {
				return (returnValue != null);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_REGISTERNUM_DAY_PREFIX+"")
						),
				true);
    }
    
    /**
     * push?????????????????????
     * ????????????
     * @param values: ????????????kye???value
     * @return
     */
	public GlobalConfig putGlobalConfig(Map<String, Object> values) {
		Update update = new Update().set("name", "global");
		Iterator<Map.Entry<String, Object>> it = values.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			update.set(entry.getKey(), entry.getValue());
		}
		GlobalConfig cfig = mongoTemplate.findAndModify(new Query(Criteria.where("name").is("global")), update,
				new FindAndModifyOptions().returnNew(true).upsert(true), GlobalConfig.class, EntityMeta.GLOBAL_TABLE);
		new CacheableTemplate<GlobalConfig>(cacheProvider) {
			@Override
			protected GlobalConfig getFromRepository() {
				return null;
			}

			@Override
			protected boolean canPutToCache(GlobalConfig returnValue) {
				return (returnValue != null);
			}
		}.push(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_REGISTERNUM_DAY_PREFIX + "")), cfig);
		return cfig;
	}
    
	/**
	 * ?????????????????? ++
	 * @param key
	 * @param val
	 * @return
	 */
	public GlobalConfig putGlobalConfigInc(String key, Integer val) {
		Update update = new Update().set("name", "global").inc(key, val);
		GlobalConfig cfig = mongoTemplate.findAndModify(new Query(Criteria.where("name").is("global")), update,
				new FindAndModifyOptions().returnNew(true).upsert(true), GlobalConfig.class, EntityMeta.GLOBAL_TABLE);
		return cfig;
	}
	
	
	public GlobalConfig getGlobalConfigAll() {
		GlobalConfig cfig = mongoTemplate.findOne(new Query(Criteria.where("name").is("global")),GlobalConfig.class, EntityMeta.GLOBAL_TABLE);
		return cfig;
	}



    /**
     * ???????????? 
     *  ?????????id
     * @param id    ??????id
     * @param tname ?????????
     * @param field ??????
     * @return
     */
    public void objectIncById(long id, String tname, String field, int state) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), new Update().inc(field, state).set("vistTime", new Date().getTime()/1000), HashMap.class, tname);
    }


 
 
    
    /**
     * ??????????????????
     * @param ids  //??????????????????
     * @return
     */
    public ReturnData hidden(String tname, List<Long> ids) {
    	WriteResult wr=mongoTemplate.updateMulti(new Query(Criteria.where("id").in(ids)), new Update().set("hidden", 1), tname);
    	return ReturnData.SUCCESS;
    }
    /**
     * ??????????????????
     * @param id  //??????????????????
     * @return
     */
    public ReturnData hidden(String tname, long id) {
    	WriteResult wr=mongoTemplate.updateMulti(new Query(Criteria.where("id").in(id)), new Update().set("hidden", 1), tname);
    	return ReturnData.SUCCESS;
    }


    /**
     * ??????????????????id
     *
     * @param tname  ????????????
     * @param entity
     */
    public void save(String tname, MongoBase entity) {
        if (entity.getId() == null) {
            entity.setId(this.getId(tname));
        }
        entity.setUpdateStamp(new Date().getTime()/1000);
        mongoTemplate.save(entity, tname);
    }
    
    
    /**
     * ??????????????????
     * @param query
     */
    public void setQueryFeilds(Query query, String... fields) {
		if(fields!=null) {
			for(int i=0;i<fields.length;i++) {
				query.fields().include(fields[i]);
			}
		}

    }

    /*
	 *??????????????????
	 */
	public  void  createImageSrc(String appName, List datas, String...imgFields) {
		if(imgFields!=null&&imgFields.length>0) {
			for(int i=0;i<datas.size();i++) {
				Map map=(Map)datas.get(i);
				for(String f:imgFields) {
					if(map.get(f)!=null&&!StringUtils.isBlank(map.get(f)+"")) {
						map.put(f+"Src", CommonUtils.getImageSrc(appName,map.get(f)+""));
					}
				}
			}
		}
	}	
    
	/**
	 * ?????????????????????
	 * @param key
	 * @return
	 */
	/*public String getPro(String key) {
		 try {
			String val=propertiesFactoryBean.getObject().getProperty(key);
			if(StringUtils.isBlank(val)) return "";
			return val;
		} catch (IOException e) {
			Map map=new HashMap();
    		map.put("method", "BaseService.getPro");
    		map.put("key", key);
    		new LogException(e,map);
		}
		 return "";
	}*/

	/**
	 * ??????????????????
	 * @param clazz
	 * @param id
	 * @param sort
	 * @param sortField
	 * @param prime
	 * @return
	 */
	public ReturnData ManualSetSort(Class clazz, Integer id, Integer sort, String sortField, String prime){
		try {
			String[] validSortFields = {"sort", "sort2"};
			String[] clearOrignFields = {"sort"};
			if (ArrayUtils.contains(validSortFields, sortField)) {
				if(ArrayUtils.contains(clearOrignFields, sortField)) {
					//?????????sort
					mongoTemplate.findAndModify(new Query(Criteria.where(sortField).is(sort)), Update.update(sortField, null), clazz);
				}
				//?????????sort
				mongoTemplate.findAndModify(new Query(Criteria.where(prime).is(id)), Update.update(sortField, sort), clazz);
				return ReturnData.SUCCESS;
			} else {
				return new ReturnData(-1, "?????????sortField???");
			}
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "BaseService.ManualSetSort");
			map.put("table", clazz);
			map.put("id", id);
			map.put("sort", sort);
			map.put("sortField", sortField);
			new LogException(e,map);
			return ReturnData.ERROR;
		}
	}

	/**
	 * ??????????????????
	 * @param clazz
	 * @param id
	 * @param sort
	 * @param sortField
	 * @return
	 */
	public ReturnData ManualSetSort(Class clazz, Integer id, Integer sort, String sortField){
		return ManualSetSort(clazz, id, sort, sortField, "id");
	}
	
	/**
     * ??????????????????????????????????????????
     * @param account
     */
    public ReturnData getAccountVcodeState(String account) {
    	//??????type??? 0?????? 1??????????????? 2????????????
        Map map=mongoTemplate.findOne(new Query(Criteria.where("account").is(account)).with(new Sort(Direction.DESC,"updateStamp")),HashMap.class,"vcode_state_account");
        int type=0;
        //int maxNum=3;
        int maxNum = ConfUtils.getResourceNum("day_vcode_maxNum");

        if(null!=map){
        	if(null!=map.get("hidden")&&1== Integer.parseInt(map.get("hidden")+"")){
        		//???????????????????????????
        		type=2;
        	} else if(null!=map.get("total")&&maxNum< Integer.parseInt(map.get("total")+"")){
        		//System.out.println(CommonUtils.curDayMaxAndMinTime(1));
        		if(CommonUtils.curDayMaxAndMinTime(1)< Integer.parseInt(map.get("updateStamp")+"")&& Integer.parseInt(map.get("updateStamp")+"")<CommonUtils.curDayMaxAndMinTime(0)){
        			//??????????????????????????????????????????
        			type=1;
        		}
        	}
        }
        
        if(type==0){
        	return new ReturnData();
        } else if(type==1){
        	return UserInfo.IMAGE_VALID_CODE;
        } else if(type==2){
        	return ReturnData.OPERATION_NOT_PERMIT;
        }
        
        return new ReturnData();
    }
    
	
	/**
	 * ??????3.2??????????????????????????????????????????
	 * @param userInfo
	 * @return
	 */
	public ReturnData oldVerifyState(UserInfo userInfo){
		int authentication = ConfUtils.getResourceNum("user_authentication_old_send");
		Boolean verifyState=userInfo.getVerifyState();
		if(verifyState==null && authentication==0){
			String msg="?????????app,??????????????????????????????";
			int ret=-6;
			return new ReturnData(ret,msg);
		}
		return ReturnData.SUCCESS;
	}
	
	/**
	 * v3.2
	 * ?????????????????????
	 * @param userInfo
	 * @return
	 */
	public ReturnData verifyState(UserInfo userInfo){
		Boolean verifyState=userInfo.getVerifyState();
		if(verifyState==null){
			String msg="??????????????????????????????";
			int ret=-6;
			
			Map<String,String> aMap = ConfUtils.getJSONMap("user_authentication");
			int open= Integer.parseInt(aMap.get("open"));
			int mandatory= Integer.parseInt(aMap.get("mandatory"));
			if(0==open && 0==mandatory){
				return new ReturnData(ret,msg);
			}
		}
		return ReturnData.SUCCESS;
	}
	

	
	  /**
		 * 3.2????????????????????????,??????????????????????????????
		 * @param userInfo
		 * @return
		 */
	protected ReturnData switchOfPhoneCheck(UserInfo userInfo) {
		long id = userInfo.getId();
		String phone=null;
		if (id != 0) {
			phone =userInfo.getPhone();
			if (StringUtils.isBlank(phone)) {
				String msg = "?????????????????????????????????";
				int ret = -8;
				Map<String,String> aMap = ConfUtils.getJSONMap("user_switchOfPhone");
				int open= Integer.parseInt(aMap.get("open"));
				int mandatory= Integer.parseInt(aMap.get("mandatory"));
				if(0==open && 0==mandatory){
					return new ReturnData(ret,msg);
					
				}
			}
		}
	
	
		return ReturnData.SUCCESS;
	}
	
	  /**
		 * 3.2?????????????????????
		 * @param userInfo
		 * @return
		 */
	protected ReturnData oldSwitchOfphoneCheck(UserInfo userInfo) {

		int SwitchOfPhone = ConfUtils.getResourceNum("user_switchOfPhone_old_send");
		long id = userInfo.getId();
		String phone=null;
		if (id != 0) {
			phone =userInfo.getPhone();
			if (StringUtil.isBlank(phone)&& SwitchOfPhone == 0) {
				String msg = "?????????app,?????????????????????????????????";
				int ret = -8;
				Map<String,String> aMap = ConfUtils.getJSONMap("user_switchOfPhone");
				int open= Integer.parseInt(aMap.get("open"));
				if(0==open){
					return new ReturnData(ret,msg);
				}
			}
		}
		return ReturnData.SUCCESS;
	}
	
	/**
	 * push redis??????
	 * @param object: ???????????????
	 * @param cacheType: ???????????????
	 * @param cacheKeyParams: ??????key?????????
	 */
	public void pushObjectToRedisCache(Object object, String cacheType, String... cacheKeyParams){
		new CacheableTemplate<Object>(cacheProvider) {
			@Override
			protected Object getFromRepository() {
				return null;
			}
			
			@Override
			protected boolean canPutToCache(Object returnValue) {
				return (returnValue != null);
			}
		}.push(new CacheKey(CACHE_NAME.NAMESPACE, cacheType,
				CACHE_NAME.createInstanceKey(cacheKeyParams)),object);
	}
	
	public void compareGoodsMids(List<Map> listMap){
		for(Map map:listMap){
			Map param=(Map)map.get("param");
			if(null!=param.get("mids")){
				List<Goods> goodsList=goodsOldMapper.getGoodsByIds(String.valueOf(param.get("mids")));
				StringBuffer mids=new StringBuffer();
				for(Goods g:goodsList){
					mids.append(g.getMid()).append(",");
				}
				if(StringUtils.isNotBlank(mids+"")){
					String strMids=mids.substring(0,mids.length()-1);
					param.put("mids", strMids);
				}
			}
		}
	}

}
