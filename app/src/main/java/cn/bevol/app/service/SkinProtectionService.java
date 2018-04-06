package cn.bevol.app.service;

import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.app.entity.metadata.EntityInfo;
import cn.bevol.model.metadata.UserGoodsCategory;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.user.UserSkinProtection;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.response.ReturnData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkinProtectionService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
	RedisCacheProvider cacheProvider;

    @Autowired
    EntityService entityService;

    @Autowired
    MessageService messageService;


    @Autowired
	AliyunService aliyunService;
    
	 @Autowired
	 GoodsOldMapper goodsMapper;
	 
	    @Autowired
	    UserService userService;


    private static Logger logger = LoggerFactory.getLogger(SkinProtectionService.class);
     /**
     * 添加或者修改 肤质分类
     * @param id
     * @param category_name
     * @return
     */
	public ReturnData addOrUpdateSkinProtectionCategory(Long id, Long pid, String category_name, Long userId) {
		try{	
			String actionType="user_goods_category";
			if(StringUtils.isNotBlank(category_name)) {
				//验证唯一性
				Query query=null;
				if(pid==null||pid==0) {
					query=new Query(Criteria.where("userId").is(userId).and("pid").is(0).and("categoryName").is(category_name.trim()));
				} else {
					query=new Query(Criteria.where("userId").is(userId).and("pid").is(pid).and("categoryName").is(category_name.trim()));
				}
				UserGoodsCategory uai = mongoTemplate.findOne(query, UserGoodsCategory.class, actionType);
				
				//唯一验证
				if(uai!=null) {
					if(uai.getId()!=id.longValue()) {
						return new ReturnData(-4,"分类存在");
					}
				}
				
				if(id!=null&&id>0) {
					if(uai!=null&&uai.getSrcCategoryId()!=null&&uai.getSrcCategoryId()>0) {
						return new ReturnData(1,"默认分类不能修改");
					}
						//可以修改
						Update update = new Update();
						update.set("categoryName", category_name.trim()).set("updateStamp", new Date().getTime()/1000);
						mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id).and("userId").is(userId)), update, actionType);
				}else{
					
 					//添加护肤方案 
					return addSkinProtectionCategory(id,pid,category_name,userId);
 				}
			}
			
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			logger.error("method:UserPartService.addOrUpdateSkinProtectionCategory arg:{id:" + id + ",category_name:" + category_name + ",userId:"+userId+"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnData.ERROR;
		}

	}
	
	/**
	 * 添加护肤方案 
	 * @param id
	 * @param pid
	 * @param category_name
	 * @param userId
	 * @return
	 */
	public ReturnData addSkinProtectionCategory(Long id, Long pid, String category_name, Long userId) {
			try {
				String actionType="user_goods_category";
				long count=0L;
				if(null==pid||pid==0) {
					count=mongoTemplate.count(new Query(Criteria.where("userId").is(userId).and("pid").is(0)), UserGoodsCategory.class, actionType);
				} else {
					
					//添加子分类
					long c=mongoTemplate.count(new Query(Criteria.where("id").is(pid).and("userId").is(userId)), UserGoodsCategory.class, actionType);
					if(c>0) {
						count=mongoTemplate.count(new Query(Criteria.where("userId").is(userId).and("pid").is(pid)), UserGoodsCategory.class, actionType);
					} else {
						return new ReturnData(-4,"肤质方案不存在");
					}
				}
				if(count>=10) {
					return new ReturnData(-4,"分类不能超过10个");
				}
				
				
				//id自增
				id=this.getId("user_goods_category");
				UserGoodsCategory ugc=new UserGoodsCategory();
				ugc.setCategoryName(category_name);
				ugc.setUserId(userId);
				ugc.setPid(pid);
				ugc.setId(id);
				ugc.setSort(new Date().getTime()/1000);
				mongoTemplate.save(ugc, actionType);
				//初始化子分类
				if(pid==null||pid==0) {
					getSubCategory(userId,id);
				} else {
					//添加子分类 更新护肤方案时间
					updateSkinProtection(pid,userId);
				}
				return new ReturnData(ugc);
			}catch(Exception e) {
				logger.error("method:UserPartService.addSkinProtectionCategory arg:{id:" + id + ",category_name:" + category_name + ",userId:"+userId+"}"
						+ "   desc:" + ExceptionUtils.getStackTrace(e));
				return ReturnData.ERROR;
			}
	
	}
 	
	
	public List<UserGoodsCategory> getUserGoodsCategorys(Long userId) {
		String actionType="user_goods_category";
		Long n=new Date().getTime()/1000;
		//获取所有方案
		List<UserGoodsCategory> ugcs=this.mongoTemplate.find(new Query(Criteria.where("userId").is(userId).and("pid").is(0)).with(new Sort(Direction.ASC, "sort")), UserGoodsCategory.class, actionType);
		
		long curTime=new Date().getTime()/1000;
		if(null!=ugcs){
			for(UserGoodsCategory ugc:ugcs){
				Query query=new Query(Criteria.where("categoryPid").is(ugc.getId()).and("expireTime").lte(curTime).and("expire").is(true));
				query.fields().include("id");
				UserSkinProtection u = mongoTemplate.findOne(query.limit(1), UserSkinProtection.class, "user_skin_protection");
				if(null!=u){
					ugc.setExprieGoods(true);
				}
			}
		}

		
		//默认的基础方案
		String value="[ { \"id\": 10, \"type\": 1, \"base\":true, \"name\": \"基础方案\" } ]";
		JSONArray ja=JSONArray.fromObject(value);
		if(ugcs==null || ugcs.size()==0) {
			//没有方案时,初始化基础方案
			ugcs=new ArrayList<UserGoodsCategory>();
			//默认初始化
			for(int i=0;i<ja.size();i++) {
				JSONObject json=ja.getJSONObject(i);
				Long id=this.getId("user_goods_category");
				UserGoodsCategory userGoodsCategory=new UserGoodsCategory();
				userGoodsCategory.setSrcCategoryId(json.getLong("id"));
				userGoodsCategory.setCategoryName(json.getString("name"));
				userGoodsCategory.setSrcCategoryName(json.getString("name"));
				userGoodsCategory.setType(json.getInt("type"));
				userGoodsCategory.setBase(json.getBoolean("base"));
				userGoodsCategory.setId(id);
				userGoodsCategory.setUserId(userId);
				userGoodsCategory.setSort(n++);
				ugcs.add(userGoodsCategory);
				mongoTemplate.save(userGoodsCategory, actionType);
			}
		}
		return ugcs;
}
	
	
	/**
	 * 获取原始分类
	 * @param userId
	 */
	public List<UserGoodsCategory> getSubCategory(Long userId, Long categoryPid) {
		//初始化分类
		

			//默认
			String actionType="user_goods_category";
			Long n=new Date().getTime()/1000;
			
			//获取所有分类
			List<UserGoodsCategory> ugcs=this.mongoTemplate.find(new Query(Criteria.where("userId").is(userId).and("pid").is(categoryPid)).limit(20), UserGoodsCategory.class, actionType);
			String value="[ { \"id\": 6, \"type\": 1, \"name\": \"洁面\" }, { \"id\": 7, \"type\": 1, \"name\": \"化妆水\" }, { \"id\": 9, \"type\": 1, \"name\": \"精华\" }, {\"id\": 8, \"type\": 1, \"name\": \"乳霜\" }, { \"id\": -1, \"type\": 2, \"name\": \"其他\" } ]";
			JSONArray ja=JSONArray.fromObject(value);
			if(ugcs==null || ugcs.size()==0) {
				ugcs=new ArrayList<UserGoodsCategory>();
				//默认初始化
				for(int i=0;i<ja.size();i++) {
					JSONObject json=ja.getJSONObject(i);
					Long id=this.getId("user_goods_category");
					UserGoodsCategory userGoodsCategory=new UserGoodsCategory();
					userGoodsCategory.setSrcCategoryId(json.getLong("id"));
					userGoodsCategory.setCategoryName(json.getString("name"));
					userGoodsCategory.setSrcCategoryName(json.getString("name"));
					userGoodsCategory.setType(json.getInt("type"));
					userGoodsCategory.setId(id);
					userGoodsCategory.setUserId(userId);
					userGoodsCategory.setPid(categoryPid);
					userGoodsCategory.setSort(n++);
					mongoTemplate.save(userGoodsCategory, actionType);
					ugcs.add(userGoodsCategory);
				}
			} 
			return ugcs;
	}

	
	/**
	 * 提供分享的的
	 * @param category_pid
	 * @return
	 */
	public ReturnData findCommonSkinProtectionGoods(Long userId, Long category_pid, long startId, int pageSize) {
		//	m.put("total", count);
		ReturnData<Map> rd= findSkinProtectionGoods(userId,category_pid,startId,pageSize);
		Map m=rd.TResult();
		UserGoodsCategory cuy=this.mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("id").is(category_pid).and("pid").is(0)), UserGoodsCategory.class, "user_goods_category");
		m.put("category", cuy);
		 ReturnData<UserInfo>  ui=userService.getUserById(userId);
		m.put("userinfo", ui.getResult());
		return rd;
	}



	/**
	 * 查询产品分类
	 * @param category_pid
	 * @return
	 */
	public ReturnData findSkinProtectionGoods(Long userId, Long category_pid, long startId, int pageSize) {
		String actionType="user_skin_protection";
		Criteria crt= Criteria.where("categoryPid").is(category_pid).and("userId").is(userId);
		Query query = Query.query(crt).limit(pageSize).with(new Sort(Direction.ASC, "id"));
        if (startId > 0) {
            crt.and("id").lt(startId);
        }
        //方案下的所有实体信息
		List<UserSkinProtection> uais = mongoTemplate.find(query, UserSkinProtection.class, actionType);
		//long count = mongoTemplate.count(Query.query(crt), actionType);
		Map m=new HashMap();
		//产品列表
		m.put("list", uais);
		
		//方案是否存在
		UserGoodsCategory cuy=this.mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("id").is(category_pid).and("pid").is(0)), UserGoodsCategory.class, "user_goods_category");
		if(cuy==null) {
			return new ReturnData(-2,"分类不存在");
		}
		//分类列表
		m.put("subCategory", getSubCategory(userId,category_pid));
		
		//查找过期产品
		if(null!=uais && uais.size()>0){
			long pid=uais.get(0).getCategoryPid();
			//方案下的所有实体信息
			List<Long> ids=new ArrayList<Long>();
			for(int i=0;i<uais.size();i++){
				UserSkinProtection uai=uais.get(i);
				if(null!=uai.getOpen()&& 1==uai.getOpen() && null!=uai.getRemainingDays() && uai.getRemainingDays()==0&&null!=uai.getExpire()&&uai.getExpire()){
					//给过期产品做标记(已经查看过)
					ids.add(uai.getId());
				}
			}
			if(ids.size()>0){
				mongoTemplate.updateMulti(new Query(Criteria.where("id").in(ids)), new Update().set("expire", false), UserSkinProtection.class,actionType);
			}
		}
		//user_goods_category添加是否过期和被点击的字段
		//方案设置为没有过期产品
		//mongoTemplate.updateFirst(new Query(Criteria.where("id").is(category_pid)), new Update().set("exprieGoods", false), UserGoodsCategory.class);

		return new ReturnData<Map>(m);
	}

	public ReturnData deleteSkinProtectionGoods(Long id, Long userId) {
		try {
			String actionType="user_skin_protection";
			mongoTemplate.remove(new Query(Criteria.where("id").is(id).and("userId").is(userId)), actionType);
			
			
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			logger.error("method:UserPartService.deleteSkinProtectionGoods arg:{id:" + id +"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			
		}
		return ReturnData.ERROR;
	}

	
	/**
	 * 直接添加产品
	 * @param entityId
	 * @param userInfo
	 * @return
	 */
	public ReturnData addSkinProtectionGoods(Long entityId, UserInfo userInfo, Integer open, Long openTime, Integer releaseDate, Integer usedType) {
		Long userId=0L;
		try{
			String actionType="user_skin_protection";
			UserGoodsCategory ugcy=null;
			//从产品详细页面中添加
			if(entityId!=null&&entityId>0) {
				userId=userInfo.getId();
				userInfo=userService.getMongoUserInfo(userId);
				//添加至系统分类
			//	return new ReturnData("请选择分类");
				//找到基础方案
				UserGoodsCategory baseCty = mongoTemplate.findOne(new Query(Criteria.where("base").is(true).and("pid").is(0).and("userId").is(userId)), UserGoodsCategory.class, "user_goods_category");

				//
				UserSkinProtection uai = mongoTemplate.findOne(new Query(Criteria.where("entityId").is(entityId).and("categoryPid").is(baseCty.getId()).and("userId").is(userId)), UserSkinProtection.class, actionType);
				Goods goods=goodsMapper.getById(entityId);
				if(goods==null) return new ReturnData("产品不存在");
				
				if(uai!=null) {
					return new ReturnData(1,"已经添加过此产品");
				} else {
					//mongo的数据
 					Goods eb=null;
					if(entityId!=null&&entityId>0) {
						eb=goodsMapper.getById(entityId);
						if(eb==null) return new ReturnData("产品不存在");
					}
					
					//对应 默认添加的分类
					UserGoodsCategory other = mongoTemplate.findOne(new Query(Criteria.where("pid").is(baseCty.getId()).and("srcCategoryId").is(eb.getCategory()).and("userId").is(userId)), UserGoodsCategory.class, "user_goods_category");
					if(other==null)
						other = mongoTemplate.findOne(new Query(Criteria.where("pid").is(baseCty.getId()).and("type").is(2).and("userId").is(userId)), UserGoodsCategory.class, "user_goods_category");


					UserSkinProtection usp=new UserSkinProtection();
					usp.setId(this.getId(actionType));
					usp.setEntityId(entityId);
					usp.setEntityName("goods");
					usp.setUserId(userId);
					usp.setCategoryPid(other.getPid());
					usp.setCategoryId(other.getId());
					
					usp.setOpen(open);
					usp.setOpenTime(openTime);
					usp.setUsedType(usedType);
					usp.setReleaseDate(releaseDate);
					EntityInfo entityInfo=new EntityInfo();
					entityInfo.setEntiyInfo(eb);
					usp.setEntityInfo(entityInfo);
					mongoTemplate.save(usp,actionType);
					updateSkinProtection(other.getPid(),userId);
					
					return new ReturnData(usp);
				}
			}
			
			}   catch(Exception e) {
				logger.error("method:UserPartService.addSkinProtectionGoods arg:{userId:" + entityId + ",userId:" + userId + "}"
						+ "   desc:" + ExceptionUtils.getStackTrace(e));

			}
			return ReturnData.ERROR;
	}
	
	/**
	 * 根据分类添加产品
	 * @param entityId
	 * @param categoryId
	 * @param categoryPid
	 * @param userInfo
	 * @return
	 */
	public ReturnData addSkinProtectionGoods(Long entityId, Long categoryId, Long categoryPid, UserInfo userInfo, Integer open, Long openTime, Integer releaseDate, Integer usedType) {
		Long userId=0L;
		try{
			userId=userInfo.getId();
			String actionType="user_skin_protection";
			UserGoodsCategory  ugcy=this.mongoTemplate.findOne(new Query(Criteria.where("id").is(categoryId).and("pid").is(categoryPid).and("userId").is(userId)), UserGoodsCategory.class, "user_goods_category");
			if(ugcy==null) return new ReturnData(1,"分类信息有误 ");
			//从产品详细页面中添加
			if(entityId!=null&&entityId>0) {
				//添加至系统分类
			//	return new ReturnData("请选择分类");
				UserSkinProtection uai = mongoTemplate.findOne(new Query(Criteria.where("entityId").is(entityId).and("categoryPid").is(categoryPid).and("userId").is(userId)), UserSkinProtection.class, actionType);
				//重新获取产品
				EntityBase eb=null;
				if(entityId!=null&&entityId>0) {
					eb=this.getEntityById("goods", entityId);
					if(eb==null) return new ReturnData("产品不存在");
				}
				
				if(uai!=null) {
					return new ReturnData(1,"已经添加过此产品");
				} else {
					UserSkinProtection usp=new UserSkinProtection();
					usp.setId(this.getId(actionType));
					usp.setEntityId(entityId);
					usp.setEntityName("goods");
					usp.setUserId(userId);
					usp.setCategoryPid(categoryPid);
					usp.setCategoryId(categoryId);
					
					usp.setOpen(open);
					usp.setOpenTime(openTime);
					usp.setReleaseDate(releaseDate);
					usp.setUsedType(usedType);
					EntityInfo entityInfo=new EntityInfo();
					entityInfo.setEntiyInfo(eb);
					usp.setEntityInfo(entityInfo);
					if(null!=open && null!=releaseDate && null!=openTime){
						usp.setExpireTime(openTime+releaseDate*30*24*60*60);
					}
					
					mongoTemplate.save(usp,actionType);
					
					updateSkinProtection(categoryPid,userId);
					return new ReturnData(usp);
				}
			}
			}   catch(Exception e) {
				logger.error("method:UserPartService.addSkinProtectionGoods arg:{userId:" + entityId + ",userId:" + userId + ",categoryId:"+categoryId+",categoryPid:"+categoryPid+"}"
						+ "   desc:" + ExceptionUtils.getStackTrace(e));

			}
			return ReturnData.ERROR;
	}
	
	/*public void updateCategoryGoods(UserInfo userInfo,Long openTime,Integer releaseDate,Integer open,Long id){
		if(null!=open){
			Map<String,Object> expireMap=userInfo.getExpireSkinFlow();
			long minTime=Long.parseLong(String.valueOf(expireMap.get("expireTime")));
			long minId=Long.parseLong(String.valueOf(expireMap.get("id")));

			if(open==1&& null!=openTime && null!=releaseDate){
				//开封
				Long monTime=releaseDate*30*24*60*60L;
				Long expireTime=openTime+monTime;
				if(null==expireMap){
					expireMap=new HashMap<String,Object>();
					expireMap.put("expireTime", expireTime);
					expireMap.put("id",id);
				}else{
					if(expireTime<minTime){
						expireMap.put("expireTime", expireTime);
						expireMap.put("id",id);
					}
				}
				//当前添加的产品的结束时间为所有产品的最小结束时间
				//更新用户的最小结束时间,用于判断是否过期
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), new Update().set("expireSkinFlow", expireMap), UserInfo.class,"user_info");
			}else if(open==0){
				if(minId==id){
					//用户的最小结束时间改为了未开封状态
					
				}
				
			}
			
		}
		
		
	}*/
	
	
	
	private void updateSkinProtection(long id,long userId) {
		String actionType="user_goods_category";
		//更新肤质方案修改
		Update update = new Update();
		update.set("updateStamp", new Date().getTime()/1000);
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id).and("userId").is(userId)), update, actionType);

	}
	
	/**
	 * 用户自己添加的产品
	 * @param title
	 * @param image
	 * @param categoryId
	 * @param categoryPid
	 * @param userInfo
	 * @return
	 */
	public ReturnData addSkinProtectionGoods(String title, String image, Long categoryId, Long categoryPid, UserInfo userInfo, Integer open, Long openTime, Integer releaseDate, Integer usedType){
		Long userId=0L;
		try{
			userId=userInfo.getId();
			UserGoodsCategory  ugcy=this.mongoTemplate.findOne(new Query(Criteria.where("id").is(categoryId).and("pid").is(categoryPid).and("userId").is(userId)), UserGoodsCategory.class, "user_goods_category");
			if(ugcy==null) return new ReturnData(1,"分类信息有误 ");
			//验证是否是自己的
			
			String actionType="user_skin_protection";
				//添加至系统分类
			//	return new ReturnData("请选择分类");
				UserSkinProtection uai = mongoTemplate.findOne(new Query(Criteria.where("entityInfo.title").is(title).and("categoryPid").is(categoryPid).and("userId").is(userId)), UserSkinProtection.class, actionType);
				//重新获取产品
				if(uai!=null) {
					return new ReturnData(1,"已经添加过此产品");
				} else {
					UserSkinProtection usp=new UserSkinProtection();
					usp.setId(this.getId(actionType));
					usp.setEntityName("goods");
					usp.setUserId(userId);
					usp.setCategoryPid(categoryPid);
					usp.setCategoryId(categoryId);
					
					usp.setOpen(open);
					usp.setOpenTime(openTime);
					usp.setUsedType(usedType);
					usp.setReleaseDate(releaseDate);
					usp.setEntityInfo(EntityInfo.setCusEntiyInfo("goods", title, CommonUtils.getImag(image)));
					mongoTemplate.save(usp,actionType);
					
					//更新肤质方案修改
					updateSkinProtection(categoryPid,userId);
					
					return new ReturnData(usp);
				}
			}   catch(Exception e) {
				logger.error("method:UserPartService.addSkinProtectionGoods arg:{title:" + title + ",title:" + title + ",categoryId:"+categoryId+",categoryPid:"+categoryPid+"}"
						+ "   desc:" + ExceptionUtils.getStackTrace(e));

			}
			return ReturnData.ERROR;

	}
	
	/**
	 * 用户修改肤质方案 产品
	 * @param id
	 * @param categoryId
	 * @param open
	 * @param open_time
	 * @param releaseDate
	 * @param used_type
	 * @param image
	 * @return
	 */
	public ReturnData updateSkinProtectionGoods(Long id, Long categoryId, Integer open, Long open_time,
                                                Integer releaseDate, Integer used_type, String image, UserInfo userInfo) {
		
		Long userId=userInfo.getId();
		UserGoodsCategory  ugcy=this.mongoTemplate.findOne(new Query(Criteria.where("id").is(categoryId).and("userId").is(userId)), UserGoodsCategory.class, "user_goods_category");
		if(ugcy==null) return new ReturnData(1,"分类信息有误 ");
		String actionType="user_skin_protection";
		UserSkinProtection uai=this.mongoTemplate.findOne(new Query(Criteria.where("id").is(id).and("userId").is(userId)), UserSkinProtection.class, actionType);
		if(uai==null) {
			return new ReturnData(-2,"无效产品");
		}

		//确定是当前用户
		Update update = new Update();
			update.set("categoryId", categoryId);
			uai.setCategoryId(categoryId);
		
		if(open_time!=null) {
			update.set("openTime", open_time);
			uai.setOpenTime(open_time);
		}
		
		
		if(uai.getEntityId()==null) {
			EntityInfo u=EntityInfo.setCusEntiyInfo("goods", uai.getEntityInfo().getTitle(), image);
			uai.setEntityInfo(u);
			if(StringUtils.isNotBlank(image)) {
				u.setImage(CommonUtils.getImag(image));
				update.set("entityInfo.image",u.getImage());
			}
		}
		
 		if(releaseDate!=null) {
			update.set("releaseDate",releaseDate);
			uai.setReleaseDate(releaseDate);
		}
		if(used_type!=null) {
			update.set("usedType",used_type);
			uai.setUsedType(used_type);
		}
		
		 if(open==null||open==0||(used_type==null&&releaseDate==null&&open_time==null)) {
			update.set("usedType",null);
			uai.setUsedType(null);
			update.set("releaseDate",null);
			uai.setReleaseDate(null);
			update.set("openTime", null);
			uai.setOpenTime(null);
			update.set("open", null);
			uai.setOpen(0);
		} else {
			update.set("open", open);
			uai.setOpen(open);
		}

		if(null!=uai.getOpen()&& 1==uai.getOpen() && null!=uai.getRemainingDays() && uai.getRemainingDays()==0){
			uai.setExpire(false);
			update.set("expire", false);
		}
		 
		if(null!=open && open==1 && null!=releaseDate && null!=open_time){
			long expireTime=open_time+releaseDate*30*24*60*60;
			uai.setExpireTime(expireTime);
			update.set("expireTime", expireTime);
		}
		
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), update, actionType);
		
		updateSkinProtection(ugcy.getPid(),userId);
		
		return new ReturnData(uai);

	}
	/**
	 * 添加产品
	 * @param id
	 * @param title
	 * @param category_id
	 * @param open
	 * @param open_time
	 * @param used_type
	 * @param image
	 * @return
	 */
	public ReturnData addOrUpdateSkinProtectionGoods(UserInfo userInfo, String tname, Long id, String title, Long category_id, Long category_pid, Integer open, Long open_time,
                                                     Integer releaseDate, Integer used_type, String image, Long entityId) {
		long userId=0L;
		try{
			userId=userInfo.getId();
			if(entityId!=null&&entityId>0) {
				if(category_id!=null&&category_id>0&&category_pid!=null&&category_pid>0) {
					//用户在分类中添加产品
					return this.addSkinProtectionGoods(entityId, category_id, category_pid, userInfo,open,open_time,releaseDate,used_type);
				}
				if((category_id==null||category_id==0)&&(category_pid==null||category_pid==0)) {
					//用户在产品中添加
					return this.addSkinProtectionGoods(entityId, userInfo,open,open_time,releaseDate,used_type);
				}
			} else if(id==null||id==0) {
				//用户在产品中搜索不到时 自己添加 
				if(category_id!=null&&category_id>0&&category_pid!=null&&category_pid>0&& StringUtils.isNotBlank(title)) {
					return this.addSkinProtectionGoods(title, image, category_id, category_pid, userInfo,open,open_time,releaseDate,used_type);
				}
			} else if(id!=null&&id>0) {
				//修改
				return this.updateSkinProtectionGoods(id, category_id, open, open_time, releaseDate, used_type, image, userInfo);
			}
			return ReturnData.ERROR;
		} catch (Exception e) {
			logger.error("method:UserPartService.addOrUpdateSkinProtectionCategory arg:{userId:" + userId + ",tname:" + tname + ",userId:"+userId+",category_id:"+category_id+",open:"+open+",open_time:"+open_time+",releaseDate:"+releaseDate+",used_type:"+used_type+",image:"+image+",entityId:"+entityId+"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnData.ERROR;
		}

	}

	
	/**
	 * 删除分类
	 * @param id
	 * @param type: 1为方案 2为分类
	 * @return
	 */
	public ReturnData deleteSkinProtectionByCategoryId(Long id, UserInfo userInfo, int type) {
		try{
			String actionType="user_goods_category";
			UserGoodsCategory uai = mongoTemplate.findOne(new Query(Criteria.where("id").is(id).and("userId").is(userInfo.getId())), UserGoodsCategory.class, actionType);
			if(uai!=null&&id!=null&&id>0) {
				if(uai.getSrcCategoryId()!=null&&uai.getId()==id.intValue()) {
					return new ReturnData(-3,"默认分类不能删除");
				}
				
 				updateSkinProtection(uai.getPid(),userInfo.getId());
 				//删除方案或者分类
				mongoTemplate.remove(new Query(Criteria.where("id").is(id)), actionType);
				//删除方案或者分类下的产品
				Query query=null;
				if(type==1){
					query=new Query(Criteria.where("categoryPid").is(id));
				}else if(type==2){
					query=new Query(Criteria.where("categoryId").is(id));
				}
				if(null!=query){
					mongoTemplate.remove(query, "user_skin_protection");
				}
				
				return ReturnData.SUCCESS;
			}
		} catch (Exception e) {
			logger.error("method:UserPartService.deleteSkinProtectionByCategoryId arg:{id:" + id +"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			
		}
		return ReturnData.ERROR;
	}
	
	/**
	 * 删除方案
	 * @param categoryPid
	 * @return
	 */
	public ReturnData deleteSkinProtectionByCategoryPid(Long categoryPid, Long userId) {
		try{
			String actionType="user_goods_category";
			UserGoodsCategory uai = mongoTemplate.findOne(new Query(Criteria.where("id").is(categoryPid).and("userId").is(userId)), UserGoodsCategory.class, actionType);
			if(uai!=null&&categoryPid!=null&&categoryPid>0) {
				if(uai.getSrcCategoryId()!=null&&uai.getId()==categoryPid.intValue()) {
					return new ReturnData(-3,"默认分类不能删除");
				}
				mongoTemplate.remove(new Query(Criteria.where("id").is(categoryPid)), actionType);
				mongoTemplate.remove(new Query(Criteria.where("categoryPid").is(categoryPid)), "user_skin_protection");
				return ReturnData.SUCCESS;
			}
		} catch (Exception e) {
			logger.error("method:UserPartService.deleteSkinProtectionByCategoryPid arg:{categoryPid:" + categoryPid +"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			
		}
		return ReturnData.ERROR;
	}


	/**
	 * 方案分类排序
	 * @param sorts
	 * @param ids
	 * @return
	 */
	public ReturnData sortSkinProtectionCategory(String ids, String sorts, Long userId) {
		try{
			String actionType="user_goods_category";
			Update update=new Update();
			String id[]=ids.split(",");
			String sort[]=sorts.split(",");
			
			for(int i=0;i<id.length;i++) {
				//更新排序 升序
				update.set("sort", Long.parseLong(sort[i]));
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(Long.parseLong(id[i])).and("userId").is(userId)),update, actionType);
			}
			//List<UserGoodsCategory> ugcList=mongoTemplate.find(new Query(Criteria.where("userId").is(userId)).with(new Sort(Direction.ASC, "sort")), UserGoodsCategory.class,actionType);
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			logger.error("method:sortSkinProtectionCategory arg:{id:" + ids +", sort:"+sorts+"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnData.ERROR;
		}
	}

 



 	
}


