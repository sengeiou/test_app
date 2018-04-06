package cn.bevol.internal.entity.user;

import cn.bevol.internal.entity.entityAction.Comment;
import cn.bevol.model.entity.EntityBase;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;

/**
 * 评论回复的扩展
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class MsgExtComment extends MsgExt{

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
	 * 实体类型
	 */
	private String tname;

	/**
	 * 产品id
	 */
	private Long entityId;
	
	/**
	 * 产品对比的id
	 */
	private String sid;
	
	/**
	 * 产品对比的mids
	 */
	private String mids;
	
	/**
	 * 产品图片
	 */
	private String image;

	/**
	 * 产品名称
	 */
	private String title;
	
	/**
	 * 评论回复人的id
	 */
	private Long rUserId;

	/**
	 * 发送者昵称
	 */
	private String rNickName;

	/**
	 * 发送者
	 */
	private String rHeadimgurl;
	
	/**
	 * 回复的评论
	 */
	private String rContent;
	
	/**
	 * 回复的图片
	 */
	private String rImage;
	
	/**
	 * 原内容
	 */
	private String oContent;
	
	/**
	 * 原图片
	 */
	private String oImage;
	
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

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
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

	public Long getrUserId() {
		return rUserId;
	}

	public void setrUserId(Long rUserId) {
		this.rUserId = rUserId;
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

	public String getrContent() {
		return rContent;
	}

	public void setrContent(String rContent) {
		this.rContent = rContent;
	}

	public String getrImage() {
		return rImage;
	}

	public void setrImage(String rImage) {
		this.rImage = rImage;
	}

	public String getoContent() {
		return oContent;
	}

	public void setoContent(String oContent) {
		this.oContent = oContent;
	}

	public String getoImage() {
		return oImage;
	}

	public void setoImage(String oImage) {
		this.oImage = oImage;
	}
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	
	/*public static MsgExtComment  createCommentMsg(String tname,Comment cmt,Comment pcmt,EntityBase entity,UserInfo userInfo) {
		// TODO Auto-generated method stub
		MsgExtComment msgExt =new MsgExtComment();
		msgExt.setcMainId(cmt.getMainId());
		msgExt.setcPid(cmt.getPid());
		msgExt.setEntityId(cmt.getEntityId());
		msgExt.setoImage(cmt.getImage());
		msgExt.setcId(cmt.getId());
		msgExt.setrContent(cmt.getContent());
		
		msgExt.setrNickName(userInfo.getNickname());
		msgExt.setrUserId(userInfo.getId());
		if(userInfo.getHeadimgurl()!=null&&userInfo.getHeadimgurl().indexOf("file:///var")!=-1) {
			msgExt.setrHeadimgurl(null);
		} else {
			msgExt.setrHeadimgurl(userInfo.getHeadimgurl());
		}
		
		if(null!=pcmt){
			msgExt.setoContent(pcmt.getContent());
			msgExt.setoImage(pcmt.getImage());	
		}
		
		
		msgExt.setEntityId(entity.getId());
		msgExt.setTitle(entity.getTitle());
		msgExt.setImage(entity.getImage());
		msgExt.setTname(tname);
		msgExt.setMid(entity.getMid());
		return msgExt;
	}*/
	
	public static MsgExtComment  createCommentMsg(String tname, Map cmt, Comment pcmt, EntityBase entity, UserInfo userInfo) {
		// TODO Auto-generated method stub
		MsgExtComment msgExt =new MsgExtComment();
		if(null!=cmt.get("mainId")){
			msgExt.setcMainId(Long.parseLong(String.valueOf(cmt.get("mainId"))));
		}
		msgExt.setcPid(Long.parseLong(String.valueOf(cmt.get("pid"))));
		if(null!=cmt.get("entityId")){
			msgExt.setEntityId(Long.parseLong(String.valueOf(cmt.get("entityId"))));
		}
		if(null!=cmt.get("sid")){
			msgExt.setSid(String.valueOf(cmt.get("sid")));
		}
		if(null!=cmt.get("image")){
			msgExt.setoImage(String.valueOf(cmt.get("image")));
		}
		msgExt.setcId(Long.parseLong(String.valueOf(cmt.get("id"))));
		msgExt.setrContent(String.valueOf(cmt.get("content")));
		
		msgExt.setrNickName(userInfo.getNickname());
		msgExt.setrUserId(userInfo.getId());
		if(userInfo.getHeadimgurl()!=null&&userInfo.getHeadimgurl().indexOf("file:///var")!=-1) {
			msgExt.setrHeadimgurl(null);
		} else {
			msgExt.setrHeadimgurl(userInfo.getHeadimgurl());
		}
		
		if(null!=pcmt){
			msgExt.setoContent(pcmt.getContent());
			msgExt.setoImage(pcmt.getImage());	
		}
		
		
		msgExt.setEntityId(entity.getId());
		msgExt.setTitle(entity.getTitle());
		msgExt.setImage(entity.getImage());
		msgExt.setTname(tname);
		msgExt.setMid(entity.getMid());
		msgExt.setMids(entity.getMids());
		return msgExt;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getMids() {
		return mids;
	}

	public void setMids(String mids) {
		this.mids = mids;
	}

	
}
