package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.MediaCategoryUpdateResponse;

/**
 * TOP API: taobao.media.category.update request
 * 
 * @author top auto create
 * @since 1.0, 2016.03.10
 */
public class MediaCategoryUpdateRequest extends BaseTaobaoRequest<MediaCategoryUpdateResponse> {
	
	

	/** 
	* 文件分类ID,不能为空
	 */
	private Long mediaCategoryId;

	/** 
	* 文件分类名，最大长度20字符，中英文都算一字符,不能为空
	 */
	private String mediaCategoryName;

	public void setMediaCategoryId(Long mediaCategoryId) {
		this.mediaCategoryId = mediaCategoryId;
	}

	public Long getMediaCategoryId() {
		return this.mediaCategoryId;
	}

	public void setMediaCategoryName(String mediaCategoryName) {
		this.mediaCategoryName = mediaCategoryName;
	}

	public String getMediaCategoryName() {
		return this.mediaCategoryName;
	}

	public String getApiMethodName() {
		return "taobao.media.category.update";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("media_category_id", this.mediaCategoryId);
		txtParams.put("media_category_name", this.mediaCategoryName);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<MediaCategoryUpdateResponse> getResponseClass() {
		return MediaCategoryUpdateResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(mediaCategoryId, "mediaCategoryId");
		RequestCheckUtils.checkNotEmpty(mediaCategoryName, "mediaCategoryName");
	}
	

}