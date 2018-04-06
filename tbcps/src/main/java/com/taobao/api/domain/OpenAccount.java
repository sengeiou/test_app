package com.taobao.api.domain;

import java.util.Date;
import com.taobao.api.internal.mapping.ApiField;
import com.taobao.api.TaobaoObject;


/**
 * Open Account模型
 *
 * @author top auto create
 * @since 1.0, null
 */
public class OpenAccount extends TaobaoObject {

	private static final long serialVersionUID = 3129849568186672369L;

	/**
	 * 支付宝的帐号标识
	 */
	@ApiField("alipay_id")
	private String alipayId;

	/**
	 * 头像url
	 */
	@ApiField("avatar_url")
	private String avatarUrl;

	/**
	 * 银行卡号
	 */
	@ApiField("bank_card_no")
	private String bankCardNo;

	/**
	 * 银行卡的拥有者姓名
	 */
	@ApiField("bank_card_owner_name")
	private String bankCardOwnerName;

	/**
	 * 出生日期
	 */
	@ApiField("birthday")
	private String birthday;

	/**
	 * 创建帐号的App Key
	 */
	@ApiField("create_app_key")
	private String createAppKey;

	/**
	 * 帐号创建的设备的ID
	 */
	@ApiField("create_device_id")
	private String createDeviceId;

	/**
	 * 账号创建时的位置
	 */
	@ApiField("create_location")
	private String createLocation;

	/**
	 * 展示名
	 */
	@ApiField("display_name")
	private String displayName;

	/**
	 * 数据域
	 */
	@ApiField("domain_id")
	private Long domainId;

	/**
	 * 邮箱
	 */
	@ApiField("email")
	private String email;

	/**
	 * 自定义扩展信息Map的Json格式
	 */
	@ApiField("ext_infos")
	private String extInfos;

	/**
	 * 1男 2女
	 */
	@ApiField("gender")
	private Long gender;

	/**
	 * 记录创建时间
	 */
	@ApiField("gmt_create")
	private Date gmtCreate;

	/**
	 * 记录上次更新时间
	 */
	@ApiField("gmt_modified")
	private Date gmtModified;

	/**
	 * Open Account Id
	 */
	@ApiField("id")
	private Long id;

	/**
	 * 开发者自定义账号id
	 */
	@ApiField("isv_account_id")
	private String isvAccountId;

	/**
	 * 地区
	 */
	@ApiField("locale")
	private String locale;

	/**
	 * 登录名
	 */
	@ApiField("login_id")
	private String loginId;

	/**
	 * 密码
	 */
	@ApiField("login_pwd")
	private String loginPwd;

	/**
	 * 加密算法类型：1、代表单纯MD5，2：代表单一Salt的MD5，3、代表根据记录不同后的MD5
	 */
	@ApiField("login_pwd_encryption")
	private Long loginPwdEncryption;

	/**
	 * 密码加密强度
	 */
	@ApiField("login_pwd_intensity")
	private Long loginPwdIntensity;

	/**
	 * 密码salt
	 */
	@ApiField("login_pwd_salt")
	private String loginPwdSalt;

	/**
	 * 手机
	 */
	@ApiField("mobile")
	private String mobile;

	/**
	 * 姓名
	 */
	@ApiField("name")
	private String name;

	/**
	 * TAOBAO = 1;WEIXIN = 2;WEIBO = 3;QQ = 4;
	 */
	@ApiField("oauth_plateform")
	private Long oauthPlateform;

	/**
	 * 第三方oauth openid
	 */
	@ApiField("open_id")
	private String openId;

	/**
	 * 账号状态：1、启用，2、删除，3、禁用
	 */
	@ApiField("status")
	private Long status;

	/**
	 * 账号创建类型：1、通过短信创建，2、ISV批量导入，3、ISV OAuth创建
	 */
	@ApiField("type")
	private Long type;

	/**
	 * 记录的版本号
	 */
	@ApiField("version")
	private Long version;

	/**
	 * 旺旺
	 */
	@ApiField("wangwang")
	private String wangwang;

	/**
	 * 微信
	 */
	@ApiField("weixin")
	private String weixin;


	public String getAlipayId() {
		return this.alipayId;
	}
	public void setAlipayId(String alipayId) {
		this.alipayId = alipayId;
	}

	public String getAvatarUrl() {
		return this.avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getBankCardNo() {
		return this.bankCardNo;
	}
	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}

	public String getBankCardOwnerName() {
		return this.bankCardOwnerName;
	}
	public void setBankCardOwnerName(String bankCardOwnerName) {
		this.bankCardOwnerName = bankCardOwnerName;
	}

	public String getBirthday() {
		return this.birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getCreateAppKey() {
		return this.createAppKey;
	}
	public void setCreateAppKey(String createAppKey) {
		this.createAppKey = createAppKey;
	}

	public String getCreateDeviceId() {
		return this.createDeviceId;
	}
	public void setCreateDeviceId(String createDeviceId) {
		this.createDeviceId = createDeviceId;
	}

	public String getCreateLocation() {
		return this.createLocation;
	}
	public void setCreateLocation(String createLocation) {
		this.createLocation = createLocation;
	}

	public String getDisplayName() {
		return this.displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Long getDomainId() {
		return this.domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getExtInfos() {
		return this.extInfos;
	}
	public void setExtInfos(String extInfos) {
		this.extInfos = extInfos;
	}

	public Long getGender() {
		return this.gender;
	}
	public void setGender(Long gender) {
		this.gender = gender;
	}

	public Date getGmtCreate() {
		return this.gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return this.gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getIsvAccountId() {
		return this.isvAccountId;
	}
	public void setIsvAccountId(String isvAccountId) {
		this.isvAccountId = isvAccountId;
	}

	public String getLocale() {
		return this.locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLoginId() {
		return this.loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginPwd() {
		return this.loginPwd;
	}
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public Long getLoginPwdEncryption() {
		return this.loginPwdEncryption;
	}
	public void setLoginPwdEncryption(Long loginPwdEncryption) {
		this.loginPwdEncryption = loginPwdEncryption;
	}

	public Long getLoginPwdIntensity() {
		return this.loginPwdIntensity;
	}
	public void setLoginPwdIntensity(Long loginPwdIntensity) {
		this.loginPwdIntensity = loginPwdIntensity;
	}

	public String getLoginPwdSalt() {
		return this.loginPwdSalt;
	}
	public void setLoginPwdSalt(String loginPwdSalt) {
		this.loginPwdSalt = loginPwdSalt;
	}

	public String getMobile() {
		return this.mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Long getOauthPlateform() {
		return this.oauthPlateform;
	}
	public void setOauthPlateform(Long oauthPlateform) {
		this.oauthPlateform = oauthPlateform;
	}

	public String getOpenId() {
		return this.openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Long getStatus() {
		return this.status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getType() {
		return this.type;
	}
	public void setType(Long type) {
		this.type = type;
	}

	public Long getVersion() {
		return this.version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}

	public String getWangwang() {
		return this.wangwang;
	}
	public void setWangwang(String wangwang) {
		this.wangwang = wangwang;
	}

	public String getWeixin() {
		return this.weixin;
	}
	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

}
