package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.NTbkItem;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.item.recommend.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkItemRecommendGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4278856662288877177L;

	/** 
	 * 淘宝客商品
	 */
	@ApiListField("results")
	@ApiField("n_tbk_item")
	private List<NTbkItem> results;


	public void setResults(List<NTbkItem> results) {
		this.results = results;
	}
	public List<NTbkItem> getResults( ) {
		return this.results;
	}
	


}
