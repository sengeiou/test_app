package cn.bevol.model.entity;

import java.io.Serializable;

public class GoodsBrand implements Serializable{

  private Integer id;
  private String displayName;
  private String cnName;
  private String enName;
  private String aliasName;
  private String capital;
  private Integer sortIndex;
  private String imgPath;
  private String description;
  private String country;
  private String tags;
  private Integer displayStatus;
  private Integer deleteStatus;
  private Integer publishStatus;
  private Integer updateTime;
  private Integer createTime;
  private String other;
  private Integer brandId;
  private Integer allowComment;
  private Integer hiddenSkin;
  private String aliasSearch;
	  
	  public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public String getCapital() {
		return capital;
	}
	public void setCapital(String capital) {
		this.capital = capital;
	}
	public Integer getSortIndex() {
		return sortIndex;
	}
	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTag() {
		return tags;
	}
	public void setTag(String tags) {
		this.tags = tags;
	}
	public Integer getDisplayStatus() {
		return displayStatus;
	}
	public void setDisplayStatus(Integer displayStatus) {
		this.displayStatus = displayStatus;
	}
	public Integer getDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public Integer getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}
	public Integer getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Integer updateTime) {
		this.updateTime = updateTime;
	}
	public String getAliasSearch() {
		return aliasSearch;
	}
	public void setAliasSearch(String aliasSearch) {
		this.aliasSearch = aliasSearch;
	}
	public Integer getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public Integer getBrandId() {
		return brandId;
	}
	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}
	public Integer getAllowComment() {
		return allowComment;
	}
	public void setAllowComment(Integer allowComment) {
		this.allowComment = allowComment;
	}
	public Integer getHiddenSkin() {
		return hiddenSkin;
	}
	public void setHiddenSkin(Integer hiddenSkin) {
		this.hiddenSkin = hiddenSkin;
	}

}
