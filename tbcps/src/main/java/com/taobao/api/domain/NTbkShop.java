package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * 淘宝客店铺
 *
 * @author top auto create
 * @since 1.0, null
 */
public class NTbkShop extends TaobaoObject {

	private static final long serialVersionUID = 1142746985346621314L;

	/**
	 * 淘客地址
	 */
	@ApiField("click_url")
	private String clickUrl;

	/**
	 * 店标图片
	 */
	@ApiField("pict_url")
	private String pictUrl;

	/**
	 * 卖家昵称
	 */
	@ApiField("seller_nick")
	private String sellerNick;

	/**
	 * 店铺名称
	 */
	@ApiField("shop_title")
	private String shopTitle;

	/**
	 * 店铺类型，B：天猫，C：淘宝
	 */
	@ApiField("shop_type")
	private String shopType;

	/**
	 * 店铺地址
	 */
	@ApiField("shop_url")
	private String shopUrl;

	/**
	 * 卖家ID
	 */
	@ApiField("user_id")
	private Long userId;


	public String getClickUrl() {
		return this.clickUrl;
	}
	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}

	public String getPictUrl() {
		return this.pictUrl;
	}
	public void setPictUrl(String pictUrl) {
		this.pictUrl = pictUrl;
	}

	public String getSellerNick() {
		return this.sellerNick;
	}
	public void setSellerNick(String sellerNick) {
		this.sellerNick = sellerNick;
	}

	public String getShopTitle() {
		return this.shopTitle;
	}
	public void setShopTitle(String shopTitle) {
		this.shopTitle = shopTitle;
	}

	public String getShopType() {
		return this.shopType;
	}
	public void setShopType(String shopType) {
		this.shopType = shopType;
	}

	public String getShopUrl() {
		return this.shopUrl;
	}
	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}

	public Long getUserId() {
		return this.userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
