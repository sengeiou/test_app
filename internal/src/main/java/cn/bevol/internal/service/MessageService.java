package cn.bevol.internal.service;

import cn.bevol.model.entity.SystemMessage;
import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.dao.mapper.DoyenOldMapper;
import cn.bevol.internal.dao.mapper.GoodsOldMapper;
import cn.bevol.internal.dao.mapper.IndexOldMapper;
import cn.bevol.internal.entity.constant.CommenMeta;
import cn.bevol.internal.entity.user.Message;
import cn.bevol.internal.entity.user.MsgExt;
import cn.bevol.internal.entity.user.UserInfo;
import cn.bevol.internal.entity.user.UserMessage;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.Log.LogException;
import flexjson.JSONDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 消息服务
 * @author hualong
 *
 */
@Service
public class MessageService extends BaseService{
    private static Logger logger = LoggerFactory.getLogger(MessageService.class);
    /**
     * 最后一次发送消息时间
     */
    public final static String SYS_LAST_MSG_TIME="sys_last_msg_time";

    
    @Autowired
    IndexOldMapper indexOldMapper;
    
    @Autowired
    DoyenOldMapper doyenOldMapper;
    
    @Autowired
    UserService userService;
    
    @Autowired
    GoodsOldMapper goodsOldMapper;
    
    @Resource
    private EntityService entityService;

    /**
     * 增加修修酱 消息数量
     * @param userIds
     * @param num
     * @return
     */
    public ReturnData addXXJMsg(List<Long> userIds, long num) {
        mongoTemplate.findAndModify(new Query(Criteria.where("id").in(userIds)), new Update().inc("newMsgNum", num).inc("msgNum", num).inc("newXxjMsgNum", num).inc("xxjMsgNum", num), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
        return ReturnData.SUCCESS;
    }


    


    /**
     * 消息推送sendtype=1 
    * 即时发送同步消息发送  系统消息类
    *
    * @param senderId    消息发送者
    * @param receiverIds  接受者
    * @param description  详细
    * @param type         消息类型
    * @param title			消息标题
    * @param content: 消息内容
    * @param params: 跳转的参数
    * @param newType: 要跳转的类型(发现,心得,产品等) 同index接口的type
    */
   public ReturnData sendSynMessage(long senderId, List<Long> receiverIds, int type, int description, String title,
                                    String content, String redirect_type,
                                    //跳转的页面
                                    String page,
                                    String params, Integer newType) {
      int boundary=2; //发送给部分人
      int sendType=1;//推的方式
        //记录消息
       String tname = "message";
       Message msg = new Message();
       msg.setId(this.getId(tname));
       msg.setSenderId(senderId);
       msg.setReceiverIds(receiverIds);
       msg.setBoundary(boundary);
       msg.setType(type);
       msg.setDescription(description);
       msg.setTitle(title);
       msg.setReceiverIds(receiverIds);
       msg.setContent(content);
       msg.setId(this.getId(tname));
       msg.setSendType(sendType);
       msg.setPublishStamp(DateUtils.nowInMillis()/1000);
       msg.setRedirectType(redirect_type);
       msg.setRedirectPage(page);
       HashMap<String,Object> param=null;
       if(!StringUtils.isBlank(params)) {
     	  param  = new JSONDeserializer<HashMap<String,Object>>().deserialize(params);  
       }
       msg.setRedirectParams(param);
       
       //新的参数
       msg.setNewType(newType);
       mongoTemplate.save(msg, tname);

       //发送指定的人
       List<UserMessage> umlist = new ArrayList<UserMessage>();
       for (int i = 0; i < receiverIds.size(); i++) {
           UserMessage um = new UserMessage();
           um.setReceiverId(receiverIds.get(i));
           um.setSenderId(senderId);
           um.setType(msg.getType());
           um.setBoundary(msg.getBoundary());
           um.setDescription(msg.getDescription());
           um.setTitle(msg.getTitle());
           um.setContent(msg.getContent());
           um.setRedirectType(redirect_type);
           um.setPublishStamp(msg.getPublishStamp());
           um.setRedirectPage(page);
           um.setRedirectParams(param);
           msg.setNewType(newType);
           long id = this.getId("user_message");
           um.setId(id);
           um.setMessageId(msg.get_id());
           um.setMessageIdInt(msg.getId());
           umlist.add(um);
       }
       //批量插入
       mongoTemplate.insertAll(umlist);
       
       return ReturnData.SUCCESS;
   }
   
   
   
    /**
    * 消息拉 发送消息  sendtype=0 拉
    *
    * @param senderId    消息发送者
    * @param receiverIds  接受者
    * @param boundary 范围 1为当前所有用户  2为用户初始化消息
    * @param description  详细
    * @param type         消息类型
    * @param title
    * @param content
    */
   public boolean sendMessage(long senderId, String receiverIds, int boundary, int type, int description, String title,
                              String content, String redirectType, String redirectPage, String redirectParams) {
       // TODO Auto-generated method stub
	   /*senderId=351060;
	   receiverIds="351061";
	   boundary=1;*/
       List<Long> rids = new ArrayList<Long>();
       if (receiverIds != null && !receiverIds.equals("")) {
           String[] strids = receiverIds.split(",");
           for (int i = 0; i < strids.length; i++) {
               long curid = Long.parseLong(strids[i]);
               if (senderId != curid)
                   rids.add(curid);
           }
       }
       HashMap<String,Object> param=null;
       if(!StringUtils.isBlank(redirectParams)) {
     	  param  = new JSONDeserializer<HashMap<String,Object>>().deserialize(redirectParams);
       }
       int sendType=0;
       if (boundary == 2) {
           String tname = "message";
           Message msg = new Message();
           msg.setId(this.getId(tname));
           msg.setSenderId(senderId);
           msg.setBoundary(boundary);
           msg.setType(type);
           msg.setDescription(description);
           msg.setTitle(title);
           msg.setReceiverIds(rids);
           msg.setContent(content);
           msg.setSendType(sendType);
           msg.setRedirectType(redirectType);
           msg.setRedirectPage(redirectPage);
           msg.setRedirectParams(param);
           msg.setPublishStamp(DateUtils.nowInMillis()/1000);
           msg.setId(this.getId(tname));
           mongoTemplate.save(msg, tname);
       } else if (boundary == 1) {
           String tname = "system_message";
           SystemMessage sm = new SystemMessage();
           sm.setContent(content);
           sm.setTitle(title);
           sm.setSenderId(senderId);
           sm.setReceiverIds(rids);
           sm.setSendType(sendType);
           /**
            * 所有人
            */
           sm.setBoundary(boundary);
           sm.setDescription(description);
           sm.setType(type);
           sm.setRedirectType(redirectType);
           sm.setRedirectPage(redirectPage);
           sm.setRedirectParams(param);
           sm.setPublishStatus(1);
           sm.setPublishStamp(DateUtils.nowInMillis()/1000);
           sm.setId(this.getId(tname));
           mongoTemplate.save(sm, tname);

           //系统消息发送成功之后记录最后一次发送的时间
           ConfUtils.setResourceNum(SYS_LAST_MSG_TIME, Integer.parseInt(sm.getCreateStamp()+""));
       }
       return true;
   }


	/**
	 * 发送修修酱消息
	 * @param userids:	消息接受者(用户)的id,逗号分隔
	 * @param content:	消息内容
	 * @param title:	消息标题
	 * @param redirect_type: 跳转方式1 app页面内
	 * @param page: 跳转对应的页面
	 * @param params: 跳转页面对应的参数
	 * @param newType: 新的页面跳转参数
	 * @return
	 */
	public ReturnData sendMsgByXxj(long replyUserId, String userids, String title, String content, String redirect_type,
                                   //跳转的页面
                                   String page,
                                   String params, Integer newType) {
		try {
		if(StringUtils.isBlank(userids)) return  ReturnData.ERROR;
		String uids[]=userids.split(",");
		List<Long> receiverIds =new ArrayList<Long>();
		for(int i=0;i<uids.length;i++) {
			receiverIds.add(Long.parseLong(uids[i]));
		}
			if(receiverIds.size()>0) {
				//获取类型
				CommenMeta.MessageStatus msgDesc= CommenMeta.MessageStatus.getStatusByKey("msg-xxj-mange");
				//发送消息
			       ReturnData r=  this.sendSynMessage(replyUserId, receiverIds, msgDesc.getType(), msgDesc.getDescription(), title, content,redirect_type,page,params,newType);
			         //评论更新用户消息
			       addXXJMsg(receiverIds,1);
				return  ReturnData.SUCCESS;
			} 
       } catch (Exception e) {
    	   Map map = new HashMap();
			map.put("method", "GoodsService.sendMsg");
			map.put("userids", userids);
			map.put("content", content);
			map.put("redirect_type", redirect_type);
			map.put("page", page);
			map.put("newType", newType);
			new LogException(e, map);
       }

		return  ReturnData.ERROR;
	}

	
    /**
     * 推送扩展消息 一对
    * 即时发送同步消息发送  系统消息类
    *
    * @param senderId    消息发送者
    * @param receiverId  接受者
    * @param msgCode      消息发送类型码
    * @param msgField     用户消息数累加字段
    * @param msgExt: 消息的扩展 根据类型发送相应的消息 详情参考:CommenMeta
    */
   public ReturnData sendEntitySynMessage(long senderId, long  receiverId, String msgCode, String msgField, MsgExt msgExt) {
      int boundary=2; //发送给部分人
      int sendType=1;//推的方式
        //记录消息
       String tname = "message";
       Message msg = new Message();
       msg.setId(this.getId(tname));
       msg.setSenderId(senderId);
       List<Long> rids=new ArrayList<Long>();
       rids.add(receiverId);
       msg.setReceiverIds(rids);
       msg.setId(this.getId(tname));
       msg.setSendType(sendType);
       msg.setBoundary(boundary);
       msg.setSendType(sendType);
       msg.setPublishStamp(DateUtils.nowInMillis()/1000);
       CommenMeta.MessageStatus msgDesc= CommenMeta.MessageStatus.getStatusByKey(msgCode);
       msg.setType(msgDesc.getType());
       msg.setDescription(msgDesc.getDescription());
       msg.setMsgExt(msgExt);
       mongoTemplate.save(msg, tname);
       //发送指定的人
       List<UserMessage> umlist = new ArrayList<UserMessage>();
       for (int i = 0; i < rids.size(); i++) {
           UserMessage um = new UserMessage();
           um.setReceiverId(rids.get(i));
           um.setSenderId(senderId);
           um.setType(msg.getType());
           um.setBoundary(msg.getBoundary());
           um.setDescription(msg.getDescription());
           um.setMsgExt(msg.getMsgExt());
           um.setPublishStamp(msg.getPublishStamp());
           long id = this.getId("user_message");
           um.setId(id);
           um.setMessageId(msg.get_id());
           um.setMessageIdInt(msg.getId());
           umlist.add(um);
       }
       //批量插入
       mongoTemplate.insertAll(umlist);
       //更新用户评论消息数量 
       int num=1;
       mongoTemplate.findAndModify(new Query(Criteria.where("id").in(rids)), new Update().inc("newMsgNum", num).inc("msgNum", num).inc("new"+ StringUtils.capitalize(msgField)+"MsgNum", num).inc(msgField+"MsgNum", num), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
       return ReturnData.SUCCESS;
   }



   /**
    * 最新活动,系统消息
    * @param msgType new_active  最新活动    sys 系统通知
	 * @param content:	消息内容
	 * @param title:	消息标题
	 * @param redirect_type: 跳转方式1 app页面内
	 * @param page: 跳转对应的页面
	 * @param params: 跳转页面对应的参数
    * @return
    */
   public ReturnData sendSysmMessage(String msgType, String title,
                                     String content, String redirect_type, String page, String params) {
	      int boundary=1;
       CommenMeta.MessageStatus msgDesc= CommenMeta.MessageStatus.getStatusByKey("msg-sys-"+msgType);
       int type=msgDesc.getType();
       int description=msgDesc.getDescription();

      if(sendMessage(0,null,boundary,type,description,title,content,redirect_type,page,params)){
    	  return ReturnData.SUCCESS;
      }
      return ReturnData.ERROR;
   }



}
