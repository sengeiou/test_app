package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.BaichuanUserLoginResponse;

/**
 * TOP API: taobao.baichuan.user.login request
 * 
 * @author top auto create
 * @since 1.0, 2015.06.10
 */
public class BaichuanUserLoginRequest extends BaseTaobaoRequest<BaichuanUserLoginResponse> {
	
	

	/** 
	* name
	 */
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getApiMethodName() {
		return "taobao.baichuan.user.login";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("name", this.name);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<BaichuanUserLoginResponse> getResponseClass() {
		return BaichuanUserLoginResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}