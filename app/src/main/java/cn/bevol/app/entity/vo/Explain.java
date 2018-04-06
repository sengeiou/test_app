package cn.bevol.app.entity.vo;

import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.entity.model.Used;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Explain implements Serializable{
	
	private Integer id;
 
	//大分类名
	private String name;
	
	//成分关系
	private List<Long> compositionIds;
	
	//成分详细
	private List<Composition> composition;

	//显示的总chenf
	private String num;
	
	public static Map maxNum=new HashMap();
	static{
		maxNum.put(1, "5");
		maxNum.put(2, "6");
		maxNum.put(3, "6");
		maxNum.put(4, "6");
		maxNum.put(5, "6");
	}
	
	/**
	 * 百分比
	 */
	private String percent;
	
	/**
	 * 0 单位 种
	 * 1 表示星级
	 */
	private int unit;

	private Integer effectId;
	
 	/**
 	 * 功效的父id
 	 */
 	private Integer effectPid;
 	
 	private String effectPidName;
 	
 	/**
 	 * 页面显示的名称
 	 */
 	private String displayName;
 	
 	
 	/**
 	 * 对比页面显示名称 
 	 */
 	private String compareName;

 	/** 
 	 * 页面显示的类型
 	 */
 	private Integer displayType;
 	
 	/**
 	 * 显示的排序
 	 */
 	private Integer displaySort;
 	
 	//用于运算的索引
 	private Integer index;
 	
 	/**
 	 * 页面显示的对比大成分名称
 	 */
 	private String displayCompareName;
 	
 	private Integer displayCompareSort;
 	
 	private Integer displayCompare;
 	
 	/**
	 * 用户与产品肤质匹配关系
	 */
	private int skinMatchNum;
	private int skinNotMatchNum;
	private int skinMatchIsexist;
	private int skinNotMatchIsexist;
	private String skinMatchCps="";
	private String skinNotMatchCps;

	/**
	 * 全成分表解读
	 */
	private String desc;
	

 	/**
 	 * 对应成分表中的使用目的id
 	 */
 	private String cpsUsed;
 	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getCompositionIds() {
		if(compositionIds==null) compositionIds=new ArrayList();
		return compositionIds;
	}

	public void setCompositionIds(List<Long> compositionIds) {
		this.compositionIds = compositionIds;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}
	

	public void addCompositionId(Long compositionId) {
		if(this.compositionIds==null) this.compositionIds=new ArrayList<Long>();
		this.compositionIds.add(compositionId);
	}
	
	/**
	 * 添加成分
	 * @param c
	 */
	public void addComposition(Composition c) {
		if(this.composition==null) this.composition=new ArrayList<Composition>();
		this.composition.add(c);
	}


	public String getNum() {
		//非星级的运算
		if(this.unit==0) {
			if(this.composition!=null) this.num=this.composition.size()+""; else this.num="0";
		}
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getEffectId() {
		return effectId;
	}

	public void setEffectId(Integer effectId) {
		this.effectId = effectId;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getCompareName() {
		return compareName;
	}

	public void setCompareName(String compareName) {
		this.compareName = compareName;
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

	public String getCpsUsed() {
		return cpsUsed;
	}

	public void setCpsUsed(String cpsUsed) {
		this.cpsUsed = cpsUsed;
	}

	public List<Composition> getComposition() {
		return composition;
	}

	public void setComposition(List<Composition> composition) {
		this.composition = composition;
	}

	
	/**
	 * 使用目的计算
	 * @param cpsUsed2
	 * @param list 
	 */
	public void cpsUsedAs(String cpsUsed2, List<Composition> list) {
		String cpss[]=null;
		if(!StringUtils.isBlank(cpsUsed2))
			cpss=cpsUsed2.split(",");
		// TODO Auto-generated method stub
		if(this.composition==null) this.composition=new ArrayList<Composition>();
		
		if(list!=null)
		for(Composition c:list) {
			for(int i=0;i<this.getCompositionIds().size();i++) {
				if(this.getCompositionIds().get(i).longValue()==c.getId()) {
					Composition v=new Composition();
					v.copyCompositon(c);
					String curUsedName="";
					if(cpss!=null) {
						for(int j=0;j<cpss.length;j++) {
							for(Used ud:v.getUseds()) {
								if(cpss[j]!=null&&ud.getId()==Integer.parseInt(cpss[j])) {
									curUsedName=ud.getTitle();
									break;
								}
							}
						}
					}
					v.setCurUsedName(curUsedName);
					//使用目的计算
					this.composition.add(v); 
				}
			}
		}
	}

	public void setSkinMatchNum(int skinMatchNum) {
		this.skinMatchNum = skinMatchNum;
	}

	public void setSkinNotMatchNum(int skinNotMatchNum) {
		this.skinNotMatchNum = skinNotMatchNum;
	}

	public void setSkinMatchIsexist(int skinMatchIsexist) {
		this.skinMatchIsexist = skinMatchIsexist;
	}

	public void setSkinNotMatchIsexist(int skinNotMatchIsexist) {
		this.skinNotMatchIsexist = skinNotMatchIsexist;
	}

	public void setSkinMatchCps(String skinMatchCps) {
		this.skinMatchCps = skinMatchCps;
	}

	public void setSkinNotMatchCps(String skinNotMatchCps) {
		this.skinNotMatchCps = skinNotMatchCps;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	
	
}
