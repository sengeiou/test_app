package cn.bevol.model.user;

import cn.bevol.model.entity.MongoBase;
import cn.bevol.util.response.ReturnData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 验证码 
 * 
 * 1、始终获取最后1条
 * 2、验证此条是否在10分钟之内  
 *   获取验证码：
 *   	1、小于1分钟 不能获取
 *   	2、大于1分钟 刷新验证码重新获取
 * @author hualong
 *
 */
@Document(collection = "verification_code")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class VerificationCodeEntity extends MongoBase{
	/**
	 * 只用于异常处理
	 */
	public static ReturnData NOT_PASS_TIME=new ReturnData(-2,"未超过一分钟");
	public static ReturnData VCODE_ERRER=new ReturnData(-3,"验证码错误或超时,请重新获取");
	
	
	public static ReturnData VCODE_OUTTIME=new ReturnData(-6,"提交超时,请返回重试");

	public static ReturnData VCODE_TYPE_ERRER=new ReturnData(-7,"验证码错误类型有误");

	public static ReturnData VCODE_ACCOUNT_ERRER=new ReturnData(-8,"验证账号有误");

	@JsonIgnore
	private String phone;
	
	/**
	 * 手机或者邮箱验证码
	 */
	 //@JsonIgnore  
	private String vcode;
	
	private String email;
	
	/**
	 * 图片验证码
	 */
	private String imgVcode;
	
	public VerificationCodeEntity() {}
	public VerificationCodeEntity(String account, int type) {
		if(account.indexOf("@")==-1){
			this.phone=account;
		}else{
			this.email=account;
		}
		this.vcode= RandomStringUtils.random(4, false, true);
		this.type=type;
	}

	
	/**
	 * 0手机注册
	 * 1找回密码
	 * 2手机邦定
	 */
	private Integer type=0;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
