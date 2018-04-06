package cn.bevol.config.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Created by owen on 16-9-1.
 */
@Document(collection="system_conf")
public class ConfData implements Serializable{
    @Id
    @JsonIgnore
    private String _id;


    /**
     * key for  conf  value
     */
    @Indexed(unique = true)
    private String key;

    @Field
    private String value;

    @Field
    private String name;

    @Field
    private Integer updateTime;

    @Field
    private Integer accessNum;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getAccessNum() {
        return accessNum;
    }

    public void setAccessNum(Integer accessNum) {
        this.accessNum = accessNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
