package cn.bevol.statics.service.weixin.vo;

import java.io.Serializable;


/**
 * 公众号回复给用户的消息-基本信息
 * 
 */

public class MsgResponse implements Serializable {

	private String ToUserName;
	private String FromUserName;
	private Long CreateTime;
	private String MsgType;
	private String FuncFlag;

	
	private static final long serialVersionUID = -2672453770325583072L;
	
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public Long getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	public String getFuncFlag() {
		return FuncFlag;
	}

	public void setFuncFlag(String funcFlag) {
		FuncFlag = funcFlag;
	}
}
