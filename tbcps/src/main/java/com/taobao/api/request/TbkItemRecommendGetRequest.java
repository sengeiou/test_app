package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkItemRecommendGetResponse;

/**
 * TOP API: taobao.tbk.item.recommend.get request
 * 
 * @author top auto create
 * @since 1.0, 2017.06.17
 */
public class TbkItemRecommendGetRequest extends BaseTaobaoRequest<TbkItemRecommendGetResponse> {
	
	

	/** 
	* 返回数量，默认20，最大值40
	 */
	private Long count;

	/** 
	* 需返回的字段列表
	 */
	private String fields;

	/** 
	* 商品Id
	 */
	private Long numIid;

	/** 
	* 链接形式：1：PC，2：无线，默认：１
	 */
	private Long platform;

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getCount() {
		return this.count;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getFields() {
		return this.fields;
	}

	public void setNumIid(Long numIid) {
		this.numIid = numIid;
	}

	public Long getNumIid() {
		return this.numIid;
	}

	public void setPlatform(Long platform) {
		this.platform = platform;
	}

	public Long getPlatform() {
		return this.platform;
	}

	public String getApiMethodName() {
		return "taobao.tbk.item.recommend.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("count", this.count);
		txtParams.put("fields", this.fields);
		txtParams.put("num_iid", this.numIid);
		txtParams.put("platform", this.platform);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkItemRecommendGetResponse> getResponseClass() {
		return TbkItemRecommendGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(fields, "fields");
		RequestCheckUtils.checkNotEmpty(numIid, "numIid");
	}
	

}