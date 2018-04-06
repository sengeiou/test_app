package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * Open Account模型
 *
 * @author top auto create
 * @since 1.0, null
 */
public class OpenaccountObject extends TaobaoObject {

	private static final long serialVersionUID = 4154241999543276756L;

	/**
	 * 错误码
	 */
	@ApiField("code")
	private Long code;

	/**
	 * 返回数据
	 */
	@ApiField("data")
	private OpenAccount data;

	/**
	 * 返回信息
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

	public OpenAccount getData() {
		return this.data;
	}
	public void setData(OpenAccount data) {
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
