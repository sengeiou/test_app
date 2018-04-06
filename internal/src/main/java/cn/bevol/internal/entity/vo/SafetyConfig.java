package cn.bevol.internal.entity.vo;

import cn.bevol.internal.entity.model.Composition;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class SafetyConfig {

	private Integer id;

	private String name;
	
	
	/**
	 * 使用目的
	 */
	private String cpsUsed;
	
	/**
	 * 高风险成分 
	 */
	private String highSafety;
	/**
	 * 孕妇慎用
	 */
	private String shenyong;

	/**
	 * 安全解读
	 */
	private String desc;
	
	//成分关系
	private List<Long> compositionIds;
	private List<Composition> composition;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCpsUsed() {
		return cpsUsed;
	}

	public void setCpsUsed(String cpsUsed) {
		this.cpsUsed = cpsUsed;
	}

 	public String getShenyong() {
		return shenyong;
	}

	public void setShenyong(String shenyong) {
		this.shenyong = shenyong;
	}

	public List<Long> getCompositionIds() {
		return compositionIds;
	}

	public void setCompositionIds(List<Long> compositionIds) {
		this.compositionIds = compositionIds;
	}
	public void addCompositionId(Long compositionId) {
		if(this.compositionIds==null) compositionIds=new ArrayList<Long>();
		this.compositionIds.add(compositionId);
	}
	public void addComposition(Composition c) {
		if(this.composition==null) composition=new ArrayList<Composition>();
		Composition cn=new Composition();
		cn.copyCompositon(c);
		if(c.getUseds()!=null&&c.getUseds().size()>0) {
			cn.setCurUsedName(c.getUseds().get(0).getTitle());
		}
		this.composition.add(cn);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHighSafety() {
		return highSafety;
	}

	public void setHighSafety(String highSafety) {
		this.highSafety = highSafety;
	}

	public List<Composition> getComposition() {
		return composition;
	}

	public void setComposition(List<Composition> composition) {
		this.composition = composition;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	
}
