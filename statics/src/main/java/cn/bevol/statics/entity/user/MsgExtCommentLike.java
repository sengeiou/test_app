package cn.bevol.statics.entity.user;

import cn.bevol.statics.entity.EntityBase;
import cn.bevol.statics.entity.entityAction.Comment;
import cn.bevol.statics.entity.entityAction.Discuss;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 评论回复的扩展
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class MsgExtCommentLike extends MsgExt{

	/**
	 * 评论id
	 */
	private Long cId;
	/**
	 * 评论的上级id
	 */
	private Long cPid;
	
	/**
	 * 主评论的id
	 */
	private Long cMainId;
	

	/**
	 * 点赞者昵称
	 */
	private String rNickName;

	/**
	 * 点赞者头像
	 */
	private String rHeadimgurl;
	
	private Long rUserId;
	
	/**
	 * 内容
	 */
	private String cContent;
	
	/**
	 * 图片
	 */
	private String cImage;
	
	/**
	 * 实体类型
	 */
	private String tname;

	/**
	 * 产品id
	 */
	private Long entityId;
	
	/**
	 * 产品名称
	 */
	private String title;
	
	/**
	 * v3.3添加
	 * 产品图片
	 */
	private String image;

	/**
	 * 话题实体id
	 */
	private String sid;
	
	private String mids;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

	/**
	 *  mid
	 */
	private String mid;

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public Long getcPid() {
		return cPid;
	}

	public void setcPid(Long cPid) {
		this.cPid = cPid;
	}

	public Long getcMainId() {
		return cMainId;
	}

	public void setcMainId(Long cMainId) {
		this.cMainId = cMainId;
	}

 	public String getrNickName() {
		return rNickName;
	}

	public void setrNickName(String rNickName) {
		this.rNickName = rNickName;
	}

	public String getrHeadimgurl() {
		return rHeadimgurl;
	}

	public void setrHeadimgurl(String rHeadimgurl) {
		this.rHeadimgurl = rHeadimgurl;
	}

 

	public String getcContent() {
		return cContent;
	}

	public void setcContent(String cContent) {
		this.cContent = cContent;
	}

	public String getcImage() {
		return cImage;
	}

	public void setcImage(String cImage) {
		this.cImage = cImage;
	}

	public Long getrUserId() {
		return rUserId;
	}

	public void setrUserId(Long rUserId) {
		this.rUserId = rUserId;
	}

	public static MsgExtCommentLike  createCommentLikeMsg(String tname, Comment cmt, EntityBase entity, UserInfo userInfo) {
		// TODO Auto-generated method stub
		MsgExtCommentLike msgExt =new MsgExtCommentLike();
		msgExt.setcMainId(cmt.getMainId());
		msgExt.setcPid(cmt.getPid());
		msgExt.setEntityId(cmt.getEntityId());
		msgExt.setcId(cmt.getId());
		msgExt.setcPid(cmt.getPid());
		
		msgExt.setrNickName(userInfo.getNickname());
		msgExt.setrUserId(userInfo.getId());
		if(userInfo.getHeadimgurl()!=null&&userInfo.getHeadimgurl().indexOf("file:///var")!=-1) {
			msgExt.setrHeadimgurl(null);
		} else {
			msgExt.setrHeadimgurl(userInfo.getHeadimgurl());
		}
		
		msgExt.setcContent(cmt.getContent());
		msgExt.setcImage(cmt.getImage());
		msgExt.setEntityId(entity.getId());
		msgExt.setTitle(entity.getTitle());
		msgExt.setTname(tname);
		msgExt.setMid(entity.getMid());
		msgExt.setImage(entity.getImage());
		
		return msgExt;
	}
	
	public static MsgExtCommentLike  createDiscussLikeMsg(String tname, Discuss cmt, EntityBase entity, UserInfo userInfo) {
		// TODO Auto-generated method stub
		MsgExtCommentLike msgExt =new MsgExtCommentLike();
		msgExt.setcMainId(cmt.getId());
		msgExt.setcPid(cmt.getPid());
		msgExt.setSid(cmt.getSid());
		msgExt.setcId(cmt.getId());
		msgExt.setcPid(cmt.getPid());
		msgExt.setMids(entity.getMids());
		
		msgExt.setrNickName(userInfo.getNickname());
		msgExt.setrUserId(userInfo.getId());
		if(userInfo.getHeadimgurl()!=null&&userInfo.getHeadimgurl().indexOf("file:///var")!=-1) {
			msgExt.setrHeadimgurl(null);
		} else {
			msgExt.setrHeadimgurl(userInfo.getHeadimgurl());
		}
		
		msgExt.setcContent(cmt.getContent());
		msgExt.setTitle("对比");
		msgExt.setTname(tname);
		return msgExt;
	}

	public String getMids() {
		return mids;
	}

	public void setMids(String mids) {
		this.mids = mids;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}


}
