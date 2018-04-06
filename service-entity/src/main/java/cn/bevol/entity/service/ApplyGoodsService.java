package cn.bevol.entity.service;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.constant.CommenMeta;
import cn.bevol.constant.CommenMeta.MessageStatus;
import cn.bevol.model.entity.EntityApplyGoods;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.entity.EntityUserPart;
import cn.bevol.model.entityAction.ApplyGoodsUser;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.vo.SmartUserInfo;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import cn.bevol.conf.client.ConfUtils;
import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
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

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 福利社调整数据
 * @author hualong
 *
 */
@Service
@Deprecated
public class ApplyGoodsService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(ApplyGoodsService.class);

    @Autowired
    MongoTemplate mongoTemplate;


    @Autowired
    RedisCacheProvider cacheProvider;

    @Autowired
    private EntityService entityService;

    @Autowired
	AliyunService aliyunService;

    @Autowired
    UserService userService;
    
    @Autowired
    MessageService messageService;
    
    @Autowired
    CacheService cacheService;
    
    @Autowired
    UserPartService userPartService;
    
    /**
     * v3.1
     * 福利社文章/活动列表  
     * 带缓存 五分钟过期
     * @param pager
     * @param rows
     * @return
     */
	public ReturnListData findApplyGoodslist(final long userId,final int pager,final int rows) {
		return new CacheableTemplate<ReturnListData<List<EntityUserPart>>>(cacheProvider) {
			@Override
			protected ReturnListData getFromRepository() {
				try {
					//要查询的字段
					String fields[]=new String[]{"id","notLikeNum","likeNum","hitNum","title","image","tag","tagIds","startTime","lastTime","curTime","activeState","activeStateDesc","goodsNum","applyNum","type","doyenScore"};
					return list(userId,pager,rows,fields);
				} catch (Exception e) {
					Map map=new HashMap();
					map.put("method", "ApplyGoodsService.findApplyGoodslist");
					map.put("userId", userId);
					map.put("pager", pager);
					map.put("rows", rows);
					new cn.bevol.log.LogException(e,map);
					return ReturnListData.ERROR;
				}
			}
			@Override
			protected boolean canPutToCache(ReturnListData returnValue) {
				return (returnValue != null && returnValue.Tesult() != null && returnValue.Tesult().size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_APPGOODS_LIST_PREFIX,pager+"",rows+"")),
				true);
	}
	
	
	/**
	 * 不带缓存的福利社列表
	 * @param pager
	 * @param rows
	 * @return
	 */
	public ReturnListData list(Long userId,int pager, int rows,String... fields) {
			Criteria crt= Criteria.where("hidden").is(0).and("createStamp").lt(1502770014);
			int p=0;
			Query query = Query.query(crt);
			if(pager<=1) {
				p=0;
			} else {
				p=(pager-1);
			}
			//设置查询字段
			setQueryFeilds(query,fields);
			Integer startId = Integer.valueOf((p * rows) + "");
			query.skip(startId).limit(rows).with(new Sort(Direction.DESC, "id"));
			List<EntityApplyGoods> uais = mongoTemplate.find(query, CommenMeta.getEntityClass("entity_apply_goods"));
			List<Map> listM=new ArrayList();
			String tname="lists";
			//添加活动的状态
			for(EntityApplyGoods uag:uais){
				//设置永户与活动的关系,活动的参与人数
				long userPartNum=userPartService.findUserPartCount2(tname,2,uag.getId());
				JSONObject map=new JSONObject();
				
				try {
					map = JSONObject.fromObject(uag);
					//map=BeanUtils.describe(uag);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				if(null!=userId && userId>0){
					int applyState=entityService.applyState(uag.getId(),userId);
					map.put("apply",applyState);
				}
				map.put("userPartNum", userPartNum);
				
				listM.add(map);
			}
			//列表活动的数量
			long count = mongoTemplate.count(query, CommenMeta.getEntityClass("entity_apply_goods"));
			return new ReturnListData(listM,count);
	}
	
	/**
	 * v3.1
	 * 试用报告列表
	 * 缓存:五分钟过期
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	public ReturnListData findApplyGoods(final int pager, final int rows) {
		return new CacheableTemplate<ReturnListData<List<EntityUserPart>>>(cacheProvider) {
			@Override
			protected ReturnListData getFromRepository() {
				try {
					//todo 硬编码
					String actionType="entity_user_part_lists";
					Criteria crt= Criteria.where("hidden").is(0).and("type").is(2);
					int p=0;
					
					Query query = Query.query(crt);
					String fields[]=new String[]{"id","notLikeNum","likeNum","hitNum","title","image","tag","tagIds","startTime","lastTime","curTime","activeState","activeStateDesc","userBaseInfo","pEntityId","pEntityName"};
					//设置要查询的字段
					setQueryFeilds(query,fields);
					if(pager<=1) {
						p=0;
					} else {
						p=(pager-1);
					}
					Integer startId = Integer.valueOf((p * rows) + "");
					//id倒叙排序
					query.skip(startId).limit(rows).with(new Sort(Direction.DESC, "id"));
					List<EntityUserPart> uais = mongoTemplate.find(query, EntityUserPart.class, actionType);
					return new ReturnListData(uais,0);
				} catch (Exception e) {
					Map map=new HashMap();
					map.put("method", "ApplyGoodsService.findApplyGoods");
					map.put("pager", pager);
					map.put("rows", rows);
					new cn.bevol.log.LogException(e,map);
					return ReturnListData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnListData returnValue) {
				return (returnValue != null && returnValue.Tesult() != null && returnValue.Tesult().size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_USERPART_APPGOODS_LIST_PREFIX,pager+"",rows+"")),
				true);

	}
	
	
	/**
	 * 申请试用
	 * @param userInfo 用户
	 * @param id: 活动id
	 * @return
	 */
	public ReturnData applyUsed(UserInfo userInfo, Long id) {
		try{
			//检查是否还可以申请
			//验证实体是否存在
			String actype="apply_goods_user";
	        EntityApplyGoods entityApplyGoods= mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), EntityApplyGoods.class);
			if(entityApplyGoods==null ) return new ReturnData(-2,"不存在");
			if(entityApplyGoods.getActiveState()!=null) {
				if(entityApplyGoods.getActiveState()==2) {
					return new ReturnData(-2,"还未开始");
				} else if(entityApplyGoods.getActiveState()==3) {
					return new ReturnData(-2,"已经结束");
				}
			}
			
			//判断用户修行值是否满足
			if(null!=entityApplyGoods.getDoyenScore() && entityApplyGoods.getDoyenScore()>0){
				int entityScore=entityApplyGoods.getDoyenScore();
				userInfo=mongoTemplate.findOne(Query.query(Criteria.where("id").is(userInfo.getId())),UserInfo.class,"user_info");
				if(null!=userInfo){
					if(null==userInfo.getScore() || userInfo.getScore()<entityScore){
						return new ReturnData(-6,"修行值不够");
					}
				}else{
					return ReturnData.ERROR;
				}
			}
			
			
			if(mongoTemplate.exists(Query.query(Criteria.where("entityId").is(id).and("userId").is(userInfo.getId())), ApplyGoodsUser.class)) {
				return new ReturnData(4,"已经申请过产品");
			}else {
				//申请人数增加
				this.objectIncById(id, "entity_apply_goods", "applyNum", 1);
				
				ApplyGoodsUser applyGoods=new ApplyGoodsUser();
				applyGoods.setUserId(userInfo.getId());
				applyGoods.setState(1);
				applyGoods.setEntityId(id);
				applyGoods.setId(this.getId(actype));
				this.mongoTemplate.save(applyGoods);
			}
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "ApplyGoodsService.applyUsed");
			map.put("userInfo", userInfo.getId());
			map.put("id", id);
			new cn.bevol.log.LogException(e,map);
			return ReturnData.ERROR;
		}
	}
	
	/**
	 * 我的申请列表
	 * @param userId
	 * @param start_id
	 * @param page_size
	 * @return
	 */
	public ReturnListData myApplyGoodsLists(Long userId, long start_id, int page_size) {
		try{
			Criteria crt= Criteria.where("userId").is(userId).and("hidden").is(0);
			if (start_id > 0) {
	            crt.and("id").lt(start_id);
	        }
			/*Integer skipId=0;
			if(start_id > 1){
				skipId= Integer.parseInt((start_id-1)*page_size+"");
			}*/
			
			Query query = Query.query(crt).limit(page_size).with(new Sort(Direction.DESC, "id"));
	        
	        List<ApplyGoodsUser> aguList = mongoTemplate.find(query, ApplyGoodsUser.class);
	        //更新状态
	        for(ApplyGoodsUser agu:aguList){
	        	Query query2=new Query(Criteria.where("id").is(agu.getEntityId()));
	        	//查找对应的活动
	        	EntityBase map = (EntityBase) mongoTemplate.findOne(query2, CommenMeta.getEntityClass("entity_apply_goods"));
	        	//用户与活动的状态
	        	int applyState=entityService.applyState2(map.getId(), userId,agu);
	        	agu.setState(applyState);
	        }
	        
	        
	        long total = mongoTemplate.count(new Query(Criteria.where("userId").is(userId).and("hidden").is(0)), ApplyGoodsUser.class);
	 		return new ReturnListData(entityService.entityActionHandler(aguList,"apply_goods"),total);
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "ApplyGoodsService.myApplyGoodsLists");
			map.put("userId", userId);
			map.put("start_id", start_id);
			map.put("page_size", page_size);
			new cn.bevol.log.LogException(e,map);
			return ReturnListData.ERROR;
		}
		
	}

	/**
	 * 设置用户中奖
	 * @param id
	 * @param user_ids
	 * @return
	 */
	public ReturnData usedGoodsByUserIds(long id, String userIds,String title,String content,String redirect_type,Integer newType) {
		try{
			//查找活动
			EntityBase uais = this.getEntityById("apply_goods", id);
			if(uais==null) return EntityBase.ENTITY_HIDDEN;
			String users[]=userIds.split(",");
			List<Long> receiverIds = new ArrayList<Long>();
			for(int i=0;i<users.length;i++) {
				Long uid=Long.parseLong(users[i]);
				Criteria crt= Criteria.where("userId").is(uid).and("entityId").is(id);
				Query query = Query.query(crt);
				this.mongoTemplate.updateFirst(query, new Update().set("state", 2), ApplyGoodsUser.class);
				//发送系统消息
				receiverIds.add(uid);
			}
			// 发送消息
			String params="";
			String page="";
			MessageStatus msgDesc = MessageStatus.getStatusByKey("msg-xxj-apply_goods_user");
			 long replyUserId=ConfUtils.getResourceNum("mangeUserId");
			messageService.sendSynMessage(replyUserId, receiverIds, msgDesc.getType(), msgDesc.getDescription(), title, content,redirect_type,page,params,newType);
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "ApplyGoodsService.usedGoodsByUserIds");
			map.put("id", id);
			map.put("userIds", userIds);
			map.put("title", title);
			map.put("content", content);
			map.put("redirect_type", redirect_type);
			map.put("newType", newType);
			new cn.bevol.log.LogException(e,map);
			return ReturnData.ERROR;
		}
	}
	
	
	/**
	 * 添加活动
	 * @param entityApplyGoods
	 * @return
	 */
	public ReturnData addEntityApplyGoods(EntityApplyGoods entityApplyGoods) {
		try{
				if(null!=entityApplyGoods.getStartTime() && null!=entityApplyGoods.getLastTime()){
					long date=new Date().getTime()/1000;
					if(entityApplyGoods.getStartTime().intValue()>entityApplyGoods.getLastTime().intValue()){
						return new ReturnData(-6,"开始时间必须小于结束时间");
					}else if(entityApplyGoods.getLastTime().intValue()<date){
						return new ReturnData(-6,"结束时间必须大于当前时间");
					}else if(entityApplyGoods.getStartTime().intValue()>date && entityApplyGoods.getStartTime().intValue()<entityApplyGoods.getLastTime().intValue()){
						entityApplyGoods.setActiveState(1);
					}
				
				String tname="entity_apply_goods";
				long id=this.getId(tname);
				entityApplyGoods.setPublishTime(Integer.parseInt((new Date().getTime()/1000)+""));
				entityApplyGoods.setId(id);
				this.save(tname, entityApplyGoods);
				//清理列表的缓存
				cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_APPGOODS_LIST_PREFIX));

				return new ReturnData(entityApplyGoods);
			}
			return ReturnData.ERROR;
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "ApplyGoodsService.addEntityApplyGoods");
			map.put("id", entityApplyGoods.getId());
			map.put("activeState", entityApplyGoods.getActiveState());
			map.put("activeStateDesc", entityApplyGoods.getActiveStateDesc());
			map.put("doyenScore", entityApplyGoods.getDoyenScore());
			map.put("userPartNum", entityApplyGoods.getUserPartNum());
			map.put("uniqueId", entityApplyGoods.getUniqueId());
			map.put("type", entityApplyGoods.getType());
			map.put("tname", entityApplyGoods.getTname());
			map.put("tagIds", entityApplyGoods.getTagIds());
			map.put("startTime", entityApplyGoods.getStartTime());
			map.put("lastTime", entityApplyGoods.getLastTime());
			map.put("title", entityApplyGoods.getTitle());
			map.put("image", entityApplyGoods.getImage());
			map.put("goodsIds", entityApplyGoods.getGoodsIds());
			map.put("descp", entityApplyGoods.getDescp());
			new cn.bevol.log.LogException(e,map);
			return ReturnData.ERROR;
		}
	}

	/**
	 * 修改申请
	 * @param entityApplyGoods
	 * @return
	 */
	public ReturnData updateEntityApplyGoods(EntityApplyGoods entityApplyGoods) {
		try{
			Update update=new Update();
			String tname="entity_apply_goods";
			if(!StringUtils.isBlank(entityApplyGoods.getTitle())) {
				update.set("title", entityApplyGoods.getTitle());
			} 
			if(!StringUtils.isBlank(entityApplyGoods.getTagIds())) {
				update.set("tagIds", entityApplyGoods.getTagIds());
			} 
			if(!StringUtils.isBlank(entityApplyGoods.getImage())) {
				update.set("image", entityApplyGoods.getImage());
			} 
			if(entityApplyGoods.getHidden()>0) {
				update.set("hidden", entityApplyGoods.getHidden());
			}
			if(null!=entityApplyGoods.getGoodsNum()){
				update.set("goodsNum", entityApplyGoods.getGoodsNum());
			}
			if(null!=entityApplyGoods.getDoyenScore()){
				update.set("doyenScore", entityApplyGoods.getDoyenScore());
			}
			if(null!=entityApplyGoods.getLastTime()){
				update.set("lastTime", entityApplyGoods.getLastTime());
			}
			if(null!=entityApplyGoods.getStartTime()){
				update.set("startTime", entityApplyGoods.getStartTime());
			}
			if(!StringUtils.isBlank(entityApplyGoods.getGoodsIds())) {
				update.set("goodsIds", entityApplyGoods.getGoodsIds());
			} 
			if(null!=entityApplyGoods.getType()) {
				update.set("type", entityApplyGoods.getType());
			} 
			
			update.set("updateStamp", new Date().getTime()/1000);
			this.mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(entityApplyGoods.getId())), update, tname);
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "ApplyGoodsService.updateEntityApplyGoods");
			map.put("id", entityApplyGoods.getId());
			map.put("activeState", entityApplyGoods.getActiveState());
			map.put("activeStateDesc", entityApplyGoods.getActiveStateDesc());
			map.put("doyenScore", entityApplyGoods.getDoyenScore());
			map.put("userPartNum", entityApplyGoods.getUserPartNum());
			map.put("uniqueId", entityApplyGoods.getUniqueId());
			map.put("type", entityApplyGoods.getType());
			map.put("tagIds", entityApplyGoods.getTagIds());
			map.put("startTime", entityApplyGoods.getStartTime());
			map.put("lastTime", entityApplyGoods.getLastTime());
			map.put("title", entityApplyGoods.getTitle());
			map.put("image", entityApplyGoods.getImage());
			map.put("goodsIds", entityApplyGoods.getGoodsIds());
			map.put("descp", entityApplyGoods.getDescp());
			new cn.bevol.log.LogException(e,map);
			return ReturnData.ERROR;
		}
		
	}

	/**
	 * 根据state,福利社参与申请的用户信息
	 * @param id: 申请的活动id
     * @param state:* 1参与中 ----活动正在进行中
 					* 4参与中		活动已结束
					* 2中奖了 没发过该活动的心得
					* 3中奖了 发过该活动的心得
	 */
	public ReturnListData findApplyUserList(Integer id, Integer state, int pager, int pageSize) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		try{
			Criteria crt= Criteria.where("entityId").is(id);
			Query query = Query.query(crt).skip(pager).limit(pageSize).with(new Sort(Direction.DESC, "id"));
			if(state>0)
				crt.and("state").is( state);
			List<ApplyGoodsUser> agus=this.mongoTemplate.find(query , ApplyGoodsUser.class);
			long count=this.mongoTemplate.count(query , ApplyGoodsUser.class);
			//获取集合中对象的某个属性的集合
	        Collection<Long> peoplesCities = CollectionUtils.collect( agus, new BeanToPropertyValueTransformer("userId") );
	        List<Long> ls=new ArrayList<Long>(peoplesCities);
			List<SmartUserInfo> userser=userService.findSmartUserInfoByIds2(ls);
			List<Map> applyGoods=new ArrayList();
			for(int i=0;i<agus.size();i++) {
				boolean flag=true;
				for(int j=0;j<userser.size()&&flag;i++) {
					if(userser.get(j).getId().equals(agus.get(i).getUserId())&&flag) {
						agus.get(i).setSmartUserInfo(userser.get(j));
						flag=false;
						
	 				}
				}
			}
			return new ReturnListData(agus,count);
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "ApplyGoodsService.findApplyUserList");
			map.put("id", id);
			map.put("state", state);
			map.put("pager", pager);
			map.put("pageSize", pageSize);
			new cn.bevol.log.LogException(e,map);
			return ReturnListData.ERROR;
		}
		
	}


}
