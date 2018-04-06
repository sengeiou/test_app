package cn.bevol.entity.service.goodsCalculate;

import cn.bevol.mybatis.model.*;
import cn.bevol.model.vo.GoodsExplain;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 标签
 * @author Administrator
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) 
public class GoodsTagResultCalculateHandler implements GoodsCalculateI {
	
	private List<GoodsTag> allGtList;
	
	private List<GoodsTagComposition> allGtcList;
	
	private List<GoodsRule> allGrList;
	
	private GoodsExplain goodsExplain;
	
	private GoodsTagResult goodsTagResult=new GoodsTagResult();
	
	private GoodsTagResult goodsTagResultByGoods=new GoodsTagResult();
	
	private Goods goods;
	
	private String updateSql;
	
	private String insertSql;
	
	private String insertKeys;
	private String insertValues;

	private String selectSql;
	String autoTagIds="";
	String autoTagNames="";
	String categoryIds="8,9,10";
	
	
	public GoodsTagResultCalculateHandler(GoodsExplain goodsExplain,List<GoodsTag> allGtList,List<GoodsTagComposition> allGtcList,List<GoodsRule> allGrList,GoodsTagResult goodsTagResult) {
		this.goodsExplain=goodsExplain;
		this.goods=goodsExplain.getGoods();
		this.allGtList=allGtList;
		this.allGtcList=allGtcList;
		this.allGrList=allGrList;
		this.goodsTagResultByGoods=goodsTagResult;
	}
	
	/**
 	 * 产品名是否符合规则
 	 * @param ids
 	 * @return
 	 */
 	public boolean getTagByGoods(){
		boolean flag=false;
		for(int k=0;k<this.allGrList.size();k++){
			String[] coreWork=new String[]{};
			if(!StringUtils.isBlank(this.allGrList.get(k).getRule1())){
				coreWork=this.allGrList.get(k).getRule1().split(",");
				boolean isCoreWork=false;  
				for(int i=0;!isCoreWork && i<coreWork.length;i++){ 
					//匹配关键词
					if(this.goods.getTitle().contains(coreWork[i])){
						/*String tagNmae="";
						for(GoodsTag gt:allGtList){
							if(gt.getId()==allGrList.get(k).getTagId()){
								tagNmae=gt.getName();
							}
						}*/
						flag=true;
						isCoreWork=true;
						//只在分类为精华、面霜乳液、眼部护理的打抗痘的标签
						if(allGrList.get(k).getTagId()==4 && !(","+categoryIds+",").contains(","+goods.getCategory()+",")){
							continue;
						}
						
						//去重
						if(!(","+autoTagIds).contains(","+allGtList.get(k).getId()+",")){
							this.autoTagIds+=allGtList.get(k).getId()+",";
							//this.autoTagNames+=tagNmae+",";
						}
						
					}
				}
			}else{	//条件一为空
				//判断是否为美白祛斑
				if((this.goods.getDataType()==2 || this.goods.getDataType()==1) && this.goods.getCategory()==14){
					flag=true;
					if(!(","+autoTagIds).contains(",1,")){
						this.autoTagIds+=1+",";
						//this.autoTagNames+="美白祛斑"+",";
					}
				}
			}
		}
 		return flag;
 	}
	
 	/**
 	 * 计算出结果
 	 * @param allGtcList  所有产品标签成分的集合
 	 * @param gtlist
 	 * @return
 	 */
	public void goodsTagResult() {
		String cps = "";
		if (!StringUtils.isBlank(this.goods.getCps())) {
			cps = this.goods.getCps();
		}
		// todo
		// cps+=",16";
		// 判断是否含有 核心成分
		for (int i = 0; i < this.allGtcList.size(); i++) {
			if (this.allGtcList.get(i).getIsMain() == 1) {
				long gtcps = this.allGtcList.get(i).getCompositionId();
				if (("," + cps + ",").contains(("," + gtcps + ","))) {
					long tagId = this.allGtcList.get(i).getTagId();
					// 去重
					if (!("," + autoTagIds).contains("," + tagId + ",")) {
						// 只在分类为精华、面霜乳液、眼部护理的打抗痘的标签
						if (tagId == 4 && !("," + categoryIds + ",").contains("," + goods.getCategory() + ",")) {
							continue;
						}
						// 三号库产品不打美白祛斑标签
						if (tagId == 1 && this.goods.getDataType() == 3) {
							continue;
						}
						/*
						 * //得到标签和标签名 for(GoodsTag gt:this.allGtList){
						 * if((gt.getId()+"").equals(tagId+"")){
						 * autoTagNames+=gt.getName()+",";
						 * autoTagIds+=gt.getId()+","; } }
						 */
						autoTagIds += tagId + ",";

					}
				}
			}

		}

		// 分类中含有抗痘的 打上抗痘的标签
		// 抗痘 tag:2
		if (goods.getCategory() == 16 && !("," + autoTagIds).contains(",2,")) {
			autoTagIds += "2,";
			// autoTagNames+="抗痘,";
		}
		// 是否含有保湿剂
		List<Composition> cpsList = goods.getCompositions();
		boolean isFind = false;
		for (int i = 0; !isFind && i < cpsList.size(); i++) {
			List<Used> usedList = cpsList.get(i).getUseds();
			for (Used ud : usedList) {
				// 只要含有保湿剂就是保湿标签
				if (ud.getId() == 5) {
					autoTagIds += "3";
					// autoTagNames+="保湿";

					isFind = true;
				}
			}
		}

		// 产品有标签
		if (!StringUtils.isBlank(autoTagIds)) {
			if (!isFind) {
				autoTagIds = autoTagIds.substring(0, autoTagIds.length() - 1);
				// autoTagNames=autoTagNames.substring(0,autoTagNames.length()-1);
			}

			// 查询tag_composition所有
			// 1.含有的标签--ismain=1的 +含有成分
			// 2.通过1的标签筛选notcomposition --含有，去除该标签
			// 标签对应的不能包含的成分
			String[] tagIds = autoTagIds.split(",");

			Map<Integer, List<GoodsTagComposition>> map = new HashMap<Integer, List<GoodsTagComposition>>();
			for (int j = 0; j < tagIds.length; j++) {
				List<GoodsTagComposition> tagList = new ArrayList<GoodsTagComposition>();
				for (int i = 0; i < this.allGtcList.size(); i++) {
					// 该产品含有的标签的数据
					if (this.allGtcList.get(i).getTagId() == Integer.parseInt(tagIds[j])) {
						tagList.add(this.allGtcList.get(i));
					}
				}
				map.put(Integer.parseInt(tagIds[j]), tagList);
			}

			String notTag = "";
			// 判断该产品含有的标签是否含有对应的标签不能包含的成分
			for (int j = 0; j < tagIds.length; j++) {
				List<GoodsTagComposition> tagList = map.get(Integer.parseInt(tagIds[j]));
				boolean find = false;
				if (null != tagList && tagList.size() > 0) {
					for (int i = 0; !find && i < tagList.size(); i++) {
						if (null != tagList.get(i).getNotCompositionId() && tagList.get(i).getNotCompositionId() > 0) {
							long notCps = tagList.get(i).getNotCompositionId();
							if (("," + cps + ",").contains(("," + notCps + ","))) {
								// 去重
								find = true;
								notTag += tagList.get(i).getTagId() + ",";
							}
						}
					}
				}
			}

			List<String> tagList = Arrays.asList(tagIds);
			List<String> tagList2 = new ArrayList<String>();
			tagList2.addAll(tagList);
			// 去除不满足条件的标签
			if (StringUtils.isNotBlank(notTag)) {
				String[] notTags = notTag.split(",");
				List<String> notList = Arrays.asList(notTags);

				List<String> notList2 = new ArrayList<String>();
				notList2.addAll(notList);
				// 取差集
				tagList2.removeAll(notList2);
			}

			if (tagList2.size() > 0) {
				autoTagIds = StringUtils.join(tagList2, ",");
				for (GoodsTag gt : this.allGtList) {
					for (int i = 0; i < tagList2.size(); i++) {
						if (gt.getId() == Integer.parseInt(tagList2.get(i))) {
							autoTagNames += gt.getName() + ",";
						}
					}
				}
				autoTagNames = autoTagNames.substring(0, autoTagNames.length() - 1);
			} else {
				autoTagIds = "";
				autoTagNames = "";
			}

			this.goodsTagResult.setAutoTagIds(autoTagIds);
			this.goodsTagResult.setAutoTagNames(autoTagNames);
			this.goodsTagResult.setGoodsId(this.goods.getId());
		} else { // 产品名不符合 且不是保湿 即--没有标签
			this.goodsTagResult = null;
		}
	}
 	
	@Override
	public void handler() {
		//这些分类不打标签
		String categoryIds="42,36,35,34,33,32,26,25,24,23,22";
		if(null!=this.goods && !(","+categoryIds+",").contains(","+goods.getCategory()+",")){
			if(null!=this.goods && !StringUtils.isBlank(this.goods.getTitle())){
				//判断是否手动修改
				madeEditResult();
				//条件1 判断
	 			getTagByGoods();
	 			//条件2 判断
				goodsTagResult();
				//拼接sql语句
				createSql();
			}
		}
	}
	
	
	//判断是否手动修改
	public void madeEditResult(){
		if(null!=goodsTagResultByGoods){
			//手动清除了所有标签  --该产品没有标签
			this.goodsTagResult.setMadeDelete(goodsTagResultByGoods.getMadeDelete());
			if(!StringUtils.isBlank(goodsTagResultByGoods.getMadeTagIds())){
				this.goodsTagResult.setMadeTagIds(goodsTagResultByGoods.getMadeTagIds());
				this.goodsTagResult.setMadeTagNames(goodsTagResultByGoods.getMadeTagNames());
			}
		}
	}
	
	
	/**
	 * 
	 */
	public void  createSql() {
		try {
			//产品有标签才操作
			if(null!=this.goodsTagResult && !StringUtils.isBlank(this.goodsTagResult.getAutoTagIds()) && !StringUtils.isBlank(this.goodsTagResult.getAutoTagNames())){
				//不存在手动修改  当前使用的tag为计算结果
				if(StringUtils.isBlank(goodsTagResult.getMadeTagIds())){
					//手动清空了标签的产品  则没有标签
					if(null==goodsTagResult.getMadeDelete() || goodsTagResult.getMadeDelete()==0){
						this.updateSql="update hq_goods_tag_result set auto_tag_ids='"+this.goodsTagResult.getAutoTagIds()+"' ,"+"auto_tag_names='"+this.goodsTagResult.getAutoTagNames()
						+"' ,"+"tag_ids='"+this.goodsTagResult.getAutoTagIds()+"',"+"tag_names='"+this.goodsTagResult.getAutoTagNames()+"' ,update_time="+this.goodsTagResult.getUpdateTime()+" where goods_id="+this.goods.getId();
					}
				}else{
					goodsTagResult.setTagIds(goodsTagResult.getMadeTagIds());
					goodsTagResult.setTagNames(goodsTagResult.getMadeTagNames());
					this.updateSql="update hq_goods_tag_result set auto_tag_ids='"+this.goodsTagResult.getAutoTagIds()+"' ,"+"auto_tag_names='"+this.goodsTagResult.getAutoTagNames()
					+"' ,"+"tag_ids='"+goodsTagResult.getTagIds()+"',"+"tag_names='"+goodsTagResult.getTagNames()+"' ,update_time="+this.goodsTagResult.getUpdateTime()+" where goods_id="+this.goods.getId();
				}
					
				this.insertKeys="insert into hq_goods_tag_result(goods_id,auto_tag_ids,auto_tag_names,tag_ids,tag_names,create_stamp,update_time)";
				this.insertValues=goods.getId()+", '"+goodsTagResult.getAutoTagIds()+"' , '"+goodsTagResult.getAutoTagNames()+"', '"+
						goodsTagResult.getAutoTagIds()+"' , '"+goodsTagResult.getAutoTagNames()+"' ,"+goodsTagResult.getCreateStamp()+","+goodsTagResult.getCreateStamp();
				this.insertSql=insertKeys+" values("+this.insertValues+")";
				this.selectSql="select * from hq_goods_tag_result where goods_id="+this.goods.getId();
				
				if(StringUtils.isBlank(goodsTagResult.getMadeTagIds())){
					goodsTagResult.setTagIds(goodsTagResult.getAutoTagIds());
					goodsTagResult.setTagNames(goodsTagResult.getAutoTagNames());
				}
				goods.setGoodsTagResult(this.goodsTagResult);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void display(List<Map<String,Object>> maps) {
		
		
	}
	
	@Override
	public String updaeSql() {
		return updateSql;
	}

	@Override
	public String selectSql() {
		return this.selectSql;
	}

	@Override
	public String insertSql() {
		return this.insertSql;
	}

	public GoodsTagResult getGoodsTagResult() {
		return goodsTagResult;
	}

	public void setGoodsTagResult(GoodsTagResult goodsTagResult) {
		this.goodsTagResult = goodsTagResult;
	}

	@Override
	public Map entityInfo() {
		// TODO Auto-generated method stub
		return null;
	}
 }
