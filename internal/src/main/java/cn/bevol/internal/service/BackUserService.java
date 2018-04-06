package cn.bevol.internal.service;


import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.cache.CacheKey;
import cn.bevol.internal.cache.CacheableTemplate;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.mapper.BackUserOldMapper;
import cn.bevol.internal.dao.mapper.IndexOldMapper;
import cn.bevol.internal.entity.model.UserBlackList;
import cn.bevol.internal.entity.user.UserInfo;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.cache.CACHE_NAME;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 
 * @author chenHaiJian
 *
 */

@Service
public class BackUserService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(BackUserService.class);
	@Autowired
	RedisCacheProvider cacheProvider;
	
	@Autowired
	BackUserOldMapper backUserOldMapper;
	
	@Autowired
	IndexOldMapper indexOldMapper;
	
	@Resource
	UserService userService;
	
	@Resource
	CacheService cacheService;
	
	@Resource
	MessageService messageService;
	
	/**
	    * 把用户拉进黑名单
	    * @param request
	    * @param user_id
	    * @param state 1永久禁言 2时效性
	    * @param description 禁言原因描述
	    * @param start_time	开始禁言的时间
	    * @param end_time	结束禁言的时间
	    */
	public ReturnData addBlcakList(Long userId, Integer state, String description, Long startTime, Long endTime) {
		try {
			if (null != userId && userId > 0) {
				if (null != startTime && null != endTime && null != state && state == 2) {
					if (startTime > endTime) {
						return new ReturnData("开始时间应小于结束时间!");
					} else if (endTime < DateUtils.nowInMillis() / 1000) {
						return new ReturnData("结束时间不能小于当前时间!");
					}
				}
				List<UserBlackList> userList = this.getUserBlackList2();
				ReturnData rd = userService.getUserById(userId);
				if (rd.getRet() == 0) {
					UserInfo userInfo = (UserInfo) rd.getResult();
					if (null == userInfo || userInfo.getId() != userId.intValue()) {
						return new ReturnData("用户不存在!");
					}
				} else {
					return ReturnData.ERROR;
				}
				for (UserBlackList user : userList) {
					if (user.getUserId().intValue() == userId) {
						return new ReturnData<UserBlackList>(user, user.getUserId().intValue(), "该用户已在黑名单!");
					}
				}
				// 添加
				UserBlackList UserBlackList = new UserBlackList();
				UserBlackList.setCreateTime(DateUtils.nowInMillis() / 1000);
				if (StringUtils.isNotBlank(description)) {
					UserBlackList.setDescription(description);
				} else {
					UserBlackList.setDescription("");
				}
				UserBlackList.setUserId(userId);
				UserBlackList.setState(state);
				// 时效性
				if (state == 2) {
					if (null != endTime && endTime > 0 && null != startTime && startTime > 0) {
						UserBlackList.setEndTime(endTime);
						UserBlackList.setStartTime(startTime);
					} else {
						return new ReturnData("时效性的黑名单必须有开始和结束时间!");
					}
					backUserOldMapper.insertUserBlackList(UserBlackList);
					// 永久
				} else if (state == 1) {
					backUserOldMapper.insertUserBlackList2(UserBlackList);
				}
				//发送xxj消息
				long replyUserId= ConfUtils.getResourceNum("mangeUserId");
				String title="禁言通知";
				String redirectType="";
				String page="";
				String params="";
				Integer newType=null;
				messageService.sendMsgByXxj(replyUserId, userId+"", title,description,redirectType,page,params,newType);
			}
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			Map map=new HashMap();
    		map.put("method", "BackUserService.addBlcakList");
    		map.put("userId", userId);
    		map.put("state", state);
    		map.put("description", description);
    		map.put("startTime", startTime);
    		map.put("endTime", endTime);
    		new LogException(e,map);
		}
		return ReturnData.ERROR;
	}
	
	/**
	 * 移出黑名单
	 * @param userId
	 * @return
	 */
	public ReturnData removeBlcakList(Long userId) {
		try {
			if (null != userId && userId > 0) {
				backUserOldMapper.deleteBlackList(userId);
				cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_USERBLACKLIST_ID_PREFIX,userId+""));
				//发送xxj消息 解禁
				long replyUserId=ConfUtils.getResourceNum("mangeUserId");
				String title="解禁通知";
				String redirectType="";
				String page="";
				String params="";
				String content="你的禁言结束!";
				Integer newType=null;
				messageService.sendMsgByXxj(replyUserId, userId+"", title,content,redirectType,page,params,newType);
			}
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			Map map=new HashMap();
    		map.put("method", "BackUserService.removeBlcakList");
    		map.put("userId", userId);
    		new LogException(e,map);
		}
		return ReturnData.ERROR;
	}
	
	
	/**
	 * 修改黑名单信息
	 * @param userId
	 * @return 
	 */
	public ReturnData updateBlcakList(Long userId, Integer state, String description, Long startTime, Long endTime) {
		try {
			if (null != userId && userId > 0) { 
				UserBlackList userBlackList = new UserBlackList();
				Long nowTime = DateUtils.nowInMillis() / 1000;
				if (null != state && state > 0) {
					// 时效性的必须有时间
					if (state == 2) {
						if (null != startTime && startTime >= 0 && null != endTime && endTime > nowTime) {
							userBlackList.setStartTime(startTime);
							userBlackList.setEndTime(endTime);
						} else {
							return new ReturnData("时间出错!");
						}
					}
					userBlackList.setState(state);
				}
				if (StringUtils.isNotBlank(description)) {
					userBlackList.setDescription(description);
				}
				userBlackList.setUpdateTime(nowTime);
				userBlackList.setUserId(userId);
				backUserOldMapper.updateBlackList(userBlackList);

			}
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			Map map=new HashMap();
    		map.put("method", "BackUserService.updateBlcakList");
    		map.put("userId", userId);
    		map.put("state", state);
    		map.put("description", description);
    		map.put("startTime", startTime);
    		map.put("endTime", endTime);
    		new LogException(e,map);
		}
		return ReturnData.ERROR;
	}
	

	/**
     * 查找用户是否在黑名单列表中
     * 五分钟缓存
     * @param userId
     * @param pager
     * @param pageSize
     * @return
     */
	public UserBlackList getUserBlackById(final Long userId) {
		return new CacheableTemplate<UserBlackList>(cacheProvider) {
			@Override
			protected UserBlackList getFromRepository() { 
				try {
					UserBlackList User = backUserOldMapper.getUserBlackById(userId);
					return User;
				} catch (Exception e) {
					Map map=new HashMap();
		    		map.put("method", "BackUserService.getUserBlackById");
		    		map.put("userId", userId);
		    		new LogException(e,map);
					return null;
				}
			}

			@Override
			protected boolean canPutToCache(UserBlackList returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_USERBLACKLIST_ID_PREFIX,userId+"")
				), true);
	}
	
	
	/**
     * 用户黑名单列表,无缓存
     */
	public List<UserBlackList> getUserBlackList2() {
		try {
			List<UserBlackList> listUser = backUserOldMapper.getUserBlackList();
			return listUser;
		} catch (Exception e) {
			Map map=new HashMap();
    		map.put("method", "BackUserService.getUserBlackList2");
    		new LogException(e,map);
			return null;
		}
			
	}
	
	
	/**
     * 1.为用户以前的精华点评和修行说计数,更新mongo的user表
     * 2.有的达人没有注册,精华点评和修行说的数量为0.注册后调用该接口,进行计数
     * @return
     */
	public ReturnData getEssenceCommentAndXxsNum() {
		try {
			String entityName="user_info";
			//根据用户进行分组
			List<Map<String,Object>> essenceCommentList= indexOldMapper.getNum(1);
			List<Long> userIdsList=new ArrayList<Long>();
			
			for(Map ecmap:essenceCommentList){
				long userId=(Integer)ecmap.get("user_id");
				//每个作者,精华点评的数量
				long ecNum=(Long)ecmap.get("num");
				userIdsList.add(userId);
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userId)), new Update().set("essenceCommentNum", ecNum), UserInfo.class,entityName);
			}
			
			//修行说的数量
			List<Map<String,Object>> xxsList= indexOldMapper.getNum(2);
			for(Map ecmap:xxsList){
				long userId=(Integer)ecmap.get("user_id");
				long xxsNum=(Long)ecmap.get("num");
				userIdsList.add(userId);
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userId)), new Update().set("xxsNum", xxsNum), UserInfo.class,entityName);
			}
			
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			Map map=new HashMap();
    		map.put("method", "BackUserService.getEssenceCommentAndXxsNum");
    		new LogException(e,map);
			return ReturnData.ERROR;
		}
			
	}

 }