package cn.bevol.app.log.interceptor;

import cn.bevol.app.service.CommenStatisticsService;
import cn.bevol.util.ComReqModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class StatisticsInterceptor implements HandlerInterceptor {

    @Autowired
    private CommenStatisticsService commenStatisticsService;
    
    

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      
    /*	if(ConfUtils.getResourceNum("write_log")==1) {
        	//获取url地址
        	String reqUrl = request.getRequestURI().replace(request.getContextPath(), "").replace("//", "/");

            if (reqUrl.contains("/open/weixin/sign")) {
                //不记录微信签名请求
                  // TODO: 16-7-13  微信请求url更改
                return true;
            }

            if (reqUrl.contains("/login")) { //login2-登陆和注册   login 旧的登陆-手机&微信
                commenStatisticsService.increseUserDailyActive(request, StatisticsI.COLLECTION_DAILY_LOGIN_PRE);
//                return true;
            }

            if (reqUrl.contains("/open/app")) {
                commenStatisticsService.increseUserDailyActive(request, StatisticsI.COLLECTION_DAILY_INIT_PRE);
                return true;
            }

            if (reqUrl.contains("/register") || reqUrl.contains("/login2")) {//旧手机如注册  ，和新的绑定的登陆会注册
                commenStatisticsService.increseUserDailyActive(request, StatisticsI.COLLECTION_DAILY_REGISTER_PRE);
                return true;
            }
            //普通请求分析
            commenStatisticsService.increseUserDailyActive(request, StatisticsI.COLLECTION_DAILY_ACTIVE_PRE);
    	}*/
        
        ComReqModel.setReqComAgrs(request);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    	ComReqModel.removeReqComAgrs();
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
                           Object arg2, ModelAndView arg3) throws Exception {

    }


}
