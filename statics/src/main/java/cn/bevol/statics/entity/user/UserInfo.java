package cn.bevol.statics.entity.user;

import cn.bevol.statics.entity.MongoBase;
import cn.bevol.statics.entity.metadata.Tag;
import cn.bevol.statics.entity.metadata.UserBaseInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.response.ReturnData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 * 
 * @author hualong
 *
 */
@Document(collection = "user_info")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UserInfo extends MongoBase {

	public static final ReturnData ERRER_LENGTH_NICKNAME = new ReturnData(4, "昵称在4到16个字符之间");

	public static final ReturnData ERRER_CONTENT_NICKNAME = new ReturnData(4, "昵称中只能出现汉字或字母或数字");

	public static final ReturnData ERRER_REPEAT_NICKNAME = new ReturnData(4, "用户名重复");

	public static final ReturnData ERRER_REST_PASSWORD = new ReturnData(4, "重置密码失败");
	public static final ReturnData ERRER_REST_ACCOUNT = new ReturnData(4, "账号绑定失败");
	public static final ReturnData ERRER_PASSWORD = new ReturnData(4, "密码有误");

	public static final ReturnData ERRER_PHONE = new ReturnData(-4, "手机号码有误");
	public static final ReturnData ERRER_EMAIL = new ReturnData(-4, "邮箱格式有误");
	public static final ReturnData ERRER_LENGTH_PASSWORD = new ReturnData(4, "密码长度不能少于6位");
	public static final ReturnData ERRER_PHONE_PASSWORD = new ReturnData(4, "手机号码或者密码错误");
	public static final ReturnData ERRER_ACCOUNT_PASSWORD = new ReturnData(4, "账号或者密码错误");
	public static final ReturnData ERRER_MODIFY_PASSWORD = new ReturnData(4, "新旧密码不能相同");
	public static final ReturnData ERRER_ACCOUNT_NOT_FOUND = new ReturnData(4, "该账号不存在");

	public static final ReturnData ERRER_USER_OUT = new ReturnData(-4, "您的帐号因广告违规已经被封");

	public static final ReturnData ERRER_NOT_LOGIN_MSG = new ReturnData(-5, "请登录");
	public static final ReturnData ERRER_NOT_LOGIN = new ReturnData(-5, "");
	public static final ReturnData ERRER_ACCOUNT_BIND_ED = new ReturnData(-3, "该账号已被绑定");
	public static final ReturnData ERRER_ACCOUNT_REGISTER_ED = new ReturnData(-3, "该账号已被注册");

	public static final ReturnData WX_NOT_REGIST = new ReturnData(2, "未微信注册过");

	public static final ReturnData WX_NOT_BINDPHONE = new ReturnData(3, "没有绑定手机号");;
	public static final ReturnData WX_NOT_BIND = new ReturnData(3, "手机号和邮箱都没绑定");;
	public static final ReturnData WX_BINDPHONE_ED = new ReturnData(-3, "该账号绑定过手机号,不能重复绑定");;
	public static final ReturnData WX_BIND_ED = new ReturnData(-3, "该账号绑定过手机号或者邮箱,不能重复绑定");;

	
	public static final ReturnData IMAGE_VALID_CODE = new ReturnData(10, "需要图片验证");;

	public static final ReturnData WX_BINDPHONE = new ReturnData(4, "微信绑定手机号");;
	public static final Map<Integer, String> WX_REGISTER_CODE = new HashMap<Integer, String>();

	static {
		WX_REGISTER_CODE.put(1, "手机注册过微信页注册过");
		WX_REGISTER_CODE.put(2, "微信没有注册过手机号码注册过");
		WX_REGISTER_CODE.put(3, "微信注册过手机号码没有注册过");
		WX_REGISTER_CODE.put(4, "微信和手机号都没有注册过");
		// 邮箱
	}

	/**
	 * 获取微信注册状态
	 * 
	 * @param userInfo
	 * @param ret
	 * @return
	 */
	public static ReturnData wxRegistStauts(UserInfo userInfo, int ret) {
		ReturnData rd = new ReturnData(userInfo, ret, WX_REGISTER_CODE.get(ret));
		return rd;
	}

	// @JsonIgnore
	private String nickname;
	// @JsonIgnore
	private Integer age = 0;
	// @JsonIgnore
	/**
	 * 1男 2女 
	 */
	private Integer sex = 2;
	// @JsonIgnore
	private String city;
	@JsonIgnore
	private String country;
	// @JsonIgnore
	private String headimgurl;
	@JsonIgnore
	private List privilege = new ArrayList();
	@JsonIgnore
	private String language;
	// @JsonIgnore
	private String province;
	@JsonIgnore
	private Integer yunfu = 0;
	// 微信统一认证标识
	@JsonIgnore
	private String unionid;

	// 微信移动登录认证id
	@JsonIgnore
	private String wxmbopenid;
	// 微信登录认证id
	@JsonIgnore
	private String openid;

	// pc微信登录id
	@JsonIgnore
	private String wxpcopenid;
	
	/**
	 * 是否绑定微信
	 */
	private Boolean bindWx;
	
	/**
	 * 是否绑定手机
	 */
	private Boolean bindPhone;

	
	/**
	 * 是否绑定email
	 */
	private Boolean bindEmail;

	// app统一登录认证
	@JsonIgnore
	private String usercode;

	private String phone;
	private String email;

	@JsonIgnore
	private String password;

	// 测试过程
	// @JsonIgnore
	private String test;
	// 测试结果
	// @JsonIgnore
	private String result;

	/**
	 * 肤质测试时间
	 */
	@JsonIgnore
	private Long skinTestTime;
	/**
	 * 第一次肤质测试的时间
	 */
	@JsonIgnore
	private Long firstSkinTestTime;

	/**
	 * 肤质测试结果字段
	 */
	private String skinResults;
	/**
	 * 用户简介
	 */
	private String descz;
	
	/**
	 * 用户类型 
	 * 1 修修酱类型的
	 * 2 达人类型的
	 * 3、普通用户
	 */
	private Integer role;
	
	/**
	 * 是否被禁言
	 */
	@Transient
	private Boolean black;

	/**
	 * 新未读消息条数
	 *
	 * 字段中明显能读懂的可以用简写如Message写成Msg
	 */
	private Integer newMsgNum = 0;

	/**
	 * 消息总数
	 */
	private Integer msgNum = 0;

	/**
	 * 新未读消息条数
	 *
	 * 评论新消息的数量
	 */
	private Integer newCommentMsgNum = 0;

	/**
	 * 评论总消息的数量
	 */
	private Integer commentMsgNum = 0;

	/**
	 * 系统新未读消息条数
	 *
	 * 系统新消息的数量
	 */
	private Integer newSysMsgNum = 0;

	/**
	 * 系统总消息的数量
	 */
	private Integer sysMsgNum = 0;

	/**
	 * 修修酱新消息
	 */
	private Integer newXxjMsgNum = 0;

	/**
	 * 修修酱消息
	 */
	private Integer xxjMsgNum = 0;

	/**
	 * 评论喜欢点击次数
	 */
	private Long commentLikeMsgNum = 0L;

	/**
	 * 最新点击的评论喜欢数量
	 */
	private Integer newCommentLikeMsgNum = 0;

	/**
	 * 积分
	 */
	private Long score = 0L;

	/**
	 * 评论喜欢点击次数
	 */
	private Long commentLikeNum = 0L;
	/**
	 * 评论数量
	 */
	private Long commentNum = 0L;

	/**
	 * 精华评论的数量
	 */
	private Long essenceNum = 0L;

	private Long loginTime = DateUtils.nowInMillis() / 1000;

	/**
	 * 精选评论的数量
	 */
	private Long essenceCommentNum;

	/**
	 * 修行说数量
	 */
	private Long xxsNum;
	/**
	 * 最后获取消息的时间
	 */
	private Long lastMsgTime;
	
	/**
	 * 用户感兴趣的标签
	 */
	private List<Tag> skinTags;
	
	/**
	 * 是否有过期的产品
	 * true存在
	 * false不存在
	 */
	@Transient
	private Boolean expireGoodsCatagory;
	
	
	/**
	 * 身份证姓名
	 */
	@JsonIgnore
	private String realName;
	/**
	 * 身份证号码
	 */
	@JsonIgnore
	private String idCard;
	
	/**
	 * 身份证认证信息
	 */
	@JsonIgnore
	private Map<String,String> IdCardInfor;
	
	/*
	 * 认证时间
	 */
	@JsonIgnore
	private Long verifyStamp;
	
	/**
	 * verifyState=true;
	 * verifyState=false;
	 */
	private Boolean verifyState;
	
	
	private UserAddressInfo[] userAddressInfos;

	public UserAddressInfo[] getUserAddressInfos() {
		return userAddressInfos;
	}

	public void setUserAddressInfos(UserAddressInfo[] userAddressInfos) {
		this.userAddressInfos = userAddressInfos;
	}

	/**
	 * 是否完成肤质流程
	 */
	@Transient
	public Boolean skinFlowed;

	public void setSkinFlowed(Boolean skinFlowed) {
		this.skinFlowed = skinFlowed;
	}

	public Long getEssenceCommentNum() {
		return essenceCommentNum;
	}

	public void setEssenceCommentNum(Long essenceCommentNum) {
		this.essenceCommentNum = essenceCommentNum;
	}
 
	public Long getXxsNum() {
		return xxsNum;
	}

	public void setXxsNum(Long xxsNum) {
		this.xxsNum = xxsNum;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgurl() {
		//todo
		if(StringUtils.isNotBlank(this.headimgurl) && this.headimgurl.indexOf("http")==-1) {
			this.headimgurl= CommonUtils.getImageSrc("uploadFile/head", this.headimgurl);
		}
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		// 设置http为https
		//this.headimgurl = CommonUtils.imgReplaceHttp(headimgurl);
		this.headimgurl =headimgurl;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getNewMsgNum() {
		return newMsgNum;
	}

	public void setNewMsgNum(Integer newMsgNum) {
		this.newMsgNum = newMsgNum;
	}

	public Integer getMsgNum() {
		return msgNum;
	}

	public void setMsgNum(Integer msgNum) {
		this.msgNum = msgNum;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getYunfu() {
		return yunfu;
	}

	public void setYunfu(Integer yunfu) {
		this.yunfu = yunfu;
	}

	public String getWxmbopenid() {
		return wxmbopenid;
	}

	public void setWxmbopenid(String wxmbopenid) {
		this.wxmbopenid = wxmbopenid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getWxpcopenid() {
		return wxpcopenid;
	}

	public void setWxpcopenid(String wxpcopenid) {
		this.wxpcopenid = wxpcopenid;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List getPrivilege() {
		return privilege;
	}

	public void setPrivilege(List privilege) {
		this.privilege = privilege;
	}

	public Long getScore() {
		if (score < 0)
			score = 0L;
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public Long getCommentLikeNum() {
		if (commentLikeNum < 0)
			commentLikeNum = 0L;
		return commentLikeNum;
	}

	public void setCommentLikeNum(Long commentLikeNum) {
		this.commentLikeNum = commentLikeNum;
	}

	public Long getCommentNum() {
		if (commentNum < 0)
			commentNum = 0L;
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	public Long getEssenceNum() {
		if (essenceNum < 0)
			essenceNum = 0L;
		return essenceNum;
	}

	public void setEssenceNum(Long essenceNum) {
		this.essenceNum = essenceNum;
	}

	public String getSkinResults() {
		return skinResults;
	}

	public void setSkinResults(String skinResults) {
		this.skinResults = skinResults;
	}

	public String getDescz() {
		return descz;
	}

	public void setDescz(String descz) {
		this.descz = descz;
	}

	/**
	 * 获取基本信息
	 */
	public UserInfo baseInfo() {
		UserInfo u = new UserInfo();
		u.setId(this.getId());
		u.setNickname(this.getNickname());
		u.setAge(this.getAge());
		u.setSex(this.getSex());
		u.setRole(this.getRole());
		u.setHeadimgurl(this.getHeadimgurl());
		u.setNewMsgNum(null);
		u.setMsgNum(null);
		u.setNewCommentMsgNum(null);
		u.setCommentMsgNum(null);
		u.setSysMsgNum(null);
		u.setNewSysMsgNum(null);
		u.setCity(null);
		u.setUpdateStamp(null);
		u.setCreateStamp(null);
		u.setSex(null);
		u.setLoginTime(null);
		u.setNewCommentMsgNum(null);
		u.setCommentMsgNum(null);
		u.setSysMsgNum(null);
		u.setNewSysMsgNum(null);
		u.setXxjMsgNum(null);
		u.setNewXxjMsgNum(null);
		u.setNewCommentLikeMsgNum(null);
		u.setCommentLikeMsgNum(null);
		return u;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getNewSysMsgNum() {
		return newSysMsgNum;
	}

	public void setNewSysMsgNum(Integer newSysMsgNum) {
		this.newSysMsgNum = newSysMsgNum;
	}

	public Integer getCommentMsgNum() {
		return commentMsgNum;
	}

	public void setCommentMsgNum(Integer commentMsgNum) {
		this.commentMsgNum = commentMsgNum;
	}

	public Integer getSysMsgNum() {
		return sysMsgNum;
	}

	public void setSysMsgNum(Integer sysMsgNum) {
		this.sysMsgNum = sysMsgNum;
	}

	public Integer getNewXxjMsgNum() {
		return newXxjMsgNum;
	}

	public void setNewXxjMsgNum(Integer newXxjMsgNum) {
		this.newXxjMsgNum = newXxjMsgNum;
	}

	public Integer getXxjMsgNum() {
		return xxjMsgNum;
	}

	public void setXxjMsgNum(Integer xxjMsgNum) {
		this.xxjMsgNum = xxjMsgNum;
	}

	public Long getLastMsgTime() {
		return lastMsgTime;
	}

	public void setLastMsgTime(Long lastMsgTime) {
		this.lastMsgTime = lastMsgTime;
	}

	public Long getSkinTestTime() {
		return skinTestTime;
	}

	public void setSkinTestTime(Long skinTestTime) {
		this.skinTestTime = skinTestTime;
	}

	public Long getFirstSkinTestTime() {
		return firstSkinTestTime;
	}

	public void setFirstSkinTestTime(Long firstSkinTestTime) {
		this.firstSkinTestTime = firstSkinTestTime;
	}

	public Integer getNewCommentMsgNum() {
		return newCommentMsgNum;
	}

	public Long getCommentLikeMsgNum() {
		return commentLikeMsgNum;
	}

	public void setCommentLikeMsgNum(Long commentLikeMsgNum) {
		this.commentLikeMsgNum = commentLikeMsgNum;
	}

	public Integer getNewCommentLikeMsgNum() {
		return newCommentLikeMsgNum;
	}

	public void setNewCommentLikeMsgNum(Integer newCommentLikeMsgNum) {
		this.newCommentLikeMsgNum = newCommentLikeMsgNum;
	}

	public void setNewCommentMsgNum(Integer newCommentMsgNum) {
		this.newCommentMsgNum = newCommentMsgNum;
	}

	/*
	 * 获取用户基本信息
	 */
	public UserBaseInfo getBaseInfo() {
		// TODO Auto-generated method stub
		UserBaseInfo ubi = new UserBaseInfo();
		ubi.setUserId(this.getId());
		if(StringUtils.isBlank(this.getHeadimgurl())) {
			ubi.setHeadimgurl(null);
		}else {
			ubi.setHeadimgurl(this.getHeadimgurl());
		}
		ubi.setSkinResults(this.getSkinResults());
		ubi.setSkin(this.getResult());
		ubi.setNickname(this.getNickname());
		return ubi;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Boolean getBindWx() {
		return bindWx;
	}

	public void setBindWx(Boolean bindWx) {
		this.bindWx = bindWx;
	}

	public Boolean getBindPhone() {
		return bindPhone;
	}

	public void setBindPhone(Boolean bindPhone) {
		this.bindPhone = bindPhone;
	}

	public Boolean getBindEmail() {
		return bindEmail;
	}

	public void setBindEmail(Boolean bindEmail) {
		this.bindEmail = bindEmail;
	}
	
	/**
	 * 展现绑定信息
	 */
	public void bindState() {
		if(StringUtils.isBlank(this.unionid)) {
			this.bindWx=false;
		} else {
			this.bindWx=true;
		}
		
		if(StringUtils.isBlank(this.email)) {
			this.bindEmail=false;
		} else {
			this.bindEmail=true;
		}
		
		if(StringUtils.isBlank(this.phone)) {
			this.bindPhone=false;
		} else {
			this.bindPhone=true;
		}

		
	}

	public Boolean getBlack() {
		return black;
	}

	public void setBlack(Boolean black) {
		this.black = black;
	}

	public List<Tag> getSkinTags() {
		return skinTags;
	}

	public void setSkinTags(List<Tag> skinTags) {
		this.skinTags = skinTags;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public Map<String, String> getIdCardInfor() {
		return IdCardInfor;
	}

	public void setIdCardInfor(Map<String, String> idCardInfor) {
		IdCardInfor = idCardInfor;
	}

	public Long getVerifyStamp() {
		return verifyStamp;
	}

	public void setVerifyStamp(Long verifyStamp) {
		this.verifyStamp = verifyStamp;
	}

	public Boolean getVerifyState() {
		return verifyState;
	}

	public void setVerifyState(Boolean verifyState) {
		this.verifyState = verifyState;
	}

	public Boolean getExpireGoodsCatagory() {
		return expireGoodsCatagory;
	}

	public void setExpireGoodsCatagory(Boolean expireGoodsCatagory) {
		this.expireGoodsCatagory = expireGoodsCatagory;
	}

	
	
}
