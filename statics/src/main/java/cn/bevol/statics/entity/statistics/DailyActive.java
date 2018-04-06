package cn.bevol.statics.entity.statistics;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by owen on 16-7-13.
 */
//collection name  daily_active_[date:20160101]
public class DailyActive {
    @Id
    private String _id;

    /**
     * 自增业务id
     */
    @Indexed(unique = true)
    private Long id;
    /**
     * ex:20160707
     */
    @Field
    private Integer date;
    @Field
    private String version;
    @Field
    private String uuid;
    @Field
    private String uid;
    @Field
    private String model;
    @Field
    private String os;
    @Field
    private Integer totalNum;


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

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
}
