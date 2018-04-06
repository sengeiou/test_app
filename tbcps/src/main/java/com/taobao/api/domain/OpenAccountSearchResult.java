package com.taobao.api.domain;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import com.taobao.api.internal.mapping.ApiListField;


/**
 * 搜索查询返回结果
 *
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountSearchResult extends TaobaoObject {

	private static final long serialVersionUID = 4635619966462585114L;

	/**
	 * 状态码
	 */
	@ApiField("code")
	private Long code;

	/**
	 * OpenAccount的列表
	 */
	@ApiListField("datas")
	@ApiField("open_account")
	private List<OpenAccount> datas;

	/**
	 * 状态信息
	 */
	@ApiField("message")
	private String message;

	/**
	 * 查询是否成功，成功返回时有可能数据为空
	 */
	@ApiField("successful")
	private Boolean successful;


	public Long getCode() {
		return this.code;
	}
	public void setCode(Long code) {
		this.code = code;
	}

	public List<OpenAccount> getDatas() {
		return this.datas;
	}
	public void setDatas(List<OpenAccount> datas) {
		this.datas = datas;
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
