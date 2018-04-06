package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkTpwdCreateResponse;

/**
 * TOP API: taobao.tbk.tpwd.create request
 * 
 * @author top auto create
 * @since 1.0, 2017.08.23
 */
public class TbkTpwdCreateRequest extends BaseTaobaoRequest<TbkTpwdCreateResponse> {
	
	

	/** 
	* 扩展字段JSON格式
	 */
	private String ext;

	/** 
	* 口令弹框logoURL
	 */
	private String logo;

	/** 
	* 口令弹框内容
	 */
	private String text;

	/** 
	* 口令跳转目标页
	 */
	private String url;

	/** 
	* 生成口令的淘宝用户ID
	 */
	private String userId;

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getExt() {
		return this.ext;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public String getApiMethodName() {
		return "taobao.tbk.tpwd.create";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("ext", this.ext);
		txtParams.put("logo", this.logo);
		txtParams.put("text", this.text);
		txtParams.put("url", this.url);
		txtParams.put("user_id", this.userId);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkTpwdCreateResponse> getResponseClass() {
		return TbkTpwdCreateResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(text, "text");
		RequestCheckUtils.checkNotEmpty(url, "url");
	}
	

}