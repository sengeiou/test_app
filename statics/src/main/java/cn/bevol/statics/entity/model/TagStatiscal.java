package cn.bevol.statics.entity.model;

/**
 * Created by Rc. on 2017/2/14.
 * 标签统计
 */
public class TagStatiscal {
    private Integer id;
    private Integer tagId;
    private String tagName;
    private Integer goodsNum;
    private Integer compositionNum;
    private Integer findNum;
    private Integer reviewNum;
    private Integer listsNum;
    private Integer talkNum;
    private long updateDate;
    private Integer count;
    private String title; //标签名称

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Integer getCompositionNum() {
        return compositionNum;
    }

    public void setCompositionNum(Integer compositionNum) {
        this.compositionNum = compositionNum;
    }

    public Integer getFindNum() {
        return findNum;
    }

    public void setFindNum(Integer findNum) {
        this.findNum = findNum;
    }

    public Integer getReviewNum() {
        return reviewNum;
    }

    public void setReviewNum(Integer reviewNum) {
        this.reviewNum = reviewNum;
    }

    public Integer getListsNum() {
        return listsNum;
    }

    public void setListsNum(Integer listsNum) {
        this.listsNum = listsNum;
    }

    public Integer getTalkNum() {
        return talkNum;
    }

    public void setTalkNum(Integer talkNum) {
        this.talkNum = talkNum;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
