package com.taobao.api.request;

import com.taobao.api.domain.CheckVerCodeRequest;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.OpenSmsCheckvercodeResponse;

/**
 * TOP API: taobao.open.sms.checkvercode request
 * 
 * @author top auto create
 * @since 1.0, 2015.08.28
 */
public class OpenSmsCheckvercodeRequest extends BaseTaobaoRequest<OpenSmsCheckvercodeResponse> {
	
	

	/** 
	* 验证验证码
	 */
	private String checkVerCodeRequest;

	public void setCheckVerCodeRequest(String checkVerCodeRequest) {
		this.checkVerCodeRequest = checkVerCodeRequest;
	}

	public void setCheckVerCodeRequest(CheckVerCodeRequest checkVerCodeRequest) {
		this.checkVerCodeRequest = new JSONWriter(false,true).write(checkVerCodeRequest);
	}

	public String getCheckVerCodeRequest() {
		return this.checkVerCodeRequest;
	}

	public String getApiMethodName() {
		return "taobao.open.sms.checkvercode";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("check_ver_code_request", this.checkVerCodeRequest);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenSmsCheckvercodeResponse> getResponseClass() {
		return OpenSmsCheckvercodeResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}