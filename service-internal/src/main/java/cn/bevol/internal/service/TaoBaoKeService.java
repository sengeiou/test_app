package cn.bevol.internal.service;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.io97.utils.DateUtils;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.UatmTbkItem;
import com.taobao.api.request.TbkUatmFavoritesItemGetRequest;
import com.taobao.api.response.TbkUatmFavoritesItemGetResponse;

import cn.bevol.entity.service.BaseService;
import cn.bevol.model.entity.EntityAdCps;
import cn.bevol.model.entity.EntityAdCpsChannel;
import cn.bevol.model.entity.EntityCPSGoodsGroup;
import cn.bevol.model.entity.EntityCPSGoodsLOG;
/**
 * 淘宝库服务类
 * @author zqc
 *
 */
@Service
public class TaoBaoKeService extends BaseService {
     
	@Resource
	private EntityCPSGoodsGroupService    entityCPSGoodsGroupService;
	
	@Resource
	private MongoTemplate mongoTemplate;
	
	@Resource
	private EntityCPSGoodsService  cpsGoodsService;
	
	
	 /// 淘宝客文档 http://bigdata.taobao.com/docs/api.htm?spm=a219a.7395905.0.0.rt2VOR&apiId=26619
	/**
	 * 返回字段
	 */
	public static final String FIELDS="num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,click_url,nick,seller_id,volume,tk_rate,zk_final_price_wap,shop_title,event_start_time,event_end_time,type,status,category,coupon_click_url,coupon_end_time,coupon_info,coupon_start_time, coupon_total_count,coupon_remain_count,total_results";
	
	/**
	 * URL,只能使用正式的接口
	 */
	public static final String URL="http://gw.api.taobao.com/router/rest";
	
	/**
	 * 安卓的key
	 */
	public static final Long ANDROID_APPKEY=24786842L;
	
	/**
	 * 苹果的key
	 */
	public static final Long IOS_APPKEY=24786384L;
	
	/**
	 * ANDROID_SECRET
	 */
	public static final String ANDROID_SECRET="4518fd8c1105cb81bc611cf5aa0a116a";
	/**
	 * IOS_SECRET 
	 */
	public static final String IOS_SECRET="c271b72971116f282f6ed000ec606f68";
	 
	 /**
	  * IOS推广位id
	  */
	public static final Long IOS_ADZONEID=216594984L;
	/**
	 * Android推广位id
	 */
	public static final Long ANDROID_ADZONEID=216598971L;
	
	/**
	 * 每页展示数据
	 */
     public static final Long PAGE_SIZE=20L;
     
     /**
      * CPS创建时间,会随着刷新的时间而改变
      */
     private Long  time =0L;
     
     /**
      * 更新CPS时间
      */
     @Deprecated
     public void refreshCpsTime() {
    	 List<EntityAdCps>  entityCPSGoods =  mongoTemplate.find(new Query(),EntityAdCps.class);
          Long time =new Date().getTime();//设置CPS更新时间
    	 for (int i = 0; i < entityCPSGoods.size(); i++) {
    		 EntityAdCps  entityAdCps  = entityCPSGoods.get(i);
    		 List<EntityAdCpsChannel>  entityAdCpsChannels = entityAdCps.getEntityAdCpsChannel();
    		 for(int y =0;y<entityAdCpsChannels.size();y++) {
    			 EntityAdCpsChannel  entityAdCpsChannel = entityAdCpsChannels.get(y);
    			   //如果是高佣金
    			   if(entityAdCpsChannel.getIsCommission()==EntityAdCpsChannel.ISCOMMISSION_YES) {
    				   if(entityAdCpsChannel.getChannelEndTimeCommission().longValue()<=time){//时间如果小于等于当前的时间，则代表cps高佣金到期了
    					   entityAdCpsChannel.setIsCommission(EntityAdCpsChannel.ISCOMMISSION_NO);
    				   }
    			   }
                  //判断普通佣金时间
    			   if(entityAdCpsChannel.getChannelEndTime().longValue()<=time) {
    				   entityAdCpsChannel.setHidden(1);
    			   }
    		 }
    		 //更新数据
    		 mongoTemplate.updateFirst(new Query(Criteria.where("id").is(entityAdCps.getId())), new Update().set("entityAdCpsChannel", entityAdCpsChannels), EntityAdCps.class);
		}
     }
   
     
	/**
	 * 刷新CPS数据
	 */
	  public  void refreshCps() throws Exception {
		  time = new Date().getTime();
		  //日志信息，会在数据同步完毕之后插入日志表中
		  Collection<EntityCPSGoodsLOG>   entityCPSGoodsLOGs   = null;
		  //获取商品组中的全部数据
		  List<EntityCPSGoodsGroup>    taoBaioKeAlliances= entityCPSGoodsGroupService.listEntityCPSGoodsGroup();
		  
		  for (EntityCPSGoodsGroup taoBaioKeAlliance : taoBaioKeAlliances) {
			  //低用迭代添加数据
			 Map<Long,EntityCPSGoodsLOG>  map= null; 
			 try {
				 map =getGoodsByTaoBaoKe(taoBaioKeAlliance.getFavoritesId());
				 if(null==map) {
					 continue;
				 }
			 }catch(Exception e) {
				 e.printStackTrace();
				 continue;
			 }
			  if(entityCPSGoodsLOGs==null) {
				  entityCPSGoodsLOGs  =	 map.values();
			  }else {
				  entityCPSGoodsLOGs.addAll(map.values());
			  }
             //保存日志，然后更新，CPS商品信息
			  //mongoTemplate.insert(batchToSave, collectionName);
			// mongoTemplate.insert(entityCPSGoodsLOGs,ENTITY_CPS_GOODS_LOG);//保存全部的日志信息
			 //
			 List<EntityAdCps>  entitys=      cpsGoodsService.listEntityCpsInId(map.keySet());
			 //同步最新的数据
			 for (EntityAdCps entityAdCps : entitys) {
				 //设置该数据已经存在，CPS商品中了
				 map.get(entityAdCps.getTaobaoKeGoodsId()).setIsSynchronization(EntityCPSGoodsLOG.ISSYNCHRONIZATION_YES);
				 //获取最新爬取的数据
				 EntityCPSGoodsLOG  entityCPSGoodsLOG  =   map.get(entityAdCps.getTaobaoKeGoodsId());
				     entityCPSGoodsLOG.setIsSynchronization(EntityCPSGoodsLOG.ISSYNCHRONIZATION_YES);
				     Update update =new Update();
				     update.set("oldName", entityCPSGoodsLOG.getGoodsName());
				     //获取渠道
				     List<EntityAdCpsChannel>      entityAdCpsChannels =  entityAdCps.getEntityAdCpsChannel();
				     for(int i=0;i<entityAdCpsChannels.size();i++) {
				    	  //迭代到渠道为天猫的,进行数据更改
				    	 if(entityAdCpsChannels.get(i).getChannelType()==EntityAdCpsChannel.CHANNEL_TYPE_TIANMAO||entityAdCpsChannels.get(i).getChannelType()==EntityAdCpsChannel.CHANNEL_TYPE_TAOBAO) {
				    		 EntityAdCpsChannel entityAdCpsChannel =entityAdCpsChannels.get(i);
				    		 entityAdCpsChannel.setSmallImages(entityCPSGoodsLOG.getGoodsSmallImages());
				    		 entityAdCpsChannel.setAndroidLink(entityCPSGoodsLOG.getAndroidURL());
				    		 entityAdCpsChannel.setIosLink(entityCPSGoodsLOG.getIOSURL());
				    		 entityAdCpsChannel.setImgSrc(entityCPSGoodsLOG.getImage());
				    		 entityAdCpsChannel.setOriginalPrice(entityCPSGoodsLOG.getGoodsReservePrice() );
				    		 entityAdCpsChannel.setChannelGoodsId(entityCPSGoodsLOG.getCpsCreateGoodsId());
				    		 break;
				    	 }
				     }
				     update.set("entityAdCpsChannel", entityAdCpsChannels);//写入渠道信息
				     Query query =new Query(Criteria.where("goodsId").is(entityAdCps.getTaobaoKeGoodsId()));
				     mongoTemplate.updateFirst(query, update, EntityAdCps.class);
			}
		 }
		  System.out.println("\n\n插入日志数量:"+entityCPSGoodsLOGs.size());
		  Iterator<EntityCPSGoodsLOG>   iter =  entityCPSGoodsLOGs.iterator();
		   while(iter.hasNext()) {
			   EntityCPSGoodsLOG  entityCPSGoodsLOG  = iter.next();
			   System.out.println(entityCPSGoodsLOG);
			   //插入日志		  ,保存全部的日志信息
			   super.save(EntityCPSGoodsLOG.ENTITY_CPS_GOODSLOG, entityCPSGoodsLOG);
		   }
	  }
	  
	  /**
	   * 根据选品库id 获取选品库
	   * @param favoritesId  选品库id
	   * @return 选品库id中的所有数据,key为商品id ,value是日志
	   * @throws Exception
	   */
	  public  Map<Long,EntityCPSGoodsLOG>  getGoodsByTaoBaoKe(Long favoritesId) throws Exception {
		  TaobaoClient androidClient = new DefaultTaobaoClient(URL, ANDROID_APPKEY.toString(), ANDROID_SECRET);
		  TaobaoClient iosClient = new DefaultTaobaoClient(URL, IOS_APPKEY.toString(), IOS_SECRET);
		  //存储 获取到的数据
		  Map<Long,EntityCPSGoodsLOG> map =new HashMap<>();
		  TbkUatmFavoritesItemGetRequest  req = new TbkUatmFavoritesItemGetRequest();
		  req.setAdzoneId(ANDROID_ADZONEID);
		  req.setFavoritesId(favoritesId);
		  req.setFields(FIELDS);
		  req.setPageNo(1L);
		  req.setPageSize(PAGE_SIZE);
		  TbkUatmFavoritesItemGetResponse androidResponse = androidClient.execute(req);
		  
		  String body  =androidResponse.getBody();
		   //如果没有从淘宝那边获取到数据就返回空
		  if(null==body) {
			   return null;
		   }
		  @SuppressWarnings({ "unchecked", "rawtypes" })
		   Map<String,Map> androMap  = (Map<String, Map>) JSON.parse(body);
		  
		  //获取总条数
		  int total_results = (int) androMap.get("tbk_uatm_favorites_item_get_response").get("total_results");
	     //获取总页数
	      Long pageCount = (total_results%PAGE_SIZE==0?total_results/PAGE_SIZE:((total_results/PAGE_SIZE)+1));
	      //获取安卓URL和其他数据
	      for(Long i=1L;i<=pageCount;i++) {
	    	  req = new TbkUatmFavoritesItemGetRequest();
	    	  req.setAdzoneId(ANDROID_ADZONEID);//
	    	  req.setFavoritesId(favoritesId);
	    	  req.setFields(FIELDS);
	    	  req.setPageNo(i);
	    	  req.setPageSize(PAGE_SIZE);
	    	  TbkUatmFavoritesItemGetResponse  response = androidClient.execute(req);
			  List<UatmTbkItem>   uatmTbkItems=response.getResults();
			  for (UatmTbkItem uatmTbkItem : uatmTbkItems) {
				  EntityCPSGoodsLOG  entityCPSGoodsLOG   = mapByEntityCPSGoodsLOG(uatmTbkItem);
				  entityCPSGoodsLOG.setFavoritesId(favoritesId);
				  map.put(uatmTbkItem.getNumIid(), entityCPSGoodsLOG);
			  }
	      }
	      //获取IOS数据
	      for(Long i=1L;i<=pageCount;i++) {
	    	  req = new TbkUatmFavoritesItemGetRequest();
	    	  req.setAdzoneId(IOS_ADZONEID);
	    	  req.setFavoritesId(favoritesId);
	    	  req.setFields(FIELDS);
	    	  req.setPageNo(i);
	    	  req.setPageSize(PAGE_SIZE);
	    	  //获取返回的数据		  
	    	  TbkUatmFavoritesItemGetResponse  response = iosClient.execute(req);
	    	  List<UatmTbkItem>   iosuatmTbkItems =response .getResults();
	    	  for (UatmTbkItem uatmTbkItem : iosuatmTbkItems) {
	    		  EntityCPSGoodsLOG  entityCPSGoodsLOG  = map.get(uatmTbkItem.getNumIid());
	    		  entityCPSGoodsLOG.setIOSURL(uatmTbkItem.getItemUrl());
	    	  }
	      }
		 return map;
	  }
	  
	  /**
	   * 数据转换
	   * @param map
	   * @return 转换后的实体数据
	   */
	  private  EntityCPSGoodsLOG   mapByEntityCPSGoodsLOG(UatmTbkItem uatmTbkItem) {
		  EntityCPSGoodsLOG  entityCPSGoodsLOG  =new EntityCPSGoodsLOG();
		  entityCPSGoodsLOG.setCreateLogTime(this.time);//创建时间
		  entityCPSGoodsLOG.setId(super.getUniqueId());
		  //写入渠道信息
		  entityCPSGoodsLOG.setChannelType(EntityAdCpsChannel.CHANNEL_TYPE_TIANMAO);
		  entityCPSGoodsLOG.setCpsCreateGoodsId(uatmTbkItem.getNumIid());//商品id
		  entityCPSGoodsLOG.setGoodsName(uatmTbkItem.getTitle());//商品名称
		  entityCPSGoodsLOG.setImage(uatmTbkItem.getPictUrl());//商品图片
		  entityCPSGoodsLOG.setGoodsSmallImages( uatmTbkItem.getSmallImages());//商品小图
		  entityCPSGoodsLOG.setGoodsReservePrice( uatmTbkItem.getReservePrice());//商品一口价/原价
		  entityCPSGoodsLOG.setGoodsZKFinalPrice( Float.parseFloat(uatmTbkItem.getZkFinalPrice()));//商品折扣价
		  entityCPSGoodsLOG.setGoodsSellerType(Integer.parseInt(uatmTbkItem.getUserType().toString()));//卖家类型
		  entityCPSGoodsLOG.setGoodsSellerAddress(uatmTbkItem.getProvcity());//商品销售地址
          entityCPSGoodsLOG.setAndroidURL(uatmTbkItem.getItemUrl());//安卓商品链接
		  entityCPSGoodsLOG.setGoodsClickUrl(uatmTbkItem.getClickUrl());//淘宝客链接
		  entityCPSGoodsLOG.setGoodsSellerName(uatmTbkItem.getNick());//商品卖家昵称
		  entityCPSGoodsLOG.setGoodsSellerId(uatmTbkItem.getSellerId());//商品卖家id
		  entityCPSGoodsLOG.setGoodsVolume( uatmTbkItem.getVolume());//销售数量
		  entityCPSGoodsLOG.setGoodsTKRate(Float.parseFloat(uatmTbkItem.getTkRate()));//商品收入比例 20.00 就是20%
		  entityCPSGoodsLOG.setGoodsZKFinalPriceWap(Float.parseFloat(uatmTbkItem.getZkFinalPriceWap()));//无线折扣价格
		  entityCPSGoodsLOG.setGoodsShopTitle(uatmTbkItem.getShopTitle());//
		  entityCPSGoodsLOG.setEventStarTime(uatmTbkItem.getEventStartTime().getTime());//招商开始时间
		  entityCPSGoodsLOG.setEventEndTime( uatmTbkItem.getEventEndTime().getTime());//招商结束时间
		  entityCPSGoodsLOG.setGoodsType(Integer.parseInt(uatmTbkItem.getType().toString()));//商品类型
		  entityCPSGoodsLOG.setGoodsState(Integer.parseInt(uatmTbkItem.getStatus().toString()));//商品状态
		 // entityCPSGoodsLOG.setCategory(Integer.parseInt(uatmTbkItem.getCategory()==null?null:uatmTbkItem.getCategory().toString()));//后台一级类目
		  entityCPSGoodsLOG.setGoodsCouponClickUrl(uatmTbkItem.getCouponClickUrl());//优惠卷领取时间
		  try {
			//优惠卷结束时间
			entityCPSGoodsLOG.setCouponEndTime(uatmTbkItem.getCouponEndTime()==null?0:DateUtils.dateParseIntDate(uatmTbkItem.getCouponEndTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			entityCPSGoodsLOG.setCouponEndTime(0);
		}
		  try {
			//优惠卷开始时间
			entityCPSGoodsLOG.setCouponStartTime( uatmTbkItem.getCouponStartTime()==null?
					  0:DateUtils.dateParseIntDate(uatmTbkItem.getCouponStartTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			//优惠卷开始时间
			entityCPSGoodsLOG.setCouponStartTime(0);
		}
		  entityCPSGoodsLOG.setCouponInfo(uatmTbkItem .getCouponInfo());//商品优惠卷，优惠面额
		  entityCPSGoodsLOG.setCouponTotalCount(Integer.parseInt(uatmTbkItem.getCouponTotalCount()==null?"0":uatmTbkItem.getCouponTotalCount().toString()));//优惠券总量
		  entityCPSGoodsLOG.setCouponRemainCount(Integer.parseInt(uatmTbkItem.getCouponRemainCount()==null?"0":uatmTbkItem.getCouponRemainCount().toString()));//优惠卷剩余数量
		  return entityCPSGoodsLOG;
	  }
	 
}
