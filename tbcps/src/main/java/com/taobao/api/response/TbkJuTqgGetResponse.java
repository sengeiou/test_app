package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.ju.tqg.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class TbkJuTqgGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6396423544554264958L;

	/** 
	 * 淘抢购对象
	 */
	@ApiListField("results")
	@ApiField("results")
	private List<Results> results;

	/** 
	 * 返回的结果数
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setResults(List<Results> results) {
		this.results = results;
	}
	public List<Results> getResults( ) {
		return this.results;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	
	/**
 * 淘抢购对象
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Results extends TaobaoObject {

	private static final long serialVersionUID = 6488498554676315589L;

	/**
		 * 类目名称
		 */
		@ApiField("category_name")
		private String categoryName;
		/**
		 * 商品链接（是淘客商品返回淘客链接，非淘客商品返回普通h5链接）
		 */
		@ApiField("click_url")
		private String clickUrl;
		/**
		 * 结束时间
		 */
		@ApiField("end_time")
		private String endTime;
		/**
		 * 商品ID
		 */
		@ApiField("num_iid")
		private Long numIid;
		/**
		 * 商品主图
		 */
		@ApiField("pic_url")
		private String picUrl;
		/**
		 * 商品原价
		 */
		@ApiField("reserve_price")
		private String reservePrice;
		/**
		 * 已抢购数量
		 */
		@ApiField("sold_num")
		private Long soldNum;
		/**
		 * 开团时间
		 */
		@ApiField("start_time")
		private String startTime;
		/**
		 * 商品标题
		 */
		@ApiField("title")
		private String title;
		/**
		 * 总库存
		 */
		@ApiField("total_amount")
		private Long totalAmount;
		/**
		 * 淘抢购活动价
		 */
		@ApiField("zk_final_price")
		private String zkFinalPrice;
	

	public String getCategoryName() {
			return this.categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getClickUrl() {
			return this.clickUrl;
		}
		public void setClickUrl(String clickUrl) {
			this.clickUrl = clickUrl;
		}
		public String getEndTime() {
			return this.endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public Long getNumIid() {
			return this.numIid;
		}
		public void setNumIid(Long numIid) {
			this.numIid = numIid;
		}
		public String getPicUrl() {
			return this.picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		public String getReservePrice() {
			return this.reservePrice;
		}
		public void setReservePrice(String reservePrice) {
			this.reservePrice = reservePrice;
		}
		public Long getSoldNum() {
			return this.soldNum;
		}
		public void setSoldNum(Long soldNum) {
			this.soldNum = soldNum;
		}
		public String getStartTime() {
			return this.startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getTitle() {
			return this.title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public Long getTotalAmount() {
			return this.totalAmount;
		}
		public void setTotalAmount(Long totalAmount) {
			this.totalAmount = totalAmount;
		}
		public String getZkFinalPrice() {
			return this.zkFinalPrice;
		}
		public void setZkFinalPrice(String zkFinalPrice) {
			this.zkFinalPrice = zkFinalPrice;
		}

}



}
