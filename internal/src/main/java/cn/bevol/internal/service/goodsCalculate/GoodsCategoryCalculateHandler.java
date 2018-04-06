package cn.bevol.internal.service.goodsCalculate;

import cn.bevol.internal.entity.model.Goods;
import cn.bevol.internal.entity.vo.GoodsExplain;
import cn.bevol.internal.service.GoodsService;
import cn.bevol.util.DateUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品分类
 * @author Administrator
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsCategoryCalculateHandler implements GoodsCalculateI {
	private static Logger logger = LoggerFactory.getLogger(GoodsCategoryCalculateHandler.class);

	private GoodsExplain goodsExplain;

	private List<Map<String,Object>> commonListMap;

	private List<Map<String,Object>> specialListMap;

	private List<Map<String,Object>> englishListMap;

	private List<Map<String,Object>> categoryListMap;

	private Goods goods;

	private String updateSql;

	private String insertSql;

	private String insertKeys;
	private String insertValues;

	private String selectSql;


	private String updateSql2;

	private String insertSql2;

	private String insertKeys2;
	private String insertValues2;

	private String selectSql2;

	private String categoryIds="";
	private int num=0;
	private int category;
	private int madeCategory=0;


	public GoodsCategoryCalculateHandler(
			GoodsExplain goodsExplain,
			List<Map<String,Object>> commonListMap,
			List<Map<String,Object>> specialListMap,
			List<Map<String,Object>> englishListMap,
			List<Map<String,Object>> categoryListMap) {
		this.goodsExplain = goodsExplain;
		this.goods=goodsExplain.getGoods();
		this.commonListMap=commonListMap;
		this.specialListMap=specialListMap;
		this.englishListMap=englishListMap;
		this.categoryListMap=categoryListMap;
	}

	@Override
	public void handler() {
		//判断是否有手动分类
		madeCategory();
		//产品分类
		commonGoodsType();
		//产品多种分类类型记录(普通类中)
		polyCategoryCal();
	}

	public void madeCategory(){
		if(null!=categoryListMap && categoryListMap.size()>0){
			for(int i=0;i<categoryListMap.size();i++){
				madeCategory=(Integer)categoryListMap.get(i).get("made_category_id");
			}
		}
	}

	/**
	 * 分类
	 */
	public void commonGoodsType() {
		if(null!=goods){
			String dataType = goods.getDataType() + "";
			if(GoodsService.domesticDataType.contains(dataType)){
				//使用国内规则
				domesticRule();
			}else if(GoodsService.internationalDataType.contains(dataType)){
				//使用国外规则
				internationalRule();
			}

		}

	}

	private void internationalRule(){
		Boolean success = commonRule("include", "exclude", "category_id", englishListMap);

		if(!success){
			//如果关键字分类不成功
			String categoryStr = goods.getCategoryStr();
			if(!StringUtils.isEmpty(goods.getCategoryStr())){
				//通过categoryStr分类
				for(int i=0; i<englishListMap.size(); i++){
					String categoryStrRule = (String) englishListMap.get(i).get("category_str");
					String[] categoryStrRules = categoryStrRule.split(",");
					for(int k=0; k<categoryStrRules.length; k++){
						if(categoryStr.equals(categoryStrRules[k])){
							if (this.madeCategory != 0) {
								this.goods.setCategory(madeCategory);
							} else {
								category = ((Long) englishListMap.get(i).get("category_id")).intValue();
								goods.setCategory(category);
							}
						}
					}
				}
			}
		}
	}

	private void domesticRule(){
		//3号库的categoryStr字段值为空    默认普通类
		if(goods.getDataType()==3){
			goods.setCategoryStr("普通类");
		}
		//category和categoryStr字段存在一个
		if(!StringUtils.isBlank(goods.getCategoryStr()) || goods.getCategory()!=0){
			//普通类
			commonRule("rule_1", "rule_2", "category_id", commonListMap);
			//特殊类
			if(this.goods.getDataType()==1 || this.goods.getDataType()==2){
				if(null!=this.specialListMap && this.specialListMap.size()>0){
					boolean flagg=false;
					boolean isSpe=false;
					//categoryStr不为空  匹配特殊类
					if(!StringUtils.isBlank(this.goods.getCategoryStr())){
						String goodsCategoryStr=this.goods.getCategoryStr();
						for(int i=0;!flagg && i<specialListMap.size();i++){
							String categoryStr=(String)specialListMap.get(i).get("goods_categoryStr");
							int categoryId=(Integer)specialListMap.get(i).get("category_id");
							//抗痘和祛斑 按普通类分
							if(goodsCategoryStr.contains("祛斑类")){
								isSpe=true;
							}
							if(goodsCategoryStr.contains("抗痘类")){
								isSpe=true;
							}


							if(!isSpe && categoryStr.equals(goodsCategoryStr)){
								this.goods.setCategory(categoryId);
								flagg=true;
							}


							if(goodsCategoryStr.contains("防晒类")){
								this.goods.setCategory(13);
								flagg=true;
							}
						}
					}

					if(flagg){
						num=1;
						//存在手动分类 以手动的为准
						if(this.madeCategory!=0){
							this.goods.setCategory(madeCategory);
						}
					}
				}
			}
		}
	}

	/**
	 * 通过关键字分类的通用方法
	 * @param includeField
	 * @param excludeField
	 * @param categoryField
	 */
	private Boolean commonRule(String includeField, String excludeField, String categoryField, List<Map<String,Object>> listMap){
		boolean flag = false;
		for (int k = 0; k < listMap.size(); k++) {
			String include = (String) listMap.get(k).get(includeField);
			String exclude = (String) listMap.get(k).get(excludeField);
			String title = goods.getTitle();
			boolean includeFlag = false;
			boolean excludeFlag = false;
			if (!StringUtils.isBlank(title)) {
				if (!StringUtils.isBlank(include)) {
					String[] includes = include.split(",");
					for (int i = 0; !includeFlag && i < includes.length; i++) {
						if (goods.getTitle().contains(includes[i])) {
							includeFlag = true;
						}
					}
				}
			}
			if (!StringUtils.isBlank(title)) {
				if (!StringUtils.isBlank(exclude)) {
					String[] excludes = exclude.split(",");
					for (int i = 0; !excludeFlag && i < excludes.length; i++) {
						if (goods.getTitle().contains(excludes[i])) {
							excludeFlag = true;
						}
					}
				}
			}
			//满足条件1和2
			if (includeFlag && !excludeFlag) {
				category = (Integer) listMap.get(k).get(categoryField);
				if (!flag) {
					//存在手动分类 以手动的为准
					if (this.madeCategory != 0) {
						this.goods.setCategory(madeCategory);
					} else {
						//产品多种分类  计算第一条
						goods.setCategory(category);
					}
				}
				flag = true;
				categoryIds += category + ",";
				num++;
			}
		}
		return flag;
	}

	public void polyCategoryCal(){
		try{
			//存在手动分类 以手动的为准
			if(this.madeCategory!=0){
				this.goods.setCategory(madeCategory);
			}
			//更新  
			updateSql2="update hq_goods_poly_category set category_id="+goods.getCategory()+",update_time="+DateUtils.nowInMillis()/1000+",exist_category_ids="+0+" where goods_id="+goods.getId();
			//多分类
			if(this.num>1 && !StringUtils.isBlank(categoryIds)){
				categoryIds=categoryIds.substring(0,categoryIds.length()-1);
				updateSql2="update hq_goods_poly_category set category_id="+goods.getCategory()+",category_ids='"+categoryIds+"',update_time="+ DateUtils.nowInMillis()/1000+",exist_category_ids="+1+" where goods_id="+goods.getId();
			}

			boolean isSql=false;
			//一个产品多个分类  存在category_ids
			if(this.num>1 && !StringUtils.isBlank(categoryIds)){
				//categoryIds=categoryIds.substring(0,categoryIds.length()-1);
				insertKeys2="insert into hq_goods_poly_category(goods_id,category_id,category_ids,exist_category_ids,update_time)";
				insertValues2=" values("+this.goods.getId()+","+goods.getCategory()+",'"+categoryIds+"',"+1+","+DateUtils.nowInMillis()/1000+")";
				insertSql2=insertKeys2+insertValues2;
				isSql=true;
			}else if(this.num==1 || this.num==0){  //一个分类或者(不是普通类 也不是特殊类  但是category有值)   不存在category_ids
				insertKeys2="insert into hq_goods_poly_category(goods_id,category_id,exist_category_ids,update_time)";
				insertValues2=" values("+this.goods.getId()+","+goods.getCategory()+","+0+","+DateUtils.nowInMillis()/1000+")";
				insertSql2=insertKeys2+insertValues2;
				isSql=true;
			}else{//不满足条件 不进行分类
				isSql=false;
			}

			if(isSql){
				selectSql2="SELECT * FROM hq_goods_poly_category WHERE goods_id="+this.goods.getId();
			}
		}catch(Exception e){
			logger.error("method:polyCategoryCal arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
		}

	}

	/**
	 * 产品安全处理
	 */
	public void  allGoodsSkin() {}

	@Override
	public String updaeSql() {
		return 	this.updateSql="update hq_goods set category="+this.goods.getCategory()+" where id="+this.goods.getId();

	}

	@Override
	public String selectSql() {
		return 	this.selectSql="SELECT * FROM hq_goods_poly_category WHERE goods_id="+this.goods.getId();
	}

	@Override
	public String insertSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map entityInfo() {
		return null;
	}

	public String getUpdateSql2() {
		return updateSql2;
	}

	public void setUpdateSql2(String updateSql2) {
		this.updateSql2 = updateSql2;
	}

	public String getInsertSql2() {
		return insertSql2;
	}

	public void setInsertSql2(String insertSql2) {
		this.insertSql2 = insertSql2;
	}

	public String getSelectSql2() {
		return selectSql2;
	}

	public void setSelectSql2(String selectSql2) {
		this.selectSql2 = selectSql2;
	}

	@Override
	public void display(List<Map<String,Object>> maps) {
		if(null!=maps && maps.size()>0){
			for(Map map:maps){
				int categoryId=(Integer)map.get("category_id");
				this.goods.setCategory(categoryId);
				this.goodsExplain.setGoods(this.goods);
			}
		}

	}

}
