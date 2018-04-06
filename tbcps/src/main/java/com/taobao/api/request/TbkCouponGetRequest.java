package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkCouponGetResponse;

/**
 * TOP API: taobao.tbk.coupon.get request
 * 
 * @author top auto create
 * @since 1.0, 2018.01.05
 */
public class TbkCouponGetRequest extends BaseTaobaoRequest<TbkCouponGetResponse> {
	
	

	/** 
	* 券ID
	 */
	private String activityId;

	/** 
	* 商品ID
	 */
	private Long itemId;

	/** 
	* 带券ID与商品ID的加密串
	 */
	private String me;

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityId() {
		return this.activityId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getItemId() {
		return this.itemId;
	}

	public void setMe(String me) {
		this.me = me;
	}

	public String getMe() {
		return this.me;
	}

	public String getApiMethodName() {
		return "taobao.tbk.coupon.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("activity_id", this.activityId);
		txtParams.put("item_id", this.itemId);
		txtParams.put("me", this.me);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkCouponGetResponse> getResponseClass() {
		return TbkCouponGetResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}