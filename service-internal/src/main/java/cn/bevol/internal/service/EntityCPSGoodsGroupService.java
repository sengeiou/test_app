package cn.bevol.internal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import cn.bevol.entity.service.BaseService;
import cn.bevol.model.entity.EntityCPSGoodsGroup;
import cn.bevol.util.ReturnData;

@Service
public class EntityCPSGoodsGroupService  extends BaseService{
	
	 
	//EntityCPSGoodsGroup
	
	@Autowired
	private MongoTemplate  mongoTemplate;
	/**
	 * entityCPSGoodsGroup 表名称
	 */
	public static final String ENTITY_CPS_GOODS_GROUP="entityCPSGoodsGroup";
	
	/**
	 * 添加商品组
	 * @param entityCPSGoodsGroup   商品组实体
	 * @return
	 */
	public ReturnData<?>    insertEntityCPSGoodsGroup(EntityCPSGoodsGroup  entityCPSGoodsGroup) {
		try {
			if(null==entityCPSGoodsGroup|| 	StringUtils.isBlank(entityCPSGoodsGroup.getGoodsName())
					||StringUtils.isBlank(entityCPSGoodsGroup.getUsername())||null==entityCPSGoodsGroup.getFavoritesId()
					||entityCPSGoodsGroup.getFavoritesId().longValue()<=0) {
				return new ReturnData<>(-1, "请完整填写，商品组名称,创建人，和商品组Id");
			}  
			  
			  if(getEntityCPSGoodsGroupByfavoritesId(entityCPSGoodsGroup.getFavoritesId())!=null) {
				  return new ReturnData<>(-1, "商品组Id已经存在");
			  }
			   entityCPSGoodsGroup.setId(super.getUniqueId());//获取父类唯一不重复的Id
			   super.mongoTemplate.insert(entityCPSGoodsGroup,ENTITY_CPS_GOODS_GROUP);
			   //更新数据信息:
               return ReturnData.SUCCESS;		
		}catch(Exception e) {
			e.printStackTrace();
			return ReturnData.ERROR;
	    }
	}
	
	
	public EntityCPSGoodsGroup getEntityCPSGoodsGroupByfavoritesId(Long favoritesId) {
		if(null==favoritesId||favoritesId.longValue()<=0) {
		  return null;
		}
		return mongoTemplate.findOne(new Query(Criteria.where("favoritesId").is(favoritesId)), EntityCPSGoodsGroup.class,EntityCPSGoodsGroup.ENTITY_CPS_GOODS_GROUP);
		
	}
	
	/**
	 * 查看 商品组信息
	 * @param pager  当前页
	 * @param size   每页展示数据
	 * @param SelectCheck 查询条件(暂未使用)
	 * @return
	 */
	public ReturnData<?>  listEntityCPSGoodsGroup(Integer pager ,Integer size, String selectCheck) {
	   
		try {
			pager   =pager==null?1:pager;
			size   =size==null?20:size;
			Query query =new Query();
			Long   count =mongoTemplate.count(query, EntityCPSGoodsGroup.class,EntityCPSGoodsGroup.ENTITY_CPS_GOODS_GROUP);
			query.skip((pager-1)*size).limit(size);
			List<EntityCPSGoodsGroup> entityCPSGoodsGroups=mongoTemplate.find(query, EntityCPSGoodsGroup.class,EntityCPSGoodsGroup.ENTITY_CPS_GOODS_GROUP);

			Map<String,Object>  map =new HashMap<>();
			map.put("entityCPSGoodsGroups", entityCPSGoodsGroups);
			map.put("Count",count);
			return new ReturnData<>(map,0, "成功");
		}catch(Exception e) {
			return ReturnData.ERROR;
		}
		
	}
	
	

	/**
	 * 查看 商品组信息
	 * @param pager  当前页
	 * @param size   每页展示数据
	 * @param SelectCheck 查询条件(暂未使用)
	 * @return
	 */
	public List<EntityCPSGoodsGroup> listEntityCPSGoodsGroup( ) {
			return mongoTemplate.find(new Query(), EntityCPSGoodsGroup.class,EntityCPSGoodsGroup.ENTITY_CPS_GOODS_GROUP);
	}
	
	
	
}
