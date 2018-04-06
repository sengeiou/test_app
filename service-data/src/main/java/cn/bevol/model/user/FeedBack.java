package cn.bevol.model.user;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.model.Base;
import cn.bevol.util.ReturnData;

/**
 * 用户反馈
 * @author hualong
 *
 */
@Document(collection="feedback")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class FeedBack extends Base {
	
	public static ReturnData ERROR_CONTENT_NOTNULL=new ReturnData(3,"内容不能为空");

	private Long userId;
	
	private Long entityId;//实体id
	private String entityTitle;//产品名称
	private String nickName;//用户名
	private String actionName;//操作模块名称
	private Integer action;//操作方式
	private Long actionId;//操作目标id
	private Integer actionType;//操作说明
	private String content;//发的内容
	private String images;//abd.jpg,bdd.jpg
	private String replyContent;//回复内容
	private Long replyUserId;//回复人
	private String replyNickName;//回复人

	private Long ownerUserId;//内容主题人
	private String ownerNickName;//内容主题人姓名
	private String source;//信息来源
	
	private String version;//用户app版本
	private String platform;//用户系统
	private String model;//機型
	private String uuid;//uuid
	private String sysV;//系统版本
 
	
	
	private String fields_1;//被举报的内容
	private String fields_2;//扩展
	private String fields_3;//扩展
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public Integer getAction() {
		return action;
	}
	public void setAction(Integer action) {
		this.action = action;
	}
	public Long getActionId() {
		return actionId;
	}
	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}
 	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}
	public Long getOwnerUserId() {
		return ownerUserId;
	}
	public void setOwnerUserId(Long ownerUserId) {
		this.ownerUserId = ownerUserId;
	}
	public String getOwnerNickName() {
		return ownerNickName;
	}
	public void setOwnerNickName(String ownerNickName) {
		this.ownerNickName = ownerNickName;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public Integer getActionType() {
		return actionType;
	}
	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}
	public String getEntityTitle() {
		return entityTitle;
	}
	public void setEntityTitle(String entityTitle) {
		this.entityTitle = entityTitle;
	}
	
	
 	public Long getReplyUserId() {
		return replyUserId;
	}
	public void setReplyUserId(Long replyUserId) {
		this.replyUserId = replyUserId;
	}
	public String getReplyNickName() {
		return replyNickName;
	}
	public void setReplyNickName(String replyNickName) {
		this.replyNickName = replyNickName;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public FeedBack() {
		
	}
	/**
	 * 纠错反馈接口专用
	 * action 为  1或2的时候  纠错反馈接口
	 * @param id
	 * @param action 
	 * @param actiontype
	 * @param actionid
	 * @param content
	 * @param images
	 * @param source
	 * @param version
	 * @param o
	 * @param model
	 * @param uuid
	 */
	public FeedBack(Long id,Long userId, String nickName, String actionName, Integer action, Long actionId,
			Integer ationType, Long entityId, String content, String images, String source,String version,String o,String model,String uuid) {
		super.setId(id);
		this.userId = userId;
		this.entityId = entityId;
		this.nickName = nickName;
		this.actionName = actionName;
		this.action = action;
		this.actionId = actionId;
		this.actionType = ationType;
		this.content = content;
		this.images = images;
		this.source = source;
		this.version=version;
		this.platform=o;
		this.model=model;
		this.uuid=uuid;
	}
	/**
	 * 举报接口专用
	 * @param userId
	 * @param entityId
	 * @param nickName
	 * @param actionName
	 * @param action
	 * @param actionId
	 * @param ationType
	 * @param content
	 * @param images
	 * @param ownerUserId
	 * @param ownerNickName
	 * @param replyContent
	 * @param source
	 * @param version
	 * @param o
	 * @param model
	 * @param uuid
	 */
	public FeedBack(Long id,Long userId, String nickName, String actionName, Integer action, Long actionId,
			Integer ationType, Long entityId, String content, String images, Long ownerUserId, String ownerNickName,
			String replyContent, String source,String version,String o,String model,String uuid,String sysV) {
		super.setId(id);
		this.userId = userId;
		this.entityId = entityId;
		this.nickName = nickName;
		this.actionName = actionName;
		this.action = action;
		this.actionId = actionId;
		this.actionType = ationType;
		this.content = content;
		this.images = images;
		this.ownerUserId = ownerUserId;
		this.ownerNickName = ownerNickName;
		this.replyContent = replyContent;
		this.source = source;
		this.version=version;
		this.platform=o;
		this.model=model;
		this.uuid=uuid;
		this.sysV=sysV;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getSysV() {
		return sysV;
	}
	public void setSysV(String sysV) {
		this.sysV = sysV;
	}
	public String getFields_1() {
		return fields_1;
	}
	public void setFields_1(String fields_1) {
		this.fields_1 = fields_1;
	}
	public String getFields_2() {
		return fields_2;
	}
	public void setFields_2(String fields_2) {
		this.fields_2 = fields_2; 
	}
	public String getFields_3() {
		return fields_3;
	}
	public void setFields_3(String fields_3) {
		this.fields_3 = fields_3;
	}
	
	
}
