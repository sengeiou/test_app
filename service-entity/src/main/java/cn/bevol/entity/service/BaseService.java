package cn.bevol.entity.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;
import com.mongodb.WriteResult;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.conf.client.ConfUtils;
import cn.bevol.constant.api.EntityMeta;
import cn.bevol.model.Base;
import cn.bevol.model.GlobalConfig;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.entityAction.EntityActionBase;
import cn.bevol.model.user.UserInfo;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.dao.UserInfoMapper;
import cn.bevol.mybatis.model.Goods;
import cn.bevol.entity.service.utils.CommonUtils;
import cn.bevol.util.ReturnData;

@Service
public class BaseService {
	
    @Autowired
	RedisCacheProvider cacheProvider;
    
    @Autowired
    public PropertiesFactoryBean propertiesFactoryBean;

    
	private static Logger logger = LoggerFactory.getLogger(BaseService.class);
	public static Configuration config =null;
	static{
		try {
			config= new PropertiesConfiguration("bevol-dp.ini");
		} catch (ConfigurationException e) {
			Map map=new HashMap();
    		map.put("method", "BaseService.bevol-dp.ini");
    		new cn.bevol.log.LogException(e,map);
		}
	}

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    private static Map<String, String> kivu = new HashMap<String, String>();
    private static Map<String, String> kuvi = new HashMap<String, String>();
    //cookies信息管理
    private static Map<String, String> userviu = new HashMap<String, String>();
    private static Map<String, UserInfo> users = new HashMap<String, UserInfo>();


    /**
     * 根据资源类型获得的资源
     * @param tname
     * @param id
     * @return  没有返回null
     */
    public EntityBase getEntityById(String tname, long id) {
        String entityTname = "entity_" + tname;
        EntityBase eb=   mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), EntityBase.class, entityTname);
       
        //更新无英文名称的产品
        if(eb!=null&&eb.getId()>0&& StringUtils.isBlank(eb.getAlias())&&tname.equals("goods")) {
    		Goods goods=goodsMapper.getById(eb.getId());
        	mongoTemplate.findAndModify(new Query(new Criteria().where("id").is(eb.getId())), new Update().set("title", goods.getTitle()).set("alias", goods.getAlias()), EntityActionBase.class, entityTname);
        	eb.setTitle(goods.getTitle());
        	eb.setAlias(goods.getAlias());
        }
        return eb;
        
    }
    
    
    /**
     * 获取唯一id
     */
    public Long getUniqueId() {
        Map map = mongoTemplate.findAndModify(new Query(Criteria.where("name").is("unique_id")), new Update().inc("id", 1), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class,  EntityMeta.UNIQUE_ID);
        if(map==null||map.get("name")==null) return null;
        Long id=Long.parseLong(map.get("id")+"");
        return id;
        
    }
    


    
    /**
     * 根据资源类型获得的资源
     * @param id
     * @param id
     * @return  没有返回null
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
     *  实体统计
     *  如果没有id 新建立一条
     * @param id    实体id
     * @param tname 表名称
     * @param field 字段
     * @return
     */
    public Long entityInc(long id, String tname, String field, int state) {
        tname = "entity_" + tname;
        Map map = mongoTemplate.findAndModify(new Query(Criteria.where("id").is(id)), new Update().set("id", id).inc(field, state), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class, tname);
        return Long.parseLong(map.get(field) + "");
    }

    
    /**
     * 获取全局的配置信息
     * 产品数,注册数,成分数等
     * 永久缓存,定时push,更新缓存内容
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
		    		new cn.bevol.log.LogException(e,map);
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
     * push全局的配置信息
     * 缓存更新
     * @param values: 要更新的kye和value
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
	 * 全局访问设置 ++
	 * @param key
	 * @param val
	 * @return
	 */
	public GlobalConfig putGlobalConfigInc(String key,Integer val) {
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
     * 实体统计 
     *  必须有id
     * @param id    实体id
     * @param tname 表名称
     * @param field 字段
     * @return
     */
    public void objectIncById(long id, String tname, String field, int state) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), new Update().inc(field, state).set("vistTime", new Date().getTime()/1000), HashMap.class, tname);
    }


 
 
    
    /**
     * 隐藏用户消息
     * @param ids  //删除用户消息
     * @return
     */
    public ReturnData hidden(String tname,List<Long> ids) {
    	WriteResult wr=mongoTemplate.updateMulti(new Query(Criteria.where("id").in(ids)), new Update().set("hidden", 1), tname);
    	return ReturnData.SUCCESS;
    }
    /**
     * 隐藏用户消息
     * @param id  //删除用户消息
     * @return
     */
    public ReturnData hidden(String tname,long id) {
    	WriteResult wr=mongoTemplate.updateMulti(new Query(Criteria.where("id").in(id)), new Update().set("hidden", 1), tname);
    	return ReturnData.SUCCESS;
    }


    /**
     * 保存对象生成id
     *
     * @param tname  对象名称
     * @param entity
     */
    public void save(String tname, Base entity) {
        if (entity.getId() == null) {
            entity.setId(this.getId(tname));
        }
        entity.setUpdateStamp(new Date().getTime()/1000);
        mongoTemplate.save(entity, tname);
    }
    
    
    /**
     * 设置查询字段
     * @param query
     */
    public void setQueryFeilds(Query query,String... fields) {
		if(fields!=null) {
			for(int i=0;i<fields.length;i++) {
				query.fields().include(fields[i]);
			}
		}

    }

    /*
	 *添加图片前缀
	 */
	public  void  createImageSrc(String appName,List datas,String ...imgFields) {
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
	 * 获取环境的配置
	 * @param key
	 * @return
	 */
	public String getPro(String key) {
		 try {
			String val=propertiesFactoryBean.getObject().getProperty(key);
			if(StringUtils.isBlank(val)) return "";
			return val;
		} catch (IOException e) {
			Map map=new HashMap();
    		map.put("method", "BaseService.getPro");
    		map.put("key", key);
    		new cn.bevol.log.LogException(e,map);
		}
		 return "";
	}

	/**
	 * 人工设置排序
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
					//清除原sort
					mongoTemplate.findAndModify(new Query(Criteria.where(sortField).is(sort)), Update.update(sortField, null), clazz);
				}
				//添加新sort
				mongoTemplate.findAndModify(new Query(Criteria.where(prime).is(id)), Update.update(sortField, sort), clazz);
				return ReturnData.SUCCESS;
			} else {
				return new ReturnData(-1, "错误的sortField值");
			}
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "BaseService.ManualSetSort");
			map.put("table", clazz);
			map.put("id", id);
			map.put("sort", sort);
			map.put("sortField", sortField);
			new cn.bevol.log.LogException(e,map);
			return ReturnData.ERROR;
		}
	}

	/**
	 * 人工设置排序
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
     * 查询用户当天的短信验证码情况
     * @param id
     * @param tname
     * @param field
     * @param state
     */
    public ReturnData getAccountVcodeState(String account) {
    	//返回type值 0正常 1超过定义值 2手动封禁
        Map map=mongoTemplate.findOne(new Query(Criteria.where("account").is(account)).with(new Sort(Direction.DESC,"updateStamp")),HashMap.class,"vcode_state_account");
        int type=0;
        //int maxNum=3;
        int maxNum = ConfUtils.getResourceNum("day_vcode_maxNum");

        if(null!=map){
        	if(null!=map.get("hidden")&&1==Integer.parseInt(map.get("hidden")+"")){
        		//手机号被手动黑名单
        		type=2;
        	} else if(null!=map.get("total")&&maxNum<Integer.parseInt(map.get("total")+"")){
        		//System.out.println(CommonUtils.curDayMaxAndMinTime(1));
        		if(CommonUtils.curDayMaxAndMinTime(1)<Integer.parseInt(map.get("updateStamp")+"")&&Integer.parseInt(map.get("updateStamp")+"")<CommonUtils.curDayMaxAndMinTime(0)){
        			//当天时间内的数值大于定义的值
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
	 * 用于3.2之前版本接口的实名认证的验证
	 * @param userInfo
	 * @return
	 */
	public ReturnData oldVerifyState(UserInfo userInfo){
		int authentication = ConfUtils.getResourceNum("user_authentication_old_send");
		Boolean verifyState=userInfo.getVerifyState();
		if(verifyState==null && authentication==0){
			String msg="请升级app,实名认证之后才能发表";
			int ret=-6;
			return new ReturnData(ret,msg);
		}
		return ReturnData.SUCCESS;
	}
	
	/**
	 * v3.2
	 * 实名认证的验证
	 * @param userInfo
	 * @return
	 */
	public ReturnData verifyState(UserInfo userInfo){
		Boolean verifyState=userInfo.getVerifyState();
		if(verifyState==null){
			String msg="实名认证之后才能发表";
			int ret=-6;
			
			Map<String,String> aMap = ConfUtils.getJSONMap("user_authentication");
			int open=Integer.parseInt(aMap.get("open"));
			int mandatory=Integer.parseInt(aMap.get("mandatory"));
			if(0==open && 0==mandatory){
				return new ReturnData(ret,msg);
			}
		}
		return ReturnData.SUCCESS;
	}
	

	
	  /**
		 * 3.2新接口的手机认证,包含是否强制实名认证
		 * @param msg
		 * @param ret
		 * @return
		 */
	protected ReturnData switchOfPhoneCheck(UserInfo userInfo) {
		long id = userInfo.getId();
		String phone=null;
		if (id != 0) {
			phone =userInfo.getPhone();
			if (StringUtil.isBlank(phone)) {
				String msg = "手机号认证之后才能发表";
				int ret = -8;
				Map<String,String> aMap = ConfUtils.getJSONMap("user_switchOfPhone");
				int open=Integer.parseInt(aMap.get("open"));
				int mandatory=Integer.parseInt(aMap.get("mandatory"));
				if(0==open && 0==mandatory){
					return new ReturnData(ret,msg);
					
				}
			}
		}
	
	
		return ReturnData.SUCCESS;
	}
	
	  /**
		 * 3.2以前的手机认证
		 * @param msg
		 * @param ret
		 * @return
		 */
	protected ReturnData oldSwitchOfphoneCheck(UserInfo userInfo) {

		int SwitchOfPhone = ConfUtils.getResourceNum("user_switchOfPhone_old_send");
		long id = userInfo.getId();
		String phone=null;
		if (id != 0) {
			phone =userInfo.getPhone();
			if (StringUtil.isBlank(phone)&& SwitchOfPhone == 0) {
				String msg = "请升级app,手机号认证之后才能发表";
				int ret = -8;
				Map<String,String> aMap = ConfUtils.getJSONMap("user_switchOfPhone");
				int open=Integer.parseInt(aMap.get("open"));
				if(0==open){
					return new ReturnData(ret,msg);
				}
			}
		}
		return ReturnData.SUCCESS;
	}
	
	/**
	 * push redis缓存
	 * @param object: 缓存的对象
	 * @param cacheType: 缓存的时间
	 * @param cacheKeyParams: 缓存key的参数
	 */
	public void pushObjectToRedisCache(Object object,String cacheType,String... cacheKeyParams){
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
				List<Goods> goodsList=goodsMapper.getGoodsByIds(String.valueOf(param.get("mids")));
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
