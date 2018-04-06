package cn.bevol.app.service;

import cn.bevol.app.dao.mapper.UserInfoOldMapper;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.http.HttpUtils;
import cn.bevol.util.response.ReturnData;
import flexjson.JSONDeserializer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信提供服务
 * @author hualong
 *
 */
@Service
public class WXService {

	private static Logger logger = LoggerFactory.getLogger(WXService.class);

	@Autowired
	private UserService userService;
	@Autowired
	private UserInfoOldMapper userInfoMapper;
	
	@Autowired
	private LoginService loginService;
    /**
     * wxpc版登录处理
     *
     * @param code 微信返回的
     * @return
     */
    public ReturnData<UserInfo> wxPcLogin(String code) {
        try {
        	//获取token
            Map<String, String> userToken = getPCLoginAccessTokenByCode(code);
            if (userToken != null && userToken.get("openid") != null) {
                String accessToken = userToken.get("access_token");
                String openid = userToken.get("openid");
                String unionid = userToken.get("unionid");
                //Map<String,String> userInfo=wXService.getPCLoginAccessTokenByCode(code);
                UserInfo userInfo = null;
                ReturnData<UserInfo> rd = userService.findWXUnionid(unionid);
                if (rd.getRet() == 1) {
                    //微信注册过
                    userInfo = rd.TResult();
                    //验证微信是否登录过网站
                    if (StringUtils.isBlank(userInfo.getWxpcopenid())) {
                        //微信没有登录过
                        userInfo.setWxpcopenid(openid);
                    }

                } else {
                    //获取用户
                    userInfo = new UserInfo();
                    Map<String, Object> uinfo = getPCLoginUserInfoByAccessToken(accessToken, openid);
                    String nickname = userService.getAutoNickName(null);
                    userInfo.setNickname(nickname);
                    userInfo.setWxpcopenid(openid);
                    userInfo.setUnionid(unionid);
                    if (uinfo.get("province") != null)
                        userInfo.setProvince(uinfo.get("province") + "");
                    if (uinfo.get("city") != null)
                        userInfo.setCity(uinfo.get("city") + "");
                    if (uinfo.get("country") != null)
                        userInfo.setCountry(uinfo.get("country") + "");
                    if (uinfo.get("language") != null)
                        userInfo.setLanguage(uinfo.get("language") + "");
                    if (uinfo.get("headimgurl") != null)
                        userInfo.setHeadimgurl(uinfo.get("headimgurl") + "");
                    int sex = 2;
                    try {
                        if (uinfo.get("sex") != null)
                            sex = Integer.valueOf(uinfo.get("sex") + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ;
                    userInfo.setSex(sex);
                }
                userInfo.setLoginTime(new Date().getTime() / 1000);
                userInfo.setUsercode(CommonUtils.getMd5(userInfo.getUnionid()));
                //记录登录时间
                int i = 0;
                if (userInfo != null && userInfo.getId() != null && userInfo.getId() > 0) {
                    i = userInfoMapper.updateOne(userInfo);
                } else {
                    i = loginService.register(userInfo);
                }
                return new ReturnData<UserInfo>(userInfo);
            }
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.wxPcLogin");
			map.put("code", code);
			new LogException(e, map);
        }
        ;
        return ReturnData.ERROR;
    }

	
	/**
	 * 1、去网站的微信登录
	 * @return
	 */
	public String getPCLoginCode() {
		try {
			Map<String, String> wxkey= ConfUtils.getMap("wx_login_key", "pc");
			//appid
			String appId=wxkey.get("APPID");
			//微信回调地址
			String redirectUri=wxkey.get("REDIRECT_URI");
			
			//状态
			String state=new Date().getTime()+".bevolmxll";
			
			String url = "https://open.weixin.qq.com/connect/qrconnect?appid={APPID}&redirect_uri={REDIRECT_URI}&response_type=code&scope=snsapi_login&state={STATE}#wechat_redirect";
			return StringUtils.replaceEach(url, new String[]{"{APPID}","{REDIRECT_URI}","{STATE}"}, new String[]{appId,redirectUri,state});
		} catch(Exception e) {
            logger.error("method:toWebWXLogin   desc:" +  ExceptionUtils.getStackTrace(e));
		}
		return null;
	}
	
	
	/**
	 * 2、根据code获取accesstoken
	 * @return
	 */
	public Map<String,String> getPCLoginAccessTokenByCode(String code) {
		String resultJson="";
        Map<String,String> postData=new HashMap<String,String>();
		try {
			Map<String, String> wxkey=ConfUtils.getMap("wx_login_key", "pc");
			//appid
			String appId=wxkey.get("APPID");
			
			String appSecret=wxkey.get("APPSECRET");
			
			//微信回调地址
	        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
	        //请求url
	        postData.put("appid", appId);
	        postData.put("secret", appSecret);
	        postData.put("code", code);
	        postData.put("grant_type", "authorization_code");  
	      //  https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx55456de48120a75f&secret=5e51f7203e2cb686571e823afa8117a9&code=021glDwD1l3pq100vWtD1tbKwD1glDwS&grant_type=authorization_code
	         resultJson = HttpUtils.post1(url, postData);
	         //转换为json
	 		Map<String,String> jsonNode=  new JSONDeserializer<Map<String,String>>().deserialize(resultJson, HashMap.class);
	 		return jsonNode;
		} catch (Exception e) {
            logger.error("method:getPCLoginAccessTokenByCode arg:{\"code\":\"" + code + "\",\"resultJson\":\""+resultJson+"\",\"postData\":\""+postData.toString()+"\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
		return null;
	}
	
	
	/**
	 * 3、根据accesstoken获取 登录的用户信息
	 * @return
	 */
	public Map<String,Object> getPCLoginUserInfoByAccessToken(String accessToken,String openId) {
		String resultJson="";
		try {
	    	String url = "https://api.weixin.qq.com/sns/userinfo";
	        //请求url
	        Map<String,String> postData=new HashMap<String,String>();
	        postData.put("access_token", accessToken);
	        postData.put("openid", openId);
	        postData.put("lang", "zh_CN");
	         resultJson = HttpUtils.post(url, postData);
	         //转换为json
	 		Map<String,Object> jsonNode=  new JSONDeserializer<Map<String,Object>>().deserialize(resultJson, HashMap.class);
 		return jsonNode;
		} catch (Exception e) {
	        logger.error("method:findUserinfoByIds arg:{\"accessToken\":\"" + accessToken + "\",\"openId\":\""+openId+"\",\"resultJson\":\""+resultJson+"\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
	    }
		return null;
	}
	
 }
