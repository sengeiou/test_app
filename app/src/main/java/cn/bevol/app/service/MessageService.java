package cn.bevol.app.service;

import cn.bevol.app.dao.mapper.DoyenOldMapper;
import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.app.dao.mapper.IndexOldMapper;
import cn.bevol.app.entity.constant.CommenMeta;
import cn.bevol.model.BaseMessage;
import cn.bevol.model.SystemMessage;
import cn.bevol.model.user.Message;
import cn.bevol.model.user.MsgExt;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.user.UserMessage;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import com.mongodb.WriteResult;
import flexjson.JSONDeserializer;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
    IndexOldMapper indexMapper;
    
    @Autowired
    DoyenOldMapper doyenMapper;
    
    @Autowired
    UserService userService;
    
    @Autowired
    GoodsOldMapper goodsMapper;
    
    @Resource
    private EntityService entityService;
    
    /**
     * 获取我的消息
     * v2.9之前
     * @param startId
     * @param limit
     * @return
     */
    @Deprecated
    public ReturnListData getMymsg(UserInfo userInfo, long startId, int limit) {
    	long userId=0;
        try {
        	 userId=userInfo.getId();
            String tname = "user_message";
            Criteria crit = Criteria.where("receiverId").is(userId).and("hidden").is(0);
            if (startId > 0) {
                crit.and("id").lt(startId);
            }
            List<Integer> ls1=new ArrayList<Integer>();
            ls1.add(1);
            ls1.add(2);
            ls1.add(8);

                crit.orOperator(new Criteria().and("type").is(1).and("description").in(ls1),new Criteria().and("type").is(3));
            List<UserMessage> rows = mongoTemplate.find(new Query(crit).with(new Sort(Direction.DESC, "id")).limit(limit), UserMessage.class, tname);
            long total = mongoTemplate.count(new Query(crit), tname);
        	this.clearAllMsg(userInfo);
            return new ReturnListData(rows,total);
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "MessageService.getMymsg");
			map.put("startId", startId);
			map.put("limit", limit);
			map.put("userId", userId);
			new LogException(e, map);
        }
        return null;
    }

    /**
     * 获取我的消息
     * 根据type获取相应的消息列表
     * @param type  1 系统消息
					2、评论消息
					3、修修酱消息
					4、表示点赞
     * @param startId
     * @param limit
     * @return
     */
    public ReturnListData getMymsg(UserInfo userInfo, int type, long startId, int limit) {
    	long userId=0;
        try {
        	 userId=userInfo.getId();
            String tname = "user_message";
            Criteria crit = Criteria.where("receiverId").is(userId).and("hidden").is(0);
            if (startId > 0) {
                crit.and("id").lt(startId);
            }
            if (type > 0) {
                crit.and("type").is(type);
            }
            List<UserMessage> rows = mongoTemplate.find(new Query(crit).with(new Sort(Direction.DESC, "id")).limit(limit), UserMessage.class, tname);
            long total = mongoTemplate.count(new Query(crit), tname);
            //更新消息
            if(type==1) {
            	//清空 系统消息
            	//this.clearSysMsg(userInfo);
            	this.clearMsg("sys",userInfo);
            } else if(type==2) {
            	//清空 评论消息
            	//this.clearCommentMsg(userInfo);
            	this.clearMsg("comment",userInfo);
            } else if(type==3) {
            	//清空修修酱评论
            	//this.clearXxjMsg(userInfo);
            	this.clearMsg("xxj",userInfo);

            } else if(type==4) {
            	//评论点赞
            	this.clearMsg("commentLike",userInfo);
            }
            return new ReturnListData(rows,total);
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "MessageService.getMymsg");
			map.put("startId", startId);
			map.put("limit", limit);
			map.put("type", type);
			map.put("userId", userId);
			new LogException(e, map);
        }
        return null;
    }
    
    
    /**
     * v3.0
     * 消息主页
     *
     * @param startId
     * @param limit
     * @return
     */
    public ReturnData messageIndex(UserInfo userInfo, long startId, int limit) {
    	long userId=0;
        try {
        	
        	userId=userInfo.getId();
            String tname = "user_message";
            Criteria crit = Criteria.where("receiverId").is(userId).and("hidden").is(0);
            if (startId > 0) {
                crit.and("id").lt(startId);
            }
            //回复和评论点赞  
            crit.orOperator(Criteria.where("type").is(2), Criteria.where("type").is(4));
            //crit.and("type").is(2).and("type").is(4);
            List<UserMessage> rows = mongoTemplate.find(new Query(crit).with(new Sort(Direction.DESC, "id")).limit(limit), UserMessage.class, tname);
            long total = mongoTemplate.count(new Query(crit), tname);
            
            //清空点赞
        	this.clearMsg("commentLike",userInfo);
        	//清空评论回复
        	this.clearMsg("comment",userInfo);
            
            //系统消息
           UserMessage sysMsg = mongoTemplate.findOne(new Query(Criteria.where("receiverId").is(userId).and("hidden").is(0).and("type").is(1)).with(new Sort(Direction.DESC, "id")), UserMessage.class, tname);

            //修修酱评论消息
           UserMessage xxjMsg = mongoTemplate.findOne(new Query(Criteria.where("receiverId").is(userId).and("hidden").is(0).and("type").is(3)).with(new Sort(Direction.DESC, "id")), UserMessage.class, tname);
           
            Map map=new HashMap();
            map.put("userMsg", rows);
            map.put("userMsgTotal", total);
            map.put("sysMsg", sysMsg);
            map.put("xxjMsg", xxjMsg);
            
            //消息的banner和文案
            if(startId==0){
                List<Map> messageBanner=entityService.getConfigList("message_banner");
                this.compareGoodsMids(messageBanner);
                map.put("messageBanner", messageBanner);
            }
            
            return new ReturnData(map);
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "MessageService.messageIndex");
			map.put("startId", startId);
			map.put("limit", limit);
			map.put("userId", userId);
			new LogException(e, map);
            logger.error("method:getMymsg arg:{userId:" + userId  + ",userId:" + userId + ",startId:" + startId + ",limit:" + limit + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 清空类型对应消息
     * @param feild
     * @param userInfo
     * @return
     * @throws Exception
     */
    private ReturnData clearMsg(String feild,UserInfo userInfo) throws Exception {
    	String fd="new"+StringUtils.capitalize(feild)+"MsgNum";
    	String newNum= BeanUtils.getProperty(userInfo, fd);
    	if(StringUtils.isBlank(newNum)) {
    		return ReturnData.ERROR;
    	}
    	Integer nNum=Integer.parseInt(newNum);
    	Integer newMsgNum=userInfo.getNewMsgNum()-nNum;
    	Update update=new Update();
    	if(newMsgNum<0) newMsgNum=0;
    	update.set("newMsgNum", newMsgNum).set(fd, 0);
      	mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), update, "user_info");
        return ReturnData.SUCCESS;
	}

	/**
     * 删除用户消息
     * @param id  //删除用户消息
     * @return
     */
    public ReturnData deleteMessageUser(long id) {
    	WriteResult wr=mongoTemplate.remove(new Query(Criteria.where("id").is(id)), "user_message");
    	return ReturnData.SUCCESS;
    }
    
    /**
     * 隐藏用户消息,批量
     * @param ids:  用户id,逗号分隔
     * @return
     */
    public ReturnData hiddenUserMessage(String ids) {
    	String tname="user_message";
    	List<Long> ls=new ArrayList();
    	if(StringUtils.isBlank(ids)) return ReturnData.ERROR;
    	String iids[]=ids.split(",");
    	for(int i=0;i<iids.length;i++) {
    		ls.add(Long.parseLong(iids[i]));
    	}
    	//隐藏
    	return this.hidden(tname, ls);
    }

    /**
     * 删除消息列表信息
     */
    public ReturnData deleteMessage(long id) {
    	WriteResult wr=mongoTemplate.remove(new Query(Criteria.where("id").is(id)), "message");
    	return ReturnData.SUCCESS;
    }

    /**
     * 拉消息
     *
     * @param userId
     */
    public void builderUserMessage(long userId) {

        /**
         * 消息 读系统表
         */
        String tname = "system_message";
        // msg_common_user_time=一般用户拉消息时间的时间
        
        //msg_new_user_time=新用户拉消息的时间
        Map<String,String> fms= ConfUtils.getMap("msg_send", "get_msg");
        Integer commonUserTime=Integer.parseInt(fms.get("msg_common_user_time"));
        Integer newUserTime=Integer.parseInt(fms.get("msg_new_user_time"));

        //五天内的系统消息  系统作为参数传入
        long starttime = new Date().getTime() / 1000 - commonUserTime;
        //24小时以内的消息时间  新用户有效默认24小时以内
        long lastTime = new Date().getTime() / 1000 - newUserTime ;
        
        //用户最后一次获取系统消息的时间
        UserInfo userInfo=userService.getMongoUserInfo(userId);
        if(userInfo.getLastMsgTime()!=null) {
        	//之前登录过 获取近5天的消息
        	if(userInfo.getLastMsgTime().longValue()<starttime) {
        		lastTime=starttime;
        	} else {
        		lastTime=userInfo.getLastMsgTime();
        	}
        }
        
        //系统最后一次发送消息的时间
        Integer lst=ConfUtils.getResourceNum(SYS_LAST_MSG_TIME);
        if(lst!=null&&lst>lastTime) {
            //获取事件范围内 用户接收到的管理员消息
            List<Integer> dcrs=new ArrayList<Integer>();
            dcrs.add(2);
            dcrs.add(8);
            Query query = new Query(Criteria.where("publishStatus").is(1).and("type").is(1).and("hidden").is(0).and("description").in(dcrs).and("createStamp").gt(lastTime)).limit(100);
            List<BaseMessage> newbms = mongoTemplate.find(query, BaseMessage.class, tname);

            if (newbms!=null&&newbms.size() > 0) {
               addUserMessage(newbms, userInfo.getId());
               mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), new Update().set("lastMsgTime", new Date().getTime()/1000), "user_info");
            }

        }
    }

    /**
     * 第一次登陆的消息
     *
     * @param userId
     */
    public void firstMessage(long userId) {
        UserInfo user = mongoTemplate.findOne(new Query(Criteria.where("id").is(userId)), UserInfo.class, "user_info");
        if (user == null) {
            //第一次注册发送消息 插入数据
            List<BaseMessage> bms = mongoTemplate.find(new Query(Criteria.where("type").is(1).and("description").is(1)), BaseMessage.class, "system_message");
            addUserMessage( bms, userId);
        }
    }

    public void addUserMessage(List<BaseMessage> msgs, Long userId) {
        if (msgs != null && msgs.size() > 0) {
            List<UserMessage> umlist = new ArrayList<UserMessage>();
            List<Long> mids = new ArrayList<Long>();
            for (int i = 0; i < msgs.size(); i++) {
                BaseMessage msg = msgs.get(i);
                UserMessage um = new UserMessage();
                um.setReceiverId(userId);
                um.setSenderId(msg.getSenderId());

                um.setType(msg.getType());
                um.setBoundary(msg.getBoundary());
                um.setDescription(msg.getDescription());
                um.setRedirectType(msg.getRedirectType());
                um.setRedirectPage(msg.getRedirectPage());
                um.setRedirectParams(msg.getRedirectParams());
                um.setTitle(msg.getTitle());
                um.setContent(msg.getContent());
                um.setPublishStamp(msg.getPublishStamp());
                long id = this.getId("user_message");
                um.setId(id);
                um.setMessageId(msg.get_id());
                um.setMessageIdInt(msg.getId());
                umlist.add(um);
                mids.add(msg.getId());
            }
            //批量插入
            mongoTemplate.insertAll(umlist);
             //系统消息累加
            this.addSysMsg(userId, umlist.size());
        }
    }
    
    
    /**
     * 用户消息 增加或者减少
     * @param userId
     * @param inc
     * @return
     */
    public ReturnData userMessageInc(long userId,int inc) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").in(userId)), new Update().inc("newMsgNum", inc).inc("msgNum", inc), "user_info");
        return ReturnData.SUCCESS;
    }
    
 
    
    /**
     * 更新用户评论消息数量
     * @param userIds
     * @param inc
     * @return
     */
    public ReturnData userCommentMessageInc(List<Long> userIds,int inc) {
        mongoTemplate.updateMulti(new Query(Criteria.where("id").in(userIds)), new Update().inc("newMsgNum", inc).inc("msgNum", inc).inc("newCommentMsgNum", inc).inc("commentMsgNum", inc), "user_info");
        return ReturnData.SUCCESS;
    }
    /**
     * 更新评论消息数量
     */
    public static List<String> UPDATE_COMMENT_MSG_FLIED= new ArrayList<String>();
    /**
     * 更新系统消息数量
     */
    public static List<String> UPDATE_SYS_MSG_FLIED= new ArrayList<String>();
    
    
    public ReturnData addCommentMsg(long userId,long num) {
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(userId)), new Update().inc("newMsgNum", num).inc("msgNum", num).inc("newCommentMsgNum", num).inc("commentMsgNum", num), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
        return ReturnData.SUCCESS;
    }
    
     
    /**
     * 更新用户id 消息数量
     * @param userIds
     * @param num
     * @return
     */
    public ReturnData addCommentMsg(List<Long> userIds,long num) {
        mongoTemplate.findAndModify(new Query(Criteria.where("id").in(userIds)), new Update().inc("newMsgNum", num).inc("msgNum", num).inc("newCommentMsgNum", num).inc("commentMsgNum", num), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
        return ReturnData.SUCCESS;
    }
    
    /**
     * 增加修修酱 消息数量
     * @param userId
     * @param num
     * @return
     */
    public ReturnData addXXJMsg(long userId,long num) {
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(userId)), new Update().inc("newMsgNum", num).inc("msgNum", num).inc("newXxjMsgNum", num).inc("xxjMsgNum", num), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
        return ReturnData.SUCCESS;
    }
    /**
     * 增加修修酱 消息数量
     * @param userIds
     * @param num
     * @return
     */
    public ReturnData addXXJMsg(List<Long> userIds,long num) {
        mongoTemplate.findAndModify(new Query(Criteria.where("id").in(userIds)), new Update().inc("newMsgNum", num).inc("msgNum", num).inc("newXxjMsgNum", num).inc("xxjMsgNum", num), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
        return ReturnData.SUCCESS;
    }


    
    public ReturnData addSysMsg(long userId, long num) {
      mongoTemplate.findAndModify(new Query(Criteria.where("id").is(userId)), new Update().inc("newMsgNum", num).inc("msgNum", num).inc("newSysMsgNum", num).inc("sysMsgNum", num), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
        return ReturnData.SUCCESS;
    }
    /**
     * 更新用户系统消息数量
     * @param userInfo
     * @return
     */
    public ReturnData clearSysMsg(UserInfo userInfo) {
    	Integer newMsgNum=userInfo.getNewMsgNum()-userInfo.getNewSysMsgNum();
    	Update update=new Update();
    	if(newMsgNum<0) newMsgNum=0;
    	update.set("newMsgNum", newMsgNum).set("newSysMsgNum", 0);
      	mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), update, "user_info");
        return ReturnData.SUCCESS;
    }

    /**
     * 更新用户评论消息数量
     * @param userInfo
     * @return
     */
    public ReturnData clearCommentMsg(UserInfo userInfo) {
    	Integer newMsgNum=userInfo.getNewMsgNum()-userInfo.getNewCommentMsgNum();
    	Update update=new Update();
    	if(newMsgNum<0) newMsgNum=0;
    	update.set("newMsgNum", newMsgNum).set("newCommentMsgNum", 0);
      	mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), update, "user_info");
        return ReturnData.SUCCESS;
    }
    /**
     * 清空修修酱
     * @param userInfo
     * @return
     */
    public ReturnData clearXxjMsg(UserInfo userInfo) {
    	Integer newMsgNum=userInfo.getNewMsgNum()-userInfo.getXxjMsgNum();
    	Update update=new Update();
    	if(newMsgNum<0) newMsgNum=0;
    	update.set("newMsgNum", newMsgNum).set("newXxjMsgNum", 0);
      	mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), update, "user_info");
        return ReturnData.SUCCESS;
    }
    /**
     * 清空所有消息 主要是支持旧接口
     * @param userInfo
     * @return
     */
    public ReturnData clearAllMsg(UserInfo userInfo) {
    	Update update=new Update();
    	update.set("newMsgNum", 0).set("newXxjMsgNum", 0).set("newCommentMsgNum", 0).set("newSysMsgNum", 0);
      	mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), update, "user_info");
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
    * @param type   =1 推送
    * @param content: 消息内容
    * @param params: 跳转的参数
    * @param newType: 要跳转的类型(发现,心得,产品等) 同index接口的type
    */
   public ReturnData sendSynMessage(long senderId, List<Long> receiverIds, int type, int description, String title,
                              String content,String redirect_type,
                  			//跳转的页面
                 			 String page,
                 			 String params,Integer newType) {
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
       msg.setPublishStamp(new Date().getTime()/1000);
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
                              String content,String redirectType,String redirectPage,String redirectParams) {
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
           msg.setPublishStamp(new Date().getTime()/1000);
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
           sm.setPublishStamp(new Date().getTime()/1000);
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
	public ReturnData sendMsgByXxj(long replyUserId, String userids, String title,String content,String redirect_type,
			//跳转的页面
			 String page,
			 String params,Integer newType) {
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
   public ReturnData sendEntitySynMessage(long senderId,long  receiverId,String msgCode,String msgField,MsgExt msgExt) {
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
       msg.setPublishStamp(new Date().getTime()/1000);
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
       mongoTemplate.findAndModify(new Query(Criteria.where("id").in(rids)), new Update().inc("newMsgNum", num).inc("msgNum", num).inc("new"+StringUtils.capitalize(msgField)+"MsgNum", num).inc(msgField+"MsgNum", num), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
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
   public ReturnData sendSysmMessage(String msgType,String title,
           String content,String redirect_type,String page,String params) {
	      int boundary=1; 
       CommenMeta.MessageStatus msgDesc= CommenMeta.MessageStatus.getStatusByKey("msg-sys-"+msgType);
       int type=msgDesc.getType();
       int description=msgDesc.getDescription();
       
      if(sendMessage(0,null,boundary,type,description,title,content,redirect_type,page,params)){
    	  return ReturnData.SUCCESS;
      }
      return ReturnData.ERROR;
   }
   
    
	/**
	 * 查询最新消息
	 * @param userId
	 * @return
	 */
	public ReturnData newActive(long userId) {
		//最新活动
		CommenMeta.MessageStatus mst= CommenMeta.ACTIONNAME_MESSAGE.get("msg-sys-new_active");
		String entityTname="system_message";
		BaseMessage msg= mongoTemplate.findOne(new Query(Criteria.where("type").is(mst.getType()).and("description").is(mst.getDescription())).with(new Sort(Direction.DESC, "id")), Message.class, entityTname);
		return new ReturnData(msg);
	}

	/**
	 * 清空新消息的数量
	 * @param userInfo
	 * @return
	 */
    public boolean msgAllOpen(UserInfo userInfo) {
        long userId=userInfo.getId();
    	try {
            //清空消息检查
            if(userInfo.getNewMsgNum()>0&&userInfo.getNewSysMsgNum()==0&&userInfo.getNewXxjMsgNum()==0&&userInfo.getNewCommentMsgNum()==0&&userInfo.getNewCommentLikeMsgNum()==0) {
                String tname = "user_info";
                //新消息数量为0
                WriteResult m = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userId)), new Update().set("newMsgNum", 0), tname);
            }
            return true;
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "MessageService.msgAllOpen");
			map.put("userId", userId);
			new LogException(e, map);
        }
        return false;
    }





}
