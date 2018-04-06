package cn.bevol.model.items;

import java.io.Serializable;

/**
 * 微信公众号信息
 */
public class MpAccount implements Serializable {
	private static final long serialVersionUID = -6315146640254918207L;
	
	private String account="美丽修行APP";//账号
//	private String appid ="wx02488e10d8a43eb4";//appid
//	private String appsecret="000a12cb15d8a034a1bc2b04cf54f3e3";//appsecret

	private String appid ="wxe3187d6b16f957f3";//appid
	private String appsecret="a99436c037cbe41b1374d32b60458cb9";//appsecret
	private String url="http://bevol.ngrok.cc/wxapi/bevol/message";//验证时用的url
	private String token="bevol";//token
	
	//ext
	private Integer msgcount;//自动回复消息条数;默认是5条
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getMsgcount() {
		if(msgcount == null)
			msgcount = 5;//默认5条
		return msgcount;
	}
	public void setMsgcount(Integer msgcount) {
		this.msgcount = msgcount;
	}
	
}
