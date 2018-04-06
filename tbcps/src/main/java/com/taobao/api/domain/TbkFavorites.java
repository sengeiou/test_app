package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * 淘宝客选品库
 *
 * @author top auto create
 * @since 1.0, null
 */
public class TbkFavorites extends TaobaoObject {

	private static final long serialVersionUID = 5696736478175856654L;

	/**
	 * 选品库id
	 */
	@ApiField("favorites_id")
	private Long favoritesId;

	/**
	 * 选品组名称
	 */
	@ApiField("favorites_title")
	private String favoritesTitle;

	/**
	 * 选品库类型，1：普通类型，2高佣金类型
	 */
	@ApiField("type")
	private Long type;


	public Long getFavoritesId() {
		return this.favoritesId;
	}
	public void setFavoritesId(Long favoritesId) {
		this.favoritesId = favoritesId;
	}

	public String getFavoritesTitle() {
		return this.favoritesTitle;
	}
	public void setFavoritesTitle(String favoritesTitle) {
		this.favoritesTitle = favoritesTitle;
	}

	public Long getType() {
		return this.type;
	}
	public void setType(Long type) {
		this.type = type;
	}

}
