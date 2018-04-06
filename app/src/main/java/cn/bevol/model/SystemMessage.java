package cn.bevol.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 系统消息
 */
@Document(collection = "system_message")
public class SystemMessage extends BaseMessage {

 
 
    /**
     * 发布状态
     * 0停止发布   用户初始化相关数据时不用处理
     * 1发布中的状态 用户初始化相关数据时可以拉取
     */
    private Integer publishStatus;
 
 
	public Integer getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}

 
    
 }
