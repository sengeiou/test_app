package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.baichuan.openaccount.registercode.check response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class BaichuanOpenaccountRegistercodeCheckResponse extends TaobaoResponse {

	private static final long serialVersionUID = 7136428542774345385L;

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
