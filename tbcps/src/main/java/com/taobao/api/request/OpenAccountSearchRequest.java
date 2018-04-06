package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenAccountSearchResponse;

/**
 * TOP API: taobao.open.account.search request
 * 
 * @author top auto create
 * @since 1.0, 2017.04.13
 */
public class OpenAccountSearchRequest extends BaseTaobaoRequest<OpenAccountSearchResponse> {
	
	

	/** 
	* 基于阿里云OpenSearch服务，openSearch查询语法:https://help.aliyun.com/document_detail/29157.html，搜索服务能够基于id，loginId，mobile，email，isvAccountId，display_name,create_app_key,做搜索查询，示例中mobile可以基于模糊搜素，搜索135的账号，该搜索是分页返回，start表示开始行，hit表示一页返回值，最大500
	 */
	private String query;

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}

	public String getApiMethodName() {
		return "taobao.open.account.search";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("query", this.query);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenAccountSearchResponse> getResponseClass() {
		return OpenAccountSearchResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(query, "query");
	}
	

}