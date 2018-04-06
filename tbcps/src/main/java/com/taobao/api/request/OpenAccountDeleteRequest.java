package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenAccountDeleteResponse;

/**
 * TOP API: taobao.open.account.delete request
 * 
 * @author top auto create
 * @since 1.0, 2015.04.20
 */
public class OpenAccountDeleteRequest extends BaseTaobaoRequest<OpenAccountDeleteResponse> {
	
	

	/** 
	* ISV自己账号的id列表
	 */
	private String isvAccountIds;

	/** 
	* Open Account的id列表
	 */
	private String openAccountIds;

	public void setIsvAccountIds(String isvAccountIds) {
		this.isvAccountIds = isvAccountIds;
	}

	public String getIsvAccountIds() {
		return this.isvAccountIds;
	}

	public void setOpenAccountIds(String openAccountIds) {
		this.openAccountIds = openAccountIds;
	}

	public String getOpenAccountIds() {
		return this.openAccountIds;
	}

	public String getApiMethodName() {
		return "taobao.open.account.delete";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("isv_account_ids", this.isvAccountIds);
		txtParams.put("open_account_ids", this.openAccountIds);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenAccountDeleteResponse> getResponseClass() {
		return OpenAccountDeleteResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkMaxListSize(isvAccountIds, 20, "isvAccountIds");
		RequestCheckUtils.checkMaxListSize(openAccountIds, 20, "openAccountIds");
	}
	

}