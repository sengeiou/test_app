package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.CloudpushMessageIosResponse;

/**
 * TOP API: taobao.cloudpush.message.ios request
 * 
 * @author top auto create
 * @since 1.0, 2015.06.09
 */
public class CloudpushMessageIosRequest extends BaseTaobaoRequest<CloudpushMessageIosResponse> {
	
	

	/** 
	* 发送的消息内容.
	 */
	private String body;

	/** 
	* 推送目标: device:推送给设备; account:推送给指定帐号,all: 推送给全部
	 */
	private String target;

	/** 
	* 根据Target来设定，如Target=device, 则对应的值为 设备id1,设备id2. 多个值使用逗号分隔
	 */
	private String targetValue;

	public void setBody(String body) {
		this.body = body;
	}

	public String getBody() {
		return this.body;
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
		return "taobao.cloudpush.message.ios";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("body", this.body);
		txtParams.put("target", this.target);
		txtParams.put("target_value", this.targetValue);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<CloudpushMessageIosResponse> getResponseClass() {
		return CloudpushMessageIosResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(body, "body");
		RequestCheckUtils.checkNotEmpty(target, "target");
		RequestCheckUtils.checkNotEmpty(targetValue, "targetValue");
	}
	

}