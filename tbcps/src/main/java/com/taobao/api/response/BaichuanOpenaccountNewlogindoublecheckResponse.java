package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.baichuan.openaccount.newlogindoublecheck response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class BaichuanOpenaccountNewlogindoublecheckResponse extends TaobaoResponse {

	private static final long serialVersionUID = 8184557627615187389L;

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
