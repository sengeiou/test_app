package cn.bevol.model.vo;

import java.io.Serializable;

/**
 * 用户关系展现
 * @author hualong
 *
 */
public class UserEntityAction implements Serializable{
	
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
	 * 是否评论
	 * 0 没有写过评论
	 * 1 写过评论
	 */
	private Integer isComment=0;
	
	/**
	 * 评论的分数 
	 * todo 3.1之后添加的 
	 */
	private Integer score;
	/**
	 * 评论的id
	 */
	private Long commentId;
	
	/**
	 * 评论状态
	 * 0 没有发过评论
	 * 1 已经评星没有写评论
	 * 2 评星和写评论完成
	 */
	private Integer commentState;
	/**
	 * 是否喜欢
	 */
	private Integer like=0;
	
	/*
	 * 是否收藏
	 */
	private Integer isCollection=0;
	
	/**
	 * 福利社用户参与状态
	 * 0 没有参与
	 * 1参与了 没中奖(参与中?)
	 * 2 参与了  中奖
	 * 3中奖了 没发过该活动的心得
	 * 4中奖了 发过该活动的心得
	 */
	private Integer Apply=0;

	
	
	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Integer getCommentState() {
		return commentState;
	}

	public void setCommentState(Integer commentState) {
		this.commentState = commentState;
	}

	public Integer getIsComment() {
		return isComment;
	}

	public void setIsComment(Integer isComment) {
		this.isComment = isComment;
	}

	public Integer getLike() {
		return like;
	}

	public void setLike(Integer like) {
		this.like = like;
	}

	public Integer getIsCollection() {
		return isCollection;
	}

	public void setIsCollection(Integer isCollection) {
		this.isCollection = isCollection;
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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getApply() {
		return Apply;
	}

	public void setApply(Integer apply) {
		Apply = apply;
	}
	
	
	
}
