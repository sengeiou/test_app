package cn.bevol.model.entity;

import cn.bevol.model.entity.EntityBase;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 成分
 * @author hualong
 *
 */
@Document(collection="entity_lists")
public class EntityLists extends EntityBase {
 	
	
	/**
	 * 参与人数 
	 */
	private Integer partNum;

	/**
	 * 小图
	 */
	private String miniImage;
	
 
	public Integer getPartNum() {
		return partNum;
	}

	public void setPartNum(Integer partNum) {
		this.partNum = partNum;
	}

	public String getMiniImage() {
		return miniImage;
	}

	public void setMiniImage(String miniImage) {
		this.miniImage = miniImage;
	}
	
	
}
