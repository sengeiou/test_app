package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.cloudpush.notice.android response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class CloudpushNoticeAndroidResponse extends TaobaoResponse {

	private static final long serialVersionUID = 2398782618651794596L;

	/** 
	 * 请求是否成功
	 */
	@ApiField("is_success")
	private Boolean isSuccess;

	/** 
	 * 请求的错误代码.
	 */
	@ApiField("request_error_code")
	private Long requestErrorCode;

	/** 
	 * 请求的错误信息.
	 */
	@ApiField("request_error_msg")
	private String requestErrorMsg;


	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public Boolean getIsSuccess( ) {
		return this.isSuccess;
	}

	public void setRequestErrorCode(Long requestErrorCode) {
		this.requestErrorCode = requestErrorCode;
	}
	public Long getRequestErrorCode( ) {
		return this.requestErrorCode;
	}

	public void setRequestErrorMsg(String requestErrorMsg) {
		this.requestErrorMsg = requestErrorMsg;
	}
	public String getRequestErrorMsg( ) {
		return this.requestErrorMsg;
	}
	


}
