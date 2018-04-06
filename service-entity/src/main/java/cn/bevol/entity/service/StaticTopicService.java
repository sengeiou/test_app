package cn.bevol.entity.service;

import cn.bevol.entity.service.iservice.StaticInfoService;
import cn.bevol.entity.service.utils.ConfUtils;
import cn.bevol.mybatis.dao.ListsMapper;
import cn.bevol.mybatis.dao.TagsMapper;
import cn.bevol.mybatis.model.Lists;
import cn.bevol.mybatis.model.Tags;
import com.io97.utils.db.Paged;
import com.io97.utils.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangcheng on 17-3-1.
 */

@Service
public class StaticTopicService implements StaticInfoService {
    private static Logger logger = LoggerFactory.getLogger(StaticTopicService.class);
    @Resource
    private TagsService tagsService;
    @Resource
    private ListsMapper listsMapper;
    @Resource
    StaticListsService staticListsService;
    @Resource
    private TagsMapper tagsMapper;

    @Override
    public Map<String, Object> getStaticInfo(Map<String, Object> dataMap) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("page", "1");
        param.put("pageSize", "10");
        String info = HttpUtils.post(dataMap.get("url") + "/hotlist/list2", param);
        JSONObject infoObj = JSONObject.fromObject(info);
        if(infoObj.has("ret") && infoObj.getInt("ret") == 0){
            JSONArray items = infoObj.getJSONArray("result");
            ArrayList<Long> ids = new ArrayList<Long>();
            for(Object obj : items){
                JSONObject jsonObject = JSONObject.fromObject(obj);
                ids.add(jsonObject.getLong("id"));
            }
            items = SinglePageService.getEntityState("lists", ids, items);
            dataMap.put("items", items);
        }
        return dataMap;
    }

    @Override
    public Map<String, Object> getStaticLoopInfo(Map<String, Object> dataMap, Integer curPage) {
        return null;
    }

    public Boolean staticTopicPage(String id) {
        Boolean res1;
        try {
            res1 = staticTopicMPage(id);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return res1;
    }

    /**
     * 按type类型静态化话题页面
     * @param id
     * @param type
     * @return
     * @throws IOException
     */
    public Boolean staticTopicPage(String id, String type) throws IOException {
        Boolean res = false;
        switch (type){
            case "mobile":
                res = staticTopicMPage(id);
                break;
        }
        return res;
    }

    private Boolean staticTopicMPage(String id) throws IOException {
        Map<String, Object> dataMap = prepareStaticInfo(id, "mobile");
        return SinglePageService.staticGeneralPage(dataMap, "mobile", "topic", "topic/"+id+".html");
    }

    private Map<String, Object> prepareStaticInfo(String id, String platform){
        if (platform.equals("m")){
            platform = "mobile";
        }
        //info详情
        String topic_api_url = ConfUtils.mps.get("url") + "hotlist/detail2";
        Map<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        String res = HttpUtils.post(topic_api_url, param);
        JSONObject resJson = JSONObject.fromObject(res);

        //话题内心得列表
        String user_lists_api_url = ConfUtils.mps.get("url") + "user_part/list/lists";
        //最热  sort = 1
        Map<String, String> userListsParam = new HashMap<String, String>();
        userListsParam.put("p_entity_id", id);
        userListsParam.put("pager", "1");
        userListsParam.put("pageSize", "10");
        userListsParam.put("sort", "1");
        String hotRes = HttpUtils.post(user_lists_api_url, userListsParam);
        JSONObject hotJson = JSONObject.fromObject(hotRes);
        //最新 sort = 0
        userListsParam.put("sort", "0");
        String lastestRes = HttpUtils.post(user_lists_api_url, userListsParam);
        JSONObject lastestJson = JSONObject.fromObject(lastestRes);

        //读取标签
        List<Tags> tags = tagsMapper.findByTabs("user");
        ArrayList<String> tagsList = new ArrayList<>();
        for(Tags tag : tags){
            tagsList.add(tag.getTag());
        }

        if(resJson.has("ret")) {
            int ret = resJson.getInt("ret");
            if (ret == 0) {
                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap = SinglePageService.getStaticInfo(dataMap, platform, "topic");
                JSONObject detailObject = resJson.getJSONObject("result").getJSONObject("detail");
                String title = detailObject.getString("title");
                String image = detailObject.getString("image");
                String descp = detailObject.getString("descp");
                String type = detailObject.getString("type");
                String startTime = detailObject.getString("startTime");
                String lastTime = detailObject.getString("lastTime");

                dataMap.put("id", id);
                dataMap.put("descp", descp);
                dataMap.put("title", title+"-美丽修行网");
                dataMap.put("topic_title", title);
                dataMap.put("image", image);
                dataMap.put("type", type);
                dataMap.put("lastTime", lastTime);
                dataMap.put("startTime", startTime);
                dataMap.put("tags", tagsList);
                if(tagsList.size() <= 0){
                    dataMap.put("keywords", title);
                }else{
                    dataMap.put("keywords", StringUtil.join(tagsList, ","));
                }
                descp = descp.replaceAll("\\<.*?>", "").replaceAll("\\s*|\t|\r|\n","").trim();
                if(descp.length()>0 && descp.length()>200){
                    descp = descp.substring(0,200)+"...";
                }
                dataMap.put("description", descp);

                //心得
                if(hotJson.has("ret") &&
                        hotJson.getInt("ret") == 0 &&
                        hotJson.getInt("total")>0){
                    JSONArray userListsHotArray = hotJson.getJSONArray("result");
                    for(int i=0; i < userListsHotArray.size(); i++){
                        JSONObject listsJson = JSONObject.fromObject(userListsHotArray.get(i));
                        String hitNum = listsJson.getInt("hitNum")/1000>1?
                                (listsJson.getInt("hitNum")/1000)+"k+":listsJson.getString("hitNum");
                        listsJson.element("hitNum", hitNum);
                        userListsHotArray.set(i, listsJson);
                    }
                    dataMap.put("user_lists_hot", userListsHotArray);
                }
                if(lastestJson.has("ret") &&
                        lastestJson.getInt("ret") == 0 &&
                        lastestJson.getInt("total")>0){
                    JSONArray userListsLastestArray = lastestJson.getJSONArray("result");
                    for(int i=0; i < userListsLastestArray.size(); i++){
                        JSONObject listsJson = JSONObject.fromObject(userListsLastestArray.get(i));
                        String hitNum = listsJson.getInt("hitNum")/1000>1?
                                (listsJson.getInt("hitNum")/1000)+"k+":listsJson.getString("hitNum");
                        listsJson.element("hitNum", hitNum);
                        userListsLastestArray.set(i, listsJson);
                    }
                    dataMap.put("user_lists_latest", userListsLastestArray);
                }
                return dataMap;
            }
        }
        return null;
    }

    /***
     * 没查到的话题处理
     * @param id
     * @param type
     * @return
     */
    public String getBackTopic(String id, String type) throws IOException {
        String ftlName=null;
        String path=null;
        String platform=null;
        switch(type){
            case "mobile":
                ftlName = "topic";
                path = "topic/"+id+".html";
                platform = "mobile";
                break;
        }
        Map<String,Object>	dataMap = prepareStaticInfo(id, platform);
        if(dataMap == null){
            logger.error(platform + "站" + type + "话题模板" + ":发现生成出错！");
        }else{
            if(ftlName != null){
                String html = FreemarkerService.getHtml(ftlName, platform, dataMap, true);
                if(!StringUtils.isEmpty(html)){
                    OSSService.uploadHtml2OSS(html, path, platform);
                }else{
                    logger.error(platform + "站" + type + "话题模板" + ":发现生成出错！");
                }
                return html;
            }else{
                logger.error(platform + "站" + type + "话题模板" + ":回源操作type传参错误！");
            }

        }
        return "404";
    }

    /***
     * 初始化话题线程
     */
    public Boolean initTopicStatic(int arg){
        if(arg ==1 ||arg ==8){
            Executors.newFixedThreadPool(1).submit(new Runnable() {
                public void run() {
                    try {
                        BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
                        int tatolPage =  listsMapper.selectTotal()/20+1;
                        logger.info("共"+ tatolPage + "页");
                        for (int i = 1; i <= tatolPage; i++) {
                            queue.put(i);
                        }
                        int page = 1;
                        while (!queue.isEmpty()) {
                            try {
                                page = queue.take();
                                try {
                                    topicStatics(page);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                //异常页码直接跳过  todo 后期处理异常情况
                                logger.error(e.getMessage(), e.getCause());
                            }

                            logger.info("第"+page+"页成功！");
                            Thread.sleep(500);
                        }

                    } catch (Exception e) {
                        logger.error(e.getMessage(), e.getStackTrace());
                    }

                }
            });
        }
        return true;
    }

    /***
     * 话题批量静态化
     * @param page
     */
    public void topicStatics(Integer page){
        List<Lists> lists =findByPage(page);
        for (Lists list : lists) {
            try {
                String id = list.getId().toString();
                if(0 == list.getType()){
                    staticListsService.staticListsPage(id);
                }else{
                    staticTopicPage(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发现列表
     * @param page
     * @return
     */
    private List<Lists> findByPage(Integer page) {
        Paged<Lists> paged = new Paged<Lists>();
        paged.setCurPage(page);
        paged.addOrderBy("id", "desc");
        return listsMapper.findByPage(paged);
    }

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        StaticTopicService staticTopicService = (StaticTopicService) context.getBean("staticTopicService");
        staticTopicService.initTopicStatic(1);
        //topicService.staticTopicPage("48");

    }
}
