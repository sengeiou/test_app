package cn.bevol.statics.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Created by Rc. on 2017/3/22.
 */
@Document(collection = "entity_advertisement")
public class Advertisement implements Serializable {
    @Id
    private  String _id;

    @Indexed(unique = true)
    private Long id;

    @Field("name")
    private Integer name;//广告名称（首页Banner、精彩点评、同肤质最爱...）
    @Field("title")
    private String title;//标题
    @Field("orientation")
    private Integer orientation;//位置
    @Field("little_title")
    private String littleTitle;//小标题
    @Field("entity_id")
    private Integer entityId;//实体ID
    @Field("entity_name")
    private String entityName;
    @Field("banner_type")
    private Integer bannerType;
    @Field("type")
    private String type;//类型（产品的分类）
    @Field("state")
    private Integer state;//状态（0：发布、1：未发布）
    @Field("hidden")
    private Integer hidden;//是否隐藏（0：不隐藏、1：隐藏）
    @Field("is_replace")
    private Integer isReplace;//是替换还是递增
    @Field("img_url")
    private String imgUrl;//图片地址
    @Field("position_type")
    private String  positionType;
    @Field("redirect_type")
    private Integer redirectType;

    @Field("redirect_url")
    private String redirectUrl;
    @Field("classify_id")
    private Integer classifyId;//分类ID（成分ID、发现ID）
    @Field("publish_time")
    private Integer publishTime;//发布时间
    @Field("overdue_time")
    private Integer overdueTime;//结束时间
    @Field("creater")
    private String creater;//创建人
    @Field("updater")
    private String updater;//修改人
    @Field("create_time")
    private Integer createTime;//创建时间
    @Field("update_time")
    private Integer updateTime;//修改时间
    @Field("click_total")
    private  Integer clickTotal;//点击量

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLittleTitle() {
        return littleTitle;
    }

    public void setLittleTitle(String littleTitle) {
        this.littleTitle = littleTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getHidden() {
        return hidden;
    }

    public void setHidden(Integer hidden) {
        this.hidden = hidden;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public Integer getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(Integer redirectType) {
        this.redirectType = redirectType;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Integer getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Integer classifyId) {
        this.classifyId = classifyId;
    }

    public Integer getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Integer publishTime) {
        this.publishTime = publishTime;
    }

    public Integer getOverdueTime() {
        return overdueTime;
    }

    public void setOverdueTime(Integer overdueTime) {
        this.overdueTime = overdueTime;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
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
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getIsReplace() {
        return isReplace;
    }

    public void setIsReplace(Integer isReplace) {
        this.isReplace = isReplace;
    }

    public Integer getBannerType() {
        return bannerType;
    }

    public void setBannerType(Integer bannerType) {
        this.bannerType = bannerType;
    }

    public Integer getClickTotal() {
        return clickTotal;
    }

    public void setClickTotal(Integer clickTotal) {
        this.clickTotal = clickTotal;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
