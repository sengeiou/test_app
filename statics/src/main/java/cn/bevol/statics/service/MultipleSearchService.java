package cn.bevol.statics.service;

import cn.bevol.statics.cache.CacheKey;
import cn.bevol.statics.cache.redis.RedisCacheProvider;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mysens on 17-2-13.
 */

@Service
public class MultipleSearchService {

    @Autowired
    private RedisCacheProvider cacheProvider;

    private String composition_compares_url = ConfUtils.mps.get("url") + "composition/compares";
    private String goods_search_url = ConfUtils.mps.get("searchUrl") + "goods/index3";

    /**
     * 多成分查找
     * @param keywords
     * @param ip
     * @return
     */
    public Map<String, Object> multiple_search(String keywords, String ip){
        Map<String, Object> outMap = new HashMap<String, Object>();
        if(isValidRequest(ip, outMap)){
            StringBuffer cps = new StringBuffer();
            outMap = getCompositionDetails(outMap, keywords, cps);
            outMap = getGoodsDetails(outMap, cps);
        }
        return outMap;
    }

    /**
     * 获取成分详细
     * @param outMap
     * @param keywords
     * @param cps
     * @return
     */
    private Map<String, Object> getCompositionDetails(Map<String, Object> outMap , String keywords, StringBuffer cps){
        //成分批量对比接口
        HashMap<String, String> param1 = new HashMap<String, String>();
        param1.put("names", keywords);
        String apiResult = HttpUtils.post(composition_compares_url, param1);
        JSONObject apiObj = JSONObject.fromObject(apiResult);
        if(apiObj.has("result")) {
            JSONArray apiArray = apiObj.getJSONArray("result");
            ArrayList result1 = new ArrayList();
            for (int i = 0; i < apiArray.size(); i++) {
                JSONObject object = (JSONObject) apiArray.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("user_title", object.getString("userTitle"));
                if (object.has("mid")) {
                    cps.append(object.getString("id") + ",");
                    map.put("title", object.getString("title"));
                    map.put("english", object.getString("english"));
                    map.put("active", object.getString("active"));
                    map.put("acneRisk", object.getString("acneRisk"));
                    map.put("safety", object.getString("safety"));
                    map.put("used_num", object.getString("usedNum"));
                    map.put("mid", object.getString("mid"));
                    map.put("used_title", object.getString("usedTitle"));
                }
                result1.add(map);
            }
            outMap.put("result", result1);
            outMap.put("ret", 0);
            outMap.put("total", result1.size());
        }else{
            outMap.put("ret", -1);
        }
        return outMap;
    }

    /**
     * 获取产品详细
     * 成分数大于5，返回相关产品
     * @param outMap
     * @param cps
     * @return
     */
    private Map<String, Object> getGoodsDetails(Map<String, Object> outMap, StringBuffer cps){
        if(outMap!=null && outMap.containsKey("total") && (int)outMap.get("total")>5){
            ArrayList result2 = new ArrayList();
            //可能查找的产品
            if(cps.length()>0){
                cps.deleteCharAt(cps.length() - 1);
            }
            String goods_search_api = goods_search_url+"?cps="+cps.toString();
            String goodsResult = HttpUtils.get(goods_search_api);
            JSONObject goodsObj = JSONObject.fromObject(goodsResult);
            if (goodsObj.has("data")) {
                JSONObject goodsData = goodsObj.getJSONObject("data");
                if (goodsData.has("items")) {
                    JSONArray items = goodsData.getJSONArray("items");
                    int size = items.size()>4?4:items.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject object = (JSONObject) items.get(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("title", object.getString("title"));
                        map.put("english", object.getString("alias"));
                        map.put("mid", object.getString("mid"));
                        map.put("image", object.getString("image"));
                        result2.add(map);
                    }
                }
            }
            outMap.put("goods", result2);
        }
        return outMap;
    }

    /**
     * 判断请求是否有效
     * ３秒内同一IP只允许一次请求
     * @param ip
     * @param outMap
     * @return
     */
    private boolean isValidRequest(String ip, Map<String, Object> outMap){
        String ipToken = "composition_multiple_search_by_" + ip;
        CacheKey key = new CacheKey(CACHE_NAME.NAMESPACE,CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE, ipToken);
        String last_time;
        try{
            last_time = cacheProvider.get(key).toString();
        }catch(NullPointerException e){
            cacheProvider.put(key, System.currentTimeMillis()/1000 + "");
            return true;
        }

        int new_time = (int) (System.currentTimeMillis()/1000) - Integer.parseInt(last_time);
        if(new_time<3){
            outMap.put("ret", -2);
            return false;
        }else{
            cacheProvider.put(key, System.currentTimeMillis()/1000 + "");
        }
        return true;
    }
}
