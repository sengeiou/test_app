package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.OpenAccountSearchResult;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.open.account.search response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountSearchResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4853398388951466943L;

	/** 
	 * 返回结果
	 */
	@ApiField("data")
	private OpenAccountSearchResult data;


	public void setData(OpenAccountSearchResult data) {
		this.data = data;
	}
	public OpenAccountSearchResult getData( ) {
		return this.data;
	}
	


}
