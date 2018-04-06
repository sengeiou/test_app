package cn.bevol.internal.entity.user;

import cn.bevol.internal.entity.metadata.EntityInfo;
import cn.bevol.internal.entity.metadata.Tag;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 护肤流程
 * @author hualong
 * 
 */
public class UserSkinFlow  implements Serializable {

	//id表示用户id 集成而来
	
	/**
	 * 实体信息 key 表示分类
	 * val表示 分类的产品
	 * 
	 */
	private Map<Long,List<EntityInfo>> goodsInfo;
	/**
	 * 肤质标签
	 */
	private List<Tag> skinTags;
	

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;
	
 	public List<Tag> getSkinTags() {
		return skinTags;
	}
	public void setSkinTags(List<Tag> skinTags) {
		this.skinTags = skinTags;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public Map<Long, List<EntityInfo>> getGoodsInfo() {
		return goodsInfo;
	}
	public void setGoodsInfo(Map<Long, List<EntityInfo>> goodsInfo) {
		this.goodsInfo = goodsInfo;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
	
	
	
}
