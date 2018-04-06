package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.OpenaccountObject;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.open.account.list response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountListResponse extends TaobaoResponse {

	private static final long serialVersionUID = 8758324999592435672L;

	/** 
	 * 返回信息
	 */
	@ApiListField("datas")
	@ApiField("openaccount_object")
	private List<OpenaccountObject> datas;


	public void setDatas(List<OpenaccountObject> datas) {
		this.datas = datas;
	}
	public List<OpenaccountObject> getDatas( ) {
		return this.datas;
	}
	


}
