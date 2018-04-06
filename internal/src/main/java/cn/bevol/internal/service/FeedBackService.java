package cn.bevol.internal.service;

import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.entity.constant.CommenMeta;
import cn.bevol.internal.entity.user.FeedBack;
import cn.bevol.internal.entity.user.UserInfo;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
	 * 后台管理回复
	 * @param id
	 * @param content
	 * @return
	 */
	public ReturnData backReply(long id, String replyContent, long replyUserId, String redirect_type,
								//跳转的页面
								String page,
								String params, Integer newType) {
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
		CommenMeta.MessageStatus msgDesc= CommenMeta.MessageStatus.getStatusByKey("msg-xxj-feedback-"+fb.getAction()+"-"+fb.getActionName());
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
