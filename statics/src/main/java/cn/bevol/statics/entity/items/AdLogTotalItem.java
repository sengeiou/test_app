package cn.bevol.statics.entity.items;

import java.io.Serializable;

/**
 * Created by Rc. on 2017-06-16.
 */
public class AdLogTotalItem implements Serializable {
    private String adDate;
    private Integer total;

    public String getAdDate() {
        return adDate;
    }

    public void setAdDate(String adDate) {
        this.adDate = adDate;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
