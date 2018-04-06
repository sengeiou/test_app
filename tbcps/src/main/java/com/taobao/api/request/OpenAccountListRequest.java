package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenAccountListResponse;

/**
 * TOP API: taobao.open.account.list request
 * 
 * @author top auto create
 * @since 1.0, 2015.08.10
 */
public class OpenAccountListRequest extends BaseTaobaoRequest<OpenAccountListResponse> {
	
	

	/** 
	* ISV自己账号的id列表，isvAccountId和openAccountId二选一必填, 每次最多查询 20 个帐户
	 */
	private String isvAccountIds;

	/** 
	* Open Account的id列表, 每次最多查询 20 个帐户
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
		return "taobao.open.account.list";
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

	public Class<OpenAccountListResponse> getResponseClass() {
		return OpenAccountListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkMaxListSize(isvAccountIds, 20, "isvAccountIds");
		RequestCheckUtils.checkMaxListSize(openAccountIds, 20, "openAccountIds");
	}
	

}