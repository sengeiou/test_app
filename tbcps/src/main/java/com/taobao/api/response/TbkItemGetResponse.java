package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.NTbkItem;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.item.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkItemGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 3734717148132441299L;

	/** 
	 * 淘宝客商品
	 */
	@ApiListField("results")
	@ApiField("n_tbk_item")
	private List<NTbkItem> results;

	/** 
	 * 搜索到符合条件的结果总数
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setResults(List<NTbkItem> results) {
		this.results = results;
	}
	public List<NTbkItem> getResults( ) {
		return this.results;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	


}
