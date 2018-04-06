package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkUatmFavoritesGetResponse;

/**
 * TOP API: taobao.tbk.uatm.favorites.get request
 * 
 * @author top auto create
 * @since 1.0, 2018.01.19
 */
public class TbkUatmFavoritesGetRequest extends BaseTaobaoRequest<TbkUatmFavoritesGetResponse> {
	
	

	/** 
	* 需要返回的字段列表，不能为空，字段名之间使用逗号分隔
	 */
	private String fields;

	/** 
	* 第几页，从1开始计数
	 */
	private Long pageNo;

	/** 
	* 默认20，页大小，即每一页的活动个数
	 */
	private Long pageSize;

	/** 
	* 默认值-1；选品库类型，1：普通选品组，2：高佣选品组，-1，同时输出所有类型的选品组
	 */
	private Long type;

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getFields() {
		return this.fields;
	}

	public void setPageNo(Long pageNo) {
		this.pageNo = pageNo;
	}

	public Long getPageNo() {
		return this.pageNo;
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	public Long getPageSize() {
		return this.pageSize;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public Long getType() {
		return this.type;
	}

	public String getApiMethodName() {
		return "taobao.tbk.uatm.favorites.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("fields", this.fields);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("type", this.type);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkUatmFavoritesGetResponse> getResponseClass() {
		return TbkUatmFavoritesGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(fields, "fields");
	}
	

}