package cn.bevol.mybatis.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.mybatis.dto.Doyen;


/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GoodsTagResult implements Serializable  {
	
	private Long id;
	
	private Long goodsId;
	
	/**
	 * 自动计算的标签ids
	 */
	private String autoTagIds;
	
	/**
	 * 自动计算的标签名称
	 */
	private String autoTagNames;
	
	/**
	 * 手动调整的ids
	 */
	private String madeTagIds;
	
	/**
	 * 手动调整的names
	 */
	private String madeTagNames;
	
	/**
	 * 正在使用的标签名称
	 */
	private String tagNames;


	/**
	 * 正在使用的
	 */
	private String tagIds;
	
	/**
	 * 创建时间
	 */
	private Long createStamp=new Date().getTime()/1000;
	
	/**
	 * 最近一次的跟新时间
	 */
	private Long updateTime=new Date().getTime()/1000;
	
	/**
	 * 状态
	 */
	private Integer status;
	
	/**
	 * 1表示隐藏
	 */
	private Integer hidden;
	
	/**
	 * 1手动去除所有标签
	 * @return
	 */
	private Integer madeDelete;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getAutoTagIds() {
		return autoTagIds;
	}

	public void setAutoTagIds(String autoTagIds) {
		this.autoTagIds = autoTagIds;
	}

	public String getAutoTagNames() {
		return autoTagNames;
	}

	public void setAutoTagNames(String autoTagNames) {
		this.autoTagNames = autoTagNames;
	}

	public String getMadeTagIds() {
		return madeTagIds;
	}

	public void setMadeTagIds(String madeTagIds) {
		this.madeTagIds = madeTagIds;
	}

	public String getMadeTagNames() {
		return madeTagNames;
	}

	public void setMadeTagNames(String madeTagNames) {
		this.madeTagNames = madeTagNames;
	}

	public String getTagNames() {
		return tagNames;
	}

	public void setTagNames(String tagNames) {
		this.tagNames = tagNames;
	}

	public String getTagIds() {
		return tagIds;
	}

	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
	}

	public Long getCreateStamp() {
		return createStamp;
	}

	public void setCreateStamp(Long createStamp) {
		this.createStamp = createStamp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getHidden() {
		return hidden;
	}

	public void setHidden(Integer hidden) {
		this.hidden = hidden;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getMadeDelete() {
		return madeDelete;
	}

	public void setMadeDelete(Integer madeDelete) {
		this.madeDelete = madeDelete;
	}

	
	
	
}

