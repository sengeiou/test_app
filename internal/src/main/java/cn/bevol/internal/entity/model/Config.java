package cn.bevol.internal.entity.model;

import java.io.Serializable;

/**
 * Created by mysens on 17-6-7.
 */
public class Config implements Serializable {
    private Integer id;
    private String key;
    private String value;
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
