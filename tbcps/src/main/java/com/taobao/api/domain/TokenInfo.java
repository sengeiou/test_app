package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * token信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public class TokenInfo extends TaobaoObject {

	private static final long serialVersionUID = 6465699324739727613L;

	/**
	 * token info扩展信息
	 */
	@ApiField("ext")
	private TokenInfoExt ext;

	/**
	 * isv自己账号的唯一id
	 */
	@ApiField("isv_account_id")
	private String isvAccountId;

	/**
	 * ISV APP的登录态时长
	 */
	@ApiField("login_state_expire_in")
	private Long loginStateExpireIn;

	/**
	 * open account id
	 */
	@ApiField("open_account_id")
	private Long openAccountId;

	/**
	 * 时间戳
	 */
	@ApiField("timestamp")
	private Long timestamp;

	/**
	 * 用于防重放的唯一id
	 */
	@ApiField("uuid")
	private String uuid;


	public TokenInfoExt getExt() {
		return this.ext;
	}
	public void setExt(TokenInfoExt ext) {
		this.ext = ext;
	}

	public String getIsvAccountId() {
		return this.isvAccountId;
	}
	public void setIsvAccountId(String isvAccountId) {
		this.isvAccountId = isvAccountId;
	}

	public Long getLoginStateExpireIn() {
		return this.loginStateExpireIn;
	}
	public void setLoginStateExpireIn(Long loginStateExpireIn) {
		this.loginStateExpireIn = loginStateExpireIn;
	}

	public Long getOpenAccountId() {
		return this.openAccountId;
	}
	public void setOpenAccountId(Long openAccountId) {
		this.openAccountId = openAccountId;
	}

	public Long getTimestamp() {
		return this.timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUuid() {
		return this.uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
