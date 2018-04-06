package cn.bevol.mybatis.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 分享
 *
 * @author ruanchen
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Share extends EntityBase{


    private String type;

    private String text;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
