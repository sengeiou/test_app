package cn.bevol.model.entityAction;

/**
 * 评论
 * @author hualong
 *
 */
@Deprecated
public class SubComment extends EntityActionBase {
	
	/**
 	 * 子评论id
 	 */
	private String subCommentId;
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 用户名称
	 */
	private String nickname;
	
	/**
	 * 图片
	 */
	private String image;

	/**
	 * 如果是二级论id 就填写 subCommentId 
	 * 如果是一级 就不填写
	 */
	private String  subCommentPId;
	
	/**
	 * 
	 * 被回复人nickname 包括 一二级评论人
	 */
	private String nickNameP;
	
	private Long userPid;
	
	
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSubCommentId() {
		return subCommentId;
	}

	public void setSubCommentId(String subCommentId) {
		this.subCommentId = subCommentId;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSubCommentPId() {
		return subCommentPId;
	}

	public void setSubCommentPId(String subCommentPId) {
		this.subCommentPId = subCommentPId;
	}

	public String getNickNameP() {
		return nickNameP;
	}

	public void setNickNameP(String nickNameP) {
		this.nickNameP = nickNameP;
	}

	public Long getUserPid() {
		return userPid;
	}

	public void setUserPid(Long userPid) {
		this.userPid = userPid;
	}
 
  	
}
