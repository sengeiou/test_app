package cn.bevol.internal.entity.user;

import cn.bevol.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 用户消息关系
 *
 * @author hualong
 */
@Document(collection = "user_message")
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class UserMessage  implements Serializable {
	
	@Id
	@JsonIgnore
	private String _id;

	
	private long id;
	

	/**
     * 消息接受者
     */
    private Long receiverId;


    /**
     * 消息发送者id
     */
    private Long senderId;

    /**
     * 消息关系 对应message 的 _id
     */
    private String messageId;

    /**
     * 业务id
     */
    private Long messageIdInt;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    
	/**
	 * 调整方式
	 * 1 app页面内
	 */
	private String redirectType;
	
	/*
	 * 对应的页面
	 */
	@Deprecated
	private String redirectPage;
	
	/**
	 * 页面对应的参数
	 */
	@Deprecated
	private Map<String,Object> redirectParams;


	/**
     */
   private Integer type=1;//消息类型
   
	/**
     *     定义 见 CommenMeta.MessageStatus类
	 */
   private Integer description=1;

   /**
	 * 1  所有人
    */
   private Integer boundary=1;

   
   /**
     */
    private Integer isRead=0;


	@Field
	@JsonIgnore
	private Integer hidden=0;
	@Field
	@JsonIgnore
	private Integer deleted=0;
	
    /**
     * 修改时间
     */
	@Field
    private Long updateStamp= DateUtils.nowInMillis()/1000;
    /**
     * 数据的系统创建时间 统一用createTime
     */
	@Field
    private Long createStamp=DateUtils.nowInMillis()/1000;
	
	/**
	 * 消息发布时间
	 */
	private Long publishStamp;

 
	private MsgExt msgExt;
	
	
	
	/**
	 * 新的页面跳转参数
	 * 	//{"page":{"h5":["_www/find/info.html","_www/goods/hotinfo.html","_www/goods/topiclist.html"],"android_path":"","ios_path":""},"param":{"type":"0","id":"380"}}
	 */
	private Map<String,Object> newPage;

 
	public Long getMessageIdInt() {
		return messageIdInt;
	}

	public void setMessageIdInt(Long messageIdInt) {
		this.messageIdInt = messageIdInt;
	}

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

 
	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public Long getUpdateStamp() {
		return updateStamp;
	}

	public void setUpdateStamp(Long updateStamp) {
		this.updateStamp = updateStamp;
	}

	public Long getCreateStamp() {
		return createStamp;
	}

	public void setCreateStamp(Long createStamp) {
		this.createStamp = createStamp;
	}

	public Integer getDescription() {
		return description;
	}

	public void setDescription(Integer description) {
		this.description = description;
	}

	public Integer getBoundary() {
		return boundary;
	}

	public void setBoundary(Integer boundary) {
		this.boundary = boundary;
	}

	public MsgExt getMsgExt() {
		return msgExt;
	}

	public void setMsgExt(MsgExt msgExt) {
		this.msgExt = msgExt;
	}



	public String getRedirectType() {
		return redirectType;
	}

	public void setRedirectType(String redirectType) {
		this.redirectType = redirectType;
	}

	public String getRedirectPage() {
		return redirectPage;
	}

	public void setRedirectPage(String redirectPage) {
		this.redirectPage = redirectPage;
	}

	public Map<String, Object> getRedirectParams() {
		return redirectParams;
	}

	public void setRedirectParams(Map<String, Object> redirectParams) {
		this.redirectParams = redirectParams;
	}

	public Long getPublishStamp() {
		if(publishStamp==null) publishStamp=this.getCreateStamp();
		return publishStamp;
	}

	public void setPublishStamp(Long publishStamp) {
		this.publishStamp = publishStamp;
	}

	public Map<String, Object> getNewPage() {
		return newPage;
	}

	public void setNewPage(Map<String, Object> newPage) {
		this.newPage = newPage;
	}

 
    
  }
