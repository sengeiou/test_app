package com.taobao.api.domain;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import com.taobao.api.internal.mapping.ApiListField;


/**
 * 淘宝客商品
 *
 * @author top auto create
 * @since 1.0, null
 */
public class NTbkItem extends TaobaoObject {

	private static final long serialVersionUID = 5835942525946248276L;

	/**
	 * 叶子类目名称
	 */
	@ApiField("cat_leaf_name")
	private String catLeafName;

	/**
	 * 一级类目名称
	 */
	@ApiField("cat_name")
	private String catName;

	/**
	 * 淘客地址
	 */
	@ApiField("click_url")
	private String clickUrl;

	/**
	 * 佣金比例
	 */
	@ApiField("commission_rate")
	private String commissionRate;

	/**
	 * couponAmount
	 */
	@ApiField("coupon_amount")
	private String couponAmount;

	/**
	 * 优惠券额度
	 */
	@ApiField("coupon_price")
	private String couponPrice;

	/**
	 * 优惠券使用门槛金额
	 */
	@ApiField("coupon_start_fee")
	private String couponStartFee;

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
	 * 商品标题
	 */
	@ApiField("title")
	private String title;

	/**
	 * 
	 */
	@ApiField("tk_rate")
	private String tkRate;

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
	 * 
	 */
	@ApiField("zk_final_price_wap")
	private String zkFinalPriceWap;


	public String getCatLeafName() {
		return this.catLeafName;
	}
	public void setCatLeafName(String catLeafName) {
		this.catLeafName = catLeafName;
	}

	public String getCatName() {
		return this.catName;
	}
	public void setCatName(String catName) {
		this.catName = catName;
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

	public String getCouponAmount() {
		return this.couponAmount;
	}
	public void setCouponAmount(String couponAmount) {
		this.couponAmount = couponAmount;
	}

	public String getCouponPrice() {
		return this.couponPrice;
	}
	public void setCouponPrice(String couponPrice) {
		this.couponPrice = couponPrice;
	}

	public String getCouponStartFee() {
		return this.couponStartFee;
	}
	public void setCouponStartFee(String couponStartFee) {
		this.couponStartFee = couponStartFee;
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
