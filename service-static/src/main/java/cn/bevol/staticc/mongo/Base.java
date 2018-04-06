package cn.bevol.staticc.mongo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 映射基类
 * @author hualong
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Base implements Serializable {

    @Id
    @JsonIgnore
    private String _id;

    /**
     * 自增业务id
     */
    @Indexed(unique=true)
    private Long id;

    @Field
    @JsonIgnore
    private Integer hidden=0;
    @Field
    @JsonIgnore
    private Integer deleted=0;

    /**
     * 修改时间
     */
    @Field
    private Long updateStamp=new Date().getTime()/1000;
    /**
     * 数据的系统创建时间 统一用createTime
     */
    @Field
    private Long createStamp;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getHidden() {
        return hidden;
    }
    public void setHidden(Integer hidden) {
        this.hidden = hidden;
    }
    public Integer getDeleted() {
        return deleted;
    }
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    public Long getUpdateStamp() {
        return updateStamp;
    }
    public void setUpdateStamp(Long updateStamp) {
        this.updateStamp = updateStamp;
    }
    public Long getCreateStamp() {
        return createStamp;
    }
    public void setCreateStamp(Long createStamp) {
        this.createStamp = createStamp;
    }
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }


}