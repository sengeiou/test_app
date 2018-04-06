package cn.bevol.internal.entity.metadata;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataStatistics  implements Serializable {

	
	/**
	 * 评论数量
	 */
	private Long commentNum=0L;
	
	
	/**
	 * 点击数量
	 */
	private Long hitNum=0L;
	
	/**
	 * 喜欢数量 数量用Num结尾
	 */
	private Long likeNum=0L;

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	public Long getHitNum() {
		return hitNum;
	}

	public void setHitNum(Long hitNum) {
		this.hitNum = hitNum;
	}

	public Long getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Long likeNum) {
		this.likeNum = likeNum;
	}
	
	

}
