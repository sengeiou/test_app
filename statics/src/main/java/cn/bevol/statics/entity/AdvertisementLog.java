package cn.bevol.statics.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Created by Rc. on 2017/3/22.
 */
@Document(collection = "entity_advertisement_log")
public class AdvertisementLog implements Serializable {
    @Id
    private  String _id;
    @Indexed(unique = true)
    private Long id;
    @Field("ad_id")
    private Integer adId;
    @Field("ad_date")
    private String adDate;
    @Field("hour")
    private Integer hour;
    @Field("total")
    private Integer total;
    @Field("position_type")
    private String  positionType;
    @Field("create_time")
    private Integer createTime;
    @Field("update_time")
    private Integer updateTime;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }


    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getAdDate() {
        return adDate;
    }

    public void setAdDate(String adDate) {
        this.adDate = adDate;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }
}
