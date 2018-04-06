package cn.bevol.model.entity;

import java.io.Serializable;

/**
 * 成分
 * @author hualong
 *
 */
public class UserPartDetail implements Serializable {
 	
	/**
	 * 1、实体  
	 * 2、文字
	 * 3、图片
	 * [{type:"1",id:"14","tname":"goods"},{type:"2",content:"3332"},{type:"3",image:"sss"}]
	 */
	private Integer type;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	
}
