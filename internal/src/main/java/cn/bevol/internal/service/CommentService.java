package cn.bevol.internal.service;

import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.mapper.CompositionOldMapper;
import cn.bevol.internal.dao.mapper.DoyenOldMapper;
import cn.bevol.internal.dao.mapper.GoodsOldMapper;
import cn.bevol.internal.dao.mapper.IndexOldMapper;
import cn.bevol.internal.entity.entityAction.Comment;
import cn.bevol.internal.entity.entityAction.SubComment;
import cn.bevol.internal.entity.user.MsgExtComment;
import cn.bevol.internal.entity.user.UserInfo;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.Log.LogMethod;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CommentService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(CommentService.class);
	private static final String VERSION="3.0";

	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	GoodsOldMapper goodsOldMapper;

	@Autowired
	CompositionOldMapper compositionOldMapper;

	@Autowired
	DoyenOldMapper doyenOldMapper;

	@Autowired
	IndexOldMapper indexOldMapper;

	@Resource
	UserService userService;

	@Resource
	EntityService entityService;
	@Resource
	MessageService messageService;
	@Resource
	AliyunService aliyunService;
	@Autowired
	ValidateService validateService;
	@Autowired
	CacheService cacheService;
	@Autowired
	UserPartService userPartService;

	/**
	 * 评论反垃圾数据
	 * @param tname
	 * @return
	 * @throws Exception
	 */
	@LogMethod
	public ReturnData commentAntiSpam(String tname) throws Exception {
		List<Comment> commentList;
		String collectionName = "entity_comment_"+tname;
		ArrayList<String> blankContent = new ArrayList<String>();
		blankContent.add("");
		blankContent.add(null);
		Query query = new Query(Criteria.where("marker").is(null).and("content").nin(blankContent)).limit(100);
		query.fields().include("id").include("content");
		do {
			//读取100个未标记的评论
			commentList = mongoTemplate.find(query, Comment.class, collectionName);
			Collection<String> commentContentCollection = CollectionUtils.collect(commentList, new BeanToPropertyValueTransformer("content"));
			Collection<Long> commentIdCollection = CollectionUtils.collect(commentList, new BeanToPropertyValueTransformer("id"));
			List<String> commentContentList = new ArrayList<String>(commentContentCollection);
			List<Long> commentIdList = new ArrayList<Long>(commentIdCollection);
			for(int j=0; j<commentContentList.size(); j++){
				List<Map> commentMarkerList = aliyunService.textKeywordScan(commentContentList.get(j));
				for (Map results : commentMarkerList) {
					//marker更新到mongo
					mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(commentIdList.get(j))),
							new Update().set("marker", results),
							Comment.class,
							collectionName);
				}
			}

		}while(commentList.size() > 0);
		return ReturnData.SUCCESS;
	}






	public void cleanComment(String tname, long entityId, Long mainId) {

		if (mainId != null && mainId > 0) {
			cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_TOTAL_PREFIX , tname , mainId+""));

			//清除子评论
			cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_SUBLIST25_PREFIX , tname , mainId+""));

		}
		// 清除评论缓存
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_OLDLIST_PREFIX , tname , entityId+""));
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_MAINLIST25_PREFIX , tname , entityId+""));
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_MAINLIST2_25_PREFIX , tname , entityId+""));
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_SOURCE_COMMENT_MAINLIST_PREFIX , tname , entityId+""));

	}

	/**
	 * 带子评论发送功能
	 *
	 * @param tname
	 *            类型
	 * @param entityId
	 *            实体id
	 * @param pid
	 *            父评论id
	 * @param userid
	 *            发送用户
	 * @param content
	 *            内容
	 * @param score
	 *            评分
	 * @param image
	 *            图片
	 * @param isvid
	 *            是否对关键字进行验证,评论发送频率是否控制.true:验证/控制
	 */
	public ReturnData replySend(String tname, long entityId, long pid, UserInfo userInfo, String content, int score,
                                String image, boolean isvid) {

		//手机绑定
		ReturnData rd1=this.oldSwitchOfphoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//身份证验证
		ReturnData rdVer=this.oldVerifyState(userInfo);
		if(rdVer.getRet()!=0){
			return rdVer;
		}
		long userId = 0L;
		String skin = "";

		String key = "notkeyword";
		String skinResults = null;
		try {

			EntityBase enty = this.getEntityById(tname, entityId);
			// 产品是否可以评论
			if (enty.getAllowComment() != null && enty.getAllowComment() == 1) {
				return Comment.NOT_ALLOW_COMMENT;
			}
			if (StringUtils.isBlank(content)) {
				return Comment.ERROR_CONTENT_NOTNULL;
			}
			if (isvid) {
				// 过滤词
				String value = entityService.getConfig(key);
				if (!StringUtils.isBlank(value)) {
					//评论内容过滤
					content = entityService.keywordInfiltration2(content, value);
				}
			}

			userId = userInfo.getId();
			if (!StringUtils.isBlank(userInfo.getResult())) {
				skin = userInfo.getResult();
			}
			if (!StringUtils.isBlank(userInfo.getSkinResults())) {
				skinResults = userInfo.getSkinResults();
			}
			String actionType = "entity_comment_" + tname;
			if (isvid) {
				//评论发送频率控制
				ReturnData rd=validateService.vSendTime(userId, actionType);
				if(rd.getRet()!=0) return rd;
			}
			// 建立评论实体
			// 发送过评论
			// 查找主评论 用于用于字段冗余
			// 自己评论过主评论
			Comment pcmt = null;
			boolean isSendMsg = false;
			Comment cmt = new Comment();
			if (pid <= 0) {//发送一级评论
				//查询是否发送过一级评论
				Comment mainCmt = mongoTemplate.findOne(
						new Query(Criteria.where("entityId").is(entityId).and("userId").is(userId)
								.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
						Comment.class, actionType);
				if (mainCmt != null) {
					//发送过一级评论
					return Comment.REVER_COMMENT;
				} else {
					// 自己发表一级评论
					Long cmid = this.getId(actionType);
					cmt.setId(cmid);
					cmt.setPid(0L);
					cmt.setMainId(cmid);
					cmt.setUserId(userId);
					String entityTname = "entity_" + tname;
					//实体评论数++
					this.objectIncById(entityId, entityTname, "commentNum", 1);
					// 发送一级评论后,修行值加分
					if (tname.equals("find")) {
						userService.addScore(userId, UserService.ScoreOpt.SENDCOMMENTFIND);
					} else if (tname.equals("goods")) {
						userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNEGOODS);
					} else if (tname.equals("composition")) {
						userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNECOMPOSITION);
					}
				}
			} else {//发送的非一级评论
				// 获取上级评论
				pcmt = mongoTemplate.findOne(new Query(Criteria.where("id").is(pid)), Comment.class, actionType);
				if (pcmt != null && pcmt.getHidden() == 0) {
					// 上级评论
					Long cmid = this.getId(actionType);
					cmt.setId(cmid);
					cmt.setpUserId(pcmt.getUserId());
					cmt.setPid(pid);
					long mainId = 0;
					long mainUserId = 0;
					if ((pcmt.getPid() == null || pcmt.getPid() == 0)) {
						// 上级评论是一级评论
						mainId = pcmt.getId();
						mainUserId = pcmt.getUserId();
					} else {
						// 上级不是一级评论
						mainId = pcmt.getMainId();
						mainUserId = pcmt.getMainUserId();
					}
					cmt.setMainId(mainId);
					cmt.setMainUserId(mainUserId);
					// 一级评论的评论数++
					this.objectIncById(mainId, actionType, "commentNum", 1);

					// 评论回复 发送系统消息
					isSendMsg = true;

				} else {
					//上级评论被隐藏
					return Comment.HIDDEN_COMMENT;
				}
			}

			cmt.setImage(image);
			if(StringUtils.isNotBlank(image)){
				String[] images=image.split(",");
				cmt.setImages(images);
			}
			cmt.setContent(content);
			cmt.setScore(score);
			cmt.setUserId(userId);
			cmt.setEntityId(entityId);
			cmt.setSkin(userInfo.getResult());
			cmt.setSkinResults(skinResults);

			cmt.calculatSkin();
			mongoTemplate.save(cmt, actionType);
			Long mainId = cmt.getMainId();
			//清除相关的缓存
			cleanComment(tname, entityId, mainId);
			//隐藏前台显示的字段
			cmt.setHiddenFeild();
			//给被回复者发消息
			if (isSendMsg) {
				// 发送消息
				if(pcmt.getUserId()!=null&&pcmt.getUserId()>0) {
					String msgCode="msg-reply-comment_"+tname;
					String msgField="comment";
					Map map= CommonUtils.ObjectToMap(cmt);
					messageService.sendEntitySynMessage(userInfo.getId(), cmt.getpUserId(),msgCode,msgField,MsgExtComment.createCommentMsg(tname, map, pcmt, enty, userInfo));
				}
			}
			return new ReturnData(cmt);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.replySend");
			map.put("tname", tname);
			map.put("entityId", entityId);
			map.put("userId", userInfo.getId());
			map.put("pid", pid);
			map.put("score", score);
			map.put("image", image);
			map.put("content", content);
			map.put("isvid", isvid);
			new LogException(e,map);
		}
		return null;
	}


	/**
	 * 获取评论
	 *
	 * @param tname
	 * @param id
	 * @return
	 */
	public Comment getCommentById(String tname, long id) {
		tname = "entity_comment_" + tname;
		Comment cmt = mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), Comment.class, tname);
		return cmt;
	}

	/**
	 * 含有子评论
	 *
	 * @param tname
	 * @param commentId
	 * @param userId
	 * @param content
	 * @return
	 */
	@Deprecated
	public ReturnData sendSubComment(String tname, long commentId, long userId, String content) {
		try {
			String key = "notkeyword";
			// 过滤词
			String value = entityService.getConfig(key);
			if (!StringUtils.isBlank(value)) {
				content = entityService.keywordInfiltration2(content, value);
			}

			String actionType = "entity_comment_" + tname;
			SubComment cmt = new SubComment();
			cmt.setContent(content);
			cmt.setUserId(userId);
			cmt.setSubCommentId(UUID.randomUUID().toString());
			mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)),
					new Update().addToSet("subComments", cmt), Comment.class, actionType);
			return new ReturnData(cmt);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.sendSubComment");
			map.put("tname", tname);
			map.put("userId", userId);
			map.put("commentId", commentId);
			map.put("content", content);
			new LogException(e,map);
		}
		return null;

	}

	//管理员回复标记
	public void replyTpag(long id, String tname) {
		String actionType="entity_comment_"+tname;
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)),new Update().set("isComment", 1),Comment.class, actionType);
		
		
	}

}