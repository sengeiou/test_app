package com.taobao.api.domain;

import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * 系统自动生成
 *
 * @author top auto create
 * @since 1.0, null
 */
public class UploadTokenRequestV extends TaobaoObject {

	private static final long serialVersionUID = 6352256965195748769L;

	/**
	 * 多媒体中心分配的业务码, mtopupload或其他
	 */
	@ApiField("biz_code")
	private String bizCode;

	/**
	 * 客户端IP
	 */
	@ApiField("client_ip")
	private String clientIp;

	/**
	 * 客户端网络类型 wifi 或 2g 或 3g 或 cdma 或 gprs
	 */
	@ApiField("client_net_type")
	private String clientNetType;

	/**
	 * 文件内容的CRC32校验和
	 */
	@ApiField("crc")
	private Long crc;

	/**
	 * 文件名
	 */
	@ApiField("file_name")
	private String fileName;

	/**
	 * 文件大小，单位byte
	 */
	@ApiField("file_size")
	private Long fileSize;

	/**
	 * 自定义数据
	 */
	@ApiField("private_data")
	private String privateData;

	/**
	 * 上传类型：resumable 或 normal
	 */
	@ApiField("upload_type")
	private String uploadType;

	/**
	 * session
	 */
	@ApiField("user_id")
	private Long userId;


	public String getBizCode() {
		return this.bizCode;
	}
	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public String getClientIp() {
		return this.clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getClientNetType() {
		return this.clientNetType;
	}
	public void setClientNetType(String clientNetType) {
		this.clientNetType = clientNetType;
	}

	public Long getCrc() {
		return this.crc;
	}
	public void setCrc(Long crc) {
		this.crc = crc;
	}

	public String getFileName() {
		return this.fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return this.fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getPrivateData() {
		return this.privateData;
	}
	public void setPrivateData(String privateData) {
		this.privateData = privateData;
	}

	public String getUploadType() {
		return this.uploadType;
	}
	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public Long getUserId() {
		return this.userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
