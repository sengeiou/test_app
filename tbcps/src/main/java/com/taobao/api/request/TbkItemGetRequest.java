package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkItemGetResponse;

/**
 * TOP API: taobao.tbk.item.get request
 * 
 * @author top auto create
 * @since 1.0, 2016.07.25
 */
public class TbkItemGetRequest extends BaseTaobaoRequest<TbkItemGetResponse> {
	
	

	/** 
	* 后台类目ID，用,分割，最大10个，该ID可以通过taobao.itemcats.get接口获取到
	 */
	private String cat;

	/** 
	* 折扣价范围上限，单位：元
	 */
	private Long endPrice;

	/** 
	* 淘客佣金比率下限，如：1234表示12.34%
	 */
	private Long endTkRate;

	/** 
	* 需返回的字段列表
	 */
	private String fields;

	/** 
	* 是否海外商品，设置为true表示该商品是属于海外商品，设置为false或不设置表示不判断这个属性
	 */
	private Boolean isOverseas;

	/** 
	* 是否商城商品，设置为true表示该商品是属于淘宝商城商品，设置为false或不设置表示不判断这个属性
	 */
	private Boolean isTmall;

	/** 
	* 所在地
	 */
	private String itemloc;

	/** 
	* 第几页，默认：１
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
	* 查询词
	 */
	private String q;

	/** 
	* 排序_des（降序），排序_asc（升序），销量（total_sales），淘客佣金比率（tk_rate）， 累计推广量（tk_total_sales），总支出佣金（tk_total_commi）
	 */
	private String sort;

	/** 
	* 折扣价范围下限，单位：元
	 */
	private Long startPrice;

	/** 
	* 淘客佣金比率上限，如：1234表示12.34%
	 */
	private Long startTkRate;

	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getCat() {
		return this.cat;
	}

	public void setEndPrice(Long endPrice) {
		this.endPrice = endPrice;
	}

	public Long getEndPrice() {
		return this.endPrice;
	}

	public void setEndTkRate(Long endTkRate) {
		this.endTkRate = endTkRate;
	}

	public Long getEndTkRate() {
		return this.endTkRate;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getFields() {
		return this.fields;
	}

	public void setIsOverseas(Boolean isOverseas) {
		this.isOverseas = isOverseas;
	}

	public Boolean getIsOverseas() {
		return this.isOverseas;
	}

	public void setIsTmall(Boolean isTmall) {
		this.isTmall = isTmall;
	}

	public Boolean getIsTmall() {
		return this.isTmall;
	}

	public void setItemloc(String itemloc) {
		this.itemloc = itemloc;
	}

	public String getItemloc() {
		return this.itemloc;
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

	public void setQ(String q) {
		this.q = q;
	}

	public String getQ() {
		return this.q;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSort() {
		return this.sort;
	}

	public void setStartPrice(Long startPrice) {
		this.startPrice = startPrice;
	}

	public Long getStartPrice() {
		return this.startPrice;
	}

	public void setStartTkRate(Long startTkRate) {
		this.startTkRate = startTkRate;
	}

	public Long getStartTkRate() {
		return this.startTkRate;
	}

	public String getApiMethodName() {
		return "taobao.tbk.item.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("cat", this.cat);
		txtParams.put("end_price", this.endPrice);
		txtParams.put("end_tk_rate", this.endTkRate);
		txtParams.put("fields", this.fields);
		txtParams.put("is_overseas", this.isOverseas);
		txtParams.put("is_tmall", this.isTmall);
		txtParams.put("itemloc", this.itemloc);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("platform", this.platform);
		txtParams.put("q", this.q);
		txtParams.put("sort", this.sort);
		txtParams.put("start_price", this.startPrice);
		txtParams.put("start_tk_rate", this.startTkRate);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkItemGetResponse> getResponseClass() {
		return TbkItemGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(fields, "fields");
	}
	

}