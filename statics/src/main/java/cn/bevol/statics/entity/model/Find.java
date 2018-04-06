package cn.bevol.statics.entity.model;

import cn.bevol.util.CommonUtils;
import cn.bevol.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;


/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Find implements Serializable  {
	
	/**
	 * 自增业务id
	 */
	private Long id;
	
	@JsonIgnore
	private Integer hidden=0;
	@JsonIgnore
	private Integer deleted=0;
	
    /**
     * 修改时间
     */
	@Field
    private Long updateStamp= DateUtils.nowInMillis();
    /**
     * 数据的系统创建时间 统一用createTime
     */
	@Field
    private Long createStamp=DateUtils.nowInMillis();
	
	
	/**
	 * 实体标题
	 */
	private String title;
	
	/**
	 * 短标题
	 */
	private String subhead;
	
	private String image; 
	
	//标签的id 逗号分隔
	private String tagIds;
	
	//标签名 逗号分隔
	private String tag;

	private String descp;

	@Transient
	private String headerImage;
	
	@Transient
	private String headerImageSrc;
	
	private String imageSrc; 
	
	private String pcImage;

	private Long authorId;

	/**
	 * 点击数量
	 */
	private Long hitNum=0L;

	/**
	 * 评论数量
	 */
	private Long commentNum=0L;

	/**
	 * 喜欢数量 数量用Num结尾
	 */
	private Long likeNum=0L;

	/**
	 * 收藏数量
	 */
	private Long collectionNum=0L;

	/**
	 * 发布时间
	 */
	private Long publishTime= DateUtils.nowInMillis();

	/**
	 * 适合/推荐 肤质
	 */
	private String skin;


	public Long getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Long likeNum) {
		this.likeNum = likeNum;
	}

	public Long getCollectionNum() {
		return collectionNum;
	}

	public void setCollectionNum(Long collectionNum) {
		this.collectionNum = collectionNum;
	}

	public Long getHitNum() {
		return hitNum;
	}

	public void setHitNum(Long hitNum) {
		this.hitNum = hitNum;
	}

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	public String getHeaderImageSrc() {
		headerImageSrc= CommonUtils.getImageSrc("find", this.getHeaderImage());
		return headerImageSrc;
	}
	public void setHeaderImageSrc(String headerImageSrc) {
		this.headerImageSrc = headerImageSrc;
	}
	public String getImageSrc() {
		imageSrc= CommonUtils.getImageSrc("find", this.getImage());
		return imageSrc;
	}
	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getHidden() {
		return hidden;
	}
	public void setHidden(Integer hidden) {
		this.hidden = hidden;
	}
	public Integer getDeleted() {
		return deleted;
	}
	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
 

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	public String getSubhead() {
		return subhead;
	}
	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}
	public String getTagIds() {
		return tagIds;
	}
	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getDescp() {
		return descp;
	}

	public void setDescp(String descp) {
		this.descp = descp;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}
	public String getPcImage() {
		return pcImage;
	}
	public void setPcImage(String pcImage) {
		this.pcImage = pcImage;
	}
	public Long getAuthorId() {
		return authorId;
	}
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}


	public Long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Long publishTime) {
		this.publishTime = publishTime;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}
}
