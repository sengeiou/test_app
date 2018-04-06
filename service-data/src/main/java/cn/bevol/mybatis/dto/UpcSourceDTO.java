package cn.bevol.mybatis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by mysens on 17-5-26.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UpcSourceDTO {
    private Long id;
    private Long goodsId;
    @JsonIgnore
    private Integer[] idsArr;
    private String ean;
    private String goodsTitle;
    /**
     * 1.接口查询
     * 2.超市数据
     * 3.用户上传
     */
    private Integer source;
    /**
     * 1.未处理
     * 2.匹配成功
     * 3.废弃不用
     */
    private Integer state;
    private String remark;
    private Integer deletedStatus;
    private String createTime;
    private String updateTime;
    private Long userId;

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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
