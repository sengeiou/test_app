package cn.bevol.app.entity.dto;

/**
 * 首页图
 *
 * @author ruanchen
 */
public class IndexImage extends EntityBase{


    private String key;

    private String value;

    private String type;

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
