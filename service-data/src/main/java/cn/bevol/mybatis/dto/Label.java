package cn.bevol.mybatis.dto;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.util.CommonUtils;

/**
 * 标签
 *
 * @author ruanchen
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Label extends EntityBase{

    private String title;

    private String image;

    private String path;

    private Integer tstamp;

    private Integer sort;

    private Integer top;


    private String tabs;

    private String type;

    private Long createTime;

    private Long updateTime;

    private Integer crdate;
    
    private Integer hidden;

    @Transient
    private String imageSrc;
    
    public String getImageSrc() {
    	imageSrc=CommonUtils.getImageSrc("find", this.getImage());
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getTstamp() {
        return tstamp;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public void setTstamp(Integer tstamp) {
        this.tstamp = tstamp;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getTabs() {
        return tabs;
    }

    public void setTabs(String tabs) {
        this.tabs = tabs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

	public Integer getCrdate() {
        return crdate;
    }

    public void setCrdate(Integer crdate) {
        this.crdate = crdate;
    }

	public Integer getHidden() {
		return hidden;
	}

	public void setHidden(Integer hidden) {
		this.hidden = hidden;
	}

    
    
}
