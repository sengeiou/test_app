package cn.bevol.internal.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import cn.bevol.log.LogMethod;
import com.io97.utils.DateUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.io97.cache.redis.RedisCacheProvider;

import cn.bevol.constant.CommenMeta;
import cn.bevol.model.user.UserInfo;
import cn.bevol.mybatis.dao.UserInfoMapper;
import cn.bevol.model.entity.EntityCompare;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.model.entity.EntityUserPart;
import cn.bevol.model.entityAction.Comment;
import cn.bevol.mybatis.dao.IndexMapper;
import cn.bevol.mybatis.dto.EssenceComment;
import cn.bevol.entity.service.BaseService;
import cn.bevol.entity.service.IndexService;
import cn.bevol.entity.service.SearchService;
import cn.bevol.log.LogException;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ReturnData;
import flexjson.JSONDeserializer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 产品标签相关的业务
 * @author chenHaiJian
 *
 */

@Service
public class BackIndexRecommendService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(IndexService.class);


	@Autowired
	private IndexMapper indexMapper;

	@Autowired
	private UserInfoMapper userInfoMapper;


	private static String tname = "tname";




	/**
	 * 缓存到mongo
	 *
	 * @param pager
	 * @param createStamp
	 * @param state
	 * @return
	 */
	public ReturnData recommendToMongo(Integer pager, Long searchTime, Integer state) {
		try {

			int defSize = 10;
			int cal = pager / defSize;
			int pageSize = 4;
			Map allMap = new HashMap();
			List list = new ArrayList();
			Map map = new HashMap();
			// 1、产品点击数越高前端展示越靠前，如果相同咋产品用户评分越高越靠前，如果评分也一样，则产品评论越多越靠前，如果评论也一样，则按照字母a-z排序；
			// 1 todo 按照字母a-z排序
			List<EntityGoods> listGoods = mongoTemplate.find(
					new Query(Criteria.where("hidden").is(0).and("deleted").is(0).and("hiddenSkin").ne(10))
							.with(new Sort(Direction.DESC, "hitNum")).with(new Sort(Direction.DESC, "grade"))
							.with(new Sort(Direction.DESC, "commentNum")).limit(pageSize * defSize * cal),
					CommenMeta.ENTITY_ACTION.get("entity_goods"), "entity_goods");
			List newListGoods = new ArrayList();
			for (EntityGoods eg : listGoods) {
				JSONObject obj = JSONObject.fromObject(eg);
				// 有多少人评分
				long scoreNum = mongoTemplate.count(new Query(
								Criteria.where("entityId").is(eg.getId()).and("score").gt(0).and("hidden").is(0).and("deleted")
										.is(0).orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
						Comment.class, "entity_comment_goods");
				obj.put("userCommentNum", scoreNum);
				if (StringUtils.isNotBlank(eg.getImage())) {
					// 产品图片路径拼接
					String imageSrc = CommonUtils.getImageSrc("goods", eg.getImage());
					obj.put("imageSrc", imageSrc);
				}
				obj.put(this.tname, "goods");
				newListGoods.add(obj);
			}
			// 2、产品评论点赞数越多展示越靠前，如果相同则评论发布时间越晚越靠前；
			Criteria commenCri = Criteria.where("hidden").is(0).and("deleted").is(0);
			if (null != searchTime && searchTime > 0) {
				commenCri.and("createStamp").lte(searchTime);
			}
			List<Comment> listComment = mongoTemplate.find(
					new Query(commenCri).with(new Sort(Direction.DESC, "likeNum"))
							.with(new Sort(Direction.DESC, "createStamp")).limit((pageSize - 1) * defSize * cal),
					Comment.class, "entity_comment_goods");
			List newListComment = new ArrayList();
			if (null != listComment && listComment.size() > 0) {
				for (Comment comment : listComment) {
					Map jsonComment = new HashMap();
					List<UserInfo> user=userInfoMapper.findUserinfoByIds(comment.getUserId()+"");
					//用户头像
					jsonComment.put("headImgUrl", user.get(0).getHeadimgurl());
					//用户昵称
					jsonComment.put("nickName", user.get(0).getNickname());
					int entityId = Integer.parseInt(comment.getEntityId() + "");
					// 查找产品
					EntityGoods entityGoods = (EntityGoods) mongoTemplate.findOne(
							new Query(Criteria.where("hidden").is(0).and("deleted").is(0).and("id").is(entityId)),
							CommenMeta.ENTITY_ACTION.get("entity_goods"), "entity_goods");

					jsonComment.put(this.tname, "comment_goods");

					if (null != entityGoods) {
						long scoreNum = mongoTemplate.count(
								new Query(Criteria.where("entityId").is(entityGoods.getId()).and("score").gt(0)
										.and("hidden").is(0).and("deleted").is(0)
										.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
								Comment.class, "entity_comment_goods");
						jsonComment.put("userCommentNum", scoreNum);
						// jsonComment.put(this.tname,
						// entityGoods.getCommentNum());
						// 评论内容优先级: 1精选点评,2精华点评,3点赞数
						String actionType = "entity_comment_goods";
						// 根据产品id获取精选点评

						EssenceComment ec = indexMapper.findByGoodsId(entityGoods.getId());
						//读缓存获取精选点评
						/*ReturnData rd=goodsService.getGoodsExplain(entityGoods.getMid(),null);
						GoodsExplain goodsExplain=(GoodsExplain)rd.getResult();
						Doyen doyen=null;
						if(null!=goodsExplain&& null!=goodsExplain.getGoods()){
							doyen=goodsExplain.getGoods().getDoyen();
						}*/
						if (null != ec && StringUtils.isNotBlank(ec.getContent())) {
							// 1精选点评
							//内容解码
							jsonComment.put("content", StringEscapeUtils.unescapeJava(ec.getContent()));
							jsonComment.put("commentId", ec.getTypeId());
							// 获取评论的星级
							Comment cmt = mongoTemplate.findOne(new Query(Criteria.where("id").is(ec.getTypeId())),
									Comment.class, actionType);
							if (null != cmt && null != cmt.getScore() && cmt.getScore() != 0) {
								jsonComment.put("score", cmt.getScore());
							}
						} else {
							// 2精华点评,3点赞数排序
							Criteria cr = Criteria.where("entityId").is(entityId);
							Query query = new Query(cr.and("hidden").is(0).and("content").exists(true)
									.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false)));
							// 精华点评倒叙
							query.with(new Sort(Direction.DESC, "isEssence"));
							// 点赞数量
							query.with(new Sort(Direction.DESC, "likeNum"));
							List<Comment> ls = mongoTemplate.find(query.limit(1), Comment.class, actionType);
							// 排序后的评论
							Comment cmt = null;
							if (null != ls && ls.size() > 0) {
								cmt = ls.get(0);
							}
							if (null != cmt && StringUtils.isNotBlank(cmt.getContent())) {
								jsonComment.put("content", cmt.getContent());
								if (null != cmt.getScore() && cmt.getScore() != 0) {
									jsonComment.put("score", cmt.getScore());
								}
								jsonComment.put("commentId", cmt.getId());
							}
						}

						// 添加产品信息
						jsonComment.put("entityId", entityGoods.getId());
						jsonComment.put("entityMid", entityGoods.getMid());
						jsonComment.put("entityTitle", entityGoods.getTitle());
						jsonComment.put("grade", entityGoods.getGrade());
						jsonComment.put("safety_1_num", entityGoods.getSafety_1_num());
						jsonComment.put("entityImage", CommonUtils.getImageSrc("goods", entityGoods.getImage()));
						// System.out.println("pager:"+i+",skip:"+skip+",limit:"+(pageSize-1)+",entityId:"+entityGoods.getId()+",entityTitle:"+entityGoods.getTitle()+",commentId:"+jsonComment.get("commentId"));
					}
					newListComment.add((Map) jsonComment);
				}
			}
			// 3、心得查看数越过展示越靠前，如果相同则心得发布时间越晚越靠前。
			Criteria userPartCri = Criteria.where("hidden").is(0).and("deleted").is(0);
			if (null != searchTime && searchTime > 0) {
				userPartCri.and("createStamp").lte(searchTime);
			}
			List<EntityUserPart> newListUserPart = mongoTemplate.find(
					new Query(userPartCri).with(new Sort(Direction.DESC, "hitNum")).with(new Sort(Direction.DESC, "createStamp"))
							.limit((pageSize - 1) * defSize * cal),
					CommenMeta.ENTITY_ACTION.get("entity_user_part_lists"), "entity_user_part_lists");
			for (EntityUserPart eup : newListUserPart) {
				eup.setTname("userPart");
			}

			// 去重处理,产品和产品评论--以产品为主
			/*
			 * for(int i=0;i<newListGoods.size();i++){ Map
			 * goodsMap=(Map)newListGoods.get(i); String
			 * goodsId=goodsMap.get("id")+""; for(int
			 * j=0;j<newListComment.size();j++){ Map
			 * commentMap=(Map)newListComment.get(j); String
			 * entityId=commentMap.get("entityId")+"";
			 * if(goodsId.equals(entityId)){ newListComment.remove(j); } } }
			 */

			// 分页处理
			for (int i = 0; i < pager; i++) {
				List allList = new ArrayList();
				List subComment = new ArrayList();
				if (newListComment.size() >= (i + 1) * (pageSize - 1)) {
					subComment = newListComment.subList(i * (pageSize - 1), (i + 1) * (pageSize - 1));
				}
				List subGoods = new ArrayList();
				if (newListGoods.size() >= (i + 1) * pageSize) {
					subGoods = newListGoods.subList(i * pageSize, (i + 1) * pageSize);
				}
				List subUserPart = new ArrayList();
				if (newListUserPart.size() >= (i + 1) * (pageSize - 1)) {
					subUserPart = newListUserPart.subList(i * (pageSize - 1), (i + 1) * (pageSize - 1));
				}

				if (subComment.size() > 0) {
					allList.add(subComment.get(0));
				}
				if (subGoods.size() > 0) {
					allList.add(subGoods.get(0));
				}
				if (subUserPart.size() > 0) {
					allList.add(subUserPart.get(0));
				}
				if (subGoods.size() > 1) {
					allList.add(subGoods.get(1));
				}
				if (subGoods.size() > 2) {
					allList.add(subGoods.get(2));
				}
				if (subComment.size() > 1) {
					allList.add(subComment.get(1));
				}
				if (subUserPart.size() > 1) {
					allList.add(subUserPart.get(1));
				}
				if (subComment.size() > 2) {
					allList.add(subComment.get(2));
				}
				if (subGoods.size() > 3) {
					allList.add(subGoods.get(3));
				}
				if (subUserPart.size() > 2) {
					allList.add(subUserPart.get(2));
				}
				allMap.put("pager_" + (i + 1), allList);
			}

			allMap.put("state", state);
			allMap.put("searchTime", searchTime);
			allMap.put("createStamp", new Date().getTime() / 1000);
			// mongoTemplate.save(allMap,"recommend");
			return new ReturnData(allMap);
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "IndexService.recommendToMongo");
			map.put("pager", pager);
			map.put("state", state);
			map.put("searchTime", searchTime);
			new LogException(e);
		}
		return null;
	}




	/*
	 * 瀑布流发送200
	 */
	public ReturnData sendRecommend(Long searchTime) {
		List alllist = new ArrayList();
		Map map = new HashMap();
		try {
			Integer pager = 20;
			Integer state = 1;
			ReturnData rd = recommendToMongo(pager, searchTime, state);
			Map allmap = (Map) rd.getResult();
			for (int i = 1; i <= 20; i++) {
				List pagelist = new ArrayList();
				pagelist = (List) allmap.get("pager_" + i);
				for (int j = 0; j < pagelist.size(); j++) {
					alllist.add(pagelist.get(j));
				}
			}
			map.put("content", alllist);
			map.put("state", allmap.get("state"));
			map.put("createStamp", allmap.get("createStamp"));
			map.put("searchTime", searchTime);
		} catch (Exception e) {
			e.printStackTrace();
			Map emap=new HashMap();
			emap.put("method", "indexService.sendRecommend()");
			emap.put("searchTime", searchTime);
			new cn.bevol.log.LogException(e,emap);
		}
		return new ReturnData(map);
	}




	/*
	 * 获取实体内容
	 */
	public ReturnData getEntity(String tname, Long id) {
		Map entityMap = new HashMap();
		try {
			Query query = new Query(Criteria.where("id").is(id));

			// 字段拼接
			if (tname.equals("userPart")) {
				tname = "user_part_lists";
				entityMap = mongoTemplate.findOne(query, HashMap.class, "entity_" + tname);
				entityMap.put("tname","userPart");
				entityMap.put("imgSrc",CommonUtils.getImageSrc("user_part/lists", (String) entityMap.get("image")));
			} else if (tname.equals("goods")) {
				EntityGoods entityGoods = (EntityGoods) mongoTemplate.findOne(new Query(Criteria.where("id").is(id)),
						CommenMeta.ENTITY_ACTION.get("entity_goods"), "entity_goods");
				JSONObject obj=JSONObject.fromObject(entityGoods);

				long scoreNum = mongoTemplate.count(new Query(Criteria.where("entityId").is(entityGoods.getId())
								.and("score").gt(0).and("hidden").is(0).and("deleted").is(0)
								.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))), Comment.class,
						"entity_comment_goods");
				obj.put("userCommentNum", scoreNum);
				obj.put("tname", "goods");
				// 路径拼接
				String imageSrc = CommonUtils.getImageSrc("goods", entityGoods.getImage());
				obj.put("imageSrc", imageSrc);
				return new ReturnData(obj);
			} else if (tname.equals("compare_goods")) {
				EntityCompare ec=mongoTemplate.findOne(query, EntityCompare.class,"entity_"+tname);
				EntityGoods entityGoods1 = (EntityGoods) mongoTemplate.findOne(new Query(Criteria.where("id").is(ec.getCid1())),
						CommenMeta.ENTITY_ACTION.get("entity_goods"), "entity_goods");
				EntityGoods entityGoods2 = (EntityGoods) mongoTemplate.findOne(new Query(Criteria.where("id").is(ec.getCid2())),
						CommenMeta.ENTITY_ACTION.get("entity_goods"), "entity_goods");
				entityMap.put("info", ec);
				entityMap.put("imgCid1", CommonUtils.getImageSrc("goods",  entityGoods1.getImage()));
				entityMap.put("imgCid2",CommonUtils.getImageSrc("goods",  entityGoods2.getImage()));
				entityMap.put("visitNum", ec.getVisitNum());
				entityMap.put("titleCid1", entityGoods1.getTitle());
				entityMap.put("titleCid2", entityGoods2.getTitle());
				entityMap.put("tname", "compare_goods");
			}
			
			else if (tname.equals("comment_goods")) {
				Comment comment = mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), Comment.class,
						"entity_comment_goods");
				UserInfo user=userInfoMapper.findFeild("id", comment.getUserId()+"");
				//用户头像
				entityMap.put("headImgUrl", user.getHeadimgurl());
				//用户昵称
				entityMap.put("nickName", user.getNickname());
				long entityId=comment.getEntityId();
				//查找产品
				EntityGoods entityGoods = (EntityGoods) mongoTemplate.findOne(
						new Query(Criteria.where("hidden").is(0).and("deleted").is(0).and("id").is(entityId)),
						CommenMeta.ENTITY_ACTION.get("entity_goods"), "entity_goods");
				if(null != entityGoods){
					long scoreNum = mongoTemplate.count(
							new Query(Criteria.where("entityId").is(entityGoods.getId()).and("score").gt(0)
									.and("hidden").is(0).and("deleted").is(0)
									.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false))),
							Comment.class, "entity_comment_goods");
					entityMap.put("userCommentNum", scoreNum);
					EssenceComment ec = indexMapper.findByGoodsId(entityGoods.getId());
					if (null != ec && StringUtils.isNotBlank(ec.getContent())) {
						// 1精选点评
						entityMap.put("content",StringEscapeUtils.unescapeJava(ec.getContent()));
						entityMap.put("commentId", ec.getTypeId());
						// 获取评论的星级
						Comment cmt = mongoTemplate.findOne(new Query(Criteria.where("id").is(ec.getTypeId())),
								Comment.class, "entity_comment_goods");
						if (null != cmt && null != cmt.getScore() && cmt.getScore() != 0) {
							entityMap.put("score", cmt.getScore());
						}
					} else {
						// 2精华点评,3点赞数排序
						Criteria cr = Criteria.where("entityId").is(entityId);
						Query query1 = new Query(cr.and("hidden").is(0).and("content").exists(true)
								.orOperator(Criteria.where("pid").is(0), Criteria.where("pid").exists(false)));
						// 精华点评倒叙
						query1.with(new Sort(Direction.DESC, "isEssence"));
						// 点赞数量
						query1.with(new Sort(Direction.DESC, "likeNum"));
						List<Comment> ls = mongoTemplate.find(query1.limit(1), Comment.class, "entity_comment_goods");
						// 排序后的评论
						Comment cmt = null;
						if (null != ls && ls.size() > 0) {
							cmt = ls.get(0);
						}
						if (null != cmt && StringUtils.isNotBlank(cmt.getContent())) {
							entityMap.put("content", cmt.getContent());
							if (null != cmt.getScore() && cmt.getScore() != 0) {
								entityMap.put("score", cmt.getScore());
							}
							entityMap.put("commentId", cmt.getId());
						}
					}

					entityMap.put("entityId", entityGoods.getId());
					entityMap.put("entityMid", entityGoods.getMid());
					entityMap.put("entityTitle", entityGoods.getTitle());
					entityMap.put("grade", entityGoods.getGrade());
					entityMap.put("safety_1_num", entityGoods.getSafety_1_num());
					entityMap.put("entityImage", CommonUtils.getImageSrc("goods", entityGoods.getImage()));
					entityMap.put("tname", "comment_goods");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Map emap=new HashMap();
			emap.put("method", "indexService.getEntity()");
			emap.put("tname", tname);
			emap.put("id", id);
			new cn.bevol.log.LogException(e,emap);

		}

		return new ReturnData(entityMap);
	}
	/*
	 * 更新/保存 推荐实体
	 */
	public ReturnData saveEntity(Long createStamp, String content, String pageName) {
		List<Map> msps= new ArrayList();
		JSONArray jsonArray=new JSONArray();
		if (content != null && !content.equals("0")) {
			msps=new JSONDeserializer<ArrayList<Map>>().deserialize(content);
			for(int m = 0;m<msps.size();m++){
				if(msps.get(m)==null ||msps.get(m).isEmpty()){
					msps.remove(msps.get(m));
				}
			}
			//jsonArray = JSONArray.fromObject(content);
			//int a =jsonArray.size();
			//lis = (List) JSONArray.toCollection(jsonArray, Map.class);
		}
		// List li = java.util.Arrays.asList(content);
		// 将list按blockSize大小等分，最后多余的单独一份
		int i = 0;
		int num = 0;
		List pagerList = new ArrayList();
		List reaminList = new ArrayList();
		int liSize = msps.size();
		int blockSize = 10;
		int batchSize = liSize / blockSize;
		int remain = liSize % blockSize;
		Set<String> set = new HashSet();
		Map<String, Object> entity = null;
		Map newentity = new HashMap();
		try {
			Query query = new Query(Criteria.where("createStamp").is(createStamp));
			Update update = new Update();
			entity = (Map<String, Object>) mongoTemplate.findOne(query, Map.class, "recommend");
			if (entity != null && !pageName.equals("0") && msps.size() == 0) {
				entity.put("pageName", pageName);
				mongoTemplate.save(entity, "recommend");
				return ReturnData.SUCCESS;
			}
			if (entity == null && msps != null) {
				newentity.put("state", 1);
				newentity.put("createStamp", createStamp);
				newentity.put("searchTime", "");
				newentity.put("pageName", pageName);
				for (i = 0; i < batchSize; i++) {
					int fromIndex = i * blockSize;
					int toIndex = i * blockSize +blockSize ;
					List nlist=new ArrayList();
					nlist=msps.subList(fromIndex, toIndex);
					//System.out.println(nlist.size()+"-----------");
					newentity.put("pager_" + (i + 1), nlist);

				}
				if (remain > 0) {
					List rlist=msps.subList(liSize - remain, liSize);
					for(int k=0;k<rlist.size();k++){
						reaminList.add(rlist.get(k));
					}
					newentity.put("pager_" + (batchSize + 1), reaminList);
				}
				newentity.put("pageName", pageName);
				mongoTemplate.save(newentity, "recommend");
				return new ReturnData(mongoTemplate.find(query, HashMap.class, "recommend"));
			}
			// 如果查询得到实体非空且content非空 则更新列表
			if (entity != null && msps != null && pageName != null) {
				List<String> keyList = new ArrayList();
				set = entity.keySet();
				keyList = new ArrayList(set);
				//循环将所有pager_删除
				for (int k = 0; k < keyList.size(); k++) {
					String key = keyList.get(k);
					String regEx = "pager_*";
					Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(key);
					boolean rs = matcher.find();
					if (rs == true) {
						String[] page = key.split("_");
						if (page != null) {
							num = Integer.parseInt(page[1]);
							if (num != 0 && num > batchSize)
								entity.remove("pager_" + num);
						}
					}
				}
				//将实体类分页保存
				for (i = 0; i < batchSize; i++) {
					int fromIndex = i * blockSize;
					int toIndex = i * blockSize + blockSize;
					pagerList=msps.subList(fromIndex, toIndex);
					if (pagerList != null && pagerList.size() == 10) {
						int pager=i+1;
						entity.put("pager_" + pager, pagerList);
					}
				}
				//若reaminList非空 则再添加额外的一页
				if (remain > 0) {
					List li = msps.subList(liSize - remain, liSize);
					for (int j = liSize - remain; j < msps.size(); j++) {
						reaminList.add(msps.get(j));
					}
					// reaminList.add(list.subList(liSize-remain,
					// liSize));
					int pager=batchSize+1;
					entity.put("pager_" + pager, reaminList);
				}
				Long updateStamp = new Date().getTime() / 1000;
				entity.put("updateStamp", updateStamp);
				if(!pageName.equals("0")){
					entity.put("pageName", pageName);
				}
				mongoTemplate.save(entity, "recommend");
				return new ReturnData(
						mongoTemplate.find(new Query(Criteria.where("createStamp").is(createStamp)), Map.class, "recommend"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Map emap=new HashMap();
			emap.put("method", "indexService.saveEntity()");
			emap.put("createStamp", createStamp);
			emap.put("content", content);
			emap.put("pageName", pageName);
			new cn.bevol.log.LogException(e,emap);
		}
		return ReturnData.FAILURE;
	}

	/*
	 * 发布
	 */
	public ReturnData publishEntity(Long createStamp, int state, Long publishStamp) {
		try {
			Query query = new Query(Criteria.where("createStamp").is(createStamp));
			Update update = Update.update("state", state).set("publishStamp", publishStamp);
			mongoTemplate.updateFirst(query, update, "recommend");
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			Map emap=new HashMap();
			emap.put("method", "indexService.publishEntity()");
			emap.put("createStamp", createStamp);
			emap.put("state", state);
			emap.put("publishStamp", publishStamp);
			new cn.bevol.log.LogException(e,emap);
		}
		return ReturnData.FAILURE;
	}

	/**
	 * 获取推荐列表
	 *
	 * @param pager
	 * @param pagerName
	 * @return
	 */
	public ReturnData findRecommendList(int pager, String pageName) {
		List<Map> li = new ArrayList<Map>();
		Criteria crt = new Criteria();
		long total = 0;

		try {
			int pagerSize = 30;
			int start = (pager - 1) * pagerSize;
			Query query = Query.query(crt);
			query.fields().include("publishStamp").include("state").include("createStamp").include("pageName");
			query.with(new Sort(Direction.DESC, "createStamp")).with(new Sort(Direction.DESC,"publishStamp")).skip(start).limit(pagerSize);
			List<Map> alllist = (List)mongoTemplate.find(query, HashMap.class, "recommend");
			total=mongoTemplate.count(query,  "recommend");
			long nowTime = new Date().getTime() / 1000;
			long mostnewTime=0;
			int closest=-1;
			for(int i=0;i<alllist.size();i++){
				//Query que=new Query(Criteria.where("createStamp").is(alllist.get(i).get("createStamp")));
				//Map map=mongoTemplate.findOne(que, HashMap.class,"recommend");
				//Long ps=(Long)map.get("publishStamp");

				Long curPublish=(Long)alllist.get(i).get("publishStamp");
				if(mostnewTime==0)  curPublish=(Long)alllist.get(i).get("publishStamp");
				if(curPublish!=null){
					if(curPublish>nowTime){
						alllist.get(i).put("type", 3);
						alllist.get(i).put("typeMsg", "即将发布");
					} else if(mostnewTime<curPublish){
						closest=i;
						mostnewTime=curPublish;
					} else {
						alllist.get(i).put("type", 5);
						alllist.get(i).put("typeMsg", "已发布");
					}
				} else {
					alllist.get(i).put("type", 2);
					alllist.get(i).put("typeMsg", "未发布");
				}
			}

			//更新一条type为1 正在显示
			if(closest!=-1){
				alllist.get(closest).put("type", 1);
				alllist.get(closest).put("typeMsg", "当前显示");

			}
			li.addAll(alllist);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Map emap=new HashMap();
			emap.put("method", "indexService.findRecommendList()");
			emap.put("pager", pager);
			emap.put("pageName", pageName);
			new cn.bevol.log.LogException(e,emap);
		}
		return new ReturnData(li, total);
	}

	/**
	 * 获取单条推荐
	 *
	 * @param createTime
	 * @return
	 */
	public ReturnData findOneRecommend(Long createStamp) {
		List alllist = new ArrayList();
		Map newmap = new HashMap();

		try {
			Query query = new Query(Criteria.where("createStamp").is(createStamp));
			Map<String, Object> map = (Map) mongoTemplate.findOne(query, HashMap.class, "recommend");
			// 从pager_1顺序迭代出每一个实体
			for (int i = 1; i <= map.size(); i++) {
				for (String key : map.keySet()) {
					if (key.equals("pager_" + i)) {
						String[] page = key.split("_");
						int num = Integer.parseInt(page[1]);
						if(num==i){
							Object li = map.get(key);
							List all = (List) li;
							for (int p = 0; p < all.size(); p++) {
								alllist.add(all.get(p));
							}
						}
					}
				}
				newmap.put("content", alllist);
			}
			newmap.put("createStamp", createStamp);
			newmap.put("state", map.get("state"));
			newmap.put("updateStamp", map.get("updateStamp"));
			newmap.put("publishStamp", map.get("publishStamp"));
		} catch (NumberFormatException e) {

			e.printStackTrace();
			Map emap=new HashMap();
			emap.put("method", "indexService.findRecommendList()");
			emap.put("createStamp", createStamp);
			new cn.bevol.log.LogException(e,emap);
		}
		return new ReturnData(newmap);
	}



	/**
	 * 修改当天首页瀑布流阅读量
	 * @return
	 */
	@LogMethod
	public ReturnData updateRecommend() {
		//发布时间小于当前时间
		Query query = new Query(Criteria.where("state").is(0).and("publishStamp").lte(DateUtils.nowInSeconds()));
		Map allMap = mongoTemplate.findOne(query.limit(1).with(new Sort(Direction.DESC, "publishStamp")),
				HashMap.class, "recommend");
		if(allMap != null) {
			for (int j = 1; j <= 20; j++) {
				String pager = "pager_" + j;
				List<Map> allList = (List<Map>) allMap.get(pager);
				if (allList != null) {
					List newList = new ArrayList();
					for (int i = 0; i < allList.size(); i++) {
						Map entityMap = (Map) allList.get(i);
						if (entityMap != null) {
							String tname = null;
							tname = (String) entityMap.get("tname");
							Long id;
							//所有实体全部更新
							if ("userPart".equals(tname) || "goods".equals(tname)) {
								id = Long.valueOf(entityMap.get("id")+"");
								ReturnData rd = getEntity(tname, Long.valueOf(id));
								Map m = (Map) rd.getResult();
								//allList.remove(allList.get(i));
								//allList.add(i, m);
								newList.add(m);
							} else if ("comment_goods".equals(tname)) {
								id = Long.valueOf(entityMap.get("commentId")+"");
								ReturnData rd = getEntity(tname, id);
								Map m = (Map) rd.getResult();
								//allList.remove(allList.get(i));
								//allList.add(i, m);
								newList.add(m);
							}


						}

					}

					new Query();
					Update update = new Update();
					update.set(pager, newList);
					mongoTemplate.updateFirst(new Query(Criteria.where("createStamp").is(allMap.get("createStamp"))), update, "recommend");
				}
			}
		}
		return new ReturnData(mongoTemplate.findOne(new Query(Criteria.where("createStamp").is(allMap.get("createStamp"))),HashMap.class, "recommend"));
	}
}