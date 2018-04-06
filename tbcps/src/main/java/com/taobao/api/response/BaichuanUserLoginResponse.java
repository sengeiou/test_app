package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.baichuan.user.login response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class BaichuanUserLoginResponse extends TaobaoResponse {

	private static final long serialVersionUID = 5657693923496526514L;

	/** 
	 * name
	 */
	@ApiField("name")
	private String name;


	public void setName(String name) {
		this.name = name;
	}
	public String getName( ) {
		return this.name;
	}
	


}
