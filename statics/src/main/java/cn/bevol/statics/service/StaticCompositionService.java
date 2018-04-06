package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.CompositionOldMapper;
import cn.bevol.statics.dao.mapper.ConfigOldMapper;
import cn.bevol.statics.entity.items.CompositionItem;
import cn.bevol.statics.entity.model.Composition;
import cn.bevol.statics.entity.model.Config;
import cn.bevol.statics.entity.model.MetaInfo;
import cn.bevol.statics.entity.model.StaticRecord;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.StringUtil;
import cn.bevol.util.http.HttpUtils;
import cn.bevol.util.response.ReturnData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Integer.parseInt;


@Service
public class StaticCompositionService {
    private static Logger logger = LoggerFactory.getLogger(StaticCompositionService.class);
    @Resource
    private CompositionOldMapper compositionOldMapper;
    @Resource
    StaticRecordService staticRecordService;
    @Resource
    private SinglePageService singlePageService;
    @Resource
    private SidebarService sidebarService;
    @Resource
    private ConfigOldMapper configOldMapper;

    /**
     * 准备静态化成分数据
     * @param mid
     * @param platform
     * @return
     */
    private Map<String, Object> prepareStaticInfo(String mid, String platform){
        Map<String, Object> dataMap = new HashMap<String, Object>();
        String composition_url = ConfUtils.mps.get("url") + "composition/info/"+mid;
//        String composition_url = "http://api.bevol.cn/composition/info/"+mid;
        String compositionJson = HttpUtils.post(composition_url, new HashMap<String, String>());
        if(!Objects.equals(compositionJson, "fault")){
            JSONObject compositionObj = JSONObject.fromObject(compositionJson);
            if(compositionObj.has("result")){
                try {
                    dataMap = getTemplateData(compositionObj);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    logger.error("数据解析失败!出错mid:"+mid);
                }
                dataMap = getMetaInfo(dataMap);
                dataMap = SinglePageService.getStaticInfo(dataMap, platform, "composition");
            }
        }else{
            logger.error(composition_url + "接口读取数据失败!出错mid:"+mid);
        }
        return dataMap;
    }

    /**
     * 静态化成分pc
     * @param mid
     * @return
     * @throws IOException
     */
    private Boolean staticCompositionPCPage(String mid){
        try {
            Map<String, Object> dataMap = prepareStaticInfo(mid, "pc");
            dataMap = sidebarService.getSidebar(dataMap);
            String uploadPath1 = "composition/" + mid + ".html";
            String uploadPath2 = "composition/goods/" + mid + ".html";
            staticRecordService.insertOrUpdate(mid, "pc", "composition", 1, uploadPath1);
            Boolean res1 = SinglePageService.staticGeneralPage(dataMap, "pc", "composition", uploadPath1);
            Boolean res2 = SinglePageService.staticGeneralPage(dataMap, "pc", "composition_more", uploadPath2);
            return res1 && res2;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 静态化成分移动站
     * @param mid
     * @return
     * @throws IOException
     */
    private Boolean staticCompositionMPage(String mid) throws IOException {
        Map<String, Object> dataMap = prepareStaticInfo(mid, "mobile");
        String uploadPath = "composition/"+mid+".html";
        staticRecordService.insertOrUpdate(mid, "mobile","composition",1, uploadPath);
        return SinglePageService.staticGeneralPage(dataMap, "mobile", "composition", uploadPath);
    }

    /**
     * 静态化全部成分页面
     * @param mid
     * @return
     * @throws IOException
     */
    public Boolean staticCompositionPage(String mid){
        Boolean res1 = null;
        Boolean res2 = null;
        try {
            res1 = staticCompositionPCPage(mid);
            res2 = staticCompositionMPage(mid);
            return res1 && res2;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 成分静态化
     * @param mid
     */
    public Boolean compositionStatic(String mid) {
        return staticCompositionPage(mid);
    }


    /***
     * 没查到的产品处理
     * @param mid
     * @param platform
     * @return
     */
    public String getBackComposition(String mid, String platform) throws IOException {
        Map<String,Object> dataMap = prepareStaticInfo(mid, platform);
        if(dataMap == null){
            logger.error(mid+"回源成分模板:数据读取出错！");
        }else{
            String html = FreemarkerService.getHtml("composition", platform, dataMap, true);
            if(!StringUtils.isEmpty(html)){
                OSSService.uploadHtml2OSS(html, "composition/"+mid+".html", platform);
            }else{
                logger.error(platform + mid +"回源成分模板:生成出错！");
                return "404";
            }
            return html;
        }
        return "404";
    }

    /***
     * 初始化数据
     * @param compositionObj
     * @return
     */
    public Map<String, Object> getTemplateData(JSONObject compositionObj) throws UnsupportedEncodingException {
        String search=ConfUtils.mps.get("searchUrl");
        String url=ConfUtils.mps.get("url");
        Map<String, Object> context = new HashMap<String, Object>();
        JSONObject composition = compositionObj.getJSONObject("result");
        // JSONObject composition = data.getJSONObject("composition");
        //JSONArray goods = data.getJSONArray("goods");
        JSONArray useds = composition.getJSONArray("useds");
        // String goodsTotal = data.getString("goodsTotal");

        String id = composition.getString("id");

        search +="goods/index3";
        String items = null;
        String goodsTotal ="";
        JSONArray itemsRows = new JSONArray();
        items = HttpUtils.get(search+"?cps="+id);
        if(!"fault".equals(items)){
            JSONObject itemsObj = JSONObject.fromObject(items);
            JSONObject itemsData = itemsObj.getJSONObject("data");
            itemsRows = itemsData.getJSONArray("items");
            goodsTotal = itemsData.getString("total");
        }

        //安全指数计算
        String safetyString = composition.getString("safety").split("-")[0];
        if(!StringUtils.isEmpty(safetyString)){
            int safety = parseInt(safetyString);
            int safeNum = 0;
            int unsafeNum = 0;
            int middleSafeNum = 0;
            switch(safety){
                case 0:
                    safeNum = 5;
                    break;
                case 1:
                    safeNum = 5;
                    break;
                case 2:
                    safeNum = 4;
                    middleSafeNum = 1;
                    break;
                case 3:
                    safeNum = 4;
                    unsafeNum = 1;
                    break;
                case 4:
                    safeNum = 3;
                    middleSafeNum = 1;
                    unsafeNum = 1;
                    break;
                case 5:
                    safeNum = 3;
                    unsafeNum = 2;
                    break;
                case 6:
                    safeNum = 2;
                    middleSafeNum = 1;
                    unsafeNum = 2;
                    break;
                case 7:
                    safeNum = 2;
                    unsafeNum = 3;
                    break;
                case 8:
                    safeNum = 1;
                    middleSafeNum = 1;
                    unsafeNum = 3;
                    break;
                case 9:
                    safeNum = 1;
                    unsafeNum = 4;
                    break;
                case 10:
                    safeNum = 1;
                    unsafeNum = 4;
                    break;
                default:
            }
            composition.put("safeNum", safeNum);
            composition.put("unsafeNum", unsafeNum);
            composition.put("middleSafeNum", middleSafeNum);
        }

        //remark字数处理
        if(composition.has("remark")){
            String remark = composition.getString("remark");
            String btRemark = remark.replaceAll("[^\\x00-\\xff]", "**");
            int remarkLenth = btRemark.length();
            if(remarkLenth> 100){
                String miniContent =  StringUtil.subTextString(remark, 100);
                composition.put("minicontent", miniContent);
            }else{
                composition.put("minicontent", remark);
            }
            composition.put("remarkLenth", remarkLenth);
        }

        //评论
        String comment_url = url + "entity/comment4/lists/composition";
        Map<String, Object> commentData = SinglePageService.getCommentInfo(id, comment_url);
        JSONArray commentList = (JSONArray) commentData.get("commentList");
        int commentTotal = (int) commentData.get("total");
        context.put("comments", commentList);
        context.put("commentTotal", commentTotal);

        // 将json字符串加入数据模型
        context.put("composition", composition);
        context.put("useds", useds);
        context.put("goodsTotal", goodsTotal);
        context.put("items", itemsRows);
        return context;

    }

    /**
     * 获取meta数据
     * @param dataMap
     * @return
     */
    public Map<String, Object> getMetaInfo(Map<String,Object> dataMap){
        JSONObject composition = (JSONObject) dataMap.get("composition");
        MetaInfo metaInfo = singlePageService.getSeoMetaInfo(composition.getInt("id"), 6);
        if(metaInfo == null){
            JSONArray useds = (JSONArray) dataMap.get("useds");
            // 将title、keyword和description 封装好
            String englishName =  composition.get("english").toString()!=""?"-"+composition.get("english").toString():"";
            String englishKey =  composition.get("english").toString()!=""?","+composition.get("english").toString():"";
            String title =composition.get("title").toString();
            StringBuffer usedTitle = new StringBuffer();

            for (int i = 0; i < useds.size(); i++) {
                JSONObject ob = (JSONObject)useds.get(i);
                usedTitle.append(ob.get("title").toString()).append(",");
            }

            String description = "";
            if(composition.has("remark")){
                description = composition.getString("remark");
            }
            if(description.length()>0 && description.length()>200){
                description = StringUtil.subTextString(description, 200);
            }else{
                description = "化妆品成分"+title+englishName+"的相关成分信息简介,使用目的说明,成分安全风险度分析,包含此化学成分的进口化妆品与国产化妆品查询";
            }
            if(usedTitle.length()>0){
                usedTitle =	usedTitle.deleteCharAt(usedTitle.length() - 1);
                usedTitle.insert(0,",");
            }
            String keyword = title+englishKey+usedTitle+",化妆品成分,护肤品成分,保养品成分";
            dataMap.put("title", title+englishName+"-化妆品配方成分分析-美丽修行网");
            //dataMap.put("title_more", title+englishName+"-成分相关化妆品查询-美丽修行网");
            dataMap.put("keywords",keyword);
            //dataMap.put("keywords_more",title+englishKey+"，成分相关化妆品推荐");
            dataMap.put("description",description);
        }else{
            dataMap.put("title", metaInfo.getTitle());
            dataMap.put("keywords",metaInfo.getKeywords());
            dataMap.put("description",metaInfo.getDescription());
        }
        return dataMap;
    }


    /***
     * 成分批量静态化
     * @param page
     */
    public void compositionStatics(Integer page){
        List<Composition> list =compositionList(page);
        for (Composition comp : list) {
            compositionStatic(comp.getMid());
        }
    }
    /***
     * 静态化失败的批量静态化
     */
    public void compositionErrorStatics(){
        List<StaticRecord> list =staticRecordService.errorStatics("composition");
        for (StaticRecord record : list) {
            compositionStatic(record.getMid());
        }
    }


    private List<Composition> compositionList(Integer page) {
        Paged<Composition> paged = new Paged<Composition>();
        paged.setCurPage(page);
        paged.setWheres(new  Composition());
        paged.addOrderBy("id", "desc");
        return compositionOldMapper.compositionByPage(paged);
    }
    /***
     * 初始化成分线程
     */
    public ReturnData initCompositionStatic(int arg){
        final String key = "static_all_composition_page";
        final String running = "1"; //正在进行
        final String stopped = "0"; //已停止
        Config config = configOldMapper.selectByKey(key);
        String status;
        if(null == config){
            //未取到key
            status = null;
        }else{
            //取到key
            status = config.getValue();
        }
        if(stopped.equals(status) || null == status) {
            //如果未进行静态化，则开始并更新开始记录
            configOldMapper.insertOrUpdate(key, running);
            if (arg == 4 || arg == 8) {
                Executors.newFixedThreadPool(1).submit(new Runnable() {
                    public void run() {
                        try {
                            BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
                            int tatolPage = compositionOldMapper.selectTotal() / 20 + 1;
                            for (int i = 1; i <= tatolPage; i++) {
                                queue.put(i);
                            }
                            int page = 1;
                            while (!queue.isEmpty()) {
                                try {
                                    page = queue.take();
                                    try {
                                        compositionStatics(page);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    //异常页码直接跳过  todo 后期处理异常情况
                                    logger.error(e.getMessage(), e.getCause());
                                }
                                logger.info("第" + page + "页成功！");
                                Thread.sleep(100);
                            }
                            configOldMapper.insertOrUpdate(key, stopped);

                        } catch (Exception e) {
                            logger.error(e.getMessage(), e.getStackTrace());
                        }
                    }
                });
            }
            if (arg == -4 || arg == -8) {//出错后的
                Executors.newFixedThreadPool(1).submit(new Runnable() {
                    public void run() {
                        try {
                            BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
                            int tatolPage = staticRecordService.getNoUpdateCount("pc", "composition", -1) / 20 + 1;
                            for (int i = 1; i <= tatolPage; i++) {
                                queue.put(i);
                            }
                            int page = 1;
                            while (!queue.isEmpty()) {
                                try {
                                    page = queue.take();
                                    try {
                                        compositionErrorStatics();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    //异常页码直接跳过  todo 后期处理异常情况
                                    logger.error(e.getMessage(), e.getCause());
                                }

                                logger.info("第" + page + "页成功！");
                                Thread.sleep(100);
                            }

                        } catch (Exception e) {
                            logger.error(e.getMessage(), e.getStackTrace());
                        }
                    }

                });
            }
        }
        return ReturnData.SUCCESS;
    }

    /***
     * 多成分查询
     * @param names
     * @return
     */
    public Object findCompositionList(String names) {
        List<CompositionItem> lsItem = new ArrayList<CompositionItem>();

        String url=ConfUtils.mps.get("url");
        String goods_url = url + "/composition/compares";
        Map<String,String> map = new HashMap<String, String>();
        map.put("names",names);
        String compositionJson = HttpUtils.post(goods_url,map);
        if(compositionJson!="fault") {
            JSONObject compositionObj = JSONObject.fromObject(compositionJson);
            if (compositionObj.has("result")) {
                JSONArray commentResult = compositionObj.getJSONArray("result");
                for (int i = 0; i < commentResult.size(); i++) {
                    CompositionItem item = new CompositionItem();
                    JSONObject object = (JSONObject) commentResult.get(i);
                    item.setTitle(object.get("title").toString());
                    item.setActive(object.get("active").toString());
                    item.setAcneRisk(object.get("acneRisk").toString());
                    item.setSafety(object.get("safety").toString());
                    lsItem.add(item);
                }
            }

        }
        return lsItem;
    }

    /***
     * 单一成分查询
     * @param names
     * @return
     */
    public Object findComposition(String names) {
        //TODO 方法未实现
        return null;
    }

    /**
     * 迁移到内部项目
     * @param ids
     * @return
     */
    /*@Deprecated
    public List<Composition> findCompositionByIds(String[] ids) {
        return compositionOldMapper.findCompositionByIds(ids);
    }*/

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        StaticCompositionService s = (StaticCompositionService) context.getBean("staticCompositionService");
        //s.compositionStatic("b9c386eec0061e3a3278b785a375751e");
        //s.compositionStatic("41635c92ac746673fc78a6c2512d2917");
        s.staticCompositionPCPage("011c16cc269e8b98b43c6a51fbf3cd80");

        //s.initCompositionStatic(4);
        // s.compositionStatic("4c3060178d1184935a48c4e51be4f63f");

    }


}
