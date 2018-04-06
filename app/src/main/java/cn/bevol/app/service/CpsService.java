package cn.bevol.app.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import cn.bevol.model.entity.EntityAdCps;
import cn.bevol.model.entity.EntityAdCpsChannel;
import cn.bevol.util.response.ReturnData;

@Service
public class CpsService {
	
	
	@Resource
	private MongoTemplate mongoTemplate;
	
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
			  // 
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
	 

}
