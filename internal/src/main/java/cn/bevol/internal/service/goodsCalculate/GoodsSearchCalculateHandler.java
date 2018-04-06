package cn.bevol.internal.service.goodsCalculate;

import cn.bevol.internal.entity.model.Composition;
import cn.bevol.internal.entity.model.Goods;
import cn.bevol.internal.entity.vo.Explain;
import cn.bevol.internal.entity.vo.GoodsExplain;
import cn.bevol.internal.service.GoodsService;
import cn.bevol.util.DateUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Administrator  
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsSearchCalculateHandler implements GoodsCalculateI {
	private static Logger logger = LoggerFactory.getLogger(GoodsSearchCalculateHandler.class);

	@Resource
	private GoodsService goodsService;

	private GoodsExplain goodsExplain;
	
	private Goods goods;
	
	private String updateSql;
	
	private String insertSql;
	
	private String insertKeys;
	private String insertValues;

	private String cps;
	
	private String updateSql2;
	
	private String selectSql2;
	
	private List<Map<String,Object>> goodsCategoryList;
	
	private Map<Integer,List<Composition>> ruleCpsMap;
	
	public GoodsSearchCalculateHandler(GoodsExplain goodsExplain, Map<Integer,List<Composition>> cpsMap, List<Map<String,Object>> goodsCategoryListMap) {
		this.goodsExplain = goodsExplain;
		this.goods=goodsExplain.getGoods();
		this.cps=goodsExplain.getGoods().getCps();
		this.ruleCpsMap=cpsMap;
		this.goodsCategoryList=goodsCategoryListMap;
	}
	
	@Override
	public void handler() {  
		createSql();
	}
	
	public void createSql(){
		String newCps="";
		//有pid的 用pid替换id
		if(null!=this.goods && !StringUtils.isBlank(cps) && goodsExplain.getSrcCpsIds().size()>0){
			String[] cpss=cps.split(",");
			List<Long> cpsIdList=goodsExplain.getSrcCpsIds();
			if(null!=cpss && cpss.length>0){
				newCps=StringUtils.join(cpsIdList,",");
			}
		}
		
		String xinji="";
		List<Explain> eList=goodsExplain.getSafety();
		if(null!=eList && eList.size()>0){
			for(Explain explain:eList){
				if(explain.getName().equals("安全星级")){
					xinji=explain.getNum();
				}
			}
		}

		Float safety_1_num= Float.valueOf(xinji);
		String tag_ids="";
		if(null!=goods.getGoodsTagResult()){
			tag_ids=goods.getGoodsTagResult().getTagIds();
		}
		
		//获取p_category
		int pCategory=0;
		for(Map map:goodsCategoryList){
			int category=(Integer)map.get("id");
			if(category==this.goods.getCategory()){
				pCategory=(Integer)map.get("parent_id");
			}
		}


		String ruleCpsIds="";
		//成分分组
		Set<Integer> set=ruleCpsMap.keySet();
		for(Integer id:set){
			List<Composition> cpsList=ruleCpsMap.get(id);
			for(Composition cpss:cpsList){
				//去重
				if((","+newCps+",").contains(","+cpss.getId()+",") && !(","+ruleCpsIds).contains(","+id+",")){
					ruleCpsIds+=id+",";
				}
			}
		}
		
		insertKeys="insert into hq_goods_search(goods_id,cps,cps_search,category,safety_1_num,tag_ids,update_time,rule_cps_ids,p_category)";

		if(!StringUtils.isBlank(ruleCpsIds)){
			ruleCpsIds=ruleCpsIds.substring(0,ruleCpsIds.length()-1);
			this.updateSql="update hq_goods_search set cps='"+this.goods.getCps()+"',cps_search='"+newCps+"',category="+goods.getCategory()+",safety_1_num="+safety_1_num+",tag_ids='"+tag_ids+"',update_time="+DateUtils.nowInMillis()/1000+",rule_cps_ids='"+ruleCpsIds+"',p_category="+pCategory+" where goods_id="+goods.getId();
			insertValues=" values("+goods.getId()+",'"+this.goods.getCps()+"','"+newCps+"',"+goods.getCategory()+","+safety_1_num+",'"+tag_ids+"',"+ DateUtils.nowInMillis()/1000+",'"+ruleCpsIds+"',"+pCategory+")";
		}else{
			ruleCpsIds=null;
			this.updateSql="update hq_goods_search set cps='"+this.goods.getCps()+"',cps_search='"+newCps+"',category="+goods.getCategory()+",safety_1_num="+safety_1_num+",tag_ids='"+tag_ids+"',update_time="+DateUtils.nowInMillis()/1000+",rule_cps_ids="+ruleCpsIds+",p_category="+pCategory+" where goods_id="+goods.getId();
			insertValues=" values("+goods.getId()+",'"+this.goods.getCps()+"','"+newCps+"',"+goods.getCategory()+","+safety_1_num+",'"+tag_ids+"',"+DateUtils.nowInMillis()/1000+","+ruleCpsIds+","+pCategory+")";
		}
		//去除字符串null
		/*if(StringUtils.isBlank(tag_ids)){
			tag_ids=null;
			this.updateSql="update hq_goods_search set cps='"+this.goods.getCps()+"',cps_search='"+newCps+"',category="+goods.getCategory()+",safety_1_num="+safety_1_num+",tag_ids="+tag_ids+",update_time="+DateUtils.nowInMillis()/1000+",rule_cps_ids='"+ruleCpsIds+"',p_category="+pCategory+" where goods_id="+goods.getId();
			insertValues=" values("+goods.getId()+",'"+this.goods.getCps()+"','"+newCps+"',"+goods.getCategory()+","+safety_1_num+","+tag_ids+","+DateUtils.nowInMillis()/1000+",'"+ruleCpsIds+"',"+pCategory+")";
		}*/


		this.updateSql=this.updateSql.replaceAll("''", "null");
		insertValues=insertValues.replaceAll("''", "null");

		insertSql=insertKeys+insertValues;

	}
	
	

	@Override
	public String updaeSql() {
		return this.updateSql;
	}

	@Override
	public String selectSql() {
		return "select * from hq_goods_search where goods_id="+goods.getId();
	}

	@Override
	public String insertSql() {
		// TODO Auto-generated method stub
		return this.insertSql;
	}

	public String getUpdateSql2() {
		return updateSql2;
	}

	public void setUpdateSql2(String updateSql2) {
		this.updateSql2 = updateSql2;
	}

	public String getSelectSql2() {
		return selectSql2;
	}

	public void setSelectSql2(String selectSql2) {
		this.selectSql2 = selectSql2;
	}

	@Override
	public void display(List<Map<String,Object>> maps) {
		
		
	}

	@Override
	public Map entityInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
