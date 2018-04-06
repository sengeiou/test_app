package cn.bevol.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.mybatis.model.Composition;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CompareGoodsItem implements Serializable {

	
	private String val;
	  
	/**
	 * 1、产品可点击
	 * 2、成分成分可以点击
	 */
	private Integer type; 
	
	private Long typeId;
	
	private String mid;

	private List<Composition> compostion;
	
	private Integer index;
	
	private int unit;
	
	
	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public List<Composition> getCompostion() {
		return compostion;
	}

	public void setCompostion(List<Composition> compostion) {
		this.compostion = compostion;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
	
	
	public void addComposition(Composition e) {
		if(this.compostion==null) this.compostion=new ArrayList();
		this.compostion.add(e);
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}
	
	
}
