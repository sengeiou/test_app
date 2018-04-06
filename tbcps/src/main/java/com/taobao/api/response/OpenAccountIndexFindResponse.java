package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.OpenAccountResult;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.open.account.index.find response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountIndexFindResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6623237244987474158L;

	/** 
	 * 返回结果
	 */
	@ApiField("result")
	private OpenAccountResult result;


	public void setResult(OpenAccountResult result) {
		this.result = result;
	}
	public OpenAccountResult getResult( ) {
		return this.result;
	}
	


}
