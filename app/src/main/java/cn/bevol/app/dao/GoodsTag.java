package cn.bevol.app.dao;

import java.io.Serializable;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table hq_goods_tag
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class GoodsTag implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hq_goods_tag.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hq_goods_tag.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hq_goods_tag.create_stamp
     *
     * @mbg.generated
     */
    private Integer createStamp;

    /**
     * Database Column Remarks:
     *   状态 0为显示 1为隐藏
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column hq_goods_tag.status
     *
     * @mbg.generated
     */
    private Boolean status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table hq_goods_tag
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hq_goods_tag.id
     *
     * @return the value of hq_goods_tag.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hq_goods_tag
     *
     * @mbg.generated
     */
    public GoodsTag withId(Integer id) {
        this.setId(id);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hq_goods_tag.id
     *
     * @param id the value for hq_goods_tag.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hq_goods_tag.name
     *
     * @return the value of hq_goods_tag.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hq_goods_tag
     *
     * @mbg.generated
     */
    public GoodsTag withName(String name) {
        this.setName(name);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hq_goods_tag.name
     *
     * @param name the value for hq_goods_tag.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hq_goods_tag.create_stamp
     *
     * @return the value of hq_goods_tag.create_stamp
     *
     * @mbg.generated
     */
    public Integer getCreateStamp() {
        return createStamp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hq_goods_tag
     *
     * @mbg.generated
     */
    public GoodsTag withCreateStamp(Integer createStamp) {
        this.setCreateStamp(createStamp);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hq_goods_tag.create_stamp
     *
     * @param createStamp the value for hq_goods_tag.create_stamp
     *
     * @mbg.generated
     */
    public void setCreateStamp(Integer createStamp) {
        this.createStamp = createStamp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column hq_goods_tag.status
     *
     * @return the value of hq_goods_tag.status
     *
     * @mbg.generated
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hq_goods_tag
     *
     * @mbg.generated
     */
    public GoodsTag withStatus(Boolean status) {
        this.setStatus(status);
        return this;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column hq_goods_tag.status
     *
     * @param status the value for hq_goods_tag.status
     *
     * @mbg.generated
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * This enum was generated by MyBatis Generator.
     * This enum corresponds to the database table hq_goods_tag
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public enum Column {
        id("id"),
        name("name"),
        createStamp("create_stamp"),
        status("status");

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table hq_goods_tag
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private final String column;

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table hq_goods_tag
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String value() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table hq_goods_tag
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String getValue() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table hq_goods_tag
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        Column(String column) {
            this.column = column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table hq_goods_tag
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String desc() {
            return this.column + " DESC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table hq_goods_tag
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String asc() {
            return this.column + " ASC";
        }
    }
}