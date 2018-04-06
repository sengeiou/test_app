package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.OpenAccountTokenValidateResult;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.open.account.token.validate response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountTokenValidateResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6669584549113854991L;

	/** 
	 * 验证成功返回token中的信息
	 */
	@ApiField("data")
	private OpenAccountTokenValidateResult data;


	public void setData(OpenAccountTokenValidateResult data) {
		this.data = data;
	}
	public OpenAccountTokenValidateResult getData( ) {
		return this.data;
	}
	


}
