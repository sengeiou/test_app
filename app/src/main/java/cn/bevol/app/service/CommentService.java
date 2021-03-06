package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.CompositionOldMapper;
import cn.bevol.app.dao.mapper.DoyenOldMapper;
import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.app.dao.mapper.IndexOldMapper;
import cn.bevol.app.entity.constant.CommenMeta;
import cn.bevol.model.entityAction.Comment;
import cn.bevol.model.entityAction.CommentLike;
import cn.bevol.model.entityAction.EntityActionBase;
import cn.bevol.model.entityAction.SubComment;
import cn.bevol.app.entity.metadata.Tag;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.model.entity.EntityUserPart;
import cn.bevol.model.user.MsgExtComment;
import cn.bevol.model.user.MsgExtCommentLike;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.Log.LogMethod;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ResponseBuilder;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import com.mongodb.BasicDBObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
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
	GoodsOldMapper goodsMapper;

	@Autowired
	CompositionOldMapper compositionMapper;

	@Autowired
	DoyenOldMapper doyenMapper;

	@Autowired
	IndexOldMapper indexMapper;

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
	 * ?????????????????????
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
			//??????100?????????????????????
			commentList = mongoTemplate.find(query, Comment.class, collectionName);
			Collection<String> commentContentCollection = CollectionUtils.collect(commentList, new BeanToPropertyValueTransformer("content"));
			Collection<Long> commentIdCollection = CollectionUtils.collect(commentList, new BeanToPropertyValueTransformer("id"));
			List<String> commentContentList = new ArrayList<String>(commentContentCollection);
			List<Long> commentIdList = new ArrayList<Long>(commentIdCollection);
			for(int j=0; j<commentContentList.size(); j++){
				List<Map> commentMarkerList = aliyunService.textKeywordScan(commentContentList.get(j));
				for (Map results : commentMarkerList) {
					//marker?????????mongo
					mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(commentIdList.get(j))),
							new Update().set("marker", results),
							Comment.class,
							collectionName);
				}
			}

		}while(commentList.size() > 0);
		return ReturnData.SUCCESS;
	}


	public ReturnData findSubComments(String tname, long entityId, long comment_main_id, long userId, int start,
                                      int pageSize) {
		Map m = new HashMap();
		Object ent = entityService.getEntityById(tname, entityId);
		if (ent == null) {
			return EntityBase.ENTITY_HIDDEN;
		}
		// ?????????????????????
		Comment cmt = this.findCommentById(tname, comment_main_id);
		if (cmt != null) {
			if (ent != null && start < 1) {
				//m.put("entity", ent);
				List<Comment> cts = new ArrayList<Comment>();
				cts.add(cmt);
				findCommentUserInfo(userId, cts);
				m.put("mainComment", cts.get(0));
			}
		} else {
			return Comment.HIDDEN_COMMENT;
		}
		int startId=0;
		ReturnListData rd=findSubComments2_5(tname, comment_main_id, userId, start, pageSize);
		m.put("list", rd.getResult());
		return new ReturnData(m);
	}

	/**
	 * ???????????????????????????????????????
	 *
	 * @param tname
	 * @param id
	 * @return
	 */
	public Comment findCommentById(String tname, long id) {
		String actionType = "entity_comment_" + tname;
		Criteria cr = Criteria.where("id").is(id);
		Query query = new Query(cr.and("hidden").is(0));
		query.with(new Sort(Direction.DESC, "id"));
		return mongoTemplate.findOne(query, Comment.class, actionType);
	}

	/**
	 * 2.5?????? ???????????????????????????
	 *
	 * @param tname
	 * @return
	 */
	public ReturnListData findSubComments2_5(final String tname, final long mainId, final long userId,
											 final int startId, final int pageSize) {
		return new CacheableTemplate<ReturnListData<List<Comment>>>(cacheProvider) {
			@Override
			protected ReturnListData getFromRepository() {
				try {
					List<Comment> subCommentList = findSubCommentListByMainId(tname, mainId, startId, pageSize);
					findCommentUserInfo(userId, subCommentList);
					long total = 0;
					return new ReturnListData(subCommentList, 0);
				} catch (Exception e) {
					Map map=new HashMap();
					map.put("method", "CommentService.findSubComments2_5");
					map.put("tname", tname);
					map.put("mainId", mainId);
					new LogException(e,map);
					return ReturnListData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnListData returnValue) {
				return (returnValue != null && returnValue.Tesult() != null && returnValue.Tesult().size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_COMMENT_SUBLIST25_PREFIX,tname,mainId+"",startId+"",pageSize+"")
				),
				true);
	}

	/**
	 * ???????????????????????????????????????
	 *
	 * @param tname
	 * @param mainId
	 * @return
	 */
	public List<Comment> findSubCommentListByMainId(String tname, long mainId, int startId, int pageSize) {
		Integer skinid = Integer.valueOf((startId * pageSize) + "");
		String actionType = "entity_comment_" + tname;
		Criteria cr = Criteria.where("mainId").is(mainId);
		Query query = new Query(cr.and("hidden").is(0).and("pid").gt(0)).skip(skinid).limit(pageSize);
		query.with(new Sort(Direction.DESC, "id"));
		List<Comment> subCommentList = mongoTemplate.find(query, Comment.class, actionType);
		return subCommentList;
	}

	/**
	 * 2.5?????????????????????????????????
	 *
	 * @param tname
	 * @return
	 */
	public ReturnListData findComments2_5(final String tname, final long entityId, final int type, final long userId,
                                          final String skinResults, final long startId, final int pageSize) {
		return new CacheableTemplate<ReturnListData<List<Comment>>>(cacheProvider) {
			@Override
			protected ReturnListData getFromRepository() {
				try {
					int sublimit = ConfUtils.getResourceNum("subcomment_count");
					// ???????????????
					List<Comment> mainList = findMainCommentList(tname, entityId, type, skinResults, startId, pageSize);
					if (mainList == null)
						mainList = new ArrayList<Comment>();
					// ???????????????
					List<Long> ids = new ArrayList<Long>();
					List<Comment> subCms = new ArrayList<Comment>();
					// ??????????????????????????????
					for (Comment c : mainList) {


						ids.add(c.getId());
						// ??????????????? ?????????
						List<Comment> sc = findSubCommentsByMainId(tname, c.getId(), sublimit);
						subCms.addAll(sc);
					}
					// ?????????????????????
					if (subCms == null)
						subCms = new ArrayList<Comment>();
					// ?????????????????????
					// ??????????????????
					List<Comment> allCms = new ArrayList<Comment>();
					allCms.addAll(mainList);
					allCms.addAll(subCms);
					findCommentUserInfo(userId, allCms);

					// ???????????????
					for (Comment m : mainList) {
						m.addSubComment(subCms);
					}
					long total = findMainCommentsTotal(tname, entityId);

					return new ReturnListData(mainList, total);
				} catch (Exception e) {
					Map map=new HashMap();
					map.put("method", "CommentService.findComments2_5");
					map.put("tname", tname);
					map.put("entityId", entityId);
					new LogException(e,map);
					return ReturnListData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnListData returnValue) {
				return (returnValue != null && returnValue.Tesult() != null && returnValue.Tesult().size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_COMMENT_MAINLIST25_PREFIX,tname,entityId+"",type+"",skinResults,startId+"",pageSize+"")
				),
				true);
	}

	/**
	 * 2.7?????????????????????????????????
	 *
	 * @param tname
	 * @return
	 */
	public Map findComments2_7(final String tname, final long entityId, final int type, final long userId,
                               final String skinResults, final long startId, final int pageSize) {
		return new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					// ?????????
					//cleanComment(tname, entityId, null);
					int sublimit = ConfUtils.getResourceNum("subcomment_count");
					// ???????????????
					Map map= findMainCommentList2(tname, entityId, type, skinResults, startId, pageSize);

					//????????????????????? ????????????  ??????????????????
					if(startId<2 && "goods".equals(tname)) {
						List<EntityUserPart> eup=userPartService.findUserPartGoods(entityId,5,"id","title","image");
						map.put("userParts", eup);
					}

					List<Comment> mainList=null;
					if(null!=map && map.size()>0){
						mainList=(List<Comment>)map.get("list");
					}

					if (mainList == null)
						mainList = new ArrayList<Comment>();
					// ???????????????
					List<Long> ids = new ArrayList<Long>();
					List<Comment> subCms = new ArrayList<Comment>();
					// ??????????????????????????????
					for (Comment c : mainList) {
						ids.add(c.getId());
						// ??????????????? ?????????
						List<Comment> sc = findSubCommentsByMainId(tname, c.getId(), sublimit);
						subCms.addAll(sc);
					}
					// ?????????????????????
					if (subCms == null)
						subCms = new ArrayList<Comment>();
					// ?????????????????????
					// ??????????????????
					List<Comment> allCms = new ArrayList<Comment>();
					allCms.addAll(mainList);
					allCms.addAll(subCms);
					findCommentUserInfo(userId, allCms);

					// ???????????????
					for (Comment m : mainList) {
						m.addSubComment(subCms);
					}

					long total = findMainCommentsTotal(tname, entityId);

					map.put("total", total);

					//????????????????????? typeCount
					Map typeCountMap=new HashMap();
					for(int i=1;i<5;i++){
						if(null!=map.get("count"+i) && (Long)map.get("count"+i)>=0){
							typeCountMap.put("count"+i, map.get("count"+i));
						}
					}
					map.put("typeCount", typeCountMap);
					map.put("mainList",mainList);
					return map;
				} catch (Exception e) {
					Map map=new HashMap();
					map.put("method", "CommentService.findComments2_7");
					map.put("tname", tname);
					map.put("entityId", entityId);
					new LogException(e,map);
				}
				return ResponseBuilder.buildFailureMessage();
			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && returnValue.size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_COMMENT_MAINLIST2_25_PREFIX,tname,entityId+"",type+"",skinResults,startId+"",pageSize+"")
				),
				true);
	}



	/**
	 * 3.0????????????????????????
	 *
	 * @param tname
	 * @return
	 */
	public Map findSourceComments(final String tname, final long entityId, final long userId, final long startId,
                                  final int pageSize, final int type, final String skin) {
		return new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					// ?????????????????????????????????????????? //todo  ???????????????
					int sublimit = ConfUtils.getResourceNum("subcomment_count");

					Map map2= findMainCommentList2(tname, entityId, type, skin, startId, pageSize);
					Map map=new HashMap();


					// ????????????????????? ???????????? ??????????????????
					if ("goods".equals(tname)) {
						long skinTotal=0;
						if(null!=map2 && map2.size()>0 && null!=map2.get("count1")){
							skinTotal=(Long)map2.get("count1");
						}
						//???????????????
						map.put("skinTotal", skinTotal);

						if(startId < 1){
							List<EntityUserPart> eup = userPartService.findUserPartGoods(entityId, 5, "id", "title",
									"image");
							map.put("userParts", eup);
						}

					}
					//?????????
					Long commentNum=commentNumByEntityId(tname, entityId);
					//???????????????????????????????????????10?????????????????????
					if(startId < 1){
						// ????????????
						Map mapz = findHotMainCommentList(tname, entityId, 0, 20);
						List<Comment> hotAllList = getMainList((List<Comment>) mapz.get("hotList"), tname, sublimit, userId);
						map.put("hotList", hotAllList);
					}

					// ????????????
					List<Comment> newList = findNewMainCommentList(tname, entityId, startId, pageSize);
					List<Comment> newAllList = getMainList(newList, tname, sublimit, userId);

					long total = findMainCommentsTotal(tname, entityId);

					map.put("total", total);
					map.put("list", newAllList);

					return map;
				} catch (Exception e) {
					Map map=new HashMap();
					map.put("method", "CommentService.findSourceComments");
					map.put("tname", tname);
					map.put("entityId", entityId);
					new LogException(e,map);
				}
				return ResponseBuilder.buildFailureMessage();
			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && returnValue.size() > 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE, CACHE_NAME.createInstanceKey(
				CACHE_NAME.INSTANCE_SOURCE_COMMENT_MAINLIST_PREFIX, tname, entityId + "",type+"",skin, startId + "", pageSize + "")),
				true);
	}

	/**
	 * ?????????????????????????????????????????????????????????,????????????????????????
	 * @param mainList
	 * @param tname
	 * @param sublimit
	 * @param userId
	 * @return
	 */
	public List<Comment> getMainList(List<Comment> mainList, String tname, int sublimit, long userId){
		if (mainList == null)
			mainList = new ArrayList<Comment>();
		// ???????????????
		List<Long> ids = new ArrayList<Long>();
		List<Comment> subCms = new ArrayList<Comment>();
		// ??????????????????????????????
		for (Comment c : mainList) {
			ids.add(c.getId());
			// ??????????????? ?????????
			List<Comment> sc = findSubCommentsByMainId(tname, c.getId(), sublimit);
			subCms.addAll(sc);
		}
		// ?????????????????????
		if (subCms == null)
			subCms = new ArrayList<Comment>();
		// ?????????????????????
		// ??????????????????
		List<Comment> allCms = new ArrayList<Comment>();
		allCms.addAll(mainList);
		allCms.addAll(subCms);
		findCommentUserInfo(userId, allCms);

		// ???????????????
		for (Comment m : mainList) {
			m.addSubComment(subCms);
		}
		return mainList;
	}

	/**
	 * ???????????????????????????
	 * @param skinResults
	 * @return
	 */
	public List skinType(String skinResults){
		String skinType=skinResults.substring(0,1);
		Map map=new HashMap();
		//???????????????map
		if("O".equals(skinType)){
			map=CommenMeta.SKIN_O;
		}else if("D".equals(skinType)){
			map=CommenMeta.SKIN_D;
		}
		//map???list
		List<String> mapKeyList = new ArrayList<String>(map.keySet());
		return mapKeyList;
	}

	/**
	 * ???????????????
	 *
	 * @param tname
	 * @param entityId
	 * @param type
	 * @param skinResults
	 * @param startId
	 * @param pageSize
	 * @return
	 */
	public Map findMainCommentList2(String tname, long entityId, int type, String skinResults, long startId,
                                    int pageSize) {
		String actionType = "entity_comment_" + tname;

		Criteria cr = Criteria.where("entityId").is(entityId);
		List listType=null;
		if(StringUtils.isNotBlank(skinResults)){
			listType=this.skinType(skinResults);
			if(null!=listType && listType.size()>0){
				cr.and("skinResults").in(listType);
			}
		}
		Integer skinid = Integer.valueOf((startId * pageSize) + "");

		Map map=new HashMap();
		// ????????????
		if ("goods".equals(tname)) {
			boolean flag = false;
			// ????????????
			Criteria goodsCri = Criteria.where("entityId").is(entityId);

			// ??????
			if (type == 2) {
				goodsCri.and("score").gte(4).lte(5);
				flag = true;
				// ??????
			} else if (type == 3) {
				goodsCri.and("score").gte(2).lte(3);
				flag = true;
				// ?????????
			} else if (type == 4) {
				goodsCri.and("score").is(1);
				flag = true;
			}

			Query goodsQuery = new Query(goodsCri.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0),
					Criteria.where("pid").exists(false))).skip(skinid).limit(pageSize);
			// ?????????+??????+??????
			goodsQuery.with(new Sort(Direction.DESC, "score")).with(new Sort(Direction.DESC, "likeNum"))
					.with(new Sort(Direction.DESC, "createStamp"));

			// ????????????type?????????
			Long total = 0L;
			for(int i=1;i<5;i++){
				Criteria goodsCri2 = Criteria.where("entityId").is(entityId);
				Query goodsQuery2 = new Query(goodsCri2.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0),
						Criteria.where("pid").exists(false))).skip(skinid).limit(pageSize);
				// ?????????+??????+??????
				goodsQuery2.with(new Sort(Direction.DESC, "score")).with(new Sort(Direction.DESC, "likeNum"))
						.with(new Sort(Direction.DESC, "createStamp"));

				if(i == 1){
					if(!StringUtils.isBlank(skinResults)){
						//goodsCri2.and("skinResults").is(skinResults);
						if(null!=listType && listType.size()>0){
							goodsCri2.and("skinResults").in(listType);
						}
						total = mongoTemplate.count(goodsQuery2, actionType);
						map.put("count"+i, total);
					}
				}else if (i == 2) {
					goodsCri2.and("score").gte(4).lte(5);
					total = mongoTemplate.count(goodsQuery2, actionType);
					map.put("count"+i, total);
					// ??????
				} else if (i == 3) {
					goodsCri2.and("score").gte(2).lte(3);
					total = mongoTemplate.count(goodsQuery2, actionType);
					map.put("count"+i, total);
					// ?????????
				} else if (i == 4) {
					goodsCri2.and("score").is(1);
					total = mongoTemplate.count(goodsQuery2, actionType);
					map.put("count"+i, total);
				}



			}


			if (flag) {
				List<Comment> ls = mongoTemplate.find(goodsQuery, Comment.class, actionType);
				total = mongoTemplate.count(goodsQuery, actionType);
				map.put("count"+type, total);
				map.put("list", ls);
				return map;
			}
		}

		Query query = new Query(
				cr.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false)));

		// ?????????
		if (type == 1 && !StringUtils.isBlank(skinResults)) {
			//cr.and("skinResults").is(skinResults);
			query.with(new Sort(new Order(Direction.ASC, skinResults)));
			Long total = mongoTemplate.count(query, actionType);
			map.put("count"+type, total);
		} else{
			//???????????????????????????
			query.with(new Sort(Direction.DESC, "isEssence")).with(new Sort(Direction.DESC, "likeNum"))
					.with(new Sort(Direction.DESC, "id"));
		}

		List<Comment> ls = mongoTemplate.find(query.skip(skinid).limit(pageSize), Comment.class, actionType);
		map.put("list", ls);
		return map;

	}

	/**
	 * ??????????????????
	 *
	 * @return
	 */
	public Map findHotMainCommentList(String tname, long entityId, long startId, int pageSize) {
		String actionType = "entity_comment_" + tname;
		Criteria cr = Criteria.where("entityId").is(entityId);
		Query query = new Query(
				cr.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false)))
				.skip((int)startId).limit(pageSize);
		//??????????????????
		query.with(new Sort(new Order(Direction.DESC, "isEssence")));
		//????????????
		query.with(new Sort(new Order(Direction.DESC, "likeNum")));
		//????????????
		List<Comment> ls = mongoTemplate.find(query, Comment.class, actionType);
		Map map=new HashMap();
		map.put("hotList", ls);
		return map;

	}

	/**
	 * ??????????????????
	 *
	 * @return
	 */
	public List<Comment> findNewMainCommentList(String tname, long entityId, long startId, int pageSize) {
		String actionType = "entity_comment_" + tname;
		Criteria cr = Criteria.where("entityId").is(entityId);
		Integer skinid = Integer.valueOf((startId * pageSize) + "");
		Query query = new Query(
				cr.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false)))
				.skip(skinid).limit(pageSize);
		//??????
		query.with(new Sort(new Order(Direction.DESC, "id")));
		//????????????
		List<Comment> ls = mongoTemplate.find(query, Comment.class, actionType);
		return ls;

	}

	/**
	 * ???????????????
	 *
	 * @param tname
	 * @param entityId
	 * @param type
	 * @param skinResults
	 * @param startId
	 * @param pageSize
	 * @return
	 */
	public List<Comment> findMainCommentList(String tname, long entityId, int type, String skinResults, long startId,
                                             int pageSize) {
		String actionType = "entity_comment_" + tname;
		Criteria cr = Criteria.where("entityId").is(entityId);
		Integer skinid = Integer.valueOf((startId * pageSize) + "");
		Query query = new Query(
				cr.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false)))
				.skip(skinid).limit(pageSize);
		if (type == 1 && !StringUtils.isBlank(skinResults)) {
			query.with(new Sort(new Order(Direction.ASC, skinResults)));
		} else {
			query.with(new Sort(Direction.DESC, "isEssence")).with(new Sort(Direction.DESC, "likeNum"))
					.with(new Sort(Direction.DESC, "id"));
		}

		List<Comment> ls = mongoTemplate.find(query, Comment.class, actionType);
		return ls;

	}


	/**
	 * ??????????????????????????????(?????????????????????)
	 * TODO
	 * @param tname
	 * @param entityId
	 * @return
	 */
	public long commentNumByEntityId(String tname, long entityId) {
		String actionType = "entity_comment_" + tname;
		Criteria cr = Criteria.where("entityId").is(entityId);
		Query query = new Query(
		cr.and("hidden").is(0).and("score").exists(true).orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false)));
		//String actionType="entity_"+tname;
		//Query query=new Query(Criteria.where("id").is(entityId));
		//query.fields().include("commentNum");
		long total = mongoTemplate.count(query, Comment.class, actionType);
		//long total=mongoTemplate.count(query, actionType);
		return total;
	}

	/**
	 * ???????????????????????????
	 *
	 * @param userId
	 *            ???????????????id
	 * @param userId
	 *            ???????????????id
	 * @param allCms
	 *            ??????s
	 */
	public void findCommentUserInfo(long userId, List<Comment> allCms) {
		StringBuffer userIds = new StringBuffer();
		Map<Long, String> users = new HashMap<Long, String>();
		if (allCms != null)
			for (Comment c : allCms) {
				c.setHiddenFeild();
				// ??
				if (c.getUserId() != null)
					users.put(c.getUserId(), null);
				if (c.getpUserId() != null)
					users.put(c.getpUserId(), null);

				//img????????????
			}
		// ????????????
		for (Map.Entry<Long, String> entry : users.entrySet()) {
			userIds.append(",").append(entry.getKey());
		}

		// ??????????????????
		if (!StringUtils.isBlank(userIds.toString())) {
			String userIdss = userIds.substring(1);
			ReturnData rd = userService.findUserinfoByIds(userIdss);
			if (rd.getRet() == 0) {
				List<UserInfo> userInfos = (List<UserInfo>) rd.getResult();
				for (Comment c : allCms) {
					boolean flag1 = true;
					boolean flag2 = true;
					// ???????????????????????????
					if (userId > 0) {
						List<CommentLike> cls = c.getCommentLikes();
						for (int i = 0; cls != null && i < cls.size(); i++) {
							if (cls.get(i) != null && cls.get(i).getUserId() == userId) {
								c.setIsLike(1);
							}
						}
					}
					if(c.getUserId()==null) {
						UserInfo m=new UserInfo();
						c.setUserInfo(m);
						m.baseInfo();
					} else {
						// ??????????????????
						if (flag1 || flag2) {
							for (UserInfo u : userInfos) {
								if (c.getUserId().intValue() == u.getId()) {
									c.setUserInfo(u.baseInfo());
									flag1 = false;
								}
								if (c.getpUserId() != null && c.getpUserId().intValue() == u.getId()) {
									c.setpUserInfo(u.baseInfo());
									flag2 = false;
								}
								u.baseInfo();
							}
						}
					}
				}
			}
		}

	}

	/**
	 * ???????????????
	 *
	 * @param tname
	 * @return
	 */
	private long findMainCommentsTotal(String tname, long entityId) {
//		String actionType = "entity_comment_" + tname;
//		long total = mongoTemplate.count(new Query(Criteria.where("entityId").is(entityId).and("hidden").is(0).and("content").exists(true)
//				.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))), actionType);
		 
		String actionType="entity_"+tname;
		Query query=new Query(Criteria.where("id").is(entityId));
		query.fields().include("commentNum");
		EntityGoods map = mongoTemplate.findOne(query, EntityGoods.class, actionType);
		long total=0;
		if(null!=map && null!=map.getCommentNum()){
			total=map.getCommentNum();
		}
		return total;
	}

	/**
	 * ?????????????????????????????????
	 *
	 * @param tname
	 * @return
	 */
	private long findCommentsTotal(final String tname, final long entityId) {
		return new CacheableTemplate<Long>(cacheProvider) {
			@Override
			protected Long getFromRepository() {
				try {
					return findMainCommentsTotal(tname, entityId);
				} catch (Exception e) {
					Map map=new HashMap();
					map.put("method", "CommentService.findCommentsTotal");
					map.put("tname", tname);
					map.put("entityId", entityId);
					new LogException(e,map);
					return new Long(0);
				}

			}

			@Override
			protected boolean canPutToCache(Long returnValue) {
				return (returnValue != null && returnValue > 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_COMMENT_TOTAL_PREFIX,tname,entityId+"")), true);

	}

	/**
	 * 2.5????????????????????????????????? ???????????????
	 *
	 * @param tname
	 * @return
	 */
	@Deprecated
	public ReturnListData findComments(final String tname, final long entityId, final int type, final long userId,
                                       final String skinResults, final long startId, final int pageSize) {
		return new CacheableTemplate<ReturnListData<List<Comment>>>(cacheProvider) {
			@Override
			protected ReturnListData getFromRepository() {
				try {
					// ???????????????
					List<Comment> ls = findMainCommentList(tname, entityId, type, skinResults, startId, pageSize);
					if (ls == null)
						ls = new ArrayList<Comment>();
					// ??????????????????????????????
					String userIds = "";
					for (Comment c : ls) {
						userIds += "," + c.getUserId();
						List<CommentLike> cls = c.getCommentLikes();
						for (int i = 0; i < cls.size(); i++) {
							if (cls.get(i).getUserId() == userId) {
								c.setIsLike(1);
							}
						}

					}
					// ?????????????????????
					if (!StringUtils.isBlank(userIds)) {
						userIds = userIds.substring(1);
						ReturnData rd = userService.findUserinfoByIds(userIds);
						if (rd.getRet() == 0) {
							List<UserInfo> userInfos = (List<UserInfo>) rd.getResult();
							for (Comment c : ls) {
								boolean flag = true;
								if (flag) {
									for (UserInfo u : userInfos) {
										if (c.getUserId().intValue() == u.getId()) {
											c.setUserInfo(u);
											flag = false;
										}

									}
								}
							}
						}
					}
					long total = findCommentsTotal(tname, entityId);
					return new ReturnListData(ls, total);
				} catch (Exception e) {
					Map map=new HashMap();
					map.put("method", "CommentService.findComments");
					map.put("tname", tname);
					map.put("entityId", entityId);
					map.put("type", type);
					map.put("userId", userId);
					map.put("skinResults", skinResults);
					map.put("startId", startId);
					map.put("pageSize", pageSize);
					new LogException(e,map);
					return ReturnListData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnListData returnValue) {
				return (returnValue != null && returnValue.Tesult() != null && returnValue.Tesult().size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_COMMENT_OLDLIST_PREFIX,tname,entityId+"",type+"",skinResults+"",startId+"",pageSize+"")
				),
				true);

	}

	/**
	 * ?????????????????????????????????????????????????????????
	 *
	 * @param tname
	 * @param ids
	 * @return
	 */
	public List<Comment> findSubCommentsByMainId(String tname, Long ids, int limit) {
		//
		Criteria cr = Criteria.where("mainId").in(ids).and("hidden").is(0).and("pid").gt(0);
		Query query = new Query(cr);
		query.with(new Sort(Direction.DESC, "id")).limit(limit);
		String actionType = "entity_comment_" + tname;
		List<Comment> ls = mongoTemplate.find(query, Comment.class, actionType);
		if (ls.size() > 0) {
			for (Comment comment : ls) {
				comment.setMainId(ids);
			}
		}

		return ls;
	}

	/**
	 * ???????????????
	 *
	 * @param tname
	 * @param commentid
	 * @param replyUserId
	 * @param replyContent
	 * @return
	 */
	@Deprecated
	public ReturnData backReplyComment(String tname, long commentid, long replyUserId, String replyContent, String redirect_type,
                                       //???????????????
                                       String page,
                                       String params, Integer newType) {

		try {
			replyUserId = 248660;
			ReturnData<UserInfo> rd = userService.getUserById(replyUserId);
			if (rd.getRet() != 0)
				return ReturnData.ERROR;
			UserInfo replyUser = rd.TResult();

			// ??????tname
			String actionType = "entity_comment_" + tname;

			if (StringUtils.isBlank(replyContent))
				return Comment.ERROR_CONTENT_NOTNULL;

			// ??????
			Comment cmt = getCommentById(tname, commentid);
			SubComment subcmt = new SubComment();
			subcmt.setContent(replyContent);
			subcmt.setUserId(replyUserId);
			subcmt.setSubCommentId(UUID.randomUUID().toString());
			subcmt.setNickname(replyUser.getNickname());
			// ??????????????????
			EntityBase eb = this.getEntityById(tname, cmt.getEntityId());
			if (eb == null)
				return ReturnData.ERROR;
			mongoTemplate.updateFirst(new Query(new Criteria().where("id").is(commentid)),
					new Update().addToSet("subComments", subcmt), Comment.class, actionType);
			// ????????????
			Integer msgType = 1;
			String title = "";
			CommenMeta.MessageStatus msgDesc = CommenMeta.MessageStatus.getStatusByKey("reply-comment_" + tname);
			String content = msgDesc.managerReply(cmt.getCreateStamp(), replyUser.getNickname(), eb.getTitle(),
					replyContent);
			List<Long> receiverIds = new ArrayList<Long>();
			receiverIds.add(cmt.getUserId());
			messageService.sendSynMessage(replyUserId, receiverIds, msgType, msgDesc.getDescription(), title, content,redirect_type,page,params,newType);

			// ??????????????????
			cleanComment(tname, cmt.getEntityId(), null);

			return new ReturnData();
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.sendSubComment");
			map.put("tname", tname);
			map.put("replyUserId", replyUserId);
			map.put("commentid", commentid);
			map.put("replyUserId", replyUserId);
			map.put("replyContent", replyContent);
			new LogException(e,map);
		}
		return ReturnData.ERROR;
	}

	/**
	 * ?????????
	 *
	 * @param tname
	 *            ??????
	 * @param entityId
	 *            ??????id
	 * @param userInfo
	 *            ????????????
	 * @param content
	 *            ??????
	 * @param score
	 *            ??????
	 * @param image
	 *            ??????
	 * @return true?????? false?????????
	 */
	public ReturnData sendComment(String tname, long entityId, UserInfo userInfo, String content, int score,
								  String image) {

		//????????????
		ReturnData rd1=this.oldSwitchOfphoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//???????????????
		ReturnData rdVer=this.oldVerifyState(userInfo);
		if(rdVer.getRet()!=0){
			return rdVer;
		}
		// TODO Auto-generated method stub
		long userId = 0L;
		String skin = "";

		String key = "notkeyword";
		String skinResults = null;
		try {
			EntityBase map = this.getEntityById(tname, entityId);
			if (map.getAllowComment() != null && map.getAllowComment() == 1) {
				return Comment.NOT_ALLOW_COMMENT;
			}
			// ?????????
			String value = entityService.getConfig(key);
			if (!StringUtils.isBlank(value)) {
				content = entityService.keywordInfiltration2(content, value);
			}

			userId = userInfo.getId();
			if (!StringUtils.isBlank(userInfo.getResult())) {
				skin = userInfo.getResult();
			}
			if (!StringUtils.isBlank(userInfo.getSkinResults())) {
				skinResults = userInfo.getSkinResults();
			}

			// ??????????????????
			String actionType = "entity_comment_" + tname;
			Comment cmt = mongoTemplate.findOne(
					new Query(Criteria.where("userId").is(userId).and("entityId").is(entityId)), Comment.class,
					actionType);
			if (cmt == null) {
				cmt = new Comment();
				cmt.setContent(content);
				cmt.setScore(score);
				cmt.setUserId(userId);
				cmt.setEntityId(entityId);
				cmt.setSkin(userInfo.getResult());
				cmt.setSkinResults(skinResults);
				cmt.calculatSkin();
				//cmt.setImage(image);
				Long cmid = this.getId(actionType);
				cmt.setId(cmid);
				cmt.setMainId(cmid);
				cmt.setPid(0L);
				mongoTemplate.save(cmt, actionType);
				// ??????
				if (tname.equals("find")) {
					userService.addScore(userId, UserService.ScoreOpt.SENDCOMMENTFIND);
				} else if (tname.equals("goods")) {
					userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNEGOODS);
				} else if (tname.equals("composition")) {
					userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNECOMPOSITION);
				}
				String entityTname = "entity_" + tname;
				this.objectIncById(entityId, entityTname, "commentNum", 1);
				cleanComment(tname, entityId, null);
				cmt.setHiddenFeild();
				return new ReturnData(cmt);
			} else {
				return Comment.REVER_COMMENT;
			}
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.sendComment");
			map.put("tname", tname);
			map.put("entityId", entityId);
			map.put("userId", userId);
			map.put("skin", skin);
			map.put("score", score);
			map.put("image", image);
			map.put("content", content);
			map.put("skinResults", skinResults);
			new LogException(e,map);
		}
		return null;
	}




	public void cleanComment(String tname, long entityId, Long mainId) {

		if (mainId != null && mainId > 0) {
			cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_TOTAL_PREFIX , tname , mainId+""));

			//???????????????
			cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_SUBLIST25_PREFIX , tname , mainId+""));

		}
		// ??????????????????
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_OLDLIST_PREFIX , tname , entityId+""));
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_MAINLIST25_PREFIX , tname , entityId+""));
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMMENT_MAINLIST2_25_PREFIX , tname , entityId+""));
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_SOURCE_COMMENT_MAINLIST_PREFIX , tname , entityId+""));

	}

	/**
	 * ????????????????????????
	 *
	 * @param tname
	 *            ??????
	 * @param entityId
	 *            ??????id
	 * @param pid
	 *            ?????????id
	 * @param content
	 *            ??????
	 * @param score
	 *            ??????
	 * @param image
	 *            ??????
	 * @param isvid
	 *            ??????????????????????????????,??????????????????????????????.true:??????/??????
	 */
	public ReturnData replySend(String tname, long entityId, long pid, UserInfo userInfo, String content, int score,
                                String image, boolean isvid) {

		//????????????
		ReturnData rd1=this.oldSwitchOfphoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//???????????????
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
			// ????????????????????????
			if (enty.getAllowComment() != null && enty.getAllowComment() == 1) {
				return Comment.NOT_ALLOW_COMMENT;
			}
			if (StringUtils.isBlank(content)) {
				return Comment.ERROR_CONTENT_NOTNULL;
			}
			if (isvid) {
				// ?????????
				String value = entityService.getConfig(key);
				if (!StringUtils.isBlank(value)) {
					//??????????????????
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
				//????????????????????????
				ReturnData rd=validateService.vSendTime(userId, actionType);
				if(rd.getRet()!=0) return rd;
			}
			// ??????????????????
			// ???????????????
			// ??????????????? ????????????????????????
			// ????????????????????????
			Comment pcmt = null;
			boolean isSendMsg = false;
			Comment cmt = new Comment();
			if (pid <= 0) {//??????????????????
				//?????????????????????????????????
				Comment mainCmt = mongoTemplate.findOne(
						new Query(Criteria.where("entityId").is(entityId).and("userId").is(userId)
								.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
						Comment.class, actionType);
				if (mainCmt != null) {
					//?????????????????????
					return Comment.REVER_COMMENT;
				} else {
					// ????????????????????????
					Long cmid = this.getId(actionType);
					cmt.setId(cmid);
					cmt.setPid(0L);
					cmt.setMainId(cmid);
					cmt.setUserId(userId);
					String entityTname = "entity_" + tname;
					//???????????????++
					this.objectIncById(entityId, entityTname, "commentNum", 1);
					// ?????????????????????,???????????????
					if (tname.equals("find")) {
						userService.addScore(userId, UserService.ScoreOpt.SENDCOMMENTFIND);
					} else if (tname.equals("goods")) {
						userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNEGOODS);
					} else if (tname.equals("composition")) {
						userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNECOMPOSITION);
					}
				}
			} else {//????????????????????????
				// ??????????????????
				pcmt = mongoTemplate.findOne(new Query(Criteria.where("id").is(pid)), Comment.class, actionType);
				if (pcmt != null && pcmt.getHidden() == 0) {
					// ????????????
					Long cmid = this.getId(actionType);
					cmt.setId(cmid);
					cmt.setpUserId(pcmt.getUserId());
					cmt.setPid(pid);
					long mainId = 0;
					long mainUserId = 0;
					if ((pcmt.getPid() == null || pcmt.getPid() == 0)) {
						// ???????????????????????????
						mainId = pcmt.getId();
						mainUserId = pcmt.getUserId();
					} else {
						// ????????????????????????
						mainId = pcmt.getMainId();
						mainUserId = pcmt.getMainUserId();
					}
					cmt.setMainId(mainId);
					cmt.setMainUserId(mainUserId);
					// ????????????????????????++
					this.objectIncById(mainId, actionType, "commentNum", 1);

					// ???????????? ??????????????????
					isSendMsg = true;

				} else {
					//?????????????????????
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
			//?????????????????????
			cleanComment(tname, entityId, mainId);
			//???????????????????????????
			cmt.setHiddenFeild();
			//????????????????????????
			if (isSendMsg) {
				// ????????????
				if(pcmt.getUserId()!=null&&pcmt.getUserId()>0) {
					String msgCode="msg-reply-comment_"+tname;
					String msgField="comment";
					Map map= CommonUtils.ObjectToMap(cmt);
					messageService.sendEntitySynMessage(userInfo.getId(), cmt.getpUserId(),msgCode,msgField, MsgExtComment.createCommentMsg(tname, map, pcmt, enty, userInfo));
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
	 * ??????????????????
	 *
	 * @param tname
	 *            ??????
	 * @return true ?????? flase??????????????????
	 */
	public ReturnData sendCommentLike(String tname, long commentId, UserInfo userInfo, int pager, int pageSize, int type) {

		long userId = 0L;
		String skin = "";
		String skinResults = "";
		try {
			if("apply_goods".equals(tname)){
				tname="apply_goods2";
			}
			userId = userInfo.getId();
			if (!StringUtils.isBlank(userInfo.getResult())) {
				skin = userInfo.getResult();
			}
			if (!StringUtils.isBlank(userInfo.getSkinResults())) {
				skinResults = userInfo.getSkinResults();
			}
			String actionType = "entity_comment_" + tname;
			CommentLike cl = new CommentLike();
			cl.setUserId(userId);
			cl.setSkin(skin);
			cl.setSkinResults(skinResults);
			Query query = Query.query(new Criteria().andOperator(Criteria.where("id").is(commentId),
					Criteria.where("commentLikes").elemMatch(Criteria.where("userId").is(userId))));

			//????????????????????????
			Comment comment = mongoTemplate.findOne(new Query(Criteria.where("id").is(commentId).and("hidden").is(0)), Comment.class,
					actionType);
			//??????????????????
			if(null!=comment && comment.getId()>0){
				//???????????????????????????
				Comment eb = mongoTemplate.findOne(query, Comment.class, actionType);
				if (eb == null) {
					//????????????
					mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)),
							new Update().addToSet("commentLikes", cl), actionType);
					// ??????
					this.objectIncById(commentId, actionType, "likeNum", 1);
					// ????????????
					this.objectIncById(comment.getUserId(), "user_info", "commentLikeNum", 1);
					// ????????????
					// messaegService.commentLikeMessage(userId, comment.getUserId()
					// + "", tname);
					//???????????????????????????????????????
					Query q=new Query(Criteria.where("msgExt.cId").is(comment.getId()).and("msgExt.rUserId").is(userInfo.getId()));
					HashMap mes = mongoTemplate.findOne(q, HashMap.class, "user_message");
					if(mes==null) {
						EntityBase enty = mongoTemplate.findOne(new Query(Criteria.where("id").is(comment.getEntityId())), EntityBase.class, "entity_"+tname);
						String msgCode="msg-comment_like_"+tname;
						String msgField="commentLike";
						messageService.sendEntitySynMessage(userInfo.getId(), comment.getUserId(),msgCode,msgField, MsgExtCommentLike.createCommentLikeMsg(tname, comment, enty, userInfo));
					}
					//??????????????????????????????????????????
					//this.setCommentListLike(comment, tname, userInfo, 1,pager,pageSize, type);
					return new ReturnData(1, "????????????");
				} else {
					//????????????
					Update update = new Update();
					update.pull("commentLikes", new BasicDBObject("userId", userId));
					mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)), update, actionType);

					this.objectIncById(commentId, actionType, "likeNum", -1);

					this.objectIncById(comment.getUserId(), "user_info", "commentLikeNum", -1);

					//this.setCommentListLike(comment, tname, userInfo, 0,pager,pageSize, type);
					return new ReturnData(2, "??????????????????");
				}
			}
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.sendCommentLike");
			map.put("tname", tname);
			map.put("commentId", commentId);
			map.put("userId", userId);
			new LogException(e,map);
		}
		return null;
	}

	/**
	 * ???????????????????????????????????????
	 */
	public void setCommentListLike(Comment comment, String tname, UserInfo userInfo, Integer like, int pager, int pageSize, int type){
		try {
			if(null!=comment){
				int num=0;
				if(like==1){
					num=1;
				}else{
					num=-1;
				}
				if(null!=comment.getPid() && comment.getPid()>0){
					//???????????????
					ReturnListData rd=this.findSubComments2_5(tname, comment.getPid(), userInfo.getId(), pager, pageSize);
					if(rd.getRet()==0){
						List<Comment> subCommentList =(List<Comment>)rd.getResult();
						this.setLike(subCommentList,comment,like,num);
					}

					this.pushObjectToRedisCache(rd, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,CACHE_NAME.INSTANCE_COMMENT_SUBLIST25_PREFIX,tname,comment.getPid()+"",pager+"",pageSize+"");
				}else{
					if("goods".equals(tname)&& 0==type){
						type=1;
					}
					//??????????????????
					Map map=this.findSourceComments(tname,comment.getEntityId(), userInfo.getId(), pager,
							pageSize,type,userInfo.getSkinResults());
					List<Comment> hotList=(List)map.get("hotList");
					List<Comment> list=(List)map.get("list");

					this.setLike(list,comment,like,num);
					this.setLike(hotList,comment,like,num);
					this.pushObjectToRedisCache(map,CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,CACHE_NAME.INSTANCE_SOURCE_COMMENT_MAINLIST_PREFIX, tname, comment.getEntityId() + "",type+"",userInfo.getSkinResults(), pager + "", pageSize + "");
				}
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	public void setLike(List<Comment> cmtList, Comment comment, int like, int num){
		for(Comment cmt:cmtList){
			if(cmt.getId()==comment.getId().longValue()){
				cmt.setIsLike(like);
				cmt.setLikeNum(cmt.getLikeNum()+num);
			}
		}
	}

	/**
	 * ????????????
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
	 * ???????????????
	 *
	 * @param tname
	 * @param commentId
	 * @param userId
	 * @param content
	 * @return
	 */
	public ReturnData sendSubComment3(String tname, long commentId, long pid, long userId, String content,
                                      String image) {
		try {
			String key = "notkeyword";
			// ?????????
			String value = entityService.getConfig(key);
			if (!StringUtils.isBlank(value)) {
				content = entityService.keywordInfiltration2(content, value);
			}

			String actionType = "entity_comment_" + tname;
			SubComment cmt = new SubComment();
			cmt.setImage(image);
			cmt.setContent(content);
			cmt.setUserId(userId);
			cmt.setSubCommentId(UUID.randomUUID().toString());
			mongoTemplate.updateFirst(new Query(new Criteria().where("id").is(commentId)),
					new Update().addToSet("subComments", cmt), Comment.class, actionType);
			return new ReturnData(cmt);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.sendSubComment3");
			map.put("tname", tname);
			map.put("userId", userId);
			map.put("pid", pid);
			map.put("image", image);
			map.put("content", content);
			new LogException(e,map);
		}
		return null;

	}

	/**
	 * ???????????????
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
			// ?????????
			String value = entityService.getConfig(key);
			if (!StringUtils.isBlank(value)) {
				content = entityService.keywordInfiltration2(content, value);
			}

			String actionType = "entity_comment_" + tname;
			SubComment cmt = new SubComment();
			cmt.setContent(content);
			cmt.setUserId(userId);
			cmt.setSubCommentId(UUID.randomUUID().toString());
			mongoTemplate.updateFirst(new Query(new Criteria().where("id").is(commentId)),
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

	/**
	 * ????????????????????????
	 *
	 * @param tname
	 * @param userId
	 * @param startId
	 * @param pageSize
	 * @return
	 */
	public Map findCommentByUserId(String tname, long userId, long startId, int pageSize) {
		try {
			if("apply_goods".equals(tname)){
				tname="apply_goods2";
			}
			String actionType = "entity_comment_" + tname;
			Map map = entityService.findEntityActionType(actionType, tname, userId, Comment.class, startId, pageSize);
			//
			if (map != null) {
				// ??????????????????
				List<Map> maps = (List<Map>) map.get("result");
				if (maps != null) {
					for (Map m : maps) {
						EntityActionBase cmts = (EntityActionBase) m.get("action");
						if (cmts != null) {
							Comment ct = (Comment) cmts;
							ct.setHiddenFeild();
						}
					}

				}
			}
			return map;
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.findCommentByUserId");
			map.put("tname", tname);
			map.put("userId", userId);
			map.put("startId", startId);
			map.put("pageSize", pageSize);
			new LogException(e,map);
		}
		return null;
	}

	/**
	 * ????????????????????????
	 * ????????????????????????---
	 * @param tname
	 * @param userId
	 * @param startId
	 * @param pageSize
	 * @return
	 */
	/*public Map findCommentByUserId(String tname, long userId, long startId, int pageSize) {
		try {
			String actionType = "entity_comment_" + tname;
			Map map =null;
			if("compare_goods".equals(tname)){
				//????????????
				actionType="entity_discuss_"+tname;
				map = entityService.findEntityActionType(actionType, tname, userId, Discuss.class, startId, pageSize);
			}else{
				map = entityService.findEntityActionType(actionType, tname, userId, Comment.class, startId, pageSize);
			}
			//
			if (map != null) {
				// ??????????????????
				List<Map> maps = (List<Map>) map.get("result");
				if (maps != null) {
					for (Map m : maps) {
						EntityActionBase cmts = (EntityActionBase) m.get("action");
						if (cmts != null) {
							if(cmts instanceof Comment){
								Comment ct = (Comment) cmts;
								ct.setHiddenFeild();
							}
						}
					}

				}
			}
			return map;
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.findCommentByUserId");
			map.put("tname", tname);
			map.put("userId", userId);
			map.put("startId", startId);
			map.put("pageSize", pageSize);
			new cn.bevol.log.LogException(e,map);
		}
		return null;
	}*/

	/**
	 * ????????????
	 *
	 * @param tname
	 * @param userId
	 *            ??????
	 * @param content
	 *            ??????
	 * @return
	 */
	public ReturnData updateComment(String tname, long userId, long commentId, String content, String image, String[] images, Boolean addScore) {
		UserInfo userInfo=new UserInfo();
		userInfo=mongoTemplate.findOne(new Query(Criteria.where("id").is(userId)), UserInfo.class,"user_info");
		//????????????
		ReturnData rd1=this.oldSwitchOfphoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}

		//???????????????
		ReturnData rdVer=this.oldVerifyState(userInfo);
		if(rdVer.getRet()!=0){
			return rdVer;
		}

		return updateCmt(tname, userId, commentId, content, image, images,addScore);
	}

	/**
	 * ????????????
	 *
	 * @param tname
	 * @param userId
	 *            ??????
	 * @param content
	 *            ??????
	 * @return
	 */
	public ReturnData updateCmt(String tname, long userId, long commentId, String content, String image,String[] images,Boolean addScore) {

		Comment comment = new Comment();
		try {
			if(StringUtils.isBlank(content)){
				//??????????????????????????????
				return Comment.ERROR_CONTENT_NOTNULL;
			}

			String actionType = "entity_comment_" + tname;
			// ???????????????
			String key = "notkeyword";
			// ?????????
			String value = entityService.getConfig(key);
			if (!StringUtils.isBlank(value)) {
				content = entityService.keywordInfiltration2(content, value);
			}

			// ?????????????????????(isEssence)
			Query query = new Query(new Criteria().where("id").is(commentId).and("userId").is(userId));
			String[] includeFields={"id","userId","score","content","image","images","entityId","skin","skinResults","isEssence","appVersion"};
			this.setQueryFeilds(query,includeFields);
			comment = mongoTemplate.findOne(
					query, Comment.class,actionType);
			if (null == comment)
				return ReturnData.ERROR;
			if (comment.getIsEssence() != null && comment.getIsEssence() == 1) {
				return Comment.UNABLE_UPDATE_ISESSENCE;
			}

			Update u = new Update().set("content", content.trim()).set("updateStamp", new Date().getTime()/1000);
			comment.setContent(content.trim());
			if (image != null) {
				u.set("image", image);
				comment.setImage(image);
			}

			if(null!=images){
				if(images.length>0){
					u.set("images", images);
					//u.set("image", images[0]);
					comment.setImages(images);
				}else if(images.length==0){
					//????????????
					u.set("images", null);
					u.set("image", null);
					comment.setImages(null);
					comment.setImage(null);
				}
			}

			mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)), u, actionType);
			this.cleanComment(tname, comment.getEntityId(), null);

			if(null!=addScore && addScore){
				//???????????????
				if (tname.equals("find")) {
					userService.addScore(userId, UserService.ScoreOpt.SENDCOMMENTFIND);
				} else if (tname.equals("goods")) {
					userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNEGOODS);
				} else if (tname.equals("composition")) {
					userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNECOMPOSITION);
				}
			}

			return new ReturnData(comment);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.updateCmt");
			map.put("tname", tname);
			map.put("userId", userId);
			map.put("content", content);
			map.put("image", image);
			new LogException(e,map);
		}
		return ReturnData.ERROR;

	}

	/**
	 * ????????????
	 *
	 * @param tname
	 * @param userId
	 *            ??????
	 * @param content
	 *            ??????
	 * @return
	 */
	public ReturnData updateComment2(String tname, long userId, long commentId, String content, String image, String[] images, Boolean addScore) {
		UserInfo userInfo=new UserInfo();
		if("apply_goods".equals(tname)){
			tname="apply_goods2";
		}
		userInfo=mongoTemplate.findOne(new Query(Criteria.where("id").is(userId)), UserInfo.class,"user_info");
		//????????????
		ReturnData rd1=this.switchOfPhoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//???????????????
		ReturnData rd=this.verifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}

		return this.updateCmt(tname, userId, commentId, content, image, images,addScore);

	}

	/**
	 * ????????????
	 *
	 * @param tname
	 * @return
	 */
	public ReturnData delComment(long id, String tname) {
		String actionType = "entity_comment_" + tname;
		mongoTemplate.remove(new Query(Criteria.where("id").is(id)), actionType);
		return new ReturnData();
	}

	/**
	 * ????????????
	 *
	 * @param tname
	 * @param userId
	 *            ??????
	 * @param entityId
	 *            ??????id
	 * @return
	 */
	public ReturnData delComment(String tname, long userId, long entityId) {
		String actionType = "entity_comment_" + tname;
		Comment cmt = mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("entityId").is(entityId)),
				Comment.class, actionType);
		if (cmt != null)
			return delComment(cmt.getId(), tname);
		return ReturnData.ERROR;
	}



	/**
	 * ????????????
	 * @param tname
	 * @param id
	 * @param userInfo
	 * @param score
	 * @return
	 */
	public ReturnData commentScore(String tname, long id, UserInfo userInfo, int score, List tags) {
		String actionType = "entity_comment_" + tname;
		Comment cmt = new Comment();
		cmt.setScore(score);
		cmt.setUserId(userInfo.getId());
		cmt.setEntityId(id);
		cmt.setSkin(userInfo.getResult());
		cmt.setSkinResults(userInfo.getSkinResults());
		cmt.calculatSkin();
		cmt.setId(this.getId(actionType));
		cmt.setAppVersion(this.VERSION);
		//??????
		if(null!=tags && tags.size()>0){
			cmt.setTags(tags);
		}
		//????????????
		mongoTemplate.save(cmt, actionType);
		cmt.setHiddenFeild();
		return new ReturnData(cmt);
	}











	/**
	 * v3.2
	 * ???????????????
	 * @param tname
	 * @param entityId
	 * @param pid
	 * @param userInfo
	 * @param content
	 * @param score
	 * @param image
	 * @return
	 */
	public ReturnData sendMain3_2(String tname, long entityId, long pid, UserInfo userInfo, String content, int score,
                                  String image, List tags, String[] images, Integer reason) {
		//????????????
		ReturnData rd1=this.switchOfPhoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//???????????????
		ReturnData rd=this.verifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}

		rd=sendMainComment(tname, entityId, pid, userInfo, content, score, image, tags, images,reason);
		return rd;
	}

	/**
	 * v3.2??????
	 * ???????????????
	 * @param tname
	 * @param entityId
	 * @param pid
	 * @param userInfo
	 * @param content
	 * @param score
	 * @param image
	 * @return
	 */
	public ReturnData sendMain(String tname, long entityId, long pid, UserInfo userInfo, String content, int score,
                               String image, List tags, String[] images) {

		//????????????
		ReturnData rd1=this.oldSwitchOfphoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//???????????????
		ReturnData rd=this.oldVerifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}

		rd=sendMainComment(tname, entityId, pid, userInfo, content, score, image, tags, images,null);
		return rd;
	}

	/**
	 * v3.2??????
	 * ???????????????
	 * @param tname
	 * @param entityId
	 * @param pid
	 * @param userInfo
	 * @param content
	 * @param score
	 * @param image
	 * @return
	 */
	public ReturnData sendMainComment(String tname, long entityId, long pid, UserInfo userInfo, String content, int score,
                                      String image, List tags, String[] images, Integer reason) {
		long userId=userInfo.getId();
		String actionType = "entity_comment_" + tname;
		Long cmid = this.getId(actionType);
		Comment cmt = new Comment();
		cmt.setId(cmid);
		// ????????????????????????
		cmt.setPid(0L);
		cmt.setMainId(cmid);
		cmt.setUserId(userId);
		cmt.setImage(image);
		cmt.setContent(content);
		cmt.setScore(score);
		cmt.setUserId(userId);
		cmt.setEntityId(entityId);
		cmt.setSkin(userInfo.getResult());
		cmt.setSkinResults(userInfo.getSkinResults());
		cmt.calculatSkin();
		cmt.setHiddenFeild();
		cmt.setAppVersion(this.VERSION);
		if(null!=tags && tags.size()>0){
			cmt.setTags(tags);
		}
		if(null!=images && images.length>0){
			cmt.setImages(images);
		}

		if(null!=reason && reason==1){
			cmt.setReason(reason);
		}

		mongoTemplate.save(cmt, actionType);
		String entityTname = "entity_" + tname;
		if (tname.equals("find")) {
			userService.addScore(userId, UserService.ScoreOpt.SENDCOMMENTFIND);
		} else if (tname.equals("goods")) {
			userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNEGOODS);
		} else if (tname.equals("composition")) {
			userService.addScore(userId, UserService.ScoreOpt.SENDCOMMNECOMPOSITION);
		}
		//?????????????????????
		cleanComment(tname, entityId, null);

		//?????????????????????,??????????????????????????????
		if("user_part_lists".equals(tname)){
			EntityBase enty = this.getEntityById(tname, entityId);
			Query query=new Query(Criteria.where("id").is(entityId));
			query.fields().include("userId");
			EntityUserPart eup=mongoTemplate.findOne(query, EntityUserPart.class,"entity_user_part_lists");
			String msgCode="msg-reply-main-comment_"+tname;
			String msgField="comment";
			Map map=CommonUtils.ObjectToMap(cmt);
			messageService.sendEntitySynMessage(userInfo.getId(), eup.getUserId(),msgCode,msgField, MsgExtComment.createCommentMsg(tname, map, null, enty, userInfo));
		}

		return new ReturnData(cmt);
	}


	/**
	 * ???????????????
	 * @param tname
	 * @param entityId
	 * @param pid
	 * @param userInfo
	 * @param content
	 * @param image
	 * @return
	 */
	public ReturnData sendSub(String tname, long entityId, long pid, UserInfo userInfo, String content, String image){
		//????????????
		ReturnData rd1=this.oldSwitchOfphoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//??????????????????
		ReturnData rd=this.oldVerifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}

		return this.sendSubCmt(tname, entityId, pid, userInfo, content, image);

	}

	/**
	 * ???????????????
	 * @param tname
	 * @param entityId
	 * @param pid
	 * @param userInfo
	 * @param content
	 * @param image
	 * @return
	 */
	public ReturnData sendSub3_2(String tname, long entityId, long pid, UserInfo userInfo, String content, String image){
		//????????????
		ReturnData rd=this.switchOfPhoneCheck(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}
		//???????????????
		ReturnData rd1=this.verifyState(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}

		return this.sendSubCmt(tname, entityId, pid, userInfo, content, image);

	}

	/**
	 * ???????????????
	 * @param tname
	 * @param entityId
	 * @param pid
	 * @param userInfo
	 * @param content
	 * @param image
	 * @return
	 */
	public ReturnData sendSubCmt(String tname, long entityId, long pid, UserInfo userInfo, String content, String image){

		String actionType = "entity_comment_" + tname;
		// ??????????????????
		Comment pcmt = mongoTemplate.findOne(new Query(Criteria.where("id").is(pid)), Comment.class, actionType);
		if (pcmt != null && pcmt.getHidden() == 0) {
			EntityBase enty = this.getEntityById(tname, entityId);
			Long cmid = this.getId(actionType);
			Comment cmt = new Comment();
			cmt.setId(cmid);
			// ????????????
			cmt.setpUserId(pcmt.getUserId());
			cmt.setPid(pid);
			long mainId = 0;
			long mainUserId = 0;
			if ((pcmt.getPid() == null || pcmt.getPid() == 0)) {
				// ?????????????????????
				mainId = pcmt.getId();
				mainUserId = pcmt.getUserId();
			} else {
				// ???????????????????????? ???????????????????????????????????????
				mainId = pcmt.getMainId();
				mainUserId = pcmt.getMainUserId();
			}
			cmt.setMainId(mainId);
			cmt.setMainUserId(mainUserId);
			// ????????????????????????++
			this.objectIncById(mainId, actionType, "commentNum", 1);
			cmt.setImage(image);
			cmt.setContent(content);
			cmt.setUserId(userInfo.getId());
			cmt.setEntityId(entityId);
			cmt.setSkin(userInfo.getResult());
			cmt.setSkinResults(userInfo.getSkinResults());
			cmt.setAppVersion(this.VERSION);
			cmt.calculatSkin();
			mongoTemplate.save(cmt, actionType);
			mainId = cmt.getMainId();
			cleanComment(tname, entityId, mainId);
			cmt.setHiddenFeild();
			// ????????????
			if(pcmt.getUserId()!=null&&pcmt.getUserId()>0) {
				if("apply_goods2".equals(tname)){
					tname="apply_goods";
				}
				String msgCode="msg-reply-comment_"+tname;
				String msgField="comment";
				Map map=CommonUtils.ObjectToMap(cmt);
				messageService.sendEntitySynMessage(userInfo.getId(), cmt.getpUserId(),msgCode,msgField,MsgExtComment.createCommentMsg(tname, map, pcmt, enty, userInfo));
			}
			return new ReturnData(cmt);
		} else {
			return Comment.HIDDEN_COMMENT;
		}

	}

	public ReturnData replySend3_0(String tname, long entityId, long pid, UserInfo userInfo, String content, int score,
                                   String image, boolean isvid) {

		long userId = 0L;
		String skin = "";
		String key = "notkeyword";
		long mainId=0;
		String skinResults = null;
		try {
			EntityBase enty = this.getEntityById(tname, entityId);
			// ??????????????????
			if (enty.getAllowComment() != null && enty.getAllowComment() == 1) {
				return Comment.NOT_ALLOW_COMMENT;
			}
			//???????????????????????????
			if (isvid) {
				// ?????????
				String value = entityService.getConfig(key);
				if (!StringUtils.isBlank(value)) {
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
				if (!(pid <= 0&&StringUtils.isBlank(content)&&score>0&&"goods".equals(tname))) {
					//????????????????????????
					ReturnData rd=validateService.vSendTime(userId, actionType);
					if(rd.getRet()!=0) return rd;
				}
			}

			//????????????????????????
			String[] images=null;
			if(StringUtils.isNotBlank(image)){
				images=image.split(",");
			}
			if (pid <= 0) {
				//???????????????
				Comment mainCmt = mongoTemplate.findOne(
						new Query(Criteria.where("entityId").is(entityId).and("userId").is(userId)
								.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
						Comment.class, actionType);

				if (mainCmt != null) {
					mainId=mainCmt.getId();
					//1??????????????????????????????
					if(mainCmt.getScore()>0&&tname.equals("goods")){
						//???????????????
						if(StringUtils.isBlank(mainCmt.getContent())) {
							return this.updateComment(tname, userId, mainCmt.getId(), content, image,images,true);
						}
					}
					return Comment.REVER_COMMENT;
				} else {
					//???????????????????????????????????????  todo  ?????????????????????????????????????????????
// 					this.objectIncById(entityId, "entity_goods", "commentNum", 1);
					this.objectIncById(entityId, "entity_" + tname, "commentNum", 1);

					//2????????????????????????
					if (StringUtils.isBlank(content)&&score>0&&tname.equals("goods")) {
						//????????????
						return this.commentScore(tname, entityId, userInfo, score,null);
					}

					if(score<0&&tname.equals("goods")) {
						return Comment.SCORE_GT_0;
					}

					//???????????????????????????????????????????????????
					//cacheService.cleanCacheListByKey(CACHE_NAME.INSTANCE_INDEX_6_PREFIX);

					//3???????????????????????????
					return this.sendMain(tname, entityId, pid, userInfo, content, score, image,null,images);
				}
			} else {
				//????????????
				return this.sendSub(tname, entityId, pid, userInfo, content, image);
			}
			//?????????????????????
			//cleanComment(tname, entityId, mainId);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.replySend3.0");
			map.put("tname", tname);
			map.put("userId", userId);
			map.put("content", content);
			map.put("image", image);
			map.put("entityId", entityId);
			map.put("isvid", isvid);
			map.put("score", score);
			new LogException(e,map);
		}
		return null;
	}

	public ReturnData replySend3_2(String tname, long entityId, long pid, UserInfo userInfo, String content, int score,
                                   String image, boolean isvid, String tags, String images, Integer reason) {

		long userId = 0L;
		String skin = "";
		String key = "notkeyword";
		long mainId=0;
		String skinResults = null;
		try {
			if("apply_goods".equals(tname)){
				tname="apply_goods2";
			}
			EntityBase enty = this.getEntityById(tname, entityId);
			// ??????????????????
			if (enty.getAllowComment() != null && enty.getAllowComment() == 1) {
				return Comment.NOT_ALLOW_COMMENT;
			}
			//???????????????????????????
			if (isvid) {
				// ?????????
				String value = entityService.getConfig(key);
				if (!StringUtils.isBlank(value)) {
					content = entityService.keywordInfiltration2(content, value);
				}
			}

			List<Tag> tagList=new ArrayList<Tag>();

			//??????
			JSONArray  commentTags  =null;
			if(StringUtils.isNotBlank(tags)){
				commentTags=JSONArray.fromObject(tags);
				//????????????
				for(int i=0;i<commentTags.size();i++) {
					JSONObject obj=(JSONObject) commentTags.get(i);
					if(obj.getLong("id")>0&&StringUtils.isNotBlank(obj.getString("title"))) {
						tagList.add(new Tag(obj.getLong("id"),obj.getString("title")));
					}
				}
			}

			//????????????,????????????
			String[] imagess=null;
			if(StringUtils.isNotBlank(images)){
				//??????????????????,???????????????
				images= CommonUtils.getImages(images);
				imagess=images.split(",");
				image=imagess[0];
			}

			userId = userInfo.getId();
			if (!StringUtils.isBlank(userInfo.getResult())) {
				skin = userInfo.getResult();
			}
			if (!StringUtils.isBlank(userInfo.getSkinResults())) {
				skinResults = userInfo.getSkinResults();
			}
			String actionType = "entity_comment_" + tname;
			//???????????????????????????????????????
			if (isvid && !"apply_goods2".equals(tname)) {
				if (!(pid <= 0&&StringUtils.isBlank(content)&&score>0&&"goods".equals(tname))) {
					//????????????????????????
					ReturnData rd=validateService.commentSendTime(userId, actionType);
					if(rd.getRet()!=0) return rd;
				}
			}
			if (pid <= 0) {
				//???????????????
				Comment mainCmt = mongoTemplate.findOne(
						new Query(Criteria.where("entityId").is(entityId).and("userId").is(userId)
								.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
						Comment.class, actionType);

				if (mainCmt != null && !"apply_goods2".equals(tname)) {
					mainId=mainCmt.getId();
					//1??????????????????????????????
					if(mainCmt.getScore()>0&&tname.equals("goods")){
						//
						if(StringUtils.isBlank(mainCmt.getContent())) {
							return this.updateComment2(tname, userId, mainCmt.getId(), content, image,imagess,true);
						}
					}
					return Comment.REVER_COMMENT;
				} else {
					//???????????????????????????????????????  todo  ?????????????????????????????????????????????
// 					this.objectIncById(entityId, "entity_goods", "commentNum", 1);
					this.objectIncById(entityId, "entity_" + tname, "commentNum", 1);

					//2????????????????????????,?????????
					if (StringUtils.isBlank(content)&&score>0&&tname.equals("goods")) {
						//????????????
						return this.commentScore(tname, entityId, userInfo, score,tagList);
					}

					if(score<0&&tname.equals("goods")) {
						return Comment.SCORE_GT_0;
					}

					//???????????????????????????????????????????????????
					//cacheService.cleanCacheListByKey(CACHE_NAME.INSTANCE_INDEX_6_PREFIX);

					//3???????????????????????????
					return this.sendMain3_2(tname, entityId, pid, userInfo, content, score, image,tagList,imagess,reason);
				}
			} else {
				//????????????
				return this.sendSub3_2(tname, entityId, pid, userInfo, content, image);
			}
			//?????????????????????
			//cleanComment(tname, entityId, mainId);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "CommentService.replySend3.0");
			map.put("tname", tname);
			map.put("userId", userId);
			map.put("content", content);
			map.put("image", image);
			map.put("entityId", entityId);
			map.put("isvid", isvid);
			map.put("score", score);
			new LogException(e,map);
		}
		return null;
	}

	//?????????????????????
	public void replyTpag(long id, String tname) {
		String actionType="entity_comment_"+tname;
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)),new Update().set("isComment", 1),Comment.class, actionType);
		
		
	}

	/**
	 * ??????????????????
	 * @param tname
	 * @param userId
	 * @param commentId
	 * @return
	 */
	public ReturnData hiddenComment(String tname, long userId, long commentId) {
		Comment comment=new Comment();
		//?????????????????????
		String actionType = "entity_comment_" + tname;
		Query query = new Query(new Criteria().where("id").is(commentId).and("userId").is(userId));
		comment = mongoTemplate.findOne(
				query, Comment.class,actionType);
		if (null == comment)
			return ReturnData.ERROR;
		if (comment.getIsEssence() != null && comment.getIsEssence() == 1) {
			return Comment.UNABLE_DELETE_ISESSENCE;
		}
		//????????????
		Update update=new Update().set("hidden", 2);
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId).and("userId").is(userId)), update, actionType);
		return ReturnData.SUCCESS;
	}
}