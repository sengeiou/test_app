package cn.bevol.internal.service;

import cn.bevol.model.entity.EntityGoods;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.entity.model.*;
import cn.bevol.internal.dao.mapper.BackGoodsOldMapper;
import cn.bevol.internal.dao.mapper.GoodsExtOldMapper;
import cn.bevol.internal.dao.mapper.GoodsOldMapper;
import cn.bevol.internal.entity.dto.GoodsDTO;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.MD5Utils;
import cn.bevol.util.http.HttpUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by ZhangCheng on 17-3-15.
 */
@Service
public class InternalGoodsService {
    private static Logger logger = LoggerFactory.getLogger(InternalGoodsService.class);
    @Resource
    private GoodsOldMapper goodsOldMapper;

    @Resource
    private GoodsExtOldMapper goodsExtOldMapper;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CacheService cacheService;

    @Resource
    private GoodsService goodsService;

    @Autowired
    private BackGoodsOldMapper backGoodsOldMapper;

    @Autowired
    private RedisCacheProvider cacheProvider;
    @Resource
    private StaticClientService staticClientService;
    @Resource
    private InternalGoodsCalculateService backGoodsCalculateService;

    private final static String ENTITY_GOODS_COLLECTION = "entity_goods";

    @Transactional
    public ReturnData addNewGoods(GoodsDTO goodsDTO){
        try{
            //插入到产品表
            goodsDTO = addGoodsBaseInfo(goodsDTO);
            //生成产品mid
            goodsDTO = saveGoodsMid(goodsDTO);
            //插入到产品扩展表
            addGoodsExtInfo(goodsDTO);
            //插入mongo数据
            addGoodsMongoData(goodsDTO);
            //人工修改分类
            changeCategory(goodsDTO);
            //加入seo
            addGoods2Seo(goodsDTO);
            //重新计算产品信息
            calculateGoodsInfo(goodsDTO);
            //重新静态化
            goodsStatic(goodsDTO);
        }catch(Exception e){
            e.printStackTrace();
            logger.debug("Class CmsGoodsService,Method addNewGoods:" + ExceptionUtils.getStackTrace(e));
            return ReturnData.ERROR;
        }
        return new ReturnData(goodsDTO);
    }

    /**
     * 单个修改
     * @param goodsDTO
     * @return
     */
    @Transactional
    public ReturnData saveBaseGoodsInfo(GoodsDTO goodsDTO){
        try{
            if(goodsDTO.getId() != null){
                goodsOldMapper.saveGoodsBaseInfo(goodsDTO);
                saveGoodsMongoData(goodsDTO);
                //人工修改分类
                changeCategory(goodsDTO);
                //产品禁言
                changeAllowComment(goodsDTO);
                //重新计算产品信息
                calculateGoodsInfo(goodsDTO);
                //重新静态化
                goodsStatic(goodsDTO);
            }else{
                return ReturnData.ERROR;
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.debug("Class CmsGoodsService Method saveBaseGoodsInfo:"+ExceptionUtils.getStackTrace(e));
            return ReturnData.ERROR;
        }
        return ReturnData.SUCCESS;
    }

    /**
     * 批量修改 （只更改hidden,deleted,hot,top,hidden_skin,image,allow_comment字段）
     * @param goodsDTO
     * @param ids
     * @return
     */
    @Transactional
    public ReturnData saveBaseGoodsList(GoodsDTO goodsDTO, String ids){
        try{
            String idsStringArr[] = ids.split(",");
            Integer[] idsArr = new Integer[idsStringArr.length];
            for(int i=0;i<idsStringArr.length;i++) {
                idsArr[i] = Integer.parseInt(idsStringArr[i]);
            }
            goodsDTO.setIdsArr(idsArr);
            Integer allowComment = goodsDTO.getAllowComment();
            if(allowComment != null){
                //产品禁止评论单独调用api中的接口
                changeAllowComment(ids, allowComment);
            }else{
                goodsOldMapper.saveGoodsBaseList(goodsDTO);
                saveGoodsMongoData(goodsDTO, idsArr);
            }
            if(goodsDTO.getImage() != null){
                //批量删除图片时静态化
                List<String> mids = goodsOldMapper.getMidListById(idsArr);
                for(String mid : mids){
                    goodsStatic(mid);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            logger.debug("Class CmsGoodsService Method saveBaseGoodsList:"+ExceptionUtils.getStackTrace(e));
            return ReturnData.ERROR;
        }
        return ReturnData.SUCCESS;
    }

    @Transactional
    public ReturnData saveGoodsExtCps(GoodsExt goodsExt){
        try{
            Long goodsId = goodsExt.getGoodsId();
            if(goodsId != null){
                goodsExtOldMapper.saveGoodsExtCps(goodsExt);
                String mid = goodsOldMapper.getMidById(goodsId);
                //重新计算产品信息
                calculateGoodsInfo(goodsId.intValue());
                //重新静态化
                goodsStatic(mid);
            }else{
                return ReturnData.ERROR;
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.debug("Class CmsGoodsService Method saveGoodsExtCps:"+ExceptionUtils.getStackTrace(e));
            return ReturnData.ERROR;
        }
        return ReturnData.SUCCESS;
    }

    private GoodsDTO addGoodsBaseInfo(GoodsDTO goodsDTO){
        if(goodsDTO.getSorting() == null){
            goodsDTO.setSorting(goodsDTO.getId());
        }
        goodsOldMapper.addGoodsBaseInfo(goodsDTO);
        return goodsDTO;
    }

    private void addGoodsExtInfo(GoodsDTO goodsDTO){
        GoodsExt goodsExt = new GoodsExt();
        Long goodsId = goodsDTO.getId();
        goodsExt.setGoodsId(goodsId);
        String cps = goodsDTO.getCps();
        String cpsType = goodsDTO.getCpsType();
        if(cps != null){
            if(cpsType != null){
                goodsExt.setCpsType(cpsType);
                if(cpsType.contains("def_cps")){
                    goodsExt.setDefCps(cps);
                }else if(cpsType.equals("mfj_cps")){
                    goodsExt.setMfjCps(cps);
                }else if(cpsType.equals("gc_cps")){
                    goodsExt.setGcCps(cps);
                }else if(cpsType.equals("def_ext_cps")){
                    goodsExt.setDefExtCps(cps);
                }
            }
        }
        goodsExtOldMapper.addGoodsExtInfo(goodsExt);
    }

    private void saveGoodsMid(Long id, String mid){
        goodsOldMapper.saveGoodsMid(id, mid);
    }

    private GoodsDTO saveGoodsMid(GoodsDTO goodsDTO){
        Long goodsId = goodsDTO.getId();
        Integer dataType = goodsDTO.getDataType();
        String salt = "20160302bevol";
        String mid = MD5Utils.encode(goodsId + salt + dataType);
        saveGoodsMid(goodsId, mid);
        goodsDTO.setMid(mid);
        return goodsDTO;
    }

    private void addGoodsMongoData(GoodsDTO goodsDTO){
        EntityGoods entityGoods = new EntityGoods();
        entityGoods.setId(goodsDTO.getId());
        if(goodsDTO.getImage() != null){
            entityGoods.setImage(goodsDTO.getImage());
        }
        entityGoods.setMid(goodsDTO.getMid());
        entityGoods.setTitle(goodsDTO.getTitle());
        entityGoods.setCommentNum(0L);
        entityGoods.setLikeNum(0L);
        entityGoods.setNotLikeNum(0L);
        entityGoods.setDeleted(0);
        entityGoods.setHidden(0);
        entityGoods.setCreateStamp(System.currentTimeMillis()/1000);
        entityGoods.setUpdateStamp(System.currentTimeMillis()/1000);
        mongoTemplate.insert(entityGoods, ENTITY_GOODS_COLLECTION);
    }

    private void saveGoodsMongoData(GoodsDTO goodsDTO){
        Update update=new Update();
        if(goodsDTO.getImage() != null){
            update.set("image", goodsDTO.getImage());
        }
        if(goodsDTO.getTitle() != null) {
            update.set("title", goodsDTO.getTitle());
        }
        if(goodsDTO.getDeleted() != null){
            update.set("deleted", goodsDTO.getDeleted());
        }
        if(goodsDTO.getHidden() != null){
            update.set("hidden", goodsDTO.getHidden());
        }
        update.set("updateStamp", DateUtils.nowInSeconds());
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(goodsDTO.getId())), update, EntityGoods.class, ENTITY_GOODS_COLLECTION);
    }

    private void saveGoodsMongoData(GoodsDTO goodsDTO, Integer[] ids){
        Update update=new Update();
        if(goodsDTO.getImage() != null){
            update.set("image", goodsDTO.getImage());
        }
        if(goodsDTO.getDeleted() != null){
            update.set("deleted", goodsDTO.getDeleted());
        }
        if(goodsDTO.getHidden() != null){
            update.set("hidden", goodsDTO.getHidden());
        }
        update.set("updateStamp", DateUtils.nowInSeconds());
        mongoTemplate.updateMulti(new Query(Criteria.where("id").in(ids)), update, ENTITY_GOODS_COLLECTION);
    }

    private void addGoods2Seo(GoodsDTO goodsDTO){
        String mid = goodsDTO.getMid();
        staticClientService.seoProductAdd(mid);
    }

    public void goodsStatic(Integer id){
        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setId(id.longValue());
        goodsStatic(goodsDTO);
    }

    private void goodsStatic(GoodsDTO goodsDTO){
        String mid = goodsDTO.getMid();
        if(mid == null){
            mid = goodsOldMapper.getMidById(goodsDTO.getId());
        }
        goodsStatic(mid);
    }

    private void goodsStatic(String mid){
        //清理缓存
        cacheService.cleanProducts(mid);
        //静态化
        staticClientService.goodsStatic(mid);
    }


    public void calculateGoodsInfo(GoodsDTO goodsDTO){
        Integer goodsId = goodsDTO.getId().intValue();
        //manualMadeCategory(goodsDTO);
        calculateGoodsInfo(goodsId);
    }

    public ReturnListData calculateGoodsInfo(Integer goodsId){
        //计算产品信息
        //Map<String, Integer> goodsInfoMap = new HashMap<String, Integer>();
        //goodsInfoMap.put("ids", goodsId);
        //goodsInfoMap.put("update", 1);
        return backGoodsCalculateService.goodsCalculate(goodsId.toString(), 1);
        //HttpUtils.post(ConfUtils.getResourceString("url") + "/goods/info", goodsInfoMap, 8000);
    }

    /**
     * 单个
     * @param goodsDTO
     */
    private void changeAllowComment(GoodsDTO goodsDTO){
        Integer allowComment = goodsDTO.getAllowComment();
        Integer id = goodsDTO.getId().intValue();
        changeAllowComment(id, allowComment);
    }


    private void changeAllowComment(Integer id, Integer allowComment){
        if(id != null && allowComment != null){
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("id", id);
            map.put("val", allowComment);
            HttpUtils.post(ConfUtils.getResourceString("url") + "/back/entity/change/allow_comment/goods", map);
        }
    }

    private void changeAllowComment(String ids, Integer allowComment){
        if(ids!= null && allowComment != null){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ids", ids);
            map.put("val", allowComment);
            HttpUtils.post(ConfUtils.getResourceString("url") + "/back/entity/batch_change/allow_comment/goods", map);
        }
    }

    /**
     * 人工修改分类信息
     * @param goodsDTO
     */
    private void changeCategory(GoodsDTO goodsDTO){
        Integer category = goodsDTO.getCategory();
        String ids = goodsDTO.getId().toString();
        changeCategory(category, ids);
    }

    private void changeCategory(Integer category, String ids){
        if(category != null){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("'goods_ids'", ids);
            map.put("'category_id'", category);
            HttpUtils.post(ConfUtils.getResourceString("url") + "/back/goods/poly/category/edit", map);
        }
    }


    /***
     * 根据产品名称查产品
     * @param title
     * @return
     */
    public List<GoodsByNameItems> findByName(String title){
        return goodsOldMapper.findByName(title);
    }
}
