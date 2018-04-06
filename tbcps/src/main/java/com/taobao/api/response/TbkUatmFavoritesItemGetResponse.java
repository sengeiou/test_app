package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.UatmTbkItem;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.uatm.favorites.item.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkUatmFavoritesItemGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4768434995471773839L;

	/** 
	 * 招商宝贝信息
	 */
	@ApiListField("results")
	@ApiField("uatm_tbk_item")
	private List<UatmTbkItem> results;

	/** 
	 * 选品库中的商品总条数
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setResults(List<UatmTbkItem> results) {
		this.results = results;
	}
	public List<UatmTbkItem> getResults( ) {
		return this.results;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	


}
