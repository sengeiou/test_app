package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.ItemsGetResponse;

/**
 * TOP API: taobao.items.get request
 * 
 * @author top auto create
 * @since 1.0, 2015.06.11
 */
public class ItemsGetRequest extends BaseTaobaoRequest<ItemsGetResponse> {
	
	

	/** 
	* 商品所属类目Id,ItemCat中的cid字段。可以通过taobao.itemcats.get取到
	 */
	private Long cid;

	/** 
	* 商品最高价格。单位:元。正整数，取值范围:0-100000000
	 */
	private Long endPrice;

	/** 
	* 商品所属卖家的最大信用等级数，1表示1心，2表示2心……，设置此条件表示搜索结果里的商品，所属的卖家信用必须小于等于设置的end_score
	 */
	private Long endScore;

	/** 
	* 商品最近成交量最大值，设置此条件表示搜索结果里的商品，最近成交量必须小于等于设置的end_volume
	 */
	private Long endVolume;

	/** 
	* 需返回的商品结构字段列表。可选值为Item中的以下字段：num_iid,title,nick,pic_url,cid,price,type,delist_time,post_fee;多个字段用“,”分隔。如：num_iid,title。新增字段score(卖家信用等级数),volume(最近成交量),location（卖家地址，可以分别获取location.city,location.state）,num_iid商品数字id。此接口返回的post_fee是快递费用，volume对应搜索商品列表页的最近成交量
	 */
	private String fields;

	/** 
	* 是否正品保障商品(既是如实描述，又是7天无理由退换货的商品，设置了这个属性时：is_prepay和promoted_service不能再行设置)，设置为true表示该商品是正品保障的商品，设置为false或不设置表示不判断这个属性
	 */
	private Boolean genuineSecurity;

	/** 
	* 是否是3D淘宝的商品,置为false或为空表示不对是否3D商品进行判断
	 */
	private Boolean is3D;

	/** 
	* 是否支持货到付款，设置为true表示支持货到付款，设置为false或不设置表示不判断这个属性
	 */
	private Boolean isCod;

	/** 
	* 表示是否搜索商城的商品这一搜索条件。值含义：true：搜索商城商品，false：搜素集市商品，若为null，则搜索全部的商品（默认值）
	 */
	private Boolean isMall;

	/** 
	* 是否如实描述(即:先行赔付)商品，设置为true表示该商品是如实描述的商品，设置为false或不设置表示不判断这个属性
	 */
	private Boolean isPrepay;

	/** 
	* 表示是否新品这一搜索条件。值含义：true-是新品，false-无限制，null-无限制
	 */
	private Boolean isXinpin;

	/** 
	* 所在市。如：杭州。具体可以根据taobao.areas.get取到
	 */
	private String locationCity;

	/** 
	* 所在省。如：浙江。具体可以根据taobao.areas.get 取到
	 */
	private String locationState;

	/** 
	* 卖家昵称列表。多个之间以“,”分隔;最多支持5个卖家昵称。如：nick1,nick2,nick3
	 */
	private String nicks;

	/** 
	* 是否淘1站代购商品，设置为true表示淘1站商品，设置为false或不设置表示不判断这个属性
	 */
	private Boolean oneStation;

	/** 
	* 排序方式。格式为column:asc/desc,column可选值为: price（价格）, delist_time（下架时间）, seller_credit（卖家信用）,popularity(人气)。如按价格升序排列表示为：price:asc。新增排序字段：volume（最近成交量）
	 */
	private String orderBy;

	/** 
	* 页码。取值范围:大于零的整数。默认值为1,即默认返回第一页数据。用此接口获取数据时，当翻页获取的条数（page_no*page_size）超过10240,为了保护后台搜索引擎，接口将报错。所以请大家尽可能的细化自己的搜索条件，例如根据修改时间分段获取商品
	 */
	private Long pageNo;

	/** 
	* 每页条数。取值范围:大于零的整数;最大值:200;默认值:40
	 */
	private Long pageSize;

	/** 
	* 免运费（不设置包含所有邮费状态，设置为true结果只有卖家包邮的商品）。不能单独使用，要和其他条件一起用才行
	 */
	private Boolean postFree;

	/** 
	* 可以根据产品Id搜索属于这个spu的商品。这个字段可以通过查询 taobao.products.get 取到
	 */
	private Long productId;

	/** 
	* 是否提供保障服务的商品。可选入参有：2、4。设置为2表示该商品是“假一赔三”的商品，设置为4表示该商品是“7天无理由退换货”的商品
	 */
	private String promotedService;

	/** 
	* 商品属性。可以搜到拥有和输入的属性一样的商品列表。字段格式为：pid1:vid1;pid2:vid2.属性的pid调用taobao.itemprops.get取得，属性值的vid用taobao.itempropvalues.get取得vid。
	 */
	private String props;

	/** 
	* 搜索字段。 用来搜索商品的title以及商品所对应的产品的title
	 */
	private String q;

	/** 
	* 商品最低价格。单位:元。正整数，取值范围:0-100000000。
	 */
	private Long startPrice;

	/** 
	* 商品所属卖家的最小信用等级数，1表示1心，2表示2心……，设置此条件表示搜索结果里的商品，所属的卖家信用必须大于等于设置的start_score。
	 */
	private Long startScore;

	/** 
	* 商品最近成交量最小值，设置此条件表示搜索结果里的商品，最近成交量必须大于等于设置的start_volume。
	 */
	private Long startVolume;

	/** 
	* 商品的新旧状态。可选入参有：new、second、unused 。设置为new表示该商品是全新的商品，设置为second表示该商品是二手的商品，设置为unused表示该商品是闲置的商品
	 */
	private String stuffStatus;

	/** 
	* 旺旺在线状态（不设置结果包含所有状态，设置为true结果只有旺旺在线卖家的商品）。不能单独使用，要和其他条件一起用才行
	 */
	private Boolean wwStatus;

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Long getCid() {
		return this.cid;
	}

	public void setEndPrice(Long endPrice) {
		this.endPrice = endPrice;
	}

	public Long getEndPrice() {
		return this.endPrice;
	}

	public void setEndScore(Long endScore) {
		this.endScore = endScore;
	}

	public Long getEndScore() {
		return this.endScore;
	}

	public void setEndVolume(Long endVolume) {
		this.endVolume = endVolume;
	}

	public Long getEndVolume() {
		return this.endVolume;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getFields() {
		return this.fields;
	}

	public void setGenuineSecurity(Boolean genuineSecurity) {
		this.genuineSecurity = genuineSecurity;
	}

	public Boolean getGenuineSecurity() {
		return this.genuineSecurity;
	}

	public void setIs3D(Boolean is3D) {
		this.is3D = is3D;
	}

	public Boolean getIs3D() {
		return this.is3D;
	}

	public void setIsCod(Boolean isCod) {
		this.isCod = isCod;
	}

	public Boolean getIsCod() {
		return this.isCod;
	}

	public void setIsMall(Boolean isMall) {
		this.isMall = isMall;
	}

	public Boolean getIsMall() {
		return this.isMall;
	}

	public void setIsPrepay(Boolean isPrepay) {
		this.isPrepay = isPrepay;
	}

	public Boolean getIsPrepay() {
		return this.isPrepay;
	}

	public void setIsXinpin(Boolean isXinpin) {
		this.isXinpin = isXinpin;
	}

	public Boolean getIsXinpin() {
		return this.isXinpin;
	}

	public void setLocationCity(String locationCity) {
		this.locationCity = locationCity;
	}

	public String getLocationCity() {
		return this.locationCity;
	}

	public void setLocationState(String locationState) {
		this.locationState = locationState;
	}

	public String getLocationState() {
		return this.locationState;
	}

	public void setNicks(String nicks) {
		this.nicks = nicks;
	}

	public String getNicks() {
		return this.nicks;
	}

	public void setOneStation(Boolean oneStation) {
		this.oneStation = oneStation;
	}

	public Boolean getOneStation() {
		return this.oneStation;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderBy() {
		return this.orderBy;
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

	public void setPostFree(Boolean postFree) {
		this.postFree = postFree;
	}

	public Boolean getPostFree() {
		return this.postFree;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getProductId() {
		return this.productId;
	}

	public void setPromotedService(String promotedService) {
		this.promotedService = promotedService;
	}

	public String getPromotedService() {
		return this.promotedService;
	}

	public void setProps(String props) {
		this.props = props;
	}

	public String getProps() {
		return this.props;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getQ() {
		return this.q;
	}

	public void setStartPrice(Long startPrice) {
		this.startPrice = startPrice;
	}

	public Long getStartPrice() {
		return this.startPrice;
	}

	public void setStartScore(Long startScore) {
		this.startScore = startScore;
	}

	public Long getStartScore() {
		return this.startScore;
	}

	public void setStartVolume(Long startVolume) {
		this.startVolume = startVolume;
	}

	public Long getStartVolume() {
		return this.startVolume;
	}

	public void setStuffStatus(String stuffStatus) {
		this.stuffStatus = stuffStatus;
	}

	public String getStuffStatus() {
		return this.stuffStatus;
	}

	public void setWwStatus(Boolean wwStatus) {
		this.wwStatus = wwStatus;
	}

	public Boolean getWwStatus() {
		return this.wwStatus;
	}

	public String getApiMethodName() {
		return "taobao.items.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("cid", this.cid);
		txtParams.put("end_price", this.endPrice);
		txtParams.put("end_score", this.endScore);
		txtParams.put("end_volume", this.endVolume);
		txtParams.put("fields", this.fields);
		txtParams.put("genuine_security", this.genuineSecurity);
		txtParams.put("is_3D", this.is3D);
		txtParams.put("is_cod", this.isCod);
		txtParams.put("is_mall", this.isMall);
		txtParams.put("is_prepay", this.isPrepay);
		txtParams.put("is_xinpin", this.isXinpin);
		txtParams.put("location.city", this.locationCity);
		txtParams.put("location.state", this.locationState);
		txtParams.put("nicks", this.nicks);
		txtParams.put("one_station", this.oneStation);
		txtParams.put("order_by", this.orderBy);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("post_free", this.postFree);
		txtParams.put("product_id", this.productId);
		txtParams.put("promoted_service", this.promotedService);
		txtParams.put("props", this.props);
		txtParams.put("q", this.q);
		txtParams.put("start_price", this.startPrice);
		txtParams.put("start_score", this.startScore);
		txtParams.put("start_volume", this.startVolume);
		txtParams.put("stuff_status", this.stuffStatus);
		txtParams.put("ww_status", this.wwStatus);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<ItemsGetResponse> getResponseClass() {
		return ItemsGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkMinValue(cid, 0L, "cid");
		RequestCheckUtils.checkMinValue(endPrice, 0L, "endPrice");
		RequestCheckUtils.checkMinValue(endScore, 0L, "endScore");
		RequestCheckUtils.checkMinValue(endVolume, 0L, "endVolume");
		RequestCheckUtils.checkNotEmpty(fields, "fields");
		RequestCheckUtils.checkMinValue(productId, 0L, "productId");
		RequestCheckUtils.checkMinValue(startPrice, 0L, "startPrice");
		RequestCheckUtils.checkMinValue(startScore, 0L, "startScore");
		RequestCheckUtils.checkMinValue(startVolume, 0L, "startVolume");
	}
	

}