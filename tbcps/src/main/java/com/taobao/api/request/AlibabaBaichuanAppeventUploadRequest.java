package com.taobao.api.request;

import com.taobao.api.internal.util.json.JSONValidatingReader;
import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.AlibabaBaichuanAppeventUploadResponse;

/**
 * TOP API: alibaba.baichuan.appevent.upload request
 * 
 * @author top auto create
 * @since 1.0, 2016.05.10
 */
public class AlibabaBaichuanAppeventUploadRequest extends BaseTaobaoRequest<AlibabaBaichuanAppeventUploadResponse> {
	
	

	/** 
	* 标识app
	 */
	private String appid;

	/** 
	* 标识场景
	 */
	private String bizid;

	/** 
	* 标识具体行为
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
		return "alibaba.baichuan.appevent.upload";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("appid", this.appid);
		txtParams.put("bizid", this.bizid);
		txtParams.put("params", this.params);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlibabaBaichuanAppeventUploadResponse> getResponseClass() {
		return AlibabaBaichuanAppeventUploadResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(appid, "appid");
		RequestCheckUtils.checkNotEmpty(bizid, "bizid");
		RequestCheckUtils.checkNotEmpty(params, "params");
	}
	

}