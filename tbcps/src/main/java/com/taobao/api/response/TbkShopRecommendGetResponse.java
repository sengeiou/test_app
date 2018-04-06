package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.NTbkShop;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.shop.recommend.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkShopRecommendGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 5692646956276231996L;

	/** 
	 * 淘宝客店铺
	 */
	@ApiListField("results")
	@ApiField("n_tbk_shop")
	private List<NTbkShop> results;


	public void setResults(List<NTbkShop> results) {
		this.results = results;
	}
	public List<NTbkShop> getResults( ) {
		return this.results;
	}
	


}
