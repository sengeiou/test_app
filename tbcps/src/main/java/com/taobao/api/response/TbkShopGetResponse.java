package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.NTbkShop;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.shop.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkShopGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 1425482381367574926L;

	/** 
	 * 淘宝客店铺
	 */
	@ApiListField("results")
	@ApiField("n_tbk_shop")
	private List<NTbkShop> results;

	/** 
	 * 搜索到符合条件的结果总数
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setResults(List<NTbkShop> results) {
		this.results = results;
	}
	public List<NTbkShop> getResults( ) {
		return this.results;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	


}
