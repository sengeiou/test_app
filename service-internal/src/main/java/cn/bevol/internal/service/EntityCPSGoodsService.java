package cn.bevol.internal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import cn.bevol.entity.service.BaseService;
import cn.bevol.entity.service.GoodsService;
import cn.bevol.model.entity.EntityAdCps;
import cn.bevol.model.entity.EntityAdCpsChannel;
import cn.bevol.model.entity.EntityCPSGoodsLOG;
import cn.bevol.util.ReturnData;

/**
 * CPS 服务
 * @author zqc
 *
 */
@Service
public class EntityCPSGoodsService  extends BaseService{
       
	//EntityAdCps CPS 实体，EntityAdCpsChannel 渠道 CPS
	   
	@Resource
	private MongoTemplate  mongoTemplate;
	//ENTITYADCPS 类的Mongo表名称
	
	
	 @Resource
	 private InternalAdCpsService internalAdCpsService;
	 
	 @Resource
	 private EntityCPSGoodsLOGService   cpsGoodsLOGService;
	 
	 @Resource
	 private TaoBaoKeService         taoBaoKeService;
	 
	 @Resource
	 private GoodsService          goodsService;
	 
	 
	 
	 
	 /**
	  * 根据id 查询CPS集合
	  * @param ids  淘宝客商品Id使用逗号分隔
	  * @return
	  */
	 public List<EntityAdCps>  listEntityCpsInId(Set<Long> ids){
         if(null==ids||ids.size()==0) {
        	 return null;
         }
		 Query query = new Query(Criteria.where("goodsId").in(ids));
		 return mongoTemplate.find(query, EntityAdCps.class);
	 }
	 
	 
	 ////> db.entity_ad_cps.update({id:8989 },{$set:{entityAdCpsChannel:{andrioCount:1  } } }  )
	 /**
	  * 保存用户访问过的链接
	  * @param uniqueId     CPS 唯一Id
	  * @param channelType  渠道类型
	  * @param systemType   系统类型
	  * @return  结果
	  */
	 public ReturnData<?>  savaClickCpsLink(Long  cpsId,Integer channelType
			 ,Integer systemType) {
		 //先查询，CPS 广告,然后获取列表中的渠道数据
		 Query query = new Query(Criteria.where("id").is(cpsId));
		 
		 EntityAdCps  entityAdCps  = mongoTemplate.findOne(query, EntityAdCps.class, EntityAdCps.ENTITY_AD_CPS);
		 if(entityAdCps==null) {
			 return new ReturnData<>(-1,"CPS的主键ID不存在");
		 }//{ "id" : "210"}
		 
		 List<EntityAdCpsChannel>   entityAdCpsChannels  =  entityAdCps.getEntityAdCpsChannel();
		 for(int i=0;i<entityAdCpsChannels.size();i++) {
			  // $
			  EntityAdCpsChannel  enAdCpsChannel  = entityAdCpsChannels.get(i);
			  
			  
			   //获取 
			  if(enAdCpsChannel.getChannelType()==channelType) {
				  query =new Query(Criteria.where("id").is(cpsId).and("entityAdCpsChannel.channelGoodsId").in(enAdCpsChannel.getChannelGoodsId()));
				  	Update  update =new Update();
				   if(systemType==2) {
					   update.set("entityAdCpsChannel.$.iosCount",   enAdCpsChannel.getIosCount()+1);
				   }else {
					   update.set("entityAdCpsChannel.$.andrioCount",   enAdCpsChannel.getAndrioCount()+1);
				   }
				   System.out.println("\n\n\n"+mongoTemplate.findOne(query, EntityAdCpsChannel.class)+"\n\n\n" );
				   
				   mongoTemplate.updateMulti(query, update, EntityAdCps.class);
				   
				   
				   return ReturnData.SUCCESS;
			   }
			  //最后一个渠道没有找到数据,
			  if(enAdCpsChannel.equals(entityAdCpsChannels.get(entityAdCpsChannels.size()-1 ))) {
				  return new ReturnData<>(-1,"没有找到该渠道");
			  }
		 }
		 
		 //失败
		 return ReturnData.ERROR;
	 }
	 
	
     /**
      * 根据 CPS的id 查询CPS信息	 
      * @param cpsId  CPSID
      * @return
      */
	 public  ReturnData<?> getCpsByid(Long cpsId) {
		 if(null==cpsId||cpsId<=0) {
			 return ReturnData.ERROR;
		 }
		 Query query = new Query(Criteria.where("id").is(cpsId));
		 EntityAdCps  entityAdCps  =	 mongoTemplate.findOne(query, EntityAdCps.class, EntityAdCps.ENTITY_AD_CPS);
		 return new ReturnData<>(entityAdCps,0,"成功");
	 }
	 
	 
	/**
	 * 验证数据是否正确，
	 * @param entityAdCps
	 * @return
	 */
	private boolean checkCPS(EntityAdCps entityAdCps) {
		    
		if(null==entityAdCps.getTaobaoKeGoodsId()||entityAdCps.getTaobaoKeGoodsId()<=0) {
		   return false; 
		}
		return true;
	}
	
	/**
	 * 修改CPS 信息
	 * @param entityAdCps CPS
	 * @return
	 */
	/**
	 * 修改     CPS信息
	 * @param cpsId                  被修改的CPS编号
	 * @param title                  商品名称
	 * @param isCommission           是否为高佣金
	 * @param channelType            渠道类型
	 * @param channelCommissionType  渠道佣金形式  1.百分比  2.定额
	 * @param channelCommission      渠道佣金
	 * @param channelStartTime       开始时间
	 * @param channelEndTime         结束时间
	 * @param updater                修改人
	 * @param shopIds                店铺id
	 * @param entityAdCpsChannelId   商品渠道Id
	 * @return
	 * @throws Exception 
	 */
	public ReturnData<?> updateAdCps(Long cpsId
			,String title  ,Integer  isCommission   
			,Integer channelCommissionType ,String channelCommissio
			,Long  channelStartTime,Long channelEndTime
			,String updater,String shopIds  ,Long entityAdCpsChannelId,Long goodsid
			) throws Exception {
		 
		
		//表单验证
		if(null==cpsId||cpsId<=0) {
			return  new ReturnData<>(-1,"请携带被修改的cpsId");
		}
		  
          //获取渠道信息，修改渠道数据		
		  EntityAdCps  adCps=(EntityAdCps) this.getCpsByid(cpsId).getResult();
		  if(adCps==null) {
			  return new  ReturnData<>(-1,"CPSId不存在");
		  }
		  
		  
		  if(null!=goodsid&&goodsid.longValue()<=0) {
				if(goodsService.getGoodsById(goodsid)==null) {
					return new ReturnData<>(-1,"美修产品不存在");
				}
		  }
		  List<EntityAdCpsChannel> entityAdCpsChannels  =  adCps.getEntityAdCpsChannel();//获取渠道信息
		  int  index =-1;
		  //迭代获取当前需要被修改的实体
		  for (int i = 0; i < entityAdCpsChannels.size(); i++) {
 			    if(entityAdCpsChannels.get(0).getId().longValue()==entityAdCpsChannelId.longValue()) {
 			    	index=i;
 			    	break;
			    }
                   //迭代到最后仍然未取到数据,执行添加的操作
 			    if(i ==entityAdCpsChannels.size()-1) {
 			    	EntityAdCpsChannel  entityAdCpsChannel  =new EntityAdCpsChannel();
 			    	entityAdCpsChannel.setId(super.getUniqueId());//写入主键的id
 			    	entityAdCpsChannels.add(entityAdCpsChannel);
 			    	index =i+1;
 			    	break;
 			    }
		}
		
		//设置需要修改的实体
		Query query =new Query(Criteria.where("id").is(cpsId));
		Update update =new Update();
	    //验证  商品名称是否需要修改
	   if(!StringUtils.isBlank(title)) {
		   update.set("goodsName", title);
		   
	   }
	   //修改，修改人信息
	   if(!StringUtils.isBlank(updater)) {
		   update.set("updater", updater);
	   }
	   
	   //判断是否需要修改渠道信息
	   if(null!=entityAdCpsChannelId&&entityAdCpsChannelId.longValue()>0) {
		   //验证是否修改高佣金,需要参数，知道渠道信息
		   if(null != isCommission&&isCommission>=0 ) {
			   entityAdCpsChannels.get(index).setIsCommission(isCommission);
		   }
	      
		   //修改  渠道佣金形式  1.百分比  2.定额
		   if(null != channelCommissionType &&channelCommissionType.longValue()>0) {
			   entityAdCpsChannels.get(index).setChannelCommissionType(channelCommissionType);
		   }
		   //渠道佣金
		   if(StringUtils.isBlank(channelCommissio)) {
			   entityAdCpsChannels.get(index).setChannelCommission(channelCommissio);
		   }
		   //佣金开始时间,佣金开始时间必须大于等于当前时间
		   if(null!=channelStartTime&&channelStartTime.longValue()>=new Date().getTime()) {
			     //写入非高佣金时间
			     if(isCommission==EntityAdCpsChannel.ISCOMMISSION_NO) {
			        entityAdCpsChannels.get(index).setChannelStartTime(channelStartTime);
			     //写入高佣金时间
			     }else {
			    	 entityAdCpsChannels.get(index).setChannelStartTimeCommission(channelStartTime);
			     }
		   }
		   
		   //佣金结束时间,佣金结束时间必须大于开始时间
		   if(null!=channelEndTime&&channelStartTime.longValue()>channelStartTime.longValue()) {
			     //写入非高佣金时间
			     if(isCommission==EntityAdCpsChannel.ISCOMMISSION_NO) {
			        entityAdCpsChannels.get(index).setChannelStartTime(channelStartTime);
			     //写入高佣金时间
			     }else {
			    	 entityAdCpsChannels.get(index).setChannelStartTimeCommission(channelStartTime);
			     }
		   }
		   //修改渠道信息
		   update.set("entityAdCpsChannel", entityAdCpsChannels);
	   }
		   mongoTemplate.updateFirst(query, update, EntityAdCps.class);
		return ReturnData.SUCCESS;
	}
	
	/**
	 * 保存CPS
	 * @param entityCPSGoodsLOGId   CPS日志的ID 
	 * @param title                 被添加的CPS商品名称
	 * @param isCommission          是否为高佣金
	 * @param channelType           渠道类型
	 * @param channelCommissionType 渠道佣金形式  1.百分比  2.定额
	 * @param channelCommission     渠道佣金
	 * @param channelStartTime      佣金开始时间
	 * @param channelEndTime        佣金结束时间
	 * @param creator               创建人
	 * @param shopIds               多个店铺
	 * @return  保存结果
	 */
	public ReturnData<?> insertAdCps(Long  entityCPSGoodsLOGId ,String title 
			,Integer  isCommission,Integer channelType  ,Integer channelCommissionType
			,String channelCommissio,Long  channelStartTime,Long channelEndTime
			,String creator,String shopIds,Long goodsid) {
		/**
		 * 数据验证
		 */
		if(null==entityCPSGoodsLOGId||entityCPSGoodsLOGId<=0||StringUtils.isBlank(title)
				||StringUtils.isBlank(creator)||null==channelType||channelType<0||null==goodsid||goodsid.longValue()<=0) {
			return new ReturnData<>(-1, "请携带参数创建人、商品名称和CPS日志的ID、渠道类型、美修商品id");
		}
		
		//根据cps日志Id获取cps日志信息
		EntityCPSGoodsLOG     entity =
				cpsGoodsLOGService.getEntityCPSGoodsLOGByid(entityCPSGoodsLOGId);
		if(entity==null) {
			return new ReturnData<>(-1,"日志不id不存在");
		}
		  
		
		if(goodsService.getGoodsById(goodsid)==null) {
			return new ReturnData<>(-1,"美修产品不存在");
		}
		
		
	try {
		//根据CPS日志中的商品ID获取商品，并且判断，商品是否存在CPS商品列表中，如果存在则不允许添加商品
		EntityAdCps  AdCps  = mongoTemplate.findOne(new Query(Criteria.where("goodsId").is(entity.getCpsCreateGoodsId())), EntityAdCps.class,EntityAdCps.ENTITY_AD_CPS);
		if(AdCps!=null) {
			return new ReturnData<>(-1,"该商品已经存在于CPS商品库中了!!");
		}
		
	}catch (Exception e) {
      e.printStackTrace();
	}
		
		//CPS实体
		EntityAdCps entityAdCps  =new EntityAdCps();
		entityAdCps.setTitle(title);//设置商品名称
		entityAdCps.setImage(entity.getImage());//商品图片
		entityAdCps.setOldName(entity.getTitle());//获取商品原名称
		entityAdCps.setFavoritesId(entity.getFavoritesId().toString() );//淘宝客ID
		entityAdCps.setCreator(creator);//创建者
		entityAdCps.setShopIds(shopIds);//设置多个店铺
		entityAdCps.setEntityCPSLogId(entityCPSGoodsLOGId);//设置和日志相关联的id
		entityAdCps.setTaobaoKeGoodsId(entity.getCpsCreateGoodsId());//获取创建时，CPS商品的ID
		entityAdCps.setGoodsId(goodsid);//美修产品Id
		
		/**
		 * 每个渠道都是有不同的佣金模式
		 */
		EntityAdCpsChannel  entityAdCpsChannel   =new EntityAdCpsChannel();
		entityAdCpsChannel.setId(super.getUniqueId());//写入主键Id
		entityAdCpsChannel.setChannelType(channelType);//写入渠道
		entityAdCpsChannel.setGoodsSellerName(entity.getGoodsSellerName());//写入店铺名称
		entityAdCpsChannel.setIsCommission(isCommission==null?EntityAdCpsChannel.ISCOMMISSION_NO:EntityAdCpsChannel.ISCOMMISSION_YES);//是否高佣金
		entityAdCps.setOldName(entity.getGoodsName());//设置商品原名称
	    entityAdCpsChannel.setChannelCommission(channelCommissio==null?"":channelCommissio);//设置渠道佣金
	    entityAdCpsChannel.setChannelCommissionType(channelCommissionType==null?0:channelCommissionType);//设置渠道佣金形式
		entityAdCpsChannel.setAndrioCount(0);//初始化安卓点击次数
		entityAdCpsChannel.setIosCount(0);//初始化IOS点击次数
	    entityAdCpsChannel.setHidden(1);//初始化商品为显示状态
	    entityAdCpsChannel.setImage(entity.getImage());//获取大图
	    entityAdCpsChannel.setSmallImages(entity.getGoodsSmallImages());//获取小图片
	    entityAdCpsChannel.setChannelGoodsId(entity.getCpsCreateGoodsId());//设置渠道id
	    //判断时间是否正确
	    if(null!=channelEndTime&&channelEndTime.longValue()>0&&null!=channelStartTime&&channelStartTime.longValue()>0) {
	    	 
	    	if(channelStartTime.longValue()>channelEndTime.longValue()) {
	    		return  new ReturnData(-1,"时间不正确!");
	    	}
	    	
	    	//兼容老版本的时间，信息
	    	entityAdCps.setStartTime(channelStartTime==null?0:channelStartTime);//CPS开始时间
	    	entityAdCps.setEndTime(channelEndTime==null?0:channelEndTime);//CPS结束时间
	    	
			//是高佣金的情况
			if(isCommission==EntityAdCpsChannel.ISCOMMISSION_YES) {
				entityAdCpsChannel.setChannelStartTimeCommission(channelStartTime==null?0:channelStartTime);
				entityAdCpsChannel.setChannelEndTimeCommission(channelEndTime==null?0:channelEndTime);
				//非高佣金的情况
			}else {
				entityAdCpsChannel.setChannelStartTime(channelStartTime==null?0:channelStartTime);
				entityAdCpsChannel.setChannelEndTime(channelEndTime==null?0:channelEndTime);
			}
			//添加时有时间则显示，无时间隐藏
			entityAdCps.setHidden(EntityAdCps.HIDDEN_YES);
			
	    }
		entityAdCpsChannel.setChannelLink(entity.getAndroidURL());//写入安卓链接
		entityAdCpsChannel.setIosLink(entity.getIOSURL());//写入苹果链接
		entityAdCpsChannel.setChannelGoodsId( entity.getCpsCreateGoodsId());//写入商品id
		
		if(checkCPS(entityAdCps)) {
			List<EntityAdCpsChannel>   entityAdCpsChannels =new ArrayList<>();
			entityAdCpsChannels.add(entityAdCpsChannel);
			entityAdCps.setEntityAdCpsChannel(entityAdCpsChannels);
			entityAdCps.setId(super.getUniqueId());//写入唯一的主键id
            mongoTemplate.insert(entityAdCps,EntityAdCps.ENTITY_AD_CPS);
            //修改 CPS日志的状态,设置日志为隐藏的状态
            Update update =new Update();
            Query query=new Query(Criteria.where("id").is(entity.getId()));
            update.set("isSynchronization", EntityCPSGoodsLOG.ISSYNCHRONIZATION_YES);
            mongoTemplate.updateFirst(query, update, EntityCPSGoodsLOG.class,EntityCPSGoodsLOG.ENTITY_CPS_GOODSLOG);
			return ReturnData.SUCCESS;
		}
		return ReturnData.ERROR;
	}
	
	/**
	 * 获取全部的cps 信息
	 * @param pager       页码
	 * @param size        每页展示数据
	 * @param selectChek  查询条件(暂未使用)
	 * @return
	 */
	public ReturnData<?>  listEntityAdCps(Integer pager,Integer size,String selectChek) {
		
		pager   =pager==null?1:pager;
		size    =size==null?20:size;
		Query query =new Query();
		Long   count =mongoTemplate.count(query,  EntityAdCps.class,EntityAdCps.ENTITY_AD_CPS);
		query.skip((pager-1)*size).limit(size);
		List<EntityAdCps>  entityCPSGoods =  mongoTemplate.find(query,EntityAdCps.class);
        Map<String,Object>  map =new HashMap<>();
        map.put("entityCPSGoods", entityCPSGoods);
        map.put("count", count);
	  return  new ReturnData<>(map, 0,"成功!");
	}
	
	/**
	 * 隐藏CPS 商品信息
	 * @param adCpsId  cpsId对应Mongo数据库中的uniqueId
	 * @param state    隐藏状态，大于0   0显示 1隐藏
	 */
	public ReturnData<?> hidden(Long adCpsId,Integer state) {
		
		if(state.intValue()!=1&&state.intValue()!=0) {
			return ReturnData.ERROR;
		}
		
		try {
			Query query =new Query((Criteria.where("id").is(adCpsId)));
			Update update =new Update();
			update.set("hidden", 1);//
			mongoTemplate.updateFirst(query, update, EntityAdCps.class,EntityAdCps.ENTITY_AD_CPS);
			return  ReturnData.SUCCESS;
		}catch(Exception e) {
			e.printStackTrace();
			return ReturnData.ERROR;
		}
	}
	
	
	
	  
	
	  
}
