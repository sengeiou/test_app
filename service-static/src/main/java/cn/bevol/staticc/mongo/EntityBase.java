package cn.bevol.staticc.mongo;

import cn.bevol.util.ReturnData;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EntityBase extends Base {
    public static ReturnData ENTITY_HIDDEN=new ReturnData(-4,"实体信息不存在");
    public static ReturnData ENTITY_NOT_OPT=new ReturnData(-4,"不允许对内容进行操作");


    /**
     * 喜欢数量 数量用Num结尾
     */
    private Long likeNum=0L;

    /**
     * 不喜欢数量
     */
    private Long notLikeNum=0L;

    /**
     * 收藏数量
     */
    private Long collectionNum=0L;
    /**
     * 评论数量
     */
    private Long commentNum=0L;


    /**
     * 点击数量
     */
    private Long hitNum=0L;

    /**
     * 实体标题
     */
    private String title;

    /**
     * 实体标题
     */
    private String alias;


    /**
     * 实体图片
     */
    private String image;
    /**
     * mid
     */
    private String mid;

    /**
     * 父mid
     */
    private String mPid;

    /**
     * 是否可以评论评论 默认可以 0表示可以评论
     */
    private Integer allowComment;

    /**
     * 访问时间 用于统计记录时间
     */
    private Long vistTime;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }


    public Long getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Long likeNum) {
        this.likeNum = likeNum;
    }

    public Long getNotLikeNum() {
        return notLikeNum;
    }

    public void setNotLikeNum(Long notLikeNum) {
        this.notLikeNum = notLikeNum;
    }

    public Long getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(Long collectionNum) {
        this.collectionNum = collectionNum;
    }

    public Long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getHitNum() {
        return hitNum;
    }

    public void setHitNum(Long hitNum) {
        this.hitNum = hitNum;
    }

    public Integer getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Integer allowComment) {
        this.allowComment = allowComment;
    }

    public Long getVistTime() {
        return vistTime;
    }

    public void setVistTime(Long vistTime) {
        this.vistTime = vistTime;
    }

    public String getmPid() {
        return mPid;
    }

    public void setmPid(String mPid) {
        this.mPid = mPid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}