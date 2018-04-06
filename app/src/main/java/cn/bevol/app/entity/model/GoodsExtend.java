package cn.bevol.app.entity.model;

import java.util.Date;

/**
 * Created by Rc. on 2017/2/8.
 * 产品扩展表，用于再次静态化操作
 */
public class GoodsExtend {

    private Integer dataType;
    private Integer num;
    private String mid;
    private Integer state;
    private Integer crdate;
    private Integer tstamp;
    private Date addDate;
    private Date updateDate;


    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCrdate() {
        return crdate;
    }

    public void setCrdate(Integer crdate) {
        this.crdate = crdate;
    }

    public Integer getTstamp() {
        return tstamp;
    }

    public void setTstamp(Integer tstamp) {
        this.tstamp = tstamp;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
