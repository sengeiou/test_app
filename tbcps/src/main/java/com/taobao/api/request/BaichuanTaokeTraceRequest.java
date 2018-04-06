package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.BaichuanTaokeTraceResponse;

/**
 * TOP API: taobao.baichuan.taoke.trace request
 * 
 * @author top auto create
 * @since 1.0, 2016.03.08
 */
public class BaichuanTaokeTraceRequest extends BaseTaobaoRequest<BaichuanTaokeTraceResponse> {
	
	

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
		return "taobao.baichuan.taoke.trace";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("name", this.name);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<BaichuanTaokeTraceResponse> getResponseClass() {
		return BaichuanTaokeTraceResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}