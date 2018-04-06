package cn.bevol.entity.service.utils;

import cn.bevol.entity.service.statistics.StatisticsI;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  当前线程的临时变量存储
 * @author Administrator
 *
 */
public class ComReqModel {
	/**
	 * 请求公用的参数
	 */
	private static ThreadLocal<Map<String,Object>> reqComAgrs=new ThreadLocal<Map<String,Object>>();

/**
 * 社区公用请求参数
 * @param request
 */
	public static void setReqComAgrs(HttpServletRequest  request) {
        String uuid = request.getParameter(StatisticsI.KEY_UUID);
        String machineModel = request.getParameter(StatisticsI.KEY_MODEL) == null ? "" : request.getParameter(StatisticsI.KEY_MODEL);
        String platform = request.getParameter(StatisticsI.KEY_PLATFORM);
        String version = request.getParameter(StatisticsI.KEY_VERSION) ;
        String sys_v = request.getParameter(StatisticsI.KEY_SYS_V);
        String channel = request.getParameter(StatisticsI.KEY_CHANEL);
        String ip = getIpAddr(request);

        if (StringUtils.isEmpty(platform)) {
            platform = "other";
        }
        if(StringUtils.isEmpty(sys_v)) {
        	sys_v="";
        }
        Long userId;
        try {
            userId = CommonUtils.getLoginCookieById(request);
        } catch (Exception ex) {
            userId = 0L;
        }
    	Map map=new HashMap();
    	
    	map.put("uri", request.getRequestURI());
    	map.put("url", request.getRequestURL());
    	map.put(StatisticsI.KEY_CHANEL, channel);
    	map.put(StatisticsI.FIELD_USER_ID, userId);
    	map.put(StatisticsI.FIELD_UUID, uuid);
    	if(!org.apache.commons.lang3.StringUtils.isBlank(version))
    	map.put(StatisticsI.FIELD_VERSION, version.toLowerCase());
    	map.put(StatisticsI.FIELD_PLATFORM, platform.toLowerCase());
    	map.put(StatisticsI.FIELD_MODEL, machineModel.toLowerCase());
    	map.put(StatisticsI.KEY_SYS_V, sys_v.toLowerCase());
    	map.put(StatisticsI.KEY_IP, ip);
    	map.put("createStamp", new Date().getTime()/1000);
		reqComAgrs.set(map);
	}
 	
	
	public static String getIpAddr(HttpServletRequest request) {

		String ip = request.getHeader("x-forwarded-for");

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

		ip = request.getHeader("Proxy-Client-IP");

		}

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

		ip = request.getHeader("WL-Proxy-Client-IP");

		}

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

		ip = request.getRemoteAddr();

		}

		return ip;

		}


	public static Map<String, Object> getReqComAgrs() {
		return reqComAgrs.get();
	}
 	/**
 	 * 请求响应的时候一定要清空
 	 */
	public static void removeReqComAgrs() {
		 reqComAgrs.remove();
	}

}
