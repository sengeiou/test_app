package cn.bevol.internal.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by mysens on 17-5-25.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class UpcDTO {
    private Long id;
    @JsonIgnore
    private Integer[] idsArr;
    private String ean;
    private Long goodsId;
    private String goodsMid;
    private String goodsTitle="";
    /**
     * 1.正在使用
     * 2.过期不用
     */
    private Integer state;
    private Integer hiddenStatus;
    private Integer deletedStatus;
    private String createTime;
    private String updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer[] getIdsArr() {
        return idsArr;
    }

    public void setIdsArr(Integer[] idsArr) {
        this.idsArr = idsArr;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsMid() {
        return goodsMid;
    }

    public void setGoodsMid(String goodsMid) {
        this.goodsMid = goodsMid;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getHiddenStatus() {
        return hiddenStatus;
    }

    public void setHiddenStatus(Integer hiddenStatus) {
        this.hiddenStatus = hiddenStatus;
    }

    public Integer getDeletedStatus() {
        return deletedStatus;
    }

    public void setDeletedStatus(Integer deletedStatus) {
        this.deletedStatus = deletedStatus;
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

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }
}
