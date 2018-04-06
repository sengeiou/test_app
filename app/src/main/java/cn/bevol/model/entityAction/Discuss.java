package cn.bevol.model.entityAction;

import cn.bevol.app.entity.metadata.UserBaseInfo;
import cn.bevol.model.entityAction.CommentLike;
import cn.bevol.util.response.ReturnData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 评论
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Discuss extends Skin {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7179494007942816446L;

	public static ReturnData ERROR_CONTENT_NOTNULL=new ReturnData(-3,"内容不能为空");
	public static ReturnData UNABLE_UPDATE_ISESSENCE=new ReturnData(2,"精华点评不能修改");
	public static ReturnData REVER_COMMENT=new ReturnData(-2,"您已经评论过了，不要重复提交哦～");

	public static ReturnData HIDDEN_COMMENT=new ReturnData(-4,"上级评论隐藏或者不存在");
	public static ReturnData NOT_ALLOW_COMMENT=new ReturnData(-5,"由于部分用户对该产品存在过多不实评论，目前处于评论禁言状态。多谢谅解！");

	
	public static ReturnData SCORE_GT_0=new ReturnData(-7,"星级需要大于0");
	
	public static ReturnData SCORE_ED=new ReturnData(-8,"不能重复评星");

	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 *显示的3条子评论
	 *
	 *map:id、content、createStamp、num编号
	 *{id:1,content:"2223",createStamp:1},{id:2,conent:"麻酥酥",num:2},{id:2,conent:"麻酥酥",num:5}
	 */
	private List<Map> childs;
	
	
	/**
	 * 引用的所有id
	 */
	private List<Long> rids;

	/**
	 * 上一级的评论id
	 */
	private Long pid;

	 
	/**
	 * 上一级的评论人
	 */
	private Long pUserId;
	

	
 	/**
	 * 点赞的关系
	 */
	@JsonIgnore
	private List<CommentLike> commentLikes=new ArrayList<CommentLike>();

	/**
	 * 点赞的数量
	 */
	private Long likeNum=0L;

	/**
	 * 精华1
	 */
	private Integer isEssence=0;
	
	/**
	 * 排序
	 */
	private Integer sort;
	
	/**
	 * 用户信息
	 */
	 @Transient
	private UserBaseInfo userInfo;

 
	/**
	 * 上级用户信息
	 */
	 @Transient
	private UserBaseInfo pUserInfo;

	/**
	 * 当前用户是否点赞 0没有1点赞
	 */
	@Transient
	private Integer isLike=0;
	

	/**
	 * 0未举报 1举报
	 */
	 @JsonIgnore
	private Integer isJubao=0;

	
	/**
	 * 子评论数量
	 */
	private Long commentNum;
	
	/**
	 * 上层实体id
	 */
	//private String  sid;
 	
	
	
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

 	public Long getPid() {
		return pid;
	}

 
	public Integer getIsJubao() {
		return isJubao;
	}

	public void setIsJubao(Integer isJubao) {
		this.isJubao = isJubao;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Map> getChilds() {
		return childs;
	}

	public void setChilds(List<Map> childs) {
		this.childs = childs;
	}

	public Long getpUserId() {
		return pUserId;
	}

	public void setpUserId(Long pUserId) {
		this.pUserId = pUserId;
	}

	public List<CommentLike> getCommentLikes() {
		return commentLikes;
	}

	public void setCommentLikes(List<CommentLike> commentLikes) {
		this.commentLikes = commentLikes;
	}

	public Long getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Long likeNum) {
		this.likeNum = likeNum;
	}

	public Integer getIsEssence() {
		return isEssence;
	}

	public void setIsEssence(Integer isEssence) {
		this.isEssence = isEssence;
	}

	public Integer getIsLike() {
		return isLike;
	}

	public void setIsLike(Integer isLike) {
		this.isLike = isLike;
	}

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	public List<Long> getRids() {
		return rids;
	}

	public void setRids(List<Long> rids) {
		this.rids = rids;
	}

	public UserBaseInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserBaseInfo userInfo) {
		this.userInfo = userInfo;
	}

	public UserBaseInfo getpUserInfo() {
		return pUserInfo;
	}

	public void setpUserInfo(UserBaseInfo pUserInfo) {
		this.pUserInfo = pUserInfo;
	}

    
  	
}
