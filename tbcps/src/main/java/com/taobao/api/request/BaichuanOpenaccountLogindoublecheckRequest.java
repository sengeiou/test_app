package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.BaichuanOpenaccountLogindoublecheckResponse;

/**
 * TOP API: taobao.baichuan.openaccount.logindoublecheck request
 * 
 * @author top auto create
 * @since 1.0, 2015.06.10
 */
public class BaichuanOpenaccountLogindoublecheckRequest extends BaseTaobaoRequest<BaichuanOpenaccountLogindoublecheckResponse> {
	
	

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
		return "taobao.baichuan.openaccount.logindoublecheck";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("name", this.name);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<BaichuanOpenaccountLogindoublecheckResponse> getResponseClass() {
		return BaichuanOpenaccountLogindoublecheckResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}