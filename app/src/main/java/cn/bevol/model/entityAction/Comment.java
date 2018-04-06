package cn.bevol.model.entityAction;

import cn.bevol.app.entity.metadata.Tag;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.response.ReturnData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 评论
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Comment extends Skin {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7179494007942816446L;

	public static ReturnData ERROR_CONTENT_NOTNULL=new ReturnData(-3,"内容不能为空");
	public static ReturnData UNABLE_UPDATE_ISESSENCE=new ReturnData(2,"您的点评已被设为精华点评,请在意见反馈中联系修修酱修改哦");
	public static ReturnData REVER_COMMENT=new ReturnData(-2,"您已经评论过了，不要重复提交哦～");
	public static ReturnData UNABLE_DELETE_ISESSENCE=new ReturnData(2,"您的点评已被设为精华点评,请在意见反馈中联系修修酱删除哦");
	public static ReturnData HIDDEN_COMMENT=new ReturnData(-4,"上级评论隐藏或者不存在");
	public static ReturnData NOT_ALLOW_COMMENT=new ReturnData(-5,"由于部分用户对该产品存在过多不实评论，目前处于评论禁言状态。多谢谅解！");

	
	public static ReturnData SCORE_GT_0=new ReturnData(-7,"星级需要大于0");
	
	public static ReturnData SCORE_ED=new ReturnData(-8,"不能重复评星");

	/**
	 * 评分
	 */
	private Integer score=0;
	
	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 图片
	 */
	private String image;
	
	/**
	 * 图片源路径
	 */
	private String imgSrc;
	
	/**
	 * app版本号
	 */
	private String appVersion;
	
	private Integer reason;
	
 	/**
	 * 点赞的关系
	 */
	@JsonIgnore
	private List<CommentLike> commentLikes=new ArrayList<CommentLike>();
	
	public void setPid(Long pid) {
		this.pid = pid;
	}


	/**
	 * 点赞的数量
	 */
	private Long likeNum=0L;

	/**
	 * 精华1
	 */
	private Integer isEssence=0;
	
	/**
	 * 用户信息
	 */
	 @Transient
	private UserInfo userInfo;

 
	/**
	 * 上级用户信息
	 */
	 @Transient
	private UserInfo pUserInfo;

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
	  * 一级评论id
	  */
	private Long mainId;
	 
	/**
	 * 一级评论人
	 */
	private Long mainUserId;
	
	/**
	 * 子评论数量
	 */
	private Long commentNum;
	
	/**
	 * 标签
	 */
	private List<Tag> tags;
	
	/**
	 * 3.2新的图片(三张),图片名
	 */
	private String[] images;
	
	/**
	 * 3.2新的图片(三张),完整路径
	 */
	@Transient
	private String[] imagesSrc;
	
 	public Long getPid() {
		return pid;
	}

 
	public Integer getIsJubao() {
		return isJubao;
	}

	public void setIsJubao(Integer isJubao) {
		this.isJubao = isJubao;
	}
	

	/**
	 *评论回复
	 */
	@Deprecated
	private List<SubComment> subComments=new ArrayList<SubComment>();
	
	
	/**
	 *2.5以后的子评论
	 */
	private List<Comment> comments=null;

	/**
	 * 上一级的评论id
	 */
	private Long pid;

	 
	/**
	 * 上一级的评论人
	 */
	private Long pUserId;
	
	private Integer isComment;
	




	/**
	 * 子评论数 每发一片子评论 ++
	 */
	private Integer subCommentNum;

	private HashMap marker;
	
	public Integer getIsLike() {
		return isLike;
	}

	public void setIsLike(Integer isLike) {
		this.isLike = isLike;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public List<CommentLike> getCommentLikes() {
		return commentLikes;
	}

	public void setCommentLikes(List<CommentLike> commentLikes) {
		this.commentLikes = commentLikes;
	}

	public List<SubComment> getSubComments() {
		return subComments;
	}

	public void setSubComments(List<SubComment> subComments) {
		this.subComments = subComments;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public Integer getSubCommentNum() {
		return subCommentNum;
	}

	public void setSubCommentNum(Integer subCommentNum) {
		this.subCommentNum = subCommentNum;
	}

	public String getImage() {
		this.image = CommonUtils.imgReplaceHttp(this.image);
		if(StringUtils.isNotBlank(this.image) && (this.image.indexOf("https")==-1)) {
			this.image= CommonUtils.getImageSrc("comment", this.image);
		}
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Long getMainId() {
		return mainId;
	}

	public void setMainId(Long mainId) {
		this.mainId = mainId;
	}

	public Long getMainUserId() {
		return mainUserId;
	}

	public void setMainUserId(Long mainUserId) {
		this.mainUserId = mainUserId;
	}

	public Long getpUserId() {
		return pUserId;
	}

	public void setpUserId(Long pUserId) {
		this.pUserId = pUserId;
	}

	public UserInfo getpUserInfo() {
		return pUserInfo;
	}

	public void setpUserInfo(UserInfo pUserInfo) {
		this.pUserInfo = pUserInfo;
	}
	
	
	/*
	 * 隐藏前台显示的字段
	 */
	public void setHiddenFeild() {
		this.setDelNullSkin();
		this.setSubComments(null);
	}
	/**
	 * 添加子评论
	 * @param subCms
	 */
	public void addSubComment(List<Comment> subCms) {
		 this.setHiddenFeild();
	   	 for (Comment sub : subCms) {
	   		 //long id=this.getId().longValue();
	   		 if(sub.getMainId()!=null && this.getId().longValue()==sub.getMainId().longValue()) {
	   			 if(this.comments==null) comments=new ArrayList<Comment>();
	   			 this.comments.add(sub);
	   		 }
	   	 }
	}

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}


	public String getImgSrc() {
		this.imgSrc=CommonUtils.getImageSrc("comment", this.getImage());
		return imgSrc;
	}


	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}


	public String getAppVersion() {
		return appVersion;
	}


	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public List<Tag> getTags() {
		return tags;
	}


	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}


	public String[] getImages() {
		return images;
	}


	public void setImages(String[] images) {
		this.images = images;
	}


	public String[] getImagesSrc() {
		//全路径处理
		if(null!=this.images && this.images.length>0){
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<this.images.length;i++){
				if(this.images[i].indexOf("http")==-1){
					String image= CommonUtils.getImageSrc("comment", this.images[i]);
					sb.append(image+",");
				}else{
					sb.append(this.images[i]+",");
				}
				
			}
			if(sb.length()>0){
				String images=sb.substring(0,sb.length()-1);
				if(StringUtils.isNotBlank(images)){
					this.imagesSrc=images.split(",");
				}
			}
		}
		return imagesSrc;
	}


	public void setImagesSrc(String[] imagesSrc) {
		this.imagesSrc = imagesSrc;
	}

	public HashMap getMarker() {
		return marker;
	}

	public void setMarker(HashMap marker) {
		this.marker = marker;
	}


	public Integer getReason() {
		return reason;
	}


	public void setReason(Integer reason) {
		this.reason = reason;
	}
	
	public Integer getIsComment() {
		return isComment;
	}


	public void setIsComment(Integer isComment) {
		this.isComment = isComment;
	}
	
}
