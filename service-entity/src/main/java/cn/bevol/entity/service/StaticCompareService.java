package cn.bevol.entity.service;

import cn.bevol.entity.service.utils.ConfUtils;
import cn.bevol.log.LogMethod;
import cn.bevol.util.ReturnData;
import com.io97.utils.DateUtils;
import com.io97.utils.JsonUtils;
import com.io97.utils.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class StaticCompareService {

    /**
     * 生成产品对比分享页面
     * @param mids
     * @return
     * @throws IOException
     */
    @LogMethod
    public ReturnData staticComparePage(String mids, Integer force) throws IOException {
        String path = "app_share/compare/goods/" +
                DateUtils.format(new Date(), "yyyyMMdd") + "/" +
                mids.replace(",", "/");
        String url = ConfUtils.mps.get("m_domain") + "/" + path;
        if(force == 1 || isTodayFirstShare(path)) {
            //当天第一次分享或者强制时,生成页面
            String compareGoodsApi = ConfUtils.mps.get("url") + "/compare/goods";
            Map<String, String> param = new HashMap<String, String>();
            param.put("mids", mids);
            String res = HttpUtils.post(compareGoodsApi, param);
            Map dataMap = JsonUtils.toMap(res);
            if(dataMap != null && Integer.valueOf(dataMap.get("ret")+"") == 0){
                dataMap = (Map) dataMap.get("result");
                dataMap = SinglePageService.getStaticInfo(dataMap, "mobile", "wx_compare_goods");
                //设置title
                JSONArray entityInfo = JSONArray.fromObject(dataMap.get("entityInfo"));
                String title1 = entityInfo.getJSONObject(0).getJSONObject("goods").getString("title");
                String title2 = entityInfo.getJSONObject(1).getJSONObject("goods").getString("title");
                dataMap.put("title", title1+" 对比 "+title2);
                //支持人数设置
                JSONObject entity= JSONObject.fromObject(dataMap.get("entity"));
                ArrayList supportNum = new ArrayList();
                supportNum.add(entity.getInt("cid1LikeNum"));
                supportNum.add(entity.getInt("cid2LikeNum"));
                dataMap.put("supportNum", supportNum);
                //获取讨论
                String sid = entity.getString("sid");
                String discussApi = ConfUtils.mps.get("url") + "/discuss/list/compare_goods";
                Map<String, String> discussParam = new HashMap<String, String>();
                discussParam.put("s_id", sid);
                String discussResult = HttpUtils.post(discussApi, discussParam);
                JSONObject discussResultObject = JSONObject.fromObject(discussResult);
                if(discussResultObject.getJSONArray("result").size() > 0 ){
                    JSONObject discuss = discussResultObject.getJSONArray("result").getJSONObject(0);
                    discuss.element("skinArr" ,SinglePageService.getSkinDesc(discuss.getString("skinResults")));
                    dataMap.put("discuss", discuss);
                    dataMap.put("discussTotal", discussResultObject.getInt("total"));
                }

                Boolean success = SinglePageService.staticGeneralPage(dataMap, "mobile", "wx_compare_goods", path);
                if (success) {
                    return new ReturnData(url);
                }
            }else{
                return new ReturnData("api对比接口异常", -1);
            }
        }else{
            //非当天第一次分享,直接返回url
            return new ReturnData(url);
        }
        return ReturnData.ERROR;
    }

    /**
     * 判断是否存在path
     * @param path
     * @return
     */
    private Boolean isTodayFirstShare(String path){
        //不存在则是第一次分享
        return !OSSService.isExist(OSSService.getMClient(), OSSService.getMBucketName(), path);
    }

}
