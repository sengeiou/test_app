package cn.bevol.app.log.interceptor;

import cn.bevol.util.CommonUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor  extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		long id= CommonUtils.getLoginCookieById(request);
		if(id>0) {
 			//	ThreadLocalUtils.setUserId(id);
				return true;
		} 
		String nologin="{\"ret\":-5}";
		response.getWriter().println(nologin);
		return false;
	}

	@Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
