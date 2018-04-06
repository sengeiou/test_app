package cn.bevol.entity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;

import cn.bevol.constant.CommenMeta.MessageStatus;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.entityAction.Comment;
import cn.bevol.model.user.FeedBack;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.ReturnData;

/**
 * 反馈处理
 * @author Administrator
 *
 */
@Service
public class FeedBackService extends BaseService{

    @Autowired
    EntityService entityService;
    @Autowired
    UserService userService;
    
    @Autowired
    CommentService commentService;

    @Autowired
    MessageService messageService;
    @Autowired
    ValidateService validateService;

    
    @Autowired
    MongoTemplate mongoTemplate;
    /**
     * 保存反馈
     * @param actionname
     * @param userInfo
     * @param action
     * @param ationtype
     * @param actionid
     * @param content
     * @param images
     * @param source
     * @param version
     * @param OS
     * @param model
     * @param uuid
     * @return 
     * 
     */
    public ReturnData saveFeedBack(String actionname,UserInfo userInfo, int action,int ationtype, long actionid,
    		String content, String images, String source,String version,String o
    		,String model,String uuid,String sysV,String fields1,String fields2,String fields3) {
        String tname="feedback";
    	FeedBack fb =new FeedBack();
    	long userId=userInfo.getId();
    	String nickName=userInfo.getNickname();
    	//排除举报
    	if(action!=3) {
            ReturnData vs= validateService.vSendTime(userId,tname);
     		if(vs.getRet()!=0) return vs;
    	}
        // 纠错  反馈 直接新增记录 
    	if(action==1||action==2) {
			//if(0==ationtype) return FeedBack.ERROR_CONTENT_NOTNULL;
    		if(action==1) {
    			if(actionid<=0) return ReturnData.ERROR; 
    			fb.setEntityId(actionid);
    			//查询实体
    			EntityBase eb=entityService.getEntityById(actionname, actionid);
    			if(eb==null) return ReturnData.ERROR;
    			fb.setEntityTitle(eb.getTitle());
    		}
    		Long id=this.getId(tname);
        	fb.setId(id);
        	
     	}  else if(action==3) {
     		//检查用户是否举报过
     		FeedBack jb=mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("action").is(action).and("actionId").is(actionid).and("actionName").is(actionname)), FeedBack.class, tname);
     		if(jb!=null) {
     	        Map m=new HashMap();
     	        m.put("id", jb.getId());
     			return  new ReturnData(m);
     		}
     		//获取被举报用户
     		//1、查询的评论
     		int findex=actionname.indexOf("_");
     		String tb=actionname.substring(findex+1); //实体名称
     		if(actionname.indexOf("comment")!=-1) {
     			if("apply_goods".equals(tb)){
     				tb="apply_goods2";
     			}
     			Comment cmt=commentService.getCommentById(tb, actionid);
     			if(cmt==null) return ReturnData.ERROR;
     			//获取评论内容
     			fb.setContent(cmt.getContent());

     			//查找用户
     			ReturnData rd=userService.getUserById(cmt.getUserId());
     			if(rd.getRet()!=0) return rd;
     			UserInfo ownerUserInfo=(UserInfo) rd.getResult();
     			
     	    	fb.setOwnerNickName(ownerUserInfo.getNickname());
     	    	fb.setOwnerUserId(ownerUserInfo.getId());
     	    	
     	    	//查找产品
     	    	EntityBase eb=entityService.getEntityById(tb, cmt.getEntityId());
     			if(eb==null) return ReturnData.ERROR;
     			fb.setEntityTitle(eb.getTitle());
			} else{
				//心得或者文章等实体的举报
				EntityBase entity=this.getEntityById(actionname,actionid);
				if(null!=entity){
					fb.setEntityTitle(entity.getTitle());
				}else{
					return ReturnData.ERROR;
				}
			}
    		Long id=this.getId(tname);
 			fb.setId(id);
     	}
    	//被举报内容
    	if(StringUtils.isNotBlank(fields1)){
        	fb.setFields_1(fields1);
    	}
		if(StringUtils.isNotBlank(fields2)){
        	fb.setFields_2(fields2);

    	}
		if(StringUtils.isNotBlank(fields3)){
        	fb.setFields_3(fields3);
		}
		
		fb.setImages(images);
    	fb.setUserId(userId);
    	fb.setNickName(nickName);
    	fb.setActionName(actionname);
    	fb.setAction(action);
    	fb.setActionType(ationtype);
    	fb.setActionId(actionid);
    	fb.setSource(source);
    	fb.setVersion(version);
    	fb.setPlatform(o);
    	fb.setModel(model);
    	fb.setUuid(uuid);
    	fb.setSysV(sysV);
    	if(!StringUtils.isBlank(content)) {
        	fb.setContent(content);
    	}
        mongoTemplate.save(fb, tname);
        Map m=new HashMap();
        m.put("id", fb.getId());
        return new ReturnData(m);
    }
    

	/**
     * 根据id查询 feedback
     * @param id
     * @return
     */
    public ReturnData<FeedBack> getFeedBackById(long id) {
    	FeedBack fb=  mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), FeedBack.class, "feedback");
    	if(fb==null) return ReturnData.ERROR;
    	return new ReturnData<FeedBack>(fb);
    }
    
    /**
     * 根据id查询 feedback
     * @param id
     * @return
     */
    public ReturnData deleteFeedBackById(long id) {
    	WriteResult wr= mongoTemplate.remove(new Query(Criteria.where("id").is(id)), "feedback");
    	return ReturnData.SUCCESS;
    }
    
    

    /**
     * 后台管理回复
     * @param id
     * @param content
     * @return
     */
	public ReturnData backReply(long id, String replyContent,long replyUserId, String redirect_type,
			//跳转的页面
			 String page,
			 String params,Integer newType) {
    	ReturnData<UserInfo> rd=userService.getUserById(replyUserId);
    	if(rd.getRet()!=0) return ReturnData.ERROR;
    	UserInfo replyUser=rd.TResult();
		if(StringUtils.isBlank(replyContent)) return FeedBack.ERROR_CONTENT_NOTNULL;
		ReturnData<FeedBack> rfb=this.getFeedBackById(id);
		if(rfb.getRet()!=0) return rfb;
		FeedBack fb=rfb.TResult();
		fb.setReplyContent(replyContent);
		fb.setReplyUserId(replyUserId);
		
		//回复内容
		 WriteResult m =mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), new Update().set("replyContent", replyContent).set("replyUserId", replyUserId), "feedback");
		
		String title="";
		MessageStatus msgDesc=MessageStatus.getStatusByKey("msg-xxj-feedback-"+fb.getAction()+"-"+fb.getActionName());
		String content=msgDesc.managerReply(fb.getCreateStamp(),replyUser.getNickname(), fb.getEntityTitle(), replyContent);
		List<Long> receiverIds=new ArrayList<Long>();
		receiverIds.add(fb.getUserId());
		//发送消息
		//ReturnData md=messageService.sendSynMessage(replyUserId, receiverIds, msgType, msgDesc.getDescription(), title, content);
		
		//ReturnData md=messageService.sendXxjMessage( replyUserId, receiverIds,msgDesc.getType(), msgDesc.getDescription(), title, content);
	       ReturnData r=  messageService.sendSynMessage(replyUserId, receiverIds, msgDesc.getType(), msgDesc.getDescription(), title, content,redirect_type,page,params,newType);
		return r;
	}
	

}
