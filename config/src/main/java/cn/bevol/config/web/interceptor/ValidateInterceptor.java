package cn.bevol.config.web.interceptor;

import cn.bevol.config.constant.conf.Module;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ValidateInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String reqUrl = httpServletRequest.getRequestURI().replace(httpServletRequest.getContextPath(), "").replace("//", "/");
        if (reqUrl.equals("/resource")||reqUrl.equals("/resource/upsert")) {

            String key = httpServletRequest.getParameter("key");
            if (StringUtils.isEmpty(key)) {
                return false;
            }


            if (key.contains("num")
                    || key.contains("string")) {
            } else return false;

            if (key.split("\\.").length < 3) {
                return false;
            }

            boolean isValidModule = false;
            for (int i = 0; i < Module.modules.size(); i++) {
                if (key.contains(Module.modules.get(i))) {
                    isValidModule = true;
                    break;
                }
            }

            if (isValidModule) {
            } else return false;

        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
