package cn.bevol.mybatis.dto;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.util.CommonUtils;

/**
 * 精选点评
 *
 * @author chenhaijian
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EssenceComment extends EntityBase{
	

	private Long userId;

    private String nickname;
    
    private String headimgurl;

    private String userDescz;
    
    private String skin;
    
    private String skinResults;
    
    /**
	 * 精选评论数量
	 */
    @Transient
	private Long essenceCommentNum;
	
	/**
	 * 修行说数量
	 */
    @Transient
	private Long xxsNum;

    private Long goodsId;
    
    private String goodsMid;
    
    private String goodsTitle;

    private String goodsImage;

    private Integer type;

    private Long typeId;
    
    private String content;
    
    private Long publishTime;

    private Long likeNum;

    private Integer isEssence;
    
    private Integer hiddenStatus;
    
    private Long createTime;
    
    @Transient
    private Integer adId;
    
    @Transient
    private Integer adOrientation;
    
    @Transient
    private String positionType;
    
    @Transient
    private Integer redirectType;
    
    @Transient
    private String redirectUrl;
    
    @Transient
    private String goodsImageSrc;
    
	public String getGoodsImageSrc() {
		goodsImageSrc=CommonUtils.getImageSrc("goods", this.getGoodsImage());
		return goodsImageSrc;
	}

	public void setGoodsImageSrc(String goodsImageSrc) {
		this.goodsImageSrc = goodsImageSrc;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname; 
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getUserDescz() {
		return userDescz;
	}

	public void setUserDescz(String userDescz) {
		this.userDescz = userDescz;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}


	public String getGoodsImage() {
		return goodsImage;
	}

	public void setGoodsImage(String goodsImage) {
		this.goodsImage = goodsImage;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Long publishTime) {
		this.publishTime = publishTime;
	}


	public Integer getIsEssence() {
		return isEssence;
	}

	public void setIsEssence(Integer isEssence) {
		this.isEssence = isEssence;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}


	public String getGoodsMid() {
		return goodsMid;
	}

	public void setGoodsMid(String goodsMid) {
		this.goodsMid = goodsMid;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public Long getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Long likeNum) {
		this.likeNum = likeNum;
	}

	public Integer getHiddenStatus() {
		return hiddenStatus;
	}

	public void setHiddenStatus(Integer hiddenStatus) {
		this.hiddenStatus = hiddenStatus;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
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

	public Long getEssenceCommentNum() {
		return essenceCommentNum;
	}

	public void setEssenceCommentNum(Long essenceCommentNum) {
		this.essenceCommentNum = essenceCommentNum;
	}

	public Long getXxsNum() {
		return xxsNum;
	}

	public void setXxsNum(Long xxsNum) {
		this.xxsNum = xxsNum;
	}

	public Integer getAdId() {
		return adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
	}

	public Integer getAdOrientation() {
		return adOrientation;
	}

	public void setAdOrientation(Integer adOrientation) {
		this.adOrientation = adOrientation;
	}


	public String getPositionType() {
		return positionType;
	}

	public void setPositionType(String positionType) {
		this.positionType = positionType;
	}

	public Integer getRedirectType() {
		return redirectType;
	}

	public void setRedirectType(Integer redirectType) {
		this.redirectType = redirectType;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	
}
