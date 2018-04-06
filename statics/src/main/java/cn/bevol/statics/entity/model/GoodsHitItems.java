package cn.bevol.statics.entity.model;

import java.io.Serializable;

/**
 * Created by Rc. on 2017/4/14.
 */
public class GoodsHitItems implements Serializable {

    private String mid;
    private Integer dateType;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }
}
