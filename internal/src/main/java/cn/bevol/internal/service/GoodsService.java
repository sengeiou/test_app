package cn.bevol.internal.service;


import cn.bevol.internal.cache.CacheKey;
import cn.bevol.internal.cache.CacheableTemplate;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.GoodsTag;
import cn.bevol.internal.dao.db.Paged;
import cn.bevol.internal.dao.mapper.*;
import cn.bevol.internal.entity.dto.Doyen;
import cn.bevol.internal.entity.model.*;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import cn.bevol.internal.entity.user.UserInfo;
import cn.bevol.internal.entity.vo.GoodsExplain;
import cn.bevol.internal.service.goodsCalculate.*;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.cache.CACHE_NAME;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *  星级计算
 */
@Service
public class GoodsService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(GoodsService.class);


	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	GoodsOldMapper goodsOldMapper;

	@Autowired
	GoodsExtOldMapper goodsExtOldMapper;


	@Autowired
	CompositionOldMapper compositionOldMapper;

	@Autowired
	DoyenOldMapper doyenOldMapper;

	@Resource
	UserService userService;

	@Autowired
	private BackGoodsService backGoodsService;

	@Resource
	CompositionService compositionService;

 
	@Resource
	EntityService entityService;

	@Resource
	CompositionService compositonService;

	@Resource
    MongoTemplate mongoTemplate;
	@Resource
	CacheService cacheService;

	@Resource
	ConfigOldMapper configOldMapper;

	public static List<String> domesticDataType = null;
	public static List<String> internationalDataType = null;

	/**
	 * 根据产品mid获取产品信息,产品扩展的产品排序方式
	 * 两天缓存
	 * @param mid: 产品mid
	 * @return
	 */
	public Goods  getGoodsByMid(final String mid) {
		return new CacheableTemplate<Goods>(cacheProvider) {
			@Override
			protected Goods getFromRepository() {
				try {
					Goods goods=goodsOldMapper.getByGoodsByMid(mid);

					if(goods!=null) {
						//获取cps
						GoodsExt goodsCps=goodsExtOldMapper.getExtByGoodsId(goods.getId());
						//排序方式
						if(goodsCps!=null) {
							if(goodsCps.getCpsType().equals("def_cps")) {
								goods.setCps(goodsCps.getDefCps());
							} else if(goodsCps.getCpsType().equals("mfj_cps")){
								goods.setCps(goodsCps.getMfjCps());
							} else if(goodsCps.getCpsType().equals("gc_cps")){
								goods.setCps(goodsCps.getGcCps());
							} else if(goodsCps.getCpsType().equals("def_ext_cps")){
								goods.setCps(goodsCps.getDefExtCps());
							}
							goods.setGoodsExt(goodsCps);
						}
					}
					return goods;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getGoodsByMid");
					map.put("mid", mid);
					new LogException(e, map);
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(Goods returnValue) {
				return (returnValue != null);
			}

		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_PRODUCT_MID_PREFIX,mid))
				, true);
	}


	/**
	 * 获取所有的产品大分类功效关系列表
	 * 两天缓存
	 * hq_goods_effect_category_used
	 * @return
	 */
	public List<GoodsEffectUsed>  getAllGoodsEffectUsed() {
		return new CacheableTemplate<List<GoodsEffectUsed>>(cacheProvider) {
			@Override
			protected List<GoodsEffectUsed> getFromRepository() {
				try {
					List<GoodsEffectUsed> geu=goodsOldMapper.getAllGoodsEffectUsed();
					return geu;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getAllGoodsEffectUsed");
					new LogException(e, map);
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<GoodsEffectUsed> returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_GOODS_EFFECT_USED_PREFIX))
				, true);
	}




	/**
	 * 根据产品分类获取相应的大分类对应的功效关系,多个
	 * @param categoryId: 产品分类id
	 * @return
	 */
	public List<GoodsEffectUsed>  getGoodsEffectUsedByCategoryId(long categoryId) {
		//得到hq_goods_effect_category_used对象
		List<GoodsEffectUsed> gefs=getAllGoodsEffectUsed();
		//循环找  category对应hq_goods_effect_category_used的category_ids
		List<GoodsEffectUsed> glist=new ArrayList<GoodsEffectUsed>();
		for(int i=0;i<gefs.size();i++){
			//产品的类型category
			String[] arry=gefs.get(i).getCategoryIds().split(",");
			for(int j=0;j<arry.length;j++){
				//得到hq_goods_effect_category_used的对象(id)
				if(arry[j].equals(categoryId+"")){
					glist.add(gefs.get(i));
				}
			}
		}
		return glist;
	}

	/**
	 *
	 *
	 *  goods的category和含有的composition里的used都
	 *  满足hq_goods_effect_category_used
	 * @param goods
	 * @param cps
	 * @return
	 */

	public List<GoodsEffectUsed>  getEffectDeatilByGoodsId(final Goods goods,final List<Composition> cps) {
		try {
			//1、获取关系 产品与hq_goods_effect_category_used表的关系
			List<GoodsEffectUsed>  geus=getGoodsEffectUsedByCategoryId(goods.getCategory());
			if(geus!=null) {
				//定位成分表字段
				for(GoodsEffectUsed geu:geus) {
					//初始化功效分类,活性成分等
					geu.initEffectCategrys();
					//保湿成分
					for(int i=0;i<cps.size();i++) {
						Composition cp=cps.get(i);
						if(!StringUtils.isBlank(cp.getUsed())) {
							//composition的used字段
							String cpusedIds[]=cp.getUsed().split(",");
							//usedids 和 cpusedIds取交集
							boolean flag=false;
							//hq_composition中的used为hq_used的id
							for(int j=0;!flag&&j<cpusedIds.length;j++) {
								//一个成分和该成分中的一个used
								//用于成分排重
								flag=geu.addCompositon(cp,GoodsEffectUsed.CPS_USED_FEILD, cpusedIds[j]);
							}
							//活性成分的处理
							geu.addCompositon(cp,GoodsEffectUsed.CPS_ACTIVE_FEILD,cp.getActive());
						}
					}
				}

			}
			//是否含有皂基
			//this.soapCps(geus,goods, cps);
			
			return geus;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getEffectDeatilByGoodsId");
			map.put("goodsId", goods.getId());
			map.put("cps.size()", cps.size());
			new LogException(e, map);
		}
		return null;
	}

	/**
	 * 根据产品mid获取产品详情,友情特邀
	 * @return
	 */
	public ReturnData<GoodsExplain>  getGoodsDetail(String mid) {
		try{
			GoodsExplain goodsExplain=new GoodsExplain();
			//获取产品表信息 category cps
			Goods  goods=getGoodsByMid(mid);
			Doyen doyen=doyenOldMapper.getDoyenByGoodsId(goods.getId());
			if(doyen!=null){
				//todo  cache
				ReturnData rd=userService.getUserById(doyen.getUserId());
				if(rd.getRet()==0){
					UserInfo userInfo=(UserInfo)rd.getResult();
					//友情特邀
					doyen.setUserDescz(userInfo.getDescz());
					doyen.setHeadimgurl(userInfo.getHeadimgurl());
					doyen.setNickname(userInfo.getNickname());
					doyen.setSkin(userInfo.getResult());
					doyen.setSkinResults(userInfo.getSkinResults());
					goods.setDoyen(doyen);
				}
			}
			goodsExplain.setGoods(goods);
			//hq_goods  cps字段  成分id    单个成分有缓存
			List<Composition> cps=compositonService.getCompositionByIds(goods.getCps());
			goodsExplain.setComposition(cps);
			//1、获取关系
			//产品功效
			List<GoodsEffectUsed>  geus=getEffectDeatilByGoodsId(goods,cps);
			//1、添加功效,功效分析
			goodsExplain.effectAnalysis(geus);

			//2、添加安全

			//	boolean flag=getGoodsSafter(goodsExplain);
			//   if(!flag) setGoodsSafter(goodsExplain);
			//计算安全星级
			goodsExplain.safterAnalysis();
			//肤质测试
			//	allGoodsSkin(goodsExplain);
			return new ReturnData<GoodsExplain>(goodsExplain);
			//goodsExplain.safterAnalysis();

		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getGoodsDetail");
			map.put("mid", mid);
			new LogException(e, map);
		}
		return ReturnData.ERROR;
	}
	


	/**
	 * 更新安全星级
	 * @param mids: 产品mid,逗号分隔
	 * @return
	 */
	@Deprecated
	public ReturnData  updateGoodsSafter(String mids) {
		try {
			//获取产品表信息 category cps
			if(StringUtils.isBlank(mids)) return ReturnData.ERROR;
			String[] midss=mids.split(",");
			for(int i=0;i<midss.length;i++){
				GoodsExplain goodsExplain=new GoodsExplain();
				//获取产品信息
				Goods  goods=getGoodsByMid(midss[i]);
				goodsExplain.setGoods(goods);
				//获取单个产品的成分
				List<Composition> cps=compositonService.getCompositionByIds(goods.getCps());
				goodsExplain.setComposition(cps);
				//星级处理
				setGoodsSafter(goodsExplain);
			}
			return new ReturnData();
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.updateGoodsSafter");
			map.put("mids", mids);
			new LogException(e, map);
		}
		return ReturnData.ERROR;
	}

	/**
	 * 产品安全处理
	 * @param goodsExplain
	 */
	public void  setGoodsSafter(GoodsExplain goodsExplain) {
		//2、添加安全
		Goods goods=goodsExplain.getGoods();
		//星级计算
		goodsExplain.safterAnalysis();
		//sql语句
		Map<String,Map<String,String>>  gen=goodsExplain.createSafetySql();
		String fileds= gen.get("insert").get("fields");
		String vals= gen.get("insert").get("vals");
		String updateStr= gen.get("update").get("fields");
		//goodsId 是否存在表中  存在跟新 不存在 insert
		List<Map<String,Object>> list=getGoodsSafter(goods.getId().toString());
		if(null!=list && list.size()>0){
			goodsOldMapper.updateSafter(updateStr,goods.getId());
		}else{
			goodsOldMapper.insertTable("hq_goods_safter",fileds,vals);
		}
	}



	public List<Map<String,Object>> getGoodsSafter(String goodsIds){
		List<Map<String,Object>> list=goodsOldMapper.getSafter(goodsIds);
		return list;
	}

	/**
	 * 得到16种肤质信息
	 * 两天缓存
	 * @return
	 */
	public List<Map<String,Object>>  getAllGoodsSkin() {
		return new CacheableTemplate<List<Map<String,Object>>>(cacheProvider) {
			@Override
			protected List<Map<String,Object>> getFromRepository() {
				try {
					List<Map<String,Object>> listMap=goodsOldMapper.getAllGoodsSkin();
					return listMap;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getAllGoodsSkin");
					new LogException(e, map);
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<Map<String,Object>> returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_GOODS_SKIN_PREFIX)), true);
	}


	/**
	 * 产品全部普通分类
	 * 两天缓存
	 * @return
	 */
	public List<Map<String,Object>>  getAllCommonGoodsCategory() {
		return new CacheableTemplate<List<Map<String,Object>>>(cacheProvider) {
			@Override
			protected List<Map<String,Object>> getFromRepository() {
				try {
					List<Map<String,Object>> listMap=goodsOldMapper.getAllCommonGoodsCategory();
					return listMap;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getAllCommonGoodsCategory");
					new LogException(e, map);
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<Map<String,Object>> returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_GOODS_COMMON_CATEGORY_PREFIX)), true);
	}



	/**
	 * moved to internal project
	 * 批量综合计算
	 * @param ids 肤质id
	 * @param update
	 * @return
	 */
	@Deprecated
	public ReturnListData goodsCalculate(UserInfo userInfo, String ids, int update) {
		try{
			String skin=null;
			if(userInfo!=null)
				skin=userInfo.getResult();
			String [] idss=ids.split(",");
			int num=idss.length;
			List gclist=new ArrayList();
			Date start=new Date();
			int n=0;
			StringBuffer skinBuffer=new StringBuffer();
			StringBuffer tagBuffer=new StringBuffer();
			StringBuffer safetyBuffer=new StringBuffer();
			StringBuffer polyBuffer=new StringBuffer();
			StringBuffer polysBuffer=new StringBuffer();
			StringBuffer searchBuffer=new StringBuffer();
			//用于拼接sql语句
			String skinSql="insert into hq_goods_skin2(goods_id,mid,skin_match_OSPW_num,skin_notmatch_OSPW_num,skin_match_OSPW_isexist,skin_notmatch_OSPW_isexist,skin_match_OSPW_cps,skin_notmatch_OSPW_cps,skin_match_OSPT_num,skin_notmatch_OSPT_num,skin_match_OSPT_isexist,skin_notmatch_OSPT_isexist,skin_match_OSPT_cps,skin_notmatch_OSPT_cps,skin_match_OSNW_num,skin_notmatch_OSNW_num,skin_match_OSNW_isexist,skin_notmatch_OSNW_isexist,skin_match_OSNW_cps,skin_notmatch_OSNW_cps,skin_match_OSNT_num,skin_notmatch_OSNT_num,skin_match_OSNT_isexist,skin_notmatch_OSNT_isexist,skin_match_OSNT_cps,skin_notmatch_OSNT_cps,skin_match_ORPW_num,skin_notmatch_ORPW_num,skin_match_ORPW_isexist,skin_notmatch_ORPW_isexist,skin_match_ORPW_cps,skin_notmatch_ORPW_cps,skin_match_ORPT_num,skin_notmatch_ORPT_num,skin_match_ORPT_isexist,skin_notmatch_ORPT_isexist,skin_match_ORPT_cps,skin_notmatch_ORPT_cps,skin_match_ORNW_num,skin_notmatch_ORNW_num,skin_match_ORNW_isexist,skin_notmatch_ORNW_isexist,skin_match_ORNW_cps,skin_notmatch_ORNW_cps,skin_match_ORNT_num,skin_notmatch_ORNT_num,skin_match_ORNT_isexist,skin_notmatch_ORNT_isexist,skin_match_ORNT_cps,skin_notmatch_ORNT_cps,skin_match_DSPW_num,skin_notmatch_DSPW_num,skin_match_DSPW_isexist,skin_notmatch_DSPW_isexist,skin_match_DSPW_cps,skin_notmatch_DSPW_cps,skin_match_DSPT_num,skin_notmatch_DSPT_num,skin_match_DSPT_isexist,skin_notmatch_DSPT_isexist,skin_match_DSPT_cps,skin_notmatch_DSPT_cps,skin_match_DSNW_num,skin_notmatch_DSNW_num,skin_match_DSNW_isexist,skin_notmatch_DSNW_isexist,skin_match_DSNW_cps,skin_notmatch_DSNW_cps,skin_match_DSNT_num,skin_notmatch_DSNT_num,skin_match_DSNT_isexist,skin_notmatch_DSNT_isexist,skin_match_DSNT_cps,skin_notmatch_DSNT_cps,skin_match_DRPW_num,skin_notmatch_DRPW_num,skin_match_DRPW_isexist,skin_notmatch_DRPW_isexist,skin_match_DRPW_cps,skin_notmatch_DRPW_cps,skin_match_DRPT_num,skin_notmatch_DRPT_num,skin_match_DRPT_isexist,skin_notmatch_DRPT_isexist,skin_match_DRPT_cps,skin_notmatch_DRPT_cps,skin_match_DRNW_num,skin_notmatch_DRNW_num,skin_match_DRNW_isexist,skin_notmatch_DRNW_isexist,skin_match_DRNW_cps,skin_notmatch_DRNW_cps,skin_match_DRNT_num,skin_notmatch_DRNT_num,skin_match_DRNT_isexist,skin_notmatch_DRNT_isexist,skin_match_DRNT_cps,skin_notmatch_DRNT_cps,update_time) ";
			String safetySql="insert into hq_goods_safter2(goods_id,safety_1_name,safety_1_unit,safety_1_num,safety_1_cps,safety_2_name,safety_2_unit,safety_2_num,safety_2_cps,safety_3_name,safety_3_unit,safety_3_num,safety_3_cps,safety_4_name,safety_4_unit,safety_4_num,safety_4_cps,safety_5_name,safety_5_unit,safety_5_num,safety_5_cps,update_time) ";
			String polysKey="insert into hq_goods_poly_category2(goods_id,category_id,category_ids,exist_category_ids,update_time) ";
			String polyKey="insert into hq_goods_poly_category2(goods_id,category_id,exist_category_ids,update_time) ";
			String tagSql="insert into hq_goods_tag_result2(goods_id,auto_tag_ids,auto_tag_names,tag_ids,tag_names,create_stamp,update_time) ";
			String searchSql="insert into hq_goods_search2(goods_id,cps,cps_search,category,safety_1_num,tag_ids,update_time)";
			for(int i=0;i<idss.length;i++) {
				Long id=Long.parseLong(idss[i]);
				//查询扩展表
				GoodsExplain goodsExplain=this.getGoodsExplainById(id);
				goodsExplain.setUserSkin(skin);
				List<GoodsCalculateI> gci=getGoodsCalculates(goodsExplain);
				for(int j=0;j<gci.size();j++) {
					if(null!=gci.get(j)){
						gci.get(j).handler();
						//update=1正常流程 一个产品id一次操作
						if(update==1) {
							goodsProcessCalculate(gci.get(j),update);
							//update=3 批量操作 多个产品一起插入(拼接字符串)
						} else if(update==3) {// 旧逻辑未使用
						}
					}

				}
				n++;
				gclist.add(goodsExplain);



				//清空缓存
				if(goodsExplain.getGoods()!=null) {
					String mid=goodsExplain.getGoods().getMid();
					//清空缓存
					cacheService.cleanProducts(mid);
					cacheService.cleanProducts(id+"");
				}
			}

			long total=gclist.size();
			Date end=new Date();
			return new ReturnListData(gclist,total);
		}catch(Exception e){
			Map map = new HashMap();
			map.put("method", "GoodsService.goodsCalculate");
			map.put("update", update);
			map.put("ids", ids);
			new LogException(e, map);
		}
		return ReturnListData.ERROR;
	}

	/**
	 * moved to internal project
	 * 产品计算,单个
	 * 根据type计算相应的业务
	 * @param ids: 产品id
	 * @param update: 1强制更新
	 * @return
	 */
	@Deprecated
	public ReturnListData goodsCalculate(String ids, int update,String type,UserInfo userInfo) {
		// TODO Auto-generated method stub
		try{
			String [] idss=ids.split(",");
			List gclist=new ArrayList();
			for(int i=0;i<idss.length;i++) {
				Long id=Long.parseLong(idss[i]);
				//查询扩展表  排序规则
				GoodsExplain goodsExplain=this.getGoodsExplainById(id);
				if(null!=userInfo && !StringUtils.isBlank(userInfo.getResult())){
					goodsExplain.setUserSkin(userInfo.getResult());
				}
				//根据业务类型计算
				GoodsCalculateI gci=getGoodsCalculate(goodsExplain,type);
				//数据操作
				gci=goodsProcessCalculate(gci,update);
				gclist.add(goodsExplain);
			}
			long total=gclist.size();
			return new ReturnListData(gclist,total);
		}catch(Exception e){
			Map map = new HashMap();
			map.put("method", "GoodsService.goodsCalculate");
			map.put("update", update);
			map.put("ids", ids);
			new LogException(e, map);
		}
		return ReturnListData.ERROR;

	}
	/**
	 * 产品运算处理
	 * 表数据处理
	 * @param gci 被处理对象
	 * @param update 是否更新
	 */
	private GoodsCalculateI goodsProcessCalculate(GoodsCalculateI gci,int update) {
		try{
			String selectSql=gci.selectSql();
			if(gci instanceof GoodsCategoryCalculateHandler){
				GoodsCategoryCalculateHandler gcch=(GoodsCategoryCalculateHandler)gci;
				//产品存在多个分类
				if(!StringUtils.isBlank(gcch.getSelectSql2())){
					//hq_goods_poly_category
					List<Map<String,Object>> listMap=goodsOldMapper.select(gcch.getSelectSql2());
					//不存在
					if(listMap.size()==0 || null==listMap){
						goodsOldMapper.insert(gcch.getInsertSql2());
					}else{
						goodsOldMapper.update(gcch.getUpdateSql2());
					}
				}
			}
			if(!StringUtils.isBlank(selectSql)) {
				Long goodsId=0L;
				String safetyNum="";
				try{
					//处理产品信息
					if(gci instanceof GoodsSafetyCalculateHandler){
						Map map=gci.entityInfo();
						goodsId=(Long)map.get("goodsId");
						safetyNum=(String)map.get("xinji");
					}
				}catch (Exception e){
					Map map = new HashMap();
					map.put("method", "GoodsService.goodsProcessCalculate");
					map.put("update", update);
					map.put("goodsId", goodsId);
					new LogException(e, map);
				}


				List<Map<String,Object>> m=goodsOldMapper.select(gci.selectSql());
				Criteria cr= Criteria.where("id").is(goodsId.intValue());
				Update up=new Update();
				up.set("safety_1_num", safetyNum);

				//update=1 强行跟新
				if(null==m || m.size()==0) {
					if(!StringUtils.isBlank(gci.insertSql())){
						goodsOldMapper.insert(gci.insertSql());
						//更新mongo的安全星级
						mongoTemplate.updateFirst(new Query(cr), up, EntityGoods.class,"entity_goods");
					}
				} else if(update==1) {
					if(!StringUtils.isBlank(gci.updaeSql())){
						goodsOldMapper.update(gci.updaeSql());
						mongoTemplate.updateFirst(new Query(cr), up, EntityGoods.class,"entity_goods");
					}
				}
				//作为最终的显示
				m=goodsOldMapper.select(gci.selectSql());
				if(m!=null) {
					gci.display(m);
				}

			}
			return gci;
		}catch(Exception e){
			Map map = new HashMap();
			map.put("method", "GoodsService.goodsProcessCalculate");
			map.put("update", update);
			new LogException(e, map);
		}
		return null;
	}


	/**
	 * 获取产品成分排序顺序
	 * @return
	 */
	public  Goods  getGoodsById(final long id) {
		Goods goods=goodsOldMapper.getById(id);
		//获取cps
		GoodsExt goodsCps=goodsExtOldMapper.getExtByGoodsId(id);
		//排序方式
		if(goodsCps!=null) {
			if(goodsCps.getCpsType().equals("def_cps")) {
				goods.setCps(goodsCps.getDefCps());
			} else if(goodsCps.getCpsType().equals("mfj_cps")){
				goods.setCps(goodsCps.getMfjCps());
			} else if(goodsCps.getCpsType().equals("gc_cps")){
				goods.setCps(goodsCps.getGcCps());
			} else if(goodsCps.getCpsType().equals("def_ext_cps")){
				goods.setCps(goodsCps.getDefExtCps());
			}
		}
		return goods;
	}

	/**
	 *  获取explain信息
	 * @param id
	 * @return
	 */
	public GoodsExplain getGoodsExplainById(Long id) {
		Goods goods=getGoodsById(id);
		GoodsExplain goodsExplain=new GoodsExplain();
		//重新排序
		if(null!=goods && goods.getId()!=null){
			List<Composition> cps=getCompositionByIds(goods.getCps());
			goods.setCompositions(cps);
			goodsExplain.setGoods(goods);
			goodsExplain.setComposition(goods.getCompositions());
		}
		return goodsExplain;
	}


	/**
	 * all
	 * @param goodsExplain
	 * @return
	 */
	private List<GoodsCalculateI> getGoodsCalculates(GoodsExplain goodsExplain) {
		List<GoodsCalculateI> gcis=new ArrayList<GoodsCalculateI>();
		//肤质计算
		gcis.add(getGoodsCalculate(goodsExplain,"category"));
		//gcis.add(getGoodsCalculate(goodsExplain,"effect"));
		gcis.add(getGoodsCalculate(goodsExplain,"safety"));
		gcis.add(getGoodsCalculate(goodsExplain,"tag"));
		//gcis.add(getGoodsCalculate(goodsExplain,"skin"));
		gcis.add(getGoodsCalculate(goodsExplain,"search"));
		return gcis;
	}


	public static final boolean  islocalcache=false;

	/**
	 *  根据type进行相关产品的业务计算 sql的更改
	 * @param goodsExplain
	 * @return
	 */
	private GoodsCalculateI getGoodsCalculate(GoodsExplain goodsExplain,String type) {
		if(null!=goodsExplain.getGoods() && null!=goodsExplain.getGoods().getId()){
			long goodsId=goodsExplain.getGoods().getId();
			if(islocalcache){
				if(type.equals("skin")) {
					//计算肤质
					return new GoodsSkinCalculateHandler(goodsExplain, allGoodsSkin);
				} else if(type.equals("category")) {
					//产品分类
					return new GoodsCategoryCalculateHandler(goodsExplain,allCommonGoodsCategory,allSpecialCategory,allEnglishCategory,getPolyCategoryBygoodsIdsLocal(goodsId+""));
				} else if(type.equals("effect")) {
					//产品功效
					List<GoodsEffectUsed>  geus= getEffectDeatilByGoodsId(goodsExplain.getGoods(),goodsExplain.getComposition());
					return new GoodsEffectCalculateHandler(goodsExplain,geus);
				} else if(type.equals("safety")) {
					//安全星级
					return new GoodsSafetyCalculateHandler(goodsExplain);
				} else if(type.equals("tag")) {
					//标签
					return new GoodsTagResultCalculateHandler(goodsExplain,allGoodsTag,getAllTagComposition(),goodsRules,getTagResultByGoodsIdLocal(goodsId));
				} else if(type.equals("search")){
					//搜索
					return new GoodsSearchCalculateHandler(goodsExplain,allOutComposition,allGoodsCategory);
				}
			}else{
				if(type.equals("skin")) {
					//计算肤质
					return new GoodsSkinCalculateHandler(goodsExplain, getAllGoodsSkin());
				} else if(type.equals("category")) {
					//产品分类
					return new GoodsCategoryCalculateHandler(goodsExplain,getAllCommonGoodsCategory(), getAllSpecialCategory(),getAllEnglishCategory(),getPolyCategoryBygoodsIdsLocal(goodsId+""));
				} else if(type.equals("effect")) {
					//产品功效
					List<GoodsEffectUsed>  geus=getEffectDeatilByGoodsId(goodsExplain.getGoods(),goodsExplain.getComposition());
					return new GoodsEffectCalculateHandler(goodsExplain,geus);
				} else if(type.equals("safety")) {
					//安全星级
					return new GoodsSafetyCalculateHandler(goodsExplain);
				} else if(type.equals("tag")) {
					//标签
					return new GoodsTagResultCalculateHandler(goodsExplain,getAllGoodsTag(),getAllTagComposition(),getAllGoodsRule(),getTagResultByGoodsIdRedis(goodsId));
				} else if(type.equals("search")){
					//搜索
					return new GoodsSearchCalculateHandler(goodsExplain,getOutComposition(),getAllGoodsCategory2());
				}
			}
		}
		return null;
	}


	/**
	 * 获取分类
	 * @param goodsIds
	 * @return
	 */
	public List<Map<String,Object>>  getPolyCategoryBygoodsIdsLocal(String goodsIds) {
		try {
			List<Map<String,Object>> geu=goodsOldMapper.getPolyCategoryBygoodsIds(goodsIds);
			return geu;
		} catch (Exception e) {
			logger.error("method:getPolyCategoryBygoodsIdsLocal arg:{ goodsIds:"+goodsIds  + "   desc:" +  ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	/**
	 * 获取产品标签
	 * @return
	 */
	public List<GoodsTag> getAllGoodsTag(){
		return new CacheableTemplate<List<GoodsTag>>(cacheProvider) {
			@Override
			protected List<GoodsTag> getFromRepository() {
				try {
					List<GoodsTag> gtList=backGoodsService.getAllTag();
					return gtList;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getAllGoodsTag");
					new LogException(e, map);
				}
				return new ArrayList();
			}
			@Override
			protected boolean canPutToCache(List<GoodsTag> returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				"GoodsService.getAllGoodsTag_"+CACHE_NAME.VERSION ), true);//todo  key注册
	}

	/**
	 * 根据产品id获取数据库中标签计算的结果
	 * 带缓存--后来取消
	 * @param goodsId: 产品id
	 * @return
	 */
	public GoodsTagResult getTagResultByGoodsIdRedis(final Long goodsId){
		try {
			GoodsTagResult gtr=new GoodsTagResult();
			gtr=goodsOldMapper.getTagResultByGoodsId(goodsId);
			return gtr;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getTagResultByGoodsIdRedis");
			map.put("goodsId", goodsId);
			new LogException(e, map);
		}
		return null;
	}

	/**
	 * 根据产品id获取数据库中标签计算的结果
	 * 不带缓存
	 * @param goodsId: 产品id
	 * @return
	 */
	public GoodsTagResult getTagResultByGoodsIdLocal(Long goodsId){
		try {
			GoodsTagResult gtr=new GoodsTagResult();
			gtr=goodsOldMapper.getTagResultByGoodsId(goodsId);
			return gtr;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getTagResultByGoodsIdRedis");
			map.put("goodsId", goodsId);
			new LogException(e, map);
		}
		return null;
	}


	/**
	 * 获取产品特殊分类
	 * 两天缓存
	 * @return
	 */
	public List<Map<String,Object>> getAllSpecialCategory(){
		return new CacheableTemplate<List<Map<String,Object>>>(cacheProvider) {
			@Override
			protected List<Map<String,Object>> getFromRepository() {
				try {
					List<Map<String,Object>> listMap=goodsOldMapper.getAllSpecialCategory();
					return listMap;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getAllSpecialCategory");
					new LogException(e, map);
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<Map<String,Object>> returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_GOODS_SPECIAL_CATEGORY_PREFIX)), true);

	}

	/**
	 * 获取产品的英文分类规则
	 * @return
	 */
	public List<Map<String,Object>> getAllEnglishCategory(){
		return new CacheableTemplate<List<Map<String,Object>>>(cacheProvider) {
			@Override
			protected List<Map<String,Object>> getFromRepository() {
				try {
					List<Map<String,Object>> listMap=goodsOldMapper.getAllEnglishCategory();
					return listMap;
				} catch (Exception e) {
					logger.error("method:getAllEnglishCategory arg:{" + "   desc:" +  ExceptionUtils.getStackTrace(e));
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<Map<String,Object>> returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				"GoodsService.getAllEnglishCategory"+CACHE_NAME.VERSION), true);//todo

	}

	/**
	 * 获取产品的所有标签规则
	 * 两天缓存
	 * @return
	 */
	public List<GoodsRule> getAllGoodsRule(){
		return new CacheableTemplate<List<GoodsRule>>(cacheProvider) {
			@Override
			protected List<GoodsRule> getFromRepository() {
				try {
					List<GoodsRule> gtList=goodsOldMapper.getAllRule();
					return gtList;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getAllGoodsRule");
					new LogException(e, map);
				}
				return new ArrayList();
			}
			@Override
			protected boolean canPutToCache(List<GoodsRule> returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_GOODS_RULE_PREFIX)), true);
	}

	/**
	 * 获取该产品的所有成分列表,pid的处理
	 * 根据islocalcache,分为本地缓存和reids缓存
	 * @param ids: 成分id,逗号分隔
	 * @return
	 */
	public List<Composition>  getCompositionByIds(String ids) {
		try {
			//根据成分id 获取成分列表
			List<Composition> list=new ArrayList<Composition>();
			if(islocalcache){
				String[] idss=ids.split(",");
				for(int j=0;j<idss.length;j++){
					//在所有產品中查找
					Long id=Long.parseLong(idss[j]);
					Composition composition=allcps.get(id);
					if(composition!=null&&composition.getId()>0) {
						if(composition.getPid()>0){
							Composition pComposition=allcps.get(id);
							long pid=pComposition.getPid();
							long curid=composition.getId();
							pComposition.setPid(pid);
							pComposition.setId(curid);
							pComposition.setTitle(composition.getTitle());
							composition=pComposition;
						}
						list.add(composition);
					}
				}
			}else{
				//读缓存
				List<Composition> cpsList=compositionService.getAllComposition();
				String[] idss=ids.split(",");
				for(int j=0;j<idss.length;j++){
					//在所有產品中查找
					Long id=Long.parseLong(idss[j]);
					Composition composition=null;
					boolean isFind=false;
					for(int i=0;!isFind && i<cpsList.size();i++){
						if(idss[j].equals(cpsList.get(i).getId()+"")){
							if(!StringUtils.isBlank(cpsList.get(i).getUsed())) {
								//使用目的处理
								List<Used> useds=compositionOldMapper.getUsedsByUid(cpsList.get(i).getUsed());
								cpsList.get(i).setUseds(useds);
								isFind=true;
							}
							composition=cpsList.get(i);
						}
					}
					//Composition composition=allcps.get(id);
					//成分pid处理
					if(composition!=null&&composition.getId()>0) {
						if(composition.getPid()>0){
							//Composition pComposition=allcps.get(id);
							Composition pComposition=composition;
							long pid=pComposition.getPid();
							long curid=composition.getId();
							pComposition.setPid(pid);
							pComposition.setId(curid);
							pComposition.setTitle(composition.getTitle());
							composition=pComposition;
						}
						list.add(composition);
					}
				}
			}
			return list;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getCompositionByIds");
			map.put("ids", ids);
			new LogException(e, map);
		}
		return null;
	}


	static Map<Integer,List<Composition>> allOutComposition=null;
	static List<Map<String,Object>> allGoodsSkin=null;
	static List<Map<String,Object>> allCommonGoodsCategory=null;
	static List<Map<String,Object>> allSpecialCategory=null;
	private static List<Map<String,Object>> allEnglishCategory=null;
	static List<GoodsTag> allGoodsTag=null;
	static List<GoodsRule> goodsRules=null;
	static List<GoodsTagComposition> tagsByIsMain=null;
	static List<Map<String,Object>> allGoodsCategory=null;

	static Map<Long,Composition>  allcps=new HashMap<Long,Composition>();

	static Map<Long,GoodsTagComposition>  allTagComposition=new HashMap<Long,GoodsTagComposition>();




	/**
	 * 获取所有标签核心成分
	 * @return
	 */
	public List<GoodsTagComposition>  getAllTagComposition() {
		if(islocalcache){
			return tagsByIsMain;
		}else{
			return getAllTagCompositionRedis();
		}

	}

	/**
	 * 得到所有产品标签核心成分
	 * 两天缓存
	 * @return
	 */
	public List<GoodsTagComposition>  getAllTagCompositionRedis() {
		return new CacheableTemplate<List<GoodsTagComposition>>(cacheProvider) {
			@Override
			protected List<GoodsTagComposition> getFromRepository() {
				try {
					List<GoodsTagComposition> allTags=goodsOldMapper.getAllTagComposition();
					return allTags;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getAllTagCompositionRedis");
					new LogException(e, map);
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<GoodsTagComposition> returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_GOODS_TAG_CPS_PREFIX)), true);
	}


	/**
	 * 缓存预热使用  产品分页查询mid
	 * @param goodsCondition
	 * @return
	 */
	public List findGoodsMidByPage(Paged goodsCondition) {
		return goodsOldMapper.findGoodsMidByPage(goodsCondition);
	}

	/**
	 * 缓存预热使用 查询可用产品总数
	 * @return
	 */
	public int selectTotal() {
		return goodsOldMapper.selectTotal();
	}

	/**
	 * 更新搜索表的pCategory字段,一次性接口
	 * @return
	 */
	@Deprecated
	public ReturnData goodsSearchPcategory() {
		try{
			goodsOldMapper.updatePcategory();
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.goodsSearchPcategory");
			new LogException(e, map);
			return ReturnData.ERROR;
		}
	}


    /**
     * 带缓存
     * 得到产品分类表
     * @return
     */
    public List<Map<String,Object>> getAllGoodsCategory2() {
        return new  CacheableTemplate<List<Map<String,Object>>>(cacheProvider) {
            @Override
            protected List<Map<String,Object>> getFromRepository() {
                // TODO Auto-generated method stub
                try{
                    List<Map<String,Object>> list=goodsOldMapper.getAllGoodsCategory();
                    return list;
                }catch (Exception e) {
                    logger.error("method:GoodsService.getAllGoodsCategory2 arg:{" + "   desc:" +  ExceptionUtils.getStackTrace(e));
                    return null;
                }
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
                "GoodsService.getAllGoodsCategory2_"+CACHE_NAME.VERSION), true);
    }

    /**
     * 获取国内国外库号
     */
    public void setDomesticInternationalDataType(){
        if(null == configOldMapper){
            logger.error("================");
            logger.error("map is null");
            logger.error("================");
        }
        Config dataTypeInfo = configOldMapper.getConfigByKey("product_category_data_type");
        JSONObject dataTypeObject = JSONObject.fromObject(dataTypeInfo.getValue());
//        JSONObject dataTypeObject = JSONObject.fromObject("{}");
        //国内
        if(dataTypeObject.has("domestic")){
            String domestic = dataTypeObject.getString("domestic");
            domesticDataType = Arrays.asList(domestic.split(","));
        }else{
            //设置默认值
            String[] domesticDefault = {"1", "2", "3", "4"};
            domesticDataType = Arrays.asList(domesticDefault);
        }

        //国外
        if(dataTypeObject.has("international")){
            String international = dataTypeObject.getString("international");
            internationalDataType = Arrays.asList(international.split(","));
        }else{
            //设置默认值
            String[] internationalDefault = {"6"};
            internationalDataType = Arrays.asList(internationalDefault);
        }
    }

	/**
	 * 不想要的成分组
	 * 不带缓存
	 * @return
	 */
	public Map<Integer,List<Composition>> getOutComposition2(){
		try {
			List<Map<String,Object>> allTags=goodsOldMapper.getAllOutComposition();
			Map<Integer,List<Composition>> m=new HashMap<Integer,List<Composition>>();
			for(Map<String,Object> map:allTags){
				String cpsIdsStr=(String)map.get("composition_ids");
				//pid替换id
				List<Composition> cpsList=getCompositionByIds(cpsIdsStr);
				int outId=(Integer)map.get("id");
				m.put(outId, cpsList);
			}
			return m;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getOutComposition2");
			new LogException(e, map);
		}
		return null;
	}

	/**
	 * 不想要的成分组
	 * 两天缓存
	 * @return
	 */
	public Map<Integer,List<Composition>> getOutComposition(){
		return new CacheableTemplate<Map<Integer,List<Composition>>>(cacheProvider) {
			@Override
			protected Map<Integer,List<Composition>> getFromRepository() {
				try {
					//不想要的成分组
					List<Map<String,Object>> allTags=goodsOldMapper.getAllOutComposition();
					Map<Integer,List<Composition>> m=new HashMap<Integer,List<Composition>>();
					for(Map<String,Object> map:allTags){
						String cpsIdsStr=(String)map.get("composition_ids");
						//pid替换id
						List<Composition> cpsList=getCompositionByIds(cpsIdsStr);
						int outId=(Integer)map.get("id");
						m.put(outId, cpsList);
					}
					return m;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getOutComposition");
					new LogException(e, map);
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(Map<Integer,List<Composition>> returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_GOODS_RULEOUT_CPS_PREFIX)), true);
	}

}