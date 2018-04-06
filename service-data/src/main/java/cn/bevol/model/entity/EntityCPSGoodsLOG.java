package cn.bevol.model.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CPS 日志实体
 * @author zqc
 * 数据主要存在Mongo中存储
 */
@Document(collection="EntityCPSGoodsLOG")
public class EntityCPSGoodsLOG  extends EntityBase{
	
	/**
	 * 实体对应的表
	 */
	public static final String  ENTITY_CPS_GOODSLOG="EntityCPSGoodsLOG";
	
	
	/**
	 * 数据未被同步到CPS实体中
	 */
	public static final Integer ISSYNCHRONIZATION_NO=0;
	/**
	 * 数据被同步到CPS实体中了
	 */
	public static final Integer ISSYNCHRONIZATION_YES=1;
	
	/**
	 * 数据是否被同步到,CPS实体中  ,默认为，未同步到实体中
	 */
	private Integer   isSynchronization=EntityCPSGoodsLOG.ISSYNCHRONIZATION_NO;
	
     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 日志创建时间
	 */
	private Long createLogTime;
	
	/**
      * 日志ID
      */
	// private Long id;
	 /**
	  * CPS  创建时保留的淘宝商品ID
	  */
	 private Long cpsCreateGoodsId;
	 /**
	  *  淘宝客商品组id
	  */
	 private Long  favoritesId;
	 /**
	  * 商品名称
	  */
	 private String goodsName;
	 /**
	  * 商品图片
	  */
	 private String goodsImage;
	 /**
	  * 商品小图
	  */
	 private List<String> goodsSmallImages;
	 /**
	  * 商品原价格
	  */
	 private String goodsReservePrice;
	 /**
	  * 商品打折后的价格
	  */
	 private Float goodsZKFinalPrice;
	 /**
	  * 商品卖家类型
	  */
	 private Integer goodsSellerType;
	 /**
	  * 商品销售地址，商家在哪里贩卖
	  */
	 private String goodsSellerAddress;
	 /**
	  * 商品链接
	  */
	 private String goodsItemUrl;
	 /**
	  * 淘宝客链接
	  */
	 private String goodsClickUrl;
	 /**
	  * 商品卖家名称
	  */
	 private String goodsSellerName; 
	 /**
	  * 商品卖家id
	  */
	 private Long goodsSellerId ;
	 /**
	  * 商品销量
	  */
	 private Long goodsVolume;
	 /**
	  * 商品收入比例
	  * 商品收入比例 20.00 就是20%
	  */
	 private Float goodsTKRate;
	 /**
	  * 无线折扣价，即宝贝在无线上的实际售卖价格。
	  */
	 private Float goodsZKFinalPriceWap ;
	 /**
	  * 
	  */
	 private String  goodsShopTitle ;
	 /**
	  * 招商活动开始时间
	  */
	 private Long eventStarTime;
	 /**
	  * 招商活动结束时间
	  */
	 private Long eventEndTime ;
	 /**
	  * 1宝贝类型：1 普通商品； 2 鹊桥高佣金商品；3 定向招商商品；4 营销计划商品;
	  */
	 private Integer  goodsType ;
	 /**
	  * 1宝贝状态，0失效，1有效；注：失效可能是宝贝已经下线或者是被处罚不能在进行推广
	  */
	 private Integer  goodsState ;
	 /**
	  * 1后台一级类目
	  */
	 private Integer  category ;
	 /**
	  * 商品优惠卷推广链接
	  */
	 private String  goodsCouponClickUrl  ;
	 /**
	  * 优惠卷结束时间
	  */
	 private Integer couponEndTime ;
	 /**
	  * 优惠卷开始时间
	  */
	 private Integer couponStartTime  ;
	 /**
	  * 优惠卷面额
	  */
	 private String  couponInfo ;
	 /**
	  * 优惠券总量
	  */
	 private Integer  couponTotalCount ;
	 
	 /**
	  * 选品库中的商品总条数
	  */
	 private Integer totalResults ;
	 /**
	  *  优惠券剩余量
	  */
	 private Integer couponRemainCount ;
	 /**
	  *  安卓链接
	  */
	 private String  AndroidURL ;
	 /**
	  * 苹果链接
	  */
	 private String  IOSURL ;
	  /**
	    * 渠道类型 1.淘宝，  2京东 3天猫
	    */
	 private Integer channelType;
	 
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
	
	public Long getCpsCreateGoodsId() {
		
		return cpsCreateGoodsId;
	}
	public void setCpsCreateGoodsId(Long cpsCreateGoodsId) {
		this.cpsCreateGoodsId = cpsCreateGoodsId;
	}
 
	
	
	public Long getFavoritesId() {
		return favoritesId;
	}
	public void setFavoritesId(Long favoritesId) {
		this.favoritesId = favoritesId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsImage() {
		return goodsImage;
	}
	public void setGoodsImage(String goodsImage) {
		this.goodsImage = goodsImage;
	}
	public List<String> getGoodsSmallImages() {
		return goodsSmallImages;
	}
	public void setGoodsSmallImages(List<String> goodsSmallImages) {
		this.goodsSmallImages = goodsSmallImages;
	}
	public String getGoodsReservePrice() {
		return goodsReservePrice;
	}
	public void setGoodsReservePrice(String goodsReservePrice) {
		this.goodsReservePrice = goodsReservePrice;
	}
	public Float getGoodsZKFinalPrice() {
		return goodsZKFinalPrice;
	}
	public void setGoodsZKFinalPrice(Float goodsZKFinalPrice) {
		this.goodsZKFinalPrice = goodsZKFinalPrice;
	}
	public int getGoodsSellerType() {
		return goodsSellerType;
	}
	public void setGoodsSellerType(Integer goodsSellerType) {
		this.goodsSellerType = goodsSellerType;
	}
	public String getGoodsSellerAddress() {
		return goodsSellerAddress;
	}
	public void setGoodsSellerAddress(String goodsSellerAddress) {
		this.goodsSellerAddress = goodsSellerAddress;
	}
	public String getGoodsItemUrl() {
		return goodsItemUrl;
	}
	public void setGoodsItemUrl(String goodsItemUrl) {
		this.goodsItemUrl = goodsItemUrl;
	}
	public String getGoodsClickUrl() {
		return goodsClickUrl;
	}
	public void setGoodsClickUrl(String goodsClickUrl) {
		this.goodsClickUrl = goodsClickUrl;
	}
	public String getGoodsSellerName() {
		return goodsSellerName;
	}
	public void setGoodsSellerName(String goodsSellerName) {
		this.goodsSellerName = goodsSellerName;
	}
	public Long getGoodsSellerId() {
		return goodsSellerId;
	}
	public void setGoodsSellerId(Long goodsSellerId) {
		this.goodsSellerId = goodsSellerId;
	}
	public Long getGoodsVolume() {
		return goodsVolume;
	}
	public void setGoodsVolume(Long goodsVolume) {
		this.goodsVolume = goodsVolume;
	}
	public Float getGoodsTKRate() {
		return goodsTKRate;
	}
	public void setGoodsTKRate(Float goodsTKRate) {
		this.goodsTKRate = goodsTKRate;
	}
	public Float getGoodsZKFinalPriceWap() {
		return goodsZKFinalPriceWap;
	}
	public void setGoodsZKFinalPriceWap(Float goodsZKFinalPriceWap) {
		this.goodsZKFinalPriceWap = goodsZKFinalPriceWap;
	}
	public String getGoodsShopTitle() {
		return goodsShopTitle;
	}
	public void setGoodsShopTitle(String goodsShopTitle) {
		this.goodsShopTitle = goodsShopTitle;
	}
	public Long getEventEndTime() {
		return eventEndTime;
	}
	public void setEventEndTime(Long eventEndTime) {
		this.eventEndTime = eventEndTime;
	}
	public Integer getGoodsType() {
		return goodsType;
	}
	public void setGoodsType(int goodsType) {
		this.goodsType = goodsType;
	}
	public Integer getGoodsState() {
		return goodsState;
	}
	public void setGoodsState(int goodsState) {
		this.goodsState = goodsState;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public String getGoodsCouponClickUrl() {
		return goodsCouponClickUrl;
	}
	public void setGoodsCouponClickUrl(String goodsCouponClickUrl) {
		this.goodsCouponClickUrl = goodsCouponClickUrl;
	}
	public Integer getCouponEndTime() {
		return couponEndTime;
	}
	public void setCouponEndTime(Integer couponEndTime) {
		this.couponEndTime = couponEndTime;
	}
	public Integer getCouponStartTime() {
		return couponStartTime;
	}
	public void setCouponStartTime(Integer couponStartTime) {
		this.couponStartTime = couponStartTime;
	}
	public String getCouponInfo() {
		return couponInfo;
	}
	public void setCouponInfo(String couponInfo) {
		this.couponInfo = couponInfo;
	}
	public Integer getCouponTotalCount() {
		return couponTotalCount;
	}
	public void setCouponTotalCount(Integer couponTotalCount) {
		this.couponTotalCount = couponTotalCount;
	}
	public Integer getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}
	public Integer getCouponRemainCount() {
		return couponRemainCount;
	}
	public void setCouponRemainCount(Integer couponRemainCount) {
		this.couponRemainCount = couponRemainCount;
	}
	public String getAndroidURL() {
		return AndroidURL;
	}
	public void setAndroidURL(String androidURL) {
		AndroidURL = androidURL;
	}
	public String getIOSURL() {
		return IOSURL;
	}
	public void setIOSURL(String iOSURL) {
		IOSURL = iOSURL;
	}
	
	public Integer getChannelType() {
		return channelType;
	}
	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}
	public void setGoodsType(Integer goodsType) {
		this.goodsType = goodsType;
	}
	public void setGoodsState(Integer goodsState) {
		this.goodsState = goodsState;
	}
	public Long getEventStarTime() {
		return eventStarTime;
	}
	public void setEventStarTime(Long eventStarTime) {
		this.eventStarTime = eventStarTime;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public void setCouponTotalCount(int couponTotalCount) {
		this.couponTotalCount = couponTotalCount;
	}
	public Long getCreateLogTime() {
		return createLogTime;
	}
	public void setCreateLogTime(Long createLogTime) {
		this.createLogTime = createLogTime;
	}
	public Integer getIsSynchronization() {
		return isSynchronization;
	}
	public void setIsSynchronization(Integer isSynchronization) {
		this.isSynchronization = isSynchronization;
	}
	  
	@Override
	public String toString() {
		return "EntityCPSGoodsLOG [isSynchronization=" + isSynchronization + ", createLogTime=" + createLogTime
				+ ", cpsCreateGoodsId=" + cpsCreateGoodsId + ", taobaoKeId=" + favoritesId + ", goodsName=" + goodsName
				+ ", goodsImage=" + goodsImage + ", goodsSmallImages=" + goodsSmallImages + ", goodsReservePrice="
				+ goodsReservePrice + ", goodsZKFinalPrice=" + goodsZKFinalPrice + ", goodsSellerType="
				+ goodsSellerType + ", goodsSellerAddress=" + goodsSellerAddress + ", goodsItemUrl=" + goodsItemUrl
				+ ", goodsClickUrl=" + goodsClickUrl + ", goodsSellerName=" + goodsSellerName + ", goodsSellerId="
				+ goodsSellerId + ", goodsVolume=" + goodsVolume + ", goodsTKRate=" + goodsTKRate
				+ ", goodsZKFinalPriceWap=" + goodsZKFinalPriceWap + ", goodsShopTitle=" + goodsShopTitle
				+ ", eventStarTime=" + eventStarTime + ", eventEndTime=" + eventEndTime + ", goodsType=" + goodsType
				+ ", goodsState=" + goodsState + ", category=" + category + ", goodsCouponClickUrl="
				+ goodsCouponClickUrl + ", couponEndTime=" + couponEndTime + ", couponStartTime=" + couponStartTime
				+ ", couponInfo=" + couponInfo + ", couponTotalCount=" + couponTotalCount + ", totalResults="
				+ totalResults + ", couponRemainCount=" + couponRemainCount + ", AndroidURL=" + AndroidURL + ", IOSURL="
				+ IOSURL + ", channelType=" + channelType + "]";
	}
	 
	 
	 
	 
}
