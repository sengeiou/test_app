package cn.bevol.internal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import cn.bevol.entity.service.BaseService;
import cn.bevol.model.entity.EntityCPSGoodsLOG;
import cn.bevol.util.ReturnData;

@Service
public class EntityCPSGoodsLOGService extends BaseService {
    
	@Resource
	private MongoTemplate  mongoTemplate;
	 
	
	/**
	 * 添加CPS日志
	 * @param entityCPSGoodsLOG   CPS
	 */
	public  void insertEntityCPSGoodsLOG(EntityCPSGoodsLOG  entityCPSGoodsLOG) {
			  super.save(EntityCPSGoodsLOG.ENTITY_CPS_GOODSLOG, entityCPSGoodsLOG);
	}
	   
	  
	/**
	 * 根据 日志ID 获取日志信息
	 * @param EntityCPSGoodsLOGByid CPS日志ID
	 * @return
	 */
	public EntityCPSGoodsLOG   getEntityCPSGoodsLOGByid(Long EntityCPSGoodsLOGByid) {
	 return 	(EntityCPSGoodsLOG) super.getEntityById(EntityCPSGoodsLOG.class, EntityCPSGoodsLOGByid);
	}
	
	
	/**
     * 查询CPS表中不存在的商品信息
	 * @param pager 当前页
	 * @param size  每页展示数据
	 * @param hidden  0为展示所有数据,1为只展示CPS商品组中没有的数据
	 */
	public ReturnData<?>  lisEntityCPSGoodsLOGlatestData(Integer pager,Integer size, Integer state){
		 
		try {
			
			//查询,未被同步到CPS商品中你的数据
			Query query =null;
			if(null==state||state==0) {
				query =new Query();
			}else {
				//获取最近爬取数据的时间
				query =new Query();
				query.with(new Sort(Direction.DESC, "createLogTime"));
				EntityCPSGoodsLOG  entityCPSGoodsLOG  =	 mongoTemplate.findOne(query, EntityCPSGoodsLOG.class,EntityCPSGoodsLOG.ENTITY_CPS_GOODSLOG);
                //查询距离现在最近的爬取时间的数据				
				query =new Query(Criteria.where("isSynchronization").is(EntityCPSGoodsLOG.ISSYNCHRONIZATION_NO).and("createLogTime").is(entityCPSGoodsLOG.getCreateLogTime()));
			}   
			//数据总量
			pager   =pager==null?1:pager;
			size   =size==null?20:size;
			Long   count =mongoTemplate.count(query, EntityCPSGoodsLOG.class,EntityCPSGoodsLOG.ENTITY_CPS_GOODSLOG);
			query.skip((pager-1)*size).limit(size);
			//分页后的数据
			List<EntityCPSGoodsLOG>  entityCPSGoodsLOGs= mongoTemplate.find(query, EntityCPSGoodsLOG.class,EntityCPSGoodsLOG.ENTITY_CPS_GOODSLOG);
			Map<String,Object>  map =new HashMap<>();
			map.put("entityCPSGoodsLOGs", entityCPSGoodsLOGs);
			map.put("count", count);
			return new ReturnData<>(map,0, "成功");
		}catch (Exception e) {
             e.printStackTrace();
             return new ReturnData<>(-1,e.getMessage());
		}
	}
	   
	 
}
