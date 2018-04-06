package com.taobao.api.request;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.JuItemsSearchResponse;

/**
 * TOP API: taobao.ju.items.search request
 * 
 * @author top auto create
 * @since 1.0, 2017.04.14
 */
public class JuItemsSearchRequest extends BaseTaobaoRequest<JuItemsSearchResponse> {
	
	

	/** 
	* query
	 */
	private String paramTopItemQuery;

	public void setParamTopItemQuery(String paramTopItemQuery) {
		this.paramTopItemQuery = paramTopItemQuery;
	}

	public void setParamTopItemQuery(TopItemQuery paramTopItemQuery) {
		this.paramTopItemQuery = new JSONWriter(false,true).write(paramTopItemQuery);
	}

	public String getParamTopItemQuery() {
		return this.paramTopItemQuery;
	}

	public String getApiMethodName() {
		return "taobao.ju.items.search";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("param_top_item_query", this.paramTopItemQuery);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<JuItemsSearchResponse> getResponseClass() {
		return JuItemsSearchResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	
	/**
 * query
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class TopItemQuery extends TaobaoObject {

	private static final long serialVersionUID = 7619159219726834611L;

	/**
		 * 页码,必传
		 */
		@ApiField("current_page")
		private Long currentPage;
		/**
		 * 一页大小,必传
		 */
		@ApiField("page_size")
		private Long pageSize;
		/**
		 * 媒体pid,必传
		 */
		@ApiField("pid")
		private String pid;
		/**
		 * 是否包邮,可不传
		 */
		@ApiField("postage")
		private Boolean postage;
		/**
		 * 状态，预热：1，正在进行中：2,可不传
		 */
		@ApiField("status")
		private Long status;
		/**
		 * 淘宝类目id,可不传
		 */
		@ApiField("taobao_category_id")
		private Long taobaoCategoryId;
		/**
		 * 搜索关键词,可不传
		 */
		@ApiField("word")
		private String word;
	

	public Long getCurrentPage() {
			return this.currentPage;
		}
		public void setCurrentPage(Long currentPage) {
			this.currentPage = currentPage;
		}
		public Long getPageSize() {
			return this.pageSize;
		}
		public void setPageSize(Long pageSize) {
			this.pageSize = pageSize;
		}
		public String getPid() {
			return this.pid;
		}
		public void setPid(String pid) {
			this.pid = pid;
		}
		public Boolean getPostage() {
			return this.postage;
		}
		public void setPostage(Boolean postage) {
			this.postage = postage;
		}
		public Long getStatus() {
			return this.status;
		}
		public void setStatus(Long status) {
			this.status = status;
		}
		public Long getTaobaoCategoryId() {
			return this.taobaoCategoryId;
		}
		public void setTaobaoCategoryId(Long taobaoCategoryId) {
			this.taobaoCategoryId = taobaoCategoryId;
		}
		public String getWord() {
			return this.word;
		}
		public void setWord(String word) {
			this.word = word;
		}

}


}