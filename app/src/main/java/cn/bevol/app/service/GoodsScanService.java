package cn.bevol.app.service;


import cn.bevol.app.dao.mapper.GoodsScanOldMapper;
import cn.bevol.app.dao.mapper.UpcOldMapper;
import cn.bevol.model.entity.GoodsOfEan;
import cn.bevol.model.entity.RecordOfEan;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogMethod;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsScanService extends BaseService{
	@Autowired
	private UpcOldMapper upcMapper;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private GoodsScanOldMapper goodsScanMapper;
	
	@Autowired
	private AliyunService aliyunService;
/**
 * 扫码开关 0为关 1为开
 * 扫码产品
 */
	@LogMethod
	public ReturnData selectGoodsByBarcode (String ean){
			RecordOfEan info=null;
			Map allMap=new HashMap();
			List goodsList=new ArrayList();
			List<Map> goodsInfo=new ArrayList();
			String str = null;
			int listSize=3;
		
			if(ean.equals("0")){
				return null;
			}
			
			//条形码upc库查询产品id
			List<Long> goodsIdList=upcMapper.selectGoods(ean);
			//若为空 去零查询
			if(goodsIdList.size()==0 && ean.charAt(0)=='0'){
				str = ean.replaceFirst("^0*", "");  
				goodsIdList=upcMapper.selectGoodsNewEan(str);
			}
			//String mongoName="entity_goods";
			if(goodsIdList.size()>0){
				String appName="goods_search";
				StringBuffer queryString=new StringBuffer();
				//从正式库查询产品详情
				for(int i=0;i<goodsIdList.size() && i<listSize;i++){
					queryString.append("id:"+"'"+goodsIdList.get(i)+"'"+"OR ");
				}
				String query=queryString.toString();
				query=query.substring(0,query.length()-3);
				int pager=1;
				int pagerSize=3;
				ReturnListData rlds=aliyunService.openSearch(appName,query,pager,pagerSize);
				goodsInfo=(List<Map>)rlds.getResult();
				for(int j=0;j<goodsInfo.size();j++){
					goodsInfo.get(j).put("imageSrc", CommonUtils.getImageSrc("goods", (String)goodsInfo.get(j).get("image")));
				}
					
				
				if(goodsInfo.size()>0){
					allMap.put("goodsInfo",goodsInfo);
					allMap.put("total", goodsInfo.size());
					
					int source=1;//1为正式库 2为预备库
					allMap.put("source", source);
					return new ReturnData(allMap);
				}
			}
			
			//加入扫码记录表中
			String collection="entity_record_ean";
			Long id = this.getId(collection);
			RecordOfEan roe=new RecordOfEan();
			roe.setId(id);
			if(str!=null){
				roe.setEan(str);
			}else{
				roe.setEan(ean);
			}
			allMap.put("recordId", id);
			//条形码存入记录表中
			if(str!=null){
				info=mongoTemplate.findOne(new Query(Criteria.where("ean").is(str)), RecordOfEan.class);
			}else{
				info=mongoTemplate.findOne(new Query(Criteria.where("ean").is(ean)), RecordOfEan.class);
			}
			if(info!=null){
				roe.setPid(id);
				if(str!=null){
					mongoTemplate.findAndModify(new Query(Criteria.where("ean").is(str).and("pid").is(0)), new Update().inc("count", 1), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class, collection);
				}
				else{
					mongoTemplate.findAndModify(new Query(Criteria.where("ean").is(ean).and("pid").is(0)), new Update().inc("count", 1), new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class, collection);
				}
			}if(info==null){
				roe.setPid(0L);
				roe.setCount(1);
			}
			
			
				//从预备库查询数据
			List<Goods> goodsLi=goodsScanMapper.findGoodsByBarcode(ean,listSize);
				
					if(goodsLi.size()>0){
						for(int i=0;i<goodsLi.size();i++){
							goodsIdList.add(goodsLi.get(i).getId());
						}
						roe.setGoodsId(goodsIdList);
						allMap.put("goodsInfo", goodsLi);
						allMap.put("total", goodsLi.size());
						int source=2;
						roe.setSource(source);
						roe.setGoodsId(goodsIdList);
						mongoTemplate.save(roe);
					
					allMap.put("source", source);
					return new ReturnData(allMap);
					}
				
				mongoTemplate.save(roe);
				Map map=new HashMap();
				String msg="查询无此产品";
				int ret=2;
				map.put("recordId", id);
				return new ReturnData(map,ret,msg);
	}

	
	/**
	 * 产品入库
	 * @param eanImg(条形码图片)
	 * @param cpsImg(成分表图片)
	 * @param ean(条形码)
	 * @param goodsImg(产品图片)
	 * @param title(产品名称)
	 * @param alias(产品英文名)
	 * @param userId(用户id)
	 * @param source(数据来源 2为预备库数据 3为用户自行填写)
	 * @param goodsId 预备库产品id
	 * @param recordId
	 * @return
	 */
	@LogMethod
public ReturnData saveToMongo(String eanImg, String ean, String cpsImg, Integer source, String goodsImg, String title, String alias, long userId, Long goodsId, Long recordId) {
		RecordOfEan roe=new RecordOfEan();
		GoodsOfEan goodsOfEan=new GoodsOfEan();
		
		//存入MongoDB
		String collection="entity_goods_ean";
		Long id = this.getId(collection);
		goodsOfEan.setId(id);
		Long createStamp = new Date().getTime() / 1000;
		goodsOfEan.setCreateStamp(createStamp);
		goodsOfEan.setUserId(userId);
		Map infoMap=new HashMap();
		List imgList=new ArrayList();
		Map imgMap=new HashMap();
		if(eanImg!=null){
			imgMap.put("eanImg", eanImg);
		}
		if(cpsImg!=null){
			imgMap.put("cpsImg", cpsImg);
		}
		if(goodsImg!=null){
			imgMap.put("goodsImg", goodsImg);
		}
		infoMap.put("ean", ean);
		infoMap.put("imgs", imgMap);
		infoMap.put("title", title);
		infoMap.put("alias", alias);
		goodsOfEan.setInfo(infoMap);
		goodsOfEan.setSource(source);
		if(source==2){
			goodsOfEan.setTname("goods_prepare");
		}
		//与记录表关联id
		if(recordId!=0){
			Query query=new Query(Criteria.where("id").is(recordId));
			mongoTemplate.updateFirst(query, new Update().set("eanId", id), RecordOfEan.class);
			roe=mongoTemplate.findOne(query, RecordOfEan.class);
			goodsOfEan.setRecordId(roe.getId());
		}
		if(source==2){
			goodsOfEan.setGoodsId(goodsId);
			
		}
		
		mongoTemplate.save(goodsOfEan, collection);
		//将本条记录到entity_record_ean
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(roe.getId())), new Update().set("source", source).set("eanId", id), RecordOfEan.class);
		return ReturnData.SUCCESS;
	}
		
}
