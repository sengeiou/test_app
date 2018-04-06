package cn.bevol.model.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.model.Base;
import cn.bevol.util.ReturnData;

/**
 * 用户上传的临时信息
 * @author hualong
 *	护肤流程
 *
 */
@Document(collection="goods_temp_info")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GoodsTempInfo extends Base {
	
	public static ReturnData ERROR_CONTENT_NOTNULL=new ReturnData(3,"内容不能为空");

	private Long userId;
	
	private Long entityId;//实体id
	private String entityTitle;//产品名称
	private String nickName;//用户名
	/**
	 * 护肤流程 actionName=user_skin_protection
	 * 
	 */
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
	
	/**
	 * 扩展字段
	 */
	private Map exField;
	
	
 	public Map getExField() {
		return exField;
	}
	public void setExField(Map exField) {
		this.exField = exField;
	}
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
 	public GoodsTempInfo() {
		
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
	public GoodsTempInfo(Long id,Long userId, String nickName, String actionName, Integer action, Long actionId,
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
	public GoodsTempInfo(Long id,Long userId, String nickName, String actionName, Integer action, Long actionId,
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
 	}
 	
	/**
	 * 初始化护肤流存的模板
	 * @param userId 用户id
	 * @param nickname 用户名称
	 * @param title 标题
	 * @param image2
	 * @param image3
	 */
	public static GoodsTempInfo initSKinFlow(Long userId, String nickname, String title,Long categoryId, String image2, String image3) {
		// TODO Auto-generated method stub
		GoodsTempInfo gti=new GoodsTempInfo();
		gti.setActionName("user_skin_protection");
		gti.setNickName(nickname);
		gti.setEntityTitle(title);
		Map map=new HashMap();
		boolean flag=false;
		if(StringUtils.isNotBlank(image2)) {
			map.put("image2", image2);
			 flag=true;
		}
		if(StringUtils.isNotBlank(image3)) {
			map.put("image3",image3 );
			flag=true;
		}
		map.put("categoryId", categoryId);
		if(flag) {
			gti.setExField(map);
			return  gti;
		}
 		return null;
	}
	
}
