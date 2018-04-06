package cn.bevol.entity.service;


import cn.bevol.log.LogException;
import cn.bevol.model.entity.DataCategory;
import cn.bevol.model.entity.DataCategoryRelation;
import cn.bevol.model.entity.DataResults;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.model.entity.GoodsBrand;
import cn.bevol.model.entityAction.Comment;
import cn.bevol.mybatis.dao.DataCategoryMapper;
import cn.bevol.mybatis.dao.GoodsBrandMapper;
import cn.bevol.mybatis.dao.IndexMapper;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;

import com.io97.utils.DateUtils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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

/**
 * 
 * @author Administrator
 *
 */
@Service
public class DataCategoryService extends BaseService {

	private static Logger logger = LoggerFactory.getLogger(DataCategoryService.class);
	@Autowired
    MongoTemplate mongoTemplate;
	@Resource
	private IndexMapper indexMapper;
	@Resource
	private DataCategoryMapper dataCategoryMapper;
	@Resource
	private GoodsBrandMapper goodsBrandMapper;
	
    public static Map<Integer, String> skins = new HashMap<Integer, String>();
    static String types[] = new String[]{"0", "1", "2"};

    static {
    	String skinStr="OSPT,OSPW,OSNW,OSNT,ORPT,ORPW,ORNW,ORNT";
		skins.put(0, skinStr);
		
		skinStr="DSNW,DSNT,DSPT,DSPW,DRPT,DRNW,DRNT,DRPW";
		skins.put(1, skinStr);
		
		skinStr="OSPT,OSPW,OSNW,OSNT,DSNW,DSNT,DSPT,DSPW";
		skins.put(2, skinStr);
		
    }
    
    static String  rid1[]=new String[]{"一般榜单","品牌榜"};
    static String rid2[]=new String []{"分类榜","肤质榜","安全榜"};
    static String rid3Banner[]=new String []{"全部","洁面","化妆水","精华","乳霜","眼霜","面膜","防晒","洗护"};
    static String rid3Safe[]=new String []{"油性","干性","敏感性"};
	/*
	 * 添加分类
	 */
	public ReturnData createCategory(String title) {
		try {
			DataCategory ndc = new DataCategory();
			ndc = mongoTemplate.findOne(new Query(Criteria.where("title").is(title)), DataCategory.class, "data_category");
			if (ndc == null && !title.equals("0")) {
				DataCategory newdc = new DataCategory();
				String collection = "data_category";
				Long id = this.getId(collection);
				newdc.setId(id);
				newdc.setTitle(title);
				mongoTemplate.save(newdc, collection);
				return ReturnData.SUCCESS;
			}
			if (title.equals("0")) {
				//添加一级分类
				for (int i = 0; i < rid1.length; i++) {
					String collection = "data_category";
					Long id = this.getId(collection);// data_category_inc
					DataCategory dc = new DataCategory();
					dc.setId(id);
					dc.setTitle(rid1[i]);
					mongoTemplate.save(dc, collection);
				}
				//添加二级分类
				for (int i = 0; i < rid2.length; i++) {
					String collection = "data_category";
					Long id = this.getId(collection);// data_category_inc
					DataCategory dc = new DataCategory();
					dc.setId(id);
					dc.setTitle(rid2[i]);
					mongoTemplate.save(dc, collection);
				}
				//添加三级分类banner
				for (int i = 0; i < rid3Banner.length; i++) {
					String collection = "data_category";
					Long id = this.getId(collection);// data_category_inc
					DataCategory dc = new DataCategory();
					dc.setId(id);
					dc.setTitle(rid3Banner[i]);
					mongoTemplate.save(dc, collection);
				}
				//添加三级分类安全
				for (int i = 0; i < rid3Safe.length; i++) {
					String collection = "data_category";
					Long id = this.getId(collection);// data_category_inc
					DataCategory dc = new DataCategory();
					dc.setId(id);
					dc.setTitle(rid3Safe[i]);
					mongoTemplate.save(dc, collection);
				}
				List alllist = mongoTemplate.find(new Query(), DataCategory.class, "data_category");
				return new ReturnData(alllist);
			}

		} catch (Exception e) {
			Map emap = new HashMap();
			emap.put("method", "dataCategoryService.createCategory()");
			emap.put("title", title);
			new LogException(e,emap);
		}
		return null;
	}

	/**
	 * 获取分类列表
	 * 
	 * @param pager
	 * @param id_1
	 * @return
	 */
	public ReturnListData gettagList(Long pager, Long rid1) {
		int pagerSize = 30;
		int start = (int) (pager - 1) * pagerSize;
		// 获取分类总数
		List<DataCategoryRelation> li = null;
		int total = 0;
		try {
			li = new ArrayList<DataCategoryRelation>();
			Query query = new Query(Criteria.where("rid1").is(rid1));
			query.with(new Sort(Direction.DESC, "CreateStamp"));
			List alllist = mongoTemplate.find(query, DataCategoryRelation.class, "data_category_relation");
			total = alllist.size();
			// 根据pager查询当前页分类
			query.with(new Sort(Direction.DESC, "CreateStamp")).skip(start).limit(pagerSize);
			li = (List<DataCategoryRelation>) mongoTemplate.find(query, DataCategoryRelation.class,
					"data_category_relation");
		} catch (Exception e) {
			Map emap = new HashMap();
			emap.put("method", "dataCategoryService.gettagList()");
			emap.put("pager", pager);
			emap.put("rid1", rid1);
			new LogException(e,emap);
		}
		return new ReturnListData(li, total);
	}

	/**
	 * 添加编辑分类信息
	 * 
	 * @param title
	 * @param exFeilds
	 * @return
	 */
	public ReturnData updateCategory(String title, String exFeilds) {
		// 如果Title ,exFeilds为空 则自动生成信息
		if (title.equals("0") && exFeilds.equals("0")) {
			int fromIndex = 0;
			int toIndex = 0;
			int rid1Siz = 2;
			int rid2Siz = 3;
			int rid3bannerSiz = 9;
			int rid3skinSize = 3;
			DataCategoryRelation dcr = new DataCategoryRelation();
			List<DataCategory> rid = mongoTemplate.find(new Query(), DataCategory.class, "data_category");
			fromIndex = toIndex;// 0
			toIndex = toIndex + rid1Siz;// 2
			List<DataCategory> rid1List = rid.subList(fromIndex, toIndex);
			fromIndex = toIndex;// 2
			toIndex = toIndex + rid2Siz;// 5
			// 二级分类列表
			List<DataCategory> rid2List = rid.subList(fromIndex, toIndex);
			fromIndex = toIndex;// 5
			toIndex = toIndex + rid3bannerSiz;// 13
			// 三级分类banner列表
			List<DataCategory> rid3List = rid.subList(fromIndex, toIndex);
			// 三级分类肤质(敏感性 干性 油性)列表
			fromIndex = toIndex;// 13
			toIndex = toIndex + rid3skinSize;// 16
			List<DataCategory> skinList = rid.subList(fromIndex, toIndex);

			// 一般榜单下的分类
			DataCategoryRelation createdcr = new DataCategoryRelation();
			createdcr.setRid1(1L);
			createdcr.setRid1Title(rid1List.get(0).getTitle());
			for (int i = 0; i < rid2List.size(); i++) {
				System.out.println(rid2List.get(i).getTitle());
				createdcr.setRid2((Long) rid2List.get(i).getId());
				createdcr.setRid2Title(rid2List.get(i).getTitle());
				// 如果二级分类为安全或者分类 则添加8个banner
				if (createdcr.getRid2Title().equals("安全榜") || createdcr.getRid2Title().equals("分类榜")) {
					for (int j = 0; j < rid3List.size(); j++) {
						// 添加分类

						DataCategoryRelation ndr = new DataCategoryRelation();
						// 添加一级标签
						ndr.setRid1(rid1List.get(0).getId());
						ndr.setRid1Title(rid1List.get(0).getTitle());
						// 添加二级标签
						ndr.setRid2((Long) rid2List.get(i).getId());
						ndr.setRid2Title(rid2List.get(i).getTitle());
						// 添加三级标签
						ndr.setRid3(rid3List.get(j).getId());
						ndr.setRid3Title(rid3List.get(j).getTitle());
						// 设置分类字段
						Map map = new HashMap();
						switch (ndr.getRid3Title()) {
						case "洁面":
							map.put("goods_category", 6L);
							break;
						case "化妆水":
							map.put("goods_category", 7L);
							break;
						case "乳霜":
							map.put("goods_category", 8L);
							break;
						case "精华":
							map.put("goods_category", 9L);
							break;
						case "眼霜":
							map.put("goods_category", 10L);
							break;
						case "面膜":
							map.put("goods_category", 11L);
							break;
						case "防晒":
							map.put("goods_category", 13L);
							break;
						case "洗护":
							map.put("goods_category", 38L);
							break;
						case "全部":
							map.put("goods_category", null);
							break;
						}
						if(createdcr.getRid2Title().equals("安全榜")){
							map.put("head1", "榜单TOP100");
							map.put("head2", "分类产品安全指数排行");
						}else{
							map.put("head1", "榜单TOP100");
							map.put("head2", "8大分类产品人气排行");
						}
						ndr.setParams(map);
						if (createdcr.getRid2Title().equals("分类榜")) {
							ndr.setDataSourceType(2);
						} else if (createdcr.getRid2Title().equals("安全榜")) {
							ndr.setDataSourceType(4);
						}
						String collection = "data_category_relation";
						Long id = new Long(this.getId(collection));// data_category_relation_inc
						ndr.setId(id);
						ndr.getCreateStamp();
						mongoTemplate.save(ndr, "data_category_relation");
					}
				}
				else if (createdcr.getRid2Title().equals("肤质榜")){
				
					for(int k=0;k<skinList.size();k++){
						Map map = new HashMap();
						DataCategoryRelation ndr = new DataCategoryRelation();
						map.put("head1", "榜单TOP100");
						map.put("head2", "主流肤质产品人气排行");
						ndr.setParams(map);
						// 添加一级标签
						ndr.setRid1(rid1List.get(0).getId());
						ndr.setRid1Title(rid1List.get(0).getTitle());
						// 添加二级标签
						ndr.setRid2((Long) rid2List.get(i).getId());
						ndr.setRid2Title(rid2List.get(i).getTitle());
						ndr.setRid3(skinList.get(k).getId());
						ndr.setRid3Title(skinList.get(k).getTitle());
						ndr.setDataSourceType(3);
						String collection = "data_category_relation";
						Long id = new Long(this.getId(collection));// data_category_relation_inc
						ndr.setDataSourceType(3);
						ndr.setId(id);
						ndr.getCreateStamp();
						mongoTemplate.save(ndr, "data_category_relation");
					}
				}
			}
			Map map =new HashMap();
			map.put("head1", "榜单TOP100");
			map.put("head2", "市面主流品牌综合人气排行");
			//添加品牌总榜 
			DataCategoryRelation dcr2 = new DataCategoryRelation();
			dcr2.setRid1(rid1List.get(1).getId());
			dcr2.setRid1Title(rid1List.get(1).getTitle());
			dcr2.getCreateStamp();
			String collection = "data_category_relation";
			Long id = this.getId(collection);// data_category_relation_inc
			dcr2.setId(id);
			dcr2.setRid2(2L);
			dcr2.setRid2Title("品牌榜");
			dcr2.setParams(map);
			dcr2.setDataSourceType(5);
			mongoTemplate.save(dcr2, "data_category_relation");
			//增加品牌榜单
			DataCategoryRelation dcr3 = new DataCategoryRelation();
			dcr3.setRid1(rid1List.get(1).getId());
			dcr3.setRid1Title(rid1List.get(1).getTitle());
			dcr3.getCreateStamp();
			Long id3 = this.getId(collection);// data_category_relation_inc
			dcr3.setId(id3);
			dcr3.setRid3(6L);
			dcr3.setRid3Title("全部");
			dcr3.setDataSourceType(5);
			dcr3.setParams(map);
			mongoTemplate.save(dcr3, "data_category_relation");
			return new ReturnData(
					mongoTemplate.find(new Query(), DataCategoryRelation.class, "data_category_relation"));
		}

		if (!exFeilds.equals("0")) {
			JSONObject exFeild = JSONObject.fromObject(exFeilds);
			Map exFeil = (Map) exFeild;
			Query query = new Query(Criteria.where("rid1").is(exFeil.get("Rid1")).and("rid2").is(exFeil.get("Rid2"))
					.and("rid3").is(exFeil.get("Rid3")));
			// 查询是否存在该分类
			DataCategoryRelation dataCategoryRelations = new DataCategoryRelation();
			try {
				dataCategoryRelations = mongoTemplate.findOne(query, DataCategoryRelation.class,
						"data_category_relation");
				// 为空则创建
				if (dataCategoryRelations == null) {
					DataCategoryRelation cdcr = new DataCategoryRelation();
					String collection = "data_category_relation";
					Long id = this.getId(collection);// data_category_relation_inc
					cdcr.setId(id);
					cdcr.setExFeilds(exFeil);
					cdcr.setTitle(title);
					cdcr.setRid1(Long.valueOf((Integer) exFeil.get("rid1")));
					cdcr.setRid1Title((String) exFeil.get("rid1"));
					cdcr.setRid2(Long.valueOf((Integer) exFeil.get("rid2")));
					cdcr.setRid1Title((String) exFeil.get("rid2"));
					cdcr.setRid3(Long.valueOf((Integer) exFeil.get("rid3")));
					cdcr.setRid1Title((String) exFeil.get("rid3"));
					cdcr.setDataSourceType((Integer) exFeil.get("DataSourceType"));
					cdcr.getCreateStamp();
					mongoTemplate.save(cdcr, collection);
					return ReturnData.SUCCESS;
				} else {
					// 查询到此分类则更改该分类信息
					dataCategoryRelations.setTitle(title);
					dataCategoryRelations.getUpdateStamp();
					mongoTemplate.save(dataCategoryRelations, "data_category_relation");
					return ReturnData.SUCCESS;
				}
			} catch (Exception e) {
				Map emap = new HashMap();
				emap.put("method", "dataCategoryService.updateCategory()");
				emap.put("title", title);
				new LogException(e,emap);
			}

		}

		return ReturnData.FAILURE;

	}

	/**
	 * 获取默认数据
	 * 
	 * @param type
	 * @return
	 */
	public ReturnData getData(Long id, Long brandId) {
		List alllist = new ArrayList();
		try {
			// 一般榜单下获取数据
			if (id != 0) {
				DataCategoryRelation dc = mongoTemplate.findOne(new Query(Criteria.where("id").is(id)),
						DataCategoryRelation.class, "data_category_relation");
				Map map = dc.getParams();
				Long category = null;
				Integer dataSourseType =0;
				dataSourseType = dc.getDataSourceType();
				try {
					category = (Long) map.get("goods_category");
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (dataSourseType == 1) {
					Long searchTime = DateUtils.nowInMillis() / 1000;
					
					return null; // todo 临时
					//return indexService.sendRecommend(searchTime);
				} else if (dataSourseType == 2) {
					// 分类榜单
					List<Long> goodsIdList=new ArrayList();
					if (null  !=category ) {
						goodsIdList = dataCategoryMapper.selectGoodsById(category);
					}else{
						goodsIdList = dataCategoryMapper.selectAllGoodsById();
					}
						for (Long goodsId : goodsIdList) {
							JSONObject obj=getGoodById(goodsId);
							if (obj != null && !obj.isEmpty()) {
								alllist.add(obj);
							}
						}
						return new ReturnData(alllist);
				}//品牌总榜
				else if(dataSourseType==5){
					List<Map> brandList=new ArrayList();
				String matchStr="{$match:{'deleted':0,'hidden':0,'brandId':{$gt:0}}}";
				DBObject match = (DBObject) JSON.parse(matchStr);
				String limitStr="{$limit:10000}";
				DBObject limit = (DBObject) JSON.parse(limitStr);
				String sortStr="{$sort:{'hitNum':-1}}";
				DBObject sort = (DBObject) JSON.parse(sortStr);
				String groupStr="{$group:{'_id':'$brandId','hitSum':{$sum:'$hitNum'}}}";
				DBObject group = (DBObject) JSON.parse(groupStr);
				String sortStr1="{$sort:{'hitSum':-1}}";
				DBObject sort1 = (DBObject) JSON.parse(sortStr1);
				String limitStr1="{$limit:100}";
				DBObject limit1 = (DBObject) JSON.parse(limitStr1);
				String actiontype="entity_goods";
				AggregationOutput output = mongoTemplate.getCollection(actiontype).aggregate(match,limit,sort,group,sort1,limit1);
				System.out.println(output.toString());
				if(null!=output){
	            	for(Iterator<DBObject> it = output.results().iterator(); it.hasNext(); ){
	            		Map brandMap = new HashMap();
	                	BasicDBObject dbo = (BasicDBObject) it.next();
	                	Integer brand = (Integer)dbo.get("_id");
	                	Integer sum=(Integer)dbo.get("hitSum");
	                	GoodsBrand goodsBrand = goodsBrandMapper.findBrandById(brand);
	                	if(null!=goodsBrand && null!=goodsBrand.getDescription()&& null!=goodsBrand.getImgPath()){
	                		brandMap.put("hitNmu", sum);
	                		String img="https://img0.bevol.cn/brand/"+goodsBrand.getImgPath();
	                		brandMap.put("brandId", brand);
		                	brandMap.put("imgPath", img);
		                	brandMap.put("displayName", goodsBrand.getDisplayName());
		                	brandMap.put("aliasName", goodsBrand.getEnName());
		                	brandMap.put("description", goodsBrand.getDescription());
		                	brandList.add(brandMap);
	                	}
	                	
	                }
	            	return new ReturnData(brandList);
	            }
				}else if (dataSourseType == 3) {
					int type = 0;
					long rid3=dc.getRid3();
					switch((int)rid3){
					//type: 肤质类型 0油性 1干性 2敏感性
						case 14 : type=0;
						break;
						case 15 : type=1;
							break;
						case 16 : type=2;
							break;
					}
					int pageSize=200;
					int pager=0;
					ReturnData rd=this.goodsSkinList(type, "goods", pageSize, pager);
					return rd;
				} else if (dataSourseType == 4) {
					List<Long> goodsIdList=new ArrayList();
					// 安全榜单 safety_1_num>4.5
					if(null  !=category){
						goodsIdList = dataCategoryMapper.selectSfetyGoodsById(category);
					}else{
						goodsIdList = dataCategoryMapper.selectAllSfetyGoods();
					}
					JSONObject obj = null;
					for (Long goodsId : goodsIdList) {

						JSONObject obj1=getGoodById(goodsId);
						if (obj1 != null && !obj1.isEmpty()) {
							alllist.add(obj1);
						}
					}
					return new ReturnData(alllist);
				} // 品牌榜单数据查询
			} else if (!brandId.equals("0")) {
				List<Long> brandidList = dataCategoryMapper.selectByBrandId(brandId);
				for (Long bId : brandidList) {
					JSONObject obj1=getGoodById(bId);
					if (obj1 != null && !obj1.isEmpty()) {
						alllist.add(obj1);
					}

				}
				return new ReturnData(alllist);
			}else if(23==id){
				
			}
		} catch (Exception e) {
			Map emap = new HashMap();
			emap.put("method", "dataCategoryService.getData()");
			emap.put("id", id);
			new LogException(e,emap);
		}
		return ReturnData.FAILURE;
	}

	/**
	 * 
	 * 新增/保存实体
	 * 
	 * @param id
	 * @param content
	 * @param listName
	 * @return
	 */
	public ReturnData saveDataCategory(Long id, String content, String listName, Long dataCategoryListsId,
                                       String writerName, Long brandId) {
		JSONArray jsonArray = new JSONArray();
		List<Map> list = new ArrayList();
		if (!content.equals("0")) {
			jsonArray = JSONArray.fromObject(content);
			list = (List) JSONArray.toCollection(jsonArray, Map.class);
			for(int m = 0;m<list.size();m++){
				if(list.get(m)==null ||list.get(m).equals("")){
					list.remove(list.get(m));
				}
				//添加排序标记
				list.get(m).put("sort", m+1);
			}
		}
		Query query = new Query(Criteria.where("id").is(id));
		try {
			DataResults drs = null;
			try {
				drs = mongoTemplate.findOne(query, DataResults.class, "data_results");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null==drs ) {
				String collection = "data_results";
				Long ID = this.getId(collection);// data_results_inc
				DataResults dataResults = new DataResults();
				dataResults.setId(ID);
				if (!listName.equals("0")) {
					dataResults.setListName(listName);
				}
				if(dataCategoryListsId!=0){
					dataResults.setDataCategoryListsId(dataCategoryListsId);
				}
				dataResults.setList(list);
				DataCategoryRelation dataCategoryRelation = new DataCategoryRelation();
				
				if (dataCategoryListsId!=0) {
					dataCategoryRelation = mongoTemplate.findOne(new Query(Criteria.where("id").is(dataCategoryListsId)),
							DataCategoryRelation.class, "data_category_relation");
					dataResults.setDataCategoryRelation(dataCategoryRelation);
				} else if(!brandId.equals("0")){
					long rid1=2;
					dataCategoryRelation.setRid1(rid1);
					dataCategoryRelation.setRid2(brandId);
					String rid2Title = dataCategoryMapper.selectTitleByBrandId(brandId);
					if (rid2Title != null)
						dataCategoryRelation.setRid2Title(rid2Title);
					dataResults.setDataCategoryRelation(dataCategoryRelation);
					
					DataCategoryRelation dataCategory=mongoTemplate.findOne(new Query(Criteria.where("rid1").is(2)),DataCategoryRelation.class,"data_category_relation");
					dataResults.setDataCategoryListsId(dataCategory.getId());
				dataResults.getCreateStamp();
				
				}
				if (!listName.equals("0")) {
					dataResults.setWriterName(writerName);
				}
					
				mongoTemplate.save(dataResults, "data_results");
				return new ReturnData(mongoTemplate.findOne(new Query(Criteria.where("id").is(ID)),
						DataResults.class, "data_results"));
			} else {
				// 更新实体
				if (!listName.equals("0")) {
					drs.setListName(listName);
				}
				drs.getUpdateStamp();
				if (!list.equals("0")) {
					drs.setList(list);
				}
				if (!writerName.equals("0")) {
					drs.setWriterName(writerName);
				}
				mongoTemplate.save(drs, "data_results");
				return ReturnData.SUCCESS;
			}
		} catch (Exception e) {
			Map emap = new HashMap();
			emap.put("method", "dataCategoryService.saveDataCategory()");
			emap.put("content", content);
			emap.put("listName", listName);
			emap.put("id", id);
			emap.put("writerName", writerName);
			emap.put("dataCategoryListsId", dataCategoryListsId);
			new LogException(e,emap);
		}
		return null;
	}

	/**
	 * 发布
	 * 
	 * @param publishTime
	 * @param state
	 * @param id
	 * @return
	 */
	public ReturnData publishresults(Long publishTime, Integer state, Long id, Long endStamp) {
		try {
			Query query = new Query(Criteria.where("id").is(id));
			DataResults dataResults = mongoTemplate.findOne(query, DataResults.class, "data_results");
			dataResults.setPublishTime(publishTime);
			dataResults.setHidden(state);
			dataResults.setEndStamp(endStamp);
			mongoTemplate.save(dataResults, "data_results");
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			Map emap = new HashMap();
			emap.put("method", "dataCategoryService.publishresults()");
			emap.put("publishTime", publishTime);
			emap.put("state", state);
			emap.put("id", id);
			emap.put("endStamp", endStamp);
			new LogException(e,emap);
		}
		return null;
	}

	/**
	 * 获取列表
	 * 
	 * @param id
	 * @param state
	 * @param pager
	 * @return
	 */
	public ReturnListData getResults(Long rid1, Integer pager) {
		int pagerSize = 30;
		int start = (pager - 1) * pagerSize;
		List<Map> allList = new ArrayList();
		List pagerList = new ArrayList();
		Query query = new Query();
		long total = 0;

		/**
		 * Query query=new Query(Criteria.where("rid1").is(rid1)); List
		 * <DataCategoryRelation> li=(List)mongoTemplate.find(query,
		 * DataCategoryRelation.class,"data_category_relation");
		 * for(DataCategoryRelation dc:li){ Long dataCategoryListsId=dc.getId();
		 * List<DataResults> dataResultsList=(List)mongoTemplate.find(new
		 * Query(Criteria.where("dataCategoryListsId").is(dataCategoryListsId)),
		 * DataResults.class,"data_results"); for(DataResults
		 * dr:dataResultsList){ allList.add(dr); } }
		 * 
		 */

		try {
			query.fields().include("publishTime").include("writerName").include("endStamp")
					.include("dataCategoryListsId").include("id").include("createStamp").include("dataCategoryRelation")
					.include("listName");
			query.with(new Sort(Direction.DESC, "createStamp")).with(new Sort(Direction.DESC,"publishTime")).skip(start).limit(pagerSize);
			List<Map> dataResultsList = (List) mongoTemplate.find(query, HashMap.class, "data_results");
			for (int i = 0; i < dataResultsList.size(); i++) {
				DataCategoryRelation dataCategoryRelation = new DataCategoryRelation();

				try {
					dataCategoryRelation = mongoTemplate.findOne(
							new Query(Criteria.where("id").is(dataResultsList.get(i).get("dataCategoryListsId"))),
							DataCategoryRelation.class, "data_category_relation");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (dataCategoryRelation != null) {
					if ((Long) dataCategoryRelation.getRid1() == rid1) {
						allList.add(dataResultsList.get(i));
					}
				}
			}
			Long nowTime = DateUtils.nowInMillis() / 1000;
			Long mostnewTime =0L;
			int closest = -1;
			for (int i = 0; i < allList.size(); i++) {
				
				Long curPublish=(Long)allList.get(i).get("publishTime");
				if(mostnewTime==0)  curPublish=(Long)allList.get(i).get("publishTime");
				if(curPublish!=null){
					if(curPublish>nowTime){
						allList.get(i).put("type", 3);
						allList.get(i).put("typeMsg", "即将发布");
					} else if(mostnewTime<curPublish){
						closest=i;
						mostnewTime=curPublish;
					} else {
						allList.get(i).put("type", 5);
						allList.get(i).put("typeMsg", "已发布");
					}
				} else {
					allList.get(i).put("type", 2);
					allList.get(i).put("typeMsg", "未发布");
				} 
				
			}
			if(closest!=-1){
				allList.get(closest).put("type", 1);
				allList.get(closest).put("typeMsg", "当前显示");

			}
			
			total =mongoTemplate.count(new Query(),long.class ,"data_results");
			return new ReturnListData(allList, total);
		} catch (Exception e) {
			Map emap = new HashMap();
			emap.put("method", "dataCategoryService.getResults()");
			emap.put("rid1", rid1);
			emap.put("pager", pager);
			new LogException(e,emap);

		}
		// query.with(new Sort(Direction.DESC,
		// "publishTime")).skip(start).limit(pagerSize);
		// List<DataResults>
		// pagerResultsList=(List)mongoTemplate.find(query,DataResults.class,"data_results");
		return null;
	}

	/**
	 * 获取单条
	 * 
	 * @param id
	 * @return
	 */
	public ReturnData getResult(Long id) {
		DataResults dataResults = new DataResults();
		try {
			Query query = new Query(Criteria.where("id").is(id));
			dataResults = mongoTemplate.findOne(query, DataResults.class, "data_results");
			return new ReturnData(dataResults);
		} catch (Exception e) {
			Map emap = new HashMap();
			emap.put("method", "dataCategoryService.getResult()");
			emap.put("id", id);
			new LogException(e,emap);
		}
		return null;
	}



	/**
	 * 获取肤质榜单中的产品
	 * @param type: 肤质类型 0油性 1干性 2敏感性
	 * @return
	 */
	public ReturnData goodsSkinList(Integer type, String tname, Integer pageSize, Integer pager) {
		try{
			if(null!=type){
				if (Arrays.binarySearch(types, type + "") == -1) {
	                return new ReturnData(0,"类型不存在");
	            }
				String[] skins= DataCategoryService.skins.get(type).split(",");
				StringBuffer sb=new StringBuffer();
				for(String skin:skins){
					sb.append("'"+skin+"',");
				}
				String skinList=sb.substring(0, sb.length()-1);
				
	            String actionType = "entity_comment_" + tname;
	            /**
	             * db.entity_comment_goods.aggregate([
					{$match:{"skin":{$in:["OSPT","OSPW","OSNW","OSNT","ORPT","ORPW","ORNW","ORNT"]},$or:[{pid:0},{pid:{$exists:false}}],hidden:0,deleted:0}},
					{$group:{_id:"$entityId","avg":{$avg:"$score"}}},{$sort:{avg:-1}},{$limit:200}
					])
	             * 
	             */
	            String groupStr = "{$group:{_id:'$entityId',avg:{$avg:'$score'}}}";
	            DBObject group = (DBObject) JSON.parse(groupStr);
	            String matchStr = "{$match:{'skin':{$in:["+skinList+"]},$or:[{pid:0},{pid:{$exists:false}}],hidden:0,deleted:0}}";
	            DBObject match = (DBObject) JSON.parse(matchStr);
	            
	            String sortStr = "{$sort:{avg:-1}}";
	            DBObject sort = (DBObject) JSON.parse(sortStr);
	            String limitStr = "{$limit:"+pageSize+"}";
	            DBObject limit = (DBObject) JSON.parse(limitStr);
	            
	            String skipStr = "{$skip:"+pager+"}";
	            DBObject skip = (DBObject) JSON.parse(skipStr);
	            //获取实体对象
	            AggregationOutput output = mongoTemplate.getCollection(actionType).aggregate(match,group,sort,skip,limit);
	            System.out.println(output);
	            sb=new StringBuffer();
	            //获取实体id
	            if(null!=output){
	            	for(Iterator<DBObject> it = output.results().iterator(); it.hasNext(); ){
	                	BasicDBObject dbo = (BasicDBObject) it.next();
	                	Long idd = (Long)dbo.get("_id");
	                	sb.append(idd+",");
	                }
	            }
	            if(StringUtils.isBlank(sb)){
	            	return new ReturnData(0,"获取实体失败");
	            }
	            String[] ids=sb.substring(0,sb.length()-1).split(",");
	            List<Long> idsList= new ArrayList<Long>();
	            for(String id:ids){
	            	idsList.add(Long.parseLong(id));
	            }
	            
	            Query query=new Query(Criteria.where("id").in(idsList).and("hidden").is(0).and("deleted").is(0).and("hiddenSkin").ne(10));
	            String[] fields={"title","id","mid","commentNum","image","grade","safety_1_num"};
	            this.setQueryFeilds(query, fields);
	            List<EntityGoods> goodsList=mongoTemplate.find(query, EntityGoods.class,"entity_goods");
	            List allList=new ArrayList();
	            for(int i=0;i<goodsList.size();i++){
	            	JSONObject obj=new JSONObject();
	            	if (StringUtils.isNotBlank(goodsList.get(i).getImage())) {
	            		
	            		obj = JSONObject.fromObject(goodsList.get(i));
	    				// 产品图片路径拼接
	    				String imageSrc = CommonUtils.getImageSrc("goods",goodsList.get(i).getImage());
	    				obj.put("imageSrc", imageSrc);
	    			}
	            	// 有多少人评分
    				long scoreNum = mongoTemplate.count(
    						new Query(Criteria.where("entityId").is(goodsList.get(i).getId()).and("score").gt(0)
    								.and("hidden").is(0).and("deleted").is(0)
    								.orOperator(Criteria.where("pid").is(0),
    										Criteria.where("pid").exists(false))),
    						Comment.class, "entity_comment_goods");
    				obj.put("userCommentNum", scoreNum);
    				obj.put("tname", "goods");
    				allList.add(obj);
	            }
				return new ReturnData(allList);
			}
		}catch(Exception e){
			e.getStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据产品Id获取产品详情
	 * @param goodsId
	 * @return
	 */
	public JSONObject getGoodById(long goodsId){
		JSONObject obj = null;
		try {
			EntityGoods listGoods = mongoTemplate.findOne(
					new Query(Criteria.where("id").is(goodsId)), EntityGoods.class, "entity_goods");
			obj = JSONObject.fromObject(listGoods);
			// 有多少人评分
			long scoreNum = mongoTemplate.count(
					new Query(Criteria.where("entityId").is(listGoods.getId()).and("score").gt(0)
							.and("hidden").is(0).and("deleted").is(0)
							.orOperator(Criteria.where("pid").is(0),
									Criteria.where("pid").exists(false))),
					Comment.class, "entity_comment_goods");
			obj.put("userCommentNum", scoreNum);
			if (StringUtils.isNotBlank(listGoods.getImage())) {
				// 产品图片路径拼接
				String imageSrc = CommonUtils.getImageSrc("goods", listGoods.getImage());
				obj.put("imageSrc", imageSrc);
			}
			obj.put("tname", "goods");
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
