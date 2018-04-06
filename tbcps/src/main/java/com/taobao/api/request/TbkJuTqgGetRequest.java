package com.taobao.api.request;

import java.util.Date;
import com.taobao.api.internal.util.RequestCheckUtils;
import java.util.Map;

import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.TaobaoHashMap;

import com.taobao.api.response.TbkJuTqgGetResponse;

/**
 * TOP API: taobao.tbk.ju.tqg.get request
 * 
 * @author top auto create
 * @since 1.0, 2017.06.17
 */
public class TbkJuTqgGetRequest extends BaseTaobaoRequest<TbkJuTqgGetResponse> {
	
	

	/** 
	* 推广位id（推广位申请方式：http://club.alimama.com/read.php?spm=0.0.0.0.npQdST&tid=6306396&ds=1&page=1&toread=1）
	 */
	private Long adzoneId;

	/** 
	* 最晚开团时间
	 */
	private Date endTime;

	/** 
	* 需返回的字段列表
	 */
	private String fields;

	/** 
	* 第几页，默认1，1~100
	 */
	private Long pageNo;

	/** 
	* 页大小，默认40，1~40
	 */
	private Long pageSize;

	/** 
	* 最早开团时间
	 */
	private Date startTime;

	public void setAdzoneId(Long adzoneId) {
		this.adzoneId = adzoneId;
	}

	public Long getAdzoneId() {
		return this.adzoneId;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return this.endTime;
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

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public String getApiMethodName() {
		return "taobao.tbk.ju.tqg.get";
	}

	public Map<String, String> getTextParams() {		
		TaobaoHashMap txtParams = new TaobaoHashMap();
		txtParams.put("adzone_id", this.adzoneId);
		txtParams.put("end_time", this.endTime);
		txtParams.put("fields", this.fields);
		txtParams.put("page_no", this.pageNo);
		txtParams.put("page_size", this.pageSize);
		txtParams.put("start_time", this.startTime);
		if(this.udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public Class<TbkJuTqgGetResponse> getResponseClass() {
		return TbkJuTqgGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(adzoneId, "adzoneId");
		RequestCheckUtils.checkNotEmpty(endTime, "endTime");
		RequestCheckUtils.checkNotEmpty(fields, "fields");
		RequestCheckUtils.checkNotEmpty(startTime, "startTime");
	}
	

}