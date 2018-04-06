package cn.bevol.internal.entity.entityAction;

import cn.bevol.internal.entity.vo.SmartUserInfo;
import cn.bevol.model.entity.MongoBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体和用户产生的关系
 * @author hualong
 *
 */
public class EntityActionBase extends MongoBase{
	
	
 	@Indexed
	private Long entityId;
 	
 	@Indexed
	private Long userId;
 	
	/**
	 * 用于展示
	 */
 	@Transient
	private SmartUserInfo smartUserInfo;
 	
 	/**
	 * 用于展示
	 */
 	@Transient
	private List objList=new ArrayList();

	/**
	 * 肤质信息
	 */
	private String skin;
	
	/**
	 * 肤质详细结果
	 */
	private String skinResults;

	
	/**
	 * 评论类型 cType=user_part 用户参与
	 *  不自在就是默认的
	 */
	private String cType;
	
	/**
	 * 评论类型 的实体id
	 */
	private String cTypeId;
	
	/**
	 * 对比的id
	 */
	private String sid;

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
		if(!StringUtils.isBlank(skin))
			this.skin = skin;
	}

	public String getSkinResults() {
		return skinResults;
	}

	public void setSkinResults(String skinResults) {
		this.skinResults = skinResults;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public String getcTypeId() {
		return cTypeId;
	}

	public void setcTypeId(String cTypeId) {
		this.cTypeId = cTypeId;
	}

	public SmartUserInfo getSmartUserInfo() {
		return smartUserInfo;
	}

	public void setSmartUserInfo(SmartUserInfo smartUserInfo) {
		this.smartUserInfo = smartUserInfo;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public List getObjList() {
		return objList;
	}

	public void setObjList(List objList) {
		this.objList = objList;
	}

 	
}
