package cn.bevol.internal.service;

import cn.bevol.mybatis.dao.SqlMapper;
import cn.bevol.model.entity.EntityUserPart;
import cn.bevol.entity.service.FreemarkerService;
import com.io97.utils.DateUtils;
import com.io97.utils.JsonUtils;
import com.io97.utils.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-6-27.
 */
@Service
public class InternalXmlService {
    private static Logger logger = LoggerFactory.getLogger(InternalXmlService.class);

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private SqlMapper sqlMapper;
    @Resource
    private FreemarkerService freemarkerService;
    //12135
    public void generateAladdinProductXml(){
        logger.info("===============开始生成xml================");

        StringBuilder goodsWhere = new StringBuilder();

        String goodsInfoUrl = "http://api.bevol.cn/goods/info/";
        String commentsInfoUrl = "http://api.bevol.cn/entity/comment4/lists/goods";

        for(int k=0; k<61; k++){
            String filePath = "/tmp/product_index_"+(k+1)+".xml";
            String sqlStr = "select id,mid from hq_goods where image !=0 and image is not null"+
                    " and image != '' and hidden=0 and deleted=0 and `category`>0 "+
                    "limit "+(k*200)+", 200";
            List<Map<String, Object>> list = sqlMapper.select(sqlStr);

            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("date", DateUtils.timeComment(DateUtils.nowInSeconds()));
            dataMap.put("url", "https://m.bevol.cn/aladdin/product_index_"+(k+1)+".xml");
            freemarkerService.createFile("aladdin_products_header", filePath, dataMap);

            int i = 1;
            for(Map<String, Object> info : list){
                logger.info("==============正在执行第"+i+"个产品================");
                logger.info("===id:"+info.get("id") + " mid:"+info.get("mid"));
                String productInfo = HttpUtils.post(goodsInfoUrl+info.get("mid"), new HashMap<String, String>());
                if(JSONObject.fromObject(productInfo).has("result")) {
                    productInfo = JSONObject.fromObject(productInfo).getString("result");
                    JSONObject productJson = JSONObject.fromObject(productInfo);
                    if (productJson.getJSONObject("goods").has("capacity")) {
                        String capacity = productJson.getJSONObject("goods").getString("capacity");
                        if (capacity.indexOf("g") > 0) {
                            productJson.getJSONObject("goods").element("weight", capacity);
                        } else if (capacity.indexOf("ml") > 0) {
                            productJson.getJSONObject("goods").element("volume", capacity);
                        }
                    }
                    productJson.getJSONObject("goods").element("title", escapeXml(productJson.getJSONObject("goods").getString("title")));
                    JSONArray safety = productJson.getJSONArray("safety");
                    String scoreStar = null;
                    for (Object item : safety) {
                        int id = JSONObject.fromObject(item).getInt("id");
                        if (id == 1) {
                            scoreStar = JSONObject.fromObject(item).getString("num");
                            break;
                        }
                    }
                    productJson.getJSONObject("goods").element("score_star", scoreStar);

                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("id", info.get("id") + "");
                    param.put("pager", "1");
                    param.put("pageSize", "50");
                    String commentsInfo = HttpUtils.post(commentsInfoUrl, param);
                    JSONArray commentsArr = JSONObject.fromObject(commentsInfo).getJSONObject("result").getJSONArray("list");
                    for (int j = 0; j < commentsArr.size(); j++) {
                        JSONObject commentObj = commentsArr.getJSONObject(j);
                        commentObj.element("updateStamp", DateUtils.timeStampParseDateStr(commentObj.getInt("updateStamp")));
                        commentsArr.set(j, commentObj);
                    }
                    productJson.element("comments", commentsArr);
                    i++;
                    HashMap<String, Object> productDataMap = new HashMap<String, Object>();
                    productDataMap.put("product", productJson);
                    try {
                        String ftl = freemarkerService.getStringFromFtl("aladdin_product_info", productDataMap);
                        appendMethod(filePath, ftl);
//                    FileWriter fileWriter = new FileWriter(filePath,true);
//                    fileWriter.write(ftl);
                        goodsWhere.append("(").append(info.get("id")).append(",0").append("),");
                    } catch (Exception e) {
                        e.printStackTrace();
                        goodsWhere.append("(").append(info.get("id")).append(",1").append("),");
                    }

                }else{
                    logger.error("此产品无result");
                    goodsWhere.append("(").append(info.get("id")).append(",1").append("),");
                }
            }

            try {
                String ftl = freemarkerService.getStringFromFtl("aladdin_products_footer", new HashMap<String, Object>());
                appendMethod(filePath, ftl);
//            FileWriter fileWriter = new FileWriter(filePath, true);
//            fileWriter.write(ftl);
            }catch(Exception e){
                e.printStackTrace();
                logger.error("=============尾部失败！！！！================");
            }
        }


        goodsWhere.deleteCharAt(goodsWhere.length()-1);
        String logGoodsSqlStr = "insert into baidu_aladdin_goods (goods_id, state) values "+goodsWhere;
        sqlMapper.insert(logGoodsSqlStr);



        logger.info("===============xml生成完毕！================");
    }

    public void generateAladdinUserListXml(){
        logger.info("===============开始生成xml================");
        StringBuilder where = new StringBuilder();

        for(int k=0;k<3;k++) {
            String filePath = "/tmp/user_list_index_"+(k+1)+".xml";
            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("date", DateUtils.timeComment(DateUtils.nowInSeconds()));
            freemarkerService.createFile("aladdin_user_list_header", filePath, dataMap);


            Query query = new Query();
            query.limit(1000);
            query.skip(k*1000);
            Criteria criteria = new Criteria();
            criteria.and("hidden").is(0);
            criteria.and("deleted").is(0);
            query.addCriteria(criteria);
            //用户心得
            List<EntityUserPart> lists = mongoTemplate.find(query, EntityUserPart.class, "entity_user_part_lists");


            for (EntityUserPart list : lists) {
                logger.info("=========start for id " + list.getId() + "=============");
                HashMap<String, Object> userListDataMap = new HashMap<String, Object>();
                String listString = JsonUtils.toJson(list);
                JSONObject listJson = JSONObject.fromObject(listString);
                listJson.element("updateStamp", DateUtils.timeComment(listJson.getInt("updateStamp")));
                if (listJson.has("userPartDetails")) {
                    JSONArray userPartDetails = listJson.getJSONArray("userPartDetails");
                    JSONArray goods = new JSONArray();
                    JSONArray contents = new JSONArray();
                    for (Object userPartDetail : userPartDetails) {
                        JSONObject userPartDetailJson = JSONObject.fromObject(userPartDetail);
                        if (userPartDetailJson.getInt("type") == 1) {
                            if (userPartDetailJson.has("title") && userPartDetailJson.get("title") != null) {
                                userPartDetailJson.element("title", escapeXml(userPartDetailJson.getString("title")));
                                goods.element(userPartDetailJson);
                            }
                        }
                        if (userPartDetailJson.getInt("type") == 2) {
                            if (userPartDetailJson.has("content") && userPartDetailJson.get("content") != null) {
                                userPartDetailJson.element("content", escapeXml(userPartDetailJson.getString("content")));
                                contents.element(userPartDetailJson);
                            }
                        }
                    /*if (userPartDetailJson.getInt("type") == 3) {
                        if (userPartDetailJson.has("image") && userPartDetailJson.get("image") != null) {
                            userPartDetailJson.element("content", userPartDetailJson.getString("image"));
                            contents.element(userPartDetailJson);
                        }
                    }*/
                    }
                    listJson.element("goods", goods);
                    listJson.element("contents", contents);
                    listJson.element("title", escapeXml(listJson.getString("title")));
                    userListDataMap.put("list", listJson);
                    for(int j=0; j<listJson.getJSONArray("goods").size(); j++){
                        Object product = listJson.getJSONArray("goods").get(j);
                        JSONObject productJson = JSONObject.fromObject(product);
                        productJson.element("title", escapeXml(productJson.getString("title")));
                        try {
                            productJson.element("image", URLEncoder.encode(productJson.getString("image"), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //productJson.element("title", "专注抢猫奴钱的paul&joe");
                        listJson.getJSONArray("goods").element(j, productJson);
                    }
                    userListDataMap.put("url", "https://m.bevol.cn/aladdin/user_list_index_"+(k+1)+".xml");
                    userListDataMap.put("date", DateUtils.timeComment(DateUtils.nowInSeconds()));
                    try {
                        String ftl = freemarkerService.getStringFromFtl("aladdin_user_list_info", userListDataMap);
                        appendMethod(filePath, ftl);
                        where.append("(").append(listJson.get("id")).append(",0").append("),");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        where.append("(").append(listJson.get("id")).append(",1").append("),");
                    }
                } else {
                    where.append("(").append(listJson.get("id")).append(",1").append("),");
                }
            }

            try {
                String ftl = freemarkerService.getStringFromFtl("aladdin_user_list_footer", dataMap);
                appendMethod(filePath, ftl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        where.deleteCharAt(where.length()-1);
        String logGoodsSqlStr = "insert into baidu_aladdin_user_list (user_list_id, state) values "+where;
        sqlMapper.insert(logGoodsSqlStr);
        logger.info("===============xml生成完毕！================");

    }

    public void generateAladdinFindXml(){
        logger.info("===============开始生成xml================");

        List<Map<String, Object>> authorMap = sqlMapper.select("select author_id,nickname from hq_new_find f " +
                "left join hq_user u on f.author_id = u.id " +
                "where author_id != 0 group by author_id");
        HashMap<Integer, String> authorHashMap = new HashMap<Integer, String>();
        for(Map<String, Object> authorInfo : authorMap){
            authorHashMap.put((Integer) authorInfo.get("id"), (String) authorInfo.get("nickname"));
        }

        StringBuilder goodsWhere = new StringBuilder();

        String goodsInfoUrl = "http://api.bevol.cn/find/info/";

        for(int k=0; k<1; k++){
            String filePath = "/tmp/find_"+(k+1)+".xml";
            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("date", DateUtils.timeComment(DateUtils.nowInSeconds()));
            freemarkerService.createFile("aladdin_user_list_header", filePath, dataMap);

            //发现文章
            List<Map<String, Object>> findLists = sqlMapper.select("select id from hq_new_find where hidden=0 and deleted=0 "+
                    "limit "+(k*500)+", 500"
            );

            for (Map<String, Object> info : findLists) {
                logger.info("=========start for find id " + info.get("id") + "=============");
                String findInfo = HttpUtils.post(goodsInfoUrl+info.get("id"), new HashMap<String, String>());
                if(JSONObject.fromObject(findInfo).has("result")) {
                    findInfo = JSONObject.fromObject(findInfo).getString("result");
                    JSONObject findJson = JSONObject.fromObject(findInfo);
                    findJson.element("updateStamp", DateUtils.timeComment(findJson.getInt("updateStamp")));
                    findJson.element("descp", escapeXml(findJson.getString("descp").trim()));
                    findJson.element("title", escapeXml(findJson.getString("title").trim()));
                    findJson.element("nickname", authorHashMap.get(findJson.getInt("authorId")));
                    HashMap<String, Object> findDataMap = new HashMap<String, Object>();
                    findDataMap.put("list", findJson);
                    findDataMap.put("date", DateUtils.timeComment(DateUtils.nowInSeconds()));
                    try {
                        String ftl = freemarkerService.getStringFromFtl("aladdin_find_info", findDataMap);
                        appendMethod(filePath, ftl);
                        goodsWhere.append("(").append(info.get("id")).append(",0").append("),");
                    } catch (Exception e) {
                        e.printStackTrace();
                        goodsWhere.append("(").append(info.get("id")).append(",1").append("),");
                    }
                }else{
                    logger.error("此发现无result");
                    goodsWhere.append("(").append(info.get("id")).append(",1").append("),");
                }
            }



            try {
                String ftl = freemarkerService.getStringFromFtl("aladdin_user_list_footer", new HashMap<String, Object>());
                appendMethod(filePath, ftl);
//            FileWriter fileWriter = new FileWriter(filePath, true);
//            fileWriter.write(ftl);
            }catch(Exception e){
                e.printStackTrace();
                logger.error("=============尾部失败！！！！================");
            }
        }


        goodsWhere.deleteCharAt(goodsWhere.length()-1);
        String logGoodsSqlStr = "insert into baidu_aladdin_find (find_id, state) values "+goodsWhere;
        sqlMapper.insert(logGoodsSqlStr);



        logger.info("===============xml生成完毕！================");
    }

    public void generateOpenArticleXml(){
        logger.info("===============开始生成xml================");

        List<Map<String, Object>> authorMap = sqlMapper.select("select author_id,nickname,count(*) as total_num from hq_new_find f " +
                "left join hq_user u on f.author_id = u.id " +
                "where author_id != 0 group by author_id");
        HashMap<Integer, String> authorHashMap = new HashMap<Integer, String>();
        HashMap<Integer, Integer> authorTotalHashMap = new HashMap<Integer, Integer>();
        for(Map<String, Object> authorInfo : authorMap){
            authorHashMap.put((Integer) authorInfo.get("author_id"), (String) authorInfo.get("nickname"));
            authorTotalHashMap.put((Integer) authorInfo.get("author_id"), ((Long) authorInfo.get("total_num")).intValue());
        }

        StringBuilder goodsWhere = new StringBuilder();

        String goodsInfoUrl = "http://api.bevol.cn/find/info/";

        for(int k=0; k<1; k++){
            String filePath = "/tmp/find_"+(k+1)+".xml";
            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("date", DateUtils.timeComment(DateUtils.nowInSeconds()));
            freemarkerService.createFile("open_baidu_header", filePath, dataMap);

            //发现文章
            List<Map<String, Object>> findLists = sqlMapper.select("select id from hq_new_find where hidden=0 and deleted=0 "+
                    "limit "+(k*500)+", 500"
            );

            for (Map<String, Object> info : findLists) {
                logger.info("=========start for find id " + info.get("id") + "=============");
                String findInfo = HttpUtils.post(goodsInfoUrl+info.get("id"), new HashMap<String, String>());
                if(JSONObject.fromObject(findInfo).has("result")) {
                    findInfo = JSONObject.fromObject(findInfo).getString("result");
                    JSONObject findJson = JSONObject.fromObject(findInfo);
                    findJson.element("updateStamp", DateUtils.timeComment(findJson.getInt("updateStamp")));
                    findJson.element("descp", escapeXml(findJson.getString("descp").trim()));
                    findJson.element("title", escapeXml(findJson.getString("title").trim()));
                    findJson.element("nickname", escapeXml(authorHashMap.get(findJson.getInt("authorId"))));
                    findJson.element("authorTotalNum", authorTotalHashMap.get(findJson.getInt("authorId")));
                    HashMap<String, Object> findDataMap = new HashMap<String, Object>();
                    findDataMap.put("list", findJson);
                    findDataMap.put("date", DateUtils.timeComment(DateUtils.nowInSeconds()));
                    try {
                        String ftl = freemarkerService.getStringFromFtl("open_baidu_article", findDataMap);
                        appendMethod(filePath, ftl);
                        goodsWhere.append("(").append(info.get("id")).append(",0").append("),");
                    } catch (Exception e) {
                        e.printStackTrace();
                        goodsWhere.append("(").append(info.get("id")).append(",1").append("),");
                    }
                }else{
                    logger.error("此发现无result");
                    goodsWhere.append("(").append(info.get("id")).append(",1").append("),");
                }
            }



            try {
                String ftl = freemarkerService.getStringFromFtl("open_baidu_footer", new HashMap<String, Object>());
                appendMethod(filePath, ftl);
//            FileWriter fileWriter = new FileWriter(filePath, true);
//            fileWriter.write(ftl);
            }catch(Exception e){
                e.printStackTrace();
                logger.error("=============尾部失败！！！！================");
            }
        }


        goodsWhere.deleteCharAt(goodsWhere.length()-1);
        String logGoodsSqlStr = "insert into baidu_aladdin_find (find_id, state) values "+goodsWhere;
        //sqlMapper.insert(logGoodsSqlStr);



        logger.info("===============xml生成完毕！================");
    }



    public static void appendMethod(String file, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String escapeXml(String content){
        return StringEscapeUtils.escapeXml(content);
    }

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/internal-spring-config.xml");
        InternalXmlService backXmlService = (InternalXmlService) context.getBean("backXmlService");

        backXmlService.generateOpenArticleXml();
        //String title = escapeXml("专注抢猫奴钱的paul&joe");

        //backXmlService.generateAladdinFindXml();
        /*if("1".equals(args[0])){
            backXmlService.generateAladdinProductXml();
        }
        if("2".equals(args[0])){
            backXmlService.generateAladdinUserListXml();
        }
        if("3".equals(args[0])){
            backXmlService.generateAladdinFindXml();
        }
        if("4".equals(args[0])){
            backXmlService.generateOpenArticleXml();
        }*/
    }
}
