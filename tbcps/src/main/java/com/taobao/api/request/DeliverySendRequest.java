package com.taobao.api.request;

import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.DeliverySendResponse;

/**
 * TOP API: taobao.delivery.send request
 * 
 * @author top auto create
 * @since 1.0, 2015.08.06
 */
public class DeliverySendRequest extends BaseTaobaoRequest<DeliverySendResponse> {
	
	

	/** 
	* 物流公司代码.如"POST"就代表中国邮政,"ZJS"就代表宅急送.调用 taobao.logistics.companies.get 获取。如传入的代码非淘宝官方物流合作公司，默认是“其他”物流的方式，在淘宝不显示物流具体进度，故传入需谨慎。如果orderType为delivery_needed，则必传
	 */
	private String companyCode;

	/** 
	* feature参数格式<br>范例: mobileCode=tid1:手机串号1,手机串号2|tid2:手机串号3;machineCode=tid3:3C机器号A,3C机器号B<br>mobileCode无忧购的KEY,machineCode为3C的KEY,多个key之间用”;”分隔<br>“tid1:手机串号1,手机串号2|tid2:手机串号3”为mobileCode对应的value。"|"不同商品间的分隔符。<br>例A商品和B商品都属于无忧购商品，之间就用"|"分开。<br>TID就是商品代表的子订单号，对应taobao.trade.fullinfo.get 接口获得的oid字段。(通过OID可以唯一定位到当前商品上)<br>":"TID和具体传入参数间的分隔符。冒号前表示TID,之后代表该商品的参数属性。<br>"," 属性间分隔符。（对应商品数量，当存在一个商品的数量超过1个时，用逗号分开）。<br>具体:当订单中A商品的数量为2个，其中手机串号分别为"12345","67890"。<br>参数格式：mobileCode=TIDA:12345,67890。TIDA对应了A宝贝，冒号后用逗号分隔的"12345","67890".说明本订单A宝贝的数量为2，值分别为"12345","67890"。<br>当存在"|"时，就说明订单中存在多个无忧购的商品，商品间用"|"分隔了开来。|"之后的内容含义同上。
	 */
	private String feature;

	/** 
	* 物流公司取货地址.XXX街道XXX门牌,省市区不需要提供.目的在于让物流公司能清楚的知道在哪取货。校验规则：1.4-60字符(字母\数字\汉字)2.不能全部数字3.不能全部字母
	 */
	private String fetcherAddress;

	/** 
	* 取货地国家公布的标准地区码.参考:http://www.stats.gov.cn/tjbz/xzqhdm/t20080215_402462675.htm 或者调用 taobao.areas.get 获取
	 */
	private Long fetcherAreaId;

	/** 
	* 取货地手机号码
	 */
	private String fetcherMobile;

	/** 
	* 联系人名称
	 */
	private String fetcherName;

	/** 
	* 取货地固定电话.包含区号,电话,分机号,中间用 " – "; 取货地固定电话和取货地手机号码,必须填写一个.
	 */
	private String fetcherPhone;

	/** 
	* 取货地邮编
	 */
	private String fetcherZip;

	/** 
	* 卖家备注.最大长度为250个字符。如果orderType为delivery_needed，则必传
	 */
	private String memo;

	/** 
	* 发货类型. 可选( delivery_needed(物流订单发货),virtual_goods(虚拟物品发货). ) 注:选择virtual_goods类型进行发货的话下面的参数可以不需填写。如果选择delivery_needed 则company_code,out_sid,seller_name,seller_area_id,seller_address,seller_zip,seller_phone,seller_mobile,memo必须要填写
	 */
	private String orderType;

	/** 
	* 运单号.具体一个物流公司的真实运单号码。淘宝官方物流会校验，请谨慎传入；若company_code中传入的代码非淘宝官方物流合作公司，此处运单号不校验。如果orderType为delivery_needed，则必传
	 */
	private String outSid;

	/** 
	* 卖家地址(详细地址).如:XXX街道XXX门牌,省市区不需要提供。如果orderType为delivery_needed，则必传.<br><font color="red">校验规则：<br>1.4-60字符(字母\数字\汉字)<br>2.不能全部数字<br>3.不能全部字母<br></font>
	 */
	private String sellerAddress;

	/** 
	* 卖家所在地国家公布的标准地区码.参考:http://www.stats.gov.cn/tjbz/xzqhdm/t20080215_402462675.htm  或者调用 taobao.areas.get 获取。如果orderType为delivery_needed，则必传
	 */
	private Long sellerAreaId;

	/** 
	* 卖家手机号码，必须由8到16位数字构成<br><font color="red">校验规则：<br>1.8-16位数字<br>2.不能数字全部相同<br>3.不能全为字符格式</font>
	 */
	private String sellerMobile;

	/** 
	* 卖家姓名。如果orderType为delivery_needed。<font color=red>注：最长支持15个字符</font color=red>
	 */
	private String sellerName;

	/** 
	* 卖家固定电话.包含区号,电话,分机号,中间用 " – "; 卖家固定电话和卖家手机号码,必须填写一个.<br><font color="red">校验规则：<br>1.字符不能全部相同<br>2.长度：5-24位<br>3.只能包含数字和横杠‘-’</font>
	 */
	private String sellerPhone;

	/** 
	* 卖家邮编。如果orderType为delivery_needed，则必传
	 */
	private String sellerZip;

	/** 
	* 交易ID
	 */
	private Long tid;

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyCode() {
		return this.companyCode;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getFeature() {
		return this.feature;
	}

	public void setFetcherAddress(String fetcherAddress) {
		this.fetcherAddress = fetcherAddress;
	}

	public String getFetcherAddress() {
		return this.fetcherAddress;
	}

	public void setFetcherAreaId(Long fetcherAreaId) {
		this.fetcherAreaId = fetcherAreaId;
	}

	public Long getFetcherAreaId() {
		return this.fetcherAreaId;
	}

	public void setFetcherMobile(String fetcherMobile) {
		this.fetcherMobile = fetcherMobile;
	}

	public String getFetcherMobile() {
		return this.fetcherMobile;
	}

	public void setFetcherName(String fetcherName) {
		this.fetcherName = fetcherName;
	}

	public String getFetcherName() {
		return this.fetcherName;
	}

	public void setFetcherPhone(String fetcherPhone) {
		this.fetcherPhone = fetcherPhone;
	}

	public String getFetcherPhone() {
		return this.fetcherPhone;
	}

	public void setFetcherZip(String fetcherZip) {
		this.fetcherZip = fetcherZip;
	}

	public String getFetcherZip() {
		return this.fetcherZip;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderType() {
		return this.orderType;
	}

	public void setOutSid(String outSid) {
		this.outSid = outSid;
	}

	public String getOutSid() {
		return this.outSid;
	}

	public void setSellerAddress(String sellerAddress) {
		this.sellerAddress = sellerAddress;
	}

	public String getSellerAddress() {
		return this.sellerAddress;
	}

	public void setSellerAreaId(Long sellerAreaId) {
		this.sellerAreaId = sellerAreaId;
	}

	public Long getSellerAreaId() {
		return this.sellerAreaId;
	}

	public void setSellerMobile(String sellerMobile) {
		this.sellerMobile = sellerMobile;
	}

	public String getSellerMobile() {
		return this.sellerMobile;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSellerName() {
		return this.sellerName;
	}

	public void setSellerPhone(String sellerPhone) {
		this.sellerPhone = sellerPhone;
	}

	public String getSellerPhone() {
		return this.sellerPhone;
	}

	public void setSellerZip(String sellerZip) {
		this.sellerZip = sellerZip;
	}

	public String getSellerZip() {
		return this.sellerZip;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}

	public Long getTid() {
		return this.tid;
	}

	public String getApiMethodName() {
		return "taobao.delivery.send";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("company_code", this.companyCode);
		txtParams.put("feature", this.feature);
		txtParams.put("fetcher_address", this.fetcherAddress);
		txtParams.put("fetcher_area_id", this.fetcherAreaId);
		txtParams.put("fetcher_mobile", this.fetcherMobile);
		txtParams.put("fetcher_name", this.fetcherName);
		txtParams.put("fetcher_phone", this.fetcherPhone);
		txtParams.put("fetcher_zip", this.fetcherZip);
		txtParams.put("memo", this.memo);
		txtParams.put("order_type", this.orderType);
		txtParams.put("out_sid", this.outSid);
		txtParams.put("seller_address", this.sellerAddress);
		txtParams.put("seller_area_id", this.sellerAreaId);
		txtParams.put("seller_mobile", this.sellerMobile);
		txtParams.put("seller_name", this.sellerName);
		txtParams.put("seller_phone", this.sellerPhone);
		txtParams.put("seller_zip", this.sellerZip);
		txtParams.put("tid", this.tid);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<DeliverySendResponse> getResponseClass() {
		return DeliverySendResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(tid, "tid");
	}
	

}