package cn.bevol.staticc.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * 用户基本信息
 * @author Administrator
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UserBaseInfo implements Serializable{

	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 用户名
	 */
	private String nickname;
	
	/**
	 * 用户头像
	 */
	private String headimgurl;


	/**
	 * 肤质信息
	 */
	private String skin;
	
	/**
	 * 肤质详细结果
	 */
	private String skinResults;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public String getSkinResults() {
		return skinResults;
	}

	public void setSkinResults(String skinResults) {
		this.skinResults = skinResults;
	}
	
	

}
