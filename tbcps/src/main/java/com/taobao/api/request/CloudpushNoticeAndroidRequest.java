package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.CloudpushNoticeAndroidResponse;

/**
 * TOP API: taobao.cloudpush.notice.android request
 * 
 * @author top auto create
 * @since 1.0, 2015.06.08
 */
public class CloudpushNoticeAndroidRequest extends BaseTaobaoRequest<CloudpushNoticeAndroidResponse> {
	
	

	/** 
	* 通知摘要
	 */
	private String summary;

	/** 
	* 推送目标: device:推送给设备; account:推送给指定帐号,all: 推送给全部
	 */
	private String target;

	/** 
	* 根据Target来设定，如Target=device, 则对应的值为 设备id1,设备id2. 多个值使用逗号分隔
	 */
	private String targetValue;

	/** 
	* 通知的标题.
	 */
	private String title;

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}

	public String getTargetValue() {
		return this.targetValue;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public String getApiMethodName() {
		return "taobao.cloudpush.notice.android";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("summary", this.summary);
		txtParams.put("target", this.target);
		txtParams.put("target_value", this.targetValue);
		txtParams.put("title", this.title);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<CloudpushNoticeAndroidResponse> getResponseClass() {
		return CloudpushNoticeAndroidResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(summary, "summary");
		RequestCheckUtils.checkNotEmpty(target, "target");
		RequestCheckUtils.checkNotEmpty(targetValue, "targetValue");
		RequestCheckUtils.checkNotEmpty(title, "title");
	}
	

}