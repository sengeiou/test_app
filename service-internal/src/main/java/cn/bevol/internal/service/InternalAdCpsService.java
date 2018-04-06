package cn.bevol.internal.service;

import cn.bevol.model.entity.EntityAdCps;
import cn.bevol.model.entity.EntityAdCpsChannel;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.entity.service.BaseService;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class InternalAdCpsService extends BaseService {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 广告cps列表
     * @param id
     * @param title
     * @param startTime
     * @param endTime
     * @param page
     * @param rows
     * @param hidden
     * @return
     */
    public ReturnListData getAdCpsList(Integer id,
                                       String title,
                                       Integer startTime,
                                       Integer endTime,
                                       Integer page,
                                       Integer rows,
                                       Integer hidden){
        try {
            Criteria cr = Criteria.where("deleted").is(0);
            if (null != id) {
                cr.and("goodsId").is(id);
            }
            if (null != title) {
                cr.and("title").regex(title);
            }
            /*if (null != startTime) {
                cr.and("startTime").gte(startTime);
            }
            if (null != endTime) {
                cr.and("endTime").lte(endTime);
            }*/
            if (null != hidden) {
                cr.and("hidden").is(hidden);
            }

            Query query = new Query();
            query.skip((page - 1) * rows).limit(rows);
            query.addCriteria(cr);

            //倒序
            query.with(new Sort(Sort.Direction.DESC, "id"));

            List<EntityAdCps> entityAdCpsList = mongoTemplate.find(query, EntityAdCps.class);
            Long total = mongoTemplate.count(Query.query(cr), EntityAdCps.class);

            //设置商品名
            ArrayList<Long> goodsIds = new ArrayList<Long>();
            for(EntityAdCps entityAdCps:entityAdCpsList){
                goodsIds.add(entityAdCps.getGoodsId());
            }
            List<EntityGoods> entityGoodsList = mongoTemplate.find(
                    Query.query(Criteria.where("id").in(goodsIds)),
                    EntityGoods.class
            );
            for(EntityAdCps entityAdCps:entityAdCpsList){
                for(EntityGoods entityGoods:entityGoodsList){
                    if(entityAdCps.getGoodsId().equals(entityGoods.getId())){
                        entityAdCps.setTitle(entityGoods.getTitle());
                        break;
                    }
                }
            }

            return new ReturnListData(entityAdCpsList, total);
        }catch(Exception e){
            e.printStackTrace();
            return ReturnListData.ERROR;
        }
    }

    /**
     * 新增修改广告cps
     * @param entityAdCps
     * @return
     */
    public ReturnData upsertAdCps(EntityAdCps entityAdCps){

        try {
            entityAdCps.transferAdCpsChannel();
            for (EntityAdCpsChannel entityAdCpsChannel : entityAdCps.getEntityAdCpsChannel()) {
                if (entityAdCpsChannel.getChannelType() == null ||
                        entityAdCpsChannel.getChannelName() == null ||
                        entityAdCpsChannel.getChannelLink() == null ||
                        entityAdCpsChannel.getChannelStartTime() == null ||
                        entityAdCpsChannel.getChannelEndTime() == null ||
                        entityAdCpsChannel.getChannelCommissionType() == null ||
                        entityAdCpsChannel.getChannelCommission() == null ||
                        entityAdCpsChannel.getChannelGoodsId() == null) {
                    return new ReturnData(-1, "商品渠道信息不全或有误！");
                }
            }

            //设置商品名
            EntityGoods entityGoods = mongoTemplate.findOne(
                    Query.query(Criteria.where("id").is(entityAdCps.getGoodsId())),
                    EntityGoods.class
            );
            entityAdCps.setTitle(entityGoods.getTitle());

            String tname = "entity_ad_cps";
            if (entityAdCps.getId() == null) {
                //id为空,新增cps

                if (null == entityAdCps.getGoodsId()) {
                    return new ReturnData(-1, "商品id不能为空");
                }

                /*if (null == entityAdCps.getStartTime()) {
                    return new ReturnData(-1, "开始时间不能为空");
                }

                if (null == entityAdCps.getEndTime()) {
                    return new ReturnData(-1, "结束时间不能为空");
                }

                if (entityAdCps.getStartTime() >= entityAdCps.getEndTime()) {
                    return new ReturnData(-1, "开始时间不能大于等于结束时间");
                }*/

                if (null == entityAdCps.getEntityAdCpsChannel()) {
                    return new ReturnData(-1, "请填写商品渠道信息");
                }

                this.save(tname, entityAdCps);
                return new ReturnData(entityAdCps);

            } else {
                Update update = new Update();
                if (null != entityAdCps.getGoodsId()) {
                    update.set("goodsId", entityAdCps.getGoodsId());
                }
                if (null != entityAdCps.getStartTime()) {
                    update.set("startTime", entityAdCps.getStartTime());
                }
                if (null != entityAdCps.getEndTime()) {
                    update.set("endTime", entityAdCps.getEndTime());
                }
                if (null != entityAdCps.getEntityAdCpsChannel()) {
                    update.set("entityAdCpsChannel", entityAdCps.getEntityAdCpsChannel());
                }
                //id不为空,编辑cps
                mongoTemplate.findAndModify(
                        Query.query(Criteria.where("id").is(entityAdCps.getId())),
                        update,
                        EntityAdCps.class
                );
                return ReturnData.SUCCESS;
            }
        }catch (Exception e){
            e.printStackTrace();
            return ReturnData.ERROR;
        }
    }

    /**
     * 设置广告cps状态
     * @param ids
     * @param hidden
     * @param deleted
     * @return
     */
    public ReturnData setAdCpsState(String ids, Integer hidden, Integer deleted){
        try {
            ArrayList<Long> idsList = new ArrayList<Long>();
            if (null == ids) {
                return new ReturnData(-1, "传参错误");
            }else{
                String[] idsArr = ids.split(",");
                for(String id : idsArr){
                    idsList.add(Long.parseLong(id));
                }
            }

            Update update = new Update();
            if (null != hidden) {
                update.set("hidden", hidden);
            }
            if (null != deleted) {
                update.set("deleted", deleted);
            }

            mongoTemplate.updateMulti(
                    Query.query(Criteria.where("id").in(idsList)),
                    update,
                    EntityAdCps.class
            );
            return ReturnData.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ReturnData.ERROR;
        }
    }
}
