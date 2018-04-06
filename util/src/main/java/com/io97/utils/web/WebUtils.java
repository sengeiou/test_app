package com.io97.utils.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


public class WebUtils {

    public static final ModelAndView ERROR_404 = new ModelAndView("error/404");


    public static boolean isLogin(HttpSession session) {
        return session.getAttribute(BaseVar.SESSION_LOGIN_MANAGER) != null;
    }

    public static Object getRight(HttpSession session) {
        return session.getAttribute(BaseVar.SESSION_USER_RIGHT);
    }

    public static void setRight(HttpSession session, Object o) {
        session.setAttribute(BaseVar.SESSION_USER_RIGHT, o);
    }

    public static Object getRole(HttpSession session) {
        return session.getAttribute(BaseVar.SESSION_USER_ROLE);
    }

    public static void setRole(HttpSession session, Object o) {
        session.setAttribute(BaseVar.SESSION_USER_ROLE, o);
    }

    public static String getCookie(HttpServletRequest req, String name) {
        if (req != null) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null)
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(name))
                        return cookie.getValue();
                }
        }
        return null;
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getRootPath(HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("/");
        return path;
    }

    public static Map<String, String> flattenRequestParamMap(Map<String, String[]> arrayMap) {
        Map<String, String> r = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : arrayMap.entrySet()) {
            String[] value = entry.getValue();
            if (value != null && value.length == 1) r.put(entry.getKey(), value[0]);
            if (value != null && value.length > 1) {
                String multValue = StringUtils.join(value, ",");
                r.put(entry.getKey(), multValue);
            }
        }
        return r;
    }


    public static Map<String, String> decodeURLEncode(Map<String, String> arrayMap) {
        Map<String, String> r = new HashMap<String, String>();
        try {
            for (Map.Entry<String, String> entry : arrayMap.entrySet()) {
                String value = entry.getValue();
                if (value != null)

                    r.put(entry.getKey(), URLDecoder.decode(value, "UTF-8"));

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return r;
    }
}
