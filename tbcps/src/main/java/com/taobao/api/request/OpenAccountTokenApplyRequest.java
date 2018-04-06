package com.taobao.api.request;

import com.taobao.api.internal.util.json.JSONValidatingReader;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.OpenAccountTokenApplyResponse;

/**
 * TOP API: taobao.open.account.token.apply request
 * 
 * @author top auto create
 * @since 1.0, 2017.07.31
 */
public class OpenAccountTokenApplyRequest extends BaseTaobaoRequest<OpenAccountTokenApplyResponse> {
	
	

	/** 
	* 用于透传一些业务附加参数
	 */
	private String ext;

	/** 
	* isv自己账号的唯一id
	 */
	private String isvAccountId;

	/** 
	* ISV APP的登录态时长单位是秒
	 */
	private Long loginStateExpireIn;

	/** 
	* open account id
	 */
	private Long openAccountId;

	/** 
	* 时间戳单位是毫秒
	 */
	private Long tokenTimestamp;

	/** 
	* 用于防重放的唯一id
	 */
	private String uuid;

	public void setExt(String ext) {
		this.ext = ext;
	}
	public void setExtString(String ext) {
		this.ext = ext;
	}

	public String getExt() {
		return this.ext;
	}

	public void setIsvAccountId(String isvAccountId) {
		this.isvAccountId = isvAccountId;
	}

	public String getIsvAccountId() {
		return this.isvAccountId;
	}

	public void setLoginStateExpireIn(Long loginStateExpireIn) {
		this.loginStateExpireIn = loginStateExpireIn;
	}

	public Long getLoginStateExpireIn() {
		return this.loginStateExpireIn;
	}

	public void setOpenAccountId(Long openAccountId) {
		this.openAccountId = openAccountId;
	}

	public Long getOpenAccountId() {
		return this.openAccountId;
	}

	public void setTokenTimestamp(Long tokenTimestamp) {
		this.tokenTimestamp = tokenTimestamp;
	}

	public Long getTokenTimestamp() {
		return this.tokenTimestamp;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getApiMethodName() {
		return "taobao.open.account.token.apply";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("ext", this.ext);
		txtParams.put("isv_account_id", this.isvAccountId);
		txtParams.put("login_state_expire_in", this.loginStateExpireIn);
		txtParams.put("open_account_id", this.openAccountId);
		txtParams.put("token_timestamp", this.tokenTimestamp);
		txtParams.put("uuid", this.uuid);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<OpenAccountTokenApplyResponse> getResponseClass() {
		return OpenAccountTokenApplyResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}