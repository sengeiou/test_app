package cn.bevol.mybatis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * 购物地址表
 *
 * @author ruanchen
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ShoppingAddress extends EntityBase{

    private String title;

    private String logo;

    private String url;

    @JsonIgnore
    private Integer hidden=0;
    @JsonIgnore
    private Integer deleted=0;
    @JsonIgnore
    private Long tstamp=new Date().getTime()/1000;
    @JsonIgnore
    private Long crdate=new Date().getTime()/1000;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Long getTstamp() {
        return tstamp;
    }

    public void setTstamp(Long tstamp) {
        this.tstamp = tstamp;
    }

    public Long getCrdate() {
        return crdate;
    }

    public void setCrdate(Long crdate) {
        this.crdate = crdate;
    }
}
