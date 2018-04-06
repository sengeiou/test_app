package com.taobao.api.response;

import java.util.List;
import com.taobao.api.domain.OpenaccountVoid;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.open.account.delete response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccountDeleteResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4757549521276185682L;

	/** 
	 * 删除结果
	 */
	@ApiListField("datas")
	@ApiField("openaccount_void")
	private List<OpenaccountVoid> datas;


	public void setDatas(List<OpenaccountVoid> datas) {
		this.datas = datas;
	}
	public List<OpenaccountVoid> getDatas( ) {
		return this.datas;
	}
	


}
