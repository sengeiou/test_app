package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * 商品查询分类结果
 *
 * @author top auto create
 * @since 1.0, null
 */
public class ItemCategory extends TaobaoObject {

	private static final long serialVersionUID = 3537817188977428991L;

	/**
	 * 分类ID
	 */
	@ApiField("category_id")
	private Long categoryId;

	/**
	 * 分类名称
	 */
	@ApiField("category_name")
	private String categoryName;

	/**
	 * 商品数量
	 */
	@ApiField("count")
	private Long count;


	public Long getCategoryId() {
		return this.categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return this.categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Long getCount() {
		return this.count;
	}
	public void setCount(Long count) {
		this.count = count;
	}

}
