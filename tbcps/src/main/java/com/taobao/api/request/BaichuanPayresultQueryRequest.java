package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.BaichuanPayresultQueryResponse;

/**
 * TOP API: taobao.baichuan.payresult.query request
 * 
 * @author top auto create
 * @since 1.0, 2015.06.10
 */
public class BaichuanPayresultQueryRequest extends BaseTaobaoRequest<BaichuanPayresultQueryResponse> {
	
	

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
		return "taobao.baichuan.payresult.query";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("name", this.name);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<BaichuanPayresultQueryResponse> getResponseClass() {
		return BaichuanPayresultQueryResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}