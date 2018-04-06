package cn.bevol.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.bevol.model.user.MsgExt;

/**
 * 消息分为2张表 
 * 1、给所有人发送的保存在 systemMessaeg中
 * 2、给部分人发送的保存在 message中
 * @author Administrator
 *
 */
public class BaseMessage extends Base{
	
	/**
	 * 消息发布时间
	 */
	private Long publishStamp;

    /**
    * 标题
    */
   private String title;
   
   /**
    * 内容
    */
   private String content;

	/**
	 * 
    */
   private Integer type=1;//消息类型
   
	/**
	 *   定义 见  CommenMeta.MessageStatus类
    *    
	 */
   private Integer description=1;

   /**
	 * 1  所有人对应systemMessage表
	 * 2 部分人对应message表
    */
   private Integer boundary=1;
   
   /**
    * 消息发送者id
    */
   private Long senderId;
 
   
   /**
    * 保存用户的id 接受者id
    */
	@JsonIgnore
   private List<Long> receiverIds=new ArrayList<Long>();
	
	/**
	 * 发送方式 0 拉  1 推
	 *
	 */
	private Integer sendType=0;
	
	/**
	 * 消息的一些扩展字段
	 */
	private MsgExt msgExt;
	/**
	 * 跳转方式
	 * 1 app页面内
	 */
	private String redirectType;
	
	/*
	 * 对应的页面
	 */
	private String redirectPage;
	
	/**
	 * 页面对应的参数
	 */
	private Map<String,Object> redirectParams;

	
	/**
	 * 新的页面跳转参数
	 * 
	 */
	private Integer newType;
	
	
	
	


	public Integer getNewType() {
		return newType;
	}

	public void setNewType(Integer newType) {
		this.newType = newType;
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

 
	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public List<Long> getReceiverIds() {
		return receiverIds;
	}

	public void setReceiverIds(List<Long> receiverIds) {
		this.receiverIds = receiverIds;
	}

	public Integer getBoundary() {
		return boundary;
	}

	public void setBoundary(Integer boundary) {
		this.boundary = boundary;
	}

	public Integer getDescription() {
		return description;
	}

	public void setDescription(Integer description) {
		this.description = description;
	}

	public Integer getSendType() {
		return sendType;
	}

	public void setSendType(Integer sendType) {
		this.sendType = sendType;
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

 
}
