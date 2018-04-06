package com.taobao.api.request;

import com.taobao.api.internal.util.json.JSONValidatingReader;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlibabaBaichuanAppcontentUploadResponse;

/**
 * TOP API: alibaba.baichuan.appcontent.upload request
 * 
 * @author top auto create
 * @since 1.0, 2016.05.10
 */
public class AlibabaBaichuanAppcontentUploadRequest extends BaseTaobaoRequest<AlibabaBaichuanAppcontentUploadResponse> {
	
	

	/** 
	* app标识
	 */
	private String appid;

	/** 
	* 业务场景标识
	 */
	private String bizid;

	/** 
	* 具体操作
	 */
	private String operate;

	/** 
	* 入参
	 */
	private String params;

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAppid() {
		return this.appid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}

	public String getBizid() {
		return this.bizid;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getOperate() {
		return this.operate;
	}

	public void setParams(String params) {
		this.params = params;
	}
	public void setParamsString(String params) {
		this.params = params;
	}

	public String getParams() {
		return this.params;
	}

	public String getApiMethodName() {
		return "alibaba.baichuan.appcontent.upload";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("appid", this.appid);
		txtParams.put("bizid", this.bizid);
		txtParams.put("operate", this.operate);
		txtParams.put("params", this.params);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlibabaBaichuanAppcontentUploadResponse> getResponseClass() {
		return AlibabaBaichuanAppcontentUploadResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}