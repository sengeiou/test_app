package cn.bevol.statics.entity;

/**
 * 成分
 * @author hualong
 *
 */
public class UserPartDetailText extends UserPartDetail {
 	
	private String content;

	public UserPartDetailText() {
		
	}
	
	public UserPartDetailText(String content) {
		this.content=content;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
  
}
