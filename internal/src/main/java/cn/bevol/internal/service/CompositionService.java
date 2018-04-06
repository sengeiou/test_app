package cn.bevol.internal.service;


import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import cn.bevol.internal.cache.CacheKey;
import cn.bevol.internal.cache.CacheableTemplate;
import cn.bevol.internal.cache.MakeCache;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.db.Paged;
import cn.bevol.internal.dao.mapper.CompositionOldMapper;
import cn.bevol.internal.entity.model.Composition;
import cn.bevol.internal.entity.model.Used;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.StringUtil;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.http.HttpUtils;
import flexjson.JSONDeserializer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CompositionService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(CompositionService.class);


	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	CompositionOldMapper compositionOldMapper;

	/**
	 * 获取该产品的所有成分列表
	 * @return
	 */
	public List<Composition> getCompositionByIds(String ids) {
		try {
			//根据成分id 获取成分列表
			List<Composition> list=new ArrayList<Composition>();
			String[] idss=ids.split(",");
			for(int j=0;j<idss.length;j++){
				//在所有產品中查找
				Long id= Long.parseLong(idss[j]);
				//获取成分和成分的使用目的
				Composition composition=getCompositionById(id);
				if(composition!=null&&composition.getId()>0) {
					if(composition.getPid()>0){
						Composition pComposition=getCompositionById(composition.getPid());
						long pid=pComposition.getId();
						long curid=composition.getId();
						pComposition.setPid(pid);
						pComposition.setId(curid);
						pComposition.setTitle(composition.getTitle());
						composition=pComposition;
					}
					list.add(composition);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error("method:getCompositionByIds arg:{"  +ids+ "   desc:" +  ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	/**
	 * 获取成分使用目的(hq_composition,hq_used)
	 *
	 * @return
	 */
	public Composition getCompositionById(final long id) {
		return new CacheableTemplate<Composition>(cacheProvider) {
			@Override
			protected Composition getFromRepository() {
				try {
					Composition cp = compositionOldMapper.getById(id);
					if (!StringUtils.isBlank(cp.getUsed())) {
						List<Used> useds = compositionOldMapper.getUsedsByUid(cp.getUsed());
						cp.setUseds(useds);
					}
					//获取成分使用目的(..保湿剂中含有的成分的使用目的)
					return cp;
				} catch (Exception e) {
					logger.error("method:getCompositionById arg:{" + "   desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}

			@Override
			protected boolean canPutToCache(Composition returnValue) {
				return (returnValue != null && returnValue.getId() > 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE,
				CACHE_NAME.createInstanceForeverKey(CACHE_NAME.INSTANCE_COMPOSITION_ID_OR_MID_PREFIX , id+"")), true);
	}


	/**
	 * 获取成分使用目的(hq_composition,hq_used)
	 *
	 * @return
	 */
	public Composition getCompositionByMid(final String mid) {
		return new CacheableTemplate<Composition>(cacheProvider) {
			@Override
			protected Composition getFromRepository() {
				try {
					Composition cp = compositionOldMapper.getByMid(mid);
					if (!StringUtils.isBlank(cp.getUsed())) {
						List<Used> useds = compositionOldMapper.getUsedsByUid(cp.getUsed());
						cp.setUseds(useds);
						/*StringBuffer sb=new StringBuffer();
						for(int i=0;i<useds.size();i++) {
							sb.append(useds.get(i).getTitle());
							if(i!=useds.size()-1) {
								sb.append(",");
							}
						}
						cp.setUsedName(sb.toString());*/
					}
					//获取成分使用目的(..保湿剂中含有的成分的使用目的)
					return cp;
				} catch (Exception e) {
					logger.error("method:getCompositionById arg:{" + "   desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}

			@Override
			protected boolean canPutToCache(Composition returnValue) {
				return (returnValue != null && returnValue.getId() > 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE,
				CACHE_NAME.createInstanceForeverKey(CACHE_NAME.INSTANCE_COMPOSITION_ID_OR_MID_PREFIX , mid)), true);
	}


	/**
	 * 缓存所有的成分
	 * @return
	 */
	public List<Composition> getAllComposition() {
		return new CacheableTemplate<List<Composition>>(cacheProvider) {
			@Override
			protected List<Composition> getFromRepository() {
				try {
					List<Composition> allTags=compositionOldMapper.getAll();
					return allTags;
				} catch (Exception e) {
					logger.error("method:getAllComposition arg:{" + "   desc:" +  ExceptionUtils.getStackTrace(e));
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<Composition> returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				"CompositionService.getAllComposition_"+CACHE_NAME.VERSION), true);
	}

	/**
	 * 获取所有成分使用目的(hq_used)
	 *
	 * @return
	 */
	public List<Used> getAllUsed() {
		return new CacheableTemplate<List<Used>>(cacheProvider) {
			@Override
			protected List<Used> getFromRepository() {
				try {
					List<Used> useds = compositionOldMapper.getAllUsed();
					//获取成分使用目的(..保湿剂中含有的成分的使用目的)
					return useds;
				} catch (Exception e) {
					logger.error("method:getAllCompositionUsed arg:{" + "   desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}

			@Override
			protected boolean canPutToCache(List<Used> returnValue) {
				return (returnValue != null && returnValue.size() > 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_COMPOSITION_USED_PREFIX)), true);
	}

	/**
	 * 缓存预热用  查询可用成分的总数
	 *
	 * @return
	 */
	public int selectTotal() {
		return compositionOldMapper.selectTotal();
	}

	/**
	 * 缓存预热用  查询主key<id   mid>分页
	 *
	 * @param paged
	 * @return
	 */
	public List findCompositionKeyIdByPage(Paged paged) {
		return compositionOldMapper.findCompositionKeyIdByPage(paged);
	}

}