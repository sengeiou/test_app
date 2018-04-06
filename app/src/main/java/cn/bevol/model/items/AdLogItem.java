package cn.bevol.model.items;

import java.io.Serializable;

/**
 * Created by Rc. on 2017-06-15.
 */
public class AdLogItem implements Serializable {
    private Integer AdId;
    private Integer adName;
    private Integer orientation;//位置
    private Integer bannerType;
    private String type;//类型（产品的分类）
    private String positionType;
    private Integer redirectType;
    private String redirectUrl;
    private Integer classifyId;//分类ID（成分ID、发现ID）
    private Integer logTotal;

    public Integer getAdId() {
        return AdId;
    }

    public void setAdId(Integer adId) {
        AdId = adId;
    }

    public Integer getAdName() {
        return adName;
    }

    public void setAdName(Integer adName) {
        this.adName = adName;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer getBannerType() {
        return bannerType;
    }

    public void setBannerType(Integer bannerType) {
        this.bannerType = bannerType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public Integer getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(Integer redirectType) {
        this.redirectType = redirectType;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Integer getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Integer classifyId) {
        this.classifyId = classifyId;
    }

    public Integer getLogTotal() {
        return logTotal;
    }

    public void setLogTotal(Integer logTotal) {
        this.logTotal = logTotal;
    }
}
