package cn.bevol.entity.service.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xidea.el.Expression;
import org.xidea.el.impl.ExpressionImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io97.utils.MD5Utils;
import com.io97.utils.http.HttpClientFactory;
import com.taobao.api.ApiException;

import cn.bevol.conf.client.ConfUtils;
import flexjson.JSONDeserializer;
import sun.misc.BASE64Encoder;


public class CommonUtils {

	private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);
 
	//public static String COOKIES_DOMAIN=".bevol.cn";
	public static Integer COOKIES_TIME=ConfUtils.getResourceNum("cookie_max_time");
	public static String COOKIES_DOMAIN=ConfUtils.getResourceString("domain");

	/**
	  * 设置1cookie（接口方法）
	  * @author 刘鹏
	  * @param response
	  * @param name  cookie名字
	  * @param value cookie值
	  * @param maxAge cookie生命周期  以秒为单位
	  */
	  public static void addCookie(HttpServletResponse response,String name,String value,Integer maxAge){

		  if(!StringUtils.isBlank(value))  {
			  Cookie cookie=null;
					try {
						value = java.net.URLEncoder.encode(value,   "utf-8");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					cookie = new Cookie(name,value);
				   cookie.setPath("/");
				   cookie.setDomain(COOKIES_DOMAIN);
				    //cookie.setDomain("localhost");
				  //  if(maxAge==null){  
					      cookie.setMaxAge(COOKIES_TIME);
				   // }  else {
					      //cookie 写入失效
					//    cookie.setMaxAge(-1);
				   // }
				    
				    response.addCookie(cookie);

		 }
	}
	  
 	  /**
	  * 根据名字获取cookie（接口方法）
	  * @author 刘鹏
	  * @param request
	  * @param name cookie名字
	  * @return
	  */
	  public static void removeCookieByName(HttpServletResponse response,String name){
			 String COOKIES_DOMAIN=ConfUtils.getResourceString("domain");
				
		
		  Cookie cookie = new Cookie(name,"");
				    cookie.setPath("/");
				   cookie.setDomain(COOKIES_DOMAIN);
				    //cookie.setDomain("localhost");
				    response.addCookie(cookie);
 	    }

	  /**
	  * 将cookie封装到Map里面（非接口方法）
	  * @author 刘鹏
	  * @param request
	  * @return
	  */
	  public static String getCookieByName(HttpServletRequest request,String name){ 
		  String encryptData=null;
	  Cookie[] cookies = request.getCookies();
	  if(null!=cookies){
	    for(Cookie cookie : cookies){
	    	if(cookie!=null&&cookie.getName()!=null&&cookie.getName().equals(name)) {
		 		 encryptData=cookie.getValue();
			 		//String encryptData="QTxrR1Nl4Ng5vNtVALQKGvGP9%2FGgZJtj"; 
					if(encryptData.indexOf("%")!=-1) {
				 		try {
				 			encryptData = java.net.URLDecoder.decode(encryptData,   "utf-8");
				 			System.out.println("cookies:"+encryptData);
				 		} catch (UnsupportedEncodingException e) {
				 			// TODO Auto-generated catch block
				 			e.printStackTrace();
				 		}
					}
			    }
	    	}
	  }
	  return encryptData;
	  }

	
	public static Map outTepl(Object data,int state,String...  info) {
		Map map=new HashMap();
		if(data==null) data=new HashMap();
		map.put("result", data);
		map.put("ret", state);
		if(info.length>0)
		map.put("msg", info[0]);
		return map;
	}
	
	
	public static Map outTepl(List rows,long total,int state,String...  info) {
		Map map=new HashMap();
		map.put("result", rows);
		map.put("total", total);
		map.put("ret", state);
		if(info.length>0)
		map.put("msg", info[0]);
		return map;
	}

    //静态方法，便于作为工具类  
    public static String getMd5(String plainText) {  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(plainText.getBytes());  
            byte b[] = md.digest();  
  
            int i;  
  
            StringBuffer buf = new StringBuffer("");  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
                if (i < 0)  
                    i += 256;  
                if (i < 16)  
                    buf.append("0");  
                buf.append(Integer.toHexString(i));  
            }  
            //32位加密  
            return buf.toString();  
            // 16位的加密  
            //return buf.toString().substring(8, 24);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
    
    
    
     public static String setLoginCookie(HttpServletResponse response,long id) {
        //添加新安全算法,如果用JCE就要把它添加进去  
        /*String keycode="bevol20160107";
        byte[] enk = hex(getMd5(keycode));//用户名  
        //219dv
        Security.addProvider(new com.sun.crypto.provider.SunJCE());  
        String password = 12+"219dv"+new Date().getTime();//"1234567";//密码  
        String loginCode="logincode";
        byte[] encoded = encryptMode(enk,password.getBytes());  
        String pword = Base64.encodeBase64String(encoded);
        addCookie(response,loginCode,pword,0);*/
    	String cookes=id+"_"+getMd5(new Date().getTime()+"");
        String loginCode="logincode";
    	addCookie(response,loginCode,cookes,-1);
    	return cookes;
    }
    public static String getLoginCookie(HttpServletRequest request) {
         String loginCode="logincode";
        String cookies=getCookieByName(request, loginCode);
        if(!StringUtils.isBlank(cookies)) {
        	  return cookies;
        }
        return null;
    }
    
    
     


    public static void main(String[] args) throws ApiException {
    	
    /*	System.out.println(isMobile("189109411"));
		java.util.Random random=new java.util.Random();// 定义随机类
		int result=random.nextInt(2);// 返回[0,10)集合中的整数，注意不包括10
		System.out.println(result);
		System.out.println(checkUserName("花龙1才小aa24"));
		System.out.println(getStrLength("龙a"));
		System.out.println(RandomStringUtils.random(4, false, true));
 		String encryptData="07YEXuQaM4nnBuZZ3GitJ01cwZaRSNpN";  
		if(encryptData.indexOf("%")!=-1) {
		try {
			encryptData = java.net.URLDecoder.decode(encryptData.toString(),   "utf-8");
		} catch (Exception e) {
			
 		}
		}
		//  System.out.println(mytext2);

			// key);
		String key = CommonUtils.getMd5("bevol20160107");
		byte[] bytes = new byte[8];
		bytes[0] = (byte) 'm';
		bytes[1] = (byte) 'b';
		bytes[2] = (byte) 'e';
		bytes[3] = (byte) 'v';
		bytes[4] = (byte) 'o';
		bytes[5] = (byte) 'l';
		String str = CompatibleDesUtil.decrypt(encryptData, new BASE64Encoder().encodeBuffer(key.getBytes()),
				bytes);
		if(str!=null) {
			String id=str.substring(0, str.lastIndexOf("219dv"));
			System.out.println(id);
}
*/
    	System.out.println(getImag("d//c//d//dddd.jpg"));
    
    }

	public static void sendRegCode(String phone, String qcode, String desc, String tcode) {
		// TODO Auto-generated method stub
	}

	private final static String idcode="219dv";
	
	private final static String keycode=MD5Utils.encode("bevol20160107");
	
	private final static byte[] mbtys=new byte[8];
	static{
		mbtys[0] = (byte) 'm';
		mbtys[1] = (byte) 'b';
		mbtys[2] = (byte) 'e';
		mbtys[3] = (byte) 'v';
		mbtys[4] = (byte) 'o';
		mbtys[5] = (byte) 'l';
	}

	public static long getLoginCookieById(HttpServletRequest request) {
		try {
		String encryptData=getLoginCookie(request);
		// key);
 		String str = CompatibleDesUtil.decrypt(encryptData, new BASE64Encoder().encodeBuffer(keycode.getBytes()),
				mbtys);
		if(str!=null) {
			String id=str.substring(0, str.lastIndexOf(idcode));
			return Long.parseLong(id);
		}
		}catch (Exception ex){
		}
		return 0;
	}
	
	public static long loginTime=60*60*24*10;//10天的登录时间
	public static long getLoginCookieById2(HttpServletRequest request) {
		try {
		String encryptData=getLoginCookie(request);
		// key);
 		String str = CompatibleDesUtil.decrypt(encryptData, new BASE64Encoder().encodeBuffer(keycode.getBytes()),
				mbtys);
		if(str!=null) {
			String id=str.substring(0, str.lastIndexOf(idcode));
			//判断是否过期过期重新登录
			String time=str.substring(str.lastIndexOf(idcode)+idcode.length());
			if((Long.parseLong(time)+loginTime)>new Date().getTime()/1000)
				return Long.parseLong(id);
		}
		}catch (Exception ex){
		}
		return 0;
	}

	
	public static void setLoginCookieId(HttpServletResponse response,long id,Integer maxtime) {
		
		String msg = id+""+idcode+""+new Date().getTime()/1000; //动态cookie
		String encryptData= CompatibleDesUtil.encrypt(msg,new BASE64Encoder().encodeBuffer(keycode.getBytes()),mbtys);
		encryptData=encryptData.trim();
		addCookie(response,"logincode",encryptData,maxtime);
	}  
	
	/**
	 * 获取图片域名
	 * @return
	 */
	public static String getImagDomain() {
		/*java.util.Random random=new java.util.Random();// 定义随机类
        List<Map<String, String>> los=ConfUtils.getList("img_domain");
		int result=random.nextInt(los.size());// 返回[0,10)集合中的整数，注意不包括10
		return los.get(result).get("domain");*/
		java.util.Random random=new java.util.Random();// 定义随机类
		int result=random.nextInt(1);
		return "https://img"+result+".bevol.cn";
	}
	
	/**
	 * 
	 * @param img 检查地址
	 * @return
	 */
	public static String imgReplaceHttp(String img) {
		if(!StringUtils.isBlank(img)) {
			if((img.indexOf("http")!=-1||img.indexOf("img.bevol.cn")!=-1)) {
				img=StringUtils.replace(img, "http:", "https:");
				img=StringUtils.replace(img, "https://img.bevol.cn", getImagDomain());
			} 
		}
		return img;
	}

	private final static Map<String,ImageEntity> imagePrvs=new HashMap<String,ImageEntity>();

	static {
		
		imagePrvs.put("apply/reason", new ImageEntity("Apply/reason",""));

		//用户上传产品 todo
		imagePrvs.put("goods/userUpload", new ImageEntity("Goods/userupload",""));
		
		//用户头像 todo
		imagePrvs.put("uploadFile/head", new ImageEntity("UploadFile/head",""));
		
		//试用产品目录--todo
		imagePrvs.put("apply_goods", new ImageEntity("apply_goods",""));

		//其它的 exp:首页分类-todo
		imagePrvs.put("back/images", new ImageEntity("back/images",""));
		
		//产品
		imagePrvs.put("goods", new ImageEntity("Goods/source",""));
		
		//达人原创/发现
		imagePrvs.put("find", new ImageEntity("Find",""));
		
		//举报/反馈/纠错
		imagePrvs.put("feedback", new ImageEntity("feedback",""));
		
		//热门话题/清单
		imagePrvs.put("lists", new ImageEntity("Lists",""));
		
		//评论
		imagePrvs.put("comment", new ImageEntity("comment/images",""));
		
		//心得
		imagePrvs.put("user_part/lists", new ImageEntity("user_part/lists",""));
		
		//护肤方案
		imagePrvs.put("user_skin_protection/images", new ImageEntity("user_skin_protection/images",""));
		
		//用户上传的产品临时图片
		imagePrvs.put("goods_upload/images", new ImageEntity("goods_upload/images",""));
	}
	
	/**
	 * 图片路径结构
	 * @author Administrator
	 *
	 */
	static class ImageEntity{
		String path;
		
		String defImage;
		public ImageEntity() {
			
		}
		public ImageEntity(String path, String defImage) {
			super();
			this.path = path;
			this.defImage = defImage;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
		public String getDefImage() {
			return defImage;
		}
		public void setDefImage(String defImage) {
			this.defImage = defImage;
		}

 	}
	
	/**
	 * 拼装图片路径
	 * @param tname key
	 * @param image 图片名称
	 * @return
	 */
 	public static  String getImageSrc(String tname,String image){
		String domain=getImagDomain();
		ImageEntity ie=imagePrvs.get(tname);
		String path=ie.getPath();
		String defimg=ie.getDefImage();
		if(StringUtils.isBlank(image)) {
			//默认图片
			if(StringUtils.isBlank(defimg)) {
				//最终默认图片
				return null;
			}else {
				return domain+"/"+defimg;
			}
		}
		return domain+"/"+path+"/"+image;
	}
 	
 	/**
 	 * 获取后缀名称
 	 * @param src
 	 * @return
 	 */
	public static String getImag(String src) {
		if(StringUtils.isNotBlank(src)) {
			int index=src.lastIndexOf("/");
			if(index!=-1) {
				return src.substring(index+1);
			} 
			 index=src.lastIndexOf("\\");
			 if(index!=-1) {
					return src.substring(index+1);
				} 
		}
		return src;
	}
	public static void sendEmail(String toEmail,String subject,String content) throws EmailException, MessagingException {
		
		List<Map<String,String>> lms= ConfUtils.getList("email_info", "host_info");
		
		java.util.Random random=new java.util.Random();// 定义随机类
		int result=random.nextInt(lms.size());// 返回[0,10)集合中的整数，注意不包括10
		Map<String,String> eh=lms.get(result);
		String hostName=eh.get("host_name");
		String authName=eh.get("auth_name");
		String authPass=eh.get("auth_pass");
		String fromEamil=eh.get("from_eamil");
		
		HtmlEmail email = new HtmlEmail();
	   	//smtp host 
	
		email.setHostName(hostName);
	   	//登陆邮件服务器的用户名和密码
	   	email.setAuthentication(authName,authPass);
	   	//接收人
	   	email.addTo(toEmail);
	   	//发送人
	   	email.setFrom(fromEamil);
	   	//标题
        email.setCharset("UTF-8");    
	   	email.setSubject(subject);
        //email.buildMimeMessage();   
        //设置内容的字符集为UTF-8,先buildMimeMessage才能设置内容文本  
	   	content=StringUtils.replaceEach(content, new String[]{"&lt;br&gt;"}, new String[]{"<br>"});
        email.setHtmlMsg(content);
      //  email.getMimeMessage().setText("测试邮件内容","UTF-8");   
        /*email.getMimeMessage().setText(content,"UTF-8");   
        email.sendMimeMessage();  */ 
        email.send();
	}
	/**
	 * 验证用户名
	 * @param userName
	 * @return
	 */
	public static boolean checkUserName(String userName) {
		String regex = "([a-z]|[A-Z]|[0-9]|[\\u4e00-\\u9fa5])+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(userName);
		return m.matches();
	}

    /**
     * 获取字符串的长度，对双字符（包括汉字）按两位计数
     * 
     * @param value
     * @return
     */
    public static int getStrLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }
    
    /** 
     * 手机号验证 
     *  
     * @param  str 
     * @return 验证通过返回true 
     */  
    public static boolean isMobile(String str) {   
    	if(StringUtils.isBlank(str)) return false;
        Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
        p = Pattern.compile("^[1][3,4,5,8,4,7,9][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);  
        b = m.matches();   
        return b;  
    }

    /** 
     * 邮箱格式验证 
     *  
     * @param  str 
     * @return 验证通过返回true 
     */  
    public static boolean isEmail(String str) {   
        Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
        p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        m = p.matcher(str);
        b = m.matches();
        return b;  
    }
    
    /**
     * 时间戳转为yyyy-MM-dd格式
     * @param timeStamp
     * @return
     */
    public static String timeStampCastToDate(Long timeStamp){
    	if(null!=timeStamp && timeStamp>0){
    		Long timestamp = timeStamp*1000;  
            String date = new java.text.SimpleDateFormat("yyyy年MM月dd号").format(new java.util.Date(timestamp));  
            return date;
    	}
    	return "";
    }
    
    /**
     * 对象转Map
     * 去除了对象中的null
     * @param str
     * @return
     */
    public static Map ObjectToMap(Object obj) {
    	try{
    		if(null!=obj){
    			ObjectMapper mapper = new ObjectMapper();  
                String json=mapper.writeValueAsString(obj);
            	Map jmap=mapper.readValue(json, Map.class);
            	return jmap;
    		}
    		return new HashMap();
    	}catch(Exception e){
    		Map map=new HashMap();
    		map.put("method", "CommonUtils.ObjectToMap");
    	}
    	return null;
    }
    
    /**
     *  v3.2
     * 前端返回完整路径,后端截取图片名存入数据库
     * @param images: 多张图片,逗号分隔
     * @return
     */
    public static String getImages(String images) {
    	try{
    		if(StringUtils.isNotBlank(images)){
    			String[] imagess=images.split(",");
    			StringBuffer newImages=new StringBuffer();
    			for(int i=0;i<imagess.length;i++){
    				int index=imagess[i].lastIndexOf("/");
    				String image=imagess[i].substring(index+1, imagess[i].length());
        			newImages.append(image+",");
    			}
    			if(newImages.length()>0){
    				images=newImages.substring(0, newImages.length()-1);
    				return images;
    			}
    		}
    		return images;
    	}catch(Exception e){
    		Map map=new HashMap();
    		map.put("method", "CommonUtils.getImages");
    	}
    	return null;
    }
    
    public static long curDayMaxAndMinTime(int type){
    	Calendar cal = Calendar.getInstance();
    	if(type==0){
    		//最大值
    		cal.set(Calendar.HOUR_OF_DAY, 24);
    	}else if(type==1){
    		cal.set(Calendar.HOUR_OF_DAY, 0);
    	}
		
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long dayMustTime = cal.getTimeInMillis() / 1000;
    	return dayMustTime;
    } 
    
    
    /***
     * 身份验证
     * @param cardNo 身份证 号码
     * @param realName 姓名
     * @return null，认证无法通过
     * @throws Exception
     */
    public static Map verifyIdCard(String cardNo,String realName) throws Exception{
    	//1为测试
		int test = ConfUtils.getResourceNum("user_authentication_test");

    	if(1==test) {
        	Map mp=new HashMap();
        	mp.put("test", "ok");
    		return mp;
    	}
    	
    	Map<String,String> maps=ConfUtils.getJSONMap("verifyidcard_info");
    	String url=maps.get("url");
    	String appcode=maps.get("appcode");
    	String successFd=maps.get("success_field");
    	String fields[]=successFd.split("=");
    	String idcardField=maps.get("idcard_field");

        Map<String,Expression> jselExp=new HashMap<String,Expression>();
    	jselExp.put(fields[0].trim(), new ExpressionImpl(fields[0].trim()));
    	jselExp.put(idcardField.trim(), new ExpressionImpl(idcardField.trim()));
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
 	    String rn=URLEncoder.encode(realName, "UTF-8");
	   // String url=host+path+"?cardNo="+cardNo+"&realName="+rn;
 	 // String url="http://idcard.market.alicloudapi.com/lianzhuo/idcard?cardno=${cardNo}&name=${realName}";
 	 	  url=url.replaceAll("&amp;", "&");
 	 	  String adrs= StringUtils.replaceEach(url, new String[]{"${cardNo}","${realName}"}, new String[]{cardNo,rn});
 	    String result=null;
	    	HttpGet  get = new HttpGet(adrs);
	    	get.addHeader("Authorization", "APPCODE " + appcode);
            HttpResponse response = HttpClientFactory.get().execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (response.getEntity() != null) {
                	result= IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                }
        	    Map m=new JSONDeserializer<Map>().deserialize(result);
				Expression exp=jselExp.get(fields[0]);
				Object result1=null;
				try {
					//没有
   				 	 result1 = exp.evaluate(m);// 555
				} catch(Exception tp) {
					
				}
				if(result1!=null) {
					String val=result1.toString();
					if(val.equals(fields[1])) {
						//成功
						Map detail=(Map) jselExp.get(idcardField).evaluate(m);
						return detail;
					}
				}
	    	return null;
            }
            return null;
            
            //
    }
 
 }
