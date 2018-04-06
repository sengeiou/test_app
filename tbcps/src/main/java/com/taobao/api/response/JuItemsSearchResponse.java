package com.taobao.api.response;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.ju.items.search response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class JuItemsSearchResponse extends TaobaoResponse {

	private static final long serialVersionUID = 4265972691635999913L;

	/** 
	 * 返回结果
	 */
	@ApiField("result")
	private PaginationResult result;


	public void setResult(PaginationResult result) {
		this.result = result;
	}
	public PaginationResult getResult( ) {
		return this.result;
	}
	
	/**
 * 扩展属性
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Extend extends TaobaoObject {

	private static final long serialVersionUID = 8846751759818734618L;

	/**
		 * empty
		 */
		@ApiField("empty")
		private Boolean empty;
	

	public Boolean getEmpty() {
			return this.empty;
		}
		public void setEmpty(Boolean empty) {
			this.empty = empty;
		}

}

	/**
 * 商品数据
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Items extends TaobaoObject {

	private static final long serialVersionUID = 7688917789157826171L;

	/**
		 * 聚划算价格，单位分
		 */
		@ApiField("act_price")
		private String actPrice;
		/**
		 * 类目名称
		 */
		@ApiField("category_name")
		private String categoryName;
		/**
		 * itemId
		 */
		@ApiField("item_id")
		private Long itemId;
		/**
		 * 商品卖点
		 */
		@ApiListField("item_usp_list")
		@ApiField("string")
		private List<String> itemUspList;
		/**
		 * 聚划算id
		 */
		@ApiField("ju_id")
		private Long juId;
		/**
		 * 开团结束时间
		 */
		@ApiField("online_end_time")
		private Long onlineEndTime;
		/**
		 * 开团时间
		 */
		@ApiField("online_start_time")
		private Long onlineStartTime;
		/**
		 * 原价
		 */
		@ApiField("orig_price")
		private String origPrice;
		/**
		 * 是否包邮
		 */
		@ApiField("pay_postage")
		private Boolean payPostage;
		/**
		 * pc链接
		 */
		@ApiField("pc_url")
		private String pcUrl;
		/**
		 * pc主图
		 */
		@ApiField("pic_url_for_p_c")
		private String picUrlForPC;
		/**
		 * 无线主图
		 */
		@ApiField("pic_url_for_w_l")
		private String picUrlForWL;
		/**
		 * 频道id
		 */
		@ApiField("platform_id")
		private Long platformId;
		/**
		 * 价格卖点
		 */
		@ApiListField("price_usp_list")
		@ApiField("string")
		private List<String> priceUspList;
		/**
		 * 展示结束时间
		 */
		@ApiField("show_end_time")
		private Long showEndTime;
		/**
		 * 开始展示时间
		 */
		@ApiField("show_start_time")
		private Long showStartTime;
		/**
		 * 淘宝类目id
		 */
		@ApiField("tb_first_cat_id")
		private Long tbFirstCatId;
		/**
		 * 商品标题
		 */
		@ApiField("title")
		private String title;
		/**
		 * 卖点描述
		 */
		@ApiListField("usp_desc_list")
		@ApiField("string")
		private List<String> uspDescList;
		/**
		 * 无线链接
		 */
		@ApiField("wap_url")
		private String wapUrl;
	

	public String getActPrice() {
			return this.actPrice;
		}
		public void setActPrice(String actPrice) {
			this.actPrice = actPrice;
		}
		public String getCategoryName() {
			return this.categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public Long getItemId() {
			return this.itemId;
		}
		public void setItemId(Long itemId) {
			this.itemId = itemId;
		}
		public List<String> getItemUspList() {
			return this.itemUspList;
		}
		public void setItemUspList(List<String> itemUspList) {
			this.itemUspList = itemUspList;
		}
		public Long getJuId() {
			return this.juId;
		}
		public void setJuId(Long juId) {
			this.juId = juId;
		}
		public Long getOnlineEndTime() {
			return this.onlineEndTime;
		}
		public void setOnlineEndTime(Long onlineEndTime) {
			this.onlineEndTime = onlineEndTime;
		}
		public Long getOnlineStartTime() {
			return this.onlineStartTime;
		}
		public void setOnlineStartTime(Long onlineStartTime) {
			this.onlineStartTime = onlineStartTime;
		}
		public String getOrigPrice() {
			return this.origPrice;
		}
		public void setOrigPrice(String origPrice) {
			this.origPrice = origPrice;
		}
		public Boolean getPayPostage() {
			return this.payPostage;
		}
		public void setPayPostage(Boolean payPostage) {
			this.payPostage = payPostage;
		}
		public String getPcUrl() {
			return this.pcUrl;
		}
		public void setPcUrl(String pcUrl) {
			this.pcUrl = pcUrl;
		}
		public String getPicUrlForPC() {
			return this.picUrlForPC;
		}
		public void setPicUrlForPC(String picUrlForPC) {
			this.picUrlForPC = picUrlForPC;
		}
		public String getPicUrlForWL() {
			return this.picUrlForWL;
		}
		public void setPicUrlForWL(String picUrlForWL) {
			this.picUrlForWL = picUrlForWL;
		}
		public Long getPlatformId() {
			return this.platformId;
		}
		public void setPlatformId(Long platformId) {
			this.platformId = platformId;
		}
		public List<String> getPriceUspList() {
			return this.priceUspList;
		}
		public void setPriceUspList(List<String> priceUspList) {
			this.priceUspList = priceUspList;
		}
		public Long getShowEndTime() {
			return this.showEndTime;
		}
		public void setShowEndTime(Long showEndTime) {
			this.showEndTime = showEndTime;
		}
		public Long getShowStartTime() {
			return this.showStartTime;
		}
		public void setShowStartTime(Long showStartTime) {
			this.showStartTime = showStartTime;
		}
		public Long getTbFirstCatId() {
			return this.tbFirstCatId;
		}
		public void setTbFirstCatId(Long tbFirstCatId) {
			this.tbFirstCatId = tbFirstCatId;
		}
		public String getTitle() {
			return this.title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public List<String> getUspDescList() {
			return this.uspDescList;
		}
		public void setUspDescList(List<String> uspDescList) {
			this.uspDescList = uspDescList;
		}
		public String getWapUrl() {
			return this.wapUrl;
		}
		public void setWapUrl(String wapUrl) {
			this.wapUrl = wapUrl;
		}

}

	/**
 * 埋点信息
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Trackparams extends TaobaoObject {

	private static final long serialVersionUID = 8692798796925425871L;

	/**
		 * empty
		 */
		@ApiField("empty")
		private Boolean empty;
	

	public Boolean getEmpty() {
			return this.empty;
		}
		public void setEmpty(Boolean empty) {
			this.empty = empty;
		}

}

	/**
 * 返回结果
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class PaginationResult extends TaobaoObject {

	private static final long serialVersionUID = 2634868869349777212L;

	/**
		 * 当前页码
		 */
		@ApiField("current_page")
		private Long currentPage;
		/**
		 * 扩展属性
		 */
		@ApiField("extend")
		private Extend extend;
		/**
		 * 商品数据
		 */
		@ApiListField("model_list")
		@ApiField("items")
		private List<Items> modelList;
		/**
		 * 错误码
		 */
		@ApiField("msg_code")
		private String msgCode;
		/**
		 * 错误信息
		 */
		@ApiField("msg_info")
		private String msgInfo;
		/**
		 * 一页大小
		 */
		@ApiField("page_size")
		private Long pageSize;
		/**
		 * 请求是否成功
		 */
		@ApiField("success")
		private Boolean success;
		/**
		 * 商品总数
		 */
		@ApiField("total_item")
		private Long totalItem;
		/**
		 * 总页数
		 */
		@ApiField("total_page")
		private Long totalPage;
		/**
		 * 埋点信息
		 */
		@ApiField("track_params")
		private Trackparams trackParams;
	

	public Long getCurrentPage() {
			return this.currentPage;
		}
		public void setCurrentPage(Long currentPage) {
			this.currentPage = currentPage;
		}
		public Extend getExtend() {
			return this.extend;
		}
		public void setExtend(Extend extend) {
			this.extend = extend;
		}
		public List<Items> getModelList() {
			return this.modelList;
		}
		public void setModelList(List<Items> modelList) {
			this.modelList = modelList;
		}
		public String getMsgCode() {
			return this.msgCode;
		}
		public void setMsgCode(String msgCode) {
			this.msgCode = msgCode;
		}
		public String getMsgInfo() {
			return this.msgInfo;
		}
		public void setMsgInfo(String msgInfo) {
			this.msgInfo = msgInfo;
		}
		public Long getPageSize() {
			return this.pageSize;
		}
		public void setPageSize(Long pageSize) {
			this.pageSize = pageSize;
		}
		public Boolean getSuccess() {
			return this.success;
		}
		public void setSuccess(Boolean success) {
			this.success = success;
		}
		public Long getTotalItem() {
			return this.totalItem;
		}
		public void setTotalItem(Long totalItem) {
			this.totalItem = totalItem;
		}
		public Long getTotalPage() {
			return this.totalPage;
		}
		public void setTotalPage(Long totalPage) {
			this.totalPage = totalPage;
		}
		public Trackparams getTrackParams() {
			return this.trackParams;
		}
		public void setTrackParams(Trackparams trackParams) {
			this.trackParams = trackParams;
		}

}



}
