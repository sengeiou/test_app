package com.taobao.api.request;

import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.ItemItemvideosGetResponse;

/**
 * TOP API: taobao.item.itemvideos.get request
 * 
 * @author top auto create
 * @since 1.0, 2016.03.10
 */
public class ItemItemvideosGetRequest extends BaseTaobaoRequest<ItemItemvideosGetResponse> {
	
	

	/** 
	* 商品id ，传入商品id则不支持分页
	 */
	private Long itemId;

	/** 
	* 页码。取值范围:大于零的整数; 默认值:1,即返回第一页数据。
	 */
	private Long pageNo;

	/** 
	* 每页条数。取值范围:大于零的整数;最大值:200;默认值:40。
	 */
	private Long pageSize;

	/** 
	* 视频id，传入视频id则不支持分页
	 */
	private Long videoId;

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getItemId() {
		return this.itemId;
	}

	public void setPageNo(Long pageNo) {
		this.pageNo = pageNo;
	}

	public Long getPageNo() {
		return this.pageNo;
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	public Long getPageSize() {
		return this.pageSize;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getVideoId() {
		return this.videoId;
	}

	public String getApiMethodName() {
		return "taobao.item.itemvideos.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("item_id", this.itemId);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("video_id", this.videoId);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<ItemItemvideosGetResponse> getResponseClass() {
		return ItemItemvideosGetResponse.class;
	}

	public void check() throws ApiRuleException {
	}
	

}