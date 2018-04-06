package cn.bevol.internal.service;

import cn.bevol.util.ConfClient;
import cn.bevol.util.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mysens on 17-4-14.
 */

@Service
public class StaticClientService {
    private static Logger logger = LoggerFactory.getLogger(StaticClientService.class);
    private static String STATIC_API;

    {
        ConfClient confClient = new ConfClient("api");
        STATIC_API = confClient.getResourceString("static_internal_url");
    }

    private void requestStaticApi(String url, Map<String, String> map){
        HttpUtils.TriggerPost(STATIC_API + url, map);
    }

    private void requestStaticApi(String url){
        Map<String, String> map = new HashMap<String, String>();
        HttpUtils.TriggerPost(STATIC_API + url, map);
    }

    public void batchGoodsStatic(String ids){
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("ids", ids);
        requestStaticApi("/static/goods/batch", param);
    }

    public void goodsStatic(String mid){
        String goodsStaticUrl = "/static/goods/"+mid;
        requestStaticApi(goodsStaticUrl);
    }

    public void compositionStatic(String mid){
        String compositionStaticUrl = "/static/composition/"+mid;
        requestStaticApi(compositionStaticUrl);
    }

    public void seoProductAdd(String mid){
        String seoProductAddUrl = "/seo/product/add";
        Map<String, String> map = new HashMap<String, String>();
        map.put("mid", mid);
        requestStaticApi(seoProductAddUrl, map);
    }

    public void seoCompositionAdd(String mid){
        String seoCompositionAddUrl = "/seo/composition/add";
        Map<String, String> map = new HashMap<String, String>();
        map.put("mid", mid);
        requestStaticApi(seoCompositionAddUrl, map);
    }
}
