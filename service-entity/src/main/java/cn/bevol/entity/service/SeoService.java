package cn.bevol.entity.service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import cn.bevol.mybatis.dao.*;
import cn.bevol.mybatis.model.*;
import com.io97.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.io97.utils.PropertyUtils;
import com.io97.utils.db.Paged;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.util.StringUtils;

@Service
public class SeoService {
    private static Logger logger = LoggerFactory.getLogger(SeoService.class);

    private static String TOKEN="oeagdHEliz2aqmwR";
    private static String BEVOL_PC ="www.bevol.cn";
    private static String BEVOL_M ="m.bevol.cn";
    @Resource
    private StaticRecordService recordService;
    @Resource
    private SeoRecordMapper seoRecordMapper;
    @Resource
    private StaticRecordService staticRecordService;
    @Resource
    private ConfigMapper configMapper;
    @Resource
    private FindMapper findMapper;
    @Resource
    private CompositionMapper compositionMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private FreemarkerService freemarkerService;

    private final String GENERATE_START = "1";
    private final String GENERATE_END = "0";
    private final Integer MAX_SITE_MAP_CONTENT = 30000;
    private final String SEO_360_URL_FTL = "seo_360_url";

    /***
     * 批量添加
     * @param dataType  pc | mobile
     * @param dataSource 模块
     * @param page
     * @return
     */
    public String addBatch(String dataType,String dataSource,Integer page){
        String zzurl ="";
        String domain="";
        String results ="";
        if("pc".equals(dataType)){
            zzurl ="http://data.zz.baidu.com/urls?site="+BEVOL_PC+"&token="+TOKEN;
            domain =PropertyUtils.getStringValue("bevol.www.domain");
            //domain ="https://www.bevol.csn";
        }else{
            zzurl ="http://data.zz.baidu.com/urls?site="+BEVOL_M+"&token="+TOKEN;
            domain =PropertyUtils.getStringValue("bevol.m.domain");
        }
        List<StaticRecord> ls =recordService.staticRecordByPage(dataType, dataSource, 1,0,page,500);
        List<String> paramPath = getPath(ls,domain);
        if(isSEO()){
            results =Post(zzurl,paramPath);
            addSEORecord(results,paramPath,"add",dataType);
            setUpdateStaticRecord(ls);
            logger.info("第"+page+"页，推送完成！");
            return results;
        }else{
            results="当天推送条数已用完！";
            logger.info("当天推送条数已用完！");
        }
        return results;
    }
    /***
     * 添加
     * @param parameters
     */
    public String add(List<String> parameters,String platform){
        String url ="";
        if("pc".equals(platform)){
            url ="http://data.zz.baidu.com/urls?site="+BEVOL_PC+"&token="+TOKEN;
        }else{
            url ="http://data.zz.baidu.com/urls?site="+BEVOL_M+"&token="+TOKEN;
        }
        if(isSEO()){
            String results =Post(url,parameters);
            addSEORecord(results,parameters,"add",platform);
            return results;
        }else{
            return "-1";//自定义结果：当天推送条数已用完
        }
    }
    /***
     * 修改
     * @param parameters
     */
    public String update(List<String> parameters,String platform){
        String url ="";
        if("pc".equals(platform)){
            url ="http://data.zz.baidu.com/update?site="+BEVOL_PC+"&token="+TOKEN;
        }else{
            url ="http://data.zz.baidu.com/update?site="+BEVOL_M+"&token="+TOKEN;
        }
        if(isSEO()){
            String results =Post(url,parameters);
            addSEORecord(results,parameters,"update",platform);
            return results;
        }else{
            return "-1";
        }
    }
    /***
     * 删除
     * @param parameters
     */
    public String delete(List<String> parameters,String platform){
        String url ="";
        if("pc".equals(platform)){
            url ="http://data.zz.baidu.com/del?site="+BEVOL_PC+"&token="+TOKEN;
        }else{
            url ="http://data.zz.baidu.com/del?site="+BEVOL_M+"&token="+TOKEN;
        }
        if(isSEO()){
            String results =Post(url,parameters);
            addSEORecord(results,parameters,"add",platform);
            return results;
        }else{
            return "-1";
        }
    }

    /***
     * 分页获取页面路径
     * @param ls
     * @param url
     * @return
     */
    public List<String> getPath(List<StaticRecord> ls,String url){
        List<String> param  = new ArrayList<String>();
        for (StaticRecord record :ls) {
            param.add(url+"/"+record.getPath());
        }
        return param;
    }
    /**
     * 修改静态页面状态（批量）
     * @param ls
     * @return
     */
    public void setUpdateStaticRecord(List<StaticRecord> ls){
        for (StaticRecord staticRecord : ls) {
            staticRecord.setIsSeo(1);//表示为已经SEO推送
            staticRecord.setUpdateTime(new Date().getTime()/1000);
            staticRecordService.update(staticRecord);
        }

    }
    /**
     * 修改静态页面状态
     * @param ls
     * @return
     */
    public void setUpdateStaticRecord(List<String> ls,String platform){
        int subLenth=0;
        if("pc".equals(platform)){
            subLenth =PropertyUtils.getStringValue("bevol.www.domain").length();
        }else{
            subLenth =PropertyUtils.getStringValue("bevol.m.domain").length();
        }
        for (String s : ls) {
            StaticRecord staticRecord = new StaticRecord();
            staticRecord.setIsSeo(1);//表示为已经SEO推送
            staticRecord.setUpdateTime(new Date().getTime()/1000);
            String path = s.substring(subLenth);
            staticRecord.setPath(path);
            staticRecordService.update(staticRecord);
        }

    }


    public boolean isSEO(){
        boolean flag = false;
        Paged<SeoRecord> paged =new Paged<SeoRecord>();
        paged.setPageSize(5);
        paged.addOrderBy("create_time", "desc");
        List<SeoRecord> ls  =seoRecordMapper.findByPage(paged);
        if(ls.size()!=0){
            Integer remain =ls.get(0).getRemain();
            if(remain>0){
                flag = true;
            }else{
                flag = false;
            }
        }else{
            flag = true;
        }
        return flag;
    }

    /***
     *
     * @param dataType  pc | m
     * @param dataSource 模块
     */
    public void runner(String dataType,String dataSource){
        while (true) {
            try {
                BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
                int tatolPage =  recordService.getCount(dataType, dataSource, 1,0)/500+1;
                if(tatolPage>0){
                    for (int i = 1; i <= tatolPage; i++) {
                        queue.put(i);
                    }
                    int page = 1;
                    while (true) {
                        try {
                            page = queue.take();
                            try {
                                addBatch(dataType, dataSource,page);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            //异常页码直接跳过  todo 后期处理异常情况
                            logger.error(e.getMessage(), e.getCause());
                        }
                        logger.info("共"+tatolPage+"页；"+dataSource+"["+dataType+"]:第"+page+"页成功！");
                        Thread.sleep(1000);
                    }
                }else{
                    break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e.getStackTrace());
            }

        }
    }

    /***
     * 批量SEO推送PC
     * @param arg 5
     */
    public void seoPCBatch(int arg){
        if(arg==5){
//		 Executors.newFixedThreadPool(1).submit(new Runnable() {
//	            public void run() {
//	            	runner("pc","industry");
//	            }
//	        });
            Executors.newFixedThreadPool(1).submit(new Runnable() {
                public void run() {
                    runner("pc","goods");
                }
            });
//			 Executors.newFixedThreadPool(1).submit(new Runnable() {
//	            public void run() {
//	            	runner("pc","composition");
//	            }
//	        });
//		 Executors.newFixedThreadPool(1).submit(new Runnable() {
//	            public void run() {
//	            	runner("pc","find");
//	            }
//	        });
        }
    }
    /***
     * 批量SEO推送M
     * @param arg 6
     */
    public void seoMBatch(int arg){
        if(arg==6){
//		 Executors.newFixedThreadPool(1).submit(new Runnable() {
//	            public void run() {
//	            	runner("mobile","industry");
//	            }
//	        });
            Executors.newFixedThreadPool(1).submit(new Runnable() {
                public void run() {
                    runner("mobile","goods");
                }
            });
//		 Executors.newFixedThreadPool(1).submit(new Runnable() {
//	            public void run() {
//	            	runner("mobile","composition");
//	            }
//	        });
//		 Executors.newFixedThreadPool(1).submit(new Runnable() {
//	            public void run() {
//	            	runner("mobile","find");
//	            }
//	        });
        }
    }
    /**
     * 百度链接实时推送
     * @param PostUrl
     * @param parameters
     * @return
     */
    public static String Post(String PostUrl,List<String> parameters){
        if(null == PostUrl || null == parameters || parameters.size() ==0){
            return null;
        }
        String result="";
        PrintWriter out=null;
        BufferedReader in=null;
        try {
            //建立URL之间的连接
            URLConnection conn=new URL(PostUrl).openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("Host","data.zz.baidu.com");
            conn.setRequestProperty("User-Agent", "curl/7.12.1");
            conn.setRequestProperty("Content-Length", "83");
            conn.setRequestProperty("Content-Type", "text/plain");

            //发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //获取conn对应的输出流
            out=new PrintWriter(conn.getOutputStream());
            //发送请求参数
            String param = "";
            for(String s : parameters){
                param += s+"\n";
            }
            out.print(param.trim());
            //进行输出流的缓冲
            out.flush();
            //通过BufferedReader输入流来读取Url的响应
            in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line=in.readLine())!= null){
                result += line;
            }

        } catch (Exception e) {
            logger.error("发送post请求出现异常！"+e);
            result ="fault";
            e.printStackTrace();
        } finally{
            try{
                if(out != null){
                    out.close();
                }
                if(in!= null){
                    in.close();
                }

            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }


    /***
     * 推送添加入库记录
     * @return 剩余推送条数，目前百度接口大于1的都返回1；请求异常返回-1
     */
    public int addSEORecord(String message,List<String> urls,String dataSource,String dataType){
        if(!"fault".equals(message)){//http请求异常！
            JSONObject dataObj = JSONObject.fromObject(message);
            int remain = dataObj.getInt("remain");
            if(!dataObj.has("result")){//没有错误
                for (int i = 0; i < urls.size(); i++) {
                    SeoRecord record = new SeoRecord();
                    record.setCreateTime(new Date().getTime()/1000);
                    record.setUpdateTime(new Date().getTime()/1000);
                    record.setDataType(dataType);
                    record.setUrls(urls.get(i));
                    record.setResults(message);
                    record.setState(1);
                    record.setRemain(remain);
                    record.setOperateType(dataSource);
                    seoRecordMapper.insertOrUpdate(record);
                }
            }
            else{
                JSONArray arr = dataObj.getJSONArray("not_same_site");
                for(Object obj:arr){
                    logger.error("出错的url:"+obj.toString());
                    System.out.println(obj.toString());
                    SeoRecord record = new SeoRecord();
                    record.setUrls(obj.toString());
                    record.setResults(message);
                    record.setDataType(dataType);
                    record.setState(0);
                    record.setRemain(remain);
                    record.setCreateTime(new Date().getTime()/1000);
                    record.setUpdateTime(new Date().getTime()/1000);
                    record.setOperateType(dataSource);
                    seoRecordMapper.insertOrUpdate(record);

                }
                String[] result_minus = minus(urls,  arr.toArray());
                for (String string : result_minus) {
                    SeoRecord record = new SeoRecord();
                    record.setUrls(string);
                    record.setResults(message);
                    record.setCreateTime(new Date().getTime()/1000);
                    record.setUpdateTime(new Date().getTime()/1000);
                    record.setDataType(dataType);
                    record.setState(1);
                    record.setRemain(remain);
                    record.setOperateType(dataSource);
                    seoRecordMapper.insertOrUpdate(record);
                }

            }

            return  remain;
        }else
            return -1;
    }
    //求两个数组的差集
    public static String[] minus(List<String> arr1, Object[] arr2) {
        LinkedList<String> list = new LinkedList<String>();
        LinkedList<String> history = new LinkedList<String>();

        for (String str : arr1) {
            if (!list.contains(str)) {
                list.add(str);
            }
        }
        for (Object str : arr2) {
            if (list.contains(str)) {
                history.add(str.toString());
                list.remove(str);
            } else {
                if (!history.contains(str)) {
                    list.add(str.toString());
                }
            }
        }

        String[] result = {};
        return list.toArray(result);
    }


    private enum Seo360Enum{
        PC_SITE_MAP_INDEX("sitemap/pc_bevol_index.xml"),
        M_SITE_MAP_INDEX("sitemap/m_bevol_index.xml"),
        BASE_SITE_MAP_PATH("sitemap/base.xml"),
        ARTICLE_SITE_MAP_PATH("sitemap/article.xml"),
        COMPOSITION_SITE_MAP_PATH("sitemap/composition.xml"),
        PRODUCT_SITE_MAP_PATH("sitemap/product.xml"),
        INDEX_360_KEY("seo_360_index"),
        BASE_360_KEY("seo_360_base"),
        ARTICLE_360_KEY("seo_360_article"),
        COMPOSITION_360_KEY("SEO_360_composition"),
        PRODUCTION_360_KEY("seo_360_product"),
        LATEST_PRODUCTION_360_KEY("seo_360_latest_product");

        private String value;

        public String getValue() {
            return value;
        }

        public String getValue(int page){
            return value.substring(0, value.length()-4) + page + ".xml";
        }

        Seo360Enum(String value) {

            this.value = value;
        }


    }

    public Boolean build360BaseSiteMap() throws ParseException, IOException {
        String baseSiteMapPath = Seo360Enum.BASE_SITE_MAP_PATH.getValue();
        String configKey = Seo360Enum.BASE_360_KEY.getValue();

        //记录site map生成开启
        configMapper.saveConfigType(configKey, GENERATE_START);

        Map<String, Object> map = get360Config(configKey);

        //pc站
        String[] pcUrls = {
                "https://www.bevol.cn",
                "https://www.bevol.cn/download.html",
                "https://www.bevol.cn/product",
                "https://www.bevol.cn/composition",
                "https://www.bevol.cn/find",
                "https://www.bevol.cn/contact.html",
                "https://www.bevol.cn/question.html",
        };
        map.put("urls", pcUrls);
        String pc_xml = freemarkerService.get360SeoXml(SEO_360_URL_FTL, map);
        OSSService.uploadXml2PC(pc_xml, baseSiteMapPath);

        //移动站
        String[] mUrls = {
                "https://m.bevol.cn",
                "https://m.bevol.cn/product",
                "https://m.bevol.cn/composition"
        };
        map.put("urls", mUrls);
        String m_xml = freemarkerService.get360SeoXml(SEO_360_URL_FTL, map);
        OSSService.uploadXml2M(m_xml, baseSiteMapPath);

        //记录site map生成结束
        configMapper.saveConfigType(configKey, GENERATE_END);
        return true;
    }

    public Boolean build360ArticleSiteMap() throws IOException {
        String baseSiteMapPath = Seo360Enum.ARTICLE_SITE_MAP_PATH.getValue();
        String configKey = Seo360Enum.ARTICLE_360_KEY.getValue();

        //记录site map生成开启
        configMapper.saveConfigType(configKey, GENERATE_START);

        Map<String, Object> map = get360Config(configKey);

        Paged<Find> paged = new Paged<Find>();
        paged.setPageSize(MAX_SITE_MAP_CONTENT);
        List<Find> list = findMapper.findByPage(paged);
        ArrayList<StringBuilder> urls = new ArrayList<StringBuilder>();
        for(Find find : list){
            StringBuilder url = new StringBuilder("https://www.bevol.cn/find/").append(find.getId()).append(".html");
            urls.add(url);
        }
        map.put("urls", urls);

        String xml = freemarkerService.get360SeoXml(SEO_360_URL_FTL, map);
        OSSService.uploadXml2PC(xml, baseSiteMapPath);

        //记录site map生成结束
        configMapper.saveConfigType(configKey, GENERATE_END);
        return true;
    }

    public Boolean build360CompositionSiteMap() throws IOException {
        String baseSiteMapPath = Seo360Enum.COMPOSITION_SITE_MAP_PATH.getValue();
        String configKey = Seo360Enum.COMPOSITION_360_KEY.getValue();

        //记录site map生成开启
        configMapper.saveConfigType(configKey, GENERATE_START);

        Map<String, Object> map =get360Config(configKey);

        Paged<Composition> paged = new Paged<Composition>();
        paged.setPageSize(MAX_SITE_MAP_CONTENT);
        List<Composition> list = compositionMapper.compositionByPage(paged);
        ArrayList<StringBuilder> pcUrls = new ArrayList<StringBuilder>();
        ArrayList<StringBuilder> mUrls = new ArrayList<StringBuilder>();
        for(Composition composition : list){
            StringBuilder pc_url = new StringBuilder("https://www.bevol.cn/composition/").append(composition.getMid()).append(".html");
            StringBuilder m_url = new StringBuilder("https://m.bevol.cn/composition/").append(composition.getMid()).append(".html");
            pcUrls.add(pc_url);
            mUrls.add(m_url);
        }
        //pc站点生成
        map.put("urls", pcUrls);
        String pc_xml = freemarkerService.get360SeoXml(SEO_360_URL_FTL, map);
        OSSService.uploadXml2PC(pc_xml, baseSiteMapPath);
        //m站点生成
        map.put("urls", mUrls);
        String m_xml = freemarkerService.get360SeoXml(SEO_360_URL_FTL, map);
        OSSService.uploadXml2M(m_xml, baseSiteMapPath);

        //记录site map生成结束
        configMapper.saveConfigType(configKey, GENERATE_END);
        return true;
    }

    private void build360ProductSiteMap(String configKey, BlockingQueue<Integer> queue) throws InterruptedException {
        //记录site map生成开启
        configMapper.saveConfigType(configKey, GENERATE_START);
        Map<String, Object> map = get360Config(configKey);

        ExecutorService exe = Executors.newFixedThreadPool(5);

        for(int i=0; i<5; i++){
            exe.execute(new Single360ProductSiteMap(queue, map));
        }

        exe.shutdown();

        while (true) {
            if (exe.isTerminated()) {
                //记录site map生成结束
                configMapper.saveConfigType(configKey, GENERATE_END);
                break;
            }
            Thread.sleep(5000);
        }
    }

    public Boolean build360AllProductSiteMap() throws InterruptedException {
        String configKey = Seo360Enum.PRODUCTION_360_KEY.getValue();
        BlockingQueue<Integer> queue = getPageQueue();
        build360ProductSiteMap(configKey, queue);
        return true;
    }

    public Boolean build360LatestProductSiteMap() throws InterruptedException {
        String configKey = Seo360Enum.LATEST_PRODUCTION_360_KEY.getValue();
        BlockingQueue<Integer> queue = getLatestPageQueue();
        build360ProductSiteMap(configKey, queue);
        return true;
    }

    public Boolean build360SiteMapIndex() throws InterruptedException, IOException {
        String configKey = Seo360Enum.INDEX_360_KEY.getValue();
        //记录site map生成开启
        configMapper.saveConfigType(configKey, GENERATE_START);

        Map<String, Object> map = get360config();
        int productTotalPage = getPageQueue().size();
        List<String> pcUrls = new ArrayList<String>();
        List<String> mUrls = new ArrayList<String>();
        String pcBaseUrl = "https://www.bevol.cn/";
        String mBaseUrl = "https://m.bevol.cn/";
        pcUrls.add(pcBaseUrl + Seo360Enum.BASE_SITE_MAP_PATH.getValue());
        pcUrls.add(pcBaseUrl + Seo360Enum.ARTICLE_SITE_MAP_PATH.getValue());
        pcUrls.add(pcBaseUrl + Seo360Enum.COMPOSITION_SITE_MAP_PATH.getValue());
        mUrls.add(mBaseUrl + Seo360Enum.BASE_SITE_MAP_PATH.getValue());
        mUrls.add(mBaseUrl + Seo360Enum.COMPOSITION_SITE_MAP_PATH.getValue());
        for(int i=0; i<productTotalPage; i++){
            pcUrls.add(pcBaseUrl + Seo360Enum.PRODUCT_SITE_MAP_PATH.getValue(i+1));
            mUrls.add(mBaseUrl + Seo360Enum.PRODUCT_SITE_MAP_PATH.getValue(i+1));
        }

        //pc站点生成
        map.put("urls", pcUrls);
        String SEO_360_INDEX_FTL = "seo_360_index";
        String pc_xml = freemarkerService.get360SeoXml(SEO_360_INDEX_FTL, map);
        OSSService.uploadXml2PC(pc_xml, Seo360Enum.PC_SITE_MAP_INDEX.getValue());
        //m站点生成
        map.put("urls", mUrls);
        String m_xml = freemarkerService.get360SeoXml(SEO_360_INDEX_FTL, map);
        OSSService.uploadXml2M(m_xml, Seo360Enum.M_SITE_MAP_INDEX.getValue());

        //记录site map生成结束
        configMapper.saveConfigType(configKey, GENERATE_END);
        return true;
    }

    private BlockingQueue<Integer> getPageQueue() throws InterruptedException {
        int totalNum = goodsMapper.selectTotal();
        int totalPage = (int) Math.ceil(totalNum/(double) MAX_SITE_MAP_CONTENT);
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
        for (int i = 1; i <= totalPage; i++) {
            queue.put(i);
        }
        return queue;
    }

    private BlockingQueue<Integer> getLatestPageQueue() throws InterruptedException {
        BlockingQueue<Integer> queue = getPageQueue();
        BlockingQueue<Integer> latestQueue = new LinkedBlockingQueue<Integer>();
        List<Integer> existQueue = new ArrayList<Integer>();
        while(!queue.isEmpty()){
            int page = queue.take();
            if(!OSSService.dose360SiteMapExist(page)){
                //不存在对应的site map，加到queue里
                latestQueue.add(page);
            }else{
                existQueue.add(page);
            }
        }
        //将存在的最后一个site map对应的page放到queue中
        latestQueue.add(existQueue.get(existQueue.size() - 1));
        return latestQueue;
    }

    class Single360ProductSiteMap implements Runnable {
        private BlockingQueue<Integer> queue;
        private Map<String, Object> map;

        Single360ProductSiteMap(BlockingQueue<Integer> queue, Map<String, Object> map){
            this.queue = queue;
            this.map = map;
        }

        public void run(){
            try {
                while(!queue.isEmpty()){
                    int page = queue.take();
                    String baseSiteMapPath = Seo360Enum.PRODUCT_SITE_MAP_PATH.getValue(page);

                    Paged<Goods> paged = new Paged<Goods>();
                    paged.setPageSize(MAX_SITE_MAP_CONTENT);
                    paged.setCurPage(page);
                    ArrayList<StringBuffer> pcUrls = new ArrayList<StringBuffer>();
                    ArrayList<StringBuffer> mUrls = new ArrayList<StringBuffer>();
                    List<Goods> list = goodsMapper.findMidByPage(paged);

                    // 休眠3000ms
                    Thread.sleep(3000);

                    for(Goods goods : list){
                        StringBuffer pc_url = new StringBuffer("https://www.bevol.cn/product/").append(goods.getMid()).append(".html");
                        StringBuffer m_url = new StringBuffer("https://m.bevol.cn/product/").append(goods.getMid()).append(".html");
                        pcUrls.add(pc_url);
                        mUrls.add(m_url);
                    }
                    //pc站点生成
                    map.put("urls", pcUrls);
                    String pc_xml = freemarkerService.get360SeoXml(SEO_360_URL_FTL, map);
                    OSSService.uploadXml2PC(pc_xml, baseSiteMapPath);
                    //m站点生成
                    map.put("urls", mUrls);
                    String m_xml = freemarkerService.get360SeoXml(SEO_360_URL_FTL, map);
                    OSSService.uploadXml2M(m_xml, baseSiteMapPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Object> get360Config(String configKey){
        Map<String, Object> map = new HashMap<String, Object>();
        Config config = configMapper.selectByKey(configKey);
        if(config !=null){
            String value = config.getValue();
            if(!StringUtils.isEmpty(value)){
                JSONObject valueJson = JSONObject.fromObject(value);
                map.put("frequency", valueJson.getString("frequency"));
                map.put("priority", valueJson.getString("priority"));
            }
        }
        return get360Config(map);
    }

    private Map<String, Object> get360Config(Map<String, Object> map){
        map.put("updateDate", DateUtils.timeComment(DateUtils.nowInSeconds()));
        return map;
    }

    private Map<String, Object> get360config(){
        Map<String, Object> map = new HashMap<String, Object>();
        return get360Config(map);
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        SeoService s = (SeoService) context.getBean("seoService");
        String[] param = {  "http://www.bevol.cn/composition/7323090f5479b50027eb0b392b894e86.html",
                "http://www.bevol.cn/product/0063cc9e85ae0fc004f3dd1bfbe4c55b.html"};
        //String str =s.update(param, "pc");
        s.seoPCBatch(5);
    }
}
