package cn.bevol.internal.entity.model;

import java.io.Serializable;

/**
 * 成分使用目的
 * @author Administrator
 *
 */
public class Used implements Serializable{

	private Long id;
	
	private String title;

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
