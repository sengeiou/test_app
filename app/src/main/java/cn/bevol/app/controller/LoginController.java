package cn.bevol.app.controller;

import cn.bevol.app.service.LoginService;
import cn.bevol.app.service.UserService;
import cn.bevol.app.service.WXService;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.response.ReturnData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
public class LoginController extends BaseController{
	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private WXService wxService;
	@Autowired
	private LoginService loginService;


	
	/**2.0接口**/
	
	/**
	 * 手机登录
	 */
	@Deprecated
	@RequestMapping(value={"/phone/login"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData register(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String phone,
			@RequestParam String password) throws Exception {
	    ReturnData rd= userService.phoneLogin( phone, password);
	    if(rd.getRet()==0) {
		    this.setUser(request,response, (UserInfo) rd.getResult());
	    }
	    return rd;
	}
	/**
	 * 手机注册
	 */
	@Deprecated
	@RequestMapping(value={"/phone/register"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData register(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String phone,
			@RequestParam String password,
			@RequestParam String vcode) throws Exception {
		 ReturnData rd= userService.phoneRegister( phone, password, vcode);
	    if(rd.getRet()==0) {
		    this.setUser(request,response, (UserInfo)rd.getResult());
	    }
	    return rd;

	}

 	/**
	 * 微信登录
	 *
	 */
	@Deprecated
	@RequestMapping(value={"/wx/login"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData wxlogin(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String unionid,
			@RequestParam String openid,
			@RequestParam String nickname,
			@RequestParam String headimgurl,
			@RequestParam(defaultValue="0") int age,
			@RequestParam(defaultValue="0") int sex,
			@RequestParam(defaultValue="0",required=false) int yunfu,
			@RequestParam(defaultValue="",required=false) String country,
			@RequestParam(defaultValue="",required=false) String language,
			@RequestParam(defaultValue="",required=false) String province,
			@RequestParam(defaultValue="",required=false) String city){
		ReturnData cols=userService.wxLogin(unionid,openid,country,language,nickname,headimgurl,age,sex,province,city,yunfu);
		if(cols.getRet()==0){
			this.setUser(request, response, (UserInfo)cols.getResult());
		}
		return cols;
 	}
	
 

 	/**
	 * 微信登录
	 *
	 */
	@Deprecated
	@RequestMapping(value={"/wx/checkuser"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData checkuser(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String unionid) {
		ReturnData cols=userService.wxCheckUser(unionid);
		//直接登录
		if(cols.getRet()==1) {
		    this.setUser(request,response, (UserInfo)cols.getResult());
		}
		return cols;
	}
	
	
 	/**
	 * 微信登录2
	 *
	 */
	@Deprecated
	@RequestMapping(value={"/wx/login2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData wxlogin2(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String unionid,
			@RequestParam String openid,
			@RequestParam String nickname,
			@RequestParam String phone,
			@RequestParam String password,
			@RequestParam String vcode,
			@RequestParam String headimgurl,
			@RequestParam(defaultValue="0") int age,
			@RequestParam(defaultValue="0") int sex,
			@RequestParam(defaultValue="0",required=false) int yunfu,
			@RequestParam(defaultValue="",required=false) String country,
			@RequestParam(defaultValue="",required=false) String language,
			@RequestParam(defaultValue="",required=false) String province,
			@RequestParam(defaultValue="",required=false) String city){
		ReturnData cols=userService.wxLogin2(unionid,openid,country,language,nickname,headimgurl,age,sex,province,phone,password,city,yunfu,vcode);
		if(cols.getRet()>0){
			this.setUser(request, response, (UserInfo)cols.getResult());
		}
		return cols;
 	}



 	/**
	 * 微信登录
	 *
	 */
	@Deprecated
	@RequestMapping(value={"/wx/checkuser2"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData checkuser2(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam String unionid) {
		ReturnData cols=userService.wxCheckUser2(unionid);
		//直接登录
		if(cols.getRet()==1) {
		    this.setUser(request,response, (UserInfo)cols.getResult());
		}
		return cols;
	}

 
	/**
	 * 手机获取验证码
	 */
	@Deprecated
	@RequestMapping(value={"/phone/vcode"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData register(HttpServletResponse response,
			@RequestParam int type,
			@RequestParam String phone) throws Exception {
		return userService.getVcode(phone, type);
	}
	
 	/**
	 * 验证
	 * 检查用户名
	 */
	@Deprecated
	@RequestMapping(value={"/user/find/{field}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData checknickname(HttpServletResponse response,
                                    @PathVariable String field, @RequestParam String val) throws Exception {
		if(field.equals("nickname")) {
			ReturnData rd= userService.findNickname(val);
			return new ReturnData(rd.getResult(),rd.getRet());
		} else if(field.equals("phone")) {
			ReturnData rd= userService.checkPhone(val);
			return new ReturnData(rd.getResult(),rd.getRet());

		}
		return ReturnData.ERROR;
	}

	
	
	
	/**
	 	重置密码
	 * 检查用户名
	 */
	@Deprecated
	@RequestMapping(value={"/phone/restpassword"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData phonelogin(HttpServletResponse response,
                                 HttpServletRequest request,
                                 @RequestParam String phone,
                                 @RequestParam String password, @RequestParam String vcode) throws Exception {
		ReturnData rd= userService.restPassword(phone, password,vcode);
	    if(rd.getRet()==0) {
		    this.setUser(request,response, (UserInfo)rd.getResult());
	    }
	    return rd;

	}

 	
 	/**
	 * 手机退出登录
	 *  
	 */
	@RequestMapping(value={"/phone/logout"},method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> phonelogout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		 super.removeUser(request,response);
		return defaultAjax();
	}
	
	


 	/**
 	 * v3.1之前
	 * 打开app
	 *  
	 */
	@Deprecated
	@RequestMapping(value={"/open/app"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData opeanapp(HttpServletRequest request,HttpServletResponse response,@RequestParam(defaultValue="") String usercode) throws Exception {
		//
		// 老方案ReturnData rd=userService.openApp(usercode);
		//新方案
		long id=this.getUserId2(request);
		ReturnData rd=userService.openApp(id);
		return rd;
	}

	
	
 	/**
	 * pc 
	 * 登录
	 *  
	 */
	@RequestMapping(value={"/open/pc"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData opeanpc(HttpServletRequest request,HttpServletResponse response,@RequestParam(defaultValue="") String usercode) throws Exception {
		//
		// 老方案ReturnData rd=userService.openApp(usercode);
		//新方案
		long id=this.getUserId2(request);
		ReturnData rd=userService.openApp(id);
		return rd;
	}

	
	
 	/**
	 * pc
	 * 微信登录部分
	 *  
	 */
	@RequestMapping(value={"/wx/pc/login"})
	public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//
		// 老方案ReturnData rd=userService.openApp(usercode);
		//新方案
		String url=wxService.getPCLoginCode();
		if(!StringUtils.isBlank(url)){
			return "redirect:"+url;
		}
		return null;
	}

	/**
	 * pc 
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/pc/logout"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData logout(HttpServletRequest request,HttpServletResponse response) throws Exception {
		super.removeUser(request,response);
		return ReturnData.SUCCESS;
	}


 	/**
	 * pc
	 * 微信回调
	 *  
	 */
	@RequestMapping(value={"/wx/pc/login/callback"})
	public String wx(HttpServletRequest request, HttpServletResponse response, @RequestParam String code) throws Exception {
		//
		// 老方案ReturnData rd=userService.openApp(usercode);
		//新方案
		ReturnData<UserInfo> rd=wxService.wxPcLogin(code);
		//写cookie
		String cururl=null;
		if(rd.getRet()==0) {
			this.setPCUser(request, response, rd.TResult());
			 cururl= CommonUtils.getCookieByName(request,"cururl");
		}
		if(StringUtils.isBlank(cururl)){
			cururl= ConfUtils.getResourceString("pc_domain");
		}
		return "redirect:"+cururl;
	}


}
