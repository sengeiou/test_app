package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenAccountTokenValidateResponse;

/**
 * TOP API: taobao.open.account.token.validate request
 * 
 * @author top auto create
 * @since 1.0, 2018.02.09
 */
public class OpenAccountTokenValidateRequest extends BaseTaobaoRequest<OpenAccountTokenValidateResponse> {
	
	

	/** 
	* token
	 */
	private String paramToken;

	public void setParamToken(String paramToken) {
		this.paramToken = paramToken;
	}

	public String getParamToken() {
		return this.paramToken;
	}

	public String getApiMethodName() {
		return "taobao.open.account.token.validate";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("param_token", this.paramToken);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenAccountTokenValidateResponse> getResponseClass() {
		return OpenAccountTokenValidateResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(paramToken, "paramToken");
	}
	

}