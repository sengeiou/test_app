package cn.bevol.app.controller;


import cn.bevol.app.service.AliyunService;
import cn.bevol.app.service.CommenStatisticsService;
import cn.bevol.app.service.LoginService;
import cn.bevol.app.service.UserService;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.user.VerificationCode;
import cn.bevol.util.response.ReturnData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Controller
public class Login2Controller extends BaseController{
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AliyunService aliyunService;

	@Autowired
	private CommenStatisticsService commenStatisticsService;
	/**2.6接口**/
	

	/**
	 * v2.6-now
	 * 手机或者邮箱登录   直接判断用户名和密码登录
	 * @param response
	 * @param request
	 * @param account: 账号
	 * @param password: 密码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/app/login"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData accountLogin2_6(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String account,
			@RequestParam String password) throws Exception {
	    ReturnData rd= loginService.accountLogin2_6(account, password);
	    if(rd.getRet()==0) {
	    	//更新cookie
		    this.setUser(request,response, (UserInfo) rd.getResult());
	    }
	    return rd;
	}
	
	/**
	 * v2.6-now
	 * 手机或者邮箱注册
	 * @param response
	 * @param request
	 * @param account: 账号
	 * @param password: 密码
	 * @param vcode: 验证码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/app/register"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData accountRegister2_6(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String account,
			@RequestParam String password,
			@RequestParam String vcode) throws Exception {
		 ReturnData rd= loginService.accountRegister2_6( account, password, vcode);
	    if(rd.getRet()==0) {
	    	UserInfo userInfo=(UserInfo) rd.getResult();
		    this.setUser(request,response, userInfo); //session  存储用户注册后的userinfo
		    //统计  todo
			    int accountType=1;
		    	if(account.indexOf("@")>0) {
		    		//邮箱
		    		accountType=2;
		    	}else {
		    		//手机
		    		accountType=1;
		    	}
		    	commenStatisticsService.userRegActive(request, userInfo.getId(), account,accountType,2);
	    }
	    return rd;

	}
  
 	
	
 	/**
 	 * v2.6-now
 	 * 微信登录   带绑定
 	 * @param unionid: 微信统一认证标识
 	 * @param openid: 微信登录认证id
 	 * @param nickname: 微信昵称
 	 * @param account: 账号
 	 * @param password: 密码
 	 * @param vcode: 验证码
 	 * @param headimgurl: 微信头像
 	 * @param headimgurl_1: 注册后第一次登录的参数,微信头像
 	 * @return
 	 */
	@RequestMapping(value={"/app/wx/login"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData wxLogin2_6(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String unionid,
			@RequestParam String openid,
			@RequestParam String nickname,
			@RequestParam String account,
			@RequestParam String password,
			@RequestParam String vcode,
			@RequestParam(defaultValue="",required=false) String headimgurl,
			@RequestParam(defaultValue="",required=false) String headimgurl_1,
			@RequestParam(defaultValue="0") int age,
			@RequestParam(defaultValue="0") int sex,
			@RequestParam(defaultValue="0",required=false) int yunfu,
			@RequestParam(defaultValue="",required=false) String country,
			@RequestParam(defaultValue="",required=false) String language,
			@RequestParam(defaultValue="",required=false) String province,
			@RequestParam(defaultValue="",required=false) String city){
		headimgurl=aliyunService.imgForm(headimgurl, headimgurl_1);
		ReturnData cols=loginService.wxLogin2_6(unionid,openid,country,language,nickname,headimgurl,age,sex,province,account,password,city,yunfu,vcode);
		if(cols.getRet()>0) {
	    	UserInfo userInfo=(UserInfo) cols.getResult();
		    this.setUser(request,response, userInfo);
		    //统计 新用户 并且是邮箱的
		    if(cols.getRet()==4) {
			    int accountType=1;
		    	if(account.indexOf("@")>0) {
		    		//邮箱
		    		accountType=2;
		    	}else {
		    		//手机
		    		accountType=1;
		    	}
		    	commenStatisticsService.userRegActive(request, userInfo.getId(), account,accountType,1);

		    }

		}
		return cols;
 	}



 	/**
 	 * v2.6-now
 	 * 验证微信是否绑定过手机号或者邮箱
 	 * @param response
 	 * @param request
 	 * @param unionid: 微信统一认证标识
 	 * @return
 	 * 
 	 * 返回值:
 	 * 	ret=1 表示绑定了的用户  --直接登录,返回用户信息
		ret=2 微信没有注册过
		ret=3 微信注册过但是没有绑定手机号码或者邮箱
		ret=-4 账号被封
 	 */
	@RequestMapping(value={"/app/wx/checkuser"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData wxCheckUser2_6(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String unionid) {
		ReturnData cols=loginService.wxCheckUser2_6(unionid);
		//更新用户cookie和session
		if(cols.getRet()==1) {
		    this.setUser(request,response, (UserInfo)cols.getResult());
		}
		return cols;
	}

	/**
	 * v2.6-now
	 * 找回密码
	 * @param account: 账号
	 * @param password: 密码
	 * @param vcode: 验证码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/app/restpassword"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData restPassword2_6(HttpServletResponse response,
                                      HttpServletRequest request,
                                      @RequestParam String account,
                                      @RequestParam String password, @RequestParam String vcode) throws Exception {
		ReturnData rd= loginService.restPassword2_6(account, password,vcode);
	    if(rd.getRet()==0) {
	    	//更新用户cookie和session
		    this.setUser(request,response, (UserInfo)rd.getResult());
	    }
	    return rd;

	}
	



	/**
	 * 手机或者邮箱获取验证码
	 * type 0 注册  1 找回密码  3微信绑定  4  更改账号
	 */
	@RequestMapping(value={"/app/vcode"},method={RequestMethod.POST})
	@ResponseBody
	@Deprecated
	public ReturnData getVcode2_6(HttpServletResponse response,HttpServletRequest request,
			@RequestParam int type,
			@RequestParam String account) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return loginService.getVcode2_6(account, type,userInfo);
	}
	
	/**
	 * v2.9-now
	 * 获取验证码(图片验证/手机/邮箱)
	 * @param type: 0 注册  1 找回密码  3微信绑定  4  更改账号
	 * @param account: 账号
	 */
	@RequestMapping(value={"/app/vcode2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData getVcode2_9(HttpServletResponse response,HttpServletRequest request,
			@RequestParam int type,
			@RequestParam String account) throws Exception {
		UserInfo userInfo=this.getUser(request);
		ReturnData rd=loginService.getVcode2_9(account, type,userInfo);
		return rd;
	}
	
	/**
	 * v2.9.2
	 * 源生 
	 * 获取验证码(图片验证/手机/邮箱)
	 * type 0 注册  1 找回密码  3微信绑定  4  更改账号
	 * 1、第一步 获取验证码
	 * 		ret=10需要图片验证码,走刷新验证码,确认图片验证码,再走第二步
	 * 返回: ret=10需要图片验证码
	 */
	@RequestMapping(value={"/app/vcode3"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData getVcode2_9_2(HttpServletResponse response,HttpServletRequest request,
			@RequestParam int type,
			@RequestParam String account,@RequestParam(name = "vcode_type",required = false) Integer vcodeType) throws Exception {
		UserInfo userInfo=this.getUser(request);
		//清除验证
		request.getSession().removeAttribute("vcode_valid");
		ReturnData rd=loginService.getVcode2_9_2(account, type,userInfo,vcodeType);
		return rd;
	}

    /**
     * 账号验证
     * @param response
     * @param request
     * @param type
     * @param account
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/app/account/valid"},method={RequestMethod.POST})
    @ResponseBody
    public ReturnData accountValid(HttpServletResponse response, HttpServletRequest request,
                                 @RequestParam int type,
                                 @RequestParam String account) throws Exception {
        UserInfo userInfo=this.getUser(request);
        return loginService.accountValid(account,userInfo, type);
    }


	/**
	 * v2.9.2
	 * 源生
	 * 验证码验证
	 * 2、第二步  比较验证码是否正确
	 * @param response
	 * @param request
	 * @param type: 验证码类型 0 注册  1 找回密码  3微信绑定  4  更改账号
	 * @param account: 账号
	 * @param vcode: 验证码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/app/vcode/valid"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData vcodeValid(HttpServletResponse response, HttpServletRequest request,
                                 @RequestParam int type,
                                 @RequestParam String account, @RequestParam String vcode) throws Exception {
		//短信验证码验证
		ReturnData rd=userService.isVcode2_6(account, vcode, type);
		if(rd.getRet()==0) {
			//验证码验证
			//时间_account_type 保持在session 用于下一步完成 时
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("time", System.currentTimeMillis()/1000);
			map.put("account", account);
			map.put("type", type);
			request.getSession().setAttribute("vcode_valid", map);
		}
		return rd;
	}
	
	
	
	/**
	 * v2.9.2
	 * 源生
	 * 第二步和第三步之间的验证,session验证
	 * @param request
	 * @param account: 账号
	 * @param vtype: 验证码类型 0 注册  1 找回密码  3微信绑定  4  更改账号
	 * @return
	 */
	private ReturnData<Map<String,Object>> vcodeValidState(HttpServletRequest request, String account, int vtype) {
		try {
			Map<String,Object> vCode=(Map<String, Object>) request.getSession().getAttribute("vcode_valid");
			if(vCode!=null) {
		        //大于10分钟
		        long vtime = Long.parseLong(vCode.get("time")+"");
		        String vaccount=vCode.get("account")+"";
		        if(StringUtils.isBlank(account)||!vaccount.equals(account)) {
		            return VerificationCode.VCODE_ACCOUNT_ERRER;
		        }
		        int type= Integer.parseInt(vCode.get("type")+"");
				request.getSession().removeAttribute("vcode_valid");
				
				//验证码是否为同一个类型
				if(type!=vtype) {
					return VerificationCode.VCODE_TYPE_ERRER;
				}
		        if ((System.currentTimeMillis()/1000) > (vtime + (60 * 10))) {
		            //验证码超时
		            return VerificationCode.VCODE_OUTTIME;
		        }
		        Map<String,Object> map=new HashMap<String,Object>();
		        map.put("time", vtime);
		        map.put("account", account);
		        map.put("type", type);
				return new ReturnData<Map<String,Object>>(map);
			}
		}catch(Exception e) {
		}
		return VerificationCode.VCODE_OUTTIME;
	}
	
	/**
	 * v2.9.2
	 * 源生
	 * 第三步 重置密码
	 * @param response
	 * @param request
	 * @param account: 账号
	 * @param password: 密码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/app/restpassword2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData restPassword3_0(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String account,
			@RequestParam String password) throws Exception {
		ReturnData rvd=vcodeValidState(request,account,1);
		if(rvd.getRet()==0) {
			ReturnData rd= loginService.restPassword(account, password);
		    if(rd.getRet()==0) {
		    	//更新cookie和session
			    this.setUser(request,response, (UserInfo)rd.getResult());
		    }
		    return rd;
		} else {
			return rvd;
		}
	}

	
	/**
	 * v2.9.2
	 * 源生
	 * 第三步
	 * 账号注册
	 * @param response
	 * @param request
	 * @param account: 账号
	 * @param password: 密码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/app/register2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData accountRegister3_0(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String account,
			@RequestParam String password) throws Exception {
		ReturnData rvd=vcodeValidState(request,account,0);
		if(rvd.getRet()==0) {
			 ReturnData rd= loginService.accountRegister( account, password);
			    if(rd.getRet()==0) {
			    	UserInfo userInfo=(UserInfo) rd.getResult();
				    this.setUser(request,response, userInfo);
				    //统计
					    int accountType=1;
				    	if(account.indexOf("@")>0) {
				    		//邮箱
				    		accountType=2;
				    	}else {
				    		//手机
				    		accountType=1;
				    	}
				    	commenStatisticsService.userRegActive(request, userInfo.getId(), account,accountType,2);
			    }
		    return rd;
		} else {
			return rvd;
		}
	}
	

	/**
	 * v2.9
	 * 生成图片验证码
	 * @param response
	 * @param request
	 * @param type: 验证码类型 0 注册 1找回密码 3微信绑定手机号 4更改手机号码
	 * @param account: 账号
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/app/vcode2/refresh"},method={RequestMethod.GET})
	@ResponseBody
	public ReturnData getImgVcode2_9(HttpServletResponse response,HttpServletRequest request,
			@RequestParam int type,
			@RequestParam String account) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return loginService.createImgVcode(account, type,userInfo,response,request);
	}
	
	
	/**
	 * v2.9
	 * 对比图片验证码,图片验证通过后直接发送短信
	 * @param response
	 * @param request
	 * @param type: 验证码类型 0 注册 1找回密码 3微信绑定手机号 4更改手机号码
	 * @param account: 账号
	 * @param imgVcode: 图片验证码
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/app/vcode2/submit"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData submitImgVcode(HttpServletResponse response, HttpServletRequest request,
                                     @RequestParam int type,
                                     @RequestParam String account, @RequestParam(name="imag_vcode") String imgVcode,
            @RequestParam(name = "vcode_type",required = false) Integer vcodeType) throws Exception {
        UserInfo userInfo=this.getUser(request);
        return loginService.compareImgVcode(response,request,imgVcode,account, type,userInfo,vcodeType);
    }
	

}
