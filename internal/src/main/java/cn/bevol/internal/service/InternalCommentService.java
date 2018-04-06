package cn.bevol.internal.service;

import cn.bevol.internal.entity.entityAction.Comment;
import cn.bevol.internal.entity.user.UserInfo;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.util.DateUtils;
import cn.bevol.util.Log.LogClass;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@LogClass
public class InternalCommentService {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 获取评论列表
     * @param entity
     * @param rows
     * @param page
     * @param sortField
     * @param order
     * @param entityId
     * @param hidden
     * @param pid
     * @param keyword
     * @param keywordType
     * @param startTimeStamp
     * @param endTimeStamp
     * @return
     */
    public ReturnListData<Comment> getCommentList(String entity,
                                                  Integer rows,
                                                  Integer page,
                                                  String sortField,
                                                  Integer order,
                                                  Integer entityId,
                                                  Integer userId,
                                                  Integer hidden,
                                                  Integer isEssence,
                                                  Integer pid,
                                                  Integer mainId,
                                                  String keyword,
                                                  Integer keywordType,
                                                  String label,
                                                  String suggestion,
                                                  Integer startTimeStamp,
                                                  Integer endTimeStamp){
        String entityCollection = "entity_comment_" + entity;
        Query query = new Query();
        Criteria cr = Criteria.where("deleted").ne(1);
        if(hidden != null){
            //筛选隐藏的评论
            cr.and("hidden").is(hidden);
        }
        if(isEssence != null){
            //是否精华点评
            cr.and("isEssence").is(isEssence);
        }
        if(pid != null){
            //筛选被回复评论/筛选一级/二级评论
            if(pid == 1){
                cr.and("pid").gt(0);
            }else if(pid == 0){
                cr.and("pid").is(pid);
            }
        }
        if(entityId != null){
            //筛选单个实体对应的评论
            cr.and("entityId").is(entityId);
        }

        if(userId != null){
            //通过用户id筛选
            cr.and("userId").is(userId);
        }

        if(mainId != null){
            //筛选目标评论
            cr.and("mainId").is(mainId);
        }

        if(label != null){
            //筛选反垃圾label
            cr.and("marker.label").is(label);
        }

        if(suggestion != null){
            //筛选反垃圾建议
            cr.and("marker.suggestion").is(suggestion);
        }

        if(keyword != null && keywordType != null){
            //筛选关键字
            switch(keywordType){
                case 1:
                    //筛选用户名
                    Query userQuery = new Query();
                    Criteria userCr = Criteria.where("nickname").regex(keyword);
                    userQuery.addCriteria(userCr);
                    List<UserInfo> userInfoList = mongoTemplate.find(userQuery, UserInfo.class);
                    ArrayList<Long> userIdArrayList = new ArrayList<Long>();
                    for(UserInfo userInfo:userInfoList){
                        userIdArrayList.add(userInfo.getId());
                    }
                    cr.and("userId").in(userIdArrayList);
                    break;
                case 2:
                    //筛选实体ID
                    cr.and("entityId").is(Integer.valueOf(keyword));
                    break;
                case 3:
                    //筛选评论内容
                    cr.and("content").regex(keyword);
                    break;
                case 4:
                    //筛选评论ID
                    cr.and("id").is(Integer.valueOf(keyword));
                    break;
                default:
            }
        }

        //筛选时间
        if(startTimeStamp != null && endTimeStamp != null){
            cr.and("updateStamp").gte(startTimeStamp).lte(endTimeStamp);
        }

        //排序
        Sort sort;
        if(order == -1){
            sort = new Sort(Sort.Direction.DESC, sortField);
        }else{
            sort = new Sort(Sort.Direction.ASC, sortField);
        }

        query.addCriteria(cr).with(sort).skip((page - 1)*rows).limit(rows);

        //设置返回字段
        String[] fields = {"id", "hidden", "images", "updateStamp", "createStamp", "entityId", "userId",
                "skin", "skinResults", "score", "content", "likeNum", "isEssence", "commentNum",
                "isJubao", "mainId", "mainUserId", "pid", "pUserId", "marker", "image", "isComment"};
        for(String field : fields){
            query.fields().include(field);
        }

        List<Comment> commentList = mongoTemplate.find(query, Comment.class, entityCollection);
        Long total = mongoTemplate.count(query, Comment.class, entityCollection);
        return new ReturnListData<Comment>(commentList, total);
    }

    /**
     * 更新评论详情
     * @param entity
     * @param ids
     * @param likeNum
     * @param isEssence
     * @param hidden
     * @param deleted
     * @return
     */
    @Transactional
    public ReturnData updateCommentInfo(String entity,
                                        String ids,
                                        Long likeNum,
                                        Integer isEssence,
                                        Integer hidden,
                                        Integer deleted){
        String[] idsArr = ids.trim().split(",");
        ArrayList<Integer> idsArrayList = new ArrayList<Integer>();
        for(String id : idsArr){
            idsArrayList.add(Integer.valueOf(id));
        }
        if(!StringUtils.isEmpty(entity) && idsArrayList.size() > 0){
            String entityCollection = "entity_comment_" + entity;
            Update update=new Update();
            if(likeNum != null){
                update.set("likeNum", likeNum);
            }
            if(isEssence!= null){
                update.set("isEssence", isEssence);
                List<Comment> commentList = mongoTemplate.find(new Query(Criteria.where("id").in(idsArrayList)), Comment.class, entityCollection);
                ArrayList<Long> userIdsArrList = new ArrayList<Long>();
                for(Comment comment : commentList){
                    userIdsArrList.add(comment.getUserId());
                }
                Update userUpdate=new Update();
                if(isEssence == 1){
                    userUpdate.inc("score", 20);
                }else if(isEssence == 0){
                    userUpdate.inc("score", -20);
                }
                mongoTemplate.updateMulti(new Query(Criteria.where("id").in(userIdsArrList)), userUpdate, UserInfo.class, "user_info");
            }
            if(hidden != null){
                update.set("hidden", hidden);
                this.hiddenCommment(hidden,idsArrayList,entityCollection,entity);
            }
            if(deleted != null){
                update.set("deleted", deleted);
            }
            update.set("updateStamp", DateUtils.nowInSeconds());
            mongoTemplate.updateMulti(new Query(Criteria.where("id").in(idsArrayList)), update, Comment.class, entityCollection);
            return ReturnData.SUCCESS;
        }else{
            return new ReturnData(-1, "传参错误");
        }
    }

    /**
     * 隐藏评论后实体或一级评论的评论数变更(没有清除缓存)
     * @param hidden
     * @param idsArrayList
     * @param entityCollection
     * @param entity
     */
    public void hiddenCommment(Integer hidden,ArrayList<Integer> idsArrayList,String entityCollection,String entity){
        List<Comment> commentList = mongoTemplate.find(new Query(Criteria.where("id").in(idsArrayList)), Comment.class, entityCollection);
        Map<Long,Integer> mainMap=new HashMap<Long,Integer>();
        Map<Long,Integer> subMap=new HashMap<Long,Integer>();
        for(Comment cmt:commentList){
            if((null==cmt.getPid() || cmt.getPid()==0) && null!=cmt.getEntityId()){
                //一级评论 实体评论数做处理
                if(null==mainMap.get(cmt.getEntityId())){
                    mainMap.put(cmt.getEntityId(),1);
                } else{
                    mainMap.put(cmt.getEntityId(),mainMap.get(cmt.getEntityId())+1);
                }
            } else if(null!=cmt.getPid() && cmt.getPid()>0){
                //子平论 一级评论数做处理
                if(null==subMap.get(cmt.getMainId())){
                    subMap.put(cmt.getMainId(),1);
                } else{
                    subMap.put(cmt.getMainId(),subMap.get(cmt.getMainId())+1);
                }
            }
        }
        for(Map.Entry<Long,Integer> entry:mainMap.entrySet()){
            //一级评论 实体评论数做处理
            if(hidden==0){
                mongoTemplate.updateFirst(new Query(Criteria.where("id").is(entry.getKey())),new Update().inc("commentNum",entry.getValue()), EntityBase.class,"entity_"+entity);
            }else if(hidden==1){
                mongoTemplate.updateFirst(new Query(Criteria.where("id").is(entry.getKey())),new Update().inc("commentNum",-entry.getValue()), EntityBase.class,"entity_"+entity);
            }
        }
        for(Map.Entry<Long,Integer> entry:subMap.entrySet()){
            //子平论 一级评论数做处理  TODO
            if(hidden==0){
                mongoTemplate.updateFirst(new Query(Criteria.where("id").is(entry.getKey())),new Update().inc("commentNum",entry.getValue()), Comment.class,entityCollection);
            }else if(hidden==1){
                mongoTemplate.updateFirst(new Query(Criteria.where("id").is(entry.getKey())),new Update().inc("commentNum",-entry.getValue()), Comment.class,entityCollection);
            }
        }
    }

    /**
     * 获取评论详情
     * @param entity
     * @param id
     * @return
     */
    public ReturnData getCommentInfo(String entity, Integer id){
        if(id > 0){
            String entityCollection = "entity_comment_" + entity;
            Comment comment = mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), Comment.class, entityCollection);
            return new ReturnData(comment, 0);
        }else{
            return new ReturnData(-1, "传参错误");
        }
    }
}
