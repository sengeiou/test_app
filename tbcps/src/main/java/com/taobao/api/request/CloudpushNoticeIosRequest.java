package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.CloudpushNoticeIosResponse;

/**
 * TOP API: taobao.cloudpush.notice.ios request
 * 
 * @author top auto create
 * @since 1.0, 2015.08.10
 */
public class CloudpushNoticeIosRequest extends BaseTaobaoRequest<CloudpushNoticeIosResponse> {
	
	

	/** 
	* iOS的通知是通过APNS中心来发送的，需要填写对应的环境信息.  DEV:表示开发环境, PRODUCT: 表示生产环境.
	 */
	private String env;

	/** 
	* 提供给IOS通知的扩展属性，如角标或者声音等,注意：参数值为json
	 */
	private String ext;

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

	public void setEnv(String env) {
		this.env = env;
	}

	public String getEnv() {
		return this.env;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getExt() {
		return this.ext;
	}

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

	public String getApiMethodName() {
		return "taobao.cloudpush.notice.ios";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("env", this.env);
		txtParams.put("ext", this.ext);
		txtParams.put("summary", this.summary);
		txtParams.put("target", this.target);
		txtParams.put("target_value", this.targetValue);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<CloudpushNoticeIosResponse> getResponseClass() {
		return CloudpushNoticeIosResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(env, "env");
		RequestCheckUtils.checkNotEmpty(summary, "summary");
		RequestCheckUtils.checkNotEmpty(target, "target");
		RequestCheckUtils.checkNotEmpty(targetValue, "targetValue");
	}
	

}