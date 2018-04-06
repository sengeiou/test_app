package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.domain.TbkFavorites;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.uatm.favorites.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkUatmFavoritesGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4827449555589692164L;

	/** 
	 * 淘宝客选品库
	 */
	@ApiListField("results")
	@ApiField("tbk_favorites")
	private List<TbkFavorites> results;

	/** 
	 * 选品库总数
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setResults(List<TbkFavorites> results) {
		this.results = results;
	}
	public List<TbkFavorites> getResults( ) {
		return this.results;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	


}
