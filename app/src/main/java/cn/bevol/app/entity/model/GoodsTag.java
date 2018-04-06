package cn.bevol.app.entity.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsTag implements Serializable  {
	
	private Long id;
	private String name;
	private Long createStamp=new Date().getTime()/1000;
	private Integer status;
	private List<GoodsRule> ruleList;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getCreateStamp() {
		return createStamp;
	}
	public void setCreateStamp(Long createStamp) {
		this.createStamp = createStamp;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List<GoodsRule> getRuleList() {
		return ruleList;
	}
	public void setRuleList(List<GoodsRule> ruleList) {
		this.ruleList = ruleList;
	}
	
}

