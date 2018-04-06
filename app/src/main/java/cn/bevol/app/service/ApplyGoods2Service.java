package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.entity.constant.CommenMeta;
import cn.bevol.model.entityAction.ApplyGoodsUser;
import cn.bevol.model.entityAction.Comment;
import cn.bevol.app.entity.vo.SmartUserInfo;
import cn.bevol.model.entity.*;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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
public class ApplyGoods2Service extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(ApplyGoods2Service.class);

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
     * v3.2
     * 福利社文章/活动列表
     * 带缓存 五分钟过期
     * @param pager
     * @param rows
     * @return
     */
    public ReturnListData findApplyGoodslist(final long userId, final int pager, final int rows) {
        return new CacheableTemplate<ReturnListData<List<EntityUserPart>>>(cacheProvider) {
            @Override
            protected ReturnListData getFromRepository() {
                try {
                    //要查询的字段
                    String fields[]=new String[]{"id","notLikeNum","likeNum","hitNum","title","image","tag","tagIds","startTime","lastTime","curTime","activeState","activeStateDesc","goodsNum","applyNum","type","doyenScore","shareState","applyEndTime","prizeEndTime","lastUserPartTime","price","publishTime"};
                    return list(userId,pager,rows,fields);
                } catch (Exception e) {
                    Map map=new HashMap();
                    map.put("method", "ApplyGoodsService.findApplyGoodslist");
                    map.put("userId", userId);
                    map.put("pager", pager);
                    map.put("rows", rows);
                    new LogException(e,map);
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
     * 不带缓存的福利社列表(不返回隐藏的数据)
     * @param pager
     * @param rows
     * @return
     */
    public ReturnListData list(Long userId, int pager, int rows, String... fields) {
        return list(userId, null, null, null, null, null, true, pager, rows, fields);
    }

    /**
     * 福利社列表
     * @param userId
     * @param hidden
     * @param pager
     * @param rows
     * @param fields
     * @return
     */
    public ReturnListData list(Long userId, Integer id, Integer shareState, String title, Integer activeState, Integer isHidden, Boolean hidden, int pager, int rows, String... fields) {
        try {
            Criteria crt;
            if (hidden) {
                //前台
                crt = Criteria.where("hidden").is(0).and("deleted").is(0);
            } else {
                //后台
                if(null != isHidden){
                    crt = Criteria.where("hidden").is(isHidden).and("deleted").is(0);
                }else{
                    crt = Criteria.where("deleted").is(0);
                }
            }
            if(null != shareState){
                crt.and("shareState").is(shareState);
            }
            if(null != id){
                crt.and("id").is(id);
            }
            if(null != title){
                crt.and("title").regex(title);
            }
            if(null != activeState){
                int curTime = DateUtils.nowInSeconds();
                if(activeState == 6){
                    //未展现
                    crt.and("publishTime").gt(curTime);
                } else if(activeState==2) {
                    //未开始、即将开始
                    crt.and("startTime").gt(curTime).and("publishTime").lt(curTime);
                } else if(activeState==1) {
                    //申请中
                    crt.and("startTime").lt(curTime).and("applyEndTime").gt(curTime);
                } else if(activeState==4) {
                    //开奖中   名单筛选中
                    crt.and("applyEndTime").lt(curTime).and("prizeEndTime").gt(curTime);
                } else if(activeState==5) {
                    //报告收取中  测试报告收集中
                    crt.and("prizeEndTime").lt(curTime).and("lastTime").gt(curTime);
                } else if(activeState==3) {
                    //报告收取中  测试报告收集中
                    crt.and("lastTime").lt(curTime);
                }
            }
            int p = 0;
            Query query = Query.query(crt);
            //不返回descp
            query.fields();
            if (pager <= 1) {
                p = 0;
            } else {
                p = (pager - 1);
            }
            //设置查询字段
            setQueryFeilds(query, fields);
            Integer startId = Integer.valueOf((p * rows) + "");
            //todo
            query.skip(startId).limit(rows).with(new Sort(Direction.DESC, "publishTime"));
            List<EntityApplyGoods2> uais = mongoTemplate.find(query, CommenMeta.getEntityClass("entity_apply_goods2"));
            
           /* List<EntityApplyGoods2> endList=new  ArrayList<EntityApplyGoods2>();
            List<EntityApplyGoods2> startList=new  ArrayList<EntityApplyGoods2>();
            List<EntityApplyGoods2> readyList=new  ArrayList<EntityApplyGoods2>();

            //活动列表排序,活动结束了的排在活动进行中的后面
            for(EntityApplyGoods2 apg:uais){
            	if(null!=apg.getActiveState()){
            		if(apg.getActiveState()==3){
                		//活动结束的
            			endList.add(apg);
                	}else if(apg.getActiveState()==1 || apg.getActiveState()==4 || apg.getActiveState()==5){
                		//活动进行中
                		startList.add(apg);
                	}else if(apg.getActiveState()==2){
                		//活动即将进行
                		readyList.add(apg);
                	}
            	} 
            }
            startList.addAll(readyList);
        	startList.addAll(endList);
        	uais=startList;*/
            //列表活动的数量
            long count = mongoTemplate.count(Query.query(crt), CommenMeta.getEntityClass("entity_apply_goods2"));
            return new ReturnListData(uais, count);
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoodsService.list");
            map.put("hidden", hidden);
            map.put("pager", pager);
            map.put("rows", rows);
            map.put("fields", fields);
            new LogException(e,map);
            return ReturnListData.ERROR;
        }
    }

    /**
     * v3.2
     * 试用报告列表
     * 缓存:五分钟过期
     * @param pager
     * @return
     */
    public ReturnListData findApplyGoods(final Long id, final int pager, final int rows) {
        return new CacheableTemplate<ReturnListData<List<EntityUserPart>>>(cacheProvider) {
            @Override
            protected ReturnListData getFromRepository() {
                try {
                    String actionType="entity_user_part_lists";
                    Criteria crt= Criteria.where("hidden").is(0).and("type").is(2).and("pEntityId").is(id);
                    int p=0;

                    Query query = Query.query(crt);
                    String fields[]=new String[]{"id","notLikeNum","likeNum","hitNum","title","image","userBaseInfo","pEntityId","pEntityName","userPartDetails"};
                    //设置要查询的字段
                    setQueryFeilds(query,fields);
                    if(pager<=1) {
                        p=0;
                    } else {
                        p=(pager-1);
                    }
                    Integer startId = Integer.valueOf((p * rows) + "");
                    //id倒叙排序
                    query.skip(startId).limit(rows).with(new Sort(Direction.DESC, "sort2")).with(new Sort(Direction.DESC, "id"));
                    List<EntityUserPart> uais = mongoTemplate.find(query, EntityUserPart.class, actionType);
                    //简化心得的内容
                    for(EntityUserPart eup: uais){
                    	if(null!=eup.getUserPartDetails() && eup.getUserPartDetails().size()>0){
                    		List<UserPartDetail> updList=eup.getUserPartDetails();
                    		List<UserPartDetail> newUpdList=new ArrayList<UserPartDetail>();
                    		for(UserPartDetail upd:updList){
                    			if(upd instanceof UserPartDetailText){
                    				if(newUpdList.size()==0){
                    					newUpdList.add(upd);
                        				eup.setUserPartDetails(newUpdList);
                    				}
                    			}
                    		}
                    	}
                    }
                    //总数
                    long total=mongoTemplate.count(new Query(Criteria.where("pEntityId").is(id).and("type").is(2).and("hidden").is(0)),EntityUserPart.class,actionType);
                    return new ReturnListData(uais,total);
                } catch (Exception e) {
                    Map map=new HashMap();
                    map.put("method", "ApplyGoodsService.findApplyGoods");
                    map.put("pager", pager);
                    map.put("rows", rows);
                    new LogException(e,map);
                    return ReturnListData.ERROR;
                }
            }

            @Override
            protected boolean canPutToCache(ReturnListData returnValue) {
                return (returnValue != null && returnValue.Tesult() != null && returnValue.Tesult().size() > 0);
            }
        }.execute(
                new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                        CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_USERPART_APPGOODS_LIST_PREFIX,id+"",pager+"",rows+"")),
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
            EntityApplyGoods2 entityApplyGoods= mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), EntityApplyGoods2.class);
            if(entityApplyGoods==null ) {
                return new ReturnData<>(-2,"不存在");
            }
            /**
             * 活动状态
             * 1、申请中
             * 2、即将开始
             * 3、活动结束
             * 4、名单筛选中
             * 5、测试报告收集中
             */
            if(entityApplyGoods.getActiveState()!=null) {
				/*if(entityApplyGoods.getActiveState()==2) {
					return new ReturnData(-2,"还未开始");
				} else if(entityApplyGoods.getActiveState()==3) {
					return new ReturnData(-2,"已经结束");
				}*/
                if(entityApplyGoods.getActiveState()!=1){
                    return new ReturnData(-2,entityApplyGoods.getActiveStateDesc());
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
                this.objectIncById(id, "entity_apply_goods2", "applyNum", 1);

                ApplyGoodsUser applyGoods=new ApplyGoodsUser();
                applyGoods.setUserId(userInfo.getId());
                //状态设置为申请
                applyGoods.setState(1);
                applyGoods.setEntityId(id);
                applyGoods.setId(this.getId(actype));
                applyGoods.setScore(userInfo.getScore());
                this.mongoTemplate.save(applyGoods);
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoodsService.applyUsed");
            map.put("userInfo", userInfo.getId());
            map.put("id", id);
            new LogException(e,map);
            return ReturnData.ERROR;
        }
    }

    /**
     * 我的申请列表
     *
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

            Query query = Query.query(crt).limit(page_size).with(new Sort(Direction.DESC, "id"));

            List<ApplyGoodsUser> aguList = mongoTemplate.find(query, ApplyGoodsUser.class,"apply_goods_user");
            long total = mongoTemplate.count(new Query(Criteria.where("userId").is(userId).and("hidden").is(0)), ApplyGoodsUser.class);
            //后台更新状态
            return new ReturnListData(entityService.entityActionHandler2(aguList,"apply_goods2"),total);
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoods2Service.myApplyGoodsLists");
            map.put("userId", userId);
            map.put("start_id", start_id);
            map.put("page_size", page_size);
            new LogException(e,map);
            return ReturnListData.ERROR;
        }

    }

    /**
     * 设置用户中奖
     * @param state  1.中奖　　2.未提交试用报告的黑名单
     * @param id
     * @return
     */
    public ReturnData usedGoodsByUserIds(long id, String userIds, Integer state, String title, String content, String redirectType, Integer newType) {
        try{
            //查找活动
            EntityBase uais = this.getEntityById("apply_goods2", id);
            if(uais==null) {
                return EntityBase.ENTITY_HIDDEN;
            }
            String users[]=userIds.split(",");
            List<Long> receiverIds = new ArrayList<Long>();
            for(int i=0;i<users.length;i++) {
                Long uid= Long.parseLong(users[i]);
                receiverIds.add(uid);

            }
            Criteria crt= Criteria.where("userId").in(receiverIds).and("entityId").is(id);
            Query query = Query.query(crt);
            if(state == 1){
                //设置中奖
                this.mongoTemplate.updateMulti(query, new Update().set("state", 2), ApplyGoodsUser.class);
                //设置中奖人数
                Long total = mongoTemplate.count(Query.query(Criteria.where("entityId").is(id).and("state").is(2)), ApplyGoodsUser.class);
                this.mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), new Update().set("prizeNum", total), EntityApplyGoods2.class);
            }else if(state == 2){
                //设置未提交试用报告的黑名单
//					HashMap<String, Integer> map = new HashMap<>();
//					map.put("noUserPart", 1);
//					this.mongoTemplate.updateFirst(query, new Update().push("blackList", map), ApplyGoodsUser.class);
            }

            if(null != content && null != redirectType) {
                // 发送消息
                String params = "";
                String page = "";
				CommenMeta.MessageStatus msgDesc= CommenMeta.MessageStatus.getStatusByKey("msg-xxj-mange");
                long replyUserId = ConfUtils.getResourceNum("mangeUserId");
                //messageService.sendMsgByXxj(replyUserId, userIds, title, content, redirectType, page, params, newType);
                messageService.sendSynMessage(replyUserId, receiverIds, msgDesc.getType(), msgDesc.getDescription(), title, content, redirectType, page, params, newType);
                messageService.addXXJMsg(receiverIds,1);
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoods2Service.usedGoodsByUserIds");
            map.put("id", id);
            map.put("userIds", userIds);
            map.put("title", title);
            map.put("content", content);
            map.put("redirect_type", redirectType);
            map.put("newType", newType);
            new LogException(e,map);
            return ReturnData.ERROR;
        }
    }


    /**
     * 添加活动
     * @param entityApplyGoods
     * @return
     */
    public ReturnData addEntityApplyGoods(EntityApplyGoods2 entityApplyGoods) {
        try{
            if(null!=entityApplyGoods.getStartTime() && null!=entityApplyGoods.getLastTime()){
                int currentTimeStamp = DateUtils.nowInSeconds();
                long date=DateUtils.nowInSeconds();
                if(entityApplyGoods.getStartTime().intValue()>entityApplyGoods.getLastTime().intValue()){
                    return new ReturnData(-6,"开始时间必须小于结束时间");
                }//else if(entityApplyGoods.getLastTime().intValue()<date){
                //return new ReturnData(-6,"结束时间必须大于当前时间");
                else if(entityApplyGoods.getStartTime().intValue()>date && entityApplyGoods.getStartTime().intValue()<entityApplyGoods.getLastTime().intValue()){
                    entityApplyGoods.setActiveState(1);
                }

                if(null == entityApplyGoods.getPublishTime()){
                    //未提交发布时间，默认设置为当前提交时间
                    entityApplyGoods.setPublishTime(currentTimeStamp);
                }
                //获取mongo自增长id
                String tname="entity_apply_goods2";
                this.save(tname, entityApplyGoods);
                //清理列表的缓存
                cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_APPGOODS_LIST_PREFIX));

                return new ReturnData(entityApplyGoods);
            }
            return new ReturnData(-1, "请输入开始和结束时间");
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoods2Service.addEntityApplyGoods");
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
            new LogException(e,map);
            return ReturnData.ERROR;
        }
    }

    /**
     * 修改福利试用
     * @param entityApplyGoods
     * @return
     */
    public ReturnData updateEntityApplyGoods(EntityApplyGoods2 entityApplyGoods) {
        try{
            Update update=new Update();
            String tname="entity_apply_goods2";
            if(!StringUtils.isBlank(entityApplyGoods.getTitle())) {
                update.set("title", entityApplyGoods.getTitle());
            }
            if(!StringUtils.isBlank(entityApplyGoods.getTagIds())) {
                update.set("tagIds", entityApplyGoods.getTagIds());
            }
            if(!StringUtils.isBlank(entityApplyGoods.getImage())) {
                update.set("image", entityApplyGoods.getImage());
            }
            if(entityApplyGoods.getHidden()!=null) {
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
            if(null!=entityApplyGoods.getPublishTime()){
                update.set("publishTime", entityApplyGoods.getPublishTime());
            }
            if(null != entityApplyGoods.getPrice()){
                update.set("price", entityApplyGoods.getPrice());
            }
            if(null != entityApplyGoods.getShareState()){
                update.set("shareState", entityApplyGoods.getShareState());
            }
            if(null != entityApplyGoods.getApplyEndTime()){
                update.set("applyEndTime", entityApplyGoods.getApplyEndTime());
            }
            if(null != entityApplyGoods.getPrizeEndTime()){
                update.set("prizeEndTime", entityApplyGoods.getPrizeEndTime());
            }
            if(null != entityApplyGoods.getLastUserPartTime()){
                update.set("lastUserPartTime", entityApplyGoods.getLastUserPartTime());
            }
            if(null!=entityApplyGoods.getActiveState()){
                //活动开始之后可以手动修改人工修改
                update.set("activeState", entityApplyGoods.getActiveState());
//				if(entityApplyGoods.getActiveState()==3) {
//					//活动结束之后修改没有中奖的人状态
//					this.mongoTemplate.updateMulti((Query.query(Criteria.where("entityId").is(entityApplyGoods.getId()).and("state").is(1))), new Update().set("state", 3), ApplyGoodsUser.class);
//				}

            }
            if(null !=entityApplyGoods.getDescp()){
                update.set("descp", entityApplyGoods.getDescp());
            }


            if(!StringUtils.isBlank(entityApplyGoods.getGoodsIds())) {
                update.set("goodsIds", entityApplyGoods.getGoodsIds());
            }
            if(null!=entityApplyGoods.getType()) {
                update.set("type", entityApplyGoods.getType());
            }

            update.set("updateStamp", DateUtils.nowInSeconds());
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
            new LogException(e,map);
            return ReturnData.ERROR;
        }

    }

    /**
     * 根据state,福利社参与申请的用户信息
     * @param id: 申请的活动id
     * @param state:* 1 申请中 ----活动正在进行中
     * 2 中奖了 		活动已结束
     * 3 结束
     @param userPartState 1 表示写过试用报告
     */
    public ReturnListData findApplyUserList(Long id,
                                            Long userId,
                                            Integer state,
                                            Integer userPartState,
                                            int hasContent,
                                            int hasPrized,
                                            int prizedTime,
                                            int minScore,
                                            int maxScore,
                                            int pager,
                                            int pageSize) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        try{
            Criteria crt= Criteria.where("entityId").is(id);
            Query query = Query.query(crt).skip((pager-1)*pageSize).limit(pageSize).with(new Sort(Direction.DESC, "id"));
            if(state>0) {
                crt.and("state").is( state);
            }

            if(userPartState!=null) {
                crt.and("userPartState").is(userPartState);
            }

            ArrayList<Long> userIds = new ArrayList<Long>();
            if (userId != null){
                userIds.add(userId);
            }

            if(hasPrized == 1 && prizedTime >0){
                //近期未中奖
                int recentTimeStamp = DateUtils.nowInSeconds() - prizedTime;
                List<ApplyGoodsUser> recentApplyGoodsUserList = mongoTemplate.find(
                        new Query(Criteria.where("updateStamp").gte(recentTimeStamp).and("state").is(3)),
                        ApplyGoodsUser.class);
                List<ApplyGoodsUser> applyGoodsUserList = mongoTemplate.find(
                        new Query(Criteria.where("entityId").is(id).and("state").is(1)),
                        ApplyGoodsUser.class);
                ArrayList<Long> recentNotPrizedUserId = new ArrayList<Long>();
                ArrayList<Long> notPrizedUserId = new ArrayList<Long>();
                for(ApplyGoodsUser applyGoodsUser:recentApplyGoodsUserList){
                    recentNotPrizedUserId.add(applyGoodsUser.getUserId());
                }
                for(ApplyGoodsUser applyGoodsUser:applyGoodsUserList){
                    notPrizedUserId.add(applyGoodsUser.getUserId());
                }
                notPrizedUserId.retainAll(recentNotPrizedUserId);
                if(notPrizedUserId.size()>0){
                    userIds.addAll(notPrizedUserId);
                }else{
                    //无交集,返回空
                    return new ReturnListData(new ArrayList(), 0);
                }
            }

            //根据修行值筛选
            if(minScore!=0 || maxScore!=0) {
	            if (minScore >= maxScore) {
	                return new ReturnListData(-1, "修行值区间错误");
	            } else if (minScore >= 0 && maxScore > 0) {
	            	crt.and("score").gte(minScore).lte(maxScore);
	            	query.with(new Sort(Direction.DESC, "score"));
	            } else if (minScore > 0 && maxScore == 0) {
	            	crt.and("score").gte(minScore);
	            	query.with(new Sort(Direction.DESC, "score"));
                } 
            }

            if(userIds.size()>0){
                crt.and("userId").in(userIds);
            }

            List<ApplyGoodsUser> agus=this.mongoTemplate.find(query , ApplyGoodsUser.class, "apply_goods_user");
            long count=this.mongoTemplate.count(query , ApplyGoodsUser.class);
            //获取集合中对象的某个属性的集合
            Collection<Long> peoplesCities = CollectionUtils.collect( agus, new BeanToPropertyValueTransformer("userId") );
            List<Long> ls=new ArrayList<Long>(peoplesCities);
            List<SmartUserInfo> userser=userService.findSmartUserInfoByIds2(ls);
            
            List<Long> entityIds=new ArrayList<Long>();
            //获取用户信息
            for(int i=0;i<agus.size();i++) {
                for(int j=0;j<userser.size();j++) {
                    if(userser.get(j).getId().equals(agus.get(i).getUserId())) {
                        agus.get(i).setSmartUserInfo(userser.get(j));
                        break;
                    }
                }
                entityIds.add(agus.get(i).getEntityId());
            }
            //查询申请理由
            List<Comment> cmtList=mongoTemplate.find(new Query(Criteria.where("entityId").in(entityIds).and("reason").is(1).and("hidden").is(0)), Comment.class,"entity_comment_apply_goods2");
            List<ApplyGoodsUser> newAgus=new ArrayList<ApplyGoodsUser>();
            for(int i=0;i<agus.size();i++) {
            	ApplyGoodsUser agu=agus.get(i);
            	boolean find=false;
            	for(int j=0;!find && j<cmtList.size();j++) {
            		Comment cmt=cmtList.get(j);
            		if(agu.getEntityId()==cmt.getEntityId().longValue() && agu.getUserId()==cmt.getUserId().longValue()){
            			agu.setContent(cmt.getContent());
            			find=true;
            			if(null!=cmt.getImages() && cmt.getImages().length>0){
                			agu.setImages(Arrays.asList(cmt.getImages()));
            			}
            			newAgus.add(agu);
            		}
            	}
            }
            if(hasContent == 1){
            	return new ReturnListData(newAgus,count);
            }
            
            return new ReturnListData(agus,count);
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoodsService.findApplyUserList");
            map.put("id", id);
            map.put("state", state);
            map.put("pager", pager);
            map.put("pageSize", pageSize);
            new LogException(e,map);
            return ReturnListData.ERROR;
        }

    }

    /**
     *
     * @param userId 用户id
     * @param id 试用id
     * @param content 试用内容
     * @param images 图片
     * @return
     */
    public ReturnData applyReason(long userId, Long id, String content, String images, Boolean isvid) {
        //内容过滤
        String key = "notkeyword";
        if(isvid){
            String value = entityService.getConfig(key);
            if (!StringUtils.isBlank(value)) {
                content = entityService.keywordInfiltration2(content, value);
            }
        }

        List<String> lists=new ArrayList();
        if(StringUtils.isNotBlank(images)){
            //图片路径处理,只保留图片名
            images= CommonUtils.getImages(images);
            String[] imagess=images.split(",");
            lists= Arrays.asList(StringUtils.split(images,","));
        }

        Criteria crt= Criteria.where("entityId").is(id).and("userId").is(userId);
        Query query = Query.query(crt);
        this.mongoTemplate.updateFirst(query, new Update().set("content", content).set("images", lists), ApplyGoodsUser.class);
        return ReturnData.SUCCESS;
    }

    /**
     * 设置快递信息
     * @param id
     * @param express
     * @param number
     * @return
     */
    public ReturnData expressSetting(Long id, String express, String number) {
        try{
            mongoTemplate.findAndModify(
                    new Query(Criteria.where("id").is(id)),
                    new Update().set("express", express).set("expressNumber", number),
                    ApplyGoodsUser.class);
            return ReturnData.SUCCESS;
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoodsService.expressSetting");
            map.put("id", id);
            map.put("express", express);
            map.put("number", number);
            new LogException(e,map);
            return ReturnData.ERROR;
        }
    }

    /**
     * 同步用户地址
     * @param id
     * @return
     */
    public ReturnData syncUserDesc(Long id) {
        try{
            //查找中奖用户id
            List<ApplyGoodsUser> applyGoodsUserList = mongoTemplate.find(
                    new Query(Criteria.where("entityId").is(id).and("state").is(2)),
                    ApplyGoodsUser.class
            );
            Collection<Long> userIds = CollectionUtils.collect( applyGoodsUserList, new BeanToPropertyValueTransformer("id") );
            List<UserInfo> userInfoList = mongoTemplate.find(
                    new Query(Criteria.where("id").in(userIds)),
                    UserInfo.class
            );
            for(ApplyGoodsUser applyGoodsUser:applyGoodsUserList){
                for(UserInfo userInfo:userInfoList){
                    if(applyGoodsUser.getUserId().equals(userInfo.getId())){
                        String userDesc=
                                userInfo.getUserAddressInfos()[0].getProvince() +
                                        userInfo.getUserAddressInfos()[0].getCity() +
                                        userInfo.getUserAddressInfos()[0].getDistrict() +
                                        userInfo.getUserAddressInfos()[0].getDetail() + "," +
                                        userInfo.getUserAddressInfos()[0].getZip() + "," +
                                        userInfo.getUserAddressInfos()[0].getPhone();
                        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())),
                                new Update().set("userDesc", userDesc),"apply_goods_user");
                        break;
                    }
                }
            }
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoodsService.syncUserDesc");
            map.put("id", id);
            new LogException(e,map);
            return ReturnData.ERROR;
        }
        return null;
    }

    /**
     * 中奖结束同步用户状态
     * @return
     */
    public ReturnData syncState() {
        try {
            //读取未同步并且已开奖的福利
            List<EntityApplyGoods2> entityApplyGoodsList = mongoTemplate.find(
                    new Query(Criteria.where("isSync").ne(1)),
                    EntityApplyGoods2.class
            );
            if(entityApplyGoodsList.size() >0 ) {
                //活动结束或者筛选结束
                int[] syncActionState = {3,5};
                for(EntityApplyGoods2 entityApplyGoods:entityApplyGoodsList) {
                    if(ArrayUtils.contains(syncActionState, entityApplyGoods.getActiveState())) {
                        //更新未中奖的用户
                        mongoTemplate.updateMulti(new Query(Criteria.where("entityId").is(entityApplyGoods.getId()).and("state").is(1)),
                                new Update().set("state", 3),
                                ApplyGoodsUser.class
                        );
                        //设置以更改状态的福利社
                        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(entityApplyGoods.getId())),
                                new Update().set("isSync", 1),
                                EntityApplyGoods2.class
                        );
                    }
                }
            }
            return ReturnData.SUCCESS;
        }catch (Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoods2Service.syncState");
            new LogException(e,map);
            return ReturnData.ERROR;
        }
    }


}
