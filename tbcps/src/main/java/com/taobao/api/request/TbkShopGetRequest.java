package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkShopGetResponse;

/**
 * TOP API: taobao.tbk.shop.get request
 * 
 * @author top auto create
 * @since 1.0, 2017.06.17
 */
public class TbkShopGetRequest extends BaseTaobaoRequest<TbkShopGetResponse> {
	
	

	/** 
	* 累计推广商品上限
	 */
	private Long endAuctionCount;

	/** 
	* 淘客佣金比率上限，1~10000
	 */
	private Long endCommissionRate;

	/** 
	* 信用等级上限，1~20
	 */
	private Long endCredit;

	/** 
	* 店铺商品总数上限
	 */
	private Long endTotalAction;

	/** 
	* 需返回的字段列表
	 */
	private String fields;

	/** 
	* 是否商城的店铺，设置为true表示该是属于淘宝商城的店铺，设置为false或不设置表示不判断这个属性
	 */
	private Boolean isTmall;

	/** 
	* 第几页，默认1，1~100
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
	* 排序_des（降序），排序_asc（升序），佣金比率（commission_rate）， 商品数量（auction_count），销售总数量（total_auction）
	 */
	private String sort;

	/** 
	* 累计推广商品下限
	 */
	private Long startAuctionCount;

	/** 
	* 淘客佣金比率下限，1~10000
	 */
	private Long startCommissionRate;

	/** 
	* 信用等级下限，1~20
	 */
	private Long startCredit;

	/** 
	* 店铺商品总数下限
	 */
	private Long startTotalAction;

	public void setEndAuctionCount(Long endAuctionCount) {
		this.endAuctionCount = endAuctionCount;
	}

	public Long getEndAuctionCount() {
		return this.endAuctionCount;
	}

	public void setEndCommissionRate(Long endCommissionRate) {
		this.endCommissionRate = endCommissionRate;
	}

	public Long getEndCommissionRate() {
		return this.endCommissionRate;
	}

	public void setEndCredit(Long endCredit) {
		this.endCredit = endCredit;
	}

	public Long getEndCredit() {
		return this.endCredit;
	}

	public void setEndTotalAction(Long endTotalAction) {
		this.endTotalAction = endTotalAction;
	}

	public Long getEndTotalAction() {
		return this.endTotalAction;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getFields() {
		return this.fields;
	}

	public void setIsTmall(Boolean isTmall) {
		this.isTmall = isTmall;
	}

	public Boolean getIsTmall() {
		return this.isTmall;
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

	public void setStartAuctionCount(Long startAuctionCount) {
		this.startAuctionCount = startAuctionCount;
	}

	public Long getStartAuctionCount() {
		return this.startAuctionCount;
	}

	public void setStartCommissionRate(Long startCommissionRate) {
		this.startCommissionRate = startCommissionRate;
	}

	public Long getStartCommissionRate() {
		return this.startCommissionRate;
	}

	public void setStartCredit(Long startCredit) {
		this.startCredit = startCredit;
	}

	public Long getStartCredit() {
		return this.startCredit;
	}

	public void setStartTotalAction(Long startTotalAction) {
		this.startTotalAction = startTotalAction;
	}

	public Long getStartTotalAction() {
		return this.startTotalAction;
	}

	public String getApiMethodName() {
		return "taobao.tbk.shop.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("end_auction_count", this.endAuctionCount);
		txtParams.put("end_commission_rate", this.endCommissionRate);
		txtParams.put("end_credit", this.endCredit);
		txtParams.put("end_total_action", this.endTotalAction);
		txtParams.put("fields", this.fields);
		txtParams.put("is_tmall", this.isTmall);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("platform", this.platform);
		txtParams.put("q", this.q);
		txtParams.put("sort", this.sort);
		txtParams.put("start_auction_count", this.startAuctionCount);
		txtParams.put("start_commission_rate", this.startCommissionRate);
		txtParams.put("start_credit", this.startCredit);
		txtParams.put("start_total_action", this.startTotalAction);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkShopGetResponse> getResponseClass() {
		return TbkShopGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(fields, "fields");
		RequestCheckUtils.checkNotEmpty(q, "q");
	}
	

}