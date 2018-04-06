package cn.bevol.entity.service;

import cn.bevol.entity.service.utils.ConfUtils;
import cn.bevol.model.entity.EntityApplyGoods;
import cn.bevol.mybatis.dao.ConfigMapper;
import cn.bevol.mybatis.dao.TagsMapper;
import cn.bevol.mybatis.model.Config;
import cn.bevol.mybatis.model.Tags;
import cn.bevol.util.ReturnData;
import com.io97.utils.DateUtils;
import com.io97.utils.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
 * Created by mysens on 17-6-1.
 */
@Service
public class StaticTrialService {
    private static Logger logger = LoggerFactory.getLogger(StaticTrialService.class);

    @Resource
    private TagsMapper tagsMapper;
    @Resource
    private ConfigMapper configMapper;
    @Resource
    private MongoTemplate mongoTemplate;

    private Map<String, Object> prepareStaticInfo(String id){
        //info详情
        String api_url = ConfUtils.mps.get("url") + "entity/info/apply_goods";
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        String res = HttpUtils.post(api_url, param);
        JSONObject resJson = JSONObject.fromObject(res);

        if(resJson.has("ret")) {
            int ret = resJson.getInt("ret");
            if (ret == 0) {
                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap = SinglePageService.getStaticInfo(dataMap, "m", "");
                dataMap.put("data", resJson.getJSONObject("result").getJSONObject("entity"));
                if(resJson.getJSONObject("result").getJSONObject("entity").has("tagIds")) {
                    String tagIds = resJson.getJSONObject("result").getJSONObject("entity").getString("tagIds");
                    String[] tagIdsArr = tagIds.split(",");
                    List<Tags> tagsList = tagsMapper.findByIds(tagIdsArr);
                    dataMap.put("tagsList", tagsList);
                }
                JSONObject entityObj = resJson.getJSONObject("result").getJSONObject("entity");
                String endDate = DateUtils.timeNewsString(entityObj.getInt("lastTime"));
                dataMap.put("endDate", endDate);
                String descp = resJson.getJSONObject("result").getJSONObject("entity").getString("descp");
                JSONArray descpJson = JSONArray.fromObject(descp);
                dataMap.put("descp", descpJson);
                dataMap.put("title", entityObj.getString("title"));
                return dataMap;
            }
        }
        return null;
    }

    public ReturnData staticTrailAppPage(String id) throws IOException {
        Map<String, Object> dataMap = prepareStaticInfo(id);
        Boolean result = SinglePageService.staticGeneralPage(dataMap, "mobile", "app_trial", "app/apply_goods/"+id);
        if(result){
            return ReturnData.SUCCESS;
        }else{
            return ReturnData.ERROR;
        }
    }

    public ReturnData staticTrailWxPage(String id) throws IOException {
        Map<String, Object> dataMap = prepareStaticInfo(id);
        Boolean result = SinglePageService.staticGeneralPage(dataMap, "mobile", "wx_trial", "app_share/apply_goods/"+id);
        if(result){
            return ReturnData.SUCCESS;
        }else{
            return ReturnData.ERROR;
        }
    }

    /***
     * 没查到的福利处理
     * @param id
     * @return
     */
    public String getAppBackTrial(String id) throws IOException {
        Map<String,Object>	dataMap = prepareStaticInfo(id);
        if(dataMap == null){
            logger.error("福利试用app模板:数据读取出错！");
        }else{
            String html = FreemarkerService.getHtml("app_trial", "mobile", dataMap, true);
            if(!StringUtils.isEmpty(html)){
                OSSService.uploadHtml2OSS(html, "app/apply_goods/"+id, "mobile");
            }else{
                logger.error("福利试用app模板:生成出错！");
                return "404";
            }
            return html;
        }
        return "404";
    }

    /***
     * 没查到的福利处理
     * @param id
     * @return
     */
    public String getWxBackTrial(String id) throws IOException {
        Map<String,Object>	dataMap = prepareStaticInfo(id);
        if(dataMap == null){
            logger.error("福利试用wx模板:数据读取出错！");
        }else{
            String html = FreemarkerService.getHtml("wx_trial", "mobile", dataMap, true);
            if(!StringUtils.isEmpty(html)){
                OSSService.uploadHtml2OSS(html, "app/apply_goods/"+id, "mobile");
            }else{
                logger.error("福利试用wx模板:生成出错！");
            }
            return html;
        }
        return "404";
    }


    public ReturnData staticTrailPages(String id){
        try {
            staticTrailAppPage(id);
            staticTrailWxPage(id);
        } catch (IOException e) {
            e.printStackTrace();
            return ReturnData.ERROR;
        }
        return ReturnData.SUCCESS;
    }

    /**
     * 初始化全量
     * @return
     */
    public ReturnData staticTrailInit(){
        final String key = "static_all_trail_page";
        final String running = "1"; //正在进行
        final String stopped = "0"; //已停止
        Config config = configMapper.selectByKey(key);
        String status;
        if(null == config){
            //未取到key
            status = null;
        }else{
            //取到key
            status = config.getValue();
        }
        if(stopped.equals(status) || null == status){
            //如果未进行静态化，则开始并更新开始记录
            configMapper.insertOrUpdate(key, running);

            Query query = new Query();
            Criteria criteria = Criteria.where("hidden").is(0);
            criteria.and("deleted").is(0);
            query.addCriteria(criteria);
            query.fields().include("id");
            List<EntityApplyGoods> entityApplyGoodsList = mongoTemplate.find(query, EntityApplyGoods.class, "entity_apply_goods");
            final  BlockingQueue<Long> queue = new LinkedBlockingQueue<>();
            for (EntityApplyGoods entityApplyGoods:entityApplyGoodsList) {
                try {
                    queue.put(entityApplyGoods.getId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Executors.newFixedThreadPool(1).submit(new Runnable() {
                public void run() {

                    try {
                        while (!queue.isEmpty()) {
                            try {
                                Long id = queue.take();
                                try {
                                    staticTrailPages(id.toString());
                                    logger.info("福利设id:" + id + "成功！");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                //异常页码直接跳过  todo 后期处理异常情况
                                logger.error(e.getMessage(), e.getCause());
                            }
                            Thread.sleep(100);
                        }
                        configMapper.insertOrUpdate(key, stopped);

                    } catch (Exception e) {
                        logger.error(e.getMessage(), e.getStackTrace());
                    }
                }
            });

        }
        return ReturnData.SUCCESS;
    }
}
