package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkUatmEventGetResponse;

/**
 * TOP API: taobao.tbk.uatm.event.get request
 * 
 * @author top auto create
 * @since 1.0, 2016.04.29
 */
public class TbkUatmEventGetRequest extends BaseTaobaoRequest<TbkUatmEventGetResponse> {
	
	

	/** 
	* 需要返回的字段列表，不能为空，字段名之间使用逗号分隔
	 */
	private String fields;

	/** 
	* 默认1，第几页，从1开始计数
	 */
	private Long pageNo;

	/** 
	* 默认20,  页大小，即每一页的活动个数
	 */
	private Long pageSize;

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getFields() {
		return this.fields;
	}

	public void setPageNo(Long pageNo) {
		this.pageNo = pageNo;
	}

	public Long getPageNo() {
		return this.pageNo;
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	public Long getPageSize() {
		return this.pageSize;
	}

	public String getApiMethodName() {
		return "taobao.tbk.uatm.event.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("fields", this.fields);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkUatmEventGetResponse> getResponseClass() {
		return TbkUatmEventGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(fields, "fields");
	}
	

}