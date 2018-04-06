package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.ListsOldMapper;
import cn.bevol.statics.entity.model.Lists;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mysens on 17-3-14.
 */

@Service
public class StaticListsService {
    private static Logger logger = LoggerFactory.getLogger(StaticListsService.class);
    @Resource
    ListsOldMapper listsOldMapper;



    public Boolean staticListsPage(String id) throws IOException {
        Boolean res1 = staticListsWXPage(id);
        Boolean res2 = staticListsMPage(id);
        return res1 && res2;
    }

    /**
     * 按type类型静态化清单页面
     * @param id
     * @param type
     * @return
     * @throws IOException
     */
    public Boolean staticListsPage(String id, String type) throws IOException {
        Boolean res = false;
        switch (type){
            case "mobile":
                res = staticListsMPage(id);
                break;
            case "wx":
                res = staticListsWXPage(id);
                break;
        }
        return res;
    }

    private Map<String, Object> prepareStaticInfo(String id, String platform){
        if (platform.equals("m")){
            platform = "mobile";
        }
        //info详情
        String lists_api_url = ConfUtils.mps.get("url") + "hotlist/detail2?id=" + id;
        String res = HttpUtils.get(lists_api_url);
        JSONObject resJson = JSONObject.fromObject(res);
        //评论
        String lists_comment_url = ConfUtils.mps.get("url") + "entity/comment4/lists/lists";
        //状态
        String lists_state_url = ConfUtils.mps.get("url") + "entity/state/lists";
        Map<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        String stateRes = HttpUtils.post(lists_state_url, param);
        JSONObject stateJson = JSONObject.fromObject(stateRes);
        if(resJson.has("ret")) {
            int ret = resJson.getInt("ret");
            if (ret == 0) {
                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap = SinglePageService.getStaticInfo(dataMap, platform, "lists");

                JSONArray goods = resJson.getJSONObject("result").getJSONArray("goods");
                JSONObject detail = resJson.getJSONObject("result").getJSONObject("detail");
                String image = detail.getString("image");
                String descp = detail.getString("descp");
                String title = detail.getString("title");
                String tag = detail.getString("tag");

                Map<String, Object> commentData = SinglePageService.getCommentInfo(id, lists_comment_url);
                JSONArray commentList = (JSONArray) commentData.get("commentList");
                int commentTotal = (int) commentData.get("total");
                dataMap.put("commentTotal", commentTotal);
                dataMap.put("comments", commentList);
                int collectionNum = stateJson.getJSONObject("result").getInt("collectionNum");
                int commentNum = stateJson.getJSONObject("result").getInt("commentNum");
                int hitNum = stateJson.getJSONObject("result").getInt("hitNum");

                dataMap.put("collectionNum", collectionNum);
                dataMap.put("commentNum", commentNum);
                dataMap.put("hitNum", hitNum);
                dataMap.put("goods", goods);
                dataMap.put("image", image);
                dataMap.put("descp", descp);
                String description = descp.replaceAll("\\<.*?>", "").replaceAll("\\s*|\t|\r|\n","").trim();
                if(description.length()>0 && description.length()>200){
                    description = description.substring(0,200)+"...";
                }
                dataMap.put("description", description);
                dataMap.put("keywords", tag);
                dataMap.put("title", title);
                return dataMap;
            }
        }
        return null;
    }

    private Boolean staticListsWXPage(String id) throws IOException {
        Map<String, Object> dataMap = prepareStaticInfo(id, "mobile");
        return SinglePageService.staticGeneralPage(dataMap, "mobile", "wx_lists", "app_share/lists/"+id)
                &&
                //兼容旧版
                SinglePageService.staticGeneralPage(dataMap, "mobile", "wx_lists", "lists/"+id);
    }

    private Boolean staticListsMPage(String id) throws IOException {
        Map<String, Object> dataMap = prepareStaticInfo(id, "mobile");
        return SinglePageService.staticGeneralPage(dataMap, "mobile", "lists", "lists/"+id+".html");
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
        return listsOldMapper.findByPage(paged);
    }

    private void listsStatics(Integer page){
        List<Lists> lists = findByPage(page);
        for (Lists list : lists) {
            try {
                //微信分享页面静态化
                String id = list.getId().toString();
                staticListsPage(id);
                //singlePageService.staticListsPage("wx_lists", "app_share/lists/" + id, id, "m");
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 初始化发现线程
     */
    public void initListsStatic(int arg){
        if(arg ==1 ||arg ==8){
            Executors.newFixedThreadPool(1).submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
                            int tatolPage =  listsOldMapper.selectTotal()/20+1;
                            for (int i = 1; i <= tatolPage; i++) {
                                queue.put(i);
                            }
                            int page = 1;
                            while (true) {
                                try {
                                    page = queue.take();
                                    try {
                                        listsStatics(page);
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


                }
            });
        }
    }




    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        StaticListsService staticListsService = (StaticListsService) context.getBean("staticListsService");
        //listsService.initListsStatic(1);
        staticListsService.staticListsPage("40");

    }
}
