package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.oauth.code.create response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OauthCodeCreateResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4349846644844235741L;

	/** 
	 * mock out params
	 */
	@ApiField("test")
	private Long test;


	public void setTest(Long test) {
		this.test = test;
	}
	public Long getTest( ) {
		return this.test;
	}
	


}
