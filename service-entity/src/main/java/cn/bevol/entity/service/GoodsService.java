package cn.bevol.entity.service;

import java.util.*;

import javax.annotation.Resource;

import cn.bevol.conf.client.ConfUtils;
import cn.bevol.mybatis.dao.*;
import cn.bevol.mybatis.model.*;
import cn.bevol.entity.service.goodsCalculate.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;
import com.io97.utils.db.Paged;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.vo.CompareGoods;
import cn.bevol.model.vo.Explain;
import cn.bevol.model.vo.GoodsExplain;
import cn.bevol.mybatis.dto.Doyen;
import cn.bevol.log.LogException;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import flexjson.JSONSerializer;

/**
 *  星级计算
 */
@Service
public class GoodsService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(GoodsService.class);


	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	GoodsMapper goodsMapper;

	@Autowired
	GoodsExtMapper goodsExtMapper;


	@Autowired
	CompositionMapper compositionMapper;

	@Autowired
	DoyenMapper doyenMapper;

	@Resource
	UserService userService;

	@Resource
	CompositionService compositionService;

 
	@Resource
	EntityService entityService;

	@Resource
	AliyunService aliyunService;

	@Resource
	CompositionService compositonService;

	@Resource
	MongoTemplate mongoTemplate;
	@Resource
	CacheService cacheService;

	@Resource
	ConfigMapper configMapper;

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
					Goods goods=goodsMapper.getByGoodsByMid(mid);

					if(goods!=null) {
						//获取cps
						GoodsExt goodsCps=goodsExtMapper.getExtByGoodsId(goods.getId());
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
	 * 获取产品使用目的功效关系表
	 * 两天缓存
	 * hq_goods_used_effect
	 * @return
	 */
	public List<GoodsUsedEffect>  getAllGoodsUsedEffect() {
		return new CacheableTemplate<List<GoodsUsedEffect>>(cacheProvider) {
			@Override
			protected List<GoodsUsedEffect> getFromRepository() {
				try {
					List<GoodsUsedEffect> geu=goodsMapper.getAllGoodsUsedEffect();
					return geu;
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getAllGoodsUsedEffect");
					new LogException(e, map);
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<GoodsUsedEffect> returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_GOODS_USED_EFFECT_PREFIX)), true);
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
					List<GoodsEffectUsed> geu=goodsMapper.getAllGoodsEffectUsed();
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
	 * 是否含有皂基成分
	 * @param geus
	 * @param goods
	 * @param cps
	 */
	public void soapCps(List<GoodsEffectUsed> geus,Goods goods,List<Composition> cps){
		//1.产品是洁面、沐浴、洗发中的一种
		if(goods.getCategory()==6 || goods.getCategory()==29 || goods.getCategory()==38){
			//2.cps含皂基的成分
			List<Composition> soapCpsList=compositonService.soapCps();
			boolean soap=false;
			GoodsEffectUsed geu=new GoodsEffectUsed();
			geu.setCategoryCateId(0);
			geu.setDisplayType(1);
			geu.setId(0);
			List<Long> cpdsIds= new ArrayList<Long>();
			List<Composition> comps= new ArrayList<Composition>();
			Map<Long,Long> uqcps=new HashMap<Long,Long>();
			for(int i=0;i<soapCpsList.size();i++){
				for(Composition c:cps){
					if(soapCpsList.get(i).getId()==c.getId().longValue()){
						cpdsIds.add(c.getId());
						comps.add(c);
						uqcps.put(c.getId(), c.getId());
					}
				}
			}
			//功效解读
			geu.setDesc("soap");
			//设置为清洁类产品
			geu.setCategoryCateId(2);
			geu.setCompositionIds(cpdsIds);
			geu.setCompositions(comps);
			if(comps.size()>0){
				geu.setDisplayName("含有皂基成分");
				geus.add(geu);
			}
		}
	}
	

	/**
	 * 根据产品mid获取产品详情,友情特邀
	 * @param goodsId
	 * @return
	 */
	public ReturnData<GoodsExplain>  getGoodsDetail(String mid) {
		try{
			GoodsExplain goodsExplain=new GoodsExplain();
			//获取产品表信息 category cps
			Goods  goods=getGoodsByMid(mid);
			Doyen doyen=doyenMapper.getDoyenByGoodsId(goods.getId());
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
	 * 产品静态化
	 * @param mid
	 */
	public ReturnData  goodsStatic(String mids) {
		Map<String,String> mps= ConfUtils.getMap("oss_upload_dir","goods_info");
		if(StringUtils.isBlank(mids)) return new ReturnData<String>("没有传mid",-7);
		String midss[]=mids.split(",");
		List<Map<String,String>> lms=new ArrayList<Map<String,String>>();
		for(int i=0;i<midss.length;i++) {
			String mid=midss[i];
			ReturnData<GoodsExplain>  ge=getGoodsDetail( mid);
			String  json=new JSONSerializer().exclude("*.class").deepSerialize(ge);
			String path=mps.get("dir")+mid+".json";
			aliyunService.upOss(mps.get("oss_name"), path, json);
			Map<String,String> m=new HashMap<String,String>();
			m.put("mid", mid);
			if(ge.getRet()!=0) {
				m.put("ret", "-2");
				m.put("ret", "静态化失败");
			} else {
				m.put("url", mps.get("domain")+path);
				m.put("ret", "0");
			}
			lms.add(m);
		}
		return new ReturnData(lms);
	}


	/**
	 * 根据产品mid获取产品详情
	 * 两天的缓存
	 * @param mid
	 * @param userInfo
	 * @return
	 */
	public ReturnData<GoodsExplain> getGoodsExplain(final String mid, final UserInfo userInfo) {

		ReturnData rd = new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				try {
					return getGoodsDetail(mid);
				} catch (Exception e) {
					Map map = new HashMap();
					map.put("method", "GoodsService.getGoodsExplain");
					map.put("mid", mid);
					new LogException(e, map);
				}
				return ReturnData.ERROR;
			}

			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null &&
						returnValue.getRet() == 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_PRODUCT_ID_OR_MID_PREFIX , mid)), true);
		if (rd.getRet() == 0) {
			String result = null;
			try {
				GoodsExplain goodsExplain = (GoodsExplain) rd.getResult();
				//3、适合我的肤质
				if (userInfo != null) {
					result = userInfo.getResult();
					if (!StringUtils.isBlank(result))
						goodsExplain.skinAnalysis(result);
				}

				//skin_match_{own}_cps
				//skin_notmatch_{own}_num
				//skin_ismatch_{own} 是否匹配
			} catch (Exception e) {
				Map map = new HashMap();
				map.put("method", "GoodsService.getGoodsExplain");
				map.put("mid", mid);
				map.put("userId", userInfo.getId());
				new LogException(e, map);
			}
		}
		return rd;
	}


	/**
	 * 多个产品对比
	 * @param mids: 产品mid,逗号分隔
	 * @return
	 */
	public ReturnData getCompare(String mids,UserInfo userInfo) {

		try {
			if(StringUtils.isBlank(mids)) return ReturnData.ERROR;
			String midss[]=mids.split(",");
			List<GoodsExplain> ges=new ArrayList<GoodsExplain>();
			for(int i=0;i<midss.length;i++) {
				//获取产品详细
				ReturnData<GoodsExplain> ge=this.getGoodsExplain(midss[i], userInfo);
				GoodsExplain g=ge.TResult();
				ges.add(g);
			}
			//处理需要显示的功效排序
			List<GoodsUsedEffect>  cgefs=new ArrayList<GoodsUsedEffect>();
			List<GoodsUsedEffect>  gefs=getAllGoodsUsedEffect();
			for(GoodsUsedEffect gef:gefs) {
				//需要排序的
				if(gef.getDisplayCompare()==1) {
					cgefs.add(gef);
				}
			}
			Collections.sort(cgefs);
			//产品对比分析
			return new ReturnData(CompareGoods.compareAnalysis(ges,cgefs,userInfo));
		} catch(Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getCompare");
			map.put("mids", mids);
			map.put("userId", userInfo.getId());
			new LogException(e, map);
		}
		return ReturnData.ERROR;
	}

	/**
	 * 多个产品对比
	 * @param mids: 产品mid,逗号分隔
	 * @return
	 */
	public ReturnData getCompare2(List<GoodsExplain> ges,UserInfo userInfo) {

		try {
			//处理需要显示的功效排序
			List<GoodsUsedEffect>  cgefs=new ArrayList<GoodsUsedEffect>();
			List<GoodsUsedEffect>  gefs=getAllGoodsUsedEffect();
			for(GoodsUsedEffect gef:gefs) {
				//需要排序的
				if(gef.getDisplayCompare()==1) {
					cgefs.add(gef);
				}
			}
			Collections.sort(cgefs);
			//产品对比分析
			return new ReturnData(CompareGoods.compareAnalysis2(ges,cgefs,userInfo));
		} catch(Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getCompare2");
			map.put("userId", userInfo.getId());
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
			goodsMapper.updateSafter(updateStr,goods.getId());
		}else{
			goodsMapper.insertTable("hq_goods_safter",fileds,vals);
		}
	}

	/**
	 * 计算或者更新安全星级信息
	 * @param goodsExplain
	 */
	public boolean  getGoodsSafter(GoodsExplain goodsExplain) {
		List<Explain> eps=new ArrayList<Explain>();
		//产品信息
		Goods goods=goodsExplain.getGoods();
		//安全星级是否计算过 true没计算过
		List<Map<String,Object>> maps=getGoodsSafter(goods.getId()+"");
		if(maps==null||maps.size()==0) return false;
		for(Map map:maps){
			int size=(map.size()-2)/4;
			for(int i=1;i<=size;i++){
				//拼接字段与字段对应的值
				Explain exp=new Explain();
				List<Long> cpsId=new ArrayList<Long>();
				String filed_name="safety_"+i+"_name";
				String name=(String)map.get(filed_name.trim());
				exp.setName(name);

				String filed_unit="safety_"+i+"_unit";
				int unit=(Integer)map.get(filed_unit.trim());
				exp.setUnit(unit);

				String filed_num="safety_"+i+"_num";
				Float num=(Float)map.get(filed_num.trim());
				exp.setNum(num.toString());

				String filed_cps="safety_"+i+"_cps";
				String cps=(String)map.get(filed_cps);
				if(!StringUtils.isBlank(cps)){
					String[] cpss=cps.split(",");
					if(null!=cpss && cpss.length>0){
						for(int j=0;j<cpss.length;j++){
							String c=cpss[j].trim();
							cpsId.add(Long.parseLong(c));
						}
					}

				}
				exp.setCompositionIds(cpsId);
				exp.setId(i);
				exp.setDisplayName(name);
				StringBuffer b=new StringBuffer();
				if(null!=cpsId && cpsId.size()>0){
					for(int k=0;k<cpsId.size();k++){
						String cp=cpsId.get(k).toString().trim()+",";
						b.append(cp);
					}
					cps=b.substring(0,b.length()-1);
					exp.setComposition(compositonService.getCompositionByIds(b.toString()));
				}
				eps.add(exp);
			}
			goodsExplain.setSafety(eps);
		}
		return true;
	}
	/**
	 *  得到安全星级
	 * @param goodsId
	 * @return
	 */
	public ReturnData  getSafter(String goodsIds) {
		try {
			List<GoodsExplain> geList=new ArrayList<GoodsExplain>();
			List<Goods> glist=goodsMapper.getGoodsByIds(goodsIds);
			boolean flag;
			if(null!=glist && glist.size()>0){
				for(int i=0;i<glist.size();i++){
					GoodsExplain goodsExplain=new GoodsExplain();
					goodsExplain.setGoods(glist.get(i));
					List<Composition> cps=compositonService.getCompositionByIds(glist.get(i).getCps());
					//获取安全
					flag=getGoodsSafter(goodsExplain);
					goodsExplain.setComposition(cps);
					if(!flag){
						setGoodsSafter(goodsExplain);
					}
					geList.add(goodsExplain);
				}
			}
			return new ReturnData(geList);
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getSafter");
			map.put("goodsIds", goodsIds);
			new LogException(e, map);
		}
		return ReturnData.ERROR;
	}

	public List<Map<String,Object>> getGoodsSafter(String goodsIds){
		List<Map<String,Object>> list=goodsMapper.getSafter(goodsIds);
		return list;
	}

	public List<Map<String,Object>> getGoodsSkin(String goodsIds){
		List<Map<String,Object>> list=goodsMapper.getGoodsSkin(goodsIds);
		return list;
	}


	/**
	 * 产品安全处理
	 * 开始进行表数据处理,拼接sql
	 * @param goodsExplain
	 */
	public void  allGoodsSkin(GoodsExplain goodsExplain) {
		try {
			List<Explain> eps=new ArrayList<Explain>();
			Goods goods=goodsExplain.getGoods();
			//肤质信息从skin表中获取
			List<Map<String,Object>> listMap2=getAllGoodsSkin();
			String key="";
			for(Map map:listMap2){
				key+=","+map.get("key");
			}
			key=key.trim().substring(1);
			String[] svalues=null;
			if(!StringUtils.isBlank(key)){
				svalues=key.split(",");
			}
			String updateSql="";
			long goodsId=goods.getId();
			String mid=goods.getMid();
			String insertKeys="goods_id"+","+"mid"+",";
			String insertValues=goodsId+",'"+mid+"'"+",";

			List<Map<String,Object>> listMap=getGoodsSkin(goods.getId()+"");
			for(int i=0;i<svalues.length;i++) {
				Explain exp=new Explain();
				String skinType=svalues[i];
				goodsExplain.skinAnalysis(svalues[i]);
				//适合我的肤质
				List<Long> SuitCpsList=new ArrayList<Long>();
				List<Long> NoSuitCpsList=new ArrayList<Long>();
				String suitCps="";
				String noSuitCps="";
				exp=goodsExplain.getSuit();
				//不匹配是否存在
				int notmatchisexist=0;
				//匹配是否存在
				int matchisexist=0;
				//匹配的数量
				long suitNum=0L;
				long noSuitNum=0L;
				if(null!=exp){
					SuitCpsList=goodsExplain.getSuit().getCompositionIds();
					suitCps=SuitCpsList.toString();
					suitCps=suitCps.trim().substring(1, suitCps.length()-1);

					suitNum=SuitCpsList.size();
					if(suitNum>0){
						matchisexist=1;
					}
				}

				exp=goodsExplain.getNoSuit();
				if(null!=exp){
					//不适合我的肤质
					NoSuitCpsList=goodsExplain.getNoSuit().getCompositionIds();
					noSuitCps=NoSuitCpsList.toString();
					noSuitCps=noSuitCps.substring(1, noSuitCps.length()-1);

					//不匹配的数量
					noSuitNum=NoSuitCpsList.size();
					if(noSuitNum>0){
						notmatchisexist=1;
					}
				}

				//先判断是否有
				if(null!=listMap && listMap.size()>0 ) {
					updateSql+="skin_match_"+skinType+"_num="+suitNum+","+"skin_notmatch_"+skinType+"_num="+noSuitNum+","+"skin_match_"+skinType+"_isexist="+matchisexist+","+"skin_notmatch_"+skinType+"_isexist="+notmatchisexist+","+"skin_match_"+skinType+"_cps="+"'"+suitCps+"'"+","+"skin_notmatch_"+skinType+"_cps="+"'"+noSuitCps+"'"+",";
				} else {
					String fields="skin_match_{SKIN}_num,skin_notmatch_{SKIN}_num,skin_match_{SKIN}_isexist,skin_notmatch_{SKIN}_isexist,skin_match_{SKIN}_cps,skin_notmatch_{SKIN}_cps";
					//遍历把skinType的值给{skin}  for
					insertKeys+=StringUtils.replaceEach(fields, new String[]{"{SKIN}"}, new String[]{skinType})+",";
					insertValues+=suitNum+","+noSuitNum+","+matchisexist+","+notmatchisexist+",'"+suitCps+"','"+noSuitCps+"'"+",";
				}
			}
			//插入 16种都匹配完全
			if(null!=listMap && listMap.size()>0){
				updateSql=updateSql.substring(0,updateSql.length()-1);
				goodsMapper.updateGoodsSkin(updateSql,goodsId);
			}else{
				insertKeys=insertKeys.substring(0,insertKeys.length()-1);
				insertValues=insertValues.substring(0, insertValues.length()-1);
				goodsMapper.insertGoodsSkin(insertKeys,insertValues);
			}
		} catch(Exception e){
			Map map = new HashMap();
			map.put("method", "GoodsService.allGoodsSkin");
			map.put("goodsId", goodsExplain.getGoods().getId());
			new LogException(e, map);
		}
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
					List<Map<String,Object>> listMap=goodsMapper.getAllGoodsSkin();
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
					List<Map<String,Object>> listMap=goodsMapper.getAllCommonGoodsCategory();
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
	public ReturnListData goodsCalculate(UserInfo userInfo,String ids, int update) {
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
							//产品适合的肤质
							if(gci.get(j) instanceof GoodsSkinCalculateHandler){
								if(!StringUtils.isBlank(gci.get(j).insertSql())){
									int index=0;
									String insertSql=gci.get(j).insertSql();
									index=this.getIndex(skinBuffer.toString(),insertSql);
									String valSql=gci.get(j).insertSql().substring(index, insertSql.length());
									skinBuffer.append(valSql+",");
								}
								//产品分类
							}else if(gci.get(j) instanceof GoodsCategoryCalculateHandler){
								GoodsCategoryCalculateHandler goodsCategory=(GoodsCategoryCalculateHandler)gci.get(j);
								if(!StringUtils.isBlank(goodsCategory.getInsertSql2())){
									int index=0;
									String polySql=goodsCategory.getInsertSql2();
									String[] cs;
									boolean isCaIds=false;
									cs=polySql.split(",");
									//判断产品是否有多个分类
									for(int k=0;k<cs.length;k++){
										if(cs[k].equals("category_ids")){
											isCaIds=true;
										}
									}

									if(!StringUtils.isBlank(polySql)){
										//一个产品只有一个分类时
										if(!isCaIds){
											index=this.getIndex(polyBuffer.toString(),polySql);
											String valSql2=polySql.substring(index, polySql.length());
											polyBuffer.append(valSql2+",");
										}else{ //一个产品至少两个分类时
											index=this.getIndex(polysBuffer.toString(),polySql);
											String valSql2=polySql.substring(index, polySql.length());
											polysBuffer.append(valSql2+",");
										}
									}
								}
								//产品星级
							}else if(gci.get(j) instanceof GoodsSafetyCalculateHandler){
								if(!StringUtils.isBlank(gci.get(j).insertSql())){
									int index=0;
									String insertSql=gci.get(j).insertSql();
									index=this.getIndex(safetyBuffer.toString(),insertSql);
									String valSql=gci.get(j).insertSql().substring(index, insertSql.length());
									safetyBuffer.append(valSql+",");
								}
								//产品搜索
							}else if(gci.get(j) instanceof GoodsSearchCalculateHandler){
								if(!StringUtils.isBlank(gci.get(j).insertSql())){
									int index=0;
									String insertSql=gci.get(j).insertSql();
									index=this.getIndex(searchBuffer.toString(),insertSql);
									String valSql=gci.get(j).insertSql().substring(index, insertSql.length());
									searchBuffer.append(valSql+",");
								}
								//产品标签
							}else if(gci.get(j) instanceof GoodsTagResultCalculateHandler){
								if(!StringUtils.isBlank(gci.get(j).insertSql())){
									String insertSql=gci.get(j).insertSql();
									int index=0;
									index=this.getIndex(tagBuffer.toString(),insertSql);
									String valSql=gci.get(j).insertSql().substring(index, insertSql.length());
									tagBuffer.append(valSql+",");
								}
							}
						}
					}

				}
				n++;
				//执行sql 批量
				if( n%600==0&&update==3){
					n=0;
					this.excSql(skinBuffer,tagBuffer,safetyBuffer,polyBuffer,polysBuffer,searchBuffer,skinSql,safetySql,polysKey,polyKey,tagSql,searchSql);
					skinBuffer=new StringBuffer();
					tagBuffer=new StringBuffer();
					safetyBuffer=new StringBuffer();
					polyBuffer=new StringBuffer();
					polysBuffer=new StringBuffer();
					searchBuffer=new StringBuffer();

				}
				gclist.add(goodsExplain);



				//清空缓存
				if(goodsExplain.getGoods()!=null) {
					String mid=goodsExplain.getGoods().getMid();
					//清空缓存
					cacheService.cleanProducts(mid);
					cacheService.cleanProducts(id+"");
				}
			}

			//n小于600
			if(n>0) {
				this.excSql(skinBuffer,tagBuffer,safetyBuffer,polyBuffer,polysBuffer,searchBuffer,skinSql,safetySql,polysKey,polyKey,tagSql,searchSql);
				skinBuffer=new StringBuffer();
				tagBuffer=new StringBuffer();
				safetyBuffer=new StringBuffer();
				polyBuffer=new StringBuffer();
				polysBuffer=new StringBuffer();
				searchBuffer=new StringBuffer();
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
	 * @param 是否更新
	 */
	private GoodsCalculateI goodsProcessCalculate(GoodsCalculateI gci,int update) {
		try{
			String selectSql=gci.selectSql();
			if(gci instanceof GoodsCategoryCalculateHandler){
				GoodsCategoryCalculateHandler gcch=(GoodsCategoryCalculateHandler)gci;
				//产品存在多个分类
				if(!StringUtils.isBlank(gcch.getSelectSql2())){
					//hq_goods_poly_category
					List<Map<String,Object>> listMap=goodsMapper.select(gcch.getSelectSql2());
					//不存在
					if(listMap.size()==0 || null==listMap){
						goodsMapper.insert(gcch.getInsertSql2());
					}else{
						goodsMapper.update(gcch.getUpdateSql2());
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


				List<Map<String,Object>> m=goodsMapper.select(gci.selectSql());
				Criteria cr=Criteria.where("id").is(goodsId.intValue());
				Update up=new Update();
				up.set("safety_1_num", safetyNum);

				//update=1 强行跟新
				if(null==m || m.size()==0) {
					if(!StringUtils.isBlank(gci.insertSql())){
						goodsMapper.insert(gci.insertSql());
						//更新mongo的安全星级
						mongoTemplate.updateFirst(new Query(cr), up, EntityGoods.class,"entity_goods");
					}
				} else if(update==1) {
					if(!StringUtils.isBlank(gci.updaeSql())){
						goodsMapper.update(gci.updaeSql());
						mongoTemplate.updateFirst(new Query(cr), up, EntityGoods.class,"entity_goods");
					}
				}
				//作为最终的显示
				m=goodsMapper.select(gci.selectSql());
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
		Goods goods=goodsMapper.getById(id);
		//获取cps
		GoodsExt goodsCps=goodsExtMapper.getExtByGoodsId(id);
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
			List<Map<String,Object>> geu=goodsMapper.getPolyCategoryBygoodsIds(goodsIds);
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
					List<GoodsTag> gtList=goodsMapper.getAllTag();
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
			gtr=goodsMapper.getTagResultByGoodsId(goodsId);
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
			gtr=goodsMapper.getTagResultByGoodsId(goodsId);
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
	 * 获取该产品含有的标签核心成分
	 * 查询出is_main 为1的所有信息
	 * @return
	 */
	public List<GoodsTagComposition> getTagCompositionByCps(List<Long> cpsId){
		try {
			//String cpss[]=cps.split(",");
			List<GoodsTagComposition> gtc=new ArrayList<GoodsTagComposition>();
			//获取所有产品标签核心成分
			List<GoodsTagComposition> allTags=getAllTagComposition();
			for(int i=0;i<cpsId.size();i++) {
				//GoodsTagComposition gt=allTagComposition.get(Long.parseLong(cpss[i]));
				for(GoodsTagComposition goodstc:allTags){
					if(cpsId.get(i).longValue()==goodstc.getCompositionId()){
						//该产品含有的标签核心成分
						gtc.add(goodstc);
					}
				}
			}

			//List<GoodsTagComposition> gt=goodsMapper.getTagCompositionByCps(cps);

			return gtc;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getTagCompositionByCps");
			map.put("cpsId.size()", cpsId.size());
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
					List<Map<String,Object>> listMap=goodsMapper.getAllSpecialCategory();
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
					List<Map<String,Object>> listMap=goodsMapper.getAllEnglishCategory();
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
					List<GoodsRule> gtList=goodsMapper.getAllRule();
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
								List<Used> useds=compositionMapper.getUsedsByUid(cpsList.get(i).getUsed());
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

	static List<Composition> allCpsList=null;
	static Map<Long,Composition>  allcps=new HashMap<Long,Composition>();

	static Map<Long,GoodsTagComposition>  allTagComposition=new HashMap<Long,GoodsTagComposition>();

	/**
	 * 本地缓存,用于产品计算
	 */
	public void goodsCache(){
		try {
			allGoodsSkin=goodsMapper.getAllGoodsSkin();
			allCommonGoodsCategory=goodsMapper.getAllCommonGoodsCategory();
			allSpecialCategory=goodsMapper.getAllSpecialCategory();
			allEnglishCategory=goodsMapper.getAllEnglishCategory();
			allGoodsTag=goodsMapper.getAllTag();
			goodsRules=goodsMapper.getAllRule();
			allGoodsCategory=goodsMapper.getAllGoodsCategory();
			List<GoodsTagComposition> allTags=goodsMapper.getAllTagComposition();
			for(int i=0;i<allTags.size();i++) {
				allTagComposition.put(allTags.get(i).getId(), allTags.get(i));
			}
			List<Composition> allCpsList = compositionMapper.getAll();
			for(int i=0;i<allCpsList.size();i++) {
				Composition  cp=allCpsList.get(i);
				if(!StringUtils.isBlank(cp.getUsed())) {
					List<Used> useds=compositionMapper.getUsedsByUid(cp.getUsed());
					cp.setUseds(useds);
				}
				allcps.put(cp.getId(), cp);
			}
			//成分组
			allOutComposition=getOutComposition2();
			//所有成分
			tagsByIsMain=goodsMapper.getAllTagComposition();
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.goodsCache");
			new LogException(e, map);
		}
	}

	/**
	 * redis缓存,用于产品计算
	 */
	public void goodsCacheByRedis(){
		try {
			this.getAllGoodsSkin();
			this.getAllCommonGoodsCategory();
			this.getAllSpecialCategory();
			this.getAllEnglishCategory();
			this.getAllGoodsTag();
			this.getAllGoodsRule();
			this.getAllGoodsCategory2();
			this.getAllTagCompositionRedis();
			compositionService.getAllComposition();

			this.getOutComposition();
			this.getAllTagCompositionRedis();
		} catch (Exception e) {
			e.printStackTrace();
			Map map = new HashMap();
			map.put("method", "GoodsService.goodsCacheByRedis");
			new LogException(e, map);
		}
	}

	public int getIndex(String sql,String sql2){
		int index=0;
		if(!StringUtils.isBlank(sql)){
			index=sql2.indexOf("values")+6;
		}else{
			index=sql2.indexOf("values");
		}
		return index;
	}

	@Deprecated
	public void excSql(StringBuffer skinBuffer,StringBuffer tagBuffer,StringBuffer safetyBuffer,StringBuffer polyBuffer,StringBuffer polysBuffer,StringBuffer searchBuffer,String skinSql,String safetySql,String polysKey,String polyKey,String tagSql,String searchSql){
		String skinVal=skinBuffer.toString();
		if(!StringUtils.isBlank(skinVal)){
			skinVal=skinVal.substring(0,skinVal.length()-1);
			String insertSkin=skinSql+skinVal;
			goodsMapper.insert(insertSkin);
		}

		String tagVal=tagBuffer.toString();
		if(!StringUtils.isBlank(tagVal)){
			tagVal=tagVal.substring(0,tagVal.length()-1);
			String insertTag=tagSql+tagVal;
			goodsMapper.insert(insertTag);
		}

		String safetyVal=safetyBuffer.toString();
		if(!StringUtils.isBlank(safetyVal)){
			safetyVal=safetyVal.substring(0,safetyVal.length()-1);
			String insertSafety=safetySql+safetyVal;
			goodsMapper.insert(insertSafety);
		}

		String polyVal=polyBuffer.toString();
		if(!StringUtils.isBlank(polyVal)){
			polyVal=polyVal.substring(0,polyVal.length()-1);
			String insertPoly=polyKey+polyVal;
			goodsMapper.insert(insertPoly);
		}

		String polysVal=polysBuffer.toString();
		if(!StringUtils.isBlank(polysVal)){
			polysVal=polysVal.substring(0,polysVal.length()-1);
			String insertPolys=polysKey+polysVal;
			goodsMapper.insert(insertPolys);
		}

		String searchVal=searchBuffer.toString();
		if(!StringUtils.isBlank(searchVal)){
			searchVal=searchVal.substring(0,searchVal.length()-1);
			String insertSearch=searchSql+searchVal;
			goodsMapper.insert(insertSearch);
		}
	}

	//本地批量时的处理--以抛弃
	@Deprecated
	public ReturnData goodsSearchUpdate(int type){
		try{
			//判断是否有需要insert的  再update 再看type是否需要清空
			//需要插入的category
			String categorySql="SELECT goods_id,exist_category_ids FROM hq_goods_poly_category2 WHERE goods_id NOT IN(SELECT goods_id FROM hq_goods_poly_category)";
			List<Map<String, Object>> categoryListMap=goodsMapper.select(categorySql);
			StringBuffer ids=new StringBuffer();
			StringBuffer ids2=new StringBuffer();
			for(int i=0;i<categoryListMap.size();i++){
				int result=(Integer)categoryListMap.get(i).get("exist_category_ids");
				if(result==1){
					ids.append(categoryListMap.get(i).get("goods_id").toString()+",");
				}else if(result==0){
					ids2.append(categoryListMap.get(i).get("goods_id").toString()+",");
				}
			}
			if(!StringUtils.isBlank(ids.toString())){
				String inIds=ids.substring(0,ids.length()-1);
				String polysSql="insert into hq_goods_poly_category(goods_id,category_id,category_ids,exist_category_ids,update_time) select goods_id,category_id,category_ids,exist_category_ids,update_time from hq_goods_poly_category2 where goods_id in("+inIds+")";
				goodsMapper.insert(polysSql);
			}
			if(!StringUtils.isBlank(ids2.toString())){
				String inIds2=ids2.substring(0,ids2.length()-1);
				String polySql="insert into hq_goods_poly_category(goods_id,category_id,exist_category_ids,update_time)  select goods_id,category_id,exist_category_ids,update_time from hq_goods_poly_category2 where goods_id in("+inIds2+")";
				goodsMapper.insert(polySql);
			}

			//safter
			String safterSql="SELECT * FROM hq_goods_safter2 WHERE goods_id NOT IN(SELECT goods_id FROM hq_goods_safter)";
			List<Map<String, Object>> safetyListMap=goodsMapper.select(safterSql);
			StringBuffer safetyBuffer=new StringBuffer();
			for(int i=0;i<safetyListMap.size();i++){
				safetyBuffer.append(safetyListMap.get(i).get("goods_id").toString()+",");
			}
			if(!StringUtils.isBlank(safetyBuffer.toString())){
				String inIds=safetyBuffer.substring(0,safetyBuffer.length()-1);
				String safetySql="insert into hq_goods_safter(goods_id,safety_1_name,safety_1_unit,safety_1_num,safety_1_cps,safety_2_name,safety_2_unit,safety_2_num,safety_2_cps,safety_3_name,safety_3_unit,safety_3_num,safety_3_cps,safety_4_name,safety_4_unit,safety_4_num,safety_4_cps,safety_5_name,safety_5_unit,safety_5_num,safety_5_cps,update_time) select goods_id,safety_1_name,safety_1_unit,safety_1_num,safety_1_cps,safety_2_name,safety_2_unit,safety_2_num,safety_2_cps,safety_3_name,safety_3_unit,safety_3_num,safety_3_cps,safety_4_name,safety_4_unit,safety_4_num,safety_4_cps,safety_5_name,safety_5_unit,safety_5_num,safety_5_cps,update_time from hq_goods_safter2 where goods_id in("+inIds+")";
				goodsMapper.insert(safetySql);
			}

			//skin
			String skinSql="SELECT * FROM hq_goods_skin2 WHERE goods_id NOT IN(SELECT goods_id FROM hq_goods_skin)";
			List<Map<String, Object>> skinListMap=goodsMapper.select(skinSql);
			StringBuffer skinBuffer=new StringBuffer();
			for(int i=0;i<skinListMap.size();i++){
				skinBuffer.append(skinListMap.get(i).get("goods_id").toString()+",");
			}
			if(!StringUtils.isBlank(skinBuffer.toString())){
				String inIds=skinBuffer.substring(0,skinBuffer.length()-1);
				String skinSql2="insert into hq_goods_skin(goods_id,mid,skin_match_OSPW_num,skin_notmatch_OSPW_num,skin_match_OSPW_isexist,skin_notmatch_OSPW_isexist,skin_match_OSPW_cps,skin_notmatch_OSPW_cps,skin_match_OSPT_num,skin_notmatch_OSPT_num,skin_match_OSPT_isexist,skin_notmatch_OSPT_isexist,skin_match_OSPT_cps,skin_notmatch_OSPT_cps,skin_match_OSNW_num,skin_notmatch_OSNW_num,skin_match_OSNW_isexist,skin_notmatch_OSNW_isexist,skin_match_OSNW_cps,skin_notmatch_OSNW_cps,skin_match_OSNT_num,skin_notmatch_OSNT_num,skin_match_OSNT_isexist,skin_notmatch_OSNT_isexist,skin_match_OSNT_cps,skin_notmatch_OSNT_cps,skin_match_ORPW_num,skin_notmatch_ORPW_num,skin_match_ORPW_isexist,skin_notmatch_ORPW_isexist,skin_match_ORPW_cps,skin_notmatch_ORPW_cps,skin_match_ORPT_num,skin_notmatch_ORPT_num,skin_match_ORPT_isexist,skin_notmatch_ORPT_isexist,skin_match_ORPT_cps,skin_notmatch_ORPT_cps,skin_match_ORNW_num,skin_notmatch_ORNW_num,skin_match_ORNW_isexist,skin_notmatch_ORNW_isexist,skin_match_ORNW_cps,skin_notmatch_ORNW_cps,skin_match_ORNT_num,skin_notmatch_ORNT_num,skin_match_ORNT_isexist,skin_notmatch_ORNT_isexist,skin_match_ORNT_cps,skin_notmatch_ORNT_cps,skin_match_DSPW_num,skin_notmatch_DSPW_num,skin_match_DSPW_isexist,skin_notmatch_DSPW_isexist,skin_match_DSPW_cps,skin_notmatch_DSPW_cps,skin_match_DSPT_num,skin_notmatch_DSPT_num,skin_match_DSPT_isexist,skin_notmatch_DSPT_isexist,skin_match_DSPT_cps,skin_notmatch_DSPT_cps,skin_match_DSNW_num,skin_notmatch_DSNW_num,skin_match_DSNW_isexist,skin_notmatch_DSNW_isexist,skin_match_DSNW_cps,skin_notmatch_DSNW_cps,skin_match_DSNT_num,skin_notmatch_DSNT_num,skin_match_DSNT_isexist,skin_notmatch_DSNT_isexist,skin_match_DSNT_cps,skin_notmatch_DSNT_cps,skin_match_DRPW_num,skin_notmatch_DRPW_num,skin_match_DRPW_isexist,skin_notmatch_DRPW_isexist,skin_match_DRPW_cps,skin_notmatch_DRPW_cps,skin_match_DRPT_num,skin_notmatch_DRPT_num,skin_match_DRPT_isexist,skin_notmatch_DRPT_isexist,skin_match_DRPT_cps,skin_notmatch_DRPT_cps,skin_match_DRNW_num,skin_notmatch_DRNW_num,skin_match_DRNW_isexist,skin_notmatch_DRNW_isexist,skin_match_DRNW_cps,skin_notmatch_DRNW_cps,skin_match_DRNT_num,skin_notmatch_DRNT_num,skin_match_DRNT_isexist,skin_notmatch_DRNT_isexist,skin_match_DRNT_cps,skin_notmatch_DRNT_cps,update_time) "
						+ " select goods_id,mid,skin_match_OSPW_num,skin_notmatch_OSPW_num,skin_match_OSPW_isexist,skin_notmatch_OSPW_isexist,skin_match_OSPW_cps,skin_notmatch_OSPW_cps,skin_match_OSPT_num,skin_notmatch_OSPT_num,skin_match_OSPT_isexist,skin_notmatch_OSPT_isexist,skin_match_OSPT_cps,skin_notmatch_OSPT_cps,skin_match_OSNW_num,skin_notmatch_OSNW_num,skin_match_OSNW_isexist,skin_notmatch_OSNW_isexist,skin_match_OSNW_cps,skin_notmatch_OSNW_cps,skin_match_OSNT_num,skin_notmatch_OSNT_num,skin_match_OSNT_isexist,skin_notmatch_OSNT_isexist,skin_match_OSNT_cps,skin_notmatch_OSNT_cps,skin_match_ORPW_num,skin_notmatch_ORPW_num,skin_match_ORPW_isexist,skin_notmatch_ORPW_isexist,skin_match_ORPW_cps,skin_notmatch_ORPW_cps,skin_match_ORPT_num,skin_notmatch_ORPT_num,skin_match_ORPT_isexist,skin_notmatch_ORPT_isexist,skin_match_ORPT_cps,skin_notmatch_ORPT_cps,skin_match_ORNW_num,skin_notmatch_ORNW_num,skin_match_ORNW_isexist,skin_notmatch_ORNW_isexist,skin_match_ORNW_cps,skin_notmatch_ORNW_cps,skin_match_ORNT_num,skin_notmatch_ORNT_num,skin_match_ORNT_isexist,skin_notmatch_ORNT_isexist,skin_match_ORNT_cps,skin_notmatch_ORNT_cps,skin_match_DSPW_num,skin_notmatch_DSPW_num,skin_match_DSPW_isexist,skin_notmatch_DSPW_isexist,skin_match_DSPW_cps,skin_notmatch_DSPW_cps,skin_match_DSPT_num,skin_notmatch_DSPT_num,skin_match_DSPT_isexist,skin_notmatch_DSPT_isexist,skin_match_DSPT_cps,skin_notmatch_DSPT_cps,skin_match_DSNW_num,skin_notmatch_DSNW_num,skin_match_DSNW_isexist,skin_notmatch_DSNW_isexist,skin_match_DSNW_cps,skin_notmatch_DSNW_cps,skin_match_DSNT_num,skin_notmatch_DSNT_num,skin_match_DSNT_isexist,skin_notmatch_DSNT_isexist,skin_match_DSNT_cps,skin_notmatch_DSNT_cps,skin_match_DRPW_num,skin_notmatch_DRPW_num,skin_match_DRPW_isexist,skin_notmatch_DRPW_isexist,skin_match_DRPW_cps,skin_notmatch_DRPW_cps,skin_match_DRPT_num,skin_notmatch_DRPT_num,skin_match_DRPT_isexist,skin_notmatch_DRPT_isexist,skin_match_DRPT_cps,skin_notmatch_DRPT_cps,skin_match_DRNW_num,skin_notmatch_DRNW_num,skin_match_DRNW_isexist,skin_notmatch_DRNW_isexist,skin_match_DRNW_cps,skin_notmatch_DRNW_cps,skin_match_DRNT_num,skin_notmatch_DRNT_num,skin_match_DRNT_isexist,skin_notmatch_DRNT_isexist,skin_match_DRNT_cps,skin_notmatch_DRNT_cps,update_time from hq_goods_skin2 where goods_id in("+inIds+")";
				goodsMapper.insert(skinSql2);
			}

			//tag
			String tagSql="SELECT * FROM hq_goods_tag_result2 WHERE goods_id NOT IN(SELECT goods_id FROM hq_goods_tag_result)";
			List<Map<String, Object>> tagListMap=goodsMapper.select(tagSql);
			StringBuffer tagBuffer=new StringBuffer();
			for(int i=0;i<tagListMap.size();i++){
				tagBuffer.append(tagListMap.get(i).get("goods_id").toString()+",");
			}
			if(!StringUtils.isBlank(tagBuffer.toString())){
				String inIds=tagBuffer.substring(0,tagBuffer.length()-1);
				String tagSql2="insert into hq_goods_tag_result(goods_id,auto_tag_ids,auto_tag_names,tag_ids,tag_names,create_stamp,update_time) select goods_id,auto_tag_ids,auto_tag_names,tag_ids,tag_names,create_stamp,update_time from hq_goods_tag_result2 where goods_id in("+inIds+")";
				goodsMapper.insert(tagSql2);
			}

			//search
			String searchSql="SELECT * FROM hq_goods_search2 WHERE goods_id NOT IN(SELECT goods_id FROM hq_goods_search)";
			List<Map<String, Object>> searchMap=goodsMapper.select(searchSql);
			StringBuffer searchBuffer=new StringBuffer();
			for(int i=0;i<searchMap.size();i++){
				searchBuffer.append(searchMap.get(i).get("goods_id").toString()+",");
			}
			if(!StringUtils.isBlank(searchBuffer.toString())){
				String inIds=searchBuffer.substring(0,searchBuffer.length()-1);
				String searchSql2="insert into hq_goods_search(goods_id,cps,cps_search,category,safety_1_num,tag_ids,update_time) select goods_id,cps,cps_search,category,safety_1_num,tag_ids,update_time from hq_goods_search2 where goods_id in("+inIds+")";
				goodsMapper.insert(searchSql2);
			}

			//type=1删除临时表
			if(type==1){
				String delTagSql="delete from hq_goods_tag_result2";
				String delCateorySql="delete from hq_goods_poly_category2";
				String delSkinSql="delete from hq_goods_skin2";
				String delSafterSql="delete from hq_goods_safter2";
				String delSearchSql="delete from hq_goods_search2";
				goodsMapper.delete(delTagSql);
				goodsMapper.delete(delCateorySql);
				goodsMapper.delete(delSkinSql);
				goodsMapper.delete(delSafterSql);
				goodsMapper.delete(delSearchSql);
			}
			return ReturnData.SUCCESS;
		}catch(Exception e){
			logger.error("method:goodsSearchUpdate arg:{type:"+type  + "   desc:" +  ExceptionUtils.getStackTrace(e));
		}
		return ReturnData.ERROR;

	}

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
					List<GoodsTagComposition> allTags=goodsMapper.getAllTagComposition();
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
					List<Map<String,Object>> allTags=goodsMapper.getAllOutComposition();
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

	/**
	 * 不想要的成分组
	 * 不带缓存
	 * @return
	 */
	public Map<Integer,List<Composition>> getOutComposition2(){
		try {
			List<Map<String,Object>> allTags=goodsMapper.getAllOutComposition();
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
	 * 缓存预热使用  产品分页查询mid
	 * @param goodsCondition
	 * @return
	 */
	public List findGoodsMidByPage(Paged goodsCondition) {
		return goodsMapper.findGoodsMidByPage(goodsCondition);
	}

	/**
	 * 缓存预热使用 查询可用产品总数
	 * @return
	 */
	public int selectTotal() {
		return goodsMapper.selectTotal();
	}

	/**
	 * 更新搜索表的pCategory字段,一次性接口
	 * @return
	 */
	@Deprecated
	public ReturnData goodsSearchPcategory() {
		try{
			goodsMapper.updatePcategory();
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.goodsSearchPcategory");
			new LogException(e, map);
			return ReturnData.ERROR;
		}
	}

	/**
	 * 得到产品分类表hq_goods_category
	 * 不带缓存
	 * @return
	 */
	public List<Map<String,Object>> getAllGoodsCategory() {
		try{
			List<Map<String,Object>> list=goodsMapper.getAllGoodsCategory();
			return list;
		}catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getAllGoodsCategory");
			new LogException(e, map);
			return null;
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
                    List<Map<String,Object>> list=goodsMapper.getAllGoodsCategory();
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
        if(null == configMapper){
            logger.error("================");
            logger.error("map is null");
            logger.error("================");
        }
        Config dataTypeInfo = configMapper.getConfigByKey("product_category_data_type");
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
     * 用于缓存预热
     * 获取热度前500的产品mid
     * @return
     */
    public Map getHotGoodsMids(){
    	Map map=null;
    	Query query=new Query(Criteria.where("hidden").is(0).and("deleted").is(0)).with(new Sort(Direction.DESC,"hitNum")).limit(50);
    	query.fields().include("mid").include("id");
    	List<EntityBase> goodsList=mongoTemplate.find(query, EntityBase.class,"entity_goods");
    	
    	if(null!=goodsList){
    		//获取mid
    		StringBuffer sb=new StringBuffer();
    		for(EntityBase goods:goodsList){
    			sb.append(goods.getMid()+",");
    		}
    		String mids=sb.substring(0, sb.length()-1);
    		if(StringUtils.isNotBlank(mids)){
    			map=new HashMap();
        		map.put("mids", mids);
        		return map;
        	}
    	}
    	return map;
    }
    
    
}