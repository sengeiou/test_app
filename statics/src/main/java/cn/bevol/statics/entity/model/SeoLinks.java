package cn.bevol.statics.entity.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by mysens on 17-7-19.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class SeoLinks {
    private Integer id;
    private String title;
    private String url;
    private Integer hidden;
    private String createTime;
    private String updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getHidden() {
        return hidden;
    }

    public void setHidden(Integer hidden) {
        this.hidden = hidden;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
