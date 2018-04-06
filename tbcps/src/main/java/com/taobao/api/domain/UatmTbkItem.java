package com.taobao.api.domain;

import java.util.List;
import java.util.Date;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import com.taobao.api.internal.mapping.ApiListField;


/**
 * 淘宝联盟选品和招商宝贝信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public class UatmTbkItem extends TaobaoObject {

	private static final long serialVersionUID = 2533321394563434681L;

	/**
	 * 后台一级类目
	 */
	@ApiField("category")
	private Long category;

	/**
	 * 淘客地址
	 */
	@ApiField("click_url")
	private String clickUrl;

	/**
	 * 佣金比率(%)
	 */
	@ApiField("commission_rate")
	private String commissionRate;

	/**
	 * 商品优惠券推广链接
	 */
	@ApiField("coupon_click_url")
	private String couponClickUrl;

	/**
	 * 优惠券结束时间
	 */
	@ApiField("coupon_end_time")
	private String couponEndTime;

	/**
	 * 优惠券面额
	 */
	@ApiField("coupon_info")
	private String couponInfo;

	/**
	 * 优惠券剩余量
	 */
	@ApiField("coupon_remain_count")
	private Long couponRemainCount;

	/**
	 * 优惠券开始时间
	 */
	@ApiField("coupon_start_time")
	private String couponStartTime;

	/**
	 * 优惠券总量
	 */
	@ApiField("coupon_total_count")
	private Long couponTotalCount;

	/**
	 * 招行活动的结束时间；如果该宝贝取自普通的选品组，则取值为1970-01-01 00:00:00
	 */
	@ApiField("event_end_time")
	private Date eventEndTime;

	/**
	 * 招商活动开始时间；如果该宝贝取自普通选品组，则取值为1970-01-01 00:00:00；
	 */
	@ApiField("event_start_time")
	private Date eventStartTime;

	/**
	 * 商品地址
	 */
	@ApiField("item_url")
	private String itemUrl;

	/**
	 * 卖家昵称
	 */
	@ApiField("nick")
	private String nick;

	/**
	 * 商品ID
	 */
	@ApiField("num_iid")
	private Long numIid;

	/**
	 * 商品主图
	 */
	@ApiField("pict_url")
	private String pictUrl;

	/**
	 * 宝贝所在地
	 */
	@ApiField("provcity")
	private String provcity;

	/**
	 * 商品一口价格
	 */
	@ApiField("reserve_price")
	private String reservePrice;

	/**
	 * 卖家id
	 */
	@ApiField("seller_id")
	private Long sellerId;

	/**
	 * 
	 */
	@ApiField("shop_title")
	private String shopTitle;

	/**
	 * 商品小图列表
	 */
	@ApiListField("small_images")
	@ApiField("string")
	private List<String> smallImages;

	/**
	 * 宝贝状态，0失效，1有效；注：失效可能是宝贝已经下线或者是被处罚不能在进行推广
	 */
	@ApiField("status")
	private Long status;

	/**
	 * 商品标题
	 */
	@ApiField("title")
	private String title;

	/**
	 * 收入比例，举例，取值为20.00，表示比例20.00%
	 */
	@ApiField("tk_rate")
	private String tkRate;

	/**
	 * 宝贝类型：1 普通商品； 2 鹊桥高佣金商品；3 定向招商商品；4 营销计划商品;
	 */
	@ApiField("type")
	private Long type;

	/**
	 * 卖家类型，0表示集市，1表示商城
	 */
	@ApiField("user_type")
	private Long userType;

	/**
	 * 30天销量
	 */
	@ApiField("volume")
	private Long volume;

	/**
	 * 商品折扣价格
	 */
	@ApiField("zk_final_price")
	private String zkFinalPrice;

	/**
	 * 无线折扣价，即宝贝在无线上的实际售卖价格。
	 */
	@ApiField("zk_final_price_wap")
	private String zkFinalPriceWap;


	public Long getCategory() {
		return this.category;
	}
	public void setCategory(Long category) {
		this.category = category;
	}

	public String getClickUrl() {
		return this.clickUrl;
	}
	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}

	public String getCommissionRate() {
		return this.commissionRate;
	}
	public void setCommissionRate(String commissionRate) {
		this.commissionRate = commissionRate;
	}

	public String getCouponClickUrl() {
		return this.couponClickUrl;
	}
	public void setCouponClickUrl(String couponClickUrl) {
		this.couponClickUrl = couponClickUrl;
	}

	public String getCouponEndTime() {
		return this.couponEndTime;
	}
	public void setCouponEndTime(String couponEndTime) {
		this.couponEndTime = couponEndTime;
	}

	public String getCouponInfo() {
		return this.couponInfo;
	}
	public void setCouponInfo(String couponInfo) {
		this.couponInfo = couponInfo;
	}

	public Long getCouponRemainCount() {
		return this.couponRemainCount;
	}
	public void setCouponRemainCount(Long couponRemainCount) {
		this.couponRemainCount = couponRemainCount;
	}

	public String getCouponStartTime() {
		return this.couponStartTime;
	}
	public void setCouponStartTime(String couponStartTime) {
		this.couponStartTime = couponStartTime;
	}

	public Long getCouponTotalCount() {
		return this.couponTotalCount;
	}
	public void setCouponTotalCount(Long couponTotalCount) {
		this.couponTotalCount = couponTotalCount;
	}

	public Date getEventEndTime() {
		return this.eventEndTime;
	}
	public void setEventEndTime(Date eventEndTime) {
		this.eventEndTime = eventEndTime;
	}

	public Date getEventStartTime() {
		return this.eventStartTime;
	}
	public void setEventStartTime(Date eventStartTime) {
		this.eventStartTime = eventStartTime;
	}

	public String getItemUrl() {
		return this.itemUrl;
	}
	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}

	public String getNick() {
		return this.nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}

	public Long getNumIid() {
		return this.numIid;
	}
	public void setNumIid(Long numIid) {
		this.numIid = numIid;
	}

	public String getPictUrl() {
		return this.pictUrl;
	}
	public void setPictUrl(String pictUrl) {
		this.pictUrl = pictUrl;
	}

	public String getProvcity() {
		return this.provcity;
	}
	public void setProvcity(String provcity) {
		this.provcity = provcity;
	}

	public String getReservePrice() {
		return this.reservePrice;
	}
	public void setReservePrice(String reservePrice) {
		this.reservePrice = reservePrice;
	}

	public Long getSellerId() {
		return this.sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getShopTitle() {
		return this.shopTitle;
	}
	public void setShopTitle(String shopTitle) {
		this.shopTitle = shopTitle;
	}

	public List<String> getSmallImages() {
		return this.smallImages;
	}
	public void setSmallImages(List<String> smallImages) {
		this.smallImages = smallImages;
	}

	public Long getStatus() {
		return this.status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}

	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTkRate() {
		return this.tkRate;
	}
	public void setTkRate(String tkRate) {
		this.tkRate = tkRate;
	}

	public Long getType() {
		return this.type;
	}
	public void setType(Long type) {
		this.type = type;
	}

	public Long getUserType() {
		return this.userType;
	}
	public void setUserType(Long userType) {
		this.userType = userType;
	}

	public Long getVolume() {
		return this.volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public String getZkFinalPrice() {
		return this.zkFinalPrice;
	}
	public void setZkFinalPrice(String zkFinalPrice) {
		this.zkFinalPrice = zkFinalPrice;
	}

	public String getZkFinalPriceWap() {
		return this.zkFinalPriceWap;
	}
	public void setZkFinalPriceWap(String zkFinalPriceWap) {
		this.zkFinalPriceWap = zkFinalPriceWap;
	}

}
