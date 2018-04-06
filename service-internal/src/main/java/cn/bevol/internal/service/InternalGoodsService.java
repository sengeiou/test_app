package cn.bevol.internal.service;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.client.StaticClientService;
import cn.bevol.conf.client.ConfUtils;
import cn.bevol.entity.service.CacheService;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.mybatis.dao.BackGoodsMapper;
import cn.bevol.mybatis.dao.GoodsExtMapper;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.dto.GoodsDTO;
import cn.bevol.mybatis.model.*;
import cn.bevol.entity.service.GoodsService;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;
import com.io97.utils.DateUtils;
import com.io97.utils.MD5Utils;
import com.io97.utils.http.HttpUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
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
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsExtMapper goodsExtMapper;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CacheService cacheService;

    @Resource
    private GoodsService goodsService;

    @Autowired
    private BackGoodsMapper backGoodsMapper;

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
                goodsMapper.saveGoodsBaseInfo(goodsDTO);
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
                goodsMapper.saveGoodsBaseList(goodsDTO);
                saveGoodsMongoData(goodsDTO, idsArr);
            }
            if(goodsDTO.getImage() != null){
                //批量删除图片时静态化
                List<String> mids = goodsMapper.getMidListById(idsArr);
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
                goodsExtMapper.saveGoodsExtCps(goodsExt);
                String mid = goodsMapper.getMidById(goodsId);
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
        goodsMapper.addGoodsBaseInfo(goodsDTO);
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
        goodsExtMapper.addGoodsExtInfo(goodsExt);
    }

    private void saveGoodsMid(Long id, String mid){
        goodsMapper.saveGoodsMid(id, mid);
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
            mid = goodsMapper.getMidById(goodsDTO.getId());
        }
        goodsStatic(mid);
    }

    private void goodsStatic(String mid){
        //清理缓存
        cacheService.cleanProducts(mid);
        //静态化
        staticClientService.goodsStatic(mid);
    }

    private void manualMadeCategory(GoodsDTO goodsDTO){
        Integer id = goodsDTO.getId().intValue();
        Integer category = goodsDTO.getCategory();
        if(category != null){
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("goods_ids", id);
            map.put("category_id", category);
            HttpUtils.post(ConfUtils.getResourceString("url") + "/back/goods/poly/category/edit", map);
        }
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

    /**
     * 批量
     * @param goodsDTO
     * @param ids
     */
    private void changeAllowComment(GoodsDTO goodsDTO, String ids){
        Integer allowComment = goodsDTO.getAllowComment();
        changeAllowComment(ids, allowComment);
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

    /**
     * 新增标签和相应的标签规则
     * @param tagName
     * @param rules  逗号分隔
     * @return
     */
    public ReturnData addTagRule(String tagName,String rules){
        try{
            if(!StringUtils.isBlank(tagName) && !StringUtils.isBlank(rules)){
                //ValJason解析
                int result=0;
                GoodsTag goodsTag=new GoodsTag();
                List<GoodsTag> gtList=goodsService.getAllGoodsTag();
                for(GoodsTag gt:gtList){
                    //标签名已经存在
                    if(gt.getName().equals(tagName)){
                        return ReturnData.FAILURE;
                    }
                }

                //标签名不存在插入
                result=backGoodsMapper.addTag(tagName,new Date().getTime()/1000);
                if(result!=1){
                    return ReturnData.FAILURE;
                }
                goodsTag=backGoodsMapper.getTagByTagName(tagName);
                if(null!=goodsTag && goodsTag.getId()!=0){
                    result=backGoodsMapper.addTagRule(goodsTag.getId(),rules,goodsTag.getCreateStamp());
                }
                if(result!=1){
                    return ReturnData.FAILURE;
                }
            }else{
                return ReturnData.FAILURE;
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            logger.error("method:addTagRule arg:{tagName:" + tagName +"	rules:"+rules+ "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /**
     * 根据tag_id 跟新两张表
     * @param tagId
     * @param tagName
     * @param rules
     * @return
     */
    public ReturnData editTagRule(long tagId,String tagName,String rules){
        try{
            if(!StringUtils.isBlank(tagName) && !StringUtils.isBlank(rules)){
                int result=0;
                result=backGoodsMapper.updateGoodsTag(tagId,tagName);
                if(result!=1){
                    return ReturnData.FAILURE;
                }
                result=backGoodsMapper.updateGoodsTagRule(tagId,rules);
                if(result!=1){
                    return ReturnData.FAILURE;
                }
            }else{
                return ReturnData.FAILURE;
            }
            return new ReturnData();
        }catch(Exception e){
            logger.error("method:editTagRule arg:{tagName:" + tagName +"	rules:"+rules+ "	tagId:"+tagId+ "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }


    /**
     * 标签对应的规则
     * @param pager
     * @param pageSize
     * @return
     */
    public ReturnListData findGoodsTag(final int pager, final int pageSize){
        try {
            long start=0L;
            if(pager>1){
                start=Long.valueOf((pager-1)*pageSize);
            }
            List<GoodsTag> gtList=backGoodsMapper.getTag(start,pageSize);
            List<GoodsRule> grList=goodsService.getAllGoodsRule();
            List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();
            for(int i=0;i<gtList.size();i++){
                List list=new ArrayList();
                String tagName=gtList.get(i).getName();
                long tagId=gtList.get(i).getId();
                for(GoodsRule gr:grList){
                    if(gr.getTagId()==tagId){
                        list.add(gr);
                    }
                }
                gtList.get(i).setRuleList(list);
            }

            long total=gtList.size();
            return new ReturnListData(gtList,total);
        } catch (Exception e) {
            logger.error("method:findGoodsTag arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnListData.ERROR;
    }

    /**
     * 根据tag_id查成分
     * @param tagId
     * @param isMain
     * @param pager
     * @param pageSize
     * @return
     */
    public ReturnListData findCpsByTagId(Long tagId,int isMain,int pager,int pageSize){
        try{
            if(null!=tagId && tagId!=0){
                long start=0;
                List<GoodsTagComposition> gtcList=null;
                if(pager>1){
                    start=Long.valueOf((pager-1)*pageSize);
                }
                //判断is_main
                gtcList=backGoodsMapper.getTagCompositionByIsMain(tagId, isMain, pager, pageSize);
                return new ReturnListData(gtcList,gtcList.size());
            }else{
                return ReturnListData.ERROR;
            }
        }catch(Exception e){
            logger.error("method:findCpsByTagId arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnListData.ERROR;
    }


    /**
     * 添加标签成分关系
     * @param tagIds
     * @param compositionIds
     * @param isMain  1为核心成分
     * @return
     */
    public ReturnData addTagComposition(String tagIds,String compositionIds,int isMain){
        try {
            if(!StringUtils.isBlank(tagIds) && !StringUtils.isBlank(compositionIds)){
                List<GoodsTagComposition> allGtcList= getAllGoodsTagComposition();
                String[] tag_idss=tagIds.split(",");
                String[] composition_idss=compositionIds.split(",");
                List<Map> listMap=new ArrayList<Map>();
                for(int i=0;i<tag_idss.length;i++){
                    List<Long> existCpsList=new ArrayList<Long>();
                    List<GoodsTagComposition> gtcList=new ArrayList<GoodsTagComposition>();
                    for(GoodsTagComposition gtc:allGtcList){
                        if(gtc.getTagId()==Long.valueOf(tag_idss[i])){
                            gtcList.add(gtc);
                        }
                    }
                    //tag_idss.get[i]中存在的cps  --gtcList
                    if(gtcList.size()>0){
                        for(GoodsTagComposition gtc:gtcList){
                            for(int j=0;j<composition_idss.length;j++){
                                if(gtc.getCompositionId()==Long.valueOf(composition_idss[j])){
                                    //要添加的tag_idss.get[i]中的cps --存在
                                    existCpsList.add(Long.valueOf(composition_idss[j]));
                                }
                            }
                        }
                    }

                    //tag存在  cps存在 --部分新成分
                    if(existCpsList.size()>0){
                        for(int j=0;j<composition_idss.length;j++){
                            Map<String,Object> map=new HashMap<String,Object>();
                            if(!existCpsList.contains(Long.valueOf(composition_idss[j]))){
                                map.put("tagId", tag_idss[i]);
                                map.put("compositionId", composition_idss[j]);
                                map.put("status", 1);
                                map.put("createStamp", new Date().getTime()/1000);
                                map.put("isMain", isMain);
                                if(isMain==1){
                                    map.put("mainName", "Y");
                                }else{
                                    map.put("mainName", "");
                                }
                                listMap.add(map);
                            }
                        }
                        //tag存在 cps不存在 	--新的成分
                    }else if(gtcList.size()>0 && (existCpsList.size()==0 || null==existCpsList)){
                        for(int j=0;j<composition_idss.length;j++){
                            Map<String,Object> map=new HashMap<String,Object>();
                            map.put("tagId", tag_idss[i]);
                            map.put("compositionId", composition_idss[j]);
                            map.put("status", 1);
                            map.put("createStamp", new Date().getTime()/1000);
                            map.put("isMain", isMain);
                            if(isMain==1){
                                map.put("mainName", "Y");
                            }else{
                                map.put("mainName", "");
                            }
                            listMap.add(map);
                        }
                    }
                }
                if(listMap.size()>0){
                    backGoodsMapper.insertBatch(listMap);
                }
                return ReturnData.SUCCESS;
            }else{
                return ReturnData.ERROR;
            }
        } catch (Exception e) {
            logger.error("method:addTagComposition arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }


    public List<GoodsTagComposition> getAllGoodsTagComposition(){
        return new CacheableTemplate<List<GoodsTagComposition>>(cacheProvider) {
            @Override
            protected List<GoodsTagComposition> getFromRepository() {
                try {
                    List<GoodsTagComposition> gtcList=backGoodsMapper.getAllTagComposition();
                    return gtcList;
                } catch (Exception e) {
                    logger.error("method:getAllGoodsTagComposition arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
                }
                return new ArrayList();
            }
            @Override
            protected boolean canPutToCache(List<GoodsTagComposition> returnValue) {
                return (returnValue != null && !returnValue.isEmpty());
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "BackGoodsService.getAllGoodsTagComposition" ), true);
    }

    /**
     * 根据tagId 删除成分 status=1
     * 删除标签成分关系
     * @param tagIds
     * @param compositionIds
     * @return
     */
    public ReturnData delTagComposition(String tagIds,String compositionIds){
        try{
            if(!StringUtils.isBlank(tagIds) && !StringUtils.isBlank(compositionIds)){
                List<Map> listMap=new ArrayList<Map>();
                String[] tagIdss=tagIds.split(",");
                for(int i=0;i<tagIdss.length;i++){
                    backGoodsMapper.delBatch(tagIdss[i],compositionIds);
                }
            }else{
                return ReturnData.ERROR;
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            logger.error("method:delTagComposition arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /**
     * 手动修改产品标签结果
     * @param tagIds
     * @param goodsIds
     * @return
     */
    public ReturnData madeEditTagResult(String tagIds,String goodsIds){
        try{
            if(!StringUtils.isBlank(tagIds) && !StringUtils.isBlank(goodsIds)){
                //修改之前 验证是否存在该goods_id
                String[] tagIdss=tagIds.split(",");
                String[] goodsIdss=goodsIds.split(",");
                List<GoodsTag> gtList=goodsMapper.getAllTag();
                //根据goodsId找到tagId
                for(int i=0;i<goodsIdss.length;i++){
                    GoodsTagResult gtr=goodsMapper.getTagResultByGoodsId(Long.valueOf(goodsIdss[i]));
                    boolean flag=false;

                    //表中存在
                    if(null!=gtr){
                        String madeTagIds="";
                        String madeTagNames="";
                        for(GoodsTag gt:gtList){
                            for(int j=0;j<tagIdss.length;j++){
                                if(Long.valueOf(tagIdss[j])==gt.getId()){
                                    madeTagIds+=gt.getId()+",";
                                    madeTagNames+=gt.getName()+",";
                                }
                            }
                        }
                        if(!StringUtils.isBlank(madeTagIds)){
                            madeTagIds=madeTagIds.substring(0,madeTagIds.length()-1);
                            madeTagNames=madeTagNames.substring(0,madeTagNames.length()-1);
                            gtr.setTagIds(madeTagIds);
                            gtr.setTagNames(madeTagNames);
                            gtr.setMadeTagIds(madeTagIds);
                            gtr.setMadeTagNames(madeTagNames);
                            gtr.setMadeDelete(0);
                            flag=true;
                        }

                    }
                    if(flag){
                        backGoodsMapper.updateGoodsResult(gtr);
                    }
                }
            }else{
                return ReturnData.ERROR;
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            logger.error("method:madeEditTagResult arg:{tagIds:"+tagIds  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /**
     * 查询含有某个标签的产品
     * @param tagId
     * @param size
     * @param pageSize
     * @return
     */
    public ReturnListData findGoodsByTagId(Long tagId,int size,int pageSize){
        try {
            List<GoodsTagResult> gtrList=getGoodsByTagId(tagId,size,pageSize);
            String ids="";
            for(GoodsTagResult gtr:gtrList){
                ids+=gtr.getGoodsId()+",";
            }
            List<Goods> glist=new ArrayList<Goods>();
            if(!StringUtils.isBlank(ids)){
                ids=ids.substring(0,ids.length()-1);
                glist=goodsMapper.getGoodsByIds(ids);
            }
            return new ReturnListData(glist,glist.size());
        } catch (Exception e) {
            logger.error("method:findGoodsByTagId arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnListData.ERROR;
    }


    /**
     * 删除含有某个标签的产品 --批量清除产品
     * @param tagId
     * @return
     */
    public ReturnData delGoodsByTagId(Long tagId){
        try {
            List<GoodsTag> tlist=goodsMapper.getAllTag();
            boolean flag=false;
            for(int i=0;!flag && i<tlist.size();i++){
                if(tlist.get(i).getId()==tagId){
                    flag=true;
                }
            }
            if(flag){
                backGoodsMapper.delGoodsByTagId(tagId);
            }else{
                return ReturnData.FAILURE;
            }
            return ReturnData.SUCCESS;
        } catch (Exception e) {
            logger.error("method:delGoodsByTagId arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /**
     * 查找含有某个标签的产品(多个)
     * @param tagId
     * @param size
     * @param pageSize
     * @return
     */
    public List<GoodsTagResult> getGoodsByTagId(final Long tagId,final int size,final int pageSize){
        return new CacheableTemplate<List<GoodsTagResult>>(cacheProvider) {
            @Override
            protected List<GoodsTagResult> getFromRepository() {
                try {
                    Long start=0L;
                    if(size>1){
                        start=Long.valueOf((size-1)*pageSize);
                    }
                    List<GoodsTagResult> gtrList=backGoodsMapper.getGoodsByTagId(tagId,start,pageSize);
                    return gtrList;
                } catch (Exception e) {
                    logger.error("method:getGoodsByTagId arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
                }
                return new ArrayList();
            }
            @Override
            protected boolean canPutToCache(List<GoodsTagResult> returnValue) {
                return (returnValue != null);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "BackGoodsService.getGoodsByTagId" ), true);
    }

    /**
     * 手动增加一条数据
     * @param tagIds
     * @param goodsIds
     * @return
     */
    public ReturnData madeAddResult(String tagIds,String goodsIds){
        try{
            if(!StringUtils.isBlank(goodsIds) && !StringUtils.isBlank(tagIds)){
                List<GoodsTag> gtList=goodsMapper.getAllTag();
                String[] goodsIdss=goodsIds.split(",");
                String[] tagIdss=tagIds.split(",");
                Map<Long,String> map=new HashMap<Long,String>();
                for(int i=0;i<goodsIdss.length;i++){
                    GoodsTagResult goodsTagResult=goodsMapper.getTagResultByGoodsId(Long.valueOf(goodsIdss[i]));
                    boolean flag=false;
                    String madeTagIds="";
                    String madeTagNames="";

                    if(null!=goodsTagResult){
                        flag=true;
                    }else{
                        goodsTagResult=new GoodsTagResult();
                    }

                    for(GoodsTag gt:gtList){
                        for(int j=0;j<tagIdss.length;j++){
                            if(tagIdss[j].equals(gt.getId()+"")){
                                madeTagIds+=gt.getId()+",";
                                madeTagNames+=gt.getName()+",";
                                map.put(gt.getId(), gt.getName());
                            }
                        }
                    }

                    goodsTagResult.setCreateStamp(new Date().getTime()/1000);
                    //不存在
                    if(!flag && !StringUtils.isBlank(madeTagIds)){
                        madeTagIds=madeTagIds.substring(0,madeTagIds.length()-1);
                        madeTagNames=madeTagNames.substring(0,madeTagNames.length()-1);
                        goodsTagResult.setGoodsId(Long.valueOf(goodsIdss[i]));
                        goodsTagResult.setMadeTagIds(madeTagIds);
                        goodsTagResult.setMadeTagNames(madeTagNames);
                        goodsTagResult.setTagIds(madeTagIds);
                        goodsTagResult.setTagNames(madeTagNames);
                        if(!StringUtils.isBlank(goodsTagResult.getTagIds())){
                            backGoodsMapper.insertResult(goodsTagResult);
                        }
                    }else if(flag && !StringUtils.isBlank(madeTagIds)){
                        //有goods_id  该记录添加tagIds  不重复
                        String idss=goodsTagResult.getTagIds();
                        String tagNames=goodsTagResult.getTagNames();
                        String idss2=idss;
                        for(int j=0;j<tagIdss.length;j++){
                            if(!(","+idss+",").contains(","+tagIdss[j]+",")){
                                idss+=","+tagIdss[j];
                                tagNames+=","+map.get(Long.valueOf(tagIdss[j]));
                            }
                        }
                        //原标签为空
                        if(StringUtils.isBlank(idss2) && !StringUtils.isBlank(idss)){
                            idss=idss.substring(1);
                            tagNames=tagNames.substring(1);
                        }
                        goodsTagResult.setMadeTagIds(idss);
                        goodsTagResult.setMadeTagNames(tagNames);
                        goodsTagResult.setTagIds(idss);
                        goodsTagResult.setTagNames(tagNames);
                        goodsTagResult.setMadeDelete(0);
                        backGoodsMapper.updateGoodsResult(goodsTagResult);
                    }
                }
            }else{
                return ReturnData.FAILURE;
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            logger.error("method:madeAddResult arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /**
     * 手动删除产品的某个标签
     * @param tagIds
     * @param goodsIds
     * @return
     */
    public ReturnData madeDelResult(String tagIds, String goodsIds) {
        try{
            if(!StringUtils.isBlank(goodsIds) && !StringUtils.isBlank(tagIds)){
                String[] goodsIdss=goodsIds.split(",");
                for(int i=0;i<goodsIdss.length;i++){
                    //查找该goodsId
                    GoodsTagResult goodsTagResult=goodsMapper.getTagResultByGoodsId(Long.valueOf(goodsIdss[i]));
                    boolean flag=false;
                    if(null==goodsTagResult){
                        goodsTagResult=new GoodsTagResult();
                    }

                    String newTagIds="";
                    String newTagNames="";
                    boolean flag2=false;
                    //标签不为空才能删除标签
                    if(!StringUtils.isBlank(goodsTagResult.getTagIds())){
                        String[] goodsTagIds=goodsTagResult.getTagIds().split(",");
                        String[] goodsTagNames=goodsTagResult.getTagNames().split(",");
                        for(int j=0;j<goodsTagIds.length;j++){
                            //判断该产品是否含有要删除的标签  只删除含有的 不含有不做处理
                            if(!(","+tagIds+",").contains(","+goodsTagIds[j]+",")){
                                newTagIds+=goodsTagIds[j]+",";
                                newTagNames+=goodsTagNames[j]+",";
                                flag2=true;
                            }
                        }

                        if(!StringUtils.isBlank(newTagIds)){
                            //删除该产品部分标签
                            newTagIds=newTagIds.substring(0, newTagIds.length()-1);
                            newTagNames=newTagNames.substring(0, newTagNames.length()-1);
                        }else{
                            //删除该产品所有标签
                            goodsTagResult.setMadeDelete(1);
                            flag2=true;
                        }
                    }

                    if(flag2){
                        goodsTagResult.setMadeTagIds(newTagIds);
                        goodsTagResult.setMadeTagNames(newTagNames);
                        goodsTagResult.setTagIds(newTagIds);
                        goodsTagResult.setTagNames(newTagNames);
                        goodsTagResult.setUpdateTime(new Date().getTime()/1000);
                        backGoodsMapper.updateGoodsResult(goodsTagResult);
                    }
                }
            }else{
                return ReturnData.FAILURE;
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            logger.error("method:madeDelResult arg:{goodsIds:"+goodsIds+"	tagIds:"+tagIds  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /**
     * 后台修改分类
     * @param categoryId
     * @param goodsIds
     * @return
     */
    public ReturnData madeEditCategory(int categoryId, String goodsIds) {
        try{
            if(!StringUtils.isBlank(goodsIds) && categoryId!=0){
                String[] goodsIdss=goodsIds.split(",");
                for(int j=0;j<goodsIdss.length;j++){
                    List<Map<String,Object>> newCategoryList=new ArrayList<Map<String,Object>>();
                    List<Map<String,Object>> categoryList=goodsService.getPolyCategoryBygoodsIdsLocal(goodsIdss[j]);
                    if(categoryList.size()>0){
                        for(int i=0;i<categoryList.size();i++){
                            Map<String,Object> map=new HashMap<String,Object>();
                            map.put("goodsId", categoryList.get(i).get("goods_id"));
                            map.put("madeCategoryId", categoryId);
                            map.put("categoryId", categoryId);
                            map.put("updateTime", new Date().getTime()/1000);
                            map.put("existCategoryIds", 0);
                            newCategoryList.add(map);
                        }
                        backGoodsMapper.madeUpdateCategory(newCategoryList);
                    }
                }
            }else{
                return ReturnData.FAILURE;
            }
            return ReturnData.SUCCESS;
        }catch(Exception e){
            logger.error("method:madeEditCategory arg:{goodsIds:"+goodsIds+"	categoryId:"+categoryId  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /***
     * 根据产品名称查产品
     * @param title
     * @return
     */
    public List<GoodsByNameItems> findByName(String title){
        return goodsMapper.findByName(title);
    }
}
