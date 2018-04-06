package com.taobao.api.response;

import com.taobao.api.internal.mapping.ApiField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.mtop.upload.token.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 */
public class MtopUploadTokenGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 5773996267382387785L;

	/** 
	 * code
	 */
	@ApiField("code")
	private String code;

	/** 
	 * 单次上传文件块最大大小，单位 byte
	 */
	@ApiField("max_body_length")
	private Long maxBodyLength;

	/** 
	 * 单个文件重试上传次数
	 */
	@ApiField("max_retry_times")
	private Long maxRetryTimes;

	/** 
	 * msg
	 */
	@ApiField("message")
	private String message;

	/** 
	 * 本次指定的上传文件服务器地址
	 */
	@ApiField("server_address")
	private String serverAddress;

	/** 
	 * token失效时间点
	 */
	@ApiField("timeout")
	private Long timeout;

	/** 
	 * 颁发的上传令牌
	 */
	@ApiField("token")
	private String token;


	public void setCode(String code) {
		this.code = code;
	}
	public String getCode( ) {
		return this.code;
	}

	public void setMaxBodyLength(Long maxBodyLength) {
		this.maxBodyLength = maxBodyLength;
	}
	public Long getMaxBodyLength( ) {
		return this.maxBodyLength;
	}

	public void setMaxRetryTimes(Long maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}
	public Long getMaxRetryTimes( ) {
		return this.maxRetryTimes;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage( ) {
		return this.message;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public String getServerAddress( ) {
		return this.serverAddress;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public Long getTimeout( ) {
		return this.timeout;
	}

	public void setToken(String token) {
		this.token = token;
	}
	public String getToken( ) {
		return this.token;
	}
	


}
