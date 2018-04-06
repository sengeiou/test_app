package cn.bevol.model.entityAction;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论点赞和用户的关系
 *
 * @author hualong
 */
public class CommentLike implements Serializable{
    /**
     * 修改时间
     */
	@Field
    private Long updateStamp=new Date().getTime()/1000;
    /**
     * 数据的系统创建时间 统一用createTime
     */
	@Field
    private Long createStamp=new Date().getTime()/1000;
	
	private Long entityId;
	
	private Long userId;

	/**
	 * 肤质信息
	 */
	private String skin;

	/**
	 * 肤质详细结果
	 */
	private String skinResults;


	public Long getUpdateStamp() {
		return updateStamp;
	}

	public void setUpdateStamp(Long updateStamp) {
		this.updateStamp = updateStamp;
	}

	public Long getCreateStamp() {
		return createStamp;
	}

	public void setCreateStamp(Long createStamp) {
		this.createStamp = createStamp;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
