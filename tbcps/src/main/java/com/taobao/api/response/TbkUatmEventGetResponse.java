package com.taobao.api.response;

import java.util.List;
import com.taobao.api.domain.TbkEvent;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.uatm.event.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkUatmEventGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 3796288567856777412L;

	/** 
	 * 淘客定向招商活动基本信息
	 */
	@ApiListField("results")
	@ApiField("tbk_event")
	private List<TbkEvent> results;

	/** 
	 * 当前进行中的招商活动总条数
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setResults(List<TbkEvent> results) {
		this.results = results;
	}
	public List<TbkEvent> getResults( ) {
		return this.results;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	


}
