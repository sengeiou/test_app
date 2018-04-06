package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkScNewuserOrderGetResponse;

/**
 * TOP API: taobao.tbk.sc.newuser.order.get request
 * 
 * @author top auto create
 * @since 1.0, 2018.02.05
 */
public class TbkScNewuserOrderGetRequest extends BaseTaobaoRequest<TbkScNewuserOrderGetResponse> {
	
	

	/** 
	* mm_xxx_xxx_xxx的第三位
	 */
	private Long adzoneId;

	/** 
	* 页码，默认1
	 */
	private Long pageNo;

	/** 
	* 页大小，默认20，1~100
	 */
	private Long pageSize;

	/** 
	* mm_xxx_xxx_xxx的第二位
	 */
	private Long siteId;

	public void setAdzoneId(Long adzoneId) {
		this.adzoneId = adzoneId;
	}

	public Long getAdzoneId() {
		return this.adzoneId;
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

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getSiteId() {
		return this.siteId;
	}

	public String getApiMethodName() {
		return "taobao.tbk.sc.newuser.order.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("adzone_id", this.adzoneId);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("site_id", this.siteId);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkScNewuserOrderGetResponse> getResponseClass() {
		return TbkScNewuserOrderGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkMaxValue(pageSize, 100L, "pageSize");
		RequestCheckUtils.checkMinValue(pageSize, 1L, "pageSize");
	}
	

}