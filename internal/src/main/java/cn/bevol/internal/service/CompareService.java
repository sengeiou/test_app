package cn.bevol.internal.service;

import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.mapper.GoodsOldMapper;
import cn.bevol.internal.entity.model.Goods;
import cn.bevol.model.entity.EntityCompare;
import cn.bevol.model.entity.EntityCompareGoods;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 对比
 * @author hualong
 *
 */
@Service
public class CompareService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(CompareService.class);

	@Autowired
	RedisCacheProvider cacheProvider;


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
    GoodsService goodsService;

	@Autowired
	private GoodsOldMapper goodsOldMapper;



	/**
	 * 后台对比列表
	 * @param sid
	 * @param type
	 * @param sort 筛选人工排序
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	public ReturnListData backCompareList(String tname, String sid, int type, Integer hidden, int sort, int pager, int pageSize) {
		try {
			String table="entity_compare_"+tname;

			//通过sid筛选
			if(null!=sid){
				List<EntityCompare> ecList=mongoTemplate.find(new Query(Criteria.where("sid").is(sid)), EntityCompare.class,table);
				List list=addGoodsInfo(ecList);
				return new ReturnListData(ecList,list.size());
			}

			Query query =new Query();
			Criteria cr = Criteria.where("deleted").is(0);
			if(hidden != null){
				cr.and("hidden").is(hidden);
			}
			if(sort == 1){
				//人工排序
				query.with(new Sort(Direction.DESC, "sort"));
				cr.and("sort").exists(true).ne(null);
			}else{
				Integer skip=(pager-1)*pageSize;
				query.skip(skip).limit(pageSize);
				//type =0 最热
				//type =1 最新
				if(type==0) {
					query.with(new Sort(Direction.DESC, "visitNum"));
				}else if(type==1) {
					query.with(new Sort(Direction.DESC, "createStamp"));
				}
			}

			query.addCriteria(cr);
			List<EntityCompare> entityCompares=mongoTemplate.find(query,  EntityCompare.class, table);

			long total=mongoTemplate.count(query, EntityCompare.class,table);

			List ecList=addGoodsInfo(entityCompares);

			return new ReturnListData(ecList,total);
		} catch (Exception e) {
			Map map=new HashMap();
    		map.put("method", "CompareService.backCompareList");
    		map.put("tname", tname);
    		map.put("sid", sid);
    		map.put("type", type);
			map.put("hidden", hidden);
    		map.put("pager", pager);
    		map.put("pageSize", pageSize);
    		new LogException(e,map);
			return null;
		}
	}

	/**
	 * 设置人工排序
	 * @param id
	 * @param sort
	 * @param sortField
	 * @return
	 */
	public ReturnData compareSort(String tname, Integer id, Integer sort, String sortField){
		Class clazz = getClazz(tname);
		return this.ManualSetSort(clazz, id, sort, sortField);
	}

	/**
	 * 对比广场设置状态
	 * @param tname
	 * @param id
	 * @param hidden
	 * @return
	 */
	public ReturnData compareState(String tname, Integer id, Integer hidden){
		try {
			Class clazz = getClazz(tname);
			mongoTemplate.findAndModify(
					Query.query(Criteria.where("id").is(id)),
					Update.update("hidden", hidden),
					clazz
			);
			return ReturnData.SUCCESS;
		}catch (Exception e){
			Map map=new HashMap();
			map.put("method", "CompareService.compareSort");
			map.put("tname", tname);
			map.put("id", id);
			map.put("hidden", hidden);
			new LogException(e,map);
			return ReturnData.ERROR;
		}
	}

	/**
	 * 根据tname获取class
	 * @param tname
	 * @return
	 */
	private Class getClazz(String tname){
		Class clazz = null;
		if(tname.equals("goods")){
			clazz = EntityCompareGoods.class;
		}
		return clazz;
	}

	public List<EntityCompare> addGoodsInfo(List<EntityCompare> entityCompares){
		for(EntityCompare ec: entityCompares){
			String[] ids=ec.getSid().split("_");
			List goodsList=new ArrayList();
			for(int i=0;i<ids.length;i++){
				Map map=new HashMap();
				//添加通过id查找简单产品的缓存方法?
				Goods goods = goodsOldMapper.getById(Long.parseLong(ids[i]));
				if(null!=goods){
					map.put("title", goods.getTitle());
					map.put("imageSrc", goods.getImageSrc());
					map.put("id", goods.getId());
					map.put("mid", goods.getMid());
					goodsList.add(map);
				}
			}
			ec.setObjList(goodsList);
		}
		return entityCompares;
	}

}