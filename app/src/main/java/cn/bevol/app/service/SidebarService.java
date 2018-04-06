package cn.bevol.app.service;

import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.model.entity.EntityFind;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.util.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-7-6.
 */
@Service("sidebarService")
public class SidebarService {


    @Autowired
    private GoodsOldMapper goodsMapper;

    @Resource
    private MongoTemplate mongoTemplate;

    private static long entity_find_total;

    private static Long minGoodsId;
    private static Long maxGoodsId;

    public SidebarService(){
        System.out.println("start sidebar ==============");
        System.out.println(entity_find_total);
        System.out.println(minGoodsId);
        System.out.println(maxGoodsId);
    }

    /**
     * mongo产品查询条件(有图片有mid) 获取方法
     * @return
     */
    private Criteria getGoodsCr() {
        ArrayList<String> param = new ArrayList<String>();
        param.add("");
        param.add(null);
        Criteria goodsCr = Criteria.where("hidden").is(0);
        goodsCr.and("deleted").is(0);
        goodsCr.and("mid").exists(true).nin(param);
        goodsCr.and("image").exists(true).nin(param);
        return goodsCr;
    }

    /**
     * mongo发现文章查询条件(有图片有mid) 获取方法
     * @return
     */
    private Criteria getFindCr(){
        Criteria findCr = Criteria.where("hidden").is(0);
        findCr.and("deleted").is(0);
        return findCr;
    }

    /**
     * 设置侧边栏缓存
     */
    public void generateSideBarCache(){
        Criteria findCr = getFindCr();
        Query findQuery = new Query();
        entity_find_total = mongoTemplate.count(findQuery.addCriteria(findCr), "entity_find");


        Criteria goodsCr = getGoodsCr();
        Query goodsQueryAsc = new Query();
        List<EntityGoods> minEntityGoods = mongoTemplate.find(goodsQueryAsc.addCriteria(goodsCr).with(new Sort(Sort.Direction.ASC, "id")).limit(1), EntityGoods.class, "entity_goods");
        minGoodsId = minEntityGoods.get(0).getId();
        Query goodsQueryDesc = new Query();
        List<EntityGoods> maxEntityGoods = mongoTemplate.find(goodsQueryDesc.addCriteria(goodsCr).with(new Sort(Sort.Direction.DESC, "id")).limit(1), EntityGoods.class, "entity_goods");
        maxGoodsId = maxEntityGoods.get(0).getId();
    }

    /**
     * 读取侧边栏数据 不带条件
     * @param dataMap
     * @return
     */
    public Map<String, Object> getSidebar(Map<String, Object>dataMap){
        dataMap = getSidebar(dataMap, 0, 0);
        return dataMap;
    }

    /**
     * 读取侧边栏数据 带条件
     * @param dataMap
     * @param goodsId
     * @param findId
     * @return
     */
    public Map<String, Object> getSidebar(Map<String, Object>dataMap, Integer goodsId, Integer findId){
        dataMap = getProductsByRandom(dataMap, goodsId);
        dataMap = getArticlesByRandom(dataMap, findId);
        return dataMap;
    }

    /**
     * 读取侧边栏产品数据
     * @param dataMap
     * @param id
     * @return
     */
    private Map<String, Object> getProductsByRandom(Map<String, Object> dataMap, Integer id){
        int[] random;
        if(id > 0){
            Integer total = goodsMapper.selectCategoryTotalById(id);
            if(total > 4){
                //产品总数大于４
                random = RandomUtils.randomCommon(0,total - 2, 3);
                assert random != null;
                List<Goods> entityGoodsList = new ArrayList<Goods>();
                for(int i : random){
                    Goods goods = goodsMapper.findOneCategoryById(id, i);
                    entityGoodsList.add(goods);
                }
                dataMap.put("sidebarGoodsList", entityGoodsList);
            }else{
                random = RandomUtils.randomCommon(minGoodsId.intValue(), maxGoodsId.intValue(), 3);
                assert random != null;
                List<EntityGoods> entityGoodsList = new ArrayList<EntityGoods>();
                ArrayList<Integer> hasGetIds = new ArrayList<Integer>();
                hasGetIds.add(id);
                entityGoodsList = getRandomGoodsByMongo(random, hasGetIds);
                dataMap.put("sidebarGoodsList", entityGoodsList);
            }
        }else{
            random = RandomUtils.randomCommon(minGoodsId.intValue(), maxGoodsId.intValue(), 3);
            assert random != null;
            List<EntityGoods> entityGoodsList = new ArrayList<EntityGoods>();
            ArrayList<Integer> hasGetIds = new ArrayList<Integer>();
            entityGoodsList = getRandomGoodsByMongo(random, hasGetIds);
            dataMap.put("sidebarGoodsList", entityGoodsList);
        }

        return dataMap;
    }

    /**
     * 通过mongo读取随机产品
     * @param random
     * @param hasGetIds
     * @return
     */
    public List<EntityGoods> getRandomGoodsByMongo(int[] random, ArrayList<Integer> hasGetIds){
        List<EntityGoods> entityGoodsList = new ArrayList<EntityGoods>();
        for(int i : random){
            Criteria cr = getGoodsCr();
            cr.and("id").gte(i).nin(hasGetIds);
            Query query = new Query();
            query.addCriteria(cr);
            List<EntityGoods> entityGoodsLists = mongoTemplate.find(query.limit(1), EntityGoods.class, "entity_goods");
            EntityGoods entityGoods = entityGoodsLists.get(0);
            hasGetIds.add(entityGoods.getId().intValue());
            entityGoodsList.add(entityGoods);
        }
        return entityGoodsList;
    }

    /**
     * 读取侧边栏发现文章数据
     * @param dataMap
     * @param id
     * @return
     */
    private Map<String, Object> getArticlesByRandom(Map<String, Object> dataMap, Integer id){
        int[] random = RandomUtils.randomCommon(0, (int) (entity_find_total - 1), 3);
        assert random != null;
        List<EntityFind> entityFindList = new ArrayList<EntityFind>();
        for(int i : random){
            Criteria cr = getFindCr();
            if(id>0){
                cr.and("id").ne(id);
            }
            Query query = new Query();
            query.addCriteria(cr);
            List<EntityFind> entityFinds = mongoTemplate.find(query.skip(i).limit(1), EntityFind.class, "entity_find");
            entityFindList.add(entityFinds.get(0));
        }
        dataMap.put("sidebarFindList", entityFindList);
        return dataMap;
    }
}
