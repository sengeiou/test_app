package cn.bevol.internal.service;

import cn.bevol.entity.service.CacheService;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.mybatis.dao.CompositionMapper;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.model.*;
import cn.bevol.model.vo.GoodsExplain;
import cn.bevol.entity.service.CompositionService;
import cn.bevol.entity.service.GoodsService;
import cn.bevol.entity.service.goodsCalculate.*;
import cn.bevol.log.LogClass;
import cn.bevol.util.ReturnListData;
import com.io97.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by mysens on 17-6-6.
 */
@Service
@LogClass
public class InternalGoodsCalculateService {

    private static Logger logger = LoggerFactory.getLogger(InternalGoodsCalculateService.class);

    @Resource
    MongoTemplate mongoTemplate;
    @Resource
    GoodsMapper goodsMapper;
    @Resource
    CompositionMapper compositionMapper;
    @Resource
    GoodsService goodsService;
    @Resource
    CompositionService compositionService;
    @Resource
    CacheService cacheService;

    public static final boolean  islocalcache=true;

    private static Map<Integer,List<Composition>> allOutComposition=null;
    private static List<Map<String,Object>> allGoodsSkin=null;
    private static List<Map<String,Object>> allCommonGoodsCategory=null;
    private static List<Map<String,Object>> allSpecialCategory=null;
    private static List<Map<String,Object>> allEnglishCategory=null;
    private static List<GoodsTag> allGoodsTag=null;
    private static List<GoodsRule> goodsRules=null;
    private static List<GoodsTagComposition> tagsByIsMain=null;
    private static List<Map<String,Object>> allGoodsCategory=null;

    static List<Composition> allCpsList=null;
    static Map<Long,Composition>  allcps=new HashMap<Long,Composition>();

    static Map<Long,GoodsTagComposition>  allTagComposition=new HashMap<Long,GoodsTagComposition>();

    /**
     * TODO
     * redis缓存
     */
    public void goodsCalculateRedisCache(){
        try {
            goodsService.setDomesticInternationalDataType();
            goodsService.getAllGoodsSkin();
            goodsService.getAllCommonGoodsCategory();
            goodsService.getAllSpecialCategory();
            goodsService.getAllEnglishCategory();
            goodsService.getAllGoodsTag();
            goodsService.getAllGoodsRule();
            goodsService.getAllGoodsCategory2();
            goodsService.getAllTagCompositionRedis();
            compositionService.getAllComposition();
            //成分使用目的
            compositionService.getAllUsed();

            goodsService.getOutComposition();
            goodsService.getAllTagCompositionRedis();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("method:goodsCacheByRedis arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
    }

    public void goodsCalculateLocalCache(){
        try {
            goodsService.setDomesticInternationalDataType();
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
                String usedsString = cp.getUsed();
                usedsString = StringUtil.trim(usedsString, ",");
                if(!StringUtils.isBlank(usedsString)) {
                    List<Used> useds=compositionMapper.getUsedsByUid(usedsString);
                    cp.setUseds(useds);
                }
                allcps.put(cp.getId(), cp);
            }
            //成分组
            allOutComposition=goodsService.getOutComposition2();
            //所有成分
            tagsByIsMain=goodsMapper.getAllTagComposition();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("method:goodsCalculateCache arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 批量综合计算
     * @param ids 肤质id
     * @param update
     * @return
     */
    public ReturnListData goodsCalculate(String ids, int update) {
		try{
			String skin=null;
            String [] idss=ids.split(",");
			List gclist=new ArrayList();
			for(int i=0;i<idss.length;i++) {
				Long id=Long.parseLong(idss[i]);
				//查询扩展表
				GoodsExplain goodsExplain=goodsService.getGoodsExplainById(id);
				goodsExplain.setUserSkin(skin);
				List<GoodsCalculateI> gci=getGoodsCalculates(goodsExplain);
				for(int j=0;j<gci.size();j++) {
					if(null!=gci.get(j)){
						gci.get(j).handler();
						//update=1正常流程 一个产品id一次操作
						if(update==1) {
							goodsProcessCalculate(gci.get(j),update);
						}
					}

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

            long total=gclist.size();
            return new ReturnListData(gclist,total);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("method:goodsCalculate arg:{update:" + update + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnListData.ERROR;
    }

    /**
     * 产品计算
     * 根据type计算相应的业务
     * @param ids 肤质id
     * @param update
     * @return
     */
    public ReturnListData goodsCalculate(String ids, int update,String type) {
        // TODO Auto-generated method stub
        try{
            String [] idss=ids.split(",");
            List gclist=new ArrayList();
            for(int i=0;i<idss.length;i++) {
                Long id=Long.parseLong(idss[i]);
                //查询扩展表  排序规则
                GoodsExplain goodsExplain=goodsService.getGoodsExplainById(id);
                //根据业务类型计算
                GoodsCalculateI gci=getGoodsCalculate(goodsExplain,type);
                //数据操作
                gci=goodsProcessCalculate(gci,update);
                gclist.add(goodsExplain);
            }
            long total=gclist.size();
            return new ReturnListData(gclist,total);
        }catch(Exception e){
            logger.error("method:goodsCalculate2 arg:{update:" + update + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnListData.ERROR;

    }
    /**
     * 产品运算处理
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
					logger.error("desc: 获取产品信息时出错: "+ExceptionUtils.getStackTrace(e));
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
			e.printStackTrace();
			logger.error("method:goodsProcessCalculate arg:{update:" + update + "   desc:" +  ExceptionUtils.getStackTrace(e));
		}
		return null;
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
                    return new GoodsCategoryCalculateHandler(goodsExplain,allCommonGoodsCategory,allSpecialCategory,allEnglishCategory,goodsService.getPolyCategoryBygoodsIdsLocal(goodsId+""));
                } else if(type.equals("effect")) {
                    //产品功效
                    List<GoodsEffectUsed>  geus=goodsService.getEffectDeatilByGoodsId(goodsExplain.getGoods(),goodsExplain.getComposition());
                    return new GoodsEffectCalculateHandler(goodsExplain,geus);
                } else if(type.equals("safety")) {
                    //安全星级
                    return new GoodsSafetyCalculateHandler(goodsExplain);
                } else if(type.equals("tag")) {
                    //标签
                    return new GoodsTagResultCalculateHandler(goodsExplain,allGoodsTag,goodsService.getAllTagComposition(),goodsRules,goodsService.getTagResultByGoodsIdLocal(goodsId));
                } else if(type.equals("search")){
                    //搜索
                    return new GoodsSearchCalculateHandler(goodsExplain,allOutComposition,allGoodsCategory);
                }
            }else{
                if(type.equals("skin")) {
                    //计算肤质
                    return new GoodsSkinCalculateHandler(goodsExplain, goodsService.getAllGoodsSkin());
                } else if(type.equals("category")) {
                    //产品分类
                    return new GoodsCategoryCalculateHandler(goodsExplain,goodsService.getAllCommonGoodsCategory(),goodsService.getAllSpecialCategory(),goodsService.getAllEnglishCategory(),goodsService.getPolyCategoryBygoodsIdsLocal(goodsId+""));
                } else if(type.equals("effect")) {
                    //产品功效
                    List<GoodsEffectUsed>  geus=goodsService.getEffectDeatilByGoodsId(goodsExplain.getGoods(),goodsExplain.getComposition());
                    return new GoodsEffectCalculateHandler(goodsExplain,geus);
                } else if(type.equals("safety")) {
                    //安全星级
                    return new GoodsSafetyCalculateHandler(goodsExplain);
                } else if(type.equals("tag")) {
                    //标签
                    return new GoodsTagResultCalculateHandler(goodsExplain,goodsService.getAllGoodsTag(),goodsService.getAllTagComposition(),goodsService.getAllGoodsRule(),goodsService.getTagResultByGoodsIdRedis(goodsId));
                } else if(type.equals("search")){
                    //搜索
                    return new GoodsSearchCalculateHandler(goodsExplain,goodsService.getOutComposition(),goodsService.getAllGoodsCategory2());
                }
            }
        }
        return null;
    }

    private int getIndex(String sql,String sql2){
        int index=0;
        if(!StringUtils.isBlank(sql)){
            index=sql2.indexOf("values")+6;
        }else{
            index=sql2.indexOf("values");
        }
        return index;
    }

    private void excSql(StringBuffer skinBuffer,StringBuffer tagBuffer,StringBuffer safetyBuffer,StringBuffer polyBuffer,StringBuffer polysBuffer,StringBuffer searchBuffer,String skinSql,String safetySql,String polysKey,String polyKey,String tagSql,String searchSql){
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
}
