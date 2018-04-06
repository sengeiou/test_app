package com.taobao.api.response;

import com.taobao.api.domain.OpenAccountTokenApplyResult;
import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.open.account.token.apply response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountTokenApplyResponse extends TaobaoResponse {

	private static final long serialVersionUID = 3436684619637722775L;

	/** 
	 * 返回的token结果
	 */
	@ApiField("data")
	private OpenAccountTokenApplyResult data;


	public void setData(OpenAccountTokenApplyResult data) {
		this.data = data;
	}
	public OpenAccountTokenApplyResult getData( ) {
		return this.data;
	}
	


}
