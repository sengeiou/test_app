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


	public ReturnData findSubComments(String tname, long entityId, long comment_main_id, long userId, int start,
                                      int pageSize) {
		Map m = new HashMap();
		Object ent = entityService.getEntityById(tname, entityId);
		if (ent == null) {
			return EntityBase.ENTITY_HIDDEN;
		}
		// 查询某一条评论
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
	 * 获取单个主评论的所有子评论
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
	 * 2.5以后 单个主评论的子评论
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
	 * 获取单个主评论的所有子评论
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
	 * 2.5以后的接口查询评论关系
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
					// 获取主评论
					List<Comment> mainList = findMainCommentList(tname, entityId, type, skinResults, startId, pageSize);
					if (mainList == null)
						mainList = new ArrayList<Comment>();
					// 获取子评论
					List<Long> ids = new ArrayList<Long>();
					List<Comment> subCms = new ArrayList<Comment>();
					// 评论当前用户是否评论
					for (Comment c : mainList) {


						ids.add(c.getId());
						// 每次哪一条 分多次
						List<Comment> sc = findSubCommentsByMainId(tname, c.getId(), sublimit);
						subCms.addAll(sc);
					}
					// 用户查询子评论
					if (subCms == null)
						subCms = new ArrayList<Comment>();
					// 获取子评论用户
					// 查询用户信息
					List<Comment> allCms = new ArrayList<Comment>();
					allCms.addAll(mainList);
					allCms.addAll(subCms);
					findCommentUserInfo(userId, allCms);

					// 封装子集合
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
	 * 2.7以后的接口查询评论关系
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
					// 清缓存
					//cleanComment(tname, entityId, null);
					int sublimit = ConfUtils.getResourceNum("subcomment_count");
					// 获取主评论
					Map map= findMainCommentList2(tname, entityId, type, skinResults, startId, pageSize);

					//第一页加载点评 用户参与  只有产品拥有
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
					// 获取子评论
					List<Long> ids = new ArrayList<Long>();
					List<Comment> subCms = new ArrayList<Comment>();
					// 评论当前用户是否评论
					for (Comment c : mainList) {
						ids.add(c.getId());
						// 每次哪一条 分多次
						List<Comment> sc = findSubCommentsByMainId(tname, c.getId(), sublimit);
						subCms.addAll(sc);
					}
					// 用户查询子评论
					if (subCms == null)
						subCms = new ArrayList<Comment>();
					// 获取子评论用户
					// 查询用户信息
					List<Comment> allCms = new ArrayList<Comment>();
					allCms.addAll(mainList);
					allCms.addAll(subCms);
					findCommentUserInfo(userId, allCms);

					// 封装子集合
					for (Comment m : mainList) {
						m.addSubComment(subCms);
					}

					long total = findMainCommentsTotal(tname, entityId);

					map.put("total", total);

					//各种类型的数量 typeCount
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
	 * 3.0源生产品详情评论
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
					// 一级评论列表中子评论显示数量 //todo  硬编码重构
					int sublimit = ConfUtils.getResourceNum("subcomment_count");

					Map map2= findMainCommentList2(tname, entityId, type, skin, startId, pageSize);
					Map map=new HashMap();


					// 第一页加载点评 用户参与 只有产品拥有
					if ("goods".equals(tname)) {
						long skinTotal=0;
						if(null!=map2 && map2.size()>0 && null!=map2.get("count1")){
							skinTotal=(Long)map2.get("count1");
						}
						//同肤质数量
						map.put("skinTotal", skinTotal);

						if(startId < 1){
							List<EntityUserPart> eup = userPartService.findUserPartGoods(entityId, 5, "id", "title",
									"image");
							map.put("userParts", eup);
						}

					}
					//评论数
					Long commentNum=commentNumByEntityId(tname, entityId);
					//第一页且总的一级评论数大于10才会有最热评论
					if(startId < 1){
						// 最热评论
						Map mapz = findHotMainCommentList(tname, entityId, 0, 20);
						List<Comment> hotAllList = getMainList((List<Comment>) mapz.get("hotList"), tname, sublimit, userId);
						map.put("hotList", hotAllList);
					}

					// 最新评论
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
	 * 获取一级评论下的子评论和子评论用户信息,用户和评论的关系
	 * @param mainList
	 * @param tname
	 * @param sublimit
	 * @param userId
	 * @return
	 */
	public List<Comment> getMainList(List<Comment> mainList, String tname, int sublimit, long userId){
		if (mainList == null)
			mainList = new ArrayList<Comment>();
		// 获取子评论
		List<Long> ids = new ArrayList<Long>();
		List<Comment> subCms = new ArrayList<Comment>();
		// 评论当前用户是否评论
		for (Comment c : mainList) {
			ids.add(c.getId());
			// 每次哪一条 分多次
			List<Comment> sc = findSubCommentsByMainId(tname, c.getId(), sublimit);
			subCms.addAll(sc);
		}
		// 用户查询子评论
		if (subCms == null)
			subCms = new ArrayList<Comment>();
		// 获取子评论用户
		// 查询用户信息
		List<Comment> allCms = new ArrayList<Comment>();
		allCms.addAll(mainList);
		allCms.addAll(subCms);
		findCommentUserInfo(userId, allCms);

		// 封装子集合
		for (Comment m : mainList) {
			m.addSubComment(subCms);
		}
		return mainList;
	}

	/**
	 * 获取相应的肤质集合
	 * @param skinResults
	 * @return
	 */
	public List skinType(String skinResults){
		String skinType=skinResults.substring(0,1);
		Map map=new HashMap();
		//获取相应的map
		if("O".equals(skinType)){
			map=CommenMeta.SKIN_O;
		}else if("D".equals(skinType)){
			map=CommenMeta.SKIN_D;
		}
		//map转list
		List<String> mapKeyList = new ArrayList<String>(map.keySet());
		return mapKeyList;
	}

	/**
	 * 查询主评论
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
		// 产品特有
		if ("goods".equals(tname)) {
			boolean flag = false;
			// 公共部分
			Criteria goodsCri = Criteria.where("entityId").is(entityId);

			// 好用
			if (type == 2) {
				goodsCri.and("score").gte(4).lte(5);
				flag = true;
				// 一般
			} else if (type == 3) {
				goodsCri.and("score").gte(2).lte(3);
				flag = true;
				// 不好用
			} else if (type == 4) {
				goodsCri.and("score").is(1);
				flag = true;
			}

			Query goodsQuery = new Query(goodsCri.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0),
					Criteria.where("pid").exists(false))).skip(skinid).limit(pageSize);
			// 点赞数+评分+时间
			goodsQuery.with(new Sort(Direction.DESC, "score")).with(new Sort(Direction.DESC, "likeNum"))
					.with(new Sort(Direction.DESC, "createStamp"));

			// 查询各种type的数量
			Long total = 0L;
			for(int i=1;i<5;i++){
				Criteria goodsCri2 = Criteria.where("entityId").is(entityId);
				Query goodsQuery2 = new Query(goodsCri2.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0),
						Criteria.where("pid").exists(false))).skip(skinid).limit(pageSize);
				// 点赞数+评分+时间
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
					// 一般
				} else if (i == 3) {
					goodsCri2.and("score").gte(2).lte(3);
					total = mongoTemplate.count(goodsQuery2, actionType);
					map.put("count"+i, total);
					// 不好用
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

		// 同肤质
		if (type == 1 && !StringUtils.isBlank(skinResults)) {
			//cr.and("skinResults").is(skinResults);
			query.with(new Sort(new Order(Direction.ASC, skinResults)));
			Long total = mongoTemplate.count(query, actionType);
			map.put("count"+type, total);
		} else{
			//没肤质时默认的排序
			query.with(new Sort(Direction.DESC, "isEssence")).with(new Sort(Direction.DESC, "likeNum"))
					.with(new Sort(Direction.DESC, "id"));
		}

		List<Comment> ls = mongoTemplate.find(query.skip(skinid).limit(pageSize), Comment.class, actionType);
		map.put("list", ls);
		return map;

	}

	/**
	 * 最热一级评论
	 *
	 * @return
	 */
	public Map findHotMainCommentList(String tname, long entityId, long startId, int pageSize) {
		String actionType = "entity_comment_" + tname;
		Criteria cr = Criteria.where("entityId").is(entityId);
		Query query = new Query(
				cr.and("hidden").is(0).and("content").exists(true).orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false)))
				.skip((int)startId).limit(pageSize);
		//精华点评倒叙
		query.with(new Sort(new Order(Direction.DESC, "isEssence")));
		//点赞数量
		query.with(new Sort(new Order(Direction.DESC, "likeNum")));
		//评论实体
		List<Comment> ls = mongoTemplate.find(query, Comment.class, actionType);
		Map map=new HashMap();
		map.put("hotList", ls);
		return map;

	}

	/**
	 * 最新一级评论
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
		//数量
		query.with(new Sort(new Order(Direction.DESC, "id")));
		//评论实体
		List<Comment> ls = mongoTemplate.find(query, Comment.class, actionType);
		return ls;

	}

	/**
	 * 查询主评论
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
	 * 某个实体的一级评论数(包含只有评星的)
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
	 * 查询评论用户的信息
	 *
	 * @param userId
	 *            登录用户的id
	 * @param userId
	 *            评论用户的id
	 * @param allCms
	 *            评论s
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

				//img检查地址
			}
		// 排出重复
		for (Map.Entry<Long, String> entry : users.entrySet()) {
			userIds.append(",").append(entry.getKey());
		}

		// 查询用户信息
		if (!StringUtils.isBlank(userIds.toString())) {
			String userIdss = userIds.substring(1);
			ReturnData rd = userService.findUserinfoByIds(userIdss);
			if (rd.getRet() == 0) {
				List<UserInfo> userInfos = (List<UserInfo>) rd.getResult();
				for (Comment c : allCms) {
					boolean flag1 = true;
					boolean flag2 = true;
					// 用户自己是否点赞过
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
						// 对应用户信息
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
	 * 查询主评论
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
	 * 查询单个实体的评论总数
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
	 * 2.5以前的接口查询评论关系 不带子评论
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
					// 获取主评论
					List<Comment> ls = findMainCommentList(tname, entityId, type, skinResults, startId, pageSize);
					if (ls == null)
						ls = new ArrayList<Comment>();
					// 评论当前用户是否评论
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
					// 用户信息的封装
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
	 * 获取单个一级评论的子评论与子评论的数量
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
	 * 管理员回复
	 *
	 * @param tname
	 * @param commentid
	 * @param replyUserId
	 * @param replyContent
	 * @return
	 */
	@Deprecated
	public ReturnData backReplyComment(String tname, long commentid, long replyUserId, String replyContent, String redirect_type,
                                       //跳转的页面
                                       String page,
                                       String params, Integer newType) {

		try {
			replyUserId = 248660;
			ReturnData<UserInfo> rd = userService.getUserById(replyUserId);
			if (rd.getRet() != 0)
				return ReturnData.ERROR;
			UserInfo replyUser = rd.TResult();

			// 验证tname
			String actionType = "entity_comment_" + tname;

			if (StringUtils.isBlank(replyContent))
				return Comment.ERROR_CONTENT_NOTNULL;

			// 查询
			Comment cmt = getCommentById(tname, commentid);
			SubComment subcmt = new SubComment();
			subcmt.setContent(replyContent);
			subcmt.setUserId(replyUserId);
			subcmt.setSubCommentId(UUID.randomUUID().toString());
			subcmt.setNickname(replyUser.getNickname());
			// 产品产品名称
			EntityBase eb = this.getEntityById(tname, cmt.getEntityId());
			if (eb == null)
				return ReturnData.ERROR;
			mongoTemplate.updateFirst(new Query(new Criteria().where("id").is(commentid)),
					new Update().addToSet("subComments", subcmt), Comment.class, actionType);
			// 发送消息
			Integer msgType = 1;
			String title = "";
			CommenMeta.MessageStatus msgDesc = CommenMeta.MessageStatus.getStatusByKey("reply-comment_" + tname);
			String content = msgDesc.managerReply(cmt.getCreateStamp(), replyUser.getNickname(), eb.getTitle(),
					replyContent);
			List<Long> receiverIds = new ArrayList<Long>();
			receiverIds.add(cmt.getUserId());
			messageService.sendSynMessage(replyUserId, receiverIds, msgType, msgDesc.getDescription(), title, content,redirect_type,page,params,newType);

			// 清除评论缓存
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
	 * 发评论
	 *
	 * @param tname
	 *            类型
	 * @param entityId
	 *            实体id
	 * @param userInfo
	 *            发送用户
	 * @param content
	 *            内容
	 * @param score
	 *            评分
	 * @param image
	 *            图片
	 * @return true成功 false评论过
	 */
	public ReturnData sendComment(String tname, long entityId, UserInfo userInfo, String content, int score,
								  String image) {

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
			// 过滤词
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

			// 建立评论实体
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
				// 积分
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
	 * 用户评论点赞
	 *
	 * @param tname
	 *            类型
	 * @return true 成功 flase不能重复点赞
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

			//查询被点赞的评论
			Comment comment = mongoTemplate.findOne(new Query(Criteria.where("id").is(commentId).and("hidden").is(0)), Comment.class,
					actionType);
			//如果评论存在
			if(null!=comment && comment.getId()>0){
				//当前用户是否点赞过
				Comment eb = mongoTemplate.findOne(query, Comment.class, actionType);
				if (eb == null) {
					//用户点赞
					mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)),
							new Update().addToSet("commentLikes", cl), actionType);
					// 点赞
					this.objectIncById(commentId, actionType, "likeNum", 1);
					// 评论计数
					this.objectIncById(comment.getUserId(), "user_info", "commentLikeNum", 1);
					// 发送消息
					// messaegService.commentLikeMessage(userId, comment.getUserId()
					// + "", tname);
					//点赞了的用户不能再发送信息
					Query q=new Query(Criteria.where("msgExt.cId").is(comment.getId()).and("msgExt.rUserId").is(userInfo.getId()));
					HashMap mes = mongoTemplate.findOne(q, HashMap.class, "user_message");
					if(mes==null) {
						EntityBase enty = mongoTemplate.findOne(new Query(Criteria.where("id").is(comment.getEntityId())), EntityBase.class, "entity_"+tname);
						String msgCode="msg-comment_like_"+tname;
						String msgField="commentLike";
						messageService.sendEntitySynMessage(userInfo.getId(), comment.getUserId(),msgCode,msgField, MsgExtCommentLike.createCommentLikeMsg(tname, comment, enty, userInfo));
					}
					//清除点赞的评论的当前页的缓存
					//this.setCommentListLike(comment, tname, userInfo, 1,pager,pageSize, type);
					return new ReturnData(1, "点赞成功");
				} else {
					//取消点赞
					Update update = new Update();
					update.pull("commentLikes", new BasicDBObject("userId", userId));
					mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)), update, actionType);

					this.objectIncById(commentId, actionType, "likeNum", -1);

					this.objectIncById(comment.getUserId(), "user_info", "commentLikeNum", -1);

					//this.setCommentListLike(comment, tname, userInfo, 0,pager,pageSize, type);
					return new ReturnData(2, "取消点赞成功");
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
	 * 及时更新评论列表的点赞信息
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
					//子评论点赞
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
					//一级评论点赞
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
	public ReturnData sendSubComment3(String tname, long commentId, long pid, long userId, String content,
                                      String image) {
		try {
			String key = "notkeyword";
			// 过滤词
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
	 * 查询我的评论列表
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
				// 隐藏默认字段
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
	 * 查询我的评论列表
	 * 包含对比和福利社---
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
				//对比讨论
				actionType="entity_discuss_"+tname;
				map = entityService.findEntityActionType(actionType, tname, userId, Discuss.class, startId, pageSize);
			}else{
				map = entityService.findEntityActionType(actionType, tname, userId, Comment.class, startId, pageSize);
			}
			//
			if (map != null) {
				// 隐藏默认字段
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
	 * 评论更新
	 *
	 * @param tname
	 * @param userId
	 *            用户
	 * @param content
	 *            内容
	 * @return
	 */
	public ReturnData updateComment(String tname, long userId, long commentId, String content, String image, String[] images, Boolean addScore) {
		UserInfo userInfo=new UserInfo();
		userInfo=mongoTemplate.findOne(new Query(Criteria.where("id").is(userId)), UserInfo.class,"user_info");
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

		return updateCmt(tname, userId, commentId, content, image, images,addScore);
	}

	/**
	 * 评论更新
	 *
	 * @param tname
	 * @param userId
	 *            用户
	 * @param content
	 *            内容
	 * @return
	 */
	public ReturnData updateCmt(String tname, long userId, long commentId, String content, String image,String[] images,Boolean addScore) {

		Comment comment = new Comment();
		try {
			if(StringUtils.isBlank(content)){
				//新的评论内容不能为空
				return Comment.ERROR_CONTENT_NOTNULL;
			}

			String actionType = "entity_comment_" + tname;
			// 违禁词判断
			String key = "notkeyword";
			// 过滤词
			String value = entityService.getConfig(key);
			if (!StringUtils.isBlank(value)) {
				content = entityService.keywordInfiltration2(content, value);
			}

			// 判断是否为精华(isEssence)
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
					//删除图片
					u.set("images", null);
					u.set("image", null);
					comment.setImages(null);
					comment.setImage(null);
				}
			}

			mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)), u, actionType);
			this.cleanComment(tname, comment.getEntityId(), null);

			if(null!=addScore && addScore){
				//添加修行值
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
	 * 评论更新
	 *
	 * @param tname
	 * @param userId
	 *            用户
	 * @param content
	 *            内容
	 * @return
	 */
	public ReturnData updateComment2(String tname, long userId, long commentId, String content, String image, String[] images, Boolean addScore) {
		UserInfo userInfo=new UserInfo();
		if("apply_goods".equals(tname)){
			tname="apply_goods2";
		}
		userInfo=mongoTemplate.findOne(new Query(Criteria.where("id").is(userId)), UserInfo.class,"user_info");
		//手机绑定
		ReturnData rd1=this.switchOfPhoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//身份证验证
		ReturnData rd=this.verifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}

		return this.updateCmt(tname, userId, commentId, content, image, images,addScore);

	}

	/**
	 * 删除评论
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
	 * 删除评论
	 *
	 * @param tname
	 * @param userId
	 *            用户
	 * @param entityId
	 *            实体id
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
	 * 评论评分
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
		//标签
		if(null!=tags && tags.size()>0){
			cmt.setTags(tags);
		}
		//评分数量
		mongoTemplate.save(cmt, actionType);
		cmt.setHiddenFeild();
		return new ReturnData(cmt);
	}











	/**
	 * v3.2
	 * 发送主评论
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
		//手机绑定
		ReturnData rd1=this.switchOfPhoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//实名制认证
		ReturnData rd=this.verifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}

		rd=sendMainComment(tname, entityId, pid, userInfo, content, score, image, tags, images,reason);
		return rd;
	}

	/**
	 * v3.2之前
	 * 发送主评论
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

		//手机绑定
		ReturnData rd1=this.oldSwitchOfphoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//身份证验证
		ReturnData rd=this.oldVerifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}

		rd=sendMainComment(tname, entityId, pid, userInfo, content, score, image, tags, images,null);
		return rd;
	}

	/**
	 * v3.2之前
	 * 发送主评论
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
		// 自己发表一级评论
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
		//清除相关的缓存
		cleanComment(tname, entityId, null);

		//心得的一级评论,给心得创建者发送信息
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
	 * 发送子评论
	 * @param tname
	 * @param entityId
	 * @param pid
	 * @param userInfo
	 * @param content
	 * @param image
	 * @return
	 */
	public ReturnData sendSub(String tname, long entityId, long pid, UserInfo userInfo, String content, String image){
		//手机认证
		ReturnData rd1=this.oldSwitchOfphoneCheck(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		//实名认证接口
		ReturnData rd=this.oldVerifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}

		return this.sendSubCmt(tname, entityId, pid, userInfo, content, image);

	}

	/**
	 * 发送子评论
	 * @param tname
	 * @param entityId
	 * @param pid
	 * @param userInfo
	 * @param content
	 * @param image
	 * @return
	 */
	public ReturnData sendSub3_2(String tname, long entityId, long pid, UserInfo userInfo, String content, String image){
		//手机认证
		ReturnData rd=this.switchOfPhoneCheck(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}
		//实名制认证
		ReturnData rd1=this.verifyState(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}

		return this.sendSubCmt(tname, entityId, pid, userInfo, content, image);

	}

	/**
	 * 发送子评论
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
		// 查询上级评论
		Comment pcmt = mongoTemplate.findOne(new Query(Criteria.where("id").is(pid)), Comment.class, actionType);
		if (pcmt != null && pcmt.getHidden() == 0) {
			EntityBase enty = this.getEntityById(tname, entityId);
			Long cmid = this.getId(actionType);
			Comment cmt = new Comment();
			cmt.setId(cmid);
			// 上级用户
			cmt.setpUserId(pcmt.getUserId());
			cmt.setPid(pid);
			long mainId = 0;
			long mainUserId = 0;
			if ((pcmt.getPid() == null || pcmt.getPid() == 0)) {
				// 上级是一级评论
				mainId = pcmt.getId();
				mainUserId = pcmt.getUserId();
			} else {
				// 上级不是一级评论 通过上级评论获取主评论信息
				mainId = pcmt.getMainId();
				mainUserId = pcmt.getMainUserId();
			}
			cmt.setMainId(mainId);
			cmt.setMainUserId(mainUserId);
			// 一级评论的评论数++
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
			// 发送消息
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
			// 是否可以评论
			if (enty.getAllowComment() != null && enty.getAllowComment() == 1) {
				return Comment.NOT_ALLOW_COMMENT;
			}
			//是否需亚关键字过滤
			if (isvid) {
				// 过滤词
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
					//是否限制发送频率
					ReturnData rd=validateService.vSendTime(userId, actionType);
					if(rd.getRet()!=0) return rd;
				}
			}

			//旧评论的图片处理
			String[] images=null;
			if(StringUtils.isNotBlank(image)){
				images=image.split(",");
			}
			if (pid <= 0) {
				//发送主评论
				Comment mainCmt = mongoTemplate.findOne(
						new Query(Criteria.where("entityId").is(entityId).and("userId").is(userId)
								.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
						Comment.class, actionType);

				if (mainCmt != null) {
					mainId=mainCmt.getId();
					//1、评分了没有发送评论
					if(mainCmt.getScore()>0&&tname.equals("goods")){
						//添加修行值
						if(StringUtils.isBlank(mainCmt.getContent())) {
							return this.updateComment(tname, userId, mainCmt.getId(), content, image,images,true);
						}
					}
					return Comment.REVER_COMMENT;
				} else {
					//评论计数全部错误记入了产品  todo  需要重新同步产品评论数！！！！
// 					this.objectIncById(entityId, "entity_goods", "commentNum", 1);
					this.objectIncById(entityId, "entity_" + tname, "commentNum", 1);

					//2、直接评分的情况
					if (StringUtils.isBlank(content)&&score>0&&tname.equals("goods")) {
						//直接评分
						return this.commentScore(tname, entityId, userInfo, score,null);
					}

					if(score<0&&tname.equals("goods")) {
						return Comment.SCORE_GT_0;
					}

					//清除首页内容推荐的产品评论评分人数
					//cacheService.cleanCacheListByKey(CACHE_NAME.INSTANCE_INDEX_6_PREFIX);

					//3、评分和发评论同时
					return this.sendMain(tname, entityId, pid, userInfo, content, score, image,null,images);
				}
			} else {
				//发子评论
				return this.sendSub(tname, entityId, pid, userInfo, content, image);
			}
			//清除相关的缓存
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
			// 是否可以评论
			if (enty.getAllowComment() != null && enty.getAllowComment() == 1) {
				return Comment.NOT_ALLOW_COMMENT;
			}
			//是否需亚关键字过滤
			if (isvid) {
				// 过滤词
				String value = entityService.getConfig(key);
				if (!StringUtils.isBlank(value)) {
					content = entityService.keywordInfiltration2(content, value);
				}
			}

			List<Tag> tagList=new ArrayList<Tag>();

			//标签
			JSONArray  commentTags  =null;
			if(StringUtils.isNotBlank(tags)){
				commentTags=JSONArray.fromObject(tags);
				//解析标签
				for(int i=0;i<commentTags.size();i++) {
					JSONObject obj=(JSONObject) commentTags.get(i);
					if(obj.getLong("id")>0&&StringUtils.isNotBlank(obj.getString("title"))) {
						tagList.add(new Tag(obj.getLong("id"),obj.getString("title")));
					}
				}
			}

			//图片处理,兼容老的
			String[] imagess=null;
			if(StringUtils.isNotBlank(images)){
				//完整路径处理,保存图片名
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
			//福利社的评论不限制发送频率
			if (isvid && !"apply_goods2".equals(tname)) {
				if (!(pid <= 0&&StringUtils.isBlank(content)&&score>0&&"goods".equals(tname))) {
					//是否限制发送频率
					ReturnData rd=validateService.commentSendTime(userId, actionType);
					if(rd.getRet()!=0) return rd;
				}
			}
			if (pid <= 0) {
				//发送主评论
				Comment mainCmt = mongoTemplate.findOne(
						new Query(Criteria.where("entityId").is(entityId).and("userId").is(userId)
								.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
						Comment.class, actionType);

				if (mainCmt != null && !"apply_goods2".equals(tname)) {
					mainId=mainCmt.getId();
					//1、评分了没有发送评论
					if(mainCmt.getScore()>0&&tname.equals("goods")){
						//
						if(StringUtils.isBlank(mainCmt.getContent())) {
							return this.updateComment2(tname, userId, mainCmt.getId(), content, image,imagess,true);
						}
					}
					return Comment.REVER_COMMENT;
				} else {
					//评论计数全部错误记入了产品  todo  需要重新同步产品评论数！！！！
// 					this.objectIncById(entityId, "entity_goods", "commentNum", 1);
					this.objectIncById(entityId, "entity_" + tname, "commentNum", 1);

					//2、直接评分的情况,带标签
					if (StringUtils.isBlank(content)&&score>0&&tname.equals("goods")) {
						//直接评分
						return this.commentScore(tname, entityId, userInfo, score,tagList);
					}

					if(score<0&&tname.equals("goods")) {
						return Comment.SCORE_GT_0;
					}

					//清除首页内容推荐的产品评论评分人数
					//cacheService.cleanCacheListByKey(CACHE_NAME.INSTANCE_INDEX_6_PREFIX);

					//3、评分和发评论同时
					return this.sendMain3_2(tname, entityId, pid, userInfo, content, score, image,tagList,imagess,reason);
				}
			} else {
				//发子评论
				return this.sendSub3_2(tname, entityId, pid, userInfo, content, image);
			}
			//清除相关的缓存
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

	//管理员回复标记
	public void replyTpag(long id, String tname) {
		String actionType="entity_comment_"+tname;
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)),new Update().set("isComment", 1),Comment.class, actionType);
		
		
	}

	/**
	 * 删除用户评论
	 * @param tname
	 * @param userId
	 * @param commentId
	 * @return
	 */
	public ReturnData hiddenComment(String tname, long userId, long commentId) {
		Comment comment=new Comment();
		//是否为精华评论
		String actionType = "entity_comment_" + tname;
		Query query = new Query(new Criteria().where("id").is(commentId).and("userId").is(userId));
		comment = mongoTemplate.findOne(
				query, Comment.class,actionType);
		if (null == comment)
			return ReturnData.ERROR;
		if (comment.getIsEssence() != null && comment.getIsEssence() == 1) {
			return Comment.UNABLE_DELETE_ISESSENCE;
		}
		//删除评论
		Update update=new Update().set("hidden", 2);
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId).and("userId").is(userId)), update, actionType);
		return ReturnData.SUCCESS;
	}
}