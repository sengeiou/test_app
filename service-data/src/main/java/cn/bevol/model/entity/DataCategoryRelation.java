package cn.bevol.model.entity;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.model.Base;

/**
 * 数据类型列表
 * @author 
 *
 */
@Document(collection="data_category_relation")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class DataCategoryRelation extends Base {

	/**
	 * 一级分类id
	 */
	private Long rid1;
	

	private String rid1Title;
	/**
	 * 二级分类id
	 */
	private Long rid2;
	
	private String rid2Title;

	
	/**
	 * 三级分类id
	 */
	private Long rid3;
	
	private String rid3Title;

	/**
	 * 名称
	 */
	private String title;
	

	private Map exFeilds;
	
	/**
	 * 1、为瀑布流
	 * 2、分类榜单：各分类下产品用户评分倒叙排列（评论总星级／一级评论数量），展示200个排序
	 * 3、肤质榜单：对于肤质下产品用户评分倒叙排列（评论总星级／一级评论数量），展示200个排序；
	 * 4、安全榜单：产品安全评分4.5分及以上的产品的用户评分倒叙排列（评论总星级／一级评论数量） ，展示200个排序；
	 */
	private Integer dataSourceType;
	
	/**
	 * {goods_category:1}
	 */
	private Map params;
	
	
	
	public Map getParams() {
		return params;
	}


	public void setParams(Map params) {
		this.params = params;
	}


	public Long getRid1() {
		return rid1;
	}


	public void setRid1(Long rid1) {
		this.rid1 = rid1;
	}


	public Long getRid2() {
		return rid2;
	}


	public void setRid2(Long rid2) {
		this.rid2 = rid2;
	}


	public Long getRid3() {
		return rid3;
	}


	public void setRid3(Long rid3) {
		this.rid3 = rid3;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public Map getExFeilds() {
		return exFeilds;
	}


	public void setExFeilds(Map exFeilds) {
		this.exFeilds = exFeilds;
	}


	public Integer getDataSourceType() {
		return dataSourceType;
	}


	public void setDataSourceType(Integer dataSourceType) {
		this.dataSourceType = dataSourceType;
	}


	public String getRid1Title() {
		return rid1Title;
	}


	public void setRid1Title(String rid1Title) {
		this.rid1Title = rid1Title;
	}


	public String getRid2Title() {
		return rid2Title;
	}


	public void setRid2Title(String rid2Title) {
		this.rid2Title = rid2Title;
	}


	public String getRid3Title() {
		return rid3Title;
	}


	public void setRid3Title(String rid3Title) {
		this.rid3Title = rid3Title;
	}


}
