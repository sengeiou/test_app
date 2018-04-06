package cn.bevol.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import cn.bevol.model.user.UserInfo;
import cn.bevol.entity.service.UserService;
import cn.bevol.entity.service.utils.CommonUtils;
import cn.bevol.log.LogStatisticsUtils;
import cn.bevol.util.ReturnData;




@Controller
public class BaseController {
	@Autowired
	private UserService userService;

	public  Map returnAjax(Object data,int state,String ... info) {
		return CommonUtils.outTepl(data,state,info);
	}

     public Map returnlistAjax(List rows,long total,int state,String ...info) {
    	return CommonUtils.outTepl(rows,total,state,info);
    	}
     public Map returnlistAjax(List rows,long total) {
    	return CommonUtils.outTepl(rows,total,0,"");
    }

    /**
     * 根据cookie获取用户id
     * @param request
     * @return
     */
	public long getUserId(HttpServletRequest request) {
		Long id=CommonUtils.getLoginCookieById(request);
		return id;
	}
	
	
    /**
     * 根据cookie获取用户id
     * @param request
     * @return
     */
	public long getUserId2(HttpServletRequest request) {
		Long id=CommonUtils.getLoginCookieById2(request);
		return id;
	}

	
	/**
	 * 设置用户cookie和session
	 * @param request
	 * @param response
	 * @param userInfo
	 */
	public void setUser(HttpServletRequest request,HttpServletResponse response,UserInfo userInfo) {
		//设置cookies
		CommonUtils.setLoginCookieId(response,userInfo.getId(),null);
		request.getSession().setAttribute("userInfo", userInfo);
	}
	
	

	/**
	 * pc设置用户session,cookie
	 * @param request
	 * @param response
	 * @param userInfo
	 */
	public void setPCUser(HttpServletRequest request,HttpServletResponse response,UserInfo userInfo) {
		//设置cookies
		CommonUtils.setLoginCookieId(response,userInfo.getId(),-1);
		request.getSession().setAttribute("userInfo", userInfo);
	}

	/**
	 * 获取用户信息
	 * @param request
	 * @param response
	 * @return
	 */
	public UserInfo getUser(HttpServletRequest request) {
		//设置cookies
		long id=this.getUserId(request);
		UserInfo userInfo=null;
		if(id>0) {
			userInfo=this.getUserInfo(request);
			if(userInfo==null||userInfo.getId()==null) {
				ReturnData rd=userService.getUserById(id);
				 if(rd.getRet()==0) {
					 userInfo=(UserInfo)rd.getResult();
					 userService.userInit(userInfo);
					 request.getSession().setAttribute("userInfo",userInfo );
				 }
					
			}
		}
		return userInfo;
	}
	
	private UserInfo getUserInfo(HttpServletRequest request) {
		try {
			
			// todo mongodb 作为session的时候  需要转型
			 request.getSession().getAttribute("userInfo");
			Object o=request.getSession().getAttribute("userInfo");
			UserInfo userInfo=new UserInfo();
			if(o!=null) {
				if(o instanceof LinkedHashMap) {
					LinkedHashMap m=(LinkedHashMap)o;
					BeanUtils.populate(userInfo,m);
					userInfo.setId(Long.parseLong(m.get("_id")+""));
					userInfo.set_id(null);
					if(m==null||m.size()==0) {
						return null;
					}
				} else {
					userInfo=(UserInfo) o;
				}
			}
			if(userInfo.getId()==null) return null;
			return userInfo;
		} catch (Exception e) {
			Map map=new HashMap();
    		map.put("method", "BaseController.getUserInfo");
    		new cn.bevol.log.LogException(e,map);
		}
		return null;
	}
	/**
	 * 重新加载用户
	 * @param request
	 */
	public ReturnData reloadUser(HttpServletRequest request) {
		UserInfo userInfo=this.getUserInfo(request);
		if(userInfo!=null)
			request.getSession().removeAttribute("userInfo");
		userInfo= getUser(request);
		return new ReturnData(userInfo);
	}
	/**
	 * 移去用户cookie和session
	 * @param request
	 * @param response
	 */
	public void removeUser(HttpServletRequest request, HttpServletResponse response) {
		 CommonUtils.removeCookieByName(response,"logincode");
		 request.getSession().removeAttribute("userInfo");
	}



 	/**
	 * 重新登录
	 * @return
	 */
	public  Map resetLogin() {
		return returnAjax(null,-5,"请重新登录");
	}

	/**
	 * 用户禁用
	 * @return
	 */
	public  Map hiddenUser() {
		return returnAjax(null,4,"用户违规暂时锁定");
	}


	/**
	 * 正常返回
	 * @return
	 */
	public  Map defaultAjax() {
		return returnAjax(null,0);
	}


    /**
     * 异常返回
     *
     * @return
     */
    public Map errorAjax(String info) {
        return returnAjax(null, -1, info);
    }


    /**
     * 异常返回
     *
     * @return
     */
    public Map errorAjax() {
        return returnAjax(null, -1);
    }
    
    /**
     * 未登录的状态
     * @return
     */
	public Map returnLogin() {
        return returnAjax(null, -5, "请登录");
	}


	/**
	 * 文件大小控制
	 * @param ex
	 * @param request
	 * @return
	 */
	 @ExceptionHandler      
	 @ResponseBody
	    public ReturnData handleException(Exception ex,HttpServletRequest request) {       
	         Map<Object, Object> model = new HashMap<Object, Object>();  
             //错误请求
	 		LogStatisticsUtils.putData("exception", true);
     		LogStatisticsUtils.putData("trace", ExceptionUtils.getRootCauseMessage(ex));
     		LogStatisticsUtils.putData("base_control",1);

	         if (ex instanceof MaxUploadSizeExceededException){  
	                        return new ReturnData(-1,"文件应不大于 "+ getFileKB(((MaxUploadSizeExceededException)ex).getMaxUploadSize()));              
	         } else{  
                 return ReturnData.ERROR;
	         }  
	    }    
	      
	    private String getFileKB(long byteFile){  
	        if(byteFile==0)  
	           return "0KB";  
	        long kb=1024;  
	        return ""+byteFile/kb+"KB";  
	    }  
	    private String getFileMB(long byteFile){  
	        if(byteFile==0)  
	           return "0MB";  
	        long mb=1024*1024;  
	        return ""+byteFile/mb+"MB";  
	    }  


}
