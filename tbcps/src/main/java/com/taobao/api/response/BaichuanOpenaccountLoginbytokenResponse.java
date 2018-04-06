package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.baichuan.openaccount.loginbytoken response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class BaichuanOpenaccountLoginbytokenResponse extends TaobaoResponse {

	private static final long serialVersionUID = 5239179598369528769L;

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
