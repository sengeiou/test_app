package cn.bevol.staticc.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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

    private Integer create_time;

    private Integer update_time;

    private Integer crdate;

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

    public Integer getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Integer create_time) {
        this.create_time = create_time;
    }

    public Integer getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Integer update_time) {
        this.update_time = update_time;
    }

    public Integer getCrdate() {
        return crdate;
    }

    public void setCrdate(Integer crdate) {
        this.crdate = crdate;
    }

}
