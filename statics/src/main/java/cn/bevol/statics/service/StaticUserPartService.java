package cn.bevol.statics.service;

import cn.bevol.statics.entity.model.Tags;
import cn.bevol.statics.service.iservice.StaticInfoService;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-5-16.
 */
@Service
public class StaticUserPartService implements StaticInfoService {
    private static Logger logger = LoggerFactory.getLogger(StaticUserPartService.class);
    @Resource
    private TagsService tagsService;

    private Map<String, Object> getUserTabs(Map<String, Object> dataMap){
        ArrayList<Map<String, String>> tagsMap = getUserTabs();
        dataMap.put("tags", tagsMap);
        return dataMap;
    }

    private ArrayList<Map<String, String>> getUserTabs(){
        ArrayList<Map<String, String>> tagsMap = new ArrayList<Map<String, String>>();
        List<Tags> list = tagsService.findByTabs("user");
        for (Tags tags : list) {
            Map<String, String> tag_info = new HashMap<String, String>();
            tag_info.put("id", tags.getId());
            tag_info.put("title", tags.getTag());
            tagsMap.add(tag_info);
        }
        return tagsMap;
    }

    public Boolean staticUserPartPage(String id){
        Boolean res1 = null;
        try {
            res1 = staticUserPartListsMPage(id);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return res1;
    }

    /**
     * 按type类型静态化user_part页面
     * @param id
     * @param type
     * @return
     * @throws IOException
     */
    public Boolean staticUserPartPage(String id, String type){
        try {
            Boolean res = false;
            switch (type) {
                case "mobile":
                    res = staticUserPartListsMPage(id);
                    break;
            }
            return res;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }

    private Boolean staticUserPartListsMPage(String id) throws IOException {
        Map<String, Object> dataMap = prepareListsStaticInfo(id, "mobile");
        return SinglePageService.staticGeneralPage(dataMap, "mobile", "user_lists", "topic/user_part/"+id+".html");
    }

    /**
     * 准备话题/清单列表数据
     * @param id
     * @param platform
     * @return
     */
    private Map<String, Object> prepareListsStaticInfo(String id, String platform){
        if (platform.equals("m")){
            platform = "mobile";
        }
        try {
            //info详情
            String topic_api_url = ConfUtils.mps.get("url") + "user_part/detail/lists";
            Map<String, String> param = new HashMap<String, String>();
            param.put("id", id);
            String res = HttpUtils.post(topic_api_url, param);
            JSONObject resJson = JSONObject.fromObject(res);

            //评论
            String user_lists_api_url = ConfUtils.mps.get("url") + "entity/comment4/lists/user_part_lists";
            Map<String, Object> commentData = SinglePageService.getCommentInfo(id, user_lists_api_url);
            JSONArray commentList = (JSONArray) commentData.get("commentList");
            int commentTotal = (int) commentData.get("total");

            if (resJson.has("ret")) {
                int ret = resJson.getInt("ret");
                if (ret == 0) {
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap = SinglePageService.getStaticInfo(dataMap, platform, "user_lists");
                    JSONObject resultObject = resJson.getJSONObject("result");
                    String title = resultObject.getString("title");
                    String hitNum = resultObject.getString("hitNum");
                    String keywordsString = null;
                    if (resultObject.has("tags")) {
                        JSONArray tags_ids = resultObject.getJSONArray("tags");
                        ArrayList<Map<String, String>> tags = getUserTabs();
                        ArrayList<String> tagsArr = new ArrayList<String>();
                        for (int i = 0; i < tags_ids.size(); i++) {
                            for (Map<String, String> tag : tags) {
                                if (tags_ids.getString(i).equals(tag.get("id"))) {
                                    tagsArr.add(tag.get("title"));
                                }
                            }
                        }
                        dataMap.put("tags", tagsArr);
                        StringBuilder keywords = new StringBuilder();
                        for (String aTagsArr : tagsArr) {
                            keywords.append(aTagsArr);
                        }
                        keywordsString = keywords.toString();
                    }


                    dataMap.put("id", id);
                    dataMap.put("title", title + "-美丽修行网");
                    dataMap.put("hitNum", hitNum);
                    dataMap.put("user_lists_title", title);
                    if (org.apache.commons.lang.StringUtils.isEmpty(keywordsString)) {
                        dataMap.put("keywords", title);
                    } else {
                        dataMap.put("keywords", keywordsString);
                    }
                    dataMap.put("description", title);
                    dataMap.put("result", resultObject);
                    if (resultObject.has("userBaseInfo")) {
                        JSONObject userBaseInfo = resultObject.getJSONObject("userBaseInfo");
                        if (userBaseInfo.has("skinResults")) {
                            String skinResults = userBaseInfo.getString("skinResults");
                            dataMap.put("skinDesc", SinglePageService.getSkinDesc(skinResults));
                        }
                    }
                    dataMap.put("createStampText", DateUtils.timeStampParseDate(resultObject.getInt("createStamp"), "MM-dd HH:mm"));

                    dataMap.put("commentTotal", commentTotal);
                    dataMap.put("comments", commentList);

                    return dataMap;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("话题/清单数据准备出错："+id);
        }
        return null;
    }

    /***
     * 没查到的话题处理
     * @param id
     * @param type
     * @return
     */
    public String getBackUserPartLists(String id, String type) throws IOException {
        String ftlName=null;
        String path=null;
        String platform=null;
        switch(type){
            case "mobile":
                ftlName = "user_lists";
                path = "topic/user_part/"+id+".html";
                platform = "mobile";
                break;
        }
        Map<String,Object> dataMap = prepareListsStaticInfo(id, platform);
        if(dataMap == null){
            logger.error(platform + "站" + type+id + "心得模板" + ":回源数据读取出错！");
        }else{
            if(ftlName != null){
                String html = FreemarkerService.getHtml(ftlName, platform, dataMap, true);
                if(!StringUtils.isEmpty(html)){
                    OSSService.uploadHtml2OSS(html, path, platform);
                }else{
                    logger.error(platform + "站" + type + id + "心得模板" + ":回源生成出错！");
                }
                return html;
            }else{
                logger.error(platform + "站" + type + id + "心得模板" + ":回源操作type传参错误！");
            }

        }
        return "404";
    }

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        StaticUserPartService staticUserPartService = (StaticUserPartService) context.getBean("staticUserPartService");
        staticUserPartService.getBackUserPartLists("108", "mobile");
        //userPartService.staticUserPartPage("69");

    }

    @Override
    public Map<String, Object> getStaticInfo(Map<String, Object> dataMap) {
        dataMap = getUserTabs(dataMap);
        return dataMap;
    }

    @Override
    public Map<String, Object> getStaticLoopInfo(Map<String, Object> dataMap, Integer curPage) {
        return null;
    }
}
