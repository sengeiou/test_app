package cn.bevol.internal.entity.metadata;

import java.io.Serializable;

/**
 * 标签 信息
 * @author hualong
 *
 */
public class Tag  implements Serializable {

	private Long id;
	
	/*
	 * 名称
	 */
	private String title;

	
	public Tag() {
		
	};
	
	
	public Tag(Long id, String title) {
		this.id=id;
		this.title=title;
	};

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
