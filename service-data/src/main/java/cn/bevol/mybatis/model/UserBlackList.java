package cn.bevol.mybatis.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * 用户黑名单 禁言
 * @author chenHaiJian
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UserBlackList implements Serializable  {
	private Long id;
	
	private Long userId;
	
	private Long createTime;
	
	private Long updateTime;
	
	/**
	 * 禁言结束时间
	 */
	private Long endTime;
	
	/**
	 * 禁言开始时间
	 */
	private Long startTime;
	
	/**
	 * 禁言类型:1永久 2时效性
	 */
	private Integer state;
	
	/**
	 * 禁言原因描述
	 */
	private String description;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}
