package cn.bevol.statics.controller;

import cn.bevol.statics.service.weixin.WeiXinService;
import cn.bevol.statics.service.weixin.WxApiClientService;
import cn.bevol.statics.service.weixin.WxCscheService;
import cn.bevol.statics.service.weixin.util.MsgXmlUtil;
import cn.bevol.statics.service.weixin.util.SignUtil;
import cn.bevol.statics.service.weixin.vo.MsgRequest;
import cn.bevol.statics.service.weixin.vo.TemplateMessage;
import cn.bevol.util.DateUtils;
import cn.bevol.util.response.ReturnData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 微信与开发者服务器交互接口
 */
@Controller
@RequestMapping("/wxapi")
public class WxApiController {
	
	@Autowired
	private WeiXinService weiXinService;
	@Autowired
	private WxApiClientService wxApiClientService;
	@Autowired
	private WxCscheService wxCscheService;
	/**
	 * GET请求：进行URL、Tocken 认证；
	 * 1. 将token、timestamp、nonce三个参数进行字典序排序
	 * 2. 将三个参数字符串拼接成一个字符串进行sha1加密
	 * 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
	 */
	@RequestMapping(value = "/{account}/message",  method = RequestMethod.GET)
	public @ResponseBody
    String doGet(HttpServletRequest request, @PathVariable String account) {

			String token ="bevol";//获取token，进行验证；

		String signature = request.getParameter("signature");// 微信加密签名
			String timestamp = request.getParameter("timestamp");// 时间戳
			String nonce = request.getParameter("nonce");// 随机数
			String echostr = request.getParameter("echostr");// 随机字符串
			
			// 校验成功返回  echostr，成功成为开发者；否则返回error，接入失败
			if (SignUtil.validSign(signature, token, timestamp, nonce)) {
				return echostr;
			}
		//}
		return "error";
	}
	
	/**
	 * POST 请求：进行消息处理；
	 * */
	@RequestMapping(value = "/{account}/message", method = RequestMethod.POST)
	public @ResponseBody
    String doPost(HttpServletRequest request, @PathVariable String account, HttpServletResponse response) {
		//处理用户和微信公众账号交互消息

		try {
			MsgRequest msgRequest = MsgXmlUtil.parseXml(request);//获取发送的消息
			return weiXinService.processMsg(msgRequest);
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}
	




    /***
     * 发送客服消息
	 * @param request
	 * @param response
	 * @param openid
     */
	@RequestMapping(value = "/sendCustomTextMsg", method = RequestMethod.POST)
	public void sendCustomTextMsg(HttpServletRequest request,HttpServletResponse response,
								  @RequestParam() String openid){
		String content = "测试客服消息";
		JSONObject result = wxApiClientService.sendCustomTextMessage(openid, content);
		try {
			if(result.getInt("errcode") != 0){
				response.getWriter().write("send failure");
			}else{
				response.getWriter().write("send success");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送模板消息
	 * @param request
	 * @param response
	 * @param openid
     */
	@RequestMapping(value = "/sendTemplateMessage", method = RequestMethod.POST)
	public void sendTemplateMessage(HttpServletRequest request,HttpServletResponse response,String openid){
		TemplateMessage tplMsg = new TemplateMessage();
		
		tplMsg.setOpenid(openid);
		//微信公众号号的template id，开发者自行处理参数
		tplMsg.setTemplateId("Wyme6_kKUqv4iq7P4d2NVldw3YxZIql4sL2q8CUES_Y"); 
		
		tplMsg.setUrl("http://www.weixinpy.com");
		Map<String, String> dataMap = new HashMap<String,String>();
		dataMap.put("first", "微信派官方微信模板消息测试");
		dataMap.put("keyword1", "时间：" + DateUtils.COMMON.getDateText(new Date()));
		dataMap.put("keyword2", "关键字二：你好");
		dataMap.put("remark", "备注：感谢您的来访");
		tplMsg.setDataMap(dataMap);
		
		JSONObject result = wxApiClientService.sendTemplateMessage(tplMsg);
		try {
			if(result.getInt("errcode") != 0){
				response.getWriter().write("send failure");
			}else{
				response.getWriter().write("send success");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//删除微信公众账号菜单
	@RequestMapping(value = "/deleteMenu")
	public Object deleteMenu(HttpServletRequest request) {
		JSONObject rstObj = null;
		return wxApiClientService.deleteMenu();
	}
	//获取用户列表
	@RequestMapping(value = "/syncAccountFansList")
	public Object syncAccountFansList(){

		return wxApiClientService.syncAccountFansList();
	}

	@RequestMapping(value = "/wx/config")
	@ResponseBody
	public Map<String, Object> getJsSdkConfig(String url){
		return wxApiClientService.getJsSdkConfig(url);
	}

	@RequestMapping(value = {"/wx/imgupload"})
	@ResponseBody
	public Object upload(HttpSession session, HttpServletRequest request,
                         @RequestParam String picUrl){
		return   new ReturnData(wxApiClientService.uploadImage(picUrl));
	}
}




