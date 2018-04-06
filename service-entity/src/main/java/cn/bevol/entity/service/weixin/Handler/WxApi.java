package cn.bevol.entity.service.weixin.Handler;

import com.aliyun.oss.HttpMethod;
import com.io97.utils.DateUtils;
import com.io97.utils.http.HttpUtils;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 微信 API、微信基本接口
 * 
 */

public class WxApi {

	//token 接口
	private static final String TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	
	//创建菜单
	private static final String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";
	
	//创建个性化菜单
	private static final String MENU_ADDCONDITIONAL = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=%s";
	
	//删除菜单
	private static final String MENU_DELETE = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s";
	
	//获取账号粉丝信息
	private static final String GET_FANS_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
	
	//获取账号粉丝列表
	private static final String GET_FANS_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s";
	
	//获取批量素材
	private static final String GET_BATCH_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=%s";
	
	//上传多媒体资料接口
	private static final String UPLOAD_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s";

	private static final String ADD_MATERIAL="https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=%s&type=%s";
	//上传永久素材：图文
	private static final String UPLOAD_NEWS = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=%s";
	
	//群发接口
	private static final String MASS_SEND = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=%s";
	
	//网页授权OAuth2.0获取code
	private static final String GET_OAUTH_CODE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s#wechat_redirect";
	
	//网页授权OAuth2.0获取token
	private static final String GET_OAUTH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
		
	//网页授权OAuth2.0获取用户信息
	private static final String GET_OAUTH_USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
	
	//生成二维码
	private static final String CREATE_QRCODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
	
	//根据ticket获取二维码图片
	private static final String SHOW_QRCODE = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
	
	//js ticket
	private static final String GET_JSAPI_TICKET="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
	
	//发送客服消息
	private static final String SEND_CUSTOM_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
	
	//模板消息接口
	private static final String SEND_TEMPLATE_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
	
	
	
	//获取token接口
	public static String getTokenUrl(String appId,String appSecret){
		return String.format(TOKEN, appId, appSecret);
	}
	
	//获取上传Media接口
	public static String getUploadMediaUrl(String token,String type){
		return String.format(UPLOAD_MEDIA, token, type);
	}
	
	//获取菜单创建接口
	public static String getMenuCreateUrl(String token){
		return String.format(MENU_CREATE, token);
	}
	
	//获取个性化菜单创建接口
	public static String getMenuAddconditionalUrl(String token){
		return String.format(MENU_ADDCONDITIONAL, token);
	}
	
	//获取菜单删除接口
	public static String getMenuDeleteUrl(String token){
		return String.format(MENU_DELETE, token);
	}
	
	//获取粉丝信息接口
	public static String getFansInfoUrl(String token,String openid){
		return String.format(GET_FANS_INFO, token, openid);
	}
	
	//获取粉丝列表接口
	public static String getFansListUrl(String token,String nextOpenId){
		if(nextOpenId == null){
			return String.format(GET_FANS_LIST, token);
		}else{
			return String.format(GET_FANS_LIST + "&next_openid=%s", token, nextOpenId);
		}
	}
	
	//获取素材列表接口
	public static String getBatchMaterialUrl(String token){
		return String.format(GET_BATCH_MATERIAL, token);
	}
	
	//获取上传图文消息接口
	public static String getUploadNewsUrl(String token){
		return String.format(UPLOAD_NEWS, token);
	}
	
	//群发接口
	public static String getMassSendUrl(String token){
		return String.format(MASS_SEND, token);
	}
	
	//网页授权OAuth2.0获取code
	public static String getOAuthCodeUrl(String appId ,String redirectUrl ,String scope ,String state){
		return String.format(GET_OAUTH_CODE, appId, urlEnodeUTF8(redirectUrl), "code", scope, state);
	}
	
	//网页授权OAuth2.0获取token
	public static String getOAuthTokenUrl(String appId ,String appSecret ,String code ){
		return String.format(GET_OAUTH_TOKEN, appId, appSecret, code);
	}
	
	//网页授权OAuth2.0获取用户信息
	public static String getOAuthUserinfoUrl(String token ,String openid){
		return String.format(GET_OAUTH_USERINFO, token, openid);
	}
	
	//获取创建二维码接口url
	public static String getCreateQrcodeUrl(String token){
		return String.format(CREATE_QRCODE, token);
	}
	
	//获取显示二维码接口
	public static String getShowQrcodeUrl(String ticket){
		return String.format(SHOW_QRCODE, ticket);
	}
	
	//获取js ticket url
	public static String getJsApiTicketUrl(String token){
		return String.format(GET_JSAPI_TICKET, token);
	}
	
	//获取发送客服消息 url
	public static String getSendCustomMessageUrl(String token){
		return String.format(SEND_CUSTOM_MESSAGE, token);
	}
	
	//获取发送模板消息 url
	public static String getSendTemplateMessageUrl(String token){
		return String.format(SEND_TEMPLATE_MESSAGE, token);
	}
	
	
	
	/**
	 * 获取创建临时二维码post data
	 * @param expireSecodes 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。
	 * @param scene 临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000)
	 * @return
	 */
	public static String getQrcodeJson(Integer expireSecodes, Integer scene){
		String postStr = "{\"expire_seconds\":%d,\"action_name\":\"QR_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":%d}}";
		return String.format(postStr, expireSecodes, scene);
	}
	/**
	 * 获取创建临时二维码post data
	 * @param scene 临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000)
	 * @return
	 */
	public static String getQrcodeLimitJson(Integer scene){
		String postStr = "{\"action_name\":\"QR_LIMIT_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":%d}}";
		return String.format(postStr, scene);
	}
	//获取永久二维码
	public static String getQrcodeLimitJson(String sceneStr){
		String postStr = "{\"action_name\":\"QR_LIMIT_STR_SCENE\",\"action_info\":{\"scene\":{\"scene_str\":%s}}";
		return String.format(postStr, sceneStr);
	}
	
	//获取接口访问凭证
	public static AccessToken getAccessToken(String appId, String appSecret) {
		AccessToken token = null;
		String tockenUrl = WxApi.getTokenUrl(appId, appSecret);
		String  obj = HttpUtils.get(tockenUrl);
		if(!StringUtils.isEmpty(obj)) {
			JSONObject jsonObject = JSONObject.fromObject(obj);
			if (null != jsonObject && !jsonObject.containsKey("errcode")) {
				try {
					token = new AccessToken();
					token.setAccessToken(jsonObject.getString("access_token"));
					token.setExpiresIn(jsonObject.getInt("expires_in"));
				} catch (JSONException e) {
					token = null;//获取token失败
				}
			} else if (null != jsonObject) {
				token = new AccessToken();
				token.setErrcode(jsonObject.getInt("errcode"));
			}
		}
		return token;
	}


	
	//获取OAuth2.0 Token
	public static OAuthAccessToken getOAuthAccessToken(String appId, String appSecret, String code) {
		OAuthAccessToken token = null;
		String tockenUrl = getOAuthTokenUrl(appId, appSecret, code);
		JSONObject jsonObject = httpsRequest(tockenUrl, HttpMethod.GET, null);
		if (null != jsonObject && !jsonObject.containsKey("errcode")) {
			try {
				token = new OAuthAccessToken();
				token.setAccessToken(jsonObject.getString("access_token"));
				token.setExpiresIn(jsonObject.getInt("expires_in"));
				token.setOpenid(jsonObject.getString("openid"));
				token.setScope(jsonObject.getString("scope"));
			} catch (JSONException e) {
				token = null;//获取token失败
			}
		}else if(null != jsonObject){
			token = new OAuthAccessToken();
			token.setErrcode(jsonObject.getInt("errcode"));
		}
		return token;
	}

	public static Ticket getTicket(String token){
		Ticket ticket = null;
		String ticketUrl = getJsApiTicketUrl(token);
		String obj = HttpUtils.get(ticketUrl);
		if(!StringUtils.isEmpty(obj)){
			JSONObject jsonObject = JSONObject.fromObject(obj);
			if(null != jsonObject && jsonObject.containsKey("ticket")){
				try{
					String ticketString = jsonObject.getString("ticket");
					String errmsg = jsonObject.getString("errmsg");
					Integer errcode = jsonObject.getInt("errcode");
					Integer expiresIn = jsonObject.getInt("expires_in");
					ticket = new Ticket(errcode, errmsg, ticketString, expiresIn);
				}catch (JSONException e){
					e.printStackTrace();
					return null;
				}
			}else if(null != jsonObject && jsonObject.containsKey("errcode")){
				try{
					String errmsg = jsonObject.getString("errmsg");
					Integer errcode = jsonObject.getInt("errcode");
					ticket = new Ticket(errcode, errmsg);
				}catch (JSONException e){
					e.printStackTrace();
					return null;
				}
			}else{
				return null;
			}
		}
		return ticket;
	}
	
	/**
	 * 上传多媒体文件
	 * 返回media_id
	 */
	public static String uploadMedia(String accessToken, String mediaType, String mediaUri) {
		String uploadMediaUrl = String.format(UPLOAD_MEDIA, accessToken, mediaType);
		String boundary = "----------" + System.currentTimeMillis();// 设置边界  
		try {
			URL uploadUrl = new URL(uploadMediaUrl);
			HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
			uploadConn.setDoOutput(true);
			uploadConn.setDoInput(true);
			uploadConn.setRequestMethod("POST");
			uploadConn.setRequestProperty("Charset", "UTF-8");
			// 设置请求头Content-Type
			uploadConn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
			// 获取媒体文件上传的输出流（往微信服务器写数据）
			OutputStream outputStream = uploadConn.getOutputStream();

			URL mediaUrl = new URL(mediaUri);
			HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();
			meidaConn.setDoOutput(true);
			meidaConn.setRequestMethod("GET");
			meidaConn.setRequestProperty("Charset", "UTF-8");
			// 从请求头中获取内容类型
			String contentType = meidaConn.getHeaderField("Content-Type");
			// 根据内容类型判断文件扩展名
			String fileExt = ".jpg";
			// 请求体开始
			outputStream.write(("--" + boundary + "\r\n").getBytes());
			outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"file1%s\"\r\n",fileExt).getBytes());
			outputStream.write(String.format("Content-Type: %s\r\n\r\n",contentType).getBytes());
			
			// 获取媒体文件的输入流（读取文件）
			BufferedInputStream bis = new BufferedInputStream(meidaConn.getInputStream());
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1) {
				outputStream.write(buf, 0, size);
			}
			// 请求体结束
			outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
			outputStream.close();
			bis.close();
			meidaConn.disconnect();

			// 获取媒体文件上传的输入流（从微信服务器读数据）
			InputStream inputStream = uploadConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			uploadConn.disconnect();
			// 使用JSON-lib解析返回结果
			JSONObject jsonObject = JSONObject.fromObject(buffer.toString());
			if(jsonObject.containsKey("media_id"))
				return jsonObject.getString("media_id");
			return null;
		} catch (Exception e) {
			String error = String.format("上传媒体文件失败：%s", e);
			System.out.println(error);
		}
		return null;
	}


	/**
	 * 上传其他永久素材(图片素材的上限为5000，其他类型为1000)
	 *
	 * @return
	 * @throws Exception
	 */
	public static String addMaterialEver(String accessToken,String type, String fileurl) {
		try {

			URL url = new URL(fileurl);
			DataInputStream dataInputStream = new DataInputStream(url.openStream());
			String imageName = DateUtils.nowInSeconds()  + ".jpg";
			FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));

			byte[] buffer = new byte[1024];
			int length;

			while ((length = dataInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, length);
			}

			dataInputStream.close();
			fileOutputStream.close();


			File file = new File(imageName);
			//上传素材
			String path = String.format(ADD_MATERIAL, accessToken, type);
			String result = connectHttpsByPost(path, file);
			result = result.replaceAll("[\\\\]", "");
			System.out.println("result:" + result);
			JSONObject resultJSON = JSONObject.fromObject(result);
			if (resultJSON != null) {
				if (resultJSON.containsKey("media_id")) {
						return resultJSON.getString("media_id");
				} else {
					System.out.println("上传" + type + "永久素材失败");
				}
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static  String connectHttpsByPost(String path, File file) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		URL urlObj = new URL(path);
		//连接
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		String result = null;
		con.setDoInput(true);

		con.setDoOutput(true);

		con.setUseCaches(false); // post方式不能使用缓存

		// 设置请求头信息
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		// 设置边界
		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type",
				"multipart/form-data; boundary="
						+ BOUNDARY);

		// 请求正文信息
		// 第一部分：
		StringBuilder sb = new StringBuilder();
		sb.append("--"); // 必须多两道线
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"" + file.length() + "\";filename=\""

				+file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		byte[] head = sb.toString().getBytes("utf-8");
		// 获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
		// 输出表头
		out.write(head);

		// 文件正文部分
		// 把文件已流文件的方式 推入到url中
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();
		// 结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
		out.write(foot);
		out.flush();
		out.close();
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}








	//发送请求
	public static JSONObject httpsRequest(String requestUrl, HttpMethod get, String requestMethod) {

		return httpsRequest(requestUrl,requestMethod,null);
	}
	
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		try {
			TrustManager[] tm = { new WeiXinX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static byte[] httpsRequestByte(String requestUrl, HttpMethod method) {
		return httpsRequestByte(requestUrl,method);
	}
	
	public static byte[] httpsRequestByte(String requestUrl, String requestMethod, String outputStr) {
		try {
			TrustManager[] tm = { new WeiXinX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = conn.getInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
		    byte[] buffer = new byte[4096];
		    int n = 0;
		    while (-1 != (n = inputStream.read(buffer))) {
		        output.write(buffer, 0, n);
		    }
		    return output.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String urlEnodeUTF8(String str){
        String result = str;
        try {
            result = URLEncoder.encode(str,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
}




class WeiXinX509TrustManager implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}