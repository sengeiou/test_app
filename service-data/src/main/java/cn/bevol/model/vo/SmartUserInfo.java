package cn.bevol.model.vo;

/**
 * 用户基本信息展现用于评论头像等
 * @author hualogn
 *
 */
public class SmartUserInfo {

	private Long id;
	
	private String nickname;
	
	private String headimgurl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	
}
