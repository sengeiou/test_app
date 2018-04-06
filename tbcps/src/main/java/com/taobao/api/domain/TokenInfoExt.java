package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * token info中的扩展字段
 *
 * @author top auto create
 * @since 1.0, null
 */
public class TokenInfoExt extends TaobaoObject {

	private static final long serialVersionUID = 7654448133268394269L;

	/**
	 * open account当前token info中open account id对应的open account信息
	 */
	@ApiField("open_account")
	private OpenAccount openAccount;


	public OpenAccount getOpenAccount() {
		return this.openAccount;
	}
	public void setOpenAccount(OpenAccount openAccount) {
		this.openAccount = openAccount;
	}

}
