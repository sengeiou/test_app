package cn.bevol.statics.service;

import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.PropertyUtils;
import cn.bevol.util.http.HttpUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * Created by zhangcheng on 17-2-27.
 */
@Service
public class StaticSkinPlanService {
    private static Logger logger = LoggerFactory.getLogger(StaticSkinPlanService.class);

    @Resource
    private FreemarkerService freemarkerService;

    public Map<String, Object> staticSkinPlan(String user_id, String category_pid) throws IOException {
        return staticSkinPlan(user_id, category_pid, false);
    }

    private Map<String, Object> staticSkinPlan(String userId, String categoryPid, Boolean force_update) throws IOException {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Map<String, Object> returnMap = new HashMap<String, Object>();

        JSONObject resultJson = getJsonInfo(userId, categoryPid);
        dataMap = getDataInfo(dataMap);
        if (!resultJson.isEmpty()) {
            getUpdateStamp(dataMap, resultJson);
            String firstUrl = isFirstShare(dataMap, userId, categoryPid);
            if (force_update || StringUtils.isEmpty(firstUrl)) {
                dataMap = getUserInfo(dataMap, resultJson);
                returnMap = generateStaticPage(dataMap, userId, categoryPid);
            }else{
                returnMap.put("ret", 0);
                returnMap.put("url", firstUrl);
            }
        }else{
            returnMap.put("ret", -1);
        }
        return returnMap;
    }

    private JSONObject getJsonInfo(String userId, String categoryPid) {
        String skinPlanApi = ConfUtils.mps.get("url") + "/common/skin_protection_goods/list";
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("user_id", userId);
        param.put("category_pid", categoryPid);
        String res = HttpUtils.post(skinPlanApi, param);
        JSONObject resJson = JSONObject.fromObject(res);
        JSONObject resultJson = new JSONObject();
        if (resJson.has("ret")) {
            int ret = resJson.getInt("ret");
            if (ret == 0) {
                resultJson = resJson.getJSONObject("result");
            }
        }
        return resultJson;
    }

    private Map<String, Object> getDataInfo(Map<String, Object> dataMap){
        dataMap.put("js", ConfUtils.mps.get("wx_js"));
        dataMap.put("css", ConfUtils.mps.get("wx_css"));
        String[] imgs = ConfUtils.imgs;
        String img = imgs[(new Random().nextInt(2))];
        dataMap.put("img", img);
        return dataMap;
    }


    /*private Map<String, Object> getStaticInfo(Map<String, Object> dataMap, String userId, String categoryPid) {
        String skinPlanApi = ConfUtils.mps.get("url") + "/common/skin_protection_goods/list";
        HashMap<String, String> param = new HashMap<String, String>();
        Map<String, Object> infoResult = new HashMap<String, Object>();
        param.put("user_id", userId);
        param.put("category_pid", categoryPid);
        String res = HttpUtils.post(skinPlanApi, param);
        logger.debug("Start====================================Start");
        logger.debug(ConfUtils.mps.get("url"));
        logger.debug(userId);
        logger.debug(categoryPid);
        logger.debug(skinPlanApi);
        logger.debug(res);
        logger.debug("END========================================END");
        JSONObject resJson = JSONObject.fromObject(res);
        if (resJson.has("ret")) {
            int ret = resJson.getInt("ret");
            if (ret == 0) {
                //设置基础js/css/img
                dataMap.put("js", ConfUtils.mps.get("wx_js"));
                dataMap.put("css", ConfUtils.mps.get("wx_css"));
                String[] imgs = ConfUtils.imgs;
                String img = imgs[(new Random().nextInt(2))];
                dataMap.put("img", img);
                JSONObject resultJson = resJson.getJSONObject("result");
                infoResult.put("resultJson", resultJson);
                infoResult.put("result", true);
            } else if (ret == -2) {
                //分类不存在
                returnMap.put("ret", -2);
                logger.error("查询分类不存在");
                infoResult.put("result", false);
            }
        } else {
            //连接失败
            returnMap.put("ret", -1);
            logger.error("请求api服务失败");
            infoResult.put("result", false);
        }

        infoResult.put("dataMap", dataMap);
        infoResult.put("returnMap", returnMap);
        return infoResult;
    }*/


    /**
     * 获取分享时间戳
     */
    private void getUpdateStamp(Map<String, Object> dataMap, JSONObject resultJson) {
        String timeStamp = resultJson.getJSONObject("category").getString("updateStamp");
        dataMap.put("timeStamp", timeStamp);
    }

    /**
     * 处理用户信息
     */
    private Map<String, Object> getUserInfo(Map<String, Object> dataMap, JSONObject resultJson) {
        JSONObject userInfoObject = resultJson.getJSONObject("userinfo");
        /*String headImgUrl = userInfoObject.getString("headimgurl");
        if (StringUtils.isEmpty(headImgUrl) || headImgUrl.substring(0, 3).equals("file")) {
            headImgUrl = dataMap.get("img") + "/wx/images/newyixiujie.png";
            userInfoObject.element("headimgurl", headImgUrl);
            resultJson.element("userinfo", userInfoObject);
        }*/
        String skinResults = null;
        if(userInfoObject.containsKey("skinResults")) {
            skinResults = userInfoObject.getString("skinResults");
        }
        if (!StringUtils.isEmpty(skinResults)) {
            String[] skinResultsArray = skinResults.split("_");
            String skinTypeJson = ConfUtils.mps.get("skinType");
            JSONObject skinTypeObject = JSONObject.fromObject(skinTypeJson);
            int k = 1;
            for (String skinResult : skinResultsArray) {
                if (!StringUtils.isEmpty(skinResult) && !"null".equals(skinResult)) {
                    userInfoObject.put("skinResult" + k, skinTypeObject.getString(skinResult));
                    k++;
                }
            }
            resultJson.element("userinfo", userInfoObject);
        }
        dataMap.put("result", resultJson);
        return dataMap;
    }

    private Map<String, Object> generateStaticPage(Map<String, Object> dataMap, String userId, String categoryPid) throws IOException {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        if (dataMap != null && dataMap.size() > 0) {
            String timeStamp = dataMap.get("timeStamp").toString();
            String htmlName = userId + "_" + categoryPid + "_" + timeStamp;
            boolean result = freemarkerService.createHtmlFile("wx_skin_plan.html", htmlName, "mobile", dataMap);
            if (result) {
                String filePath = FreemarkerService.freemarkerPath + "/mobile/" + htmlName + ".html";
                String uploadPath = "app_share/plan/" + DateUtils.format(new Date(), "yyyyMMdd") + "/" + htmlName + ".html";
                OSSService.upload2OSS(filePath, uploadPath, "mobile");
                returnMap.put("ret", 0);
                returnMap.put("url", "https://m.bevol.cn/" + uploadPath);
            } else {
                logger.error("user_id:" + userId + ",分享护肤方案静态化失败！");
                returnMap.put("ret", -3);
            }
        }
        return returnMap;
    }

    /**
     * 检查近7天是否有分享过
     *
     * @param userId
     * @param categoryPid
     * @return
     */
    private String isFirstShare(Map<String, Object> dataMap, String userId, String categoryPid) {
        Calendar day = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            if (i > 0) {
                day.add(Calendar.DAY_OF_MONTH, -1);
            }
            String path =  "app_share/plan/" +
                    DateUtils.format(day.getTime(), "yyyyMMdd") + "/" + userId + "_" + categoryPid +
                    "_" + dataMap.get("timeStamp") + ".html";
            if (OSSService.isExist(OSSService.getMClient(), OSSService.getMBucketName(), path)) {
                return ConfUtils.mps.get("m_domain") + "/" + path;
            }
        }

        return "";
    }

    /**
     * @param user_id
     * @param category_pid
     * @return
     * @throws IOException
     */
    public Map<String, Object> getBackSkinPlan(String user_id, String category_pid) throws IOException {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        int ret = (int) staticSkinPlan(user_id, category_pid, true).get("ret");
        if (ret == 0) {
            dataMap.put("path", "mobile/wx_skin_plan");
        } else {
            dataMap.put("path", "404");
        }
        return dataMap;
    }

    /**
     * 删除7天前的护肤方案
     */
    public void delete7DaysAgoFiles(){
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DAY_OF_MONTH, -7);
        String dir = "app_share/plan/" + DateUtils.format(day.getTime(), "yyyyMMdd") + "/";
        List<String> list = OSSService.getListByDir(dir, OSSService.getMClient(), OSSService.getMBucketName());
        for(String key : list){
            OSSService.deleteMObject(key);
        }
    }
}
