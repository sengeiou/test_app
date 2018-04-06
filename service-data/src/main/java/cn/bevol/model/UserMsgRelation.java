package cn.bevol.model;

/**
 * 消息关系
 * @author Administrator
 *
 */
@Deprecated
public class UserMsgRelation {
	/**
	 *  消息id 
	 */
	private String msgid;
	/**
	 * 0没有阅读 1阅读
	 */
	private int isread;
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public int getIsread() {
		return isread;
	}
	public void setIsread(int isread) {
		this.isread = isread;
	}
	
	
	
}
