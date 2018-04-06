package cn.bevol.app.log.interceptor;

import cn.bevol.app.service.AdvertisementLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Rc. on 2017/3/24.
 */
public class AdvertisementInterceptor extends HandlerInterceptorAdapter {
    @Autowired
	AdvertisementLogService advertisementLogService;
    @Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String entityId = request.getParameter("ad_id");
		String positionType = request.getParameter("positionType");
		if(StringUtils.isEmpty(positionType) || ("null".equals(positionType)) || ("undefined".equals(positionType))){
			positionType="";
		}
		if (!StringUtils.isEmpty(entityId)&&!("null".equals(entityId))&&!("undefined".equals(entityId))) {
			Integer id = Integer.valueOf(entityId);
			advertisementLogService.addADLog(id,positionType);
		}

		return true;
	}

    @Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
