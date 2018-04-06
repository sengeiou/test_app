package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.db.Paged;
import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.util.DateUtils;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class CacheService {
	private static Logger logger = LoggerFactory.getLogger(CacheService.class);

	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	GoodsService goodsService;

	@Autowired
	EntityService entityService;

	@Autowired
	CompositionService compositionService;

	@Autowired
	CommentService commentService;

	@Autowired
	GoodsOldMapper goodsMapper;

	public static boolean isFirstProductInit = true;
	public static boolean isFirstCompositionInit = true;
	final int initPerPageSize = 10000;

	/**
	 * 根据资源类型清除api缓存 根据key清除过期缓存
	 *
	 * @param tname
	 */
	public void cleanEntityCacheList(final String tname) {
		try {
			cacheProvider.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MATCH_ALL_CACHE_QUEUE,
					"EntityService.entityList_" + tname));
			return;
		} catch (Exception e) {

			logger.error("method:cleanEntityCacheList arg:{tname:\"" + tname + "\"}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * 清除2.4首页缓存 根据key清除过期缓存
	 *
	 * @param key
	 */
	public void cleanCacheListByKey2(String key) {
		try {
			cacheProvider.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MATCH_ALL_CACHE_QUEUE, key));
			return;
		} catch (Exception e) {
			logger.error("method:cleanCacheListByKey2 arg:{key:\"" + key + "\"}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));

		}
	}

	/**
	 * 根据key清除过期缓存
	 *
	 * @param key
	 */
	public void cleanCacheListByKey(String key) {
		try {
			cacheProvider.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MATCH_ALL_CACHE_QUEUE, key));
			return;
		} catch (Exception e) {
			logger.error("method:cleanCacheListByKey arg:{key:\"" + key + "\"}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));

		}
	}

	/**
	 * 一键清除常用的缓存(index,init)
	 *
	 */
	public void cleanHomeListByKey() {
		try {
			// 更新init接口版本值
			entityService.colCofValue();

			String type = CACHE_NAME.TIME;
			// 要清理的缓存的key值:init,index,文章的列表和banner,修行社bannner,消息的banner
			String[] keys = { "index_", "initApp_", "config", "open_app", "sns_i", "find_ls" };
			for (int i = 0; i < keys.length; i++) {
				cacheProvider.removeMatch(
						new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MATCH_ALL_CACHE_QUEUE, type + keys[i]));
			}
		} catch (Exception e) {
			logger.error("method:cleanHomeListByKey arg:{ }" + "   desc:" + ExceptionUtils.getStackTrace(e));

		}
	}

	public void initProductSingle(final String mid) {
		ReturnData rd = (ReturnData) (new CacheableTemplate(this.cacheProvider) {
			@Override
            protected ReturnData getFromRepository() {
				try {
					return CacheService.this.goodsService.getGoodsDetail(mid);
				} catch (Exception var2) {
					var2.printStackTrace();
					CacheService.logger.error("method:initProductSingle arg:{mid:" + mid + "  desc:"
							+ ExceptionUtils.getStackTrace(var2));
					return ReturnData.ERROR;
				}
			}

			protected boolean canPutToCache(ReturnData returnValue) {
				return returnValue != null && returnValue.getRet().intValue() == 0;
			}
		}).execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.INSTANCE_PRODUCT_ID_OR_MID_PREFIX + mid), true);
	}

	public void initProducts() {
		boolean repeat = false;
		Integer lastRequst = (Integer) (new CacheableTemplate(this.cacheProvider) {
			@Override
            protected Integer getFromRepository() {
				return Integer.valueOf(DateUtils.nowInSeconds());
			}

			protected boolean canPutToCache(Integer returnValue) {
				return returnValue.intValue() > 0;
			}
		}).execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				"CacheService.init_products_time"), true);
		// 超过1小时可重复预热
		repeat = DateUtils.nowInSeconds() - lastRequst.intValue() < 3600;
		if (!repeat || isFirstProductInit) {
			isFirstProductInit = false;
			Executors.newFixedThreadPool(1).submit(new Runnable() {
				public void run() {
					int tatolPage = CacheService.this.goodsService.selectTotal() / initPerPageSize + 1;
					LinkedBlockingQueue queue = new LinkedBlockingQueue();

					for (int i = 1; i <= tatolPage; ++i) {
						try {
							queue.put(Integer.valueOf(i));
						} catch (InterruptedException var5) {
							var5.printStackTrace();
						}
					}

					CacheService.this.initOnePageProductSingle(queue);
				}
			});
		}

	}

	private void initOnePageProductSingle(BlockingQueue<Integer> queue) {
		while (true) {
			try {
				int e = 1;

				while (true) {
					try {
						e = ((Integer) queue.take()).intValue();

						try {
							Paged e1 = new Paged();
							e1.setCurPage(e);
							e1.setPageSize(initPerPageSize);
							e1.setWheres(new Goods());
							List result = this.goodsService.findGoodsMidByPage(e1);
							Iterator i$ = result.iterator();

							while (i$.hasNext()) {
								Goods singelMid = (Goods) i$.next();
								this.initProductSingle(singelMid.getMid());
							}
						} catch (Exception var7) {
							var7.printStackTrace();
						}
					} catch (Exception var8) {
						logger.error(var8.getMessage(), var8.getCause());
					}

					logger.info("产品单个信息缓存:第" + e + "页成功！");
					Thread.sleep(1000L);
				}
			} catch (Exception var9) {
				logger.error(var9.getMessage(), var9.getStackTrace());
			}
		}
	}

	public void initCompositions() {
		boolean repeat = false;
		Integer lastRequst = (Integer) (new CacheableTemplate(this.cacheProvider) {
			@Override
            protected Integer getFromRepository() {
				return Integer.valueOf(DateUtils.nowInSeconds());
			}

			protected boolean canPutToCache(Integer returnValue) {
				return returnValue.intValue() > 0;
			}
		}).execute(new CacheKey("bevol", CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE, "CacheService.init_composition_time"),
				true);
		// 超过半小时可重复预热
		repeat = DateUtils.nowInSeconds() - lastRequst.intValue() < 1800;
		if (!repeat || isFirstCompositionInit) {
			isFirstCompositionInit = false;
			Executors.newFixedThreadPool(1).submit(new Runnable() {
				public void run() {
					int tatolPage = CacheService.this.compositionService.selectTotal() / initPerPageSize + 1;
					LinkedBlockingQueue queue = new LinkedBlockingQueue();

					for (int i = 1; i <= tatolPage; ++i) {
						try {
							queue.put(Integer.valueOf(i));
						} catch (InterruptedException var5) {
							var5.printStackTrace();
						}
					}

					CacheService.this.initOnePageCompositionSingle(queue);
				}
			});
		}
	}

	private void initOnePageCompositionSingle(LinkedBlockingQueue queue) {
		while (true) {
			try {
				int e = 1;

				while (true) {
					try {
						e = ((Integer) queue.take()).intValue();

						try {
							Paged e1 = new Paged();
							e1.setCurPage(e);
							e1.setPageSize(initPerPageSize);
							e1.setWheres(new Composition());
							List result = this.compositionService.findCompositionKeyIdByPage(e1);
							Iterator i$ = result.iterator();

							while (i$.hasNext()) {
								Composition singelMidAndId = (Composition) i$.next();
								this.initCompositionsSingle(singelMidAndId);
							}
						} catch (Exception var7) {
							var7.printStackTrace();
						}
					} catch (Exception var8) {
						logger.error(var8.getMessage(), var8.getCause());
					}

					logger.info("产品成分单个信息缓存:第" + e + "页成功！");
					Thread.sleep(1000L);
				}
			} catch (Exception var9) {
				logger.error(var9.getMessage(), var9.getStackTrace());
			}
		}
	}

	private void initCompositionsSingle(final Composition singelMidAndId) {
		compositionService.getCompositionById(singelMidAndId.getId());
		compositionService.getCompositionByMid(singelMidAndId.getMid());
		// todo api代码的缓存时间淘汰事件变更后 此处直接调用service
	}

	/**
	 * 设置缓存版本
	 * @param version
	 * @return
	 */
	public ReturnData setCacheVersion(String version){
		CACHE_NAME.VERSION = version;
		return new ReturnData(CACHE_NAME.VERSION);
	}

	/**
	 * 获取缓存版本
	 * @return
	 */
	public ReturnData getCacheVersion(){
		return new ReturnData(CACHE_NAME.VERSION);
	}


	/**
	 * 清单个产品（目前产品是两天缓存）
	 *
	 * @param mid
	 */
	public void cleanProducts(String mid) {
		try {
			//产品实体
			cacheProvider.remove(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
					CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_PRODUCT_MID_PREFIX,mid)));
			//产品详情
			cacheProvider.remove(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,

					CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_PRODUCT_ID_OR_MID_PREFIX, mid)));

		} catch (Exception ex) {
			logger.error("method:cleanProducts arg:{mid:" + mid + "   desc:" + ExceptionUtils.getStackTrace(ex));
		}
	}

	/**
	 * 批量清除产品
	 * @param ids
	 */
	public void cleanProductsBatch(String ids){
		String[] idsArr = ids.split(",");
		for(String id : idsArr){
			String mid = goodsMapper.getMidById(Long.parseLong(id));
			cleanProducts(mid);
		}
	}

	/**
	 * 清单个=成分（目前成分是永久缓存）  by mid
	 *
	 * @param mid
	 */
	public void cleanComposition(String mid) {
		try {
			cacheProvider.remove(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE,
					CACHE_NAME.createInstanceForeverKey(CACHE_NAME.INSTANCE_COMPOSITION_ID_OR_MID_PREFIX, mid)));
		} catch (Exception ex) {
			logger.error("method:cleanComposition arg:{mid:" + mid + "   desc:" + ExceptionUtils.getStackTrace(ex));
		}
	}

	/**
	 * 清单个=成分（目前成分是永久缓存） by id
	 * @param id
	 */
	public void cleanComposition(Long id){
		try {
			cacheProvider.remove(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE,
					CACHE_NAME.createInstanceForeverKey(CACHE_NAME.INSTANCE_COMPOSITION_ID_OR_MID_PREFIX, id+"")));
		} catch (Exception ex) {
			logger.error("method:cleanComposition arg:{id:" + id + "   desc:" + ExceptionUtils.getStackTrace(ex));
		}
	}

	/**
	 * 清单个=成分（目前成分是永久缓存） by id,mid
	 * @param mid
	 * @param id
	 */
	public void cleanComposition(String mid, Long id){
		cleanComposition(mid);
		cleanComposition(id);
	}

	/**
	 * 清永久缓存 根据匹配的instance key
	 *
	 * @param key
	 */
	public void cleanCacheForeverByKey(String key) {
		try {
			if (StringUtils.isEmpty(key) || key.length() <= 5) {
			} else {
				cacheProvider.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE, key));
			}
			return;
		} catch (Exception e) {
			logger.error("method:cleanCacheForeverByKey arg:{key:\"" + key + "\"}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));

		}
	}

	/**
	 * 清2天的缓存 根据匹配的instance key
	 *
	 * @param key
	 */
	public void cleanCache2DayByKey(String key) {
		try {
			if (StringUtils.isEmpty(key) || key.length() <= 5) {
			} else {
				cacheProvider
						.remove(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE, key.trim()));

			}
			return;
		} catch (Exception e) {
			logger.error("method:cleanCacheForeverByKey arg:{key:\"" + key + "\"}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));

		}
	}


	/**
	 * 后台清除缓存
	 *            3永久 1时效性的 2时效性的
	 * @return
	 */
	public ReturnData cleanRedisCacheList(String key) {
		try {
			boolean clean = false;
			int cacheType = 0;
			String type = key.substring(0, 2);
			if ("m_".equals(type)) {
				// 时效
				cacheType = 1;
				clean = true;
			}
			if ("f_".equals(type)) {
				// 永久
				cacheType = 2;
				clean = true;
			}
			if (clean) {
				clean = cleanCacheListByKeyType(cacheType, key);
			} else {
				return new ReturnData("清除失败,操作有误");
			}

			return ReturnData.SUCCESS;
		} catch (Exception e) {
			logger.error("method:CacheService.cleanRedisCacheList arg:{	key:" + key + "	}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));
		}
		return ReturnData.ERROR;
	}

	/**
	 * 清除时效性的和永久的key
	 * 
	 * @param type
	 *            1时效性的 2永久的
	 * @param key
	 */
	public boolean cleanCacheListByKeyType(Integer type, String key) {
		try {
			boolean clean = false;
			if (!StringUtils.isEmpty(key) && null != type) {
				if (2 == type) {
					if (key.length() > 5) {
						cacheProvider
								.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE, key));
						clean = true;
					}
				} else {
					cacheProvider
							.removeMatch(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.MATCH_ALL_CACHE_QUEUE, key));
					clean = true;
				}
			}

			return clean;
		} catch (Exception e) {
			logger.error("method:cleanCacheListByKey arg:{key:" + key + ",type:" + type + "}" + "   desc:"
					+ ExceptionUtils.getStackTrace(e));

		}
		return false;
	}
}
