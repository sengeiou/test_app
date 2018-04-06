package cn.bevol.internal.entity.vo;

import java.io.Serializable;

/**
 * Created by Rc. on 2017/3/27.
 */
public class AdvertisementVO implements Serializable{
    private Integer state;
    private Integer isAd;
    private Integer isReplace;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getIsAd() {
        return isAd;
    }

    public void setIsAd(Integer isAd) {
        this.isAd = isAd;
    }
}
