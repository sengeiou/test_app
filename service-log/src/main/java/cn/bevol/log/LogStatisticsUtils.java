package cn.bevol.log;

import com.io97.utils.DateUtils;
import flexjson.JSONSerializer;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *  当前线程的临时变量存储
 * @author Administrator
 *
 */
public class LogStatisticsUtils {

    /**
     * 请求必带
     */
    public static final String KEY_USER_ID = "uid";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_PLATFORM = "o";
    public static final String KEY_MODEL = "model";
    public static final String KEY_VERSION = "v";
    public static final String KEY_SYS_V = "sys_v";
    public static final String KEY_CHANEL = "channel";
    public static final String KEY_IP = "ip";



    /**
     * active_field
     */
    public static final String COLLECTION_DAILY_REGISTER_PRE = "daily_register_";
    public static final String COLLECTION_DAILY_INIT_PRE = "daily_init_";
    public static final String COLLECTION_DAILY_LOGIN_PRE = "daily_login_";
    public static final String COLLECTION_DAILY_ACTIVE_PRE = "daily_active_";
    
    public static final String COLLECTION_NOSEARCH_PRE = "nosearch_";

    
    public static final String COLLECTION_SKIN_TEST = "log_skin_test";

    /**
     * 邮箱注册
     */
    public static final String COLLECTION_USR_REG = "user_reg";


    public static final String FIELD_USER_ID = "uid";
    public static final String FIELD_UUID = "uuid";
    public static final String FIELD_PLATFORM = "platform";
    public static final String FIELD_MODEL = "model";
    public static final String FIELD_VERSION = "version";

    public static final String FIELD_TNAME = "tname";
    public static final String FIELD_KEYWORDS = "keywords";


	/**
	 * 请求公用的参数
	 */
	private static ThreadLocal<Map<String,Object>> thdata=new ThreadLocal<Map<String,Object>>();

	/**
	 * 开始执行
	 * @param request
	 */
	public static void startExc() {
		Map<String,Object> m=new HashMap<String,Object>();
		//请求时间
		m.put("req_start_time", System.currentTimeMillis());
		thdata.set(m);
	}
	
	public static void putData(String key,Object obj) {
		thdata.get().put(key, obj);
	}
	public static Object getData(String key) {
		return thdata.get().get(key);
	}

	/**
	 * 基础日志部分
	 * @param request
	 * @param userId 用户
	 * @return
	 */
	private static Map baseLog(HttpServletRequest  request,Long userId) {
        String uuid = request.getParameter(KEY_UUID);
        String machineModel = request.getParameter(KEY_MODEL) == null ? "" : request.getParameter(KEY_MODEL);
        String platform = request.getParameter(KEY_PLATFORM);
        String version = request.getParameter(KEY_VERSION) ;
        String sys_v = request.getParameter(KEY_SYS_V);
        String channel = request.getParameter(KEY_CHANEL);
        String ip = getIpAddr(request);
        if (StringUtils.isEmpty(platform)) {
            platform = "other";
        }
        if(StringUtils.isEmpty(sys_v)) {
        	sys_v="";
        }
    	Map<String,Object> map=new HashMap<String,Object>();
    	
    	map.put("uri", request.getRequestURI().toString());
    	map.put("url", request.getRequestURL().toString());
    	if(userId!=null&&userId>0)
    	map.put(FIELD_USER_ID, userId);
    	if(!StringUtils.isBlank(uuid))
    	map.put(FIELD_UUID, uuid);
    	if(!StringUtils.isBlank(version))
    	map.put(FIELD_VERSION, version.toLowerCase());
    	if(!StringUtils.isBlank(platform))
    	map.put(FIELD_PLATFORM, platform.toLowerCase());
    	if(!StringUtils.isBlank(machineModel))
    	map.put(FIELD_MODEL, machineModel.toLowerCase());
    	if(!StringUtils.isBlank(sys_v))
    	map.put(KEY_SYS_V, sys_v.toLowerCase());
    	if(!StringUtils.isBlank(channel))
    	map.put(KEY_CHANEL, channel.toLowerCase());

    	map.put(KEY_IP, ip);

        if(getData("ret") == null){
            //个别无法拦截的接口，手动赋值
            putData("ret", -10);
        }

    	map.putAll(thdata.get());


    	
    	//请求参数
		Map<String, String[]> m =request.getParameterMap();  
		  
		for (Map.Entry<String,  String[]> entry : m.entrySet()) {  
	        Object[] value=new Object[1];  
	        if(entry.getValue() instanceof String[]){  
	            value=(String[])entry.getValue();  
	        }else{  
	            value[0]=entry.getValue();  
	        }  
	        map.put("in_"+entry.getKey(),value[0]);
		}  

    	
    	
    	
		//请求 开始时间
		long reqStartTime=(Long) map.get("req_start_time");
		
		//请求结束时间
		long reqEndTime=new Date().getTime();
		// 请求占用时间
		long reqTime=reqEndTime-reqStartTime;
    	map.put("req_start_time", reqStartTime);
    	map.put("req_end_time", reqEndTime);
    	map.put("req_exc_time", reqTime);
    	
    	map.put("req_time_format", DateUtils.format( new Date(reqStartTime), "yyyy-MM-dd'T'HH:mm:ss.SSS"));

    	Map<String,Object> ret=new HashMap<String,Object>();
    	//过滤 null 或者 ""
    	  for (Map.Entry<String, Object> entry : map.entrySet()) {
    		  Object o=entry.getValue();
    		  boolean flag=true;
    		  if(o==null) {
    			  flag=false;
    		  } else  if(o instanceof String) {
    			  String str=(String) o;
    			  if(StringUtils.isBlank(str)) {
    				  flag=false;
    			  }else {
    				  o=str.trim();
    			  }
    		  }
    		  if(flag) {
    			  ret.put(entry.getKey().toLowerCase(), o);
    		  }
    		  
    	  }
    	
    	  
    	//cookies
    	  Cookie[] cookies = request.getCookies();
    	  if(null!=cookies){
    	    for(Cookie cookie : cookies){
    	    	if(cookie!=null&&StringUtils.isNotBlank(cookie.getName())) {
    		 		String encryptData=cookie.getValue();
    			 		//String encryptData="QTxrR1Nl4Ng5vNtVALQKGvGP9%2FGgZJtj"; 
    					if(encryptData.indexOf("%")!=-1) {
    				 		try {
    				 			encryptData = java.net.URLDecoder.decode(encryptData,   "utf-8");
    				 		} catch (UnsupportedEncodingException e) {
    				 			// TODO Auto-generated catch block
    				 			e.printStackTrace();
    				 		}
    					}
    					map.put("cookie_"+cookie.getName(), encryptData);
    			    }
    	    	}
    	  }

    	  
    	 
    	return map;
	}
	/**
	 * 日志级别
		 1、ret>0的业务完全没有问题   LogNormal类
	     2、ret<0的业务异常。 LogRetLt0 类
	     3、exception的 LogException类
	 * @param request
	 * @param userId
	 */
	public static void endExc(HttpServletRequest  request,Long userId) {
    	Map map=baseLog(request,userId);
    	String json=new JSONSerializer().deepSerialize(map);
    	
    	//基础日志部分
    	Object exp= getData("exception");
    	if(exp!=null) {
    		//记录异常日志
        	if(exp instanceof Boolean){
        		Boolean flag=(Boolean) exp;
        		//异常日志
        		if(flag) {
        			LogException.error(json);
        		}
         	}
    	} else {
    		Integer ret=Integer.parseInt(getData("ret").toString());
    		if(ret!=null&&ret<0) {
        		LogRetLt0.log(json);
    		} else {
        		//记录正常日志
        		LogNormal.log(json);
    		}
    	}
    	//判断是否异常
    	
    	thdata.remove();
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


 
}
