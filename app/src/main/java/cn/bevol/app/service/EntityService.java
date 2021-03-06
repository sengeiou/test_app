package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.*;
import cn.bevol.app.entity.constant.CommenMeta;
import cn.bevol.app.entity.constant.api.EntityMeta;
import cn.bevol.app.entity.vo.GoodsExplain;
import cn.bevol.app.entity.vo.SmartUserInfo;
import cn.bevol.app.entity.vo.UserEntityAction;
import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.entity.model.Find;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.app.entity.model.Lists;
import cn.bevol.model.entity.*;
import cn.bevol.model.entityAction.*;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.Log.LogMethod;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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

import javax.annotation.Resource;
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
    RedisCacheProvider cacheProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private CompositionService compositionService;

    @Autowired
    private EntityFindOldMapper findMapper;

    @Autowired
    private RecoveryOldMapper recoveryMapper;


    @Autowired
    private FindOldMapper newfindMapper;

    @Autowired
    private GoodsOldMapper goodsMapper;

    @Autowired
    private CompositionOldMapper compositionMapper;

    @Resource
    private HotListService hotListService;

    @Resource
    private UserPartService userPartService;

    @Resource
    private FindService findService;

    @Autowired
    private ListsOldMapper listsMapper;
    @Autowired
    private GoodsExtOldMapper goodsExtMapper;

    @Autowired
    CacheService cacheService;


    private EntityBase checkEntity(String tname,long id) {
        EntityBase map=new EntityBase();
        if(tname.indexOf("find")!=-1){
            Find find=newfindMapper.getById(id);
            if(find==null) return map;
            EntityFind ent = new EntityFind();

            ent.setId(find.getId());
            ent.setTitle(find.getTitle());
            ent.setHidden(find.getHidden());
            ent.setDeleted(find.getDeleted());
            ent.setImage(find.getImage());
            ent.setUpdateStamp(find.getUpdateStamp());
            ent.setCreateStamp(find.getCreateStamp());
            ent.setHeaderImage(find.getHeaderImage());
            ent.setPcImage(find.getPcImage());
            map=ent;
        }else if(tname.indexOf("goods")!=-1) {
            Goods goods=goodsMapper.getById(id);
            if(goods==null) return map;
            map = new EntityBase();
            map.setId(goods.getId());
            map.setTitle(goods.getTitle());
            map.setMid(goods.getMid());
            map.setImage(goods.getImage());
            map.setUpdateStamp(goods.getUpdateStamp());
            map.setCreateStamp(goods.getCreateStamp());
        } else  if(tname.indexOf("lists")!=-1) {
            Lists lt=listsMapper.getById(id);
            if(lt==null) return map;
            EntityLists ent = new EntityLists();
            ent.setId(lt.getId());
            ent.setTitle(lt.getTitle());
            ent.setHidden(lt.getHidden());
            ent.setDeleted(lt.getDeleted());
            ent.setImage(lt.getImage());
            ent.setUpdateStamp(lt.getUpdateStamp());
            ent.setCreateStamp(lt.getCreateStamp());
            ent.setMiniImage(lt.getMiniImage());
            map=ent;
        } else  if(tname.indexOf("composition")!=-1) {
            Composition cp=compositionMapper.getById(id);
            if(cp==null) return map;
            map = new EntityBase();
            map.setId(cp.getId());
            map.setTitle(cp.getTitle());
            map.setUpdateStamp(cp.getUpdateStamp());
            map.setCreateStamp(cp.getCreateStamp());
            map.setMid(cp.getMid());
            map.setmPid(cp.getmPid());
        }
        if(map!=null) {
            mongoTemplate.save(map, tname);
        }
        return map;
    }


    /**
     * ??????????????????
     *
     * @param tname      ????????????
     * @param id         ?????????
     * @param userId ??????id
     * @return
     */
    @LogMethod
    public UserEntityAction entityInfo(String tname, long id, long userId) {
        //try {

        String entityTname = "entity_" + tname;
        Query query=new Query(Criteria.where("id").is(id));
        EntityBase map = mongoTemplate.findOne(query, EntityBase.class, entityTname);
        if (map == null) {
            map= checkEntity(entityTname,id);

        }
        UserEntityAction uea = null;
        if (userId > 0) {
            uea = userEntityAction(tname, id, userId);
            uea.setCollectionNum(map.getCollectionNum());
            uea.setCommentNum(map.getCommentNum());
            uea.setLikeNum(map.getLikeNum());
            uea.setNotLikeNum(map.getNotLikeNum());
        } else {
            uea = new UserEntityAction();
        }

        // ????????????
        if (map.getAllowComment() != null && map.getAllowComment() == 1) {
            return null;
        }

        //tname  ??????
        this.objectIncById(id, entityTname, "hitNum", 1);
        return uea;
        // } catch (Exception e) {
        //Map map=new HashMap();
        //	map.put("tname", tname);
        //	map.put("id", id);
        //	map.put("userId", userId);
        //	map.put("method", "EntityService.entityInfo");
        //   	new LogException(e,map);
        //   }
        // return new UserEntityAction();
    }









    /**
     * ??????
     *
     * @param tname  ??????
     * @param entityId     ??????id
     * @param userInfo ????????????
     * @param type   1?????? 2????????????
     */
    public ReturnData actionCollection(String tname, long entityId, UserInfo userInfo, int type) {
        long userId=0L;
        String skin="";
        String skinResults="";

        if(userInfo == null || userInfo.getId() == null || userInfo.getId() <= 0) {
            return ReturnData.OPERATION_NOT_PERMIT;
        }

        try {
            EntityBase map=this.getEntityById(tname, entityId);
            if (map.getAllowComment() != null && map.getAllowComment() == 1) {
                return  EntityBase.ENTITY_HIDDEN;
            }

            userId=userInfo.getId();
            if(!org.apache.commons.lang.StringUtils.isBlank(userInfo.getResult())) {
                skin=userInfo.getResult();
            }
            if(!org.apache.commons.lang.StringUtils.isBlank(userInfo.getSkinResults())) {
                skinResults=userInfo.getSkinResults();
            }
            //???????????????????????????
            //??????????????????
            String actionType = "entity_collection_" + tname;
            //???????????????
            cn.bevol.model.entityAction.Collection cmt = mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("entityId").is(entityId)), cn.bevol.model.entityAction.Collection.class, actionType);
            String entityTname = "entity_" + tname;
            if (cmt == null & type == 1) {
                cmt = new cn.bevol.model.entityAction.Collection();
                Long cmid = this.getId(actionType);
                cmt.setId(cmid);
                cmt.setEntityId(entityId);
                cmt.setUserId(userId);
                cmt.setSkin(skin);
                cmt.setSkinResults(skinResults);
                mongoTemplate.save(cmt, actionType);
                this.objectIncById(entityId, entityTname, "collectionNum", 1);
                //????????????
                this.actionLike2(tname, entityId, userInfo, 1);
            } else if (cmt != null && type == 2) {
                mongoTemplate.findAndRemove(new Query(Criteria.where("userId").is(userId).and("entityId").is(entityId)), cn.bevol.model.entityAction.Collection.class, actionType);
                this.objectIncById(entityId, entityTname, "collectionNum", -1);
            }
            return new ReturnData();
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("tname", tname);
            map.put("type", type);
            map.put("userId", userId);
            map.put("method", "EntityService.actionCollection");
            new LogException(e,map);
        }
        return ReturnData.ERROR;
    }

    /**
     * ????????????
     * ????????????????????????????????????????????????
     * collectionCompositionSort
     *
     */
    public List<Composition> collectionCompositionSort() {
        try {
            String tname="composition";
            String actionType = "entity_collection_" + tname;
            /**
             db.entity_collection_composition.aggregate([{$group:{_id:
             "$entityId",count:{$sum:1}}},{$sort:{count:-1}},{$limit:10}])
             */
            String groupStr = "{$group:{_id:'$entityId',count:{$sum:1}}}";
            DBObject group = (DBObject) JSON.parse(groupStr);
            /*String matchStr = "{$match:{type:1}}";
            DBObject match = (DBObject) JSON.parse(matchStr);*/
            String sortStr = "{$sort:{count:-1}}";
            DBObject sort = (DBObject) JSON.parse(sortStr);
            String limitStr = "{$limit:10}";
            DBObject limit = (DBObject) JSON.parse(limitStr);
            //????????????????????????????????????????????????
            AggregationOutput output = mongoTemplate.getCollection(actionType).aggregate(group,sort,limit);

            String str="";
            StringBuffer sb=new StringBuffer(str);
            //????????????id
            if(null!=output){
                for( Iterator<DBObject> it = output.results().iterator(); it.hasNext(); ){
                    BasicDBObject dbo = (BasicDBObject) it.next();
                    Integer idd = (Integer)dbo.get("_id");
                    str=idd+",";
                    sb=sb.append(str);
                }
            }

            StringBuffer sb2 = new StringBuffer();
            //????????????

            String sb3=sb.toString();
            String[] ids = sb3.split(",");
            for (int i = 0; i < ids.length; i++) {
                Long n = Long.parseLong(ids[i]);
                if(n!=null)
                    sb2.append("," + n);
            }

            String compositionIds=sb2.substring(1);
            //??????mysql
            List<Composition> clist=compositionService.getCompositionByIds(compositionIds);

            return clist;
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.collectionCompositionSort");
            new LogException(e,map);
        }
        return null;
    }

    /**
     * ??????????????????
     * collectionCompositionSort
     *
     */
    public List<Composition> notLikeCompositionSort() {
        try {
            String tname="composition";
            String actionType = "entity_like_" + tname;
            //Criteria.where("type").is(1)
            /**
             db.entity_collection_composition.aggregate([{$group:{_id:
             "$entityId",count:{$sum:1}}},{$sort:{count:-1}},{$limit:10}])
             */
            // String groupStr = "{{$group:{_id:'$entityId',count:{$sum:1}},{$sort:{count:-1}},{$limit:10}}";
            String groupStr = "{$group:{_id:'$entityId',count:{$sum:1}}}";
            DBObject group = (DBObject) JSON.parse(groupStr);
            String matchStr = "{$match:{type:2}}";
            DBObject match = (DBObject) JSON.parse(matchStr);
            String sortStr = "{$sort:{count:-1}}";
            DBObject sort = (DBObject) JSON.parse(sortStr);
            String limitStr = "{$limit:10}";
            DBObject limit = (DBObject) JSON.parse(limitStr);
            AggregationOutput output = mongoTemplate.getCollection(actionType).aggregate(match,group,sort,limit);

            String str="";
            StringBuffer sb=new StringBuffer(str);
            for( Iterator<DBObject> it = output.results().iterator(); it.hasNext(); ){
                BasicDBObject dbo = (BasicDBObject) it.next();
                //Composition cms=(Composition)dbo;
                long idd = (Long)dbo.get("_id");
                str=idd+",";
                sb=sb.append(str);
            }
            StringBuffer sb2 = new StringBuffer();
            //????????????

            String sb3=sb.toString();
            String[] ids = sb3.split(",");
            for (int i = 0; i < ids.length; i++) {
                Long n = Long.parseLong(ids[i]);
                if(n!=null)
                    sb2.append("," + n);
            }

            String compositionIds=sb2.substring(1);
            List<Composition> clist=compositionService.getCompositionByIds(compositionIds);

            return clist;
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.notLikeCompositionSort");
            new LogException(e,map);
        }
        return null;
    }


    /**
     * ??????
     *
     * @param tname    ??????
     * @param entityId ??????entityId
     * @param userInfo   ????????????
     * @param type
     */
    public ReturnData actionLike(String tname, long entityId, UserInfo userInfo, int type) {

        //???????????????????????????
        //??????????????????
        long userId=0L;
        String skin="";
        String skinResults="";
        try {
            EntityBase map=this.getEntityById(tname, entityId);
            if (map.getAllowComment() != null && map.getAllowComment() == 1) {
                return  EntityBase.ENTITY_HIDDEN;
            }

            userId=userInfo.getId();
            if(!org.apache.commons.lang.StringUtils.isBlank(userInfo.getResult())) {
                skin=userInfo.getResult();
            }
            if(!org.apache.commons.lang.StringUtils.isBlank(userInfo.getSkinResults())) {
                skinResults=userInfo.getSkinResults();
            }
            String actionType = CommenMeta.COLLTION_LIKE + tname;
            //???????????????
            Like cmt = mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("entityId").is(entityId)), Like.class, actionType);
            if (cmt == null) {
                cmt = new Like();
                Long cmid = this.getId(actionType);
                cmt.setId(cmid);
                cmt.setEntityId(entityId);
                cmt.setUserId(userId);
                cmt.setType(type);
                cmt.setSkin(skin);
                cmt.setSkinResults(skinResults);
                String entityTname = "entity_" + tname;
                if (type == 1) {
                    this.objectIncById(entityId, entityTname, "likeNum", 1);
                } else if (type == 2) {
                    this.objectIncById(entityId, entityTname, "notLikeNum", 1);
                }
                mongoTemplate.save(cmt, actionType);
            }
            return new ReturnData();
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.actionLike");
            map.put("tname", tname);
            map.put("type", type);
            map.put("userId", userId);
            map.put("entityId", entityId);
            map.put("userId", userInfo.getId());
            new LogException(e,map);
        }
        return ReturnData.ERROR;

    }


    /**
     * ??????????????????
     * @param tname
     * @param ids ??????id
     * @param userInfo
     * @return
     */
    public ReturnData cancelLike(String tname, String ids, UserInfo userInfo) {
        if(org.apache.commons.lang.StringUtils.isNotBlank(ids)) {
            String idss[]=ids.split(",");
            for(int i=0;i<idss.length;i++) {
                if(org.apache.commons.lang.StringUtils.isNotBlank(idss[i])) {
                    this.actionLike2(tname, Long.parseLong(idss[i]), userInfo, 0);
                }
            }
            return ReturnData.SUCCESS;
        }
        return ReturnData.ERROR;
    }
    /**
     * v2.9
     * @param type	1?????? 2?????? 0??????
     * @param tname    ??????
     * @param entityId ??????entityId
     */
    public ReturnData actionLike2(String tname, long entityId, UserInfo userInfo, int type) {
        // ???????????????????????????
        // ??????????????????
        long userId = 0L;
        String skin = "";
        String skinResults = "";
        try {

            EntityBase map = this.getEntityById(tname, entityId);
            if (map.getAllowComment() != null && map.getAllowComment() == 1) {
                return EntityBase.ENTITY_NOT_ALLOW_LIKE;
            }

            userId = userInfo.getId();
            if (!org.apache.commons.lang.StringUtils.isBlank(userInfo.getResult())) {
                skin = userInfo.getResult();
            }
            if (!org.apache.commons.lang.StringUtils.isBlank(userInfo.getSkinResults())) {
                skinResults = userInfo.getSkinResults();
            }
            String actionType = CommenMeta.COLLTION_LIKE + tname;
            // ???????????????
            Like cmt = mongoTemplate.findOne(
                    new Query(Criteria.where("userId").is(userId).and("entityId").is(entityId)), Like.class,
                    actionType);
            String entityTname = "entity_" + tname;
            // ?????????????????????/??????
            if (cmt == null) {
                cmt = new Like();
                Long cmid = this.getId(actionType);
                cmt.setId(cmid);
                cmt.setEntityId(entityId);
                cmt.setUserId(userId);
                cmt.setType(type);
                cmt.setSkin(skin);
                cmt.setSkinResults(skinResults);
                if (type == 1) {
                    this.objectIncById(entityId, entityTname, "likeNum", 1);
                } else if (type == 2) {
                    this.objectIncById(entityId, entityTname, "notLikeNum", 1);
                }
                mongoTemplate.save(cmt, actionType);
            } else { // ??????/??????--?????? ????????????/??????--type=0,?????????????????????
                String types = "1,2,0";
                if (("," + types + ",").contains("," + type + ",")) {
                    // ???????????????/?????? ????????????/??????
                    if (type == 0) {
                        // ????????????0?????????????????????0 ??????+-1
                        // ??????????????? ????????????
                        if (cmt.getType() == 1) {
                            cmt.setType(type);
                            this.objectIncById(entityId, entityTname, "likeNum", -1);
                            // ???????????????
                        } else if (cmt.getType() == 2) {
                            cmt.setType(type);
                            this.objectIncById(entityId, entityTname, "notLikeNum", -1);
                        }
                    } else if (type == 1) {
                        // ????????????
                        if (cmt.getType() == 0) {
                            cmt.setType(type);
                            this.objectIncById(entityId, entityTname, "likeNum", 1);
                            // ??????-1?????????+1
                        } else if (cmt.getType() == 2) {
                            cmt.setType(type);
                            this.objectIncById(entityId, entityTname, "likeNum", 1);
                            this.objectIncById(entityId, entityTname, "notLikeNum", -1);
                        }
                    } else if (type == 2) {
                        // ????????????
                        if (cmt.getType() == 0) {
                            cmt.setType(type);
                            this.objectIncById(entityId, entityTname, "notLikeNum", 1);
                            // ??????-1?????????+1
                        } else if (cmt.getType() == 1) {
                            cmt.setType(type);
                            this.objectIncById(entityId, entityTname, "likeNum", -1);
                            this.objectIncById(entityId, entityTname, "notLikeNum", +1);
                        }
                    }
                    mongoTemplate.updateFirst(
                            new Query(Criteria.where("entityId").is(entityId).and("userId").is(userInfo.getId())),
                            new Update().set("type", cmt.getType()).set("vistTime", new Date().getTime() / 1000),
                            actionType);
                }
            }
            return new ReturnData();
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.actionLike2");
            map.put("tname", tname);
            map.put("type", type);
            map.put("userId", userId);
            map.put("entityId", entityId);
            map.put("userId", userInfo.getId());
            new LogException(e,map);
        }
        return ReturnData.ERROR;

    }





    /**
     * ??????????????????????????????
     *
     * @param tname  ????????????
     * @param id     ??????id
     * @param userid ??????id
     * @return ?????? ????????????????????????
     */
    public UserEntityAction userEntityAction(String tname, long id, long userid) {
        UserEntityAction uea = new UserEntityAction();
        Comment eb = (Comment) this.findActionEntity(tname, "comment", id, userid, Comment.class);
        //?????????
        if (eb != null) {
            if(!org.apache.commons.lang.StringUtils.isBlank(eb.getContent())) {
                //???????????? ???????????????
                uea.setIsComment(1);
                uea.setCommentState(2);
            } else {
                //???????????? ??????????????????
                uea.setCommentState(1);
                uea.setScore(eb.getScore());
            }
            //
            uea.setCommentId(eb.getId());
        } else {
            //???????????????
            uea.setCommentState(0);
        }

        //????????????
        Like like = (Like) this.findActionEntity(tname, "like2", id, userid, Like.class);
        //?????????
        if (like != null) {
            uea.setLike(like.getType());
        }

        //????????????
        cn.bevol.model.entityAction.Collection collection = (cn.bevol.model.entityAction.Collection) this.findActionEntity(tname, "collection", id, userid, cn.bevol.model.entityAction.Collection.class);
        //?????????
        if (collection != null) {
            uea.setIsCollection(1);
        }

        uea.setApply(applyState(id,userid));
        return uea;
    }

    public int applyState(long id,Long userid){
        int applyState=0;
        //apply_goods_user ??????id userId
        Query q=new Query(Criteria.where("entityId").is(id).and("userId").is(userid).and("hidden").is(0).and("deleted").is(0));
        ApplyGoodsUser agu = (ApplyGoodsUser) mongoTemplate.findOne(q, CommenMeta.getEntityClass("apply_goods_user"));

        //uea.setApply(agu.getState());

        if(null!=agu && null!=agu.getState()){
            applyState=agu.getState();
            //?????????
            if(agu.getState()==1){
                Query query=new Query(Criteria.where("id").is(id));
                //??????
                EntityApplyGoods2 eag = (EntityApplyGoods2) mongoTemplate.findOne(query, CommenMeta.getEntityClass("entity_apply_goods2"));
                if(null!=eag && null!=eag.getActiveState()){
                    //?????????
                    if(eag.getActiveState()==3){
                        //????????????
                        //uea.setApply(4);
                        applyState=4;
                    }
                }
            }
        }
        return applyState;
    }

    /**
     * ????????????????????????
     * @param id: ?????????id
     * @param userid: ??????id
     * @param agu: ???????????????
     * @return
     */
    public int applyState2(long id,long userid,ApplyGoodsUser agu){
        int applyState=0;
        //apply_goods_user ??????id userId
        //Query q=new Query(Criteria.where("entityId").is(id).and("userId").is(userid).and("hidden").is(0).and("deleted").is(0));
        //ApplyGoodsUser agu = mongoTemplate.findOne(q, CommenMeta.getEntityClass("apply_goods_user"));

        //uea.setApply(agu.getState());

        if(null!=agu && null!=agu.getState()){
            applyState=agu.getState();
            //?????????
            if(agu.getState()==1){
                Query query=new Query(Criteria.where("id").is(id));
                //??????
                EntityApplyGoods2 eag = (EntityApplyGoods2) mongoTemplate.findOne(query, CommenMeta.getEntityClass("entity_apply_goods2"));
                if(null!=eag && null!=eag.getActiveState()){
                    //?????????
                    if(eag.getActiveState()==3){
                        //????????????
                        //uea.setApply(4);
                        applyState=4;
                    }
                }
            }
        }
        return applyState;
    }

    /**
     * ????????????????????????
     *
     * @param tname      ????????????
     * @param actiontype ??????
     * @param entityId   ??????id
     * @param userId     ??????
     */
    public EntityActionBase findActionEntity(String tname, String actiontype, long entityId, long userId, Class clazz) {
        String tnameuser = "entity_" + actiontype + "_" + tname;

        Query query = Query.query(new Criteria().where("userId").is(userId).and("entityId").is(entityId).orOperator(Criteria.where("pid").is(0),Criteria.where("pid").exists(false)));

        EntityActionBase eb = (EntityActionBase) mongoTemplate.findOne(query, clazz, tnameuser);
        return eb;
    }

    /**
     * ????????????????????????
     *
     * @param tname
     * @param userId
     * @param startId
     * @param pageSize
     * @return
     */
    @Deprecated
    public Map findCollectionByUserId(String tname, long userId, long startId, int pageSize) {
        try {
            String actionType = "entity_collection_" + tname;
            if(pageSize>20){
                pageSize=20;
            }
            return findEntityActionType(actionType, tname, userId, cn.bevol.model.entityAction.Collection.class, startId, pageSize);
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.findCollectionByUserId");
            map.put("tname", tname);
            map.put("startId", startId);
            map.put("userId", userId);
            map.put("pageSize", pageSize);
            new LogException(e,map);
        }
        return null;
    }


    /**
     * ????????????????????????
     *
     * @param tname ??????
     * @param type ?????? 1?????? 2?????????
     * @param userId ??????id
     * @param startId
     * @param pageSize
     * @return
     */
    public Map findLikeByUserId(String tname,Integer type, long userId, long startId, int pageSize) {
        try {

            String actionType = CommenMeta.COLLTION_LIKE + tname;
            if(pageSize>20){
                pageSize=20;
            }
            //userId???type?????? ??????????????? ???????????? ??????????????????
            Criteria crt = new Criteria().where("userId").is(userId).and("type").is(type);
            Query query = Query.query(crt).limit(pageSize).with(new Sort(Direction.DESC, "id"));

            if (startId > 0) {
                crt.and("id").lt(startId);
            }
            Class clazz=Like.class;
            List<EntityActionBase> cms = mongoTemplate.find(query, clazz, actionType);
            long total = mongoTemplate.count(query, actionType);
            Map map = new HashMap();
            map.put("result", entityActionHandler(cms,tname));
            map.put("total", total);
            map.put("ret", 0);
            return map;
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.findCollectionByUserId");
            map.put("tname", tname);
            map.put("startId", startId);
            map.put("userId", userId);
            map.put("pageSize", pageSize);
            new LogException(e,map);
        }
        return null;
    }




    /**
     * ????????????????????????
     *
     * @param tname      ????????????
     * @param actionType ??????
     * @param clazz   ??????
     * @param userId     ??????
     */
    public Map findEntityActionType(String actionType, String tname, long userId, Class clazz, long startId, int pageSize) {
        Criteria crt = new Criteria().where("userId").is(userId).and("hidden").is(0);
        //pid???0 ?????? pid?????????  ?????????
        crt.orOperator(Criteria.where("pid").is(0),Criteria.where("pid").exists(false));
        Query query = Query.query(crt).limit(pageSize).with(new Sort(Direction.DESC, "id"));
        if (startId > 0) {
            crt.and("id").lt(startId);
        }
        //????????????????????????
        List<EntityActionBase> cms = mongoTemplate.find(query, clazz, actionType);
        long total = mongoTemplate.count(query, actionType);
        Map map = new HashMap();
        //??????????????????????????????
        map.put("result", entityActionHandler(cms,tname));
        map.put("total", total);
        map.put("ret", 0);
        return map;
    }


    private Class getEntityClassByTname(String tname) {
        if(tname.equals("goods")) {
            return EntityGoods.class;
        } else if(tname.equals("composition")) {
            return EntityComposition.class;
        } else if(tname.equals("find")) {
            return EntityFind.class;
        } else if(tname.equals("lists")) {
            return EntityLists.class;
        } else if(tname.equals("user_part_lists")) {
            return EntityUserPart.class;
        }
        return EntityBase.class;
    }
    /**
     * ????????????????????????
     * @param cms ??????????????????
     * @param tname ????????????
     * @return
     */
    public List<Map> entityActionHandler(List cms,String tname) {
        try{
            List<Map> maps = new ArrayList<Map>();
            if (cms != null) {
                //results.put("actions", cms);
                String tentityname = "entity_" + tname;
                List<Long> ls = new ArrayList<Long>();
                for (int i = 0; cms != null && i < cms.size(); i++) {
                    EntityActionBase base=(EntityActionBase) cms.get(i);
                    ls.add(base.getEntityId());
                }
                //????????????
                List<EntityBase> ebs = mongoTemplate.find(new Query(Criteria.where("id").in(ls).and("hidden").is(0)), EntityBase.class, tentityname);
                List<Map> listMap=new ArrayList();
                for (int i = 0; cms != null && i < cms.size(); i++) {
                    for (int j = 0; j < ebs.size(); j++) {
                        EntityActionBase base=(EntityActionBase) cms.get(i);
                        if (ebs.get(j).getId().equals(base.getEntityId())) {
                            Map map = new HashMap();
                            long userId=0;

                            //??????userId
                            if(tname.equals("find")){
                                //??????
                                Find find=this.getFindById(ebs.get(j).getId());
                                //????????????id??????????????????
                                if(null!=find && null!=find.getAuthorId() && find.getAuthorId()>0){
                                    userId=find.getAuthorId();
                                }
                            }else if(tname.equals("user_part_lists")){
                                //??????
                                ReturnData rd=userPartService.getUserPartById("lists",ebs.get(j).getId());
                                EntityUserPart eup=(EntityUserPart)rd.getResult();
                                if(null!=eup && null!=eup.getUserId()){
                                    userId=eup.getUserId();
                                }
                                //???????????????


                            }

                            //????????????id??????????????????
                            if(userId>0){
                                ReturnData rd=userService.getUserById(userId);
                                if(rd.getRet()==0){
                                    UserInfo userInfo=(UserInfo)rd.getResult();
                                    SmartUserInfo smartUserInfo=new SmartUserInfo();
                                    smartUserInfo.setHeadimgurl(userInfo.getHeadimgurl());
                                    smartUserInfo.setId(userInfo.getId());
                                    smartUserInfo.setNickname(userInfo.getNickname());
                                    base.setSmartUserInfo(smartUserInfo);
                                }
                            }


                            map.put("action", cms.get(i));

                            Goods goods=null;
                            //JSONObject jmap=null;
                            Map jmap=null;
                            //??????????????????
                            if(ebs.get(j)!=null&&ebs.get(j).getId()>0 ) {
                                ObjectMapper mapper = new ObjectMapper();
                                String json=mapper.writeValueAsString(ebs.get(j));
                                jmap=mapper.readValue(json, Map.class);


                                //jmap=JSONObject.fromObject(ebs.get(j));
                                if(!org.apache.commons.lang.StringUtils.isBlank(ebs.get(j).getTitle()) && tname.equals("goods")){
                                    goods=goodsMapper.getById(ebs.get(j).getId());
                                    mongoTemplate.updateFirst(new Query(new Criteria().where("id").is(ebs.get(j).getId())), new Update().set("title", goods.getTitle()).set("alias", goods.getAlias()), tentityname);
                                    ebs.get(j).setTitle(goods.getTitle());
                                }

                                //??????????????????
                                if(null!=goods){
                                    if(org.apache.commons.lang.StringUtils.isNotBlank(goods.getPrice())){
                                        jmap.put("price", goods.getPrice());
                                    }
                                    if(org.apache.commons.lang.StringUtils.isNotBlank(goods.getCapacity())){
                                        jmap.put("capacity", goods.getCapacity());
                                    }
                                }
                                if(null!=jmap && null!=jmap.get("commentNum")){
                                    jmap.put("commnet_num", jmap.get("commentNum"));
                                }
                                map.put("entity", jmap);
                            }
                            maps.add(map);
                        }
                    }
                }
            }
            return maps;
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "ApplyGoodsService.entityActionHandler");
            map.put("cms", cms.size());
            map.put("tname", tname);
            new LogException(e,map);
            return null;
        }
    }

    public List<Map> entityActionHandler2(List cms,String tname) {
        try{
            List<Map> maps = new ArrayList<Map>();
            if (cms != null) {
                String tentityname = "entity_" + tname;
                List ls = new ArrayList<Long>();
                for (int i = 0; cms != null && i < cms.size(); i++) {
                    EntityActionBase base=(EntityActionBase) cms.get(i);
                    if("compare_goods".equals(tname)){
                        ls.add(base.getSid());
                    }else{
                        ls.add(base.getEntityId());
                    }
                }
                //????????????
                List<EntityBase> ebs = new ArrayList();
                if(null!=ls && ls.size()>0){
                    Query query=null;
                    if("compare_goods".equals(tname)){
                        query=new Query(Criteria.where("sid").in(ls));
                    }else{
                        query=new Query(Criteria.where("id").in(ls));
                    }
                    ebs = mongoTemplate.find(query, EntityBase.class, tentityname);
                }


                List<Map> listMap=new ArrayList();
                boolean findFlag=false;

                for (int i = 0; cms != null && i < cms.size(); i++) {
                    findFlag=true;
                    for (int j = 0; j < ebs.size(); j++) {
                        EntityActionBase base=(EntityActionBase) cms.get(i);
                        boolean flag=false;
                        if("compare_goods".equals(tname)){
                            if(ebs.get(j).getSid().equals(base.getSid())){
                                flag=true;
                            }
                        }else{
                            if (ebs.get(j).getId()==base.getEntityId().intValue()) {
                                flag=true;
                            }
                        }
                        if (flag) {
                            Map map = new HashMap();
                            long userId=0;

                            //??????userId
                            if("find".equals(tname)){
                                //??????
                                Find find=this.getFindById(ebs.get(j).getId());
                                //????????????id??????????????????
                                if(null!=find && null!=find.getAuthorId() && find.getAuthorId()>0){
                                    userId=find.getAuthorId();
                                }
                            }else if("user_part_lists".equals(tname)){
                                //??????
                                ReturnData rd=userPartService.getUserPartById("lists",ebs.get(j).getId());
                                EntityUserPart eup=(EntityUserPart)rd.getResult();
                                if(null!=eup && null!=eup.getUserId()){
                                    userId=eup.getUserId();
                                }
                            }

                            //????????????id??????????????????
                            if(userId>0){
                                ReturnData rd=userService.getUserById(userId);
                                if(rd.getRet()==0){
                                    UserInfo userInfo=(UserInfo)rd.getResult();
                                    SmartUserInfo smartUserInfo=new SmartUserInfo();
                                    smartUserInfo.setHeadimgurl(userInfo.getHeadimgurl());
                                    smartUserInfo.setId(userInfo.getId());
                                    smartUserInfo.setNickname(userInfo.getNickname());
                                    base.setSmartUserInfo(smartUserInfo);
                                }
                            }

                            Map compareMap=null;
                            //????????????
                            if("compare_goods".equals(tname)){
                                EntityActionBase eab=(EntityActionBase)cms.get(i);
                                if(org.apache.commons.lang.StringUtils.isNotBlank(eab.getSid())&&eab.getObjList().size()==0){
                                    String[] ids=eab.getSid().split("_");
                                    for(int d=0;d<ids.length;d++){
                                        Goods goods=goodsMapper.getById(Long.parseLong(ids[d]));
                                        Map tempMap=new HashMap();
                                        if(null!=goods){
                                            tempMap.put("goodsTitle", goods.getTitle());
                                            tempMap.put("goodsImage", goods.getImageSrc());
                                            eab.getObjList().add(tempMap);
                                        }
                                    }
                                }
                            }

                            map.put("action", cms.get(i));

                            Goods goods=null;
                            //JSONObject jmap=null;
                            Map jmap=null;
                            //??????????????????
                            flag=false;
                            if("compare_goods".equals(tname)){
                                if(null!=ebs.get(j)&& org.apache.commons.lang.StringUtils.isNotBlank(ebs.get(j).getSid())){
                                    flag=true;
                                }
                            }
                            if(null!=ebs.get(j)&&null!=ebs.get(j).getId()&&ebs.get(j).getId()>0||flag) {
                                ObjectMapper mapper = new ObjectMapper();
                                String json=mapper.writeValueAsString(ebs.get(j));
                                jmap=mapper.readValue(json, Map.class);


                                //jmap=JSONObject.fromObject(ebs.get(j));
                                if(!org.apache.commons.lang.StringUtils.isBlank(ebs.get(j).getTitle()) && tname.equals("goods")){
                                    goods=goodsMapper.getById(ebs.get(j).getId());
                                    mongoTemplate.updateFirst(new Query(new Criteria().where("id").is(ebs.get(j).getId())), new Update().set("title", goods.getTitle()).set("alias", goods.getAlias()), tentityname);
                                    ebs.get(j).setTitle(goods.getTitle());
                                }

                                //??????????????????
                                if(null!=goods){
                                    if(org.apache.commons.lang.StringUtils.isNotBlank(goods.getPrice())){
                                        jmap.put("price", goods.getPrice());
                                    }
                                    if(org.apache.commons.lang.StringUtils.isNotBlank(goods.getCapacity())){
                                        jmap.put("capacity", goods.getCapacity());
                                    }
                                }
                                if(null!=jmap && null!=jmap.get("commentNum")){
                                    jmap.put("commnet_num", jmap.get("commentNum"));
                                }
                                map.put("entity", jmap);
                            }
                            maps.add(map);
                        }
                    }
                }
            }
            return maps;
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "EntityService.entityActionHandler2");
            map.put("cms", cms.size());
            map.put("tname", tname);
            new LogException(e,map);
            return null;
        }
    }


    /**
     * ??????????????????
     * ??????????????????
     * @param id: ??????id
     * @return
     */
    public Find getFindById(final Long id){ //// TODO: 17-7-10 ?????????????????????????????????
        return new CacheableTemplate<Find>(cacheProvider) {
            @Override
            protected Find getFromRepository() {
                try {
                    Find find = newfindMapper.getById(id);
                    return find;
                } catch (Exception e) {
                    Map map=new HashMap();
                    map.put("method", "EntityService.getFindById");
                    map.put("id", id);
                    new LogException(e,map);
                }
                return null;
            }
            @Override
            protected boolean canPutToCache(Find returnValue) {
                return (returnValue != null);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
                CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_FIND_ID_PREFIX,id+"")), true);
    }

    @Deprecated
    public boolean jubaoComment(String tname, long commentId) {
        try {
            String actionType = "entity_comment_" + tname;
            mongoTemplate.findAndModify(new Query(new Criteria().where("id").is(commentId)), new Update().set("isJubao", 1), Comment.class, actionType);
            return true;
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.jubaoComment");
            map.put("tname", tname);
            map.put("commentId", commentId);
            new LogException(e,map);
        }
        return false;
    }


    public Recovery saveRecovery(String tname, long entityId, long userId, String content) {
        Recovery rvy = new Recovery();
        rvy.setContent(content);
        rvy.setEntityId(entityId);
        rvy.setUserId(userId);
        rvy.setTname(tname);
        int i=recoveryMapper.save(rvy);
        if(i>0) {
            return rvy;
        }
        return null;
    }


    /**
     * ??????????????????(?????????,????????????)
     * @param tname: goods,find,lists,apply_goods,composition,user_part_lists
     * @param id: ??????id
     * @return
     */
    public ReturnData entityState(String tname, long id) {
        try {
            String entityTname = "entity_" + tname;
            Query query=new Query(Criteria.where("id").is(id));
            EntityBase map = mongoTemplate.findOne(query, EntityBase.class, entityTname);
            if(map==null) {
                EntityBase eb= checkEntity(entityTname,id);
                if(eb==null) {
                    return EntityBase.ENTITY_HIDDEN;
                } else {
                    map=eb;
                }
            }

            this.objectIncById(id, entityTname, "hitNum", 1);
            return new ReturnData(map);
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.entityState");
            map.put("tname", tname);
            map.put("id", id);
            new LogException(e,map);
        }
        return null;
    }

    /**
     * v3.2
     * @param tname
     * @param id
     * @return
     */
    public ReturnData entityStateByIdOrMid(String tname, Long id,String mid) {
        try {
            String entityTname = "entity_" + tname;
            Query query=new Query(Criteria.where("id").is(id));
            if(org.apache.commons.lang.StringUtils.isNotBlank(mid) && ("goods".equals(tname) || "composition".equals(tname))){
                query=new Query(Criteria.where("mid").is(mid));
            }
            query.fields().include("id");
            EntityBase map = mongoTemplate.findOne(query, EntityBase.class, entityTname);
            if(null==map) {
                return new ReturnData(0,"????????????");
            }

            this.objectIncById(id, entityTname, "hitNum", 1);
            return ReturnData.SUCCESS;
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.entityStateByIdOrMid");
            map.put("tname", tname);
            map.put("id", id);
            new LogException(e,map);
        }
        return null;
    }

    /**
     * ??????????????????(?????????,??????????????????)
     * ???????????????
     * @param tname: goods,find,lists,apply_goods,composition,user_part_lists
     * @param ids: ??????id
     * @return
     */
    public ReturnData entityState2(final String tname, final String ids) {
        if(org.apache.commons.lang.StringUtils.isBlank(ids)){
            return new ReturnData(-1,"ids??????");
        }
        return new CacheableTemplate<ReturnData>(cacheProvider){
            @Override
            protected ReturnData getFromRepository() {
                try {
                    String entityTname = "entity_" + tname;
                    String[] idss=ids.split(",");
                    List<Long> idList=new ArrayList();
                    for(String id:idss){
                        idList.add(Long.parseLong(id));
                    }
                    //id???long
                    Query query=new Query(Criteria.where("id").in(idList));
                    String fields[]=new String[]{"id","likeNum","notLikeNum","collectionNum","commentNum","commentContentNum","hitNum"};
                    setQueryFeilds(query,fields);
                    List<EntityBase> maps = mongoTemplate.find(query, EntityBase.class, entityTname);

                    return new ReturnData(maps);
                } catch (Exception e) {
                    Map map=new HashMap();
                    map.put("method", "EntityService.entityState2");
                    map.put("tname", tname);
                    map.put("ids", ids);
                    new LogException(e,map);
                }
                return null;
            }
            @Override
            protected boolean canPutToCache(ReturnData rd){
                return (null != rd && rd.getRet() == 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                        CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_ENITTY_STATE_PREFIX,tname,ids+""))
                ,true);

    }

    /**
     * ????????????
     * @param tname
     * @param ids
     * @return
     */
    public List<EntityBase> getStates(String tname, List<Long> ids) {
        try {
            String tnameuser = "entity_" + tname;
            List<EntityBase> map = mongoTemplate.find(new Query(Criteria.where("id").in(ids)), EntityBase.class, tnameuser);
            return map;
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.getStates");
            map.put("tname", tname);
            map.put("ids", ids);
            new LogException(e,map);
        }
        return null;
    }

    /**
     * ????????????????????????
     * @param tname
     * @param id
     * @param userId
     * @return
     */
    public UserEntityAction entityRelation(String tname, long id, long userId) {
        try {
            UserEntityAction uea = userEntityAction(tname, id, userId);
            return uea;
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "EntityService.entityRelation");
            map.put("tname", tname);
            map.put("id", id);
            map.put("userId", userId);
            new LogException(e,map);
        }
        return null;
    }

    @Deprecated
    public ReturnListData entityList(final String tname, final int type, final int pager, final int pageSize) {
        return new CacheableTemplate<ReturnListData>(cacheProvider) {
            @Override
            protected ReturnListData getFromRepository() {
                try {

                    long start = 0;
                    if (pager > 1) {
                        start = Long.valueOf((pager-1) * pageSize + "");
                    } else if (pager < 0) {
                        return null;
                    }

                    List<EntityFind> efs = findMapper.list(type, start, pageSize);
                    long total=0;
                    if(efs.size()>0) {
                        List<Long> flis=new ArrayList<Long>();
                        for(EntityFind f:efs) {
                            if(!org.apache.commons.lang.StringUtils.isBlank(f.getImage())) {
                                f.setPath(f.getImage()+"@30p");
                            }
                            flis.add(f.getId());
                        }
                        List<EntityBase>  ebs= getStates("find",flis);
                        for(EntityFind f:efs) {
                            boolean flag=true;
                            for(EntityBase fe:ebs) {
                                if(flag&&f.getId().equals(fe.getId())) {
                                    flag=false;
                                    f.setCollectionNum(fe.getCollectionNum());
                                    f.setCommentNum(fe.getCommentNum());
                                    f.setHitNum(fe.getHitNum());
                                    f.setLikeNum(fe.getLikeNum());
                                    f.setNotLikeNum(fe.getNotLikeNum());
                                }
                            }
                        }
                        total = findMapper.count(type);
                    }
                    //if(efs==null) efs=new ArrayList();

                    return new ReturnListData(efs,total);
                } catch (Exception e) {
                    Map map=new HashMap();
                    map.put("method", "EntityService.entityList");
                    map.put("tname", tname);
                    map.put("type", type);
                    map.put("pager", pager);
                    map.put("pageSize", pageSize);
                    new LogException(e,map);
                    return ReturnListData.ERROR;
                }
            }

            @Override
            protected boolean canPutToCache(ReturnListData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet()== 0 &&
                        returnValue.getTotal()>0 );
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "EntityService.entityList_" + tname + "_" + type + "_" + pager + "_" + pageSize), true);
    }

    /**
     * hq_config
     * ??????key????????????????????? (app?????????,banner???)
     * ??????????????????
     *
     * @return
     */
    public String getConfig(final String key) {
        return new CacheableTemplate<String>(cacheProvider) {
            @Override
            protected String getFromRepository() {
                try {
                    String value=findMapper.getConfigValue(key);
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
     * ??????config????????? value map??????
     * ??????
     *
     * @return
     */
    public JSONObject getConfigMap( String key) {
        String value=getConfig(key);
        JSONObject  jasonObject = JSONObject.fromObject(value);
        return jasonObject;
    }

    /**
     * ??????config????????? value array??????
     * ??????
     *
     * @return
     */
    public JSONArray getConfigArray( String key) {
        String value=getConfig(key);
        JSONArray  jasonObject = JSONArray.fromObject(value);
        return jasonObject;
    }


    /**
     * ??????config????????? value String???List??????
     * ??????
     *
     * @return
     */
    public List getConfigList(String key) {
        String value=getConfig(key);
        JSONArray json = JSONArray.fromObject(value);
        return new ArrayList(json);
    }

    /**
     * ??????config????????? value String???Map??????
     * ??????
     *
     * @return
     */
    public List getHotKeyWord(String key) {
        Map<String,Object> map=new HashMap<String,Object>();
        String value=getConfig(key);
        List list=new ArrayList();
        if(org.apache.commons.lang.StringUtils.isNotBlank(value)){
            String[] vals=value.split("???");
            for(int j=0;j<vals.length;j++){
                Map<String,Object> map2=new HashMap<String,Object>();
                map2.put("title", vals[j]);
                list.add(map2);
            }
        }
        return list;
    }

    /**
     * ??????key??????config????????? value
     * todo???????????????
     *
     * @return
     */
    public ReturnData updateValue(String key,String value) {
        try{
            if(org.apache.commons.lang.StringUtils.isNotBlank(key)){
                int i=findMapper.updateConfigValue(key,value);
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
     * ??????init????????????????????????
     * @return
     */
    public ReturnData colCofValue() {
        try{
            String key="update_init";
            //????????????
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
     * ?????????
     *
     * @return
     */
    public int keywordInfiltration(String content,String value) {
        String[] values=value.split(",");
        if(content!=null && values!=null && values.length>0){
            for(int i=0;i<values.length;i++){
                if(content.indexOf(values[i])!=-1){
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * ?????????
     *	???????????????????????????***
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
     * ???????????????????????????
     * @return
     */
    public Map Infiltration(String key,String title,String details) {
        // ?????????
        String conVal = getConfig(key);
        String[] vals=null;
        Map map=new HashMap();
        if(!org.apache.commons.lang.StringUtils.isBlank(conVal)){
            title = keywordInfiltration2(title, conVal);
            details = keywordInfiltration2(details, conVal);
            map.put("title", title);
            map.put("details", details);
        }
        return map;
    }


    /**
     * ????????? ?????????
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
     * ??????????????????
     * @param tname: goods,composition,find,lists
     * @param state: ???????????????
     * @param entityId: ??????id
     * @param val: 1????????????????????????????????? 0??????
     * @return
     */
    public ReturnData entityChangeState(String tname, String state, long entityId, int val) {
        EntityBase eb=this.getEntityById(tname, entityId);
        if(eb!=null) {
            String fd=enField.get(state);
            if(fd!=null) {
                //?????????????????????
                Map map = mongoTemplate.findAndModify(new Query(Criteria.where("id").is(entityId)), new Update().set(fd, val), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class, EntityMeta.ENTITY_TABLE_PREFIX+tname);

                //????????????????????????

                int i=goodsExtMapper.updateField("allow_comment", val, "goods_id", eb.getId());
                //????????????
                cacheService.cleanCacheListByKey("GoodsService.getByGoodsByMid_"+eb.getMid());
                cacheService.cleanCacheListByKey("GoodsService.getGoodsExplain_"+eb.getMid());
                cacheService.cleanCacheListByKey("GoodsService.getByGoodsByid_"+eb.getId());

                return ReturnData.SUCCESS;
            }
        }
        return ReturnData.ERROR;
    }


    /**
     * ????????????+??????????????????
     * @param tname
     * @param id
     * @param id
     * @return
     */
    public ReturnData entityInfo2(String tname, long id, UserInfo userInfo) {
        try {
            String entityTname = "entity_" + tname;
            Query query=new Query(Criteria.where("id").is(id));
            if(tname.equals("apply_goods")){
                entityTname="entity_apply_goods2";
            }
            EntityBase map = (EntityBase) mongoTemplate.findOne(query, CommenMeta.getEntityClass(entityTname));
            if (map == null) {
                map= checkEntity(entityTname,id);
            }
            Map resutls=new HashMap();
            //?????????????????????
            if(tname.equals("apply_goods")) {
                EntityApplyGoods2 agds=(EntityApplyGoods2) map;
                //??????????????????
                if(org.apache.commons.lang.StringUtils.isNotBlank(agds.getGoodsIds())){
                    List goods=goodsMapper.getGoodsByIds(agds.getGoodsIds());
                    agds.setGoods(goods);
                }
            }

            resutls.put("entity", map);

            long userId=0;
            if (null!=userInfo) {
                userId=userInfo.getId();
                UserEntityAction uea = userEntityAction(tname, id, userInfo.getId());
                uea.setCommentNum(map.getCommentNum());
                uea.setLikeNum(map.getLikeNum());
                uea.setNotLikeNum(map.getNotLikeNum());
                resutls.put("action",  uea);
            }
            ReturnData rd=null;
            //??????????????????
            if("goods".equals(tname)){
                //??????
                rd=goodsService.getGoodsExplain(map.getMid(),userInfo);
                GoodsExplain goodsExplain = (GoodsExplain) rd.getResult();
                resutls.put("entityInfo", goodsExplain);
            }else if("composition".equals(tname)){
                //??????
                Composition c= compositionService.getCompositionByMid(map.getMid());
                resutls.put("entityInfo", c);
            }else if("lists".equals(tname)){
                //??????/??????
                rd=hotListService.detailContent(Integer.parseInt(id+""),userId);
                Map resultMap=(Map)rd.getResult();
                resutls.put("entityInfo", resultMap);
            }else if("find".equals(tname)){
                //????????????/??????
                rd=findService.findArticleInfo(Integer.parseInt(id+""));
                Find find=(Find)rd.getResult();
                resutls.put("entityInfo", find);
            }


            //tname  ??????
            this.objectIncById(id, entityTname, "hitNum", 1);

            return new ReturnData(resutls,0);
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("tname", tname);
            map.put("id", id);
            map.put("userId", userInfo.getId());
            map.put("method", "EntityService.entityInfo2");
            new LogException(e,map);
        }
        return ReturnData.ERROR;
    }


    public ReturnData entityInfo3(String tname, Long id, UserInfo userInfo,String mid) {
        try {
            if(tname.equals("apply_goods")){
                tname="apply_goods2";
            }

            String entityTname = "entity_" + tname;
            Query query=new Query(Criteria.where("id").is(id));
            //???????????????????????????mid
            if(("goods".equals(tname) || "composition".equals(tname))&& org.apache.commons.lang.StringUtils.isNotBlank(mid)){
                query=new Query(Criteria.where("mid").is(mid));
            }

            EntityBase map = (EntityBase) mongoTemplate.findOne(query, CommenMeta.getEntityClass(entityTname));

            Map resutls=new HashMap();
            //????????????
            if(tname.equals("apply_goods2") && null!=map) {
                EntityApplyGoods2 agds=(EntityApplyGoods2) map;
                //??????????????????
                List<Map> descp = agds.getDescp();
                for(Map descpMap : descp){
                    if(Integer.parseInt(String.valueOf(descpMap.get("type"))) == 2){
                        String image = descpMap.get("image").toString();
                        descpMap.put("image", CommonUtils.getImagDomain() + "/trial/" + id + "/" + image);
                    }
                }
                //??????????????????
                if(org.apache.commons.lang.StringUtils.isNotBlank(agds.getGoodsIds())){
                    List goods=goodsMapper.getGoodsByIds(agds.getGoodsIds());
                    agds.setGoods(goods);
                }
            }

            resutls.put("entity", map);
            long userId=0;
            if (null!=userInfo && null!=map) {
                userId=userInfo.getId();
                UserEntityAction uea = userEntityAction(tname, map.getId(), userInfo.getId());
                uea.setCommentNum(map.getCommentNum());
                uea.setLikeNum(map.getLikeNum());
                uea.setNotLikeNum(map.getNotLikeNum());
                resutls.put("action",  uea);
            }
            ReturnData rd=null;
            //??????????????????
            if("goods".equals(tname) && org.apache.commons.lang.StringUtils.isNotBlank(mid)){
                //??????
                rd=goodsService.getGoodsExplain(mid,userInfo);
                
                GoodsExplain goodsExplain = (GoodsExplain) rd.getResult();
                resutls.put("entityInfo", goodsExplain);
            //??????      CPS????????????
                Goods goods = goodsExplain.getGoods();
                Query que = new Query(Criteria.where("goodsId").is(goods.getId()));
                que.fields().include("id").include("entityAdCpsChannel").include("goodsId").include("hidden");
                //??????????????????????????????
                EntityAdCps entityAdCps = (EntityAdCps)mongoTemplate.findOne(que,EntityAdCps.class);
               ////
                checkISCOMMISSION(entityAdCps);//?????????????????????
                if(entityAdCps != null && 0==entityAdCps.getHidden())
                {
                    EntityAdCps nea = new EntityAdCps();
                    List cpsList = entityAdCps.getEntityAdCpsChannel();
                    
                    
                    
                    List cpsLi = new ArrayList();
                    Long time = Long.valueOf((new Date()).getTime() / 1000L);
                    for(int i = 0; i < cpsList.size(); i++){
                            EntityAdCpsChannel eac = new EntityAdCpsChannel();
                            eac.setChannelGoodsId(((EntityAdCpsChannel)cpsList.get(i)).getChannelGoodsId());
                            eac.setChannelLink(((EntityAdCpsChannel)cpsList.get(i)).getChannelLink());
                            eac.setChannelType(((EntityAdCpsChannel)cpsList.get(i)).getChannelType());
                            eac.setChannelName(((EntityAdCpsChannel)cpsList.get(i)).getChannelName());
                            eac.setChannelGoodsId(((EntityAdCpsChannel)cpsList.get(i)).getChannelGoodsId());
                            eac.setPrice(((EntityAdCpsChannel)cpsList.get(i)).getPrice());
                            eac.setImgSrc(((EntityAdCpsChannel)cpsList.get(i)).getImgSrc());
                            eac.setAndroidLink(((EntityAdCpsChannel)cpsList.get(i)).getAndroidLink());
                            eac.setIosLink(((EntityAdCpsChannel)cpsList.get(i)).getIosLink());
                            Integer type=((EntityAdCpsChannel)cpsList.get(i)).getChannelType();
                            String imgSrc=null;
                            if(type==1){
                                imgSrc="http://img0.bevol.cn/cps_icon/taobao3x.png";
                            }else if(type==2){
                                imgSrc="http://img0.bevol.cn/cps_icon/jd3x.png";
                            }else if(type==3){
                                imgSrc="http://img0.bevol.cn/cps_icon/tmall3x.png";
                            }
                            eac.setImgSrc(imgSrc);
                            cpsLi.add(eac);
                        }

                    nea.setGoodsId(entityAdCps.getGoodsId());
                    nea.setId(entityAdCps.getId());
                    nea.setEntityAdCpsChannel(cpsLi);
                    resutls.put("entityAdCps", nea);
                }
                //??????
                if(null!=goodsExplain && null!=goodsExplain.getGoods()){
                    int category=goodsExplain.getGoods().getCategory();
                    //?????????????????????,lists??????tags
                    List<Map<String,Object>> listMap=findMapper.getComemntTagsByCategory(category);
                    //?????????????????????
                    resutls.put("commentTags", listMap);
                }

            }else if("composition".equals(tname) && org.apache.commons.lang.StringUtils.isNotBlank(mid)){
                //??????
                Composition c= compositionService.getCompositionByMid(mid);
                resutls.put("entityInfo", c);
            }else if("lists".equals(tname)){
                //??????/??????
                rd=hotListService.detailContent(Integer.parseInt(id+""),userId);
                Map resultMap=(Map)rd.getResult();
                resutls.put("entityInfo", resultMap);
            }else if("find".equals(tname)){
                //????????????/??????
                rd=findService.findArticleInfo(Integer.parseInt(id+""));
                Find find=(Find)rd.getResult();
                resutls.put("entityInfo", find);
            }

            //tname  ??????
            if(null!=map){
                this.objectIncById(map.getId(), entityTname, "hitNum", 1);
            }

            return new ReturnData(resutls,0);
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("tname", tname);
            map.put("id", id);
            map.put("mid", mid);
            map.put("userId", userInfo.getId());
            map.put("method", "EntityService.entityInfo3");
            new LogException(e,map);
        }
        return ReturnData.ERROR;
    }

        /**
         * ?????????????????????
         * @param entityAdCps
         */
		private   void   checkISCOMMISSION(EntityAdCps entityAdCps) {
			 //?????????????????????
          Long  time =new Date().getTime();
          List<EntityAdCpsChannel>  entityAdCpsChannels = entityAdCps.getEntityAdCpsChannel();
 		    for(int y =0;y<entityAdCpsChannels.size();y++) {
 			 EntityAdCpsChannel  entityAdCpsChannel = entityAdCpsChannels.get(y);
 			   //??????????????????
 			   if(entityAdCpsChannel.getIsCommission()==EntityAdCpsChannel.ISCOMMISSION_YES) {
 				   if(entityAdCpsChannel.getChannelEndTimeCommission().longValue()<=time){//???????????????????????????????????????????????????cps??????????????????
 					   entityAdCpsChannel.setIsCommission(EntityAdCpsChannel.ISCOMMISSION_NO);
 				   }
 			   }
               //????????????????????????
 			   if(null!=entityAdCpsChannel.getChannelEndTime()&&entityAdCpsChannel.getChannelEndTime().longValue()<=time) {
 				   entityAdCpsChannel.setHidden(1);
 			   }
 		 }
 		 //????????????
 		 mongoTemplate.updateFirst(new Query(Criteria.where("id").is(entityAdCps.getId())), new Update().set("entityAdCpsChannel", entityAdCpsChannels), EntityAdCps.class);
			
		}


    /**
     * ?????? ids????????????
     * @param ids
     * @param tname
     * @return
     */
    private List findEntityByids(String ids,String tname){
        if(org.apache.commons.lang.StringUtils.isNotBlank(ids)) {
            List<Long> goodsArrays=new ArrayList<Long>();
            String goodsIds[]=ids.split(",");
            for(int i=0;i<goodsIds.length;i++) {
                goodsArrays.add(Long.parseLong(goodsIds[i]));
            }
            if(goodsArrays.size()>0) {
                List goods = mongoTemplate.find(new Query(Criteria.where("id").in(goodsArrays)), CommenMeta.getEntityClass(tname));
                return goods;
            }
        }
        return null;
    }


    /**
     * ?????????????????????
     * type 1?????? 2??????/??????/?????? 3?????? 4?????????????????????????????????
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
                String imageUrl=CommonUtils.getImageSrc(tname, image);
                if(org.apache.commons.lang.StringUtils.isNotBlank(imageUrl)){
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
