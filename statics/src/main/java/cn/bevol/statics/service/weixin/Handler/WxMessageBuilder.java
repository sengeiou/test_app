package cn.bevol.statics.service.weixin.Handler;


import cn.bevol.statics.entity.items.MsgType;
import cn.bevol.statics.service.weixin.msg.MsgNews;
import cn.bevol.statics.service.weixin.msg.MsgText;
import cn.bevol.statics.service.weixin.vo.*;
import cn.bevol.util.DateUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 消息builder工具类
 */
public class WxMessageBuilder {
	
	//客服文本消息
	public static String prepareCustomText(String openid, String content){
		JSONObject jsObj = new JSONObject();
		jsObj.put("touser", openid);
		jsObj.put("msgtype", MsgType.Text.name());
		JSONObject textObj = new JSONObject();
		textObj.put("content", content);
		jsObj.put("text", textObj);
		return jsObj.toString();
	}
	
	//获取 MsgResponseText 对象
	public static MsgResponseText getMsgResponseText(MsgRequest msgRequest, MsgText msgText){
		if(msgText != null){
			MsgResponseText reponseText = new MsgResponseText();
			reponseText.setToUserName(msgRequest.getFromUserName());
			reponseText.setFromUserName(msgRequest.getToUserName());
			reponseText.setMsgType(MsgType.Text.toString());
			reponseText.setCreateTime(DateUtils.nowInMillis());
			reponseText.setContent(msgText.getContent());
			reponseText.setFuncFlag("0");
			return reponseText;
		}else{
			return null;
		}
	}
	//获取 MsgResponseImage 对象
	public static MsgResponseImage getMsgResponseImage(MsgRequest msgRequest, Image image){
		if(image != null){
			MsgResponseImage reponseImage = new MsgResponseImage();
			reponseImage.setToUserName(msgRequest.getFromUserName());
			reponseImage.setFromUserName(msgRequest.getToUserName());
			reponseImage.setMsgType(MsgType.Image.toString());
			reponseImage.setCreateTime(DateUtils.nowInMillis());
			reponseImage.setImage(image);
			return reponseImage;
		}else{
			return null;
		}
	}
	
	//获取 MsgResponseNews 对象
	public static MsgResponseNews getMsgResponseNews(MsgRequest msgRequest, List<MsgNews> msgNews){
		if(msgNews != null && msgNews.size() > 0){
			MsgResponseNews responseNews = new MsgResponseNews();
			responseNews.setToUserName(msgRequest.getFromUserName());
			responseNews.setFromUserName(msgRequest.getToUserName());
			responseNews.setMsgType(MsgType.News.toString());
			responseNews.setCreateTime(DateUtils.nowInMillis());
			responseNews.setArticleCount(msgNews.size());
			List<Article> articles = new ArrayList<Article>(msgNews.size());
			for(MsgNews n : msgNews){
				Article a = new Article();
				a.setTitle(n.getTitle());
				a.setPicUrl(n.getPicpath());
				if(StringUtils.isEmpty(n.getFromurl())){
					a.setUrl(n.getUrl());
				}else{
					a.setUrl(n.getFromurl());
				}
				a.setDescription(n.getBrief());
				articles.add(a);
			}
			responseNews.setArticles(articles);
			return responseNews;
		}else{
			return null;
		}
	}

	/**
	 * 获取 MsgResponse 对象(多客服)
	 */
	public static MsgResponse getMsgResponse(MsgRequest msgRequest){
		MsgResponse msgResponse = new MsgResponse();
		msgResponse.setToUserName(msgRequest.getFromUserName());
		msgResponse.setFromUserName(msgRequest.getToUserName());
		msgResponse.setCreateTime(DateUtils.nowInMillis());
		msgResponse.setMsgType(MsgType.TRANSFER_CUSTOMER_SERVICE.toString());
		return  msgResponse;
	}
}
