package cn.bevol.entity.service.weixin;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.entity.service.weixin.Handler.WxMessageBuilder;
import cn.bevol.entity.service.weixin.msg.MsgBase;

import cn.bevol.entity.service.weixin.util.MsgXmlUtil;
import cn.bevol.entity.service.weixin.vo.Image;
import cn.bevol.entity.service.weixin.vo.MsgRequest;
import cn.bevol.entity.service.weixin.vo.MsgResponseImage;
import cn.bevol.model.items.MpAccount;
import cn.bevol.model.items.MsgType;
import cn.bevol.entity.service.weixin.msg.MsgNews;
import cn.bevol.entity.service.weixin.msg.MsgText;
import cn.bevol.mybatis.dao.ConfigMapper;
import cn.bevol.mybatis.dao.QrcodeOldMapper;
import cn.bevol.mybatis.dao.WxArtileMapper;
import cn.bevol.mybatis.model.Config;
import cn.bevol.mybatis.model.WxArtile;
import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;
import com.io97.utils.PropertyUtils;
import com.io97.utils.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rc. on 2017/2/22.
 */
@Service
public class WeiXinService {

    @Resource
    private QrcodeOldMapper qrcodeOldMapper;
    @Resource
    private ConfigMapper configMapper;
    @Resource
    private RedisCacheProvider cacheProvider;
    @Resource
    private  WxApiClientService wxApiClientService;
    @Resource
    private WxArtileMapper wxArtileMapper;



    MpAccount  mpAccount;
    public WeiXinService(){
          mpAccount = new MpAccount();
          mpAccount.setUrl( PropertyUtils.getStringValue("bevol.wx.requiredUrl"));
          mpAccount.setAccount(PropertyUtils.getStringValue("bevol.wx.account"));
          mpAccount.setAppid(PropertyUtils.getStringValue("bevol.wx.appid"));
          mpAccount.setToken(PropertyUtils.getStringValue("bevol.wx.token"));
          mpAccount.setAppsecret(PropertyUtils.getStringValue("bevol.wx.appsecret"));
    }

    /***
     * ????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param msgRequest
     * @return
     */
    public String processMsg(MsgRequest msgRequest) {


        String msgtype = msgRequest.getMsgType();//????????????????????????
        String respXml = null;//??????????????????
        if (msgtype.equals(MsgType.Text.toString())) {
            /**
             * ???????????????????????????????????????????????????????????????
             */
            respXml = this.processTextMsg(msgRequest);
        } else if (msgtype.equals(MsgType.Event.toString())) {//????????????
            /**
             * ??????????????????????????????????????????????????????????????????????????????
             */
            respXml = this.processEventMsg(msgRequest);

            //??????????????????????????????????????????
        } else if (msgtype.equals(MsgType.Image.toString())) {//????????????
            Image img = new Image();
            img.setMediaId("B2jwYfDn2JfxxB09dtab_JvL_jm8D54mQrN5H95UQBWI8QXWZiH73IFbpzraJOAg");
            respXml = MsgXmlUtil.imageToXml(WxMessageBuilder.getMsgResponseImage(msgRequest,img));
        } else if (msgtype.equals(MsgType.Location.toString())) {//??????????????????

        }
        //?????????????????????????????????????????????????????????
        if (StringUtils.isEmpty(respXml)) {
            customer(msgRequest);
        }
        return respXml;
    }

    //??????????????????
    private String processTextMsg2(MsgRequest msgRequest) {
        String content = msgRequest.getContent();
        if (!StringUtils.isEmpty(content)) {//????????????
            String tmpContent = content.trim();
            // List<MsgNews> msgNews = msgNewsDao.getRandomMsgByContent(tmpContent, mpAccount.getMsgcount());
            String url = "http://source.bevol.cn/tb/wxkey/" + tmpContent + ".json";
            String newsJson = HttpUtils.get(url);
            List<MsgNews> msgNews = new ArrayList<MsgNews>();
            if (newsJson != "fault") {
                if (!"response code : 404".equals(newsJson)) {
                    JSONArray array = JSONArray.fromObject(newsJson);
                    for (Object obj : array) {
                        JSONObject newsObj = JSONObject.fromObject(obj);

                        MsgNews news = new MsgNews();
                        //news.setId((Long) newsObj.get("id"));
                        news.setTitle((String) newsObj.get("title"));
                        news.setDescription((String) newsObj.get("description"));
                        news.setUrl((String) newsObj.get("url"));
                        news.setPicpath((String) newsObj.get("picurl"));
                        msgNews.add(news);
                    }

                    if (!CollectionUtils.isEmpty(msgNews)) {
                        JSONObject object = array.getJSONObject(0);
                        if(object.getInt("type")==0) {

                           return MsgXmlUtil.newsToXml(WxMessageBuilder.getMsgResponseNews(msgRequest, msgNews));

//                            String mediaId =  wxApiClientService.uploadImage("https://img0.bevol.cn/Goods/source/cut_54223b82-57a7-416e-85f4-13d2980c1f87.jpg");
//                            Image img = new Image();
////                           // oLsGo-s0UpBFV-QOkzvV6UVe14g7xCaXUQ6QNrBVfBntxjvwWQS8Ghmt2cWzvpYa
////                            //q8gqDibBoTYP-9VQGR_0La-uzcCUW8PIy22ICHNeP5qX3nAgGf6Gvw4P_9H3qPIw
//                            img.setMediaId("kiJBQDpMsnXlnnYPFbcSaZ-fdoPioPEn6jpo1ethux53Xgq0k6ZXcSAlccSbOtKK");
//                            return MsgXmlUtil.imageToXml( WxMessageBuilder.getMsgResponseImage(msgRequest,img));
                        }else if(object.getInt("type")==1) {
                            MsgText msgText = new MsgText();
                            msgText.setContent(object.getString("description"));
                            return MsgXmlUtil.textToXml(WxMessageBuilder.getMsgResponseText(msgRequest, msgText));
                        }else if(object.getInt("type")==2){
                         //   String  picUrl = object.getString("picurl");
                          //  String mediaId =  wxApiClientService.uploadImage(picUrl);
                            Image img = new Image();
                            img.setMediaId("kiJBQDpMsnXlnnYPFbcSaZ-fdoPioPEn6jpo1ethux53Xgq0k6ZXcSAlccSbOtKK");
                            WxMessageBuilder.getMsgResponseImage(msgRequest,img);
                            return MsgXmlUtil.imageToXml(new MsgResponseImage());
                        }
                        //return MsgXmlUtil.newsToXml(WxMessageBuilder.getMsgResponseNews(msgRequest, msgNews));
                    }
                } else {//????????????
                    customer(msgRequest);
                }
            }
        }
        return null;
    }
    //??????????????????
    private String processTextMsg(MsgRequest msgRequest) {
        String content = msgRequest.getContent();
        if (!StringUtils.isEmpty(content)) {//????????????
            String tmpContent = content.trim();
            List<MsgNews> msgNews = new ArrayList<MsgNews>();
            MsgText msgText;
            Image img;
            List<WxArtile> ls=  this.findByContent(tmpContent);
            //List<WxArtile> ls =wxArtileMapper.findByContent(content);
                    for (WxArtile artile : ls) {
                        if(artile.getType()==0){
                            MsgNews news = new MsgNews();
                            //news.setId((Long) newsObj.get("id"));
                            news.setTitle(artile.getTitle());
                            news.setDescription(artile.getDescription());
                            news.setUrl(artile.getUrl());
                            news.setPicpath(artile.getPicUrl());
                            msgNews.add(news);
                        }
                        if(msgNews.size()>0){
                            return   MsgXmlUtil.newsToXml(WxMessageBuilder.getMsgResponseNews(msgRequest, msgNews));
                        }
                        if(artile.getType()==1){
                            msgText = new MsgText();
                            msgText.setContent(artile.getDescription());
                            return MsgXmlUtil.textToXml(WxMessageBuilder.getMsgResponseText(msgRequest, msgText));
                        }
                        if(artile.getType()==2){
                            img = new Image();
                            //kiJBQDpMsnXlnnYPFbcSaZ-fdoPioPEn6jpo1ethux53Xgq0k6ZXcSAlccSbOtKK
                            img.setMediaId(artile.getMediaId());
                            return MsgXmlUtil.imageToXml(WxMessageBuilder.getMsgResponseImage(msgRequest,img));
                        }

                    }
            if(ls==null||ls.size()==0){
                customer(msgRequest);
            }
        }
        return null;
    }

    public List<WxArtile> findByContent(final String content){
        return new CacheableTemplate<List<WxArtile>>(cacheProvider) {
            @Override
            protected List<WxArtile> getFromRepository() {
                return wxArtileMapper.findByContent(content);
            }
            @Override
            protected boolean canPutToCache(List<WxArtile> returnValue) {
                return (returnValue != null && returnValue.size()>= 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE, CACHE_NAME.createInstanceKey(CACHE_NAME.WX_KEYWORD_ARTILE,content)), true);
    }


    /**
     * ???????????????
     */
    public String customer(MsgRequest msgRequest){
        Config conf = configMapper.selectByKey("wxdefreplyopen");
        if ("1".equals(conf.getValue())) {
            MsgText text = new MsgText();
            text.setContent(configMapper.selectByKey("wxdefreply").getValue());
            if (text != null) {
            return MsgXmlUtil.textToXml(WxMessageBuilder.getMsgResponseText(msgRequest, text));
            }
        } else {
        msgRequest.setMsgType(MsgType.TRANSFER_CUSTOMER_SERVICE.toString());
        return MsgXmlUtil.customerToXml(WxMessageBuilder.getMsgResponse(msgRequest));
        }
    return null;
        }



    //??????????????????
    private String processEventMsg(MsgRequest msgRequest) {
        String key = msgRequest.getEventKey();
        if (MsgType.SUBSCRIBE.toString().equals(msgRequest.getEvent())) {//????????????
            //MsgText text = msgBaseDao.getMsgTextBySubscribe();
            if (!StringUtils.isEmpty(key)) {
                key = key.split("_")[1];
                qrcodeOldMapper.updateTotal(Integer.parseInt(key));
            }
            Config conf = configMapper.selectByKey("wxwelcome");
            MsgText text = new MsgText();
            text.setContent(conf.getValue());
            if (text != null) {
                return MsgXmlUtil.textToXml(WxMessageBuilder.getMsgResponseText(msgRequest, text));
            }
        } else if (MsgType.UNSUBSCRIBE.toString().equals(msgRequest.getEvent())) {//??????????????????
            // MsgText text = msgBaseDao.getMsgTextByInputCode(MsgType.UNSUBSCRIBE.toString());
            MsgText text = new MsgText();
            if (text != null) {
                return MsgXmlUtil.textToXml(WxMessageBuilder.getMsgResponseText(msgRequest, text));
            }
        } else {//??????????????????
            if (!StringUtils.isEmpty(key)) {
                /**
                 * ????????????
                 * _fix_ ????????????????????????????????????????????????????????????event_key ?????? _fix_
                 *
                 * ????????????????????????????????????
                 */
                if (key.startsWith("_fix_")) {
                    String baseIds = key.substring("_fix_".length());
                    if (!StringUtils.isEmpty(baseIds)) {
                        String[] idArr = baseIds.split(",");
                        if (idArr.length > 1) {//??????????????????
                            //  List<MsgNews> msgNews = msgBaseDao.listMsgNewsByBaseId(idArr);
                            List<MsgNews> msgNews = null;
                            if (msgNews != null && msgNews.size() > 0) {
                                return MsgXmlUtil.newsToXml(WxMessageBuilder.getMsgResponseNews(msgRequest, msgNews));
                            }
                        } else {//?????????????????????????????????
                            // MsgBase msg = msgBaseDao.getById(baseIds);
                            MsgBase msg = null;
                            if (msg.getMsgtype().equals(MsgType.Text.toString())) {
                                //  MsgText text = msgBaseDao.getMsgTextByBaseId(baseIds);
                                MsgText text = null;
                                if (text != null) {
                                    return MsgXmlUtil.textToXml(WxMessageBuilder.getMsgResponseText(msgRequest, text));
                                }
                            } else {
                                //   List<MsgNews> msgNews = msgBaseDao.listMsgNewsByBaseId(idArr);
                                List<MsgNews> msgNews = null;
                                if (msgNews != null && msgNews.size() > 0) {
                                    return MsgXmlUtil.newsToXml(WxMessageBuilder.getMsgResponseNews(msgRequest, msgNews));
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }




}