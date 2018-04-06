package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.*;
import cn.bevol.app.entity.dto.LikeGoods;
import cn.bevol.app.entity.dto.ShareEntity;
import cn.bevol.app.entity.dto.SkinInterpretation;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.user.SkinTestResult;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.JsonUtils;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ResponseBuilder;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.statistics.StatisticsI;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
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

    private static String hy="hy";
    private static String pyq="pyq";
    private static String wb="wb";
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private LikeGoodsOldMapper likeGoodsMapper;

    @Autowired
    private SkinInterpretationOldMapper skinInterpretationMapper;

    @Autowired
    RedisCacheProvider cacheProvider;

    @Autowired
    private ShareEntityOldMapper shareEntityMapper;
    
    @Autowired
    private GoodsOldMapper goodsMapper;
    
    @Autowired
    UserInfoOldMapper userInfoMapper;
    @Resource
    UserService userService;
    
    @Resource
    CommenStatisticsService commenStatisticsService;
    
    @Resource
    IndexService indexService;
    
    @Resource
    EntityService entityService;
    
    
    
    @Resource
    AdvertisementLogClientService advertisementLogClientService;

	final String tbLike="entity_like2_goods";

    //修行说类型
    final String likeType="entity_like2_goods_type";
    //修行说肤质
    final String likeskin="entity_like2_goods_skin";
    
   final String likeFunScript="function () {"+
		"var outlike='"+likeType+"';"+
		"var likeskin='"+likeskin+"'; "+
		"db[outlike].ensureIndex({'_id.k':1});"+
		"db[likeskin].remove({});"+
		"var ic=0;"+
		"db[outlike].find({'_id.type':1}).forEach(function(item) {"+
		"	var itm2=item._id;"+
		"	var gys=db[outlike].find({'_id.k':itm2.entityId+'-'+itm2.skin+'-2'})[0];   ic++;"+
		"	if(gys&&gys._id) {"+
		"		var itm=gys._id;"+
		"		var num=item.value-gys.value;"+
		"		if(num<0) num=0;"+
		"		db[likeskin].insert({id:NumberLong(ic),type:NumberInt(1),likeNum:NumberLong(item.value),notlikeNum:NumberLong(gys.value),num:NumberLong(num),entityId:NumberLong(itm2.entityId),skin:itm2.skin});"+
		"	} else {"+
		"		if(item.value>1) {db[likeskin].insert({id:NumberLong(ic),type:NumberInt(1),likeNum:NumberLong(item.value),notlikeNum:NumberLong(0),num:NumberLong(item.value),entityId:NumberLong(itm2.entityId),skin:itm2.skin});}"+
			"}"+
		"});"+
		" db[likeskin].ensureIndex({'entityId':1});db[likeskin].ensureIndex({'num':-1,'skin':-1});"+
"	};";
    
    
	  //  final String likeFunName="likeTest";
	 //   final String likeFunScript="function () { return 'eee'};";

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
        		
        		likeGoodsMapper.deleteAll();
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
                            likeGoodsMapper.insertBatch(insertbatch);
                           insertbatch=new ArrayList<Map>(); 
                        }
                        o=Long.parseLong(m.get("id")+"");
                    }
                    //插入剩余部分
                    if(insertbatch.size()>0) {
                    	 likeGoodsMapper.insertBatch(insertbatch);
                    }
                }while(map!=null&&map.size()>0);
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * todo 性能优化
     * 获取16种肤质中一种的 最爱单品列表单和肤质指南数据
     *
     * @param skin
     * @return
     */
	public Map findSkinAndGoods(final String skin) {
		return new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					SkinInterpretation skinInterpretation = new SkinInterpretation();
					List<LikeGoods> likeGoods = new ArrayList<LikeGoods>();
					String like_good = "";
					if (StringUtils.isNotBlank(skin)) {
						//根据skin获取肤质信息
						skinInterpretation = skinInterpretationMapper.findskinInterpretation(skin);
						//获取相应skin的用户最爱产品信息
						like_good = skinInterpretation.getLike_good();
						likeGoods = JsonUtils.toObject(like_good, List.class);
					}

					Map map = new HashMap();
					map.put("skin", skinInterpretation);
					map.put("good", likeGoods);

					List<Map<String, Object>> changeId = new ArrayList<Map<String, Object>>();
					// 广告处理
					Map adMap = advertisementLogClientService.findAd(3, null, null,"1");
					// 判断是否有广告
					if (null != adMap && adMap.size() > 0) {
						List<Map<String, String>> entityInfo = indexService.getAdInfo(adMap);
						for (int j = 0; null != entityInfo && j < entityInfo.size(); j++) {
							Map<String, String> advMap = entityInfo.get(j);
							// 广告位置
							int adOrientation = Integer.parseInt(advMap.get("adOrientation"));
							// 产品分类
							int category = Integer.parseInt(advMap.get("entityType"));
							// 广告id
							int adId = Integer.parseInt(advMap.get("adId"));
							
							//跳转终端 1app 2model 3pc
							String positionType = (String) advMap.get("positionType");
							//跳转类型(站内/站外)
							int redirectType = Integer.parseInt(advMap.get("redirectType"));
							//跳转地址
							String redirectUrl = (String) advMap.get("redirectUrl");
							
							String adImage = "";
							if (StringUtils.isNotBlank(advMap.get("image"))) {
								adImage = advMap.get("image");
							}
							// 查找要替换的产品
							String entityId = advMap.get("entityId") + "";

							// 替换json
							Goods goods = null;
							// 查数据库
							if (StringUtils.isNotBlank(entityId)) {
								goods = goodsMapper.getById(Long.parseLong(entityId));
							}
							// 转换json
							Map jsonMap = new HashMap();

							if (null != goods) {
								// 是否返回了广告的图片
								if (StringUtils.isNotBlank(adImage)) {
									jsonMap.put("image", adImage);
								}else{
									if(StringUtils.isNotBlank(goods.getImage())){
										jsonMap.put("image", goods.getImage());
									}
								}
								// 判断空指针
								jsonMap.put("entityId", goods.getId());
								jsonMap.put("hidden", goods.getHidden());
								jsonMap.put("deleted", goods.getDeleted());
								jsonMap.put("updateStamp", goods.getUpdateStamp());
								jsonMap.put("createStamp", goods.getCreateStamp());
								jsonMap.put("mid", goods.getMid());
								jsonMap.put("title", goods.getTitle());
								jsonMap.put("dataType", goods.getDataType());
								if (StringUtils.isNotBlank(goods.getRemark3())) {
									jsonMap.put("remark3", goods.getRemark3());
								}
								if (StringUtils.isNotBlank(goods.getAlias())) {
									jsonMap.put("alias", goods.getAlias());
								}
								if (StringUtils.isNotBlank(goods.getAlias2())) {
									jsonMap.put("alias2", goods.getAlias2());
								}
								if (StringUtils.isNotBlank(goods.getPrice())) {
									jsonMap.put("price", goods.getPrice());
								}
								if (StringUtils.isNotBlank(goods.getCapacity())) {
									jsonMap.put("capacity", goods.getCapacity());
								}

								// 查找点击数和喜欢数
								EntityBase mongGoods = mongoTemplate.findOne(
										new Query(Criteria.where("id").is(goods.getId())), EntityBase.class,
										"entity_goods");
								jsonMap.put("num", mongGoods.getLikeNum().intValue());
								jsonMap.put("hot", mongGoods.getHitNum().intValue());
								jsonMap.put("commentNum", mongGoods.getCommentNum());
								jsonMap.put("likeNum", mongGoods.getLikeNum());
								jsonMap.put("grade", mongGoods.getGrade());
								jsonMap.put("safety_1_num", mongGoods.getSafety_1_num());
								// 广告信息
								jsonMap.put("adId", adId);
								jsonMap.put("adOrientation", adOrientation);
								jsonMap.put("category", category);
								
								jsonMap.put("positionType", positionType);
								jsonMap.put("redirectType", redirectType);
								jsonMap.put("redirectUrl", redirectUrl);

								JSONObject jsonObject = JSONObject.fromObject(jsonMap);
								// result.set(k, jsonObject);
								Map mapId = new HashMap();
								mapId.put("json", jsonObject);
								mapId.put("adId", adId);
								mapId.put("adOrientation", adOrientation);
								mapId.put("category", category);
								
								mapId.put("positionType", positionType);
								mapId.put("redirectType", redirectType);
								mapId.put("redirectUrl", redirectUrl);
								//拼接结构 为转json准备
								changeId.add(mapId);
							}
						}
						
						//替换json
						JSONArray jsonObject = JSONArray.fromObject(like_good);
						if (jsonObject.size() > 0) {
							for (int k = 0; k < jsonObject.size(); k++) {
								JSONObject job = jsonObject.getJSONObject(k);
								int goodsCategory = (Integer) job.get("id");

								Map jsonMap = new HashMap();
								jsonMap.put("content", job.get("content"));
								jsonMap.put("id", goodsCategory);
								jsonMap.put("name", job.get("name"));
								jsonMap.put("state", job.get("state"));
								JSONObject newObject = null;
								boolean flag = false;
								for (int i = 0; i < changeId.size(); i++) {
									// 广告的信息
									int category = (Integer) changeId.get(i).get("category");
									int adOrientation = (Integer) changeId.get(i).get("adOrientation");
									// 找到分类
									if (goodsCategory == category) {
										// 获取分类下的产品信息
										JSONArray goodsJson = JSONArray.fromObject(job.get("good"));
										for (int j = 0; j < goodsJson.size(); j++) {
											// 找到广告位置
											if ((adOrientation - 1) == j) {
												// 进行替换
												goodsJson.set(j, changeId.get(i).get("json"));
												flag = true;
											}
										}
										jsonMap.put("good", goodsJson);
										// 替换对象
										newObject = JSONObject.fromObject(jsonMap);
									}
								}
								if (flag) {
									jsonObject.set(k, newObject);
								}
							}

						}

						likeGoods = JsonUtils.toObject(jsonObject.toString(), List.class);
						map.put("good", likeGoods);
					}

					if (map != null) {
						map.put("ret", 0);
						return map;
					}
					return ResponseBuilder.buildFailureMessage("数据为空");
				} catch (Exception e) {
					logger.error("method:findSkinAndGoods arg:{skin:" + skin + "}" + "   desc:"
							+ ExceptionUtils.getStackTrace(e));
				}
				return ResponseBuilder.buildFailureMessage();
			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue.get("skin") != null && returnValue.get("good") != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_SKIN_LIKE_GOODS_PREFIX, skin)), true);

	}

    /**
     * 缓存最爱单品信息
     *
     * @return
     */
    public boolean cacheGoods() {
        try {
            skinInterpretationMapper.deleteLikeGood();
            List<SkinInterpretation> list = skinInterpretationMapper.getAll();
            for (SkinInterpretation e : list) {
                List testList = new ArrayList();
                List<LikeGoods> likeGoods = new ArrayList<LikeGoods>();
                String pro_id = skinInterpretationMapper.getTypeId(e.getEntityId());
                String[] type = pro_id.split(",");
                for (int i = 0; i < type.length; i++) {
                    String skin = e.getCategory();
                    SkinInterpretation test = new SkinInterpretation();
                    SkinInterpretation SkinInterpretation = new SkinInterpretation();
                    if (0 == i) {
                        test.setPrompt1_id(Integer.parseInt(type[i]));
                        test.setCategory(skin);
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                        likeGoods = likeGoodsMapper.findGood(skin, Integer.parseInt(type[i]));
                        SkinInterpretation = skinInterpretationMapper.selectByPrimaryKey(test);
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
                skinInterpretationMapper.updateLikeGood(e.getEntityId(), toJson);
            }
            return true;
        } catch (Exception e) {
            logger.error( ExceptionUtils.getStackTrace(e));
            return false;
        }
    }


    public boolean insert(SkinInterpretation test) {
        int i = skinInterpretationMapper.insert(test);
        return i > 0 ? true : false;
    }

    public boolean delete(SkinInterpretation test) {
        int i = skinInterpretationMapper.delete(test);
        return i > 0 ? true : false;
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

    /**
     * 用户设置肤质
     *
     * @param userId
     * @param skinId
     * @param key
     * @return
     */
    public ReturnData setSkin(long userId, long skinId, String key) {
        try {
            if (Arrays.binarySearch(ids, skinId + "") == -1) {
                return SkinTestResult.ERROR_NOT_SKIN;
            }
            String skey = skinId + "_" + key;
            String result_msg = "";
            String type = "";
            boolean flag = true;
            for (String k : surveys.keySet()) {
                if (k.indexOf(skey) != -1 && flag) {
                    result_msg = surveys.get(k);
                    type = k.split("_")[4];
                    flag = false;
                }
            }
            if (StringUtils.isBlank(result_msg))
                return SkinTestResult.ERROR_NOT_SKIN;

            SkinTestResult suveryid = new SkinTestResult();
            suveryid.setId(skinId);
            String result = key.split("_")[0];
            suveryid.setResult(result);
            suveryid.setResult_msg(result_msg);
            suveryid.setResultMsg(result_msg);
            suveryid.setSkinResults(type);
            return saveSurvey(userId, suveryid);
        } catch (Exception e) {
            logger.error("method:setSkin arg:{\"userId\":\"" + userId + "\",\"skinId\":" + skinId + ",\"key\":\"" + key + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }

    /**
     * 保存肤质测试结果
     *
     * @param userId
     * @param suveryid
     * @return
     */
    private ReturnData saveSurvey(long userId, SkinTestResult suveryid) {
        ReturnData ud = userService.getUserById(userId);
        if (ud.getRet() != 0) return ud;
        UserInfo user = (UserInfo) ud.getResult();
        //封装json
        String test = "";
        String id = suveryid.getId() + "";
        Map test1_tmp = new HashMap();
        List<String> doz = new ArrayList<String>();
        Map<String, Map> done = new HashMap<String, Map>();

        //查看测试情况
        if (!StringUtils.isBlank(user.getTest())) {
            //已经测试过
            test = user.getTest();
            Map<String, Object> test1_cookie = new JSONDeserializer<Map<String, Object>>().deserialize(test, HashMap.class);
            if (test1_cookie.get("do") == null || test1_cookie.get("do").equals("")) {
            } else {
                List lds = (List) test1_cookie.get("do");
                for (int i = 0; i < lds.size(); i++) {
                    doz.add(lds.get(i) + "");
                }

            }
            if (test1_cookie.get("done") != null)
                done = (Map) test1_cookie.get("done");
        } else {
            //第一次测试
            doz.add("1");
            doz.add("3");
            doz.add("4");
            doz.add("5");
        }

        String nextid = "";
        for (int i = 0; i < doz.size(); i++) {
            if (doz.get(i).equals(id)) {
                //获取后面一个
                doz.remove(i);
                break;
            }
        }
        if (doz.size() > 0 && doz.get(0) != null) {
            nextid = doz.get(0);
        }
        String reusltdo = new JSONSerializer().deepSerialize(doz);
        Map curSuvery = new HashMap();
        curSuvery.put("id", suveryid.getId());
        curSuvery.put("result", suveryid.getResult());
        curSuvery.put("result_msg", suveryid.getResult_msg());
        curSuvery.put("score", suveryid.getScore());
        curSuvery.put("skin_results", suveryid.getSkinResults());
        done.put(id + "", curSuvery);
        StringBuffer sb = new StringBuffer();
        String rsl = "";
        String skinResults = "";
        //封装测试结果
        for (String d : ids) {
            Map m = done.get(d);
            if (m != null) {
                if (m.get("result") != null)
                    rsl += m.get("result");
                skinResults += "_" + m.get("skin_results");
                String str = "\"" + d + "\":{\"id\":\"" + d + "\",\"score\":" + m.get("score") + ",\"skin_results\":\"" + m.get("skin_results") + "\",\"result\":\"" + m.get("result") + "\",\"result_msg\":\"" + m.get("result_msg") + "\"}";
                sb.append(str).append(",");
            }
        }
        Map return_date = new HashMap();
        String fz = sb.toString();
        fz = "{" + fz.substring(0, fz.length() - 1) + "}";
        test = "{\"do\":" + reusltdo + ",\"done\":" + fz + ",\"result\":\"" + rsl + "\"}";

        //测试完成
        if (rsl.length() == 4) {
            //全部完成
            skinResults = skinResults.substring(1);
            return_date.put("all", 1);
            return_date.put("result", rsl);
            return_date.put("skinResults", skinResults);
            return_date.put("result_msg", suveryid.getResultMsg());
            return_date.put("test", test);
            userInfoMapper.updateField("result", rsl, "id", userId);
            UserInfo userInfo = new UserInfo();
            
            Map mongolog=new HashMap();
            //当前的肤质信息
            mongolog.putAll(curSuvery);
            
            //完成之后的肤质信息
            mongolog.put("finished_result", rsl);
            mongolog.put("finished_skinResults", skinResults);
            mongolog.put("finished_test", test);
            
            //测试完成
        	mongolog.put("state", 1);
        	
            //第一次测试结束之后加50分
            if (StringUtils.isBlank(user.getResult())) {
            	userInfo.setFirstSkinTestTime(new Date().getTime()/1000);
            	userService.addScore(userId, UserService.ScoreOpt.FINISHEDSKIN);
            	return_date.put("doyenScore", 50);
            	//第一次测试
            	mongolog.put("reset_test", 0);
            } else {
            	//重复测试
            	mongolog.put("reset_test", 1);
            }
            commenStatisticsService.mongoLog(StatisticsI.COLLECTION_SKIN_TEST, mongolog);

            
            user.setResult(rsl);
            //保存肤质
            userInfo.setId(userId);
            userInfo.setResult(rsl);
            userInfo.setSkinResults(skinResults);
            userInfo.setSkinTestTime(new Date().getTime()/1000);
            userInfoMapper.updateSKin(userInfo);
            //完成记一下
            //记下：return_date
            //统计测试完毕的清空
        } else {
            //完成某题
            return_date.put("nextid", nextid);
            return_date.put("result_msg", suveryid.getResultMsg());
            return_date.put("test", test);
            
            //未测试过 选择考题精选测试 完成
            curSuvery.put("reset_test", 0);
            //没有完成
            curSuvery.put("state", 0);
            commenStatisticsService.mongoLog(StatisticsI.COLLECTION_SKIN_TEST, curSuvery);
        }
        //suveryid
        userInfoMapper.updateField("test", test, "id", userId);
        user.setTest(test);
        //转json
        //当前测试的 记一下
       // curSuvery
        //保存肤质
        return new ReturnData(return_date,0);
    }

    /**
     * 用户肤质测试
     *
     * @param userId
     * @param skinId
     * @param score
     * @return
     */
    public ReturnData skinTest(long userId, long skinId, Integer score) {
        try {
            if (Arrays.binarySearch(ids, skinId + "") == -1) {
                return SkinTestResult.ERROR_NOT_SKIN;
            }
            //判断肤质
            SkinTestResult suveryid = new SkinTestResult();
            boolean flag = true;
            for (String str : surveys.keySet()) {
                String sys[] = str.split("_");
                String idz = sys[0];
                if (idz.equals(skinId + "") && flag) {
                    String result = sys[1];
                    int min = Integer.parseInt(sys[2]);
                    int max = Integer.parseInt(sys[3]);
                    String type = sys[4];
                    if (score >= min && max >= score) {
                        String result_msg = surveys.get(str);
                        suveryid.setId(skinId);
                        suveryid.setScore(score);
                        suveryid.setResult(result);
                        suveryid.setSkinResults(type);
                        suveryid.setResult_msg(result_msg);
                        suveryid.setResultMsg(result_msg);
                        flag = false;
                        break;
                    }
                }
            }//
            return saveSurvey(userId, suveryid);
        } catch (Exception e) {
            logger.error("method:skinTest arg:{\"userId\":\"" + userId + "\",\"skinId\":" + skinId + ",\"score\":" + score + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;


    }
    
    
    
    /**
     * 16种肤质列表
     *
     * @return
     */
    public List skinList() {
        try {
        	String key="skin_list";
            String value=entityService.getConfig(key);
            if(!StringUtils.isBlank(value)){
       		 JSONArray json = JSONArray.fromObject(value);
                return new ArrayList(json);
            }
        } catch (Exception e) {
            logger.error("method:skinList arg:{"+ "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public ReturnData newSkinTest(final String ids) {
    	try {
        	String[] idss;
        	if(StringUtils.isBlank(ids)){
        		return ReturnData.ERROR;
        	}else{
        		idss=ids.split(",");
        	}
        	List<Map> list2=new ArrayList();
        	for(int j=0;j<idss.length;j++){
        		List<Map> list=new ArrayList();
        		Map<String,List<Map<String,Object>>> map2=skinDesc(Long.valueOf(idss[j]));
                List<Map<String,Object>> titltListMap=map2.get("title");
                List<Map<String,Object>> questionListMap=map2.get("question");
                List<Map<String,Object>> answerListMap=map2.get("answer");
                
                for(Map tmap:titltListMap){
                	Map map=new HashMap();
                	Integer id=(Integer)tmap.get("id");
                	String title=(String)tmap.get("title");
                	String remark=(String)tmap.get("remark");
                	
                	map.put("id", id);
                	map.put("title", title);
                	map.put("remark", remark);
                	List questionList=new ArrayList();
                	
                	for(Map qmap:questionListMap){
                		Map<String,Object> questMap=new HashMap();
                		List answerList=new ArrayList();
                		Integer titleId=(Integer)qmap.get("survey_title_id");
                		String questionStr=(String)qmap.get("question");
                		Integer questionId=(Integer)qmap.get("id");
                		String tip=(String)qmap.get("tip");
                		if(!StringUtils.isBlank(tip)){
                			questMap.put("tip", tip);
                		}
                		questMap.put("id", questionId);
                		questMap.put("survey_title_id", titleId);
                		if(id==titleId){
                			questMap.put("question", questionStr);
                		}
                		Map<String,Object> answerMap=null;
                		int i=0;
                		for(Map amap:answerListMap){
                			answerMap=new HashMap();
                			Integer answerId=(Integer)amap.get("id");
                			String answerStr=(String)amap.get("answer");
                			Integer questionId2=(Integer)amap.get("survey_question_id");
                			Float score=(Float)amap.get("score");
                			if(questionId2==questionId){
                    			answerMap.put("survey_question_id", questionId2);
                    			answerMap.put("id", answerId);
                    			answerMap.put("answer", answerStr);
                    			answerMap.put("score",score);
                			}
                			if(null!=answerMap && answerMap.size()>0){
                				answerList.add(answerMap);
                				i++;
                			}
                			if(i==5){
                				break;
                			}
                		}
                		questMap.put("answer", answerList);
                		questionList.add(questMap);
                	}
                	map.put("question", questionList);
                	list.add(map);
                }
                list2.addAll(list);
        	}
            return new ReturnData(list2);
        } catch (Exception e) {
            logger.error("method:newSkinTest arg:{	ids:"+ids  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }


    /**
     * 根据测试模块id （共四个） 获取肤质测试题目与选项
     * @param id
     * @return
     */
    public Map<String,List<Map<String,Object>>> skinDesc(final Long id){
    	return new CacheableTemplate<Map>(cacheProvider) {
            @Override
            protected Map getFromRepository() {
            	try{
            		Map<String,List<Map<String,Object>>> map=new HashMap();
                	List<Map<String,Object>> titltListMap=userInfoMapper.getSkinTitle(id+"");
                    List<Map<String,Object>> questionListMap=userInfoMapper.getSkinQuestion(id+"");
                    List<Map<String,Object>> answerListMap=userInfoMapper.getSkinAnswer();
                    map.put("title", titltListMap);
                    map.put("question", questionListMap);
                    map.put("answer", answerListMap);
                	return map;
            	}catch(Exception e){
                    logger.error("method:skinDesc arg:{	id:"+id  + "   desc:" +  ExceptionUtils.getStackTrace(e));
            	}
            	return new HashMap();
            }
        	@Override
            protected boolean canPutToCache(Map map) {
                return (map != null);
            }
            }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
            		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_SKIN_TEST_DESC_PREFIX,id+"") ), true);
    }
    
    /**
     * 肤质分享
     *config表中获取
     * @return
     */
    public Map skinShare() {
        try {
        	String key="skin_share";
            String value=entityService.getConfig(key);
            Map map = new HashMap();
            JSONObject  jasonObject = JSONObject.fromObject(value);
            map = (Map)jasonObject;
            return map; 
        } catch (Exception e) {
            logger.error("method:skinShare arg:{"+ "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    
    /**
     * v3.1
     * 肤质分享
     * @return
     */
    public Map shareUnique() {   
        try {
        	List<ShareEntity> share=shareEntityMapper.shareList();
        	Map map=new HashMap();
        	
        	for(ShareEntity shareEntity:share){
        		Map entityMap=new HashMap();
        		
        		if(shareEntity.getType()==1){
        			//好友
        			entityMap.put("title", shareEntity.getTitle());
    				entityMap.put("content", shareEntity.getContent());
    				map.put(this.hy+"_"+shareEntity.getEntity(), entityMap);
        		}
        		if(shareEntity.getType()==2){
        			//朋友圈
        			entityMap.put("title", shareEntity.getTitle());
    				map.put(this.pyq+"_"+shareEntity.getEntity(), entityMap);
        		}
        		if(shareEntity.getType()==3){
        			//微博
        			entityMap.put("title", shareEntity.getTitle());
    				map.put(this.wb+"_"+shareEntity.getEntity(), entityMap);
        		}
        	}
            return map; 
        } catch (Exception e) {
            logger.error("method:shareUnique2 arg:{"+ "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
    
    
}
