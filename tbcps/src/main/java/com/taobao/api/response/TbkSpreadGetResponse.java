package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.spread.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkSpreadGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 7143149512633356265L;

	/** 
	 * 传播形式对象列表
	 */
	@ApiListField("results")
	@ApiField("tbk_spread")
	private List<TbkSpread> results;

	/** 
	 * totalResults
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setResults(List<TbkSpread> results) {
		this.results = results;
	}
	public List<TbkSpread> getResults( ) {
		return this.results;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	
	/**
 * 传播形式对象列表
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class TbkSpread extends TaobaoObject {

	private static final long serialVersionUID = 6473696785225532883L;

	/**
		 * 传播形式, 目前只支持短链接
		 */
		@ApiField("content")
		private String content;
		/**
		 * 调用错误信息；由于是批量接口，请重点关注每条请求返回的结果，如果非OK，则说明该结果对应的content不正常，请酌情处理;
		 */
		@ApiField("err_msg")
		private String errMsg;
	

	public String getContent() {
			return this.content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getErrMsg() {
			return this.errMsg;
		}
		public void setErrMsg(String errMsg) {
			this.errMsg = errMsg;
		}

}



}
