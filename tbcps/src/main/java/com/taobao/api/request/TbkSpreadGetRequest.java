package com.taobao.api.request;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.util.RequestCheckUtils;
import com.taobao.api.TaobaoObject;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.TbkSpreadGetResponse;

/**
 * TOP API: taobao.tbk.spread.get request
 * 
 * @author top auto create
 * @since 1.0, 2017.06.17
 */
public class TbkSpreadGetRequest extends BaseTaobaoRequest<TbkSpreadGetResponse> {
	
	

	/** 
	* 请求列表，内部包含多个url
	 */
	private String requests;

	public void setRequests(String requests) {
		this.requests = requests;
	}

	public void setRequests(List<TbkSpreadRequest> requests) {
		this.requests = new JSONWriter(false,true).write(requests);
	}

	public String getRequests() {
		return this.requests;
	}

	public String getApiMethodName() {
		return "taobao.tbk.spread.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("requests", this.requests);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkSpreadGetResponse> getResponseClass() {
		return TbkSpreadGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkObjectMaxListSize(requests, 20, "requests");
	}
	
	/**
 * 请求列表，内部包含多个url
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class TbkSpreadRequest extends TaobaoObject {

	private static final long serialVersionUID = 6194254358114952958L;

	/**
		 * 原始url, 只支持uland.taobao.com，s.click.taobao.com， ai.taobao.com，temai.taobao.com的域名转换，否则判错
		 */
		@ApiField("url")
		private String url;
	

	public String getUrl() {
			return this.url;
		}
		public void setUrl(String url) {
			this.url = url;
		}

}


}