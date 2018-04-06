package cn.bevol.internal.service;

import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.mapper.*;
import cn.bevol.internal.entity.dto.SkinInterpretation;
import cn.bevol.internal.entity.entityAction.LikeGoods;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.JsonUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SkinService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(SkinService.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RedisCacheProvider cacheProvider;

    @Resource
    EntityService entityService;
    
    @Autowired
    SkinInterpretationOldMapper skinInterpretationOldMapper;

    @Autowired
    LikeGoodsOldMapper likeGoodsOldMapper;

    //修行说肤质
    final String likeskin="entity_like2_goods_skin";
    

    /**
     * todo 性能优化
     * 添加最爱单品信息进入mysql
     *
     * @return
     */
    public boolean updateTopGood() {
        try {
        	
        	//1、计算出 产品+肤质 喜欢和不喜欢的 数量  --- 同一个 产品+肤质 出现 喜欢和不喜欢两条记录  原始mongo做法
        /*	String mapFun = "function(){if(this.type>0&&this.skin&&this.entityId>0)  emit({k:this.entityId+'-'+this.skin+'-'+this.type,entityId:this.entityId,type:this.type,skin:this.skin},1); }";  
            String reduceFunction="function(key,values){ return Array.sum(values);}";
            DBCollection personColl = mongoTemplate.getCollection(tbLike);  
            

            //2、将喜欢喝不喜欢 合并成为 一条记录 且 求出喜欢数量-不喜欢数量 作为 num 
            MapReduceOutput mapReduceOutput = personColl.mapReduce(mapFun,  
            		reduceFunction, likeType, null);  
        	ScriptOperations  scirptOption=mongoTemplate.scriptOps();
        	//if(!scirptOption.exists(likeFunName))
            ExecutableMongoScript mongoScript = new ExecutableMongoScript(likeFunScript);  
            scirptOption.execute(mongoScript);*/
            
        		//执行
        		
        		likeGoodsOldMapper.deleteAll();
                Integer limit = ConfUtils.getResourceNum("skinlikegoods_mongo_find_limit");
               
                int mysqlInsertCount=ConfUtils.getResourceNum("skinlikegoods_mysql_batch_num");;
                List<HashMap> map=null;
                long o=0;
                do{
                	//long ccount= mongoTemplate.count(new Query(Criteria.where("type").is(1).and("entityId").gt(0).and("skin").ne(null)), LikeGoods.class, "entity_like_goods");
                	//map= mongoTemplate.find(new Query(Criteria.where("type").is(1).and("entityId").gt(0).and("skin").ne(null).and("id").gt(o)).with(new Sort(Direction.ASC, "id")).limit(limit), LikeGoods.class, "entity_like2_goods");
                	
                	map=mongoTemplate.find(new Query(Criteria.where("id").gt(o).and("num").gt(1)).with(new Sort(Direction.ASC, "id")).limit(limit), HashMap.class, likeskin);
                   
                	List<Map> insertbatch=new ArrayList<Map>();
                    for(int i=0;map!=null&&i<map.size();i++) {
	                		Map<String,Object> m=map.get(i);
                        	//100个插入一次
	                    	//100个插入一次
	                    	Map imap=new HashMap();
	                    	imap.put("entityid",m.get("entityId"));
	                    	imap.put("skin", m.get("skin"));
	                    	imap.put("entityid_skin", m.get("entityId")+"-"+m.get("skin"));
	                    	imap.put("num", m.get("num"));
	                    	//imap.put("like_num",m.get("likeNum"));
	                    	//imap.put("not_like_num",m.get("notlikeNum"));
	                    	//imap.put("comment_num",m.get("commentNum"));
	                    	//imap.put("comment_sum_score",m.get("commentSumScore"));
	                    	insertbatch.add(imap);
                        //批量插入
                        if(insertbatch.size()==mysqlInsertCount) {
                            likeGoodsOldMapper.insertBatch(insertbatch);
                           insertbatch=new ArrayList<Map>();
                        }
                        o= Long.parseLong(m.get("id")+"");
                    }
                    //插入剩余部分
                    if(insertbatch.size()>0) {
                    	 likeGoodsOldMapper.insertBatch(insertbatch);
                    }
                }while(map!=null&&map.size()>0);
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 缓存最爱单品信息
     *
     * @return
     */
    public boolean cacheGoods() {
        try {
            skinInterpretationOldMapper.deleteLikeGood();
            List<SkinInterpretation> list = skinInterpretationOldMapper.getAll();
            for (SkinInterpretation e : list) {
                List testList = new ArrayList();
                List<LikeGoods> likeGoods = new ArrayList<LikeGoods>();
                String pro_id = skinInterpretationOldMapper.getTypeId(e.getEntityId());
                String[] type = pro_id.split(",");
                for (int i = 0; i < type.length; i++) {
                    String skin = e.getCategory();
                    SkinInterpretation test = new SkinInterpretation();
                    SkinInterpretation SkinInterpretation = new SkinInterpretation();
                    if (0 == i) {
                        test.setPrompt1_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt1_id());
                            goodMap.put("name", SkinInterpretation.getPrompt1());
                            goodMap.put("state", SkinInterpretation.getPrompt1_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt1_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                    if (1 == i) {
                        test.setPrompt2_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt2_id());
                            goodMap.put("name", SkinInterpretation.getPrompt2());
                            goodMap.put("state", SkinInterpretation.getPrompt2_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt2_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                    if (2 == i) {
                        test.setPrompt3_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt3_id());
                            goodMap.put("name", SkinInterpretation.getPrompt3());
                            goodMap.put("state", SkinInterpretation.getPrompt3_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt3_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                    if (3 == i) {
                        test.setPrompt4_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt4_id());
                            goodMap.put("name", SkinInterpretation.getPrompt4());
                            goodMap.put("state", SkinInterpretation.getPrompt4_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt4_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                    if (4 == i) {
                        test.setPrompt5_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt5_id());
                            goodMap.put("name", SkinInterpretation.getPrompt5());
                            goodMap.put("state", SkinInterpretation.getPrompt5_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt5_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                    if (5 == i) {
                        test.setPrompt6_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt6_id());
                            goodMap.put("name", SkinInterpretation.getPrompt6());
                            goodMap.put("state", SkinInterpretation.getPrompt6_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt6_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                    if (6 == i) {
                        test.setPrompt7_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt7_id());
                            goodMap.put("name", SkinInterpretation.getPrompt7());
                            goodMap.put("state", SkinInterpretation.getPrompt7_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt7_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                    if (7 == i) {
                        test.setPrompt8_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt8_id());
                            goodMap.put("name", SkinInterpretation.getPrompt8());
                            goodMap.put("state", SkinInterpretation.getPrompt8_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt8_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                    if (8 == i) {
                        test.setPrompt9_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsOldMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationOldMapper.selectByPrimaryKey(test);
                        if (likeGoods.size() > 0) {
                            Map goodMap = new HashMap();
                            goodMap.put("id", SkinInterpretation.getPrompt9_id());
                            goodMap.put("name", SkinInterpretation.getPrompt9());
                            goodMap.put("state", SkinInterpretation.getPrompt9_advice());
                            goodMap.put("content", SkinInterpretation.getPrompt9_content());
                            goodMap.put("good", likeGoods);
                            testList.add(goodMap);
                        }
                    }
                }
                String toJson = JsonUtils.toGson(testList);
                skinInterpretationOldMapper.updateLikeGood(e.getEntityId(), toJson);
            }
            return true;
        } catch (Exception e) {
            logger.error( ExceptionUtils.getStackTrace(e));
            return false;
        }
    }


    public static Map<String, String> surveys = new HashMap<String, String>();

    // 测试变量
    static {
        //OQ_SZ_P_W
        surveys.put("1_D_11_16_DZ", "重度干性皮肤");
        surveys.put("1_D_17_26_DQ", "轻度干性皮肤");
        surveys.put("1_O_27_33_OQ", "轻度油性皮肤");
        surveys.put("1_O_34_44_OZ", "重度油性皮肤");

        surveys.put("3_R_18_24_RZ", "重度耐受性皮肤");
        surveys.put("3_R_25_29_RQ", "轻度耐受性皮肤");
        surveys.put("3_S_30_33_SQ", "轻度敏感性皮肤");
        surveys.put("3_S_34_72_SZ", "重度敏感性皮肤");

        surveys.put("4_N_10_30_N", "非色素沉着性皮肤");
        surveys.put("4_P_31_45_P", "色素沉着性皮肤");

        surveys.put("5_T_20_40_T", "紧致性皮肤");
        surveys.put("5_W_41_85_W", "皱纹性皮肤");
    }

    static String ids[] = new String[]{"1", "3", "4", "5"};

}
