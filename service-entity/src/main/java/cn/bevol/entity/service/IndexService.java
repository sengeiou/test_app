package cn.bevol.entity.service;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.conf.client.ConfUtils;
import cn.bevol.log.LogException;
import cn.bevol.model.GlobalConfig;
import cn.bevol.model.entity.EntityUserPart;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.vo.GoodsExplain;
import cn.bevol.mybatis.dao.*;
import cn.bevol.mybatis.dto.*;
import cn.bevol.mybatis.model.Composition;
import cn.bevol.mybatis.model.Find;
import cn.bevol.mybatis.model.GoodsTag;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import com.bevol.web.response.ResponseBuilder;
import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;
import com.io97.utils.DateUtils;
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
	private InitMapper initMapper;

	@Autowired
	private IndexMapper indexMapper;

	@Autowired
	private HotListMapper hotListMapper;

	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	private SearchMapper searchMapper;

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
	private GoodsMapper goodsMapper;
	

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
	private ShareEntityMapper shareEntityMapper;

	@Autowired
	private FindMapper findMapper;
	@Autowired
	CacheService cacheService;

	private static String tname = "tname";

	/**
	 * ???????????????
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
	 * ????????????????????????
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
	 * ????????????????????? ?????????????????? ????????????????????????????????????????????????
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
				// ????????????????????????
			} else {
				// TODO: 16-8-16 ?????????????????????
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
	 * 2.9?????????????????? ??????
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
	 * 2.9?????????????????? ?????? ????????????
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
	 * { // ?????? Map adMap = advertisementLogService.findAd(name, null,
	 * null,positionType); // ????????????????????? if (null != adMap && adMap.size() > 0) {
	 * List<Map<String, String>> entityInfo = this.getAdInfo(adMap); for (int i
	 * = 0; i < listMap.size(); i++) { Map paramMap = (Map)
	 * listMap.get(i).get("param"); for (int j = 0; null != entityInfo && j <
	 * entityInfo.size(); j++) { Map<String, String> advMap = entityInfo.get(j);
	 * 
	 * //positionType ???????????? 1app 2model 3pc //????????????(??????/??????) int redirectType =
	 * Integer.parseInt(advMap.get("redirectType")); //???????????? String redirectUrl =
	 * (String) advMap.get("redirectUrl");
	 * 
	 * // ???????????? int adOrientation =
	 * Integer.parseInt(advMap.get("adOrientation")); // ??????????????????id int entityId =
	 * Integer.parseInt(advMap.get("entityId")); // ??????id int adId =
	 * Integer.parseInt(advMap.get("adId")); // ???????????? String adImage = (String)
	 * advMap.get("image"); // ??????banner ??????????????? 0?????? 1?????? 2?????? 10?????? int bannerType =
	 * Integer.parseInt(advMap.get("bannerType"));
	 * 
	 * if ((adOrientation - 1) == i) { boolean flag = false; // ???????????????????????????
	 * ????????????????????? if (StringUtils.isBlank(adImage)) { if (bannerType == 0) { // ??????
	 * ReturnData rd = findService.findArticleInfo(entityId); Find find = (Find)
	 * rd.getResult(); if (null != find) { listMap.get(i).put("image",
	 * find.getHeaderImage() + "?t=" + DateUtils.nowInSeconds()); } flag = true;
	 * } else if (bannerType == 1) { // ???????????? ReturnData rd =
	 * hotListService.detailContent(entityId, null); Map HotListmap = (Map)
	 * rd.getResult(); if(null!=HotListmap && null!=HotListmap.get("detail")){
	 * HotList hotList = (HotList) HotListmap.get("detail");
	 * listMap.get(i).put("image", hotList.getImage()); flag = true; } } else if
	 * (bannerType == 2) { // ?????? ReturnData rd =
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
		// ??????
		Map adMap = advertisementLogClientService.findAd(name, null, null, positionType);
		// ?????????????????????
		if (null != adMap && adMap.size() > 0) {
			List<Map<String, String>> entityInfo = this.getAdInfo(adMap);
			for (int i = 0; i < listMap.size(); i++) {
				Map paramMap = (Map) listMap.get(i).get("param");
				for (int j = 0; null != entityInfo && j < entityInfo.size(); j++) {
					Map<String, String> advMap = entityInfo.get(j);

					// positionType ???????????? 1app 2model 3pc
					// ????????????(??????/??????)
					int redirectType = Integer.parseInt(advMap.get("redirectType"));
					// ????????????
					String redirectUrl = (String) advMap.get("redirectUrl");

					// ????????????
					int adOrientation = Integer.parseInt(advMap.get("adOrientation"));
					// ??????????????????id
					int entityId = Integer.parseInt(advMap.get("entityId"));
					// ??????id
					int adId = Integer.parseInt(advMap.get("adId"));
					// ????????????
					String adImage = (String) advMap.get("image");
					// ??????banner ??????????????? 0?????? 1?????? 2?????? 10??????
					int bannerType = Integer.parseInt(advMap.get("bannerType"));

					boolean flag = false;
					if ((adOrientation - 1) == i) {
						// ??????????????????????????? ?????????????????????
						if (StringUtils.isBlank(adImage)) {
							/*
							 * type=1?????? 2?????? 3?????? 4?????? 5?????? 6?????? 7???????????? 8???????????? 9 ?????? 10
							 * ??????
							 */

							if (bannerType == 1) {
								// ??????
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
								// ??????
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
								// ??????
								ReturnData rd = hotListService.detailContent(entityId, null);
								Map HotListmap = (Map) rd.getResult();
								if (null != HotListmap && null != HotListmap.get("detail")) {
									HotList hotList = (HotList) HotListmap.get("detail");
									listMap.get(i).put("image", hotList.getImgSrc());
									flag = true;
								}
							} else if (bannerType == 10) {
								// ??????
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
			return ResponseBuilder.buildFailureMessage("????????????");
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
	 * ????????????
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
	 * ???????????? ????????????
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

	// ??????????????????
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

	// v2.9 ??????????????????
	public List<EssenceComment> indexEssenceComment2(String positionType) {
		try {
			// indexMapper.updateEssenceImage();
			// ??????????????????
			List<EssenceComment> elist = indexMapper.essenceCommentList2();

			// ??????/????????????
			elist = encodeEntity(elist);
			// ??????????????????
			Map adMap = advertisementLogClientService.findAd(2, null, null, positionType);
			// ?????????????????????
			if (null != adMap && adMap.size() > 0) {
				List<Map<String, String>> entityInfo = this.getAdInfo(adMap);

				for (int j = 0; null != entityInfo && j < entityInfo.size(); j++) {
					Map<String, String> map = entityInfo.get(j);
					for (int i = 0; i < elist.size(); i++) {
						// ?????????????????????
						if ((Integer.parseInt(map.get("adOrientation")) - 1) == i) {
							EssenceComment ec = elist.get(i);
							String ecId = ec.getId().toString();
							String entityId = map.get("entityId");
							String adImage = "";
							if (StringUtils.isNotBlank(map.get("image"))) {
								adImage = map.get("image");
							}
							EssenceComment newEc = null;
							// ????????????
							if (StringUtils.isNotBlank(entityId)) {
								newEc = indexMapper.getEssenceCommentByID(Long.parseLong(entityId));
							}
							if (null != newEc) {
								// ??????????????????????????????
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
			// ??????????????????
			List<UserInfo> userInfos = mongoTemplate.find(new Query(Criteria.where("id").in(userIds)), UserInfo.class,
					"user_info");
			for (UserInfo user : userInfos) {
				for (EssenceComment ec : elist) {
					if (user.getId() == ec.getUserId().intValue()) {
						// ????????????????????????
						ec.setEssenceCommentNum(user.getEssenceCommentNum());
						// ?????????????????????
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
	 * ????????????????????????,?????????????????????
	 * 
	 * @return
	 */
	public List<EssenceComment> encodeEntity(List<EssenceComment> elist) {
		// ??????
		for (EssenceComment ec : elist) {
			ec.setContent(StringEscapeUtils.unescapeJava(ec.getContent()));
			// ????????????
			ReturnData rd = goodsService.getGoodsDetail(ec.getGoodsMid());
			GoodsExplain ge = (GoodsExplain) rd.getResult();
			ec.setGoodsImage(ge.getGoods().getImage());
			ec.setGoodsImageSrc(ge.getGoods().getImageSrc());
		}
		return elist;
	}

	// ??????????????????
	public List<Map<String, String>> getAdInfo(Map adMap) {
		// System.out.println("map:"+adMap);
		// ????????????
		JSONArray result = JSONArray.fromObject(adMap.get("result"));
		List<Map> list2 = new ArrayList<Map>();
		// ??????id??????
		List<Map<String, String>> entityInfo = new ArrayList<Map<String, String>>();
		if (result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject job = result.getJSONObject(i);
				// ????????????id
				long entityId = (Integer) job.get("entityId");
				String enImage = (String) job.get("imgUrl");
				int adOrientation = (Integer) job.get("orientation");

				// ???????????? 1app 2model 3pc
				String positionType = (String) job.get("positionType");
				// ????????????(??????/??????)
				int redirectType = (Integer) job.get("redirectType");
				// ????????????
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
				// ????????????
				if (StringUtils.isNotBlank(enImage)) {
					map.put("image", enImage);
				}
				entityInfo.add(map);
			}
		}
		return entityInfo;
	}

	/**
	 * 2.4??????
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
	 * 2.4??????
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
	 * 2.4??????
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
	 * 2.9?????????????????????-- 3.0 ?????? ???????????????????????????????????????
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
	 * ???????????? ????????????????????? ?????????????????? ????????????????????????????????????????????????
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
					// ???????????????
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutComposition();
					List ruleOutGoods = searchService.ruleOutGoods2();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// ????????????
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
				// ????????????????????????
			} else {
				// TODO: 16-8-16 ?????????????????????
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp2 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}

	}

	/**
	 * ???????????? ?????????????????? ???????????? ????????????????????? ?????????????????? ????????????????????????????????????????????????
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
					// ???????????????
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutComposition();
					List ruleOutGoods = searchService.ruleOutGoods2();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// ????????????
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

					// ???????????????
					resultContent.put("skinlists", skinlists);
					// ????????????
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app?????????????????????
					resultContent.put("shareUnique", share);
					// ????????????????????????
					resultContent.put("goodsSearch", goodsSearch);
					// ????????????
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
				// ????????????????????????
			} else {
				// TODO: 16-8-16 ?????????????????????
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp3 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 * ???????????? ?????????????????? ???????????? ????????????????????? ?????????????????? ????????????????????????????????????????????????
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
					// ???????????????
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					List ruleOutGoods = searchService.ruleOutGoods2();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// ????????????
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

					// ???????????????
					resultContent.put("skinlists", skinlists);
					// ????????????
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app?????????????????????
					resultContent.put("shareUnique", share);
					// ????????????????????????
					resultContent.put("goodsSearch", goodsSearch);
					// ????????????
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					List<Map> tags = new ArrayList<Map>();
					Map m = new HashMap();
					m.put("id", 3);
					m.put("content", "??????");
					tags.add(m);
					m = new HashMap();
					m.put("id", 4);
					m.put("content", "??????");
					tags.add(m);
					// ????????????????????????
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
				// ????????????????????????
			} else {
				// TODO: 16-8-16 ?????????????????????
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp4 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 * ???????????? ?????????????????? ???????????? ????????????????????? ?????????????????? ????????????????????????????????????????????????
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
					// ???????????????
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					List ruleOutGoods = searchService.ruleOutGoods3();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// ????????????
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

					// ???????????????(??????,??????)
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

					// ???????????????
					resultContent.put("skinlists", skinlists);
					// ????????????
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app?????????????????????
					resultContent.put("shareUnique", share);
					// ????????????????????????
					resultContent.put("goodsSearch", goodsSearch);
					// ????????????
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					resultContent.put("hotComposition", hotComposition);
					resultContent.put("hotGoods", hotGoods);

					List<Map> userTags = findMapper.getUserTag("user");
					// ????????????????????????
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
				// ????????????????????????
			} else {
				// TODO: 16-8-16 ?????????????????????
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp5 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 * ???????????? ?????????????????? ???????????? ????????????????????? ?????????????????? ????????????????????????????????????????????????
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
					// ???????????????
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					List ruleOutGoods = searchService.ruleOutGoods3();
					List<Label> findtype = findMapper.findType();
					List<Label> findLabelList = findMapper.findLabelList();
					// ????????????
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

					// ???????????????(??????,??????)
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

					// ???????????????
					resultContent.put("skinlists", skinlists);
					// ????????????
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app?????????????????????
					resultContent.put("shareUnique", share);
					// ????????????
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					resultContent.put("hotComposition", hotComposition);
					resultContent.put("hotGoods", hotGoods);

					List<Map> userTags = findMapper.getUserTag("user");
					// ????????????????????????
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
				// ????????????????????????
			} else {
				// ???????????????
				/*
				 * ReturnData rd=userService.getUserById(userId); UserInfo
				 * userInfo=null; if(rd.getRet()==0){
				 * userInfo=(UserInfo)rd.getResult(); }
				 * if(StringUtils.isNotBlank(userInfo.getResult())){ Map
				 * likeGoodsList=skinService.findSkinAndGoods(userInfo.getResult
				 * ()); resultContent.put("likeGoods", likeGoodsList); }
				 */

				// TODO: 16-8-16 ?????????????????????
				// resultContent append userInfo
			}
			return new ReturnData(resultContent);
		} catch (Exception ex) {
			logger.error("method:initApp6 arg:{userId:" + userId + "}" + "   desc:" + ex.getMessage());
			return ReturnData.ERROR;
		}
	}

	/**
	 * ???????????? ?????????????????? ???????????? 3.0??????????????? ????????????????????? ?????????????????? ????????????????????????????????????????????????
	 *
	 * @return
	 */
	public ReturnData initApp7(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					// ????????????
					List<Share> shareDesc = indexMapper.shareDescByType();
					// ????????????????????????
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// ????????????
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					// ????????????
					List ruleOutGoods = searchService.ruleOutGoods3();
					// ??????/???????????????????????????
					List<Label> findtype = findMapper.findType();
					// ??????/???????????????????????????
					List<Label> findLabelList = findMapper.findLabelList();
					// ??????/???????????????????????????
					List findTag = entityService.getConfigArray("find_tag");
					// ??????????????????
					List skinlists = skinService.skinList();
					// ??????????????????
					Map skinShare = skinService.skinShare();
					// ????????????
					Map myShare = entityService.getConfigMap("my_share");
					// ?????????????????????????????????/??????????????????
					Map share = skinService.shareUnique();
					// ????????????
					List<GoodsTag> gtList = goodsService.getAllGoodsTag();

					// ????????????????????????(?????????????????????)
					List hotKeyWords = entityService.getConfigList("hot_keywords");

					// ????????????
					List goodsSearch = entityService.getConfigList("goods_search2");

					// ??????????????????
					String goodsSearchUserLike = "";
					if (null != entityService.getConfigMap("goods_search2_user_like")
							&& entityService.getConfigMap("goods_search2_user_like").size() > 0) {
						goodsSearchUserLike = (String) entityService.getConfigMap("goods_search2_user_like")
								.get("user_like");
					}

					// ?????????banners
					List<Map> xxsBanners = entityService.getConfigList("xxs_banners");

					// ?????????banner????????????
					xxsBanners = indexService.getList(xxsBanners, 6, "1");

					// ????????????
					List aboutUs = entityService.getConfigList("about_us");

					// ??????????????????
					List compositionDesc = entityService.getConfigList("composition_text");

					// ????????????
					List jubaoDesc = entityService.getConfigList("jubao_desc");
					// ??????????????????
					List correctDesc = entityService.getConfigList("correct_desc");
					// ??????????????????
					List compositionCorrectDesc = entityService.getConfigList("composition_correction_desc");
					// ??????????????????
					List skinFeedbackDesc = entityService.getConfigList("skin_feedback");

					// ?????????????????????
					List xxsButton = entityService.getConfigList("xxs_button");

					// app??????banner?????????
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

					// ???????????????
					resultContent.put("skinlists", skinlists);
					// ????????????
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app?????????????????????
					resultContent.put("shareUnique", share);
					// ????????????
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					List<Map> userTags = findMapper.getUserTag("user");
					// ????????????????????????
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
			// ????????????????????????
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// ????????????????????????
			} else {
				// TODO: 16-8-16 ?????????????????????
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
	 * ??????????????????
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
	 * ?????????????????????
	 */
	public void collectTest() {
		initMapper.collectTest();
	}

	/**
	 * v3.1 ????????????????????????????????????
	 * 
	 * @param userId
	 * @return
	 */
	public ReturnData initApp8(final Long userId) {
		Map resultContent = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {

					// ????????????
					List<Share> shareDesc = indexMapper.shareDescByType();
					// ????????????????????????
					List<ShoppingAddress> shop = indexMapper.fingShopAddress();
					// ?????????????????????
					List<SeachComposition> ruleOutComposition = searchMapper.ruleOutCompositionByInit4();
					// ????????????
					List ruleOutGoods = searchService.ruleOutGoods3();
					// ??????/???????????????????????????
					List<Label> findtype = findMapper.findType();
					// ??????/???????????????????????????
					List<Label> findLabelList = findMapper.findLabelList();
					// ??????/???????????????????????????
					List findTag = entityService.getConfigArray("find_tag");
					// ??????????????????
					List skinlists = skinService.skinList();
					// ??????????????????
					Map skinShare = skinService.skinShare();
					// ????????????
					Map myShare = entityService.getConfigMap("my_share");
					// ?????????????????????????????????/??????????????????
					Map share = skinService.shareUnique();
					// ????????????
					List<GoodsTag> gtList = goodsService.getAllGoodsTag();

					// ????????????????????????(?????????????????????)
					List hotKeyWords = entityService.getConfigList("hot_keywords");
					//??????????????????????????????
					List newGoodsSearch=entityService.getConfigList("new_goods_search");
					// ????????????
					List goodsSearch = entityService.getConfigList("goods_search2");

					// ??????????????????
					String goodsSearchUserLike = "";
					if (null != entityService.getConfigMap("goods_search2_user_like")
							&& entityService.getConfigMap("goods_search2_user_like").size() > 0) {
						goodsSearchUserLike = (String) entityService.getConfigMap("goods_search2_user_like")
								.get("user_like");
					}

					// ????????????
					List aboutUs = entityService.getConfigList("about_us");

					// ??????????????????
					List compositionDesc = entityService.getConfigList("composition_text");

					// ????????????
					List jubaoDesc = entityService.getConfigList("jubao_desc");
					// ??????????????????
					List correctDesc = entityService.getConfigList("correct_desc");
					// ??????????????????
					List compositionCorrectDesc = entityService.getConfigList("composition_correction_desc");
					// ??????????????????
					List skinFeedbackDesc = entityService.getConfigList("skin_feedback");

					// ?????????????????????
					// List
					// skinGoodsCategory=entityService.getConfigList("skinGoodsCategory");

					// ????????????????????????????????????????????????
					List<Map> skinFlowTags = findMapper.getUserTag("user_skin");

					// ???????????????
					List<Map> applyGoodsTags = findMapper.getUserTag("apply_goods");

					// app??????banner?????????
					// List<Map>
					// openAppBanner=entityService.getConfigList("open_ad");
					List<Map> openAppBanner = getOpenBanners();

					// ?????????banner?????????
					List<Map> messageBanner = entityService.getConfigList("message_banner");
					//?????????????????????(0?????? 1??????)
					int switchOfScancode= ConfUtils.getResourceNum("switchOfScancode");
					//?????????????????????open:0????????????,1?????????, ??????????????????mandatory:0?????? 1?????????
					Map<String,String> aMap = ConfUtils.getJSONMap("user_authentication");

					//?????????????????????(0?????? 1??????)
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

					// ???????????????
					resultContent.put("skinlists", skinlists);
					// ????????????
					resultContent.put("skinShare", skinShare);
					resultContent.put("myShare", myShare);

					resultContent.put("shareDesc", shareDesc);
					// app?????????????????????
					resultContent.put("shareUnique", share);
					// ????????????
					resultContent.put("findTag", findTag);

					resultContent.put("effect", gtList);
					resultContent.put("shareType", ConfUtils.getResourceString("share_type"));

					List<Map> userTags = findMapper.getUserTag("user");
					// ????????????????????????
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
			// ????????????????????????
			this.entityCountHandler(resultContent);
			if (userId == null || userId <= 0) {
				// ????????????????????????
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
	 *            ????????????
	 * @param v
	 *            app?????????
	 * @return
	 */
	public ReturnData opanApp2(final long userId, final int o, final String v) {
		Map map = new CacheableTemplate<Map>(cacheProvider) {
			@Override
			protected Map getFromRepository() {
				try {
					Map map = new HashMap();
					// ????????????
					Map versionMap = indexMapper.checkVersion(o);
					Map vMap = new HashMap();

					if (null != versionMap) {
						// ????????????/ios??????
						String version = versionMap.get("version") + "";
						if (!v.equals(version)) {
							// ????????????????????????????????????
							// ???????????? isremind=1?????? 0?????????
							boolean isremind = (Boolean) versionMap.get("isremind");
							if (isremind) {
								// ?????????????????? 0 ?????? 1???????????? 2?????????
								vMap.put("update", versionMap.get("mustupgrade"));
								vMap.put("updateCotent", versionMap.get("content"));
								vMap.put("version", versionMap.get("version"));
								
								//3.2??????,??????ios?????????bug,???????????????????????????
								vMap.put("content", versionMap.get("content"));
								
								//3.2????????????????????????
								vMap.put("url", versionMap.get("url"));
							} else {
								vMap.put("update", 2);
							}
							//3.2??????,??????ios?????????bug,???????????????????????????
							map.put("version", versionMap.get("version"));
						}
					}
					map.put("checkVersion", vMap);
					

					// 3???init???????????????????????? ??????config???,
					// ?????????????????????initUpdate???update??????,???????????????init??????
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

		// 2????????? openApp
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
	 * v3.1 ??????+???????????? ??????:???????????????
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
						// ??????banner/????????????/???????????????
						Map indexMap = indexInfo(positionType);
						map.put("index", indexMap);
					}
					// ??????????????????
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
	 * ????????????
	 * @param pager
	 * @return
	 */
	private List recommend2(int pager) {

		try {
			// ???????????????????????????
			/*Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 24);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long dayMustTime = cal.getTimeInMillis() / 1000;*/
			//??????????????????????????????
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
	 *            1app 2?????? 3pc
	 * @return
	 */
	public Map indexInfo(String positionType) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			// ??????banner
			map.put("find", indexImage2_9(positionType));
			// ??????????????????
			map.put("goodsCategory", indexClassification());
			// ??????????????????
			map.put("lists", indexArticle3());
			// ????????????????????????
			map.put("essenceComment", indexEssenceComment2(positionType));
			// ????????????
			if (null != entityService.getConfigMap("skin_image")
					&& entityService.getConfigMap("skin_image").size() > 0) {
				map.put("skinImage", entityService.getConfigMap("skin_image").get("image"));
			}
			//????????????
			ReturnListData rd=new ReturnListData();
			int type=0;
			int pager=1;
			int pageSize=2;
			rd=findService.findList(type, null, null,"", pager, pageSize);
			map.put("findArticle",rd.getResult());
			// ????????????????????????????????????????????????
			List userPartAndDoyen = entityService.getConfigList("index_desc");
			map.put("userPartAndDoyen", userPartAndDoyen);
			return map;
		} catch (Exception e) {
			logger.error("method:IndexService.indexInfo arg:{ positionType:" + positionType + ",desc:"
					+ ExceptionUtils.getStackTrace(e));
		}
		return null;

	}

 

	/**
	 * ???????????????,????????????
	 * 
	 * @param pager
	 * @return
	 */

	public List recommend(Integer pager) {
		try {
			//??????????????????????????????
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
				// ???????????????????????????
				long current = System.currentTimeMillis();
				long zero = (current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset())
						/ 1000;
				for (Map map : openAppBanner) {
					Integer state = Integer.parseInt(map.get("state") + "");
					Long startTime = Long.parseLong(map.get("startTime") + "");
					Long endTime = Long.parseLong(map.get("endTime") + "");
					// ???????????? ???????????????
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
						// ??????banner/????????????/???????????????
						Map indexMap = indexInfo(positionType);
						map.put("index", indexMap);
					}
					// ??????????????????
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

	 



 
 
	 
}
