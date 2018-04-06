package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkUatmFavoritesItemGetResponse;

/**
 * TOP API: taobao.tbk.uatm.favorites.item.get request
 * 
 * @author top auto create
 * @since 1.0, 2017.07.26
 */
public class TbkUatmFavoritesItemGetRequest extends BaseTaobaoRequest<TbkUatmFavoritesItemGetResponse> {
	
	

	/** 
	* 推广位id，需要在淘宝联盟后台创建；且属于appkey备案的媒体id（siteid），如何获取adzoneid，请参考，http://club.alimama.com/read-htm-tid-6333967.html?spm=0.0.0.0.msZnx5
	 */
	private Long adzoneId;

	/** 
	* 选品库的id
	 */
	private Long favoritesId;

	/** 
	* 需要输出则字段列表，逗号分隔
	 */
	private String fields;

	/** 
	* 第几页，默认：1，从1开始计数
	 */
	private Long pageNo;

	/** 
	* 页大小，默认20，1~100
	 */
	private Long pageSize;

	/** 
	* 链接形式：1：PC，2：无线，默认：１
	 */
	private Long platform;

	/** 
	* 自定义输入串，英文和数字组成，长度不能大于12个字符，区分不同的推广渠道
	 */
	private String unid;

	public void setAdzoneId(Long adzoneId) {
		this.adzoneId = adzoneId;
	}

	public Long getAdzoneId() {
		return this.adzoneId;
	}

	public void setFavoritesId(Long favoritesId) {
		this.favoritesId = favoritesId;
	}

	public Long getFavoritesId() {
		return this.favoritesId;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getFields() {
		return this.fields;
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

	public void setPlatform(Long platform) {
		this.platform = platform;
	}

	public Long getPlatform() {
		return this.platform;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public String getUnid() {
		return this.unid;
	}

	public String getApiMethodName() {
		return "taobao.tbk.uatm.favorites.item.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("adzone_id", this.adzoneId);
		txtParams.put("favorites_id", this.favoritesId);
		txtParams.put("fields", this.fields);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("platform", this.platform);
		txtParams.put("unid", this.unid);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkUatmFavoritesItemGetResponse> getResponseClass() {
		return TbkUatmFavoritesItemGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(adzoneId, "adzoneId");
		RequestCheckUtils.checkNotEmpty(favoritesId, "favoritesId");
		RequestCheckUtils.checkNotEmpty(fields, "fields");
	}
	

}