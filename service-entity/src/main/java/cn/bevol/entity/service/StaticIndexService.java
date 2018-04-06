package cn.bevol.entity.service;


import cn.bevol.entity.service.iservice.StaticInfoService;

import cn.bevol.entity.service.utils.ConfUtils;
import cn.bevol.mybatis.dao.CompositionMapper;
import cn.bevol.mybatis.dao.ConfigMapper;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.dao.SeoLinksMapper;
import cn.bevol.mybatis.model.Config;
import cn.bevol.mybatis.model.Goods;
import cn.bevol.mybatis.model.SeoLinks;
import com.io97.utils.JsonUtils;
import com.io97.utils.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-2-16.
 */
@Service
public class StaticIndexService implements StaticInfoService {
    private final static String HOTKEY_WORDS_URL = "http://source.bevol.cn/app/hotkeyword.json";

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private CompositionMapper compositionMapper;

    @Resource
    private SeoLinksMapper seoLinksMapper;

    /**
     * 获取静态化数据
     * @param dataMap
     * @return
     */
    @Override
    public Map<String, Object>  getStaticInfo(Map<String, Object> dataMap){
        dataMap = getKeywordsInfo(dataMap);
        if(dataMap.containsKey("platform") && "mobile".equals(dataMap.get("platform"))){
            dataMap = getMobileIndexInfo(dataMap);
        }else{
            dataMap = getCategoryList(dataMap);
            dataMap = getArticleList(dataMap);
            dataMap = getTopGoodsList(dataMap);
            dataMap = getCompositionTotal(dataMap);
            dataMap = getFriendlyLinks(dataMap);
        }
        return dataMap;
    }

    @Override
    public Map<String, Object> getStaticLoopInfo(Map<String, Object> dataMap, Integer curPage) {
        return null;
    }

    private Map<String, Object> getFriendlyLinks(Map<String, Object> dataMap){
        List<SeoLinks>  seoLinksList = seoLinksMapper.selectAll();
        dataMap.put("links", seoLinksList);
        return dataMap;
    }

    /**
     *获取移动站首页数据
     * @param dataMap
     * @return
     */
    private Map<String, Object> getMobileIndexInfo(Map<String, Object> dataMap){
        HashMap<String, Integer> param = new HashMap<String, Integer>();
        //移动站将position_type设置为2
        param.put("position_type", 2);
        String info = HttpUtils.post(dataMap.get("url") + "/index5", param);
        JSONObject infoObj = JSONObject.fromObject(info);
        if(infoObj.has("ret") && infoObj.getInt("ret") == 0){
            dataMap = getBannerInfo(dataMap, infoObj);
            dataMap = getEssenceCommentInfo(dataMap, infoObj);
        }
        return dataMap;
    }

    /**
     *获取banner数据
     * @param dataMap
     * @param infoObj
     * @return
     */
    private Map<String, Object> getBannerInfo(Map<String, Object> dataMap, JSONObject infoObj){
        JSONArray banner = infoObj.getJSONObject("result").getJSONArray("find");
        for(int i=0; i<banner.size(); i++){
            String item = banner.getString(i);
            JSONObject itemObj = JSONObject.fromObject(item);
            if(itemObj.getJSONObject("param").getInt("type") != 10) {
                String href = itemObj.getJSONObject("page").getJSONArray("h5").getString(itemObj.getJSONObject("param").getInt("type"));
                itemObj.element("href", href);
                banner.element(i, itemObj);
            }
        }
        dataMap.put("banner", banner);
        return dataMap;
    }

    /**
     *获取精选点评数据
     * @param dataMap
     * @param infoObj
     * @return
     */
    private Map<String, Object> getEssenceCommentInfo(Map<String, Object> dataMap, JSONObject infoObj){
        JSONArray essenceComment = infoObj.getJSONObject("result").getJSONArray("essenceComment");
        for(int i=0; i<essenceComment.size(); i ++){
            String essenceCommentString = essenceComment.getString(i);
            JSONObject essenceCommentObj = JSONObject.fromObject(essenceCommentString);
            if(essenceCommentObj.getInt("type") == 2){
                String[] skinDesc = SinglePageService.getSkinDesc(essenceCommentObj.getString("skinResults"));
                essenceCommentObj.element("skinDesc", skinDesc);
                essenceComment.element(i, essenceCommentObj);
            }
        }

        dataMap.put("essenceComment", essenceComment);
        return dataMap;
    }

    /**
     * 获取成分总数
     * @param dataMap
     * @return
     */
    private Map<String, Object> getCompositionTotal(Map<String, Object> dataMap){
        int total = compositionMapper.selectTotal();
        dataMap.put("composition_total", total);
        return dataMap;
    }


    /**
     * 获取热搜关键字
     * @param dataMap
     */
    private Map<String, Object> getKeywordsInfo(Map<String, Object> dataMap){
        String keywordsJson = HttpUtils.get(HOTKEY_WORDS_URL);
        if(keywordsJson != null){
            JSONObject keywordsObject = JSONObject.fromObject(keywordsJson);
            JSONArray keywordsArray;
            try{
                keywordsArray = keywordsObject.getJSONObject("data").getJSONObject("hotkeyword").getJSONArray("goods");
            }catch (Exception e){
                String defualtKeywords = ConfUtils.getResourceString("default_index_keywords");
                keywordsArray = JSONArray.fromObject(defualtKeywords);
            }
            ArrayList<String> keywords  = new ArrayList<String>();
            for(Object object : keywordsArray){
                JSONObject keywordObject = JSONObject.fromObject(object);
                String title = keywordObject.getString("title");
                keywords.add(title);
            }
            dataMap.put("hot_keywords",keywords);
        }
        return dataMap;
    }

    /**
     * 获取分类数据
     * @param dataMap
     * @return
     */
    private Map<String, Object> getCategoryList(Map<String, Object> dataMap){
        //从config表中获取categorylists
        Config categoryListsConfig = configMapper.selectByKey("categorylists");
        String categoryListsJsonString = categoryListsConfig.getValue();
        JSONArray categoryListsJson = JSONArray.fromObject(categoryListsJsonString);
        List<String> categoryIds = new ArrayList<String>();
        Map<String, String> innerMap = new HashMap<>();
        List<Object> outterList = new ArrayList<>();
        for(Object JsonObject: categoryListsJson ){
            innerMap.put("category_" + JsonUtils.getSimpleMapStringProperty(JsonObject.toString(), "id"),
                    JsonUtils.getSimpleMapStringProperty(JsonObject.toString(), "name"));
            //将id从categorylists中取出
            categoryIds.add("category_"+JsonUtils.getSimpleMapStringProperty(JsonObject.toString(), "id"));
        }
        int size = categoryIds.size();
        List<Config> configList = configMapper.getConfigByKeys(categoryIds.toArray(new String[size]));
        for(Config config:configList){
            String key = config.getKey();
            Integer id = Integer.parseInt(key.replace("category_", ""));
            JSONArray idJosnArr = JSONArray.fromObject(config.getValue());
            String[] idArr = new String[8];
            //取前8个产品
            for(int i=0; i < 8; i++){
                idArr[i] = idJosnArr.getString(i);
            }
            List<Goods> goodsList = goodsMapper.getGoodsByIds(StringUtils.join(idArr, ","));
            JSONArray goodsListJosn = JSONArray.fromObject(JsonUtils.toJson(goodsList));
            outterList.add(new Object[]{
                    innerMap.get(key),
                    goodsListJosn,
                    id
            });
        }
        dataMap.put("categories", outterList);
        return dataMap;
    }

    /**
     * 获取发现文章数据
     * @param dataMap
     * @return
     */
    private Map<String, Object> getArticleList(Map<String, Object> dataMap){
        String newUrl = ConfUtils.mps.get("url") + "/entity/list2/find";
        String hotUrl = newUrl + "?sort_type=1";
        String newRes = HttpUtils.post(newUrl, "post");
        String hotRes = HttpUtils.post(hotUrl, "post");
        JSONArray newArr = JSONObject.fromObject(newRes).getJSONArray("result");
        JSONArray hotArr = JSONObject.fromObject(hotRes).getJSONArray("result");
        dataMap.put("new_find", newArr);
        dataMap.put("hot_find", hotArr);
        return dataMap;
    }

    /**
     * 获取产品数据
     * @param dataMap
     * @return
     */
    private Map<String, Object> getTopGoodsList(Map<String, Object> dataMap){
        List<Goods> goodsList = goodsMapper.getTopGoods();
        JSONArray goodsListJosn = JSONArray.fromObject(JsonUtils.toJson(goodsList));
        dataMap.put("top_goods", goodsListJosn);
        return dataMap;
    }
}
