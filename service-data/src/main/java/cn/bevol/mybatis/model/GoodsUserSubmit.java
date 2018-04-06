package cn.bevol.mybatis.model;

/**
 * Created by Rc. on 2017/2/10.
 * 用户提交的产品
 */
public class GoodsUserSubmit {
    private long id;
    private String imgId;
    private String name;
    private String source;
    private String userId;
    private String compositionIds;
    private String compositionNames;
    private String compositionNo;
    private Integer state;
    private long addDate;
    private long updateDate;
    private long count;
    private String ids;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public long getAddDate() {
        return addDate;
    }

    public void setAddDate(long addDate) {
        this.addDate = addDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getCompositionIds() {
        return compositionIds;
    }

    public void setCompositionIds(String compositionIds) {
        this.compositionIds = compositionIds;
    }

    public String getCompositionNames() {
        return compositionNames;
    }

    public void setCompositionNames(String compositionNames) {
        this.compositionNames = compositionNames;
    }

    public String getCompositionNo() {
        return compositionNo;
    }

    public void setCompositionNo(String compositionNo) {
        this.compositionNo = compositionNo;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
