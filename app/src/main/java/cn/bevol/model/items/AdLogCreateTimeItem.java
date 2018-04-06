package cn.bevol.model.items;

import java.io.Serializable;

/**
 * Created by Rc. on 2017-06-19.
 */
public class AdLogCreateTimeItem extends AdLogTotalItem implements Serializable {
    private Integer adCreateTime;

    public Integer getAdCreateTime() {
        return adCreateTime;
    }

    public void setAdCreateTime(Integer adCreateTime) {
        this.adCreateTime = adCreateTime;
    }
}
