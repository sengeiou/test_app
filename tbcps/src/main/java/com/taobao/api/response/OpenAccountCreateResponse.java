package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.OpenaccountLong;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.open.account.create response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountCreateResponse extends TaobaoResponse {

	private static final long serialVersionUID = 1285654283841665993L;

	/** 
	 * 插入数据的Open Account Id的列表
	 */
	@ApiListField("datas")
	@ApiField("openaccount_long")
	private List<OpenaccountLong> datas;


	public void setDatas(List<OpenaccountLong> datas) {
		this.datas = datas;
	}
	public List<OpenaccountLong> getDatas( ) {
		return this.datas;
	}
	


}
