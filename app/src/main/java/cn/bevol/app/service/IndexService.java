package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.*;
import cn.bevol.app.entity.dto.*;
import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.entity.model.Find;
import cn.bevol.app.entity.model.GoodsTag;
import cn.bevol.app.entity.vo.GoodsExplain;
import cn.bevol.model.entity.DataCategory;
import cn.bevol.model.entity.DataCategoryRelation;
import cn.bevol.model.entity.EntityUserPart;
import cn.bevol.model.GlobalConfig;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ResponseBuilder;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class IndexService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(IndexService.class);

	@Autowired
	private InitOldMapper initMapper;

	@Autowired
	private IndexOldMapper indexMapper;

	@Autowired
	private HotListOldMapper hotListMapper;

	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	private SearchOldMapper searchMapper;

	@Resource
	private SearchService searchService;

	@Resource
	private IndexService indexService;

	@Resource
	private GoodsService goodsService;

	@Resource
	private HotListService hotListService;

	@Resource
	private FindService findService;
	
	@Autowired
	private GoodsOldMapper goodsMapper;
	

	@Resource
	private AdvertisementLogClientService advertisementLogClientService;

	@Resource
	private SkinService skinService;

	@Resource
	private UserService userService;

	@Resource
	private UserPartService userPartService;

	@Resource
	private EntityService entityService;

	@Autowired
	private FindOldMapper findMapper;
	@Autowired
	CacheService cacheService;

	private static String tname = "tname";

	/**
	 * 版本号信息
	 *
	 */
	public ReturnData checkVersion(final int ver) {
		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {

				try {
					return new ReturnData(indexMapper.checkVersion(ver));
				} catch (Exception e) {
					logger.error("method:checkVersion  desc:" + ExceptionUtils.getStackTrace(e));
					return ReturnData.ERROR;
				}

			}

			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_APPVESION_PREFIX, ver + "")), true);
	}

	/**
	 * 获取实体数量信息
	 *
	 * @param resultContent
	 */
	private void entityCountHandler(Map resultContent) {
		GlobalConfig cig = this.getGlobalConfig();
		Long testSkinCount = cig.getTestSkinCount();
		Long productCount = cig.getProductCount();
		Long compositionCount = cig.getCompositionCount();
		List<Composition> likeList = cig.getLikeList();
		List<Composition> notLikeList = cig.getNotLikeList();
		resultContent.put("testSkinCount", testSkinCount);
		resultContent.put("productCount", productCount);
		resultContent.put("compositionCount", compositionCount);
		resultContent.put("collectionCompositionSort", likeList);
		resultContent.put("notLikeCompositionSort", notLikeList);
	}

	/**
	 * 用户初始化应用 返回通用信息 成分总数，产品总数，肤质测试总数
	 *
	 * @return
	 */
	public ReturnData initApp(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {

				try {
					/*
					 * int testSkinCount = initMapper.countTestResult(); int
					 * productCount = initMapper.countProduct(); int
					 * compositionCount = initMapper.countComposition();
					 */
					List<Share> shareDesc = indexMapper.shareDescByType();
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					HashMap<String, Object> resultContent = new HashMap<String, Object>();
					/*
					 * resultContent.put("testSkinCount", testSkinCount);
					 * resultContent.put("productCount", productCount);
					 * resultContent.put("compositionCount", compositionCount);
					 */
					resultContent.put("shareDesc", shareDesc);
					resultContent.put("shop", shop);
					return resultContent;
				} catch (Exception e) {
					logger.error("method:initApp_part1  desc:" + ExceptionUtils.getStackTrace(e));
					return new HashMap();
				}

			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INITAPP_1_PREFIX)), true);

		try {
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// 未登录用户不处理
			} else {
				// TODO: 16-8-16 用户初始化流程
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp_part2 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}

	}

	public List indexImage() {
		try {
			IndexImage image = indexMapper.findImage();
			List<Classification> classification = indexMapper.fingClassification();
			JSONArray json = JSONArray.fromObject(image.getValue());
			List list = new ArrayList();
			if (json.size() > 0) {
				for (int i = 0; i < json.size(); i++) {
					Map map = new HashMap();
					JSONObject job = json.getJSONObject(i);
					map.put("findid", job.get("findid"));
					map.put("image", job.get("image") + "?t=" + DateUtils.nowInSeconds());
					list.add(map);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error("method:indexImage arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
			return new ArrayList();
		}
	}

	/**
	 * 2.9首页发现图片 广告
	 *
	 * @return
	 */
	/*
	 * public List indexImage2_9() { try { List<Map>
	 * listMap=entityService.getConfigArray("new_banners");
	 * listMap=this.getList(listMap,1,"1");
	 *
	 * return listMap; } catch (Exception e) { logger.error(
	 * "method:indexImage2_9 arg:{}" + "   desc:" +
	 * ExceptionUtils.getStackTrace(e)); return new ArrayList(); } }
	 */

	/**
	 * 2.9首页发现图片 广告 带客户端
	 *
	 * @return
	 */
	public List indexImage2_9(String positionType) {
		try {
			List<Map> listMap = entityService.getConfigArray("new_banners");
			listMap = this.getList(listMap, 1, positionType);

			this.compareGoodsMids(listMap);
			return listMap;
		} catch (Exception e) {
			logger.error("method:indexImage2_9 arg:{positionType: " + positionType + "}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));
			return new ArrayList();
		}
	}

	/*
	 * public List getList(List<Map> listMap, Integer name,String positionType)
	 * { // 广告 Map adMap = advertisementLogService.findAd(name, null,
	 * null,positionType); // 判断是否有广告 if (null != adMap && adMap.size() > 0) {
	 * List<Map<String, String>> entityInfo = this.getAdInfo(adMap); for (int i
	 * = 0; i < listMap.size(); i++) { Map paramMap = (Map)
	 * listMap.get(i).get("param"); for (int j = 0; null != entityInfo && j <
	 * entityInfo.size(); j++) { Map<String, String> advMap = entityInfo.get(j);
	 *
	 * //positionType 跳转终端 1app 2model 3pc //跳转类型(站内/站外) int redirectType =
	 * Integer.parseInt(advMap.get("redirectType")); //跳转地址 String redirectUrl =
	 * (String) advMap.get("redirectUrl");
	 *
	 * // 广告位置 int adOrientation =
	 * Integer.parseInt(advMap.get("adOrientation")); // 要替换的实体id int entityId =
	 * Integer.parseInt(advMap.get("entityId")); // 广告id int adId =
	 * Integer.parseInt(advMap.get("adId")); // 广告图片 String adImage = (String)
	 * advMap.get("image"); // 首页banner 实体的类型 0发现 1清单 2话题 10站外 int bannerType =
	 * Integer.parseInt(advMap.get("bannerType"));
	 *
	 * if ((adOrientation - 1) == i) { boolean flag = false; // 广告图片为空的时候
	 * 直接查实体图片 if (StringUtils.isBlank(adImage)) { if (bannerType == 0) { // 发现
	 * ReturnData rd = findService.findArticleInfo(entityId); Find find = (Find)
	 * rd.getResult(); if (null != find) { listMap.get(i).put("image",
	 * find.getHeaderImage() + "?t=" + DateUtils.nowInSeconds()); } flag = true;
	 * } else if (bannerType == 1) { // 热门清单 ReturnData rd =
	 * hotListService.detailContent(entityId, null); Map HotListmap = (Map)
	 * rd.getResult(); if(null!=HotListmap && null!=HotListmap.get("detail")){
	 * HotList hotList = (HotList) HotListmap.get("detail");
	 * listMap.get(i).put("image", hotList.getImage()); flag = true; } } else if
	 * (bannerType == 2) { // 话题 ReturnData rd =
	 * findService.findArticleInfo(entityId); Find find = (Find) rd.getResult();
	 * if (null != find) { listMap.get(i).put("image", find.getHeaderImage() +
	 * "?t=" + DateUtils.nowInSeconds()); } flag = true; } else if(bannerType ==
	 * 10){ flag = true; }
	 *
	 * if (flag) { paramMap.put("id", entityId); paramMap.put("type",
	 * bannerType); listMap.get(i).put("param", paramMap); } } else {
	 * paramMap.put("id", entityId); paramMap.put("type", bannerType);
	 * listMap.get(i).put("image", adImage); } listMap.get(i).put("adId", adId);
	 * listMap.get(i).put("adOrientation", adOrientation);
	 *
	 * listMap.get(i).put("positionType", positionType);
	 * listMap.get(i).put("redirectType", redirectType);
	 * listMap.get(i).put("redirectUrl", redirectUrl); }
	 *
	 * } } } return listMap; }
	 */

	public List getList(List<Map> listMap, Integer name, String positionType) {
		// 广告
		Map adMap = advertisementLogClientService.findAd(name, null, null, positionType);
		// 判断是否有广告
		if (null != adMap && adMap.size() > 0) {
			List<Map<String, String>> entityInfo = this.getAdInfo(adMap);
			for (int i = 0; i < listMap.size(); i++) {
				Map paramMap = (Map) listMap.get(i).get("param");
				for (int j = 0; null != entityInfo && j < entityInfo.size(); j++) {
					Map<String, String> advMap = entityInfo.get(j);

					// positionType 跳转终端 1app 2model 3pc
					// 跳转类型(站内/站外)
					int redirectType = Integer.parseInt(advMap.get("redirectType"));
					// 跳转地址
					String redirectUrl = (String) advMap.get("redirectUrl");

					// 广告位置
					int adOrientation = Integer.parseInt(advMap.get("adOrientation"));
					// 要替换的实体id
					int entityId = Integer.parseInt(advMap.get("entityId"));
					// 广告id
					int adId = Integer.parseInt(advMap.get("adId"));
					// 广告图片
					String adImage = (String) advMap.get("image");
					// 首页banner 实体的类型 0发现 1清单 2话题 10站外
					int bannerType = Integer.parseInt(advMap.get("bannerType"));

					boolean flag = false;
					if ((adOrientation - 1) == i) {
						// 广告图片为空的时候 直接查实体图片
						if (StringUtils.isBlank(adImage)) {
							/*
							 * type=1文章 2话题 3心得 4清单 5产品 6成分 7文章列表 8话题列表 9 福利 10
							 * 站外
							 */

							if (bannerType == 1) {
								// 发现
								ReturnData rd = findService.findArticleInfo(entityId);
								Find find = (Find) rd.getResult();
								if (null != find) {
									/*
									 * listMap.get(i).put("image",
									 * find.getHeaderImage() + "?t=" +
									 * DateUtils.nowInSeconds());
									 */
									listMap.get(i).put("image", find.getHeaderImageSrc());
								}
								flag = true;
							} else if (bannerType == 3) {
								// 心得
								EntityUserPart eup = mongoTemplate
										.findOne(
												new Query(Criteria.where("id").is(entityId).and("hidden").is(0)
														.and("deleted").is(0)),
												EntityUserPart.class, "entity_user_part_lists");
								if (null != eup && null != eup.getImgSrc()) {
									listMap.get(i).put("image", eup.getImgSrc());
									flag = true;
								}

							} else if (bannerType == 2 || bannerType == 4) {
								// 话题
								ReturnData rd = hotListService.detailContent(entityId, null);
								Map HotListmap = (Map) rd.getResult();
								if (null != HotListmap && null != HotListmap.get("detail")) {
									HotList hotList = (HotList) HotListmap.get("detail");
									listMap.get(i).put("image", hotList.getImgSrc());
									flag = true;
								}
							} else if (bannerType == 10) {
								// 站外
								flag = true;
							}

							if (flag) {
								paramMap.put("id", entityId);
								paramMap.put("type", bannerType);
								listMap.get(i).put("param", paramMap);
							}
						} else {
							paramMap.put("id", entityId);
							paramMap.put("type", bannerType);
							listMap.get(i).put("image", adImage);
							flag = true;
						}
						if (flag) {
							listMap.get(i).put("adId", adId);
							listMap.get(i).put("adOrientation", adOrientation);

							listMap.get(i).put("positionType", positionType);
							listMap.get(i).put("redirectType", redirectType);
							listMap.get(i).put("redirectUrl", redirectUrl);
						}

					}

				}
			}
		}
		return listMap;
	}

	public Map indexClassification() {
		try {
			List<Classification> classification = indexMapper.fingClassification();
			Map map = new HashMap();
			map.put("classification", classification);
			return map;
		} catch (Exception e) {
			logger.error("method:indexClassification arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
			return ResponseBuilder.buildFailureMessage("数据为空");
		}
	}

	public List indexArticle() {
		try {
			List<HotList> efs = hotListMapper.list(0, 5);
			return efs;
		} catch (Exception e) {
			logger.error("method:indexArticle arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
			return new ArrayList();
		}
	}

	/**
	 * 包含话题
	 *
	 * @return
	 */
	public List indexArticle2() {
		try {
			List<HotList> efs = hotListMapper.partList(0, 5);
			return efs;
		} catch (Exception e) {
			logger.error("method:indexArticle2 arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
			return new ArrayList();
		}
	}

	/**
	 * 包含话题 定时发布
	 *
	 * @return
	 */
	public List indexArticle3() {
		try {
			List<HotList> efs = hotListMapper.partList(0, 5);
			return efs;
		} catch (Exception e) {
			logger.error("method:indexArticle3 arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
			return new ArrayList();
		}
	}

	// 首页精选点评
	public List<EssenceComment> indexEssenceComment() {
		try {
			indexMapper.updateEssenceImage();
			List<EssenceComment> elist = indexMapper.essenceCommentList();
			elist = encodeEntity(elist);
			return elist;
		} catch (Exception e) {
			logger.error("method:indexEssenceComment arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	// v2.9 首页精选点评
	public List<EssenceComment> indexEssenceComment2(String positionType) {
		try {
			// indexMapper.updateEssenceImage();
			// 精选点评列表
			List<EssenceComment> elist = indexMapper.essenceCommentList2();

			// 解码/产品图片
			elist = encodeEntity(elist);
			// 精选点评广告
			Map adMap = advertisementLogClientService.findAd(2, null, null, positionType);
			// 判断是否有广告
			if (null != adMap && adMap.size() > 0) {
				List<Map<String, String>> entityInfo = this.getAdInfo(adMap);

				for (int j = 0; null != entityInfo && j < entityInfo.size(); j++) {
					Map<String, String> map = entityInfo.get(j);
					for (int i = 0; i < elist.size(); i++) {
						// 确定广告的位置
						if ((Integer.parseInt(map.get("adOrientation")) - 1) == i) {
							EssenceComment ec = elist.get(i);
							String ecId = ec.getId().toString();
							String entityId = map.get("entityId");
							String adImage = "";
							if (StringUtils.isNotBlank(map.get("image"))) {
								adImage = map.get("image");
							}
							EssenceComment newEc = null;
							// 查数据库
							if (StringUtils.isNotBlank(entityId)) {
								newEc = indexMapper.getEssenceCommentByID(Long.parseLong(entityId));
							}
							if (null != newEc) {
								// 是否返回了广告的图片
								if (StringUtils.isNotBlank(adImage)) {
									newEc.setGoodsImage(adImage);
								}
								newEc.setAdId(Integer.parseInt(map.get("adId")));
								newEc.setAdOrientation(Integer.parseInt(map.get("adOrientation")));

								newEc.setPositionType(map.get("positionType"));
								newEc.setRedirectType(Integer.parseInt(map.get("redirectType")));
								newEc.setRedirectUrl(map.get("redirectUrl"));
								elist.set(i, newEc);
							}

						}
					}
				}
			}

			List<Long> userIds = new ArrayList<Long>();
			for (EssenceComment ec : elist) {
				userIds.add(ec.getUserId());
			}
			// 精选点评作者
			List<UserInfo> userInfos = mongoTemplate.find(new Query(Criteria.where("id").in(userIds)), UserInfo.class,
					"user_info");
			for (UserInfo user : userInfos) {
				for (EssenceComment ec : elist) {
					if (user.getId() == ec.getUserId().intValue()) {
						// 获取精选点评数量
						ec.setEssenceCommentNum(user.getEssenceCommentNum());
						// 获取修行说数量
						ec.setXxsNum(user.getXxsNum());
					}
				}

			}

			return elist;
		} catch (Exception e) {
			logger.error("method:indexEssenceComment2 arg:{ positionType: " + positionType + "}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	/**
	 * 精选点评内容转码,点评的产品更新
	 *
	 * @return
	 */
	public List<EssenceComment> encodeEntity(List<EssenceComment> elist) {
		// 转码
		for (EssenceComment ec : elist) {
			ec.setContent(StringEscapeUtils.unescapeJava(ec.getContent()));
			// 产品图片
			ReturnData rd = goodsService.getGoodsDetail(ec.getGoodsMid());
			GoodsExplain ge = (GoodsExplain) rd.getResult();
			ec.setGoodsImage(ge.getGoods().getImage());
			ec.setGoodsImageSrc(ge.getGoods().getImageSrc());
		}
		return elist;
	}

	// 获取广告信息
	public List<Map<String, String>> getAdInfo(Map adMap) {
		// System.out.println("map:"+adMap);
		// 得到内容
		JSONArray result = JSONArray.fromObject(adMap.get("result"));
		List<Map> list2 = new ArrayList<Map>();
		// 实体id集合
		List<Map<String, String>> entityInfo = new ArrayList<Map<String, String>>();
		if (result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject job = result.getJSONObject(i);
				// 得到实体id
				long entityId = (Integer) job.get("entityId");
				String enImage = (String) job.get("imgUrl");
				int adOrientation = (Integer) job.get("orientation");

				// 跳转终端 1app 2model 3pc
				String positionType = (String) job.get("positionType");
				// 跳转类型(站内/站外)
				int redirectType = (Integer) job.get("redirectType");
				// 跳转地址
				String redirectUrl = "";
				if (!(job.get("redirectUrl") instanceof JSONNull)) {
					redirectUrl = (String) job.get("redirectUrl");
				}

				int entityType = -1;
				if (!(job.get("type") instanceof JSONNull)) {
					entityType = Integer.parseInt((String) job.get("type"));
				}
				int bannerType = -1;
				if (!(job.get("bannerType") instanceof JSONNull)) {
					bannerType = (Integer) job.get("bannerType");
				}

				long adId = (Integer) job.get("id");
				map.put("adOrientation", adOrientation + "");
				map.put("adId", adId + "");

				map.put("entityId", entityId + "");
				map.put("entityType", entityType + "");
				map.put("bannerType", bannerType + "");

				map.put("positionType", positionType);
				map.put("redirectType", redirectType + "");
				map.put("redirectUrl", redirectUrl);
				// 广告图片
				if (StringUtils.isNotBlank(enImage)) {
					map.put("image", enImage);
				}
				entityInfo.add(map);
			}
		}
		return entityInfo;
	}

	/**
	 * 2.4首页
	 *
	 */
	public ReturnData index2() {
		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("find", indexImage());
					map.put("goodsCategory", indexClassification());
					map.put("lists", indexArticle());
					map.put("essenceComment", indexEssenceComment());
					return new ReturnData(map);
				} catch (Exception e) {
					logger.error("method:index2 arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
					return ReturnData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
				"IndexService.index2" + "_" + CACHE_NAME.VERSION), true);
	}

	/**
	 * 2.4首页
	 *
	 */
	public ReturnData index3() {
		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("find", indexImage());
					map.put("goodsCategory", indexClassification());
					map.put("lists", indexArticle2());
					map.put("essenceComment", indexEssenceComment());
					return new ReturnData(map);
				} catch (Exception e) {
					logger.error("method:index3 arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
					return ReturnData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
				"IndexService.index3" + "_" + CACHE_NAME.VERSION), true);
	}

	/**
	 * 2.4首页
	 *
	 */
	public ReturnData index4() {
		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("find", indexImage());
					map.put("goodsCategory", indexClassification());
					map.put("lists", indexArticle3());
					map.put("essenceComment", indexEssenceComment());
					if (null != entityService.getConfigMap("skin_image")
							&& entityService.getConfigMap("skin_image").size() > 0) {
						map.put("skinImage", entityService.getConfigMap("skin_image").get("image"));
					}

					return new ReturnData(map);
				} catch (Exception e) {
					logger.error("method:index4 arg:{}" + "   desc:" + ExceptionUtils.getStackTrace(e));
					return ReturnData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
				"IndexService.index4" + "_" + CACHE_NAME.VERSION), true);
	}

	/**
	 * 2.9开始版本在使用-- 3.0 首页 精选点评新增用户精选点评数
	 *
	 */
	public ReturnData index5(final String positionType) {
		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				try {
					Map map = indexInfo(positionType);
					return new ReturnData(map);
				} catch (Exception e) {
					logger.error("method:index5 arg:{positionType:" + positionType + ",}" + "   desc:"
							+ ExceptionUtils.getStackTrace(e));
					return ReturnData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INDEX_5_PREFIX, positionType)), true);
	}

	/**
	 * 接口合并 用户初始化应用 返回通用信息 成分总数，产品总数，肤质测试总数
	 *
	 * @return
	 */
	public ReturnData initApp2(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					/*
					 * int testSkinCount = initMapper.countTestResult(); int
					 * productCount = initMapper.countProduct(); int
					 * compositionCount = initMapper.countComposition();
					 */
					List<Share> shareDesc = indexMapper.shareDescByType();
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// 合并的接口
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutComposition();
					List ruleOutGoods = searchService.ruleOutGoods2();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// 成分筛选
					/*
					 * List<Composition>
					 * likeList=entityService.collectionCompositionSort();
					 * List<Composition>
					 * notLikeList=entityService.notLikeCompositionSort();
					 */

					HashMap<String, Object> resultContent = new HashMap<String, Object>();
					/*
					 * resultContent.put("testSkinCount", testSkinCount);
					 * resultContent.put("productCount", productCount);
					 * resultContent.put("compositionCount", compositionCount);
					 */
					resultContent.put("shareDesc", shareDesc);
					resultContent.put("shop", shop);
					resultContent.put("ruleOutComposition", ruleOutComposition);
					resultContent.put("ruleOutGoods", ruleOutGoods);
					resultContent.put("findtype", findtype);
					resultContent.put("findLabelList", findLabelList);

					/*
					 * resultContent.put("collectionCompositionSort", likeList);
					 * resultContent.put("notLikeCompositionSort", notLikeList);
					 */
					return resultContent;
				} catch (Exception e) {
					logger.error("method:initApp2  desc:" + ExceptionUtils.getStackTrace(e));
					return new HashMap();
				}

			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INITAPP_2_PREFIX)), true);

		try {
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// 未登录用户不处理
			} else {
				// TODO: 16-8-16 用户初始化流程
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp2 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}

	}

	/**
	 * 接口合并 新增肤质列表 肤质分享 用户初始化应用 返回通用信息 成分总数，产品总数，肤质测试总数
	 *
	 * @return
	 */
	public ReturnData initApp3(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					/*
					 * int testSkinCount = initMapper.countTestResult(); int
					 * productCount = initMapper.countProduct(); int
					 * compositionCount = initMapper.countComposition();
					 */
					List<Share> shareDesc = indexMapper.shareDescByType();
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// 合并的接口
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutComposition();
					List ruleOutGoods = searchService.ruleOutGoods2();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// 成分筛选
					/*
					 * List<Composition>
					 * likeList=entityService.collectionCompositionSort();
					 * List<Composition>
					 * notLikeList=entityService.notLikeCompositionSort();
					 */
					List skinlists = skinService.skinList();
					Map skinShare = skinService.skinShare();
					Map myShare = entityService.getConfigMap("my_share");

					Map share = entityService.getConfigMap("share_unique");
					List<GoodsTag> gtList = goodsService.getAllGoodsTag();

					List goodsSearch = entityService.getConfigArray("goods_search");

					List findTag = entityService.getConfigArray("find_tag");

					HashMap<String, Object> resultContent = new HashMap<String, Object>();
					/*
					 * resultContent.put("testSkinCount", testSkinCount);
					 * resultContent.put("productCount", productCount);
					 * resultContent.put("compositionCount", compositionCount);
					 */
					resultContent.put("shop", shop);
					resultContent.put("ruleOutComposition", ruleOutComposition);
					resultContent.put("ruleOutGoods", ruleOutGoods);
					resultContent.put("findtype", findtype);
					resultContent.put("findLabelList", findLabelList);

					/*
					 * resultContent.put("collectionCompositionSort", likeList);
					 * resultContent.put("notLikeCompositionSort", notLikeList);
					 */

					// 肤质测试题
					resultContent.put("skinlists", skinlists);
					// 肤质分享
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app内所有分享文案
					resultContent.put("shareUnique", share);
					// 产品搜索排序方式
					resultContent.put("goodsSearch", goodsSearch);
					// 热门标签
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					return resultContent;
				} catch (Exception e) {
					logger.error("method:initApp3  desc:" + ExceptionUtils.getStackTrace(e));
					return new HashMap();
				}

			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INITAPP_3_PREFIX)), true);

		try {
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// 未登录用户不处理
			} else {
				// TODO: 16-8-16 用户初始化流程
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp3 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 * 接口合并 新增肤质列表 肤质分享 用户初始化应用 返回通用信息 成分总数，产品总数，肤质测试总数
	 *
	 * @return
	 */
	public ReturnData initApp4(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					/*
					 * int testSkinCount = initMapper.countTestResult(); int
					 * productCount = initMapper.countProduct(); int
					 * compositionCount = initMapper.countComposition();
					 */
					List<Share> shareDesc = indexMapper.shareDescByType();
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// 合并的接口
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					List ruleOutGoods = searchService.ruleOutGoods2();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// 成分筛选
					/*
					 * List<Composition>
					 * likeList=entityService.collectionCompositionSort();
					 * List<Composition>
					 * notLikeList=entityService.notLikeCompositionSort();
					 */
					List skinlists = skinService.skinList();
					Map skinShare = skinService.skinShare();
					Map myShare = entityService.getConfigMap("my_share");

					Map share = entityService.getConfigMap("share_unique");
					List<GoodsTag> gtList = goodsService.getAllGoodsTag();

					List goodsSearch = entityService.getConfigArray("goods_search");

					List findTag = entityService.getConfigArray("find_tag");

					HashMap<String, Object> resultContent = new HashMap<String, Object>();
					/*
					 * resultContent.put("testSkinCount", testSkinCount);
					 * resultContent.put("productCount", productCount);
					 * resultContent.put("compositionCount", compositionCount);
					 */
					resultContent.put("shop", shop);
					resultContent.put("ruleOutComposition", ruleOutComposition);
					resultContent.put("ruleOutGoods", ruleOutGoods);
					resultContent.put("findtype", findtype);
					resultContent.put("findLabelList", findLabelList);

					/*
					 * resultContent.put("collectionCompositionSort", likeList);
					 * resultContent.put("notLikeCompositionSort", notLikeList);
					 */

					// 肤质测试题
					resultContent.put("skinlists", skinlists);
					// 肤质分享
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app内所有分享文案
					resultContent.put("shareUnique", share);
					// 产品搜索排序方式
					resultContent.put("goodsSearch", goodsSearch);
					// 热门标签
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					List<Map> tags = new ArrayList<Map>();
					Map m = new HashMap();
					m.put("id", 3);
					m.put("content", "美白");
					tags.add(m);
					m = new HashMap();
					m.put("id", 4);
					m.put("content", "洁面");
					tags.add(m);
					// 用户参与列表标签
					resultContent.put("user_part_lists_tag", tags);

					return resultContent;
				} catch (Exception e) {
					logger.error("method:initApp4  desc:" + ExceptionUtils.getStackTrace(e));
					return new HashMap();
				}

			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INITAPP_4_PREFIX)), true);

		try {
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// 未登录用户不处理
			} else {
				// TODO: 16-8-16 用户初始化流程
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp4 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 * 接口合并 新增肤质列表 肤质分享 用户初始化应用 返回通用信息 成分总数，产品总数，肤质测试总数
	 *
	 * @return
	 */
	public ReturnData initApp5(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					/*
					 * int testSkinCount = initMapper.countTestResult(); int
					 * productCount = initMapper.countProduct(); int
					 * compositionCount = initMapper.countComposition();
					 */
					List<Share> shareDesc = indexMapper.shareDescByType();
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// 合并的接口
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					List ruleOutGoods = searchService.ruleOutGoods3();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// 成分筛选
					/*
					 * List<Composition>
					 * likeList=entityService.collectionCompositionSort();
					 * List<Composition>
					 * notLikeList=entityService.notLikeCompositionSort();
					 */
					List skinlists = skinService.skinList();
					Map skinShare = skinService.skinShare();
					Map myShare = entityService.getConfigMap("my_share");

					Map share = entityService.getConfigMap("share_unique");
					List<GoodsTag> gtList = goodsService.getAllGoodsTag();

					List goodsSearch = entityService.getConfigArray("goods_search");

					List findTag = entityService.getConfigArray("find_tag");

					// 热门关键字(产品,成分)
					List hotComposition = entityService.getHotKeyWord("hot_keyword_composition");
					List hotGoods = entityService.getHotKeyWord("hot_keyword");

					HashMap<String, Object> resultContent = new HashMap<String, Object>();
					/*
					 * resultContent.put("testSkinCount", testSkinCount);
					 * resultContent.put("productCount", productCount);
					 * resultContent.put("compositionCount", compositionCount);
					 */
					resultContent.put("shop", shop);
					resultContent.put("ruleOutComposition", ruleOutComposition);
					resultContent.put("ruleOutGoods", ruleOutGoods);
					resultContent.put("findtype", findtype);
					resultContent.put("findLabelList", findLabelList);

					/*
					 * resultContent.put("collectionCompositionSort", likeList);
					 * resultContent.put("notLikeCompositionSort", notLikeList);
					 */

					resultContent.put("hotComposition", hotComposition);
					resultContent.put("hotGoods", hotGoods);

					// 肤质测试题
					resultContent.put("skinlists", skinlists);
					// 肤质分享
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app内所有分享文案
					resultContent.put("shareUnique", share);
					// 产品搜索排序方式
					resultContent.put("goodsSearch", goodsSearch);
					// 热门标签
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					resultContent.put("hotComposition", hotComposition);
					resultContent.put("hotGoods", hotGoods);

					List<Map> userTags = findMapper.getUserTag("user");
					// 用户参与列表标签
					resultContent.put("userPartListsTag", userTags);

					return resultContent;
				} catch (Exception e) {
					logger.error("method:initApp5  desc:" + ExceptionUtils.getStackTrace(e));
					return new HashMap();
				}

			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INITAPP_5_PREFIX)), true);

		try {
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// 未登录用户不处理
			} else {
				// TODO: 16-8-16 用户初始化流程
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp5 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 * 接口合并 新增肤质列表 肤质分享 用户初始化应用 返回通用信息 成分总数，产品总数，肤质测试总数
	 *
	 * @return
	 */
	public ReturnData initApp6(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					/*
					 * int testSkinCount = initMapper.countTestResult(); int
					 * productCount = initMapper.countProduct(); int
					 * compositionCount = initMapper.countComposition();
					 */
					List<Share> shareDesc = indexMapper.shareDescByType();
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// 合并的接口
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					List ruleOutGoods = searchService.ruleOutGoods3();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// 成分筛选
					/*
					 * List<Composition>
					 * likeList=entityService.collectionCompositionSort();
					 * List<Composition>
					 * notLikeList=entityService.notLikeCompositionSort();
					 */
					List skinlists = skinService.skinList();
					Map skinShare = skinService.skinShare();
					Map myShare = entityService.getConfigMap("my_share");

					// todo
					Map share = skinService.shareUnique();
					// Map share=entityService.getConfigMap("share_unique");

					List<GoodsTag> gtList = goodsService.getAllGoodsTag();

					List findTag = entityService.getConfigArray("find_tag");

					// 热门关键字(产品,成分)
					List hotComposition = entityService.getHotKeyWord("hot_keyword_composition");
					List hotGoods = entityService.getHotKeyWord("hot_keyword");

					HashMap<String, Object> resultContent = new HashMap<String, Object>();
					/*
					 * resultContent.put("testSkinCount", testSkinCount);
					 * resultContent.put("productCount", productCount);
					 * resultContent.put("compositionCount", compositionCount);
					 */
					resultContent.put("shop", shop);
					resultContent.put("ruleOutComposition", ruleOutComposition);
					resultContent.put("ruleOutGoods", ruleOutGoods);
					resultContent.put("findtype", findtype);
					resultContent.put("findLabelList", findLabelList);

					/*
					 * resultContent.put("collectionCompositionSort", likeList);
					 * resultContent.put("notLikeCompositionSort", notLikeList);
					 */

					resultContent.put("hotComposition", hotComposition);
					resultContent.put("hotGoods", hotGoods);

					// 肤质测试题
					resultContent.put("skinlists", skinlists);
					// 肤质分享
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app内所有分享文案
					resultContent.put("shareUnique", share);
					// 热门标签
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					resultContent.put("hotComposition", hotComposition);
					resultContent.put("hotGoods", hotGoods);

					List<Map> userTags = findMapper.getUserTag("user");
					// 用户参与列表标签
					resultContent.put("userPartListsTag", userTags);

					return resultContent;
				} catch (Exception e) {
					logger.error("method:initApp6  desc:" + ExceptionUtils.getStackTrace(e));
					return new HashMap();
				}

			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INITAPP_6_PREFIX)), true);

		try {
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// 未登录用户不处理
			} else {
				// 同肤质最爱
				/*
				 * ReturnData rd=userService.getUserById(userId); UserInfo
				 * userInfo=null; if(rd.getRet()==0){
				 * userInfo=(UserInfo)rd.getResult(); }
				 * if(StringUtils.isNotBlank(userInfo.getResult())){ Map
				 * likeGoodsList=skinService.findSkinAndGoods(userInfo.getResult
				 * ()); resultContent.put("likeGoods", likeGoodsList); }
				 */

				// TODO: 16-8-16 用户初始化流程
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp6 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 * 接口合并 新增肤质列表 肤质分享 3.0开始使用？ 用户初始化应用 返回通用信息 成分总数，产品总数，肤质测试总数
	 *
	 * @return
	 */
	public ReturnData initApp7(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					// 分享描述
					List<Share> shareDesc = indexMapper.shareDescByType();
					// 去比价的商城信息
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// 成分热搜
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					// 产品热搜
					List ruleOutGoods = searchService.ruleOutGoods3();
					// 发现/达人原创的分类列表
					List<Label> findtype = findMapper.findType();
					// 发现/达人原创的标签列表
					List<Label> findLabelList = findMapper.findLabelList();
					// 发现/达人原创的热门标签
					List findTag = entityService.getConfigArray("find_tag");
					// 肤质测试模块
					List skinlists = skinService.skinList();
					// 肤质分享文案
					Map skinShare = skinService.skinShare();
					// 我的分享
					Map myShare = entityService.getConfigMap("my_share");
					// 产品、成分等分享到好友/朋友圈的文案
					Map share = skinService.shareUnique();
					// 产品标签
					List<GoodsTag> gtList = goodsService.getAllGoodsTag();

					// 搜索的热门关键字(产品和成分一起)
					List hotKeyWords = entityService.getConfigList("hot_keywords");

					// 搜索排序
					List goodsSearch = entityService.getConfigList("goods_search2");

					// 用户喜欢排序
					String goodsSearchUserLike = "";
					if (null != entityService.getConfigMap("goods_search2_user_like")
							&& entityService.getConfigMap("goods_search2_user_like").size() > 0) {
						goodsSearchUserLike = (String) entityService.getConfigMap("goods_search2_user_like")
								.get("user_like");
					}

					// 修行社banners
					List<Map> xxsBanners = entityService.getConfigList("xxs_banners");

					// 修行社banner广告处理
					xxsBanners = indexService.getList(xxsBanners, 6, "1");

					// 关于我们
					List aboutUs = entityService.getConfigList("about_us");

					// 全成分表文案
					List compositionDesc = entityService.getConfigList("composition_text");

					// 举报文案
					List jubaoDesc = entityService.getConfigList("jubao_desc");
					// 产品纠错文案
					List correctDesc = entityService.getConfigList("correct_desc");
					// 成分纠错文案
					List compositionCorrectDesc = entityService.getConfigList("composition_correction_desc");
					// 肤质反馈文案
					List skinFeedbackDesc = entityService.getConfigList("skin_feedback");

					// 修行社按钮文案
					List xxsButton = entityService.getConfigList("xxs_button");

					// app开屏banner和文案
					// List<Map>
					// openAppBanner=entityService.getConfigList("open_ad");
					List<Map> openAppBanner = getOpenBanners();

					HashMap<String, Object> resultContent = new HashMap<String, Object>();
					resultContent.put("shop", shop);
					resultContent.put("openAppBanner", openAppBanner);
					resultContent.put("xxsButton", xxsButton);
					resultContent.put("juBaoDesc", jubaoDesc);
					resultContent.put("compositionCorrectDesc", compositionCorrectDesc);
					resultContent.put("skinFeedbackDesc", skinFeedbackDesc);
					resultContent.put("correctDesc", correctDesc);
					resultContent.put("compositionDesc", compositionDesc);
					resultContent.put("ruleOutComposition", ruleOutComposition);
					resultContent.put("ruleOutGoods", ruleOutGoods);
					resultContent.put("findtype", findtype);
					resultContent.put("findLabelList", findLabelList);

					resultContent.put("goodsSearchUserLike", goodsSearchUserLike);
					resultContent.put("hotKeyWords", hotKeyWords);
					resultContent.put("xxsBanners", xxsBanners);
					resultContent.put("aboutUs", aboutUs);
					resultContent.put("goodsSearch", goodsSearch);

					// 肤质测试题
					resultContent.put("skinlists", skinlists);
					// 肤质分享
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app内所有分享文案
					resultContent.put("shareUnique", share);
					// 热门标签
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					List<Map> userTags = findMapper.getUserTag("user");
					// 用户参与列表标签
					resultContent.put("userPartListsTag", userTags);
					return resultContent;
				} catch (Exception e) {
					logger.error("method:initApp7  desc:" + ExceptionUtils.getStackTrace(e));
					return new HashMap();
				}

			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INITAPP_7_PREFIX)), true);

		try {
			// 获取实体数量信息
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// 未登录用户不处理
			} else {
				// TODO: 16-8-16 用户初始化流程
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp7 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	public boolean insertShare(Share test) {
		int i = indexMapper.insertShare(test);
		return i > 0 ? true : false;
	}

	public boolean deleteShare(Share test) {
		int i = indexMapper.deleteShare(test);
		return i > 0 ? true : false;
	}

	/**
	 * 重新加载缓存
	 */
	public void cacheInit() {
		cacheService.cleanCacheForeverByKey("IndexService.index2");
		this.index2();
		cacheService.cleanCacheForeverByKey("IndexService.index3");
		this.index3();

		cacheService.cleanCacheForeverByKey("IndexService.initApp");
		this.initApp(0L);

		cacheService.cleanCacheForeverByKey("IndexService.initApp2");
		this.initApp2(0L);

		cacheService.cleanCacheForeverByKey("IndexService.initApp3");
		this.initApp3(0L);

		cacheService.cleanCacheForeverByKey("IndexService.initApp4");
		this.initApp4(0L);
		cacheService.cleanCacheForeverByKey("IndexService.initApp5");
		this.initApp5(0L);
		cacheService.cleanCacheForeverByKey("IndexService.initApp6");
		this.initApp6(0L);
		cacheService.cleanCacheForeverByKey("IndexService.initApp7");
		this.initApp7(0L);

	}

	/**
	 * 数据库连接测试
	 */
	public void collectTest() {
		initMapper.collectTest();
	}

	/**
	 * v3.1 新增护肤流程问答题和标签
	 *
	 * @param userId
	 * @return
	 */
	public ReturnData initApp8(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {

					// 分享描述
					List<Share> shareDesc = indexMapper.shareDescByType();
					// 去比价的商城信息
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// 不想要的成分组
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					// 产品热搜
					List ruleOutGoods = searchService.ruleOutGoods3();
					// 发现/达人原创的分类列表
					List<Label> findtype = findMapper.findType();
					// 发现/达人原创的标签列表
					List<Label> findLabelList = findMapper.findLabelList();
					// 发现/达人原创的热门标签
					List findTag = entityService.getConfigArray("find_tag");
					// 肤质测试模块
					List skinlists = skinService.skinList();
					// 肤质分享文案
					Map skinShare = skinService.skinShare();
					// 我的分享
					Map myShare = entityService.getConfigMap("my_share");
					// 产品、成分等分享到好友/朋友圈的文案
					Map share = skinService.shareUnique();
					// 产品标签
					List<GoodsTag> gtList = goodsService.getAllGoodsTag();

					// 搜索的热门关键字(产品和成分一起)
					List hotKeyWords = entityService.getConfigList("hot_keywords");
					//搜索的新品搜索关键字
					List newGoodsSearch=entityService.getConfigList("new_goods_search");
					// 搜索排序
					List goodsSearch = entityService.getConfigList("goods_search2");

					// 用户喜欢排序
					String goodsSearchUserLike = "";
					if (null != entityService.getConfigMap("goods_search2_user_like")
							&& entityService.getConfigMap("goods_search2_user_like").size() > 0) {
						goodsSearchUserLike = (String) entityService.getConfigMap("goods_search2_user_like")
								.get("user_like");
					}

					// 关于我们
					List aboutUs = entityService.getConfigList("about_us");

					// 全成分表文案
					List compositionDesc = entityService.getConfigList("composition_text");

					// 举报文案
					List jubaoDesc = entityService.getConfigList("jubao_desc");
					// 产品纠错文案
					List correctDesc = entityService.getConfigList("correct_desc");
					// 成分纠错文案
					List compositionCorrectDesc = entityService.getConfigList("composition_correction_desc");
					// 肤质反馈文案
					List skinFeedbackDesc = entityService.getConfigList("skin_feedback");

					// 护肤流程问答题
					// List
					// skinGoodsCategory=entityService.getConfigList("skinGoodsCategory");

					// 用户肤质界面选择的感兴趣标签列表
					List<Map> skinFlowTags = findMapper.getUserTag("user_skin");

					// 福利社标签
					List<Map> applyGoodsTags = findMapper.getUserTag("apply_goods");

					// app开屏banner和文案
					// List<Map>
					// openAppBanner=entityService.getConfigList("open_ad");
					List<Map> openAppBanner = getOpenBanners();

					// 消息的banner和文案
					List<Map> messageBanner = entityService.getConfigList("message_banner");
					//产品扫码的开关(0为关 1为开)
					int switchOfScancode= ConfUtils.getResourceNum("switchOfScancode");
					//实名认证的开关open:0需要认证,1不需要, 是否强制实名mandatory:0强制 1非强制
					Map<String,String> aMap = ConfUtils.getJSONMap("user_authentication");

					//图片优化的开关(0为开 1为关)
					JSONObject optimize= entityService.getConfigMap("optimize");
					Map map=new HashMap();
					map.put("open", aMap.get("open"));
					map.put("mandatory", aMap.get("mandatory"));

					HashMap<String, Object> resultContent = new HashMap<String, Object>();
					resultContent.put("optimize",optimize);
					resultContent.put("switchOfScancode", switchOfScancode);
					resultContent.put("authentication", map);
					resultContent.put("openAppBanner", openAppBanner);
					resultContent.put("openAppBanner", openAppBanner);
					resultContent.put("applyGoodsTags", applyGoodsTags);
					resultContent.put("messageBanner", messageBanner);
					resultContent.put("skinFlowTags", skinFlowTags);
					resultContent.put("shop", shop);
					resultContent.put("juBaoDesc", jubaoDesc);
					resultContent.put("compositionCorrectDesc", compositionCorrectDesc);
					resultContent.put("skinFeedbackDesc", skinFeedbackDesc);
					resultContent.put("correctDesc", correctDesc);
					resultContent.put("compositionDesc", compositionDesc);
					resultContent.put("ruleOutComposition", ruleOutComposition);
					resultContent.put("ruleOutGoods", ruleOutGoods);
					resultContent.put("findtype", findtype);
					resultContent.put("findLabelList", findLabelList);
					resultContent.put("newGoodsSearch", newGoodsSearch);
					resultContent.put("goodsSearchUserLike", goodsSearchUserLike);
					resultContent.put("hotKeyWords", hotKeyWords);
					resultContent.put("aboutUs", aboutUs);
					resultContent.put("goodsSearch", goodsSearch);

					// 肤质测试题
					resultContent.put("skinlists", skinlists);
					// 肤质分享
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app内所有分享文案
					resultContent.put("shareUnique", share);
					// 热门标签
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					List<Map> userTags = findMapper.getUserTag("user");
					// 用户参与列表标签
					resultContent.put("userPartListsTag", userTags);
					return resultContent;
				} catch (Exception e) {
					logger.error("method:initApp8  desc:" + ExceptionUtils.getStackTrace(e));
					return new HashMap();
				}

			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null && !returnValue.isEmpty());
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INITAPP_8_PREFIX)), true);

		try {
			// 获取实体数量信息
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// 未登录用户不处理
			} else {
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp8 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 *
	 * @param userId
	 * @param o
	 *            用户系统
	 * @param v
	 *            app版本号
	 * @return
	 */
	public ReturnData opanApp2(final long userId, final int o, final String v) {
		Map map = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					Map map = new HashMap();
					// 检查版本
					Map versionMap = indexMapper.checkVersion(o);
					Map vMap = new HashMap();

					if (null != versionMap) {
						// 最新安卓/ios版本
						String version = versionMap.get("version") + "";
						if (!v.equals(version)) {
							// 当前版本与最新版本不一致
							// 是否提醒 isremind=1提醒 0不提醒
							boolean isremind = (Boolean) versionMap.get("isremind");
							if (isremind) {
								// 是否强制更新 0 更新 1强制更新 2不提醒
								vMap.put("update", versionMap.get("mustupgrade"));
								vMap.put("updateCotent", versionMap.get("content"));
								vMap.put("version", versionMap.get("version"));

								//3.2添加,因为ios前端有bug,后台协助添加该属性
								vMap.put("content", versionMap.get("content"));

								//3.2安卓需要下载地址
								vMap.put("url", versionMap.get("url"));
							} else {
								vMap.put("update", 2);
							}
							//3.2添加,因为ios前端有bug,后台协助添加该属性
							map.put("version", versionMap.get("version"));
						}
					}
					map.put("checkVersion", vMap);


					// 3、init接口信息是否变更 读取config表,
					// 如果前端获取的initUpdate与update不同,则重新获取init接口
					int update = Integer.parseInt(entityService.getConfigMap("update_init").get("update") + "");
					map.put("initUpdate", update);//
					return map;
				} catch (Exception e) {
					logger.error("method:IndexService.opanApp2 arg:{userId:" + userId + ", o:" + o + ", v:" + v + "}"
							+ "   desc:" + e.getMessage());
				}
				return null;
			}

			@Override
			protected boolean canPutToCache(Map returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_OPEN_APP_PREFIX, o + "", v)), true);

		// 2、用户 openApp
		try {
			ReturnData rd = userService.openApp(userId);
			UserInfo userInfo = null;
			if (rd.getRet() == 0) {
				userInfo = (UserInfo) rd.getResult();
			}
			map.put("userInfo", userInfo);
			return new ReturnData(map);
		} catch (Exception e) {
			logger.error("method:IndexService.opanApp2 arg:{userId:" + userId + ", o:" + o + ", v:" + v + "}"
					+ "   desc:" + e.getMessage());
		}
		return null;
	}

	/**
	 * v3.1 首页+内容推荐 缓存:五分钟过期
	 *
	 * @param positionType
	 * @param pager
	 * @return
	 */
	public ReturnData index6(final String positionType, final int pager) {
		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				try {
					Map map = new HashMap();
					if (pager == 1 || pager == 0) {
						// 首页banner/精选点评/清单等信息
						Map indexMap = indexInfo(positionType);
						map.put("index", indexMap);
					}
					// 首页内容推荐
					List recommend = indexService.recommend2(pager);
					map.put("recommend", recommend);
					return new ReturnData(map);
				} catch (Exception e) {
					logger.error("method:IndexService.index6 arg:{ positionType:" + positionType + ", pager:" + pager
							+ " ,desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}



			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INDEX_6_PREFIX, positionType, pager + "")),
				true);

	}
	/**
	 * 去除对比
	 * @param pager
	 * @return
	 */
	private List recommend2(int pager) {

		try {
			// 获取当天最大时间戳
			/*Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 24);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long dayMustTime = cal.getTimeInMillis() / 1000;*/
			//发布时间小于当前时间
			Query query = new Query(Criteria.where("state").is(0).and("publishStamp").lte(new Date().getTime()/1000));
			String[] strs = new String[] { "state", "pager_" + pager, "createStamp", "publishStamp" };
			setQueryFeilds(query, strs);
			Map allMap = mongoTemplate.findOne(query.limit(1).with(new Sort(Direction.DESC, "publishStamp")),
					HashMap.class, "recommend");
			List<Map> infoList = new ArrayList<Map>();
			List<Map> allList = new ArrayList<Map>();
			if (null != allMap && null != allMap.get("pager_" + pager)) {
				infoList = (List<Map>) allMap.get("pager_" + pager);
			}
			for(int i=0;i<infoList.size();i++){
				String tname=(String)infoList.get(i).get("tname");
				if(!"compare_goods".equals(tname)){
					allList.add(infoList.get(i));
				}
			}
			return allList;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "IndexService.recommend");
			map.put("pager", pager);
			new LogException(e);
			return null;
		}
	}
	/**
	 *
	 * @param positionType:
	 *            1app 2移动 3pc
	 * @return
	 */
	public Map indexInfo(String positionType) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			// 首页banner
			map.put("find", indexImage2_9(positionType));
			// 首页八个分类
			map.put("goodsCategory", indexClassification());
			// 首页五篇话题
			//map.put("lists", indexArticle3());
			// 首页三篇精选点评
			map.put("essenceComment", indexEssenceComment2(positionType));
			// 肤质图片
			if (null != entityService.getConfigMap("skin_image")
					&& entityService.getConfigMap("skin_image").size() > 0) {
				map.put("skinImage", entityService.getConfigMap("skin_image").get("image"));
			}
			//两篇原创
			ReturnListData rd=new ReturnListData();
			int type=0;
			int pager=1;
			int pageSize=2;
			rd=findService.findList(type, null, null,"", pager, pageSize);
			map.put("findArticle",rd.getResult());
			// 首页热门话题与达人原创图片和文案
			List userPartAndDoyen = entityService.getConfigList("index_desc");
			map.put("userPartAndDoyen", userPartAndDoyen);
			return map;
		} catch (Exception e) {
			logger.error("method:IndexService.indexInfo arg:{ positionType:" + positionType + ",desc:"
					+ ExceptionUtils.getStackTrace(e));
		}
		return null;

	}

	protected Map indexInfo2(String positionType) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			// 首页banner
			map.put("find", indexImage2_9(positionType));
			// 首页四大榜单及分类信息
			map.put("dataCategory",getListCateGory());
			// 首页五篇话题
			//map.put("lists", indexArticle3());
			// 首页三篇精选点评
			map.put("essenceComment", indexEssenceComment2(positionType));
			// 肤质图片
			if (null != entityService.getConfigMap("skin_image")
					&& entityService.getConfigMap("skin_image").size() > 0) {
				map.put("skinImage", entityService.getConfigMap("skin_image").get("image"));
			}
			//两篇原创
			ReturnListData rd=new ReturnListData();
			int type=0;
			int pager=1;
			int pageSize=2;
			rd=findService.findList(type, null, null,"", pager, pageSize);
			map.put("findArticle",rd.getResult());
			// 首页热门话题与达人原创图片和文案
			List userPartAndDoyen = entityService.getConfigList("index_desc");
			map.put("userPartAndDoyen", userPartAndDoyen);
			return map;
		} catch (Exception e) {
			logger.error("method:IndexService.indexInfo arg:{ positionType:" + positionType + ",desc:"
					+ ExceptionUtils.getStackTrace(e));
		}
		return null;

	}

	private List getListCateGory() {
		
		List<Map> list=new ArrayList();
		
		//首页四大榜单
		Query query=new Query(Criteria.where("hidden").is(0).and("deleted").is(0));
		query.fields().include("rid2").include("rid2Title");
		List<DataCategoryRelation> dgrList=mongoTemplate.getCollection("data_category_relation").distinct("rid2",query.getQueryObject());
		for(int i=0;i<dgrList.size();i++){	
			List<Map> mapList=new ArrayList();
			Map map=new HashMap();
			System.out.println(dgrList.get(i));
			DataCategory dataCategory=mongoTemplate.findOne(new Query(Criteria.where("id").is(dgrList.get(i))), DataCategory.class);
			map.put("rid2", dgrList.get(i));
			map.put("rid2Title", dataCategory.getTitle());
			List<DataCategoryRelation> dgr=mongoTemplate.find(new Query(Criteria.where("rid2").is(dgrList.get(i))), DataCategoryRelation.class);
			for(int j=0;j<dgr.size();j++){
				Map entityMap=new HashMap();
				entityMap.put("id", dgr.get(j).getId());
				entityMap.put("rid3", dgr.get(j).getRid3());
				entityMap.put("rid3Title", dgr.get(j).getRid3Title());
				map.put("params", dgr.get(j).getParams());
				mapList.add(entityMap);
			}
			map.put("entityRelation", mapList);
			list.add(map);
		}
		int replaceNum1 = 2;
		int replaceNum2 = 3;
		list.add(replaceNum1,list.get(replaceNum2));
		list.add(replaceNum2,list.get(replaceNum1));
		list.remove(replaceNum1+1);
		list.remove(replaceNum2+1);
		return list;
	}

	/**
	 * 首页瀑布流,分页获取
	 *
	 * @param pager
	 * @return
	 */

	public List recommend(Integer pager) {
		try {
			//发布时间小于当前时间
			Query query = new Query(Criteria.where("state").is(0).and("publishStamp").lte(new Date().getTime()/1000));
			String[] strs = new String[] { "state", "pager_" + pager, "createStamp", "publishStamp" };
			setQueryFeilds(query, strs);
			Map allMap = mongoTemplate.findOne(query.limit(1).with(new Sort(Direction.DESC, "publishStamp")),
					HashMap.class, "recommend");
			List allList = new ArrayList();
			if (null != allMap && null != allMap.get("pager_" + pager)) {
				allList = (List) allMap.get("pager_" + pager);
			}
			return allList;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "IndexService.recommend");
			map.put("pager", pager);
			new LogException(e);
			return null;
		}
	}

	public List getOpenBanners() {
		try {
			List<Map> openAppBanner = entityService.getConfigList("open_ad");
			List list = new ArrayList();
			if (null != openAppBanner && openAppBanner.size() > 0) {
				// 当天时间的最小时间
				long current = System.currentTimeMillis();
				long zero = (current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset())
						/ 1000;
				for (Map map : openAppBanner) {
					Integer state = Integer.parseInt(map.get("state") + "");
					Long startTime = Long.parseLong(map.get("startTime") + "");
					Long endTime = Long.parseLong(map.get("endTime") + "");
					// 发布状态 且时间符合
					if (null != state && state == 0 && zero >= startTime && zero < endTime) {
						list.add(map);
					}
				}
			}
			return list;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "IndexService.getOpenBanners");
			new LogException(e);
			return new ArrayList();
		}
	}

	public ReturnData index7(final String positionType, final int pager) {

		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				try {
					Map map = new HashMap();
					if (pager == 1 || pager == 0) {
						// 首页banner/精选点评/清单等信息
						Map indexMap = indexInfo(positionType);
						map.put("index", indexMap);
					}
					// 首页内容推荐
					List recommend = indexService.recommend(pager);
					map.put("recommend", recommend);
					return new ReturnData(map);
				} catch (Exception e) {
					logger.error("method:IndexService.index7 arg:{ positionType:" + positionType + ", pager:" + pager
							+ " ,desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}



			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INDEX_7_PREFIX, positionType, pager + "")),
				true);


	}

	public ReturnData index8(final String positionType, int pager) {

		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				try {
					Map map = new HashMap();
					if (pager == 1 || pager == 0) {
						// 首页banner/精选点评/清单等信息
						Map indexMap = indexInfo2(positionType);
						map.put("index", indexMap);
					}
					// 首页内容推荐
					List recommend = indexService.recommend(pager);
					map.put("recommend", recommend);
					return new ReturnData(map);
				} catch (Exception e) {
					logger.error("method:IndexService.index8 arg:{ positionType:" + positionType + ", pager:" + pager
							+ " ,desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}



			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INDEX_8_PREFIX, positionType, pager + "")),
				true);


	}
    
	/*
	 * 获取小程序banner
	 */
	
	public ReturnData mIndexBanner1(final int pager) {

		return new CacheableTemplate<ReturnData>(cacheProvider) {
			@Override
			protected ReturnData getFromRepository() {
				Map map = new HashMap();
				if (pager == 1 || pager == 0) {
					// 小程序首页banner
					Map indexMap = mIndexBannerInfo1();
					map.put("mBannerIndex", indexMap);
				}
				return new ReturnData(map);
			}

			@Override
			protected boolean canPutToCache(ReturnData returnValue) {
				return (returnValue != null && returnValue.getRet() == 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INDEX_7_PREFIX, pager + "")),
				true);


	}
	
	/**
	 *获取微信小程序banner信息
	 * @return
	 */
	public Map mIndexBannerInfo1() {
		Map<String, Object> map = new HashMap<String, Object>();
		// 首页banner
		//读取后台设置的小程序banner数据
		List<Map> listMap = entityService.getConfigArray("mg_index_banners");
		this.compareGoodsMids(listMap);
		map.put("find", listMap);
		return map;
	}

	 
}