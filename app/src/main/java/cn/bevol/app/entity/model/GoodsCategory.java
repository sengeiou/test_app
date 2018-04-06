package cn.bevol.app.entity.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by mysens on 16-12-9.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsCategory {
    String name;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
