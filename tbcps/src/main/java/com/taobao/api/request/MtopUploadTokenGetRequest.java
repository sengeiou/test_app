package com.taobao.api.request;

import com.taobao.api.domain.UploadTokenRequestV;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.MtopUploadTokenGetResponse;

/**
 * TOP API: taobao.mtop.upload.token.get request
 * 
 * @author top auto create
 * @since 1.0, 2015.05.25
 */
public class MtopUploadTokenGetRequest extends BaseTaobaoRequest<MtopUploadTokenGetResponse> {
	
	

	/** 
	* 系统自动生成
	 */
	private String paramUploadTokenRequest;

	public void setParamUploadTokenRequest(String paramUploadTokenRequest) {
		this.paramUploadTokenRequest = paramUploadTokenRequest;
	}

	public void setParamUploadTokenRequest(UploadTokenRequestV paramUploadTokenRequest) {
		this.paramUploadTokenRequest = new JSONWriter(false,true).write(paramUploadTokenRequest);
	}

	public String getParamUploadTokenRequest() {
		return this.paramUploadTokenRequest;
	}

	public String getApiMethodName() {
		return "taobao.mtop.upload.token.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("param_upload_token_request", this.paramUploadTokenRequest);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<MtopUploadTokenGetResponse> getResponseClass() {
		return MtopUploadTokenGetResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}