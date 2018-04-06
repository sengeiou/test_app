package cn.bevol.internal.service;

import cn.bevol.model.entityAction.DiscussCompareGoods;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.model.Goods;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackDiscussService {

    @Resource
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取产品对比讨论列表
     * @param sid
     * @param userId
     * @param content
     * @param startTime
     * @param endTime
     * @param isRids
     * @param hidden
     * @return
     */
    public ReturnListData getDiscussList(String sid,
                                         Integer userId,
                                         String content,
                                         Integer startTime,
                                         Integer endTime,
                                         Integer isRids,
                                         Integer hidden,
                                         Integer page,
                                         Integer rows){
        try {
            Criteria cr = Criteria.where("deleted").is(0);

            if (hidden != null) {
                cr.and("hidden").is(hidden);
            }

            //筛选sid  3256_2564 goodsId1_goodsId2
            if (sid != null) {
                cr.and("sid").is(sid);
            }

            //筛选userId
            if (userId != null) {
                cr.and("userId").is(userId);
            }

            //筛选内容
            if (content != null) {
                cr.and("content").regex(content);
            }

            //筛选时间
            if (startTime != null && endTime != null) {
                cr.and("updateStamp").gte(startTime).lte(endTime);
            } else if (startTime != null) {
                cr.and("updateStamp").gte(startTime);
            } else if (endTime != null) {
                cr.and("updateStamp").lte(endTime);
            }

            //筛选是否引用
            if (isRids !=null && isRids== 1) {
                cr.and("rids").exists(true);
            }

            List<DiscussCompareGoods> discussCompareGoodsList = mongoTemplate.find(
                    Query.query(cr).with(new Sort(Sort.Direction.DESC, "id")).skip((page-1)*rows).limit(rows),
                    DiscussCompareGoods.class
            );

            long total = mongoTemplate.count(
                    Query.query(cr),
                    DiscussCompareGoods.class
            );
            
            if(null!=discussCompareGoodsList && discussCompareGoodsList.size()>0){
            	for(DiscussCompareGoods dcg:discussCompareGoodsList){
            		if(StringUtils.isNotBlank(dcg.getSid())){
            			String goodsIds=dcg.getSid().replace("_", ",");
        				List<Goods> goodsList=goodsMapper.getGoodsByIds(goodsIds);
        				List<Map> objList=new ArrayList<Map>();
        				for(Goods goods:goodsList){
        					Map map=new HashMap();
        					map.put("title", goods.getTitle());
        					map.put("mid", goods.getMid());
        					map.put("id", goods.getId());
        					objList.add(map);
        				}
        				dcg.setObjList(objList);
            		}
            	}
            }

            return new ReturnListData<DiscussCompareGoods>(discussCompareGoodsList, total);
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "BackDiscussService.getDiscussList");
            map.put("sid", sid);
            map.put("userId", userId);
            map.put("content", content);
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            map.put("isRids", isRids);
            map.put("hidden", hidden);
            new cn.bevol.log.LogException(e,map);
            return ReturnListData.ERROR;
        }
    }

    /**
     * 设置产品对比讨论状态
     * @param id
     * @param hidden
     * @return
     */
    public ReturnData setDiscussState(Integer id, Integer hidden, Integer isEssence){
        try {
            Update update = new Update();
            if(null != hidden){
               update.set("hidden", hidden);
            }
            if(null != isEssence){
                update.set("isEssence", isEssence);
            }
            mongoTemplate.findAndModify(
                    Query.query(Criteria.where("id").is(id)),
                    update,
                    DiscussCompareGoods.class);
            return ReturnData.SUCCESS;
        }catch(Exception e){
            Map map=new HashMap();
            map.put("method", "BackDiscussService.setDiscussState");
            map.put("id", id);
            map.put("hidden", hidden);
            new cn.bevol.log.LogException(e,map);
            return ReturnData.ERROR;
        }
    }
}
