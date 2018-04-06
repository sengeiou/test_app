package cn.bevol.internal.service;


import cn.bevol.model.entity.GoodsOfEan;
import cn.bevol.model.entity.RecordOfEan;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import cn.bevol.internal.dao.mapper.GoodsScanOldMapper;
import cn.bevol.internal.dao.mapper.UpcOldMapper;
import cn.bevol.internal.entity.dto.UpcDTO;
import cn.bevol.internal.entity.model.Goods;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.Log.LogMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackGoodsEanService {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private GoodsScanOldMapper goodsScanOldMapper;
	
	@Autowired
	private AliyunService aliyunService;
	
	@Autowired
	private InternalUpcService internalUpcService;
	
	@Autowired
	private UpcOldMapper upcOldMapper;
	
	@Autowired
	private MessageService messageService;
	

	/**
	 * 后台产品审批
	 * @param id
	 * @param isPass 
	 * @return
	 */
	@LogMethod
	public ReturnData CheckGoods(Long id, String message, Integer isPass) {
	
			if(id!=null){
				Update update=new Update();
				update.set("isPass", isPass).set("message", message);
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), update, "entity_goods_ean");
				
				//预备库标记(1为通过    0为不通过)
				Query query=new Query(Criteria.where("id").is(id));
				query.fields().include("source").include("goodsId").include("userId").include("recordId");
				GoodsOfEan goodsOfEan=mongoTemplate.findOne(query,GoodsOfEan.class,"entity_goods_ean");
				Long recordId=goodsOfEan.getRecordId();
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(recordId)), new Update().set("isPass", isPass), RecordOfEan.class);
				if(2==goodsOfEan.getSource() && isPass==1){
					Long goodsId=goodsOfEan.getGoodsId();
				//标记审核通过的产品
					goodsScanOldMapper.updateFields(isPass,goodsId);
				}
				
				//更改记录表isPass
				mongoTemplate.updateFirst(new Query(Criteria.where("eanId").is(id)), new Update().set("isPass", isPass), RecordOfEan.class);
				if(!message.equals(0)){
					long replyUserId= ConfUtils.getResourceNum("mangeUserId");
					String title="";
					String content=message;
					String userId=goodsOfEan.getUserId()+"";
					Integer newType = null ;
					return messageService.sendMsgByXxj(replyUserId,userId,title,content,"","","",newType);
				}
				
				
			
				
			}
	
		
		
		return ReturnData.FAILURE;
	}
	
	/**
	 * 产品审核列表
	 * @param pager
	 * @param isPass 
	 * @param id 
	 * @param source 
	 * @param userId 
	 * @return
	 */
	@LogMethod
	public ReturnData getCheckGoodsList(int pager, Long userId, int source, int id, int isPass) {
		
		
			Map allMap=new HashMap();
			Criteria criteria = Criteria.where("isPass");
			Query query=new Query(criteria);
			
			int pagerSize=30;
			int start = (pager - 1) * pagerSize;
			query.skip(start).limit(pagerSize);
			query.with(new Sort(Direction.DESC,"createStamp"));
			if(id!=0){
				criteria.and("id").is(id);
			}
			if(userId!=0){
				criteria.and("userId").is(userId);
			}
			if(source!=0){
				criteria.and("source").is(source);
			}
			if(isPass!=3){
				if(isPass==0 ||isPass==1){
					criteria.and("isPass").is(isPass);
				}else if(isPass==2){
					criteria.andOperator(criteria.exists(false));
				}
			}
			List<GoodsOfEan> liMap=mongoTemplate.find(query,GoodsOfEan.class);
			long total=mongoTemplate.count(query, "entity_goods_ean");
			allMap.put("goodsList", liMap);
			allMap.put("total", total);
			return new ReturnData(allMap);
		
	}

	/**
	 * 产品入库upc
	 * @param ean
	 * @param goodsId
	 * @param id 
	 * @return
	 */
	public ReturnData goodsToUpc(String ean, Long goodsId, Long id) {
		UpcDTO upcDTO = new UpcDTO();
		ReturnData rd=new ReturnData();
		upcDTO.setGoodsId(goodsId);
		upcDTO.setEan(ean);
		rd=internalUpcService.addUpcRelation(upcDTO);
		//条形码去0
		if(ean.charAt(0)=='0'){
				String str = ean.replaceFirst("^0*", ""); 
				upcOldMapper.addNewUpc(ean,str);
			
		}
		
	 
		
		
		if(rd.getRet()>=0){
			Query query=new Query(Criteria.where("id").is(id).and("isPass").is(1));
			Update update=new Update();
			int isCheck=1;
			update.set("isCheck", isCheck);
			try {
				mongoTemplate.updateFirst(query, update, GoodsOfEan.class);
			} catch (Exception e) {
				return new ReturnData(-5,"产品匹配标记失败");
			}
			return ReturnData.SUCCESS;
		}else{
			return rd;
		}
	}
	
	/**
	 * 
	 * @param isPass 是否通过审核
	 * @param beginStamp 开始时间
	 * @param endStamp 截止时间
	 * @param ean 条形码
	 * @param pager 
	 * @param source 数据源
	 * @param pagerSize 
	 * @return
	 */
	public ReturnData getRecordList(int isPass, Long beginStamp, Long endStamp, String ean, int pager, int source, int pagerSize) {
		Map allMap=new HashMap();
		int start=pagerSize*(pager-1);
		Criteria cr = new Criteria();
		Query query = new Query(cr);
		query.with(new Sort(Direction.DESC,"count"));
		query.skip(start).limit(pagerSize);
		cr.and("pid").is(0);
		if(isPass!=3){
			cr.and("isPass").is(isPass);
		}
		if(beginStamp!=0 && endStamp!=0){
			cr.and("createStamp").gt(beginStamp).lt(endStamp);
		}
	
		if(!ean.equals("0")){
			cr.and("ean").is(ean);
		}
		if(source!=0){
			cr.and("source").is(source);
		}
		List<RecordOfEan> oreList=mongoTemplate.find(query, RecordOfEan.class);
		long total=mongoTemplate.count(query, RecordOfEan.class);
		allMap.put("oreList", oreList);
		allMap.put("total", total);
		
		return new ReturnData(allMap);
	}

	/**
	 * 获取实体
	 * @param goodsId
	 * @param source
	 * @return
	 */
	public ReturnData getInfoById(Long goodsId, int source) {
		List<Map> goodsInfo=new ArrayList();
		if(source==1){
			String appName="goods_search";
			String quertyString="id:"+"'"+goodsId+"'";
			int pager=1;
			int pagerSize=3;
			ReturnListData rlds=aliyunService.openSearch(appName,quertyString,pager,pagerSize);
			goodsInfo=(List<Map>)rlds.getResult();
			for(int j=0;j<goodsInfo.size();j++){
				goodsInfo.get(j).put("imageSrc", CommonUtils.getImageSrc("goods", (String)goodsInfo.get(j).get("image")));
			}
			return new ReturnData(goodsInfo);
		}
		if(source==2){
			Goods goods=goodsScanOldMapper.findGoodsById(goodsId);
			return new ReturnData(goods);
		}
		return null;
	}
}
