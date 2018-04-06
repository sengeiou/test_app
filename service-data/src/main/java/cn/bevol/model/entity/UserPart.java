package cn.bevol.model.entity;

import cn.bevol.model.metadata.UserBaseInfo;

import java.util.List;

/**
 * Created by Rc. on 2017/2/21.
 */
public class UserPart {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户基本信息
     */
    private UserBaseInfo userBaseInfo;

    /**
     * 标签信息
     */
    private List<Integer> tags;

    /**
     * 实体信息
     */
    private List<UserPartDetail> userPartDetails;


    private Long pEntityId;

    public UserBaseInfo getUserBaseInfo() {
        return userBaseInfo;
    }


    public void setUserBaseInfo(UserBaseInfo userBaseInfo) {
        this.userBaseInfo = userBaseInfo;
    }


    public List<Integer> getTags() {
        return tags;
    }


    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }


    public Long getpEntityId() {
        return pEntityId;
    }


    public void setpEntityId(Long pEntityId) {
        this.pEntityId = pEntityId;
    }


    public List<UserPartDetail> getUserPartDetails() {
        return userPartDetails;
    }


    public void setUserPartDetails(List<UserPartDetail> userPartDetails) {
        this.userPartDetails = userPartDetails;
    }


    public Long getUserId() {
        return userId;
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
