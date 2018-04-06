package cn.bevol.mybatis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * 产品分类功效关系表
 * @author Administrator
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GoodsEffectUsed implements Serializable{

	private Integer id;
	
	/**
	 * 产品功效分类的id
	 */
 	private Integer categoryCateId;
	
 	
	/**
	 * 产品功效分类的名称
	 */
 	private String categoryCateName;
 	
 	/**
 	 * 对应分类表id
 	 */
 	private String categoryIds;
 	
 	/**
 	 * 对应分类表名称
 	 */
 	private String categoryName;

 	/**
 	 * 使用目的功效分类的id
 	 */ 
 	private Integer effectId;
 	
 	/**
 	 * 使用目的功效分类的名称
 	 */
 	private String effectName;
	
 	/**
 	 * 对应成分表中的使用目的id
 	 */
 	private String cpsUsed;
 	
	
 	/**
 	 * 对应成分表中的使用目的名称
 	 */
 	private String cpsUsedName;
 	
 	/**
 	 * 对应成表中的活性成分
 	 */
 	private String cpsActive;
 	
 	/**
 	 * 对应成表中的活性成分的名称
 	 */
 	private String cpsActiveName;
 	
 	/**
 	 * 特效的父id
 	 */
 	private Integer effectPid;
 	
 	private String effectPidName;
 	
 	/**
 	 * 页面显示的名称
 	 */
 	private String displayName;
 	
 	/**
 	 * 页面显示的对比大成分名称
 	 */
 	private String displayCompareName;
 	
 	private Integer displayCompareSort;
 	
 	/**
 	 * 功效说明
 	 */
 	private String desc;
 	
 	private Integer displayCompare;
 	
 	/**
 	 * 页面显示的类型
 	 */
 	private Integer displayType;
 	
 	/**
 	 * 显示的排序
 	 */
 	private Integer displaySort;

 	private List<Composition> compositions;
 	
 	private List<Long> compositionIds;
 	

 	
 	private List<EffectMetaEntity> effectCategrys=null;
 	
 	public GoodsEffectUsed() {
 		this.initEffectCategrys();
 	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCategoryCateId() {
		return categoryCateId;
	}

	public void setCategoryCateId(Integer categoryCateId) {
		this.categoryCateId = categoryCateId;
	}

	public String getCategoryCateName() {
		return categoryCateName;
	}

	public void setCategoryCateName(String categoryCateName) {
		this.categoryCateName = categoryCateName;
	}

	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getEffectId() {
		return effectId;
	}

	public void setEffectId(Integer effectId) {
		this.effectId = effectId;
	}

	public String getEffectName() {
		return effectName;
	}

	public void setEffectName(String effectName) {
		this.effectName = effectName;
	}

	public String getCpsUsed() {
		return cpsUsed;
	}

	public void setCpsUsed(String cpsUsed) {
		this.cpsUsed = cpsUsed;
	}

	public String getCpsUsedName() {
		return cpsUsedName;
	}

	public void setCpsUsedName(String cpsUsedName) {
		this.cpsUsedName = cpsUsedName;
	}

	public String getCpsActive() {
		return cpsActive;
	}

	public void setCpsActive(String cpsActive) {
		this.cpsActive = cpsActive;
	}

	public String getCpsActiveName() {
		return cpsActiveName;
	}

	public void setCpsActiveName(String cpsActiveName) {
		this.cpsActiveName = cpsActiveName;
	}

	public List<Composition> getCompositions() {
		return compositions;
	}

	public void setCompositions(List<Composition> compositions) {
		this.compositions = compositions;
	}
 	
	public final static String CPS_USED_FEILD="cps_used";
	public final static String CPS_ACTIVE_FEILD="cps_active";

	/**
	 * 用于cps的排除重复
	 */
	private Map<Long,Long> uqcps=new HashMap<Long,Long>();
	/**
	 * 
	 * @param compositions 成分
	 * @param type 成分使用目的 CPS_USED_FEILD/活性成分CPS_ACTIVE_FEILD
	 * @param id 
	 * @return
	 */
	public boolean addCompositon(Composition compositions,String type,String id) {
		for(EffectMetaEntity eme:effectCategrys) {
			//该成分的id(used)在eme里   
			if(eme.getId().equals(id)&&eme.getType().equals(type)) {
				//得到hq_composition的id
				eme.addCompositionid(compositions.getId());
				if(this.compositions==null) this.compositions=new ArrayList();
				if(this.compositionIds==null) this.compositionIds=new ArrayList();

				if(uqcps.get(compositions.getId())==null) {
					this.compositions.add(compositions);
					//保存关系
					this.compositionIds.add(compositions.getId());
					uqcps.put(compositions.getId(), compositions.getId());
					
				}
				
				return true;
			}
		}
		return false;
	}
	
 	

	/**
	 * 初始化功效分类
	 */
	public void initEffectCategrys() {
		//成分使用目的计算
		effectCategrys=new ArrayList<EffectMetaEntity>();
		if(!StringUtils.isBlank(this.getCpsUsed())) {
			//hq_goods_effect_category_used表中cps_used
			String usedids[]=this.getCpsUsed().split(",");
			//hq_goods_effect_category_used--cps_used_name使用目的
			String usedidsName[]=this.getCpsUsedName().split(",");
			for(int i=0;i<usedids.length;i++) {
				//hq_goods_effect_category_used中一个cps_used对应一个cps_used_name
				EffectMetaEntity emn=new EffectMetaEntity(usedids[i],usedidsName[i],CPS_USED_FEILD);
				effectCategrys.add(emn);
			}
		}

		//成分活性成分计算
		if(!StringUtils.isBlank(this.getCpsActive())) {
			String actives[]=this.getCpsActive().split(",");
			String activeNames[]=this.getCpsActiveName().split(",");
			for(int i=0;i<actives.length;i++) {
				EffectMetaEntity emn=new EffectMetaEntity(actives[i],activeNames[i],CPS_ACTIVE_FEILD);
				effectCategrys.add(emn);
			}
		}

	}
	public List<EffectMetaEntity> getEffectCategrys() {
		return effectCategrys;
	}
	public void setEffectCategrys(List<EffectMetaEntity> effectCategrys) {
		this.effectCategrys = effectCategrys;
	}
	public List<Long> getCompositionIds() {
		if(compositionIds==null) compositionIds=new ArrayList<Long>();
		return compositionIds;
	} 
	public void setCompositionIds(List<Long> compositionIds) {
		this.compositionIds = compositionIds;
	}
	public Integer getEffectPid() {
		return effectPid;
	}
	public void setEffectPid(Integer effectPid) {
		this.effectPid = effectPid;
	}
	public String getEffectPidName() {
		return effectPidName;
	}
	public void setEffectPidName(String effectPidName) {
		this.effectPidName = effectPidName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Integer getDisplayType() {
		return displayType;
	}
	public void setDisplayType(Integer displayType) {
		this.displayType = displayType;
	}
	public Integer getDisplaySort() {
		return displaySort;
	}
	public void setDisplaySort(Integer displaySort) {
		this.displaySort = displaySort;
	}
	public String getDisplayCompareName() {
		return displayCompareName;
	}
	public void setDisplayCompareName(String displayCompareName) {
		this.displayCompareName = displayCompareName;
	}
	public Integer getDisplayCompareSort() {
		return displayCompareSort;
	}
	public void setDisplayCompareSort(Integer displayCompareSort) {
		this.displayCompareSort = displayCompareSort;
	}
	public Integer getDisplayCompare() {
		return displayCompare;
	}
	public void setDisplayCompare(Integer displayCompare) {
		this.displayCompare = displayCompare;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
 
	
}
