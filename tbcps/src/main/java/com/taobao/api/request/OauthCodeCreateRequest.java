package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OauthCodeCreateResponse;

/**
 * TOP API: taobao.oauth.code.create request
 * 
 * @author top auto create
 * @since 1.0, 2016.04.18
 */
public class OauthCodeCreateRequest extends BaseTaobaoRequest<OauthCodeCreateResponse> {
	
	

	/** 
	* mock param
	 */
	private Long test;

	public void setTest(Long test) {
		this.test = test;
	}

	public Long getTest() {
		return this.test;
	}

	public String getApiMethodName() {
		return "taobao.oauth.code.create";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("test", this.test);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OauthCodeCreateResponse> getResponseClass() {
		return OauthCodeCreateResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}