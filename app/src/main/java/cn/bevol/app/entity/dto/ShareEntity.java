package cn.bevol.app.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 分享文案
 * @author chenHaiJian
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ShareEntity extends EntityBase{
	
	private Long id;
	
	/**
	 * 1 : 好友 
	 * 2 : 朋友圈
	 */
	private Integer type;
	
	private Integer hiddenStatus;
	
	private Long createTime;
	
	private Long updateTime;
	
	/**
	 * 实体类型(goods,find...)
	 */
	private String entity;
	
	/**
	 * 文案内容
	 */
	private String content;
	
	/**
	 * 文案标题
	 */
	private String title;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getHiddenStatus() {
		return hiddenStatus;
	}

	public void setHiddenStatus(Integer hiddenStatus) {
		this.hiddenStatus = hiddenStatus;
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

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
