package cn.bevol.app.entity.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;


/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class GoodsTagComposition implements Serializable  {
	private Long id;
	/**
	 * 对应tag表的id
	 */
	private Long tagId;
	/**
	 * 标签应该含有的成分
	 */
	private Long compositionId;
	
	/**
	 * 对应的标签不能含有的成分(ex:美白不能含有水,祛斑可以含有)
	 */
	private Long notCompositionId;
	
	private Integer status;
	
	private Long createStamp;
	/**
	 * 1表示是核心成分
	 */
	private int isMain;
	/**
	 * Y表示是核心成分
	 */
	private String mainName;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTagId() {
		return tagId;
	}
	public void setTagId(long tagId) {
		this.tagId = tagId;
	}
	public long getCompositionId() {
		return compositionId;
	}
	public void setCompositionId(long compositionId) {
		this.compositionId = compositionId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getIsMain() {
		return isMain;
	}
	public void setIsMain(int isMain) {
		this.isMain = isMain;
	}
	public String getMainName() {
		return mainName;
	}
	public void setMainName(String mainName) {
		this.mainName = mainName;
	}
	public Long getCreateStamp() {
		return createStamp;
	}
	public void setCreateStamp(Long createStamp) {
		this.createStamp = createStamp;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
	public void setCompositionId(Long compositionId) {
		this.compositionId = compositionId;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getNotCompositionId() {
		return notCompositionId;
	}
	public void setNotCompositionId(Long notCompositionId) {
		this.notCompositionId = notCompositionId;
	}
	
	
	
	
}

