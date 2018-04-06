package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * 验证token返回数据结构
 *
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountTokenValidateResult extends TaobaoObject {

	private static final long serialVersionUID = 4673491146822425917L;

	/**
	 * 错误码
	 */
	@ApiField("code")
	private Long code;

	/**
	 * token中的数据
	 */
	@ApiField("data")
	private TokenInfo data;

	/**
	 * 错误信息
	 */
	@ApiField("message")
	private String message;

	/**
	 * 是否成功
	 */
	@ApiField("successful")
	private Boolean successful;


	public Long getCode() {
		return this.code;
	}
	public void setCode(Long code) {
		this.code = code;
	}

	public TokenInfo getData() {
		return this.data;
	}
	public void setData(TokenInfo data) {
		this.data = data;
	}

	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getSuccessful() {
		return this.successful;
	}
	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

}
