package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenAccountIndexFindResponse;

/**
 * TOP API: taobao.open.account.index.find request
 * 
 * @author top auto create
 * @since 1.0, 2016.09.28
 */
public class OpenAccountIndexFindRequest extends BaseTaobaoRequest<OpenAccountIndexFindResponse> {
	
	

	/** 
	* int MOBILE         = 1;int EMAIL          = 2;int ISV_ACCOUNT_ID = 3;int LOGIN_ID       = 4;int OPEN_ID        = 5;
	 */
	private Long indexType;

	/** 
	* 具体值，当索引类型是 OPEN_ID 是，格式为 oauthPlatform|openId，即使用竖线分隔的组合值
	 */
	private String indexValue;

	public void setIndexType(Long indexType) {
		this.indexType = indexType;
	}

	public Long getIndexType() {
		return this.indexType;
	}

	public void setIndexValue(String indexValue) {
		this.indexValue = indexValue;
	}

	public String getIndexValue() {
		return this.indexValue;
	}

	public String getApiMethodName() {
		return "taobao.open.account.index.find";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("index_type", this.indexType);
		txtParams.put("index_value", this.indexValue);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenAccountIndexFindResponse> getResponseClass() {
		return OpenAccountIndexFindResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}