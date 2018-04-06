package cn.bevol.entity.service.weixin;

import cn.bevol.entity.service.weixin.Handler.*;
import cn.bevol.entity.service.weixin.util.Utils;
import cn.bevol.entity.service.weixin.vo.TemplateMessage;
import cn.bevol.model.items.ErrCode;
import cn.bevol.model.items.MediaType;
import cn.bevol.model.items.MpAccount;
import cn.bevol.model.items.MsgType;
import cn.bevol.entity.service.weixin.msg.AccountFans;
import cn.bevol.entity.service.weixin.msg.MsgNews;
import com.aliyun.oss.HttpMethod;


import com.io97.utils.DateUtils;
import com.io97.utils.PropertyUtils;
import com.io97.utils.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 微信 客户端，统一处理微信相关接口
 */
@Service
public class WxApiClientService {
	private static final Logger logger = LoggerFactory.getLogger(WxApiClientService.class);
	@Resource
	WxCscheService wxCscheService;
	MpAccount mpAccount;

	public WxApiClientService() {
		mpAccount = new MpAccount();
		mpAccount.setUrl(PropertyUtils.getStringValue("bevol.wx.requiredUrl"));
		mpAccount.setAccount(PropertyUtils.getStringValue("bevol.wx.account"));
		mpAccount.setAppid(PropertyUtils.getStringValue("bevol.wx.appid"));
		mpAccount.setToken(PropertyUtils.getStringValue("third.aliyun.accessId"));
		mpAccount.setAppsecret(PropertyUtils.getStringValue("bevol.wx.appsecret"));
	}


	//获取accessToken
	private String getAccessToken(MpAccount mpAccount) {
		AccessToken token = wxCscheService.getAccessToken(mpAccount);
		return token.getAccessToken();
	}

	private String getTicket(MpAccount mpAccount){
		String token = getAccessToken(mpAccount);
		Ticket ticket = wxCscheService.getTicket(token);
		return ticket.getticket();
	}

	//获取OAuthAccessToken
	public OAuthAccessToken getOAuthAccessToken(String code) {
		//获取唯一的accessToken，如果是多账号，请自行处理
		OAuthAccessToken token = wxCscheService.getSingleOAuthAccessToken();
		if (token != null && !token.isExpires()) {//不为空，并且没有过期
			return token;
		} else {
			token = WxApi.getOAuthAccessToken(mpAccount.getAppid(), mpAccount.getAppsecret(), code);
			if (token != null) {
				if (token.getErrcode() != null) {//获取失败
					System.out.println("## getOAuthAccessToken Error = " + token.getErrmsg());
				} else {
					token.setOpenid(null);//获取OAuthAccessToken的时候设置openid为null；不同用户openid缓存
					wxCscheService.addOAuthAccessToken(token);
					return token;
				}
			}
			return null;
		}
	}

	public Map<String, Object> getJsSdkConfig(String url){
        String ticket = getTicket(mpAccount);
        Integer timeStamp = DateUtils.nowInSeconds();
        String nonceStr = Utils.getRandomString(20);
        String wxOri = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%d&url=%s",
                ticket, nonceStr, timeStamp, url);
        String signature = DigestUtils.shaHex(wxOri);
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", timeStamp);
        map.put("nonceStr", nonceStr);
        map.put("ticket", ticket);
        map.put("signature", signature);
        map.put("appId", mpAccount.getAppid());
        map.put("wxOri", wxOri);
        map.put("url", url);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("data", map);
        returnMap.put("info", "");
        returnMap.put("state", 1);
        return returnMap;
    }
	//获取openId
	public String getOAuthOpenId(String code) {
		OAuthAccessToken token = WxApi.getOAuthAccessToken(mpAccount.getAppid(), mpAccount.getAppsecret(), code);
		if (token != null) {
			if (token.getErrcode() != null) {//获取失败
				System.out.println("## getOAuthAccessToken Error = " + token.getErrmsg());
			} else {
				return token.getOpenid();
			}
		}
		return null;
	}

	//发布菜单
	public JSONObject publishMenus(String menus) {
		String accessToken = getAccessToken(mpAccount);
		String url = WxApi.getMenuCreateUrl(accessToken);
		return WxApi.httpsRequest(url, HttpMethod.POST, menus);
	}

	//创建个性化菜单
	public JSONObject publishAddconditionalMenus(String menus) {
		String accessToken = getAccessToken(mpAccount);
		String url = WxApi.getMenuAddconditionalUrl(accessToken);
		return WxApi.httpsRequest(url, HttpMethod.POST, menus);
	}

	//删除菜单
	public JSONObject deleteMenu() {
		String accessToken = getAccessToken(mpAccount);
		String url = WxApi.getMenuDeleteUrl(accessToken);
		return WxApi.httpsRequest(url, HttpMethod.POST, null);
	}


	//上传图文消息
	public JSONObject uploadNews(List<MsgNews> msgNewsList) {
		JSONObject rstObj = new JSONObject();
		String accessToken = getAccessToken(mpAccount);
		try {
			JSONArray jsonArr = new JSONArray();
			for (MsgNews news : msgNewsList) {
				JSONObject jsonObj = new JSONObject();
				//上传图片素材
				String mediaId = WxApi.uploadMedia(accessToken, MediaType.Image.toString(), news.getPicpath());
				jsonObj.put("thumb_media_id", mediaId);
				if (news.getAuthor() != null) {
					jsonObj.put("author", news.getAuthor());
				} else {
					jsonObj.put("author", "");
				}
				if (news.getTitle() != null) {
					jsonObj.put("title", news.getTitle());
				} else {
					jsonObj.put("title", "");
				}
				if (news.getFromurl() != null) {
					jsonObj.put("content_source_url", news.getFromurl());
				} else {
					jsonObj.put("content_source_url", "");
				}
				if (news.getBrief() != null) {
					jsonObj.put("digest", news.getBrief());
				} else {
					jsonObj.put("digest", "");
				}
				if (news.getShowpic() != null) {
					jsonObj.put("show_cover_pic", news.getShowpic());
				} else {
					jsonObj.put("show_cover_pic", "1");
				}
				jsonObj.put("content", news.getDescription());
				jsonArr.add(jsonObj);
			}
			JSONObject postObj = new JSONObject();
			postObj.put("articles", jsonArr);
			rstObj = WxApi.httpsRequest(WxApi.getUploadNewsUrl(accessToken), HttpMethod.POST, postObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rstObj;
	}

	public String uploadImage(String picpath) {
		String accessToken = getAccessToken(mpAccount);
		//上传图片素材
		return WxApi.addMaterialEver(accessToken, MediaType.Image.toString(), picpath);
	}

	/**
	 * 根据openid群发接口
	 *
	 * @param mediaId：素材的id；通过素材管理,或者上传素材获取
	 * @param msgType
	 * @return
	 */
	public JSONObject massSendByOpenIds(List<String> openids, String mediaId, MsgType msgType) {
		if (openids != null && openids.size() > 0) {
			JSONObject postObj = new JSONObject();
			JSONObject media = new JSONObject();
			postObj.put("touser", openids);
			media.put("media_id", mediaId);
			postObj.put(msgType.toString(), media);
			postObj.put("msgtype", msgType.toString());
			String accessToken = getAccessToken(mpAccount);
			return WxApi.httpsRequest(WxApi.getMassSendUrl(accessToken), HttpMethod.POST, postObj.toString());
		}
		return null;
	}

	/**
	 * 根据openid群发文本消息
	 *
	 * @param openids
	 * @param content
	 * @return
	 */
	public JSONObject massSendTextByOpenIds(List<String> openids, String content) {
		if (openids != null && openids.size() > 0) {
			if (openids.size() == 1) {//根据openId群发，size至少为2
				openids.add("1");
			}
			String[] arr = (String[]) openids.toArray(new String[openids.size()]);
			JSONObject postObj = new JSONObject();
			JSONObject text = new JSONObject();
			postObj.put("touser", arr);
			text.put("content", content);
			postObj.put("text", text);
			postObj.put("msgtype", MsgType.Text.toString());
			String accessToken = getAccessToken(mpAccount);
			return WxApi.httpsRequest(WxApi.getMassSendUrl(accessToken), HttpMethod.POST, postObj.toString());
		}
		return null;
	}

	/**
	 * 发送客服消息
	 *
	 * @param openid
	 * @param content 消息内容
	 * @return
	 */
	public JSONObject sendCustomTextMessage(String openid, String content) {
		JSONObject jsonObject = null;
		if (!StringUtils.isBlank(openid) && !StringUtils.isBlank(content)) {
			String accessToken = getAccessToken(mpAccount);
			content = WxMessageBuilder.prepareCustomText(openid, content);
			Map<String, String> map = new HashMap<String, String>();
			map.put("content", content);

			String res = HttpUtils.post(WxApi.getSendCustomMessageUrl(accessToken), map);
			if (!"fault".equals(res)) {
				jsonObject = JSONObject.fromObject(res);
			}
			if(jsonObject.containsKey("errcode")){
				logger.error("errcode:"+jsonObject.get("errcode"));
			}
		}
		return jsonObject;
	}

	/**
	 * 发送模板消息
	 *
	 * @param tplMsg
	 * @return
	 */
	public JSONObject sendTemplateMessage(TemplateMessage tplMsg) {
		if (tplMsg != null) {
			String accessToken = getAccessToken(mpAccount);
			return WxApi.httpsRequest(WxApi.getSendTemplateMessageUrl(accessToken), HttpMethod.POST, tplMsg.toString());
		}
		return null;
	}
	//获取用户列表
	public List<AccountFans> syncAccountFansList() {
		String nextOpenId = null;
		JSONObject jsonObject =null;
		List<AccountFans> fansList;
		String url = WxApi.getFansListUrl(getAccessToken(mpAccount), nextOpenId);
		String res = HttpUtils.post(url,new HashMap<String, String>());
		if (!"fault".equals(res)) {
			jsonObject = JSONObject.fromObject(res);

			if (jsonObject.containsKey("errcode")) {
				logger.error("errcode:" + jsonObject.get("errcode"));
			}
			fansList = new ArrayList<AccountFans>();
			if (jsonObject.containsKey("data")) {
				if (jsonObject.getJSONObject("data").containsKey("openid")) {
					JSONArray openidArr = jsonObject.getJSONObject("data").getJSONArray("openid");
					for(int i = 0; i < openidArr.size() ;i++){
						Object openId = openidArr.get(i);
						AccountFans fans = syncAccountFans(openId.toString());
						fansList.add(fans);
					}
				}

			}

			return fansList;
		}
		return null;
	}

	/***
	 * 根据openId获取粉丝信息
	 * @param openId
     * @return
     */

	public  AccountFans syncAccountFans(String openId){
		String accessToken = getAccessToken(mpAccount);
		String url = WxApi.getFansInfoUrl(accessToken, openId);
		JSONObject jsonObj = WxApi.httpsRequest(url, "GET", null);
		if (null != jsonObj) {
			if(jsonObj.containsKey("errcode")){
				int errorCode = jsonObj.getInt("errcode");
				System.out.println(String.format("获取用户信息失败 errcode:{} errmsg:{}", errorCode, ErrCode.errMsg(errorCode)));
				return null;
			}else{
				AccountFans fans = new AccountFans();
				fans.setOpenId(jsonObj.getString("openid"));// 用户的标识
				fans.setSubscribeStatus(new Integer(jsonObj.getInt("subscribe")));// 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
				if(jsonObj.containsKey("subscribe_time")){
					fans.setSubscribeTime(jsonObj.getString("subscribe_time"));// 用户关注时间
				}
				if(jsonObj.containsKey("nickname")){// 昵称
					try {
						String nickname = jsonObj.getString("nickname");
						fans.setNickname(nickname.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if(jsonObj.containsKey("sex")){// 用户的性别（1是男性，2是女性，0是未知）
					fans.setGender(jsonObj.getInt("sex"));
				}
				if(jsonObj.containsKey("language")){// 用户的语言，简体中文为zh_CN
					fans.setLanguage(jsonObj.getString("language"));
				}
				if(jsonObj.containsKey("country")){// 用户所在国家
					fans.setCountry(jsonObj.getString("country"));
				}
				if(jsonObj.containsKey("province")){// 用户所在省份
					fans.setProvince(jsonObj.getString("province"));
				}
				if(jsonObj.containsKey("city")){// 用户所在城市
					fans.setCity(jsonObj.getString("city"));
				}
				if(jsonObj.containsKey("headimgurl")){// 用户头像
					fans.setHeadimgurl(jsonObj.getString("headimgurl"));
				}
				if(jsonObj.containsKey("remark")){
					fans.setRemark(jsonObj.getString("remark"));
				}
				fans.setStatus(1);
				fans.setCreatetime(new Date());
				return fans;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		WxApiClientService service = (WxApiClientService) context.getBean("wxApiClientService");
		String s =service.uploadImage("https://img1.bevol.cn/Goods/source/5628a86300ad7.jpg");
	System.out.println("id:"+s);
	}




}
