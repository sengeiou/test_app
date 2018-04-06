package cn.bevol.statics.service;

import cn.bevol.statics.dao.mapper.GoodsOldMapper;
import cn.bevol.statics.dao.mapper.MetaInfoOldMapper;
import cn.bevol.statics.entity.model.MetaInfo;
import cn.bevol.statics.service.iservice.StaticInfoService;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.JsonUtils;
import cn.bevol.util.StringUtil;
import cn.bevol.util.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by zhangcheng on 17-2-8.
 */

@Service
public class SinglePageService {
    private static Logger logger = LoggerFactory.getLogger(SinglePageService.class);

    @Resource
    private GoodsOldMapper goodsOldMapper;

    /**
     * 静态化需要分页的页面（行业资讯，专题）
     * @param ftlName
     * @param htmlName
     * @param serviceName
     * @param platform
     * @param ext
     * @param path
     * @return
     */
    public Boolean staticLoopPage(String ftlName, String htmlName, String serviceName, String platform, String ext, String path){
        if (platform.equals("m")){
            platform = "mobile";
        }

        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap = getServiceInfo(dataMap, serviceName);
        dataMap = getMetaInfo(dataMap, ftlName);
        dataMap = getStaticInfo(dataMap, "pc", htmlName);

        int total = (int) dataMap.get("total");
        int pageSize = (int) dataMap.get("pageSize");
        StaticInfoService staticInfoService = (StaticInfoService) dataMap.get("service");

        int pageNum = (int) Math.ceil((float) total/pageSize);

        dataMap.put("pageNum", pageNum);
        for(int i=0; i< pageNum; i++){
            int curPage = i + 1;
            dataMap = staticInfoService.getStaticLoopInfo(dataMap, curPage);
            /*if("pc".equals(platform)){
                dataMap = sidebarService.getSidebar(dataMap);
            }*/
            try {
                String html = FreemarkerService.getHtml(ftlName, platform, dataMap);
                if (!StringUtils.isEmpty(html)) {
                    if (!StringUtils.isEmpty(ext)) {
                        htmlName += "." + ext;
                    }
                    if(StringUtils.isEmpty(path)){
                        path = htmlName;
                    }else{
                        path = path.replace("_", "/");
                        path = path + "/" + htmlName;
                    }
                    if(curPage > 1){
                        path = htmlName + "_"+curPage;
                    }
                    OSSService.uploadHtml2OSS(html, path, platform);
                } else {
                    logger.error(platform + "站:" + ftlName + "生成出错！");
                    return false;
                }
                OSSService.uploadHtml2OSS(html, path, platform);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                logger.error(platform + "站:" + ftlName + "生成出错！");
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(platform + "站:" + ftlName + "上传出错！");
                return false;
            }
        }
        return true;
    }

    /**
     * 获取产皮总数
     * @param dataMap
     * @return
     */
    private Map<String, Object> getProductTotal(Map<String, Object> dataMap){
        int total = goodsOldMapper.selectTotal();
        dataMap.put("product_total", total);
        dataMap.put("product_title_total", Math.round(total/10000));
        return dataMap;
    }

    @Resource
    private MetaInfoOldMapper metaInfoOldMapper;
    /**
     * 静态化单独页面
     * @param ftlName
     * @param htmlName
     * @param platform
     * @return
     * @throws IOException
     */
    public Boolean staticPage(String ftlName, String htmlName, String serviceName, String platform, String ext, String path) {
        if (platform.equals("m")){
            platform = "mobile";
        }
        try {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("platform", platform);
            dataMap = getMetaInfo(dataMap, ftlName);
            dataMap = getStaticInfo(dataMap, platform, htmlName);
            dataMap = getServiceInfo(dataMap, serviceName);
            dataMap.put("staticType", htmlName);

            dataMap = getProductTotal(dataMap);

            //产品、成分、发现列表页读取侧边栏数据
            /*if("pc".equals(platform) && ("product".equals(htmlName) || "composition".equals(htmlName) || "find".equals(htmlName))){
                dataMap = sidebarService.getSidebar(dataMap);
            }*/
            String html = FreemarkerService.getHtml(ftlName, platform, dataMap);
            if (!StringUtils.isEmpty(html)) {
                if (!StringUtils.isEmpty(ext)) {
                    htmlName += "." + ext;
                }
                if(StringUtils.isEmpty(path)){
                    path = htmlName;
                }else{
                    path = path.replace("_", "/");
                    path = path + "/" + htmlName;
                }
                OSSService.uploadHtml2OSS(html, path, platform);
            } else {
                logger.error(platform + "站:" + ftlName + "渲染模板出错！　地址:"+path);
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean staticPage(String name, String htmlName, String serviceName, String platform, String ext) {
        return staticPage(name, htmlName, serviceName, platform, ext, "");
    }

    public Boolean staticPage(String name, String htmlName, String serviceName, String platform) {
        return staticPage(name, htmlName, serviceName, platform, "", "");
    }

    @Deprecated
    public Boolean staticFindPage(String ftlName, String htmlName, String id, String platform) throws IOException {
        if (platform.equals("m")){
            platform = "mobile";
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        //info详情
        String find_api_url = ConfUtils.mps.get("url") + "find/info/" + id;
        String res = HttpUtils.get(find_api_url);
        JSONObject resJson = JSONObject.fromObject(res);
        //评论
        String find_comment_url = ConfUtils.mps.get("url") + "entity/comment4/lists/find";

        if(resJson.has("ret")){
            int ret = resJson.getInt("ret");
            if(ret == 0){
                dataMap = getMetaInfo(dataMap, ftlName);
                dataMap = getStaticInfo(dataMap, platform, htmlName);
                String descp = resJson.getJSONObject("result").getString("descp");
                String title = resJson.getJSONObject("result").getString("title");

                Map<String, Object> commentData = getCommentInfo(id, find_comment_url);
                JSONArray commentList = (JSONArray) commentData.get("commentList");
                int commentTotal = (int) commentData.get("total");
                dataMap.put("comments", commentList);
                dataMap.put("commentTotal", commentTotal);
                dataMap.put("content", descp);
                dataMap.put("title", title);
                dataMap.put("id", id);
                String html = FreemarkerService.getHtml(ftlName, platform, dataMap);
                if(!StringUtils.isEmpty(html)){
                    OSSService.uploadHtml2OSS(html, htmlName, platform);
                    //兼容旧版app
                    OSSService.uploadHtml2OSS(html, "article/"+id, platform);
                }else{
                    logger.error(platform+"站:"+ftlName+"生成出错！");
                    return false;
                }
            }else{
                logger.error(platform+"站:"+ftlName+"获取数据出错！");
                return false;
            }
        }else{
            logger.error(platform+"站:"+ftlName+"获取数据出错！");
            return false;
        }
        return true;
    }

    @Deprecated
    public Boolean staticListsPage(String ftlName, String htmlName, String id, String platform) throws IOException {
        if (platform.equals("m")){
            platform = "mobile";
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        String lists_api_url = ConfUtils.mps.get("url") + "hotlist/detail?id=" + id;
        String res = HttpUtils.get(lists_api_url);
        JSONObject resJson = JSONObject.fromObject(res);
        if(resJson.has("ret")){
            int ret = resJson.getInt("ret");
            if(ret == 0){
                dataMap = getMetaInfo(dataMap, ftlName);
                dataMap = getStaticInfo(dataMap, platform, htmlName);

                JSONArray goods = resJson.getJSONObject("result").getJSONArray("goods");
                JSONObject detail = resJson.getJSONObject("result").getJSONObject("detail");
                String image = detail.getString("image");
                String descp = detail.getString("descp");
                String title = detail.getString("title");
                dataMap.put("goods", goods);
                dataMap.put("image", image);
                dataMap.put("descp", descp);
                dataMap.put("title", title);
                String html = FreemarkerService.getHtml(ftlName, platform, dataMap);
                if(!StringUtils.isEmpty(html)){
                    OSSService.uploadHtml2OSS(html, htmlName, platform);
                    //兼容旧版app
                    OSSService.uploadHtml2OSS(html, "lists/"+id, platform);
                }else{
                    logger.error(platform+"站:"+ftlName+"生成出错！");
                    return false;
                }
            }else{
                logger.error(platform+"站:"+ftlName+"获取数据出错！");
                return false;
            }
        }else{
            logger.error(platform+"站:"+ftlName+"获取数据出错！");
            return false;
        }
        return true;
    }

    public Boolean staticFindJson(Integer id) throws IOException {
        String find_api_url = ConfUtils.mps.get("url") + "find/info/" + id;
        String res = HttpUtils.get(find_api_url);
        JSONObject resJson = JSONObject.fromObject(res);
        if(resJson.has("ret")){
            int ret = resJson.getInt("ret");
            if(ret == 0){
                //String htmlName = "app/article/info/id/"+id+".json";
                //新版app json 不在内置css  从v2.8开始直接访问接口不在使用json文件
                //OSSService.uploadJson2Source(res, htmlName);
                //兼容旧版app
                uploadOldFindJson(id, resJson);
            }else{
                return false;
            }
        }
        return true;
    }

    /**
     * 上传旧版发现文章地址
     */
    private void uploadOldFindJson(Integer id, JSONObject resJson) throws IOException {
        String oldHtmlName = "app/find/new_info/id/"+id+".json";
        JSONObject resultJson = resJson.getJSONObject("result");
        Map<String, Object> innerMap = new HashMap<String, Object>();
        Map<String, Object> middleMap = new HashMap<String, Object>();
        Map<String, Object> outterMap = new HashMap<String, Object>();
        String descp = resultJson.getString("descp");
        //读取模板
        String css=StringEscapeUtils.unescapeHtml(ConfUtils.getResourceString("old_app_json_styple"));

        innerMap.put("descp", css+descp);
        innerMap.put("content", resultJson.getString("descp"));
        innerMap.put("id", resultJson.getString("id"));
        innerMap.put("title", resultJson.getString("title"));
        innerMap.put("header_image", resultJson.getString("headerImage"));
        middleMap.put("0", innerMap);
        outterMap.put("data", middleMap);
        String oldJson = JsonUtils.toJson(outterMap);
        OSSService.uploadJson2Source(oldJson, oldHtmlName);
    }

    /**
     * 获取头部信息
     * @param dataMap
     * @param ftlName
     * @return
     */
    public static Map<String, Object> getMetaInfo(Map<String, Object> dataMap , String ftlName){
        String metaJson = ConfUtils.getResourceString(ftlName);
        JSONObject metaObject = JSONObject.fromObject(metaJson);
        dataMap.put("title", metaObject.get("title"));
        dataMap.put("keywords", metaObject.get("keywords"));
        dataMap.put("description", metaObject.get("description"));
        return dataMap;
    }

    /**
     * 获取seo信息
     * type 请参考禅道文档 http://zentao.bevol.cn/index.php?m=doc&f=view&docID=69
     * @param id
     * @param type
     * @return
     */
    public MetaInfo getSeoMetaInfo(Integer id, Integer type){
        return metaInfoOldMapper.getSeoMataInfo(id, type);
    }

    /**
     * 获取静态资源信息
     * @param dataMap
     * @param platform
     * @return
     */
    public static Map<String, Object> getStaticInfo(Map<String, Object> dataMap, String platform, String staticType){
        String[] imgs =  ConfUtils.imgs;
        String img = imgs[(new Random().nextInt(2))];
        dataMap.put("img", img);
        dataMap.put("staticType", staticType);
        dataMap.put("url", ConfUtils.mps.get("url"));
        dataMap.put("api_url_https", ConfUtils.mps.get("api_url_https"));
        dataMap.put("version", ConfUtils.mps.get("version"));
        if(platform.equals("pc")){
            dataMap.put("js", ConfUtils.mps.get("pc_js"));
            dataMap.put("css", ConfUtils.mps.get("pc_css"));
        }else{
            dataMap.put("js", ConfUtils.mps.get("wx_js"));
            dataMap.put("css", ConfUtils.mps.get("wx_css"));
        }
        return dataMap;
    }

    /**
     * 获取单例类的数据
     * @param dataMap
     * @param serviceName
     * @return
     */
    private Map<String, Object> getServiceInfo(Map<String, Object> dataMap , String serviceName) {
        try{
            if(!StringUtils.isEmpty(serviceName)) {
                WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();

                StaticInfoService staticInfoService = (StaticInfoService) wac.getBean("static" + StringUtil.captureName(serviceName) + "Service");
                dataMap = staticInfoService.getStaticInfo(dataMap);
                dataMap.put("service", staticInfoService);
            }
            return dataMap;
        }catch(NoSuchBeanDefinitionException e){
            return dataMap;
        }
    }

    /**
     * 获取评论数据
     * @param id
     * @param comment_url
     * @return
     */
    public static Map<String, Object> getCommentInfo(String id, String comment_url){
        Map<String, Object> commentData = new HashMap<String, Object>();
        //评论
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("id", id);
        value.put("pager", "1");
        value.put("pageSize", "10");
        String commmentJson = HttpUtils.post(comment_url, value);
        JSONArray commentList = new JSONArray();
        if(commmentJson!="fault"){
            JSONObject commentObj = JSONObject.fromObject(commmentJson);
            if(commentObj.has("result")){
                JSONObject commentResult = commentObj.getJSONObject("result");
                commentList = commentResult.getJSONArray("list");
                /*String skinTypeJson = ConfUtils.mps.get("skinType");
                JSONObject skinTypeObject = JSONObject.fromObject(skinTypeJson);*/
                for (int i = 0; i < commentList.size(); i++) {
                    JSONObject goodComment = (JSONObject) commentList.get(i);
                    goodComment.put("createStampText", DateUtils.timeStampParseDate(goodComment.getInt("updateStamp"), "MM-dd HH:mm"));
                    if(goodComment.has("skinResults")) {
                        String skinResults = goodComment.getString("skinResults");
                        if (!StringUtils.isEmpty(skinResults)) {
                            String[] skinDesc = getSkinDesc(skinResults);
                            int k = 1;
                            for(String skin: skinDesc){
                                goodComment.put("skinResult" + k, skin);
                                k++;
                            }
                            commentList.element(i, goodComment);
                        }
                    }
                }
                commentData.put("commentList", commentList);
                commentData.put("total", commentResult.getInt("total"));
            }
        }
        return commentData;
    }

    /**
     * 获取肤质信息
     * @param skinResults
     * @return
     */
    public static String[] getSkinDesc(String skinResults){
        String skinTypeJson = ConfUtils.mps.get("skinType");
        JSONObject skinTypeObject = JSONObject.fromObject(skinTypeJson);
        String[] skinResultsArray = skinResults.split("_");
        String[] skinDesc = new String[4];
        int i = 0;
        for (String skinResult : skinResultsArray) {
            if (!StringUtils.isEmpty(skinResult) && !"null".equals(skinResult)) {
                skinDesc[i] = skinTypeObject.getString(skinResult);
                i++;
            }
        }
        return skinDesc;
    }

    /**
     * 生成静态页面通用方法
     * @param dataMap
     * @param platform
     * @param ftlName
     * @param path
     * @return
     * @throws IOException
     */
    public static Boolean staticGeneralPage(Map<String,Object> dataMap, String platform, String ftlName, String path) throws IOException {
        if(dataMap == null){
            logger.error("staticGeneralPage:" + platform + "站" + ftlName + "模板" + ":dataMap为null数据读取出错！ path="+path);
            return false;
        }else{
            String html = FreemarkerService.getHtml(ftlName, platform, dataMap);
            html = StringEscapeUtils.unescapeHtml(html);
            if(!StringUtils.isEmpty(html)){
                OSSService.uploadHtml2OSS(html, path, platform);
            }else{
                logger.error("staticGeneralPage:" + platform + "站" + ftlName + "模板" + ":渲染模板出错！ path="+path);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取实体state
     * @param entity
     * @param ids
     * @param jsonArray
     * @return
     */
    public static JSONArray getEntityState(String entity, List ids, JSONArray jsonArray){
        String[] fields = {"hitNum", "likeNum", "commentNum"};
        HashMap idParam = new HashMap(1);
        idParam.put("ids", StringUtils.join(ids, ","));
        String stateData = HttpUtils.post(ConfUtils.mps.get("url") +"entity/state2/"+entity, idParam);
        JSONArray stateJsonArr = JSONObject.fromObject(stateData).getJSONArray("result");
        if(jsonArray.size() >0 && jsonArray.size() == stateJsonArr.size()){
            for(int i=0 ; i<jsonArray.size(); i++){
                for(String field:fields){
                    jsonArray.getJSONObject(i).element(
                        field, stateJsonArr.getJSONObject(i).getString(field)
                    );
                }
            }
        }
        return jsonArray;
    }

}
