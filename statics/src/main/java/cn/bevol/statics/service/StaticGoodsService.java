package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.ConfigOldMapper;
import cn.bevol.statics.dao.mapper.GoodsCategoryOldMapper;
import cn.bevol.statics.dao.mapper.GoodsOldMapper;
import cn.bevol.statics.entity.model.*;
import cn.bevol.util.ConfUtils;
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
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/***
 * 产品静态化
 * @author admin
 *
 */
@Service
public class StaticGoodsService {
    private static Logger logger = LoggerFactory.getLogger(StaticGoodsService.class);

    @Resource
    private GoodsOldMapper goodsOldMapper;
    @Resource
    StaticRecordService staticRecordService;
    @Resource
    private GoodsCategoryOldMapper goodsCategoryOldMapper;
    @Resource
    private SinglePageService singlePageService;
    @Resource
    private SidebarService sidebarService;
    @Resource
    private ConfigOldMapper configOldMapper;

    /**
     * 准备静态化产品数据
     * @param mid
     * @param platform
     * @return
     */
    private Map<String, Object> prepareStaticInfo(String mid, String platform){
        Map<String, Object> dataMap = new HashMap<String, Object>();
        String goodsUrl = ConfUtils.mps.get("url") + "goods/info/"+mid;
        String goodsJson = HttpUtils.post(goodsUrl, new HashMap<String, String>());
        if(!Objects.equals(goodsJson, "fault")){
            JSONObject goodsObj = JSONObject.fromObject(goodsJson);
            if(goodsObj.has("result")){
                dataMap = getTemplateData(goodsJson);
                dataMap = getMetaInfo(dataMap);
                dataMap = SinglePageService.getStaticInfo(dataMap, platform, "product");
            }else{
                logger.error(goodsUrl + "接口读取数据失败!出错mid:"+mid);
            }
        }else{
            logger.error(goodsUrl + "接口读取数据失败!出错mid:"+mid);
        }
        return dataMap;
    }

    /**
     * 静态化产品pc
     * @param mid
     * @return
     * @throws IOException
     */
    private Boolean staticProductPCPage(String mid) throws IOException {
        try {
            Map<String, Object> dataMap = prepareStaticInfo(mid, "pc");
            dataMap = sidebarService.getSidebar(dataMap, Integer.parseInt((String) dataMap.get("id")), 0);
            String uploadPath = "product/" + mid + ".html";
            staticRecordService.insertOrUpdate(mid, "pc", "goods", 1, uploadPath);
            return SinglePageService.staticGeneralPage(dataMap, "pc", "goods", uploadPath);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("静态化pc产品页面出错"+mid);
        }
        return false;
    }

    /**
     * 静态化产品移动站
     * @param mid
     * @return
     * @throws IOException
     */
    private Boolean staticProductMPage(String mid) throws IOException {
        Map<String, Object> dataMap = prepareStaticInfo(mid, "mobile");
        String uploadPath = "product/"+mid+".html";
        staticRecordService.insertOrUpdate(mid, "mobile","goods",1, uploadPath);
        return SinglePageService.staticGeneralPage(dataMap, "mobile", "goods", uploadPath);
    }

    /**
     * 静态化全部产品页面
     * @param mid
     * @return
     * @throws IOException
     */
    public Boolean staticProductPage(String mid){
        Boolean res1 = null;
        Boolean res2 = null;
        try {
            res1 = staticProductPCPage(mid);
            res2 = staticProductMPage(mid);
            return res1 && res2;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 静态化全部产品页面
     * @param mid
     */
    public Boolean goodsStatic(String mid) {

        return staticProductPage(mid);
    }

    /**
     * 批量静态化产品
     * @param ids
     * @return
     */
    public Boolean batchGoodsStatic(String ids){
        String[] idsArr = ids.split(",");
        for(String id : idsArr){
            String mid = goodsOldMapper.getMidById(Long.parseLong(id));
            staticProductPage(mid);
        }
        return true;
    }

    /***
     * 没查到的产品处理
     * @param mid
     * @param platform
     * @return
     */
    public String getBackGoods(String mid, String platform) throws IOException {
        Map<String,Object> dataMap = prepareStaticInfo(mid, platform);
        if(dataMap == null){
            logger.error(platform+mid+"产品回源:数据读取出错！");
        }else{
            String html = FreemarkerService.getHtml("goods", platform, dataMap, true);
            if(!StringUtils.isEmpty(html)){
                OSSService.uploadHtml2OSS(html, "product/"+mid+".html", platform);
            }else{
                logger.error(platform + "回源产品模板:"+mid+"生成出错！");
                return "404";
            }
            return html;
        }
        return "404";
    }

    /**
     * 读取模板数据
     * @param goodsJson
     * @return
     */
    private Map<String, Object> getTemplateData(String goodsJson){
        String url = ConfUtils.mps.get("url");
        Map<String, Object> context = new HashMap<String, Object>();
        JSONObject goodsObj = JSONObject.fromObject(goodsJson);
        if (goodsObj.has("result")) {
            try {
                JSONObject data = goodsObj.getJSONObject("result");
                JSONObject goods = data.getJSONObject("goods");
                JSONArray composition = data.getJSONArray("composition");
                JSONArray effect = data.getJSONArray("effect");
                JSONArray safety = data.getJSONArray("safety");

                String id = goods.getString("id");

                //评论
                String comment_url = url + "entity/comment4/lists/goods";
                Map<String, Object> commentData = SinglePageService.getCommentInfo(id, comment_url);
                JSONArray commentList = (JSONArray) commentData.get("commentList");
                int commentTotal = (int) commentData.get("total");
                context.put("comments", commentList);
                context.put("commentTotal", commentTotal);
                Map<String, Object> effects_detail = getEffectsDetail(effect);
                Map<String, Object> safety_detail = getSafetyDetail(safety);
                context.put("effects_detail", effects_detail);
                context.put("safety_detail", safety_detail);

                context.put("data", data);
                context.put("goods", goods);
                context.put("effects", effect);
                context.put("composition", composition);
                context.put("safety", safety);
                context.put("id", id);

                //成分分析
                int num01 = 0;
                int num02 = 0;
                int num03 = 0;
                for (int i = 0; i < composition.size(); i++) {
                    JSONObject comp = (JSONObject) composition.get(i);
                    String safty = comp.getString("safety");
                    if (StringUtils.isEmpty(safty)) {
                        safty = "0";
                    }
                    int safetyNum;
                    if (safty.contains("-")) {
                        safetyNum = Integer.parseInt(safty.split("-")[1]);
                    } else {
                        safetyNum = Integer.parseInt(safty);
                    }
                    if (safetyNum < 3) {
                        num01++;
                    } else if (safetyNum < 7) {
                        num02++;
                    } else {
                        num03++;
                    }
                }
                int allNum = num01 + num02 + num03;
                float percent01 = ((float) num01 / allNum * 100);
                float percent02 = ((float) num02 / allNum * 100);
                float percent03 = ((float) num03 / allNum * 100);
                context.put("percent01", percent01);
                context.put("percent02", percent02);
                context.put("percent03", percent03);
            }catch(Exception e){
                e.printStackTrace();
                logger.error("产品读取模板数据:"+goodsObj);
            }
        }
        return context;
    }

    /**
     * 获取功效
     * @param effects
     * @return
     */
    private Map<String, Object> getEffectsDetail(JSONArray effects){
        Map<String, Object> map  = new HashMap<String,Object>();
        for(Object effect : effects){

            JSONObject eff = JSONObject.fromObject(effect);

            String displayName = eff.getString("displayName");
            JSONArray composition = eff.getJSONArray("composition");
            map.put(displayName,composition);
        }
        return map;
    }

    /**
     * 获取安全
     * @param safety
     * @return
     */
    private Map<String, Object> getSafetyDetail(JSONArray safety){
        Map<String, Object> map  = new HashMap<String,Object>();
        for(Object safe : safety){

            JSONObject saf = JSONObject.fromObject(safe);

            int unit = saf.getInt("unit");
            int num = saf.getInt("num");
            if(unit == 0 && num > 0){
                String displayName = saf.getString("displayName");
                JSONArray composition = saf.getJSONArray("composition");
                map.put(displayName,composition);
            }
        }
        return map;
    }

    /**
     * 获取meta数据
     * @param dataMap
     * @return
     */
    private Map<String, Object> getMetaInfo(Map<String,Object> dataMap){


        JSONObject goods = (JSONObject) dataMap.get("goods");

        MetaInfo metaInfo = singlePageService.getSeoMetaInfo(goods.getInt("id"), 5);
        if(metaInfo == null){
            //JSONObject data = (JSONObject) dataMap.get("data");
            JSONArray safety = (JSONArray) dataMap.get("safety");
            JSONArray effect = (JSONArray) dataMap.get("effects");

            StringBuffer safetyKeyword = new StringBuffer();
            if (null != safety && safety.size() > 0) {
                for (int i = 0; i < safety.size(); i++) {
                    Object obj = safety.get(i);
                    JSONObject json = JSONObject.fromObject(obj);
                    int unit = json.getInt("unit");
                    if (unit == 0) {
                        int num = Integer.parseInt(json.getString("num"));
                        if (num >0) {
                            safetyKeyword.append(json.getString("name")+",");
                        }
                    }
                }
            }
            if(safetyKeyword.length()>0){
                safetyKeyword.deleteCharAt(safetyKeyword.length() - 1);
            }

            StringBuffer effectKeyword = new StringBuffer();
            if (null != effect && effect.size() > 0) {
                for (int i = 0; i < effect.size(); i++) {
                    Object obj = effect.get(i);
                    JSONObject json = JSONObject.fromObject(obj);
                    int unit = json.getInt("id");
                    if (unit != -1) {
                        int num = Integer.parseInt(json.getString("num"));
                        if (num >0) {
                            effectKeyword.append(json.getString("name")+",");
                        }
                    }
                }
            }
            if(effectKeyword.length()>0){
                effectKeyword.deleteCharAt(effectKeyword.length() - 1);
            }

            //获取分类
            GoodsCategory goodsCategory = goodsCategoryOldMapper.findById(goods.getInt("category"));
            String goodsCategoryName="";
            if(goodsCategory!=null){
                goodsCategoryName = goodsCategory.getName();
            }
            // 将title、keyword和description 封装好
            String categroyNameTitle = StringUtils.isEmpty(goodsCategoryName)?"":goodsCategoryName+"化妆品成分分析";
            String title_arr[] = {goods.get("title").toString(),goods.get("alias").toString(),goods.get("approval").toString(),categroyNameTitle,"美丽修行网"};
            String title = getSeoString(title_arr, "-");

            String keyword_arr[] = {
                    goods.get("title").toString(),
                    goods.get("alias").toString(),
                    goods.get("approval").toString(),
                    goodsCategoryName+"化妆品",
                    safetyKeyword.toString(),
                    effectKeyword.toString()
            };
            String keyword = getSeoString(keyword_arr,",");
            keyword += ",全成份, 全成分, 原料, 配方, 分析, 孕妇风险查询, 致粉刺, 功效";

            String description;
            if(goods.has("doyen")){
                JSONObject doyen =  goods.getJSONObject("doyen");
                description = doyen.getString("doyenComment");
            }else{
                description = goods.get("title") + "化妆品图片、批号与备案信息查看，安全分析，功效分析，孕妇慎用建议，全成分信息显示";
            }
            if(description.length()>0 && description.length()>200){
                description = description.substring(0,200)+"...";
            }

            dataMap.put("title", title);
            dataMap.put("keywords",keyword);
            dataMap.put("description",description);
        }else{
            dataMap.put("title", metaInfo.getTitle());
            dataMap.put("keywords",metaInfo.getKeywords());
            dataMap.put("description",metaInfo.getDescription());
        }

        return dataMap;
    }

    private String getSeoString(String array[], String symbol){
        List list = new ArrayList<String>();
        for (String s : array) {
            if(s != null && s.length() != 0) {
                list.add(s);
            }
        }
        return StringUtils.join(list, symbol);

    }


    /***
     * 根据mids查询
     * @param mids
     * @return
     */
    public List<Goods> getGoodsByMids(String[] mids){
        List<Goods> gLs =goodsOldMapper.getGoodsByMids(mids);
        return gLs;
    }
    /***
     * 列表分页查询
     * @param page
     * @return
     */
    public List<Goods> findByPage(Integer page){
        Paged<Goods> paged = new Paged<Goods>();
        paged.setCurPage(page);
        paged.setWheres(new Goods());
        return goodsOldMapper.findByPage(paged);

    }

    /***
     * 批量静态化
     * @param page
     */
    public void goodsStatics(Integer page){
        List<Goods> list =findByPage(page);
        for (Goods goods : list) {
            goodsStatic(goods.getMid());
        }
    }

    /***
     * 静态化失败的批量静态化
     */
    public void goodsErrorStatics(){
        List<StaticRecord> list =staticRecordService.errorStatics("goods");
        for (StaticRecord record : list) {
            goodsStatic(record.getMid());
        }
    }

    /***
     * 静态化进程
     * @param arg 产品=3
     */
    public ReturnData initStatic(int arg) {
        final String key = "static_all_goods_page";
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
        if(stopped.equals(status) || null == status){
            //如果未进行静态化，则开始并更新开始记录
            configOldMapper.insertOrUpdate(key, running);
            if (arg == 3 || arg == 8) {
                //只使用一个子线程
                Executors.newFixedThreadPool(1).submit(new Runnable() {
                    @Override
                    public void run() {
                        int tatolPage = goodsOldMapper.selectTotal() / 20 + 1;
                        BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
                        for (int i = 1; i <= tatolPage; i++) {
                            try {
                                queue.put(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        runner(queue, "OK");
                        configOldMapper.insertOrUpdate(key, stopped);
                    }
                });
            }
            //todo 逻辑有问题
            if (arg == -3 | arg == -8) {//处理批量出错
                Executors.newFixedThreadPool(1).submit(new Runnable() {
                    @Override
                    public void run() {
                        int tatolPage = staticRecordService.getNoUpdateCount("pc", "goods", -1) / 20 + 1;
                        BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
                        for (int i = 1; i <= tatolPage; i++) {
                            try {
                                queue.put(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        runner(queue, "Error");
                    }
                });
            }
        }
        return ReturnData.SUCCESS;
    }

    private void runner(BlockingQueue<Integer> queue, String type) {
        try {
            int page = 1;
            while (!queue.isEmpty()) {
                try {
                    page = queue.take();
                    try {
                        if ("OK".equals(type))
                            goodsStatics(page);
                        else
                            goodsErrorStatics();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    //异常页码直接跳过  todo 后期处理异常情况
                    logger.error(e.getMessage(), e.getCause());
                }

                logger.info(type + ":第" + page + "页成功！");
                Thread.sleep(100);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e.getStackTrace());
        }
    }

    /***
     * 根据标签查询
     * @param tagId
     * @param page
     * @param pageSize
     * @return
     */
    public Paged getTagFindList(Integer tagId, Integer page, Integer pageSize) {
        Paged<Goods> paged = new Paged<Goods>();
        paged.setPageSize(pageSize);
        paged.setCurPage(page);
        paged.setTotal(goodsOldMapper.selectTotalByTag(tagId));
        paged.setResult(goodsOldMapper.getFindByTagId(tagId,page,pageSize));
        return paged;
    }


    public List<GoodsHitItems> findGoodsHit(Integer dataType, Integer page, Integer pageSize){
        List<GoodsHitItems> itemsList = goodsOldMapper.findGoodsHit(dataType, page,pageSize);
        return null;
    }


    /***
     * 迁移到内部项目
     * 根据产品名称查产品
     * @param title
     * @return
     */
    @Deprecated
    public List<GoodsByNameItems> findByName(String title){
        return goodsOldMapper.findByName(title);
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        StaticGoodsService staticGoodsService = (StaticGoodsService) context.getBean("staticGoodsService");
        // goodsService.configOldMapper.selectByKey("wxwelcome");
//      //  String _midss[] ={"d5a59f1ad22c596f4c944cffa11063dc","85c9d87ed1ea57a111e3d9dc5ff7a10a"};
//   //     goodsService.goodsStatic("00010bb3c8991585975e24e0ebb769e6");
////			List<Goods> g =goodsService.getGoodsByMids(_midss);
////			System.out.println(g.size());
////
////			List<Goods> ls = goodsService.findByPage(1);
////			System.out.println(ls.size());
//
        staticGoodsService.initStatic(3);
        /*try {
            goodsService.staticProductPCPage("cffe1915797ec336b7c48682ae460f4a");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //goodsService.goodsStatic("2a279aca4b2ef2f27278dc180655022a");
        // goodsService.findByName("KIEHL‘S SINCE 1851 CREAMY EYE TREATMENT");


    }


}
