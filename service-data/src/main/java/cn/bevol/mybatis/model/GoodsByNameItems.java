package cn.bevol.mybatis.model;

import java.io.Serializable;

/**
 * Created by Rc. on 2017-05-12.
 */
public class GoodsByNameItems implements Serializable{
    private Long id;
    private String title;
    private String cps ;
    private String alias;
    private String brand;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCps() {
        return cps;
    }

    public void setCps(String cps) {
        this.cps = cps;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
