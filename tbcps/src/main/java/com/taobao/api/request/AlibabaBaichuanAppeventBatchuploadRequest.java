package com.taobao.api.request;

import com.taobao.api.internal.util.json.JSONValidatingReader;
import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.internal.util.RequestCheckUtils;
import com.taobao.api.TaobaoObject;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.json.JSONWriter;
import com.taobao.api.response.AlibabaBaichuanAppeventBatchuploadResponse;

/**
 * TOP API: alibaba.baichuan.appevent.batchupload request
 * 
 * @author top auto create
 * @since 1.0, 2016.05.12
 */
public class AlibabaBaichuanAppeventBatchuploadRequest extends BaseTaobaoRequest<AlibabaBaichuanAppeventBatchuploadResponse> {
	
	

	/** 
	* app标识
	 */
	private String appid;

	/** 
	* 场景标识
	 */
	private String bizid;

	/** 
	* 具体实例集合
	 */
	private String params;

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAppid() {
		return this.appid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}

	public String getBizid() {
		return this.bizid;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public void setParams(List<String> params) {
		this.params = new JSONWriter(false,true).write(params);
	}
	public void setParamsString(String params) {
		this.params = params;
	}

	public String getParams() {
		return this.params;
	}

	public String getApiMethodName() {
		return "alibaba.baichuan.appevent.batchupload";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("appid", this.appid);
		txtParams.put("bizid", this.bizid);
		txtParams.put("params", this.params);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<AlibabaBaichuanAppeventBatchuploadResponse> getResponseClass() {
		return AlibabaBaichuanAppeventBatchuploadResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkObjectMaxListSize(params, 20, "params");
	}
	
	/**
 * 具体实例集合
 *
 * @author top auto create
 * @since 1.0, null
 */
public static class Json extends TaobaoObject {

	private static final long serialVersionUID = 6496691359518178712L;

	/**
		 * app用户id
		 */
		@ApiField("app_user_id")
		private String appUserId;
		/**
		 * 是否点击
		 */
		@ApiField("click")
		private String click;
		/**
		 * 内容id
		 */
		@ApiField("content_id")
		private String contentId;
		/**
		 * imei
		 */
		@ApiField("device_id")
		private String deviceId;
		/**
		 * 是否跳转
		 */
		@ApiField("is_out")
		private String isOut;
		/**
		 * 喜欢程度分数
		 */
		@ApiField("like_score")
		private String likeScore;
		/**
		 * 跳转链接
		 */
		@ApiField("out_url")
		private String outUrl;
		/**
		 * pvid
		 */
		@ApiField("pvid")
		private String pvid;
		/**
		 * scm埋点
		 */
		@ApiField("scm")
		private String scm;
		/**
		 * 淘系商品id
		 */
		@ApiField("tao_item_id")
		private String taoItemId;
		/**
		 * 淘系用户id
		 */
		@ApiField("tao_user_id")
		private String taoUserId;
		/**
		 * 淘系设备唯一id
		 */
		@ApiField("utdid")
		private String utdid;
	

	public String getAppUserId() {
			return this.appUserId;
		}
		public void setAppUserId(String appUserId) {
			this.appUserId = appUserId;
		}
		public String getClick() {
			return this.click;
		}
		public void setClick(String click) {
			this.click = click;
		}
		public String getContentId() {
			return this.contentId;
		}
		public void setContentId(String contentId) {
			this.contentId = contentId;
		}
		public String getDeviceId() {
			return this.deviceId;
		}
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		public String getIsOut() {
			return this.isOut;
		}
		public void setIsOut(String isOut) {
			this.isOut = isOut;
		}
		public String getLikeScore() {
			return this.likeScore;
		}
		public void setLikeScore(String likeScore) {
			this.likeScore = likeScore;
		}
		public String getOutUrl() {
			return this.outUrl;
		}
		public void setOutUrl(String outUrl) {
			this.outUrl = outUrl;
		}
		public String getPvid() {
			return this.pvid;
		}
		public void setPvid(String pvid) {
			this.pvid = pvid;
		}
		public String getScm() {
			return this.scm;
		}
		public void setScm(String scm) {
			this.scm = scm;
		}
		public String getTaoItemId() {
			return this.taoItemId;
		}
		public void setTaoItemId(String taoItemId) {
			this.taoItemId = taoItemId;
		}
		public String getTaoUserId() {
			return this.taoUserId;
		}
		public void setTaoUserId(String taoUserId) {
			this.taoUserId = taoUserId;
		}
		public String getUtdid() {
			return this.utdid;
		}
		public void setUtdid(String utdid) {
			this.utdid = utdid;
		}

}


}