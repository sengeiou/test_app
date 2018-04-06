package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.CompositionOldMapper;
import cn.bevol.app.dao.mapper.DoyenOldMapper;
import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.app.dao.mapper.IndexOldMapper;
import cn.bevol.app.entity.dto.Doyen;
import cn.bevol.model.entityAction.Comment;
import cn.bevol.model.entityAction.CommentLike;
import cn.bevol.model.entityAction.Discuss;
import cn.bevol.model.entityAction.LikeCompare;
import cn.bevol.app.entity.metadata.UserBaseInfo;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.model.vo.CompareGoods;
import cn.bevol.app.entity.vo.GoodsExplain;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.entity.EntityCompare;
import cn.bevol.model.entity.EntityCompareGoods;
import cn.bevol.model.user.MsgExtComment;
import cn.bevol.model.user.MsgExtCommentLike;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.SearchKeyVal;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


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

	@Autowired
	GoodsOldMapper goodsMapper;

	@Autowired
	CompositionOldMapper compositionMapper;

	@Autowired
	DoyenOldMapper doyenMapper;
	
	@Autowired
	IndexOldMapper indexMapper;
	
	@Resource
	UserService userService;
	
	@Resource
	CommentService commentService;

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
    /**
     * 对比产品
     * @param mids
     * @return
     */
	public ReturnData compareGoods(String mids, UserInfo userInfo) {
		try {
			if(StringUtils.isBlank(mids)) return ReturnData.ERROR;
			String midss[]=mids.split(",");
			List<GoodsExplain> ges=new ArrayList<GoodsExplain>();

			for(int i=0;i<midss.length;i++) {
				//获取产品详细
				ReturnData<GoodsExplain> ge=goodsService.getGoodsExplain(midss[i], userInfo);
				GoodsExplain g=ge.TResult();
				ges.add(g);
			}
			
			//获取对比实体信息
			Long cid1=ges.get(0).getGoods().getId();
			Long cid2=ges.get(1).getGoods().getId();
			//确定 索引
			if(cid1>cid2) {
				Long t=cid1;
				cid1=cid2;
				cid2=t;
				
				List<GoodsExplain> sortGes=new ArrayList<GoodsExplain>();
				sortGes.add(ges.get(1));
				sortGes.add(ges.get(0));
				ges=sortGes;
			}

			//用户与对比的关系
			Map results=this.entityActionState(userInfo, "goods", cid1, cid2,mids);
			results.put("entityInfo", ges);
			
			//对比的功效部分
			ReturnData rd=goodsService.getCompare2(ges, userInfo);
			List<CompareGoods> cgList=(List<CompareGoods>)rd.getResult();
			results.put("effectCompare", cgList);
			
			//用户评分部分的评论内容
			List comemntList=this.getCommentInfo(userInfo,ges);
			results.put("commentCompare", comemntList);
			
			return new ReturnData(results);
			//return new ReturnData(CompareGoods.compareAnalysis(ges,cgefs,userInfo));
		} catch(Exception e) {
			Map map = new HashMap();
			map.put("method", "GoodsService.getCompare");
			map.put("mids", mids);
			map.put("userId", userInfo.getId());
			new LogException(e, map);
		}
		return ReturnData.ERROR;
	}
	
	public List getCommentInfo(UserInfo userInfo, List<GoodsExplain> ges){
		List commentList=new ArrayList();
		Long userId=0L;
		if(null!=userInfo && userInfo.getId()>0){
			userId=userInfo.getId();
		}
		for(int i=0;i<ges.size();i++){
			Map map=new HashMap();
			//修行说
			Doyen doyen=ges.get(i).getGoods().getDoyen();
			
			//产品评论的平均星级
			Query query=new Query(Criteria.where("id").is(ges.get(i).getGoods().getId().longValue()));
			query.fields().include("grade").include("id");
			EntityBase eb=mongoTemplate.findOne(query, EntityBase.class,"entity_goods");

			if(null!=eb && null!=eb.getGrade()&&eb.getGrade()>0){
				map.put("avgScore", eb.getGrade());
			}else{
				map.put("avgScore", 0);
			}
			
			if(null!=doyen){
				//产品含有修行说
				map.put("content", doyen.getDoyenComment());
				map.put("goodsId", doyen.getGoodsId());
				commentList.add(map);
			}else{
				//查找最热评论的第一条,缓存中获取
				Map allComment =commentService.findSourceComments("goods", ges.get(i).getGoods().getId(), userId, 0, 0,0,null);
				if(null!=allComment.get("hotList")){
					List<Comment> hotAllList = (List<Comment>)allComment.get("hotList");
					if(null!=hotAllList && hotAllList.size()>0){
						map.put("content", hotAllList.get(0).getContent());
						map.put("goodsId", hotAllList.get(0).getEntityId());
					}else{
						map.put("content", "");
						map.put("goodsId", 0);
					}
					commentList.add(map);
				}
			}
		}
		return commentList;
	}
	
	/**
	 * 用户和对比的关系
	 * @param userInfo
	 * @param tname 实体
	 * @param cid1 小id
	 * @param cid2 大id
	 * @return
	 */
	public Map entityActionState(UserInfo userInfo, String tname, Long cid1, Long cid2, String mids) {
		Map resutls=new HashMap();
		String table="entity_compare_"+tname;
		String sid=cid1+"_"+cid2;
		//id 相加的方式产生id
		long curTime=new Date().getTime()/1000;
		EntityCompare entityCompare=mongoTemplate.findOne(new Query(Criteria.where("sid").is(sid)),  EntityCompare.class, table);
		if(entityCompare==null) {
			// 产生id 注意 id并发的问题
			Long id=this.getId(table);
			entityCompare=mongoTemplate.findAndModify(new Query(Criteria.where("cid1").is(cid1).and("cid2").is(cid2).and("sid").is(sid)), new Update().set("mids", mids).set("id", id).set("hidden", 0).set("deleted", 0).set("createStamp", curTime).set("updateStamp", curTime).inc("hitNum", 1).inc("visitNum", 1).inc("hotNum", 1).set("cid1LikeNum", 0).set("cid2LikeNum", 0), new FindAndModifyOptions().returnNew(true).upsert(true), EntityCompare.class, table);
			//清除对比广场的缓存
			cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_COMPARE_LIST_PREFIX,tname));
			//更改缓存的key,使对比列表的缓存失效,重新查询一次。没有新的对比,读的就是列表的缓存
			mongoTemplate.findAndModify(new Query(),new Update().set("id", id),new FindAndModifyOptions().returnNew(true).upsert(true),HashMap.class,"temp_compare_goods");
			
		} else {
			//++ 统计
			entityCompare=mongoTemplate.findAndModify(new Query(Criteria.where("sid").is(sid)), new Update().inc("hitNum", 1).inc("visitNum", 1).inc("hotNum", 1), new FindAndModifyOptions().returnNew(true).upsert(true), EntityCompare.class, table);
		}
        resutls.put("entity", entityCompare);
        
        //记录全局访问量
        this.putGlobalConfigInc("compareGoodsVistNum", 1);

        if(userInfo!=null){
    		//实体关系
            //是否喜欢
    		 String likeTable="entity_like_compare_"+tname;
    		 LikeCompare  likeCompareGoods=mongoTemplate.findOne(new Query(Criteria.where("sid").is(sid).and("userId").is(userInfo.getId())), LikeCompare.class, likeTable);
    		 if(likeCompareGoods==null) {
    				likeCompareGoods=new LikeCompare();
    				likeCompareGoods.setSid(sid);
    				likeCompareGoods.setCid1(cid1);
    				likeCompareGoods.setCid2(cid2);
    				likeCompareGoods.setClikeId(0L);
    		 }
			 resutls.put("action", likeCompareGoods);

        }
         return resutls;
	}
	/**
	 * 产品对比
	 * @param userInfo
	 * @param tname
	 * @param sId 对比id
	 * @param id  支持的id 为0时不支持
	 * @return
	 */
	public ReturnData likeCompare(UserInfo userInfo, String tname, String sId, Long id) {
		// TODO Auto-generated method stub
		
		
		String liketable="entity_like_compare_"+tname;
		String comptable="entity_compare_"+tname;
		//支持的频率控制
		ReturnData rd=validateService.comparegGoodsLikeSendTime(userInfo.getId(),sId, liketable);
		if(rd.getRet()!=0){
			return rd;
		}
		
		LikeCompare likeCompareGoods= mongoTemplate.findOne(new Query(Criteria.where("sid").is(sId).and("userId").is(userInfo.getId())),LikeCompare.class,  liketable);
		this.putGlobalConfigInc("compareGoodsVistNum", 1);
		String ids[]=sId.split("_");
		Long cId1= Long.parseLong(ids[0]);
		Long cId2= Long.parseLong(ids[1]);
		if(null==id) id=0L;
		
		Query query=new Query(Criteria.where("sid").is(sId));
		query.fields().include("sid").include("cid1LikeNum").include("cid2LikeNum");
		EntityCompare ece=mongoTemplate.findOne(query,EntityCompare.class, comptable );
		if(null==ece){
			return new ReturnData(-1,"实体不存在");
		}
		//之前参与的支持的id,没有参与则为0
		Long beforeClikeId=0L;
		long updateStamp=new Date().getTime()/1000;
		if(likeCompareGoods==null) {
			likeCompareGoods=new LikeCompare();
			likeCompareGoods.setId(this.getId(liketable));
			likeCompareGoods.setSid(sId);
			likeCompareGoods.setCid1(cId1);
			likeCompareGoods.setCid2(cId2);
			likeCompareGoods.setClikeId(id);
			likeCompareGoods.setUserId(userInfo.getId());
			likeCompareGoods.setUpdateStamp(updateStamp);
			mongoTemplate.insert(likeCompareGoods,liketable);
		}  else{
			beforeClikeId=likeCompareGoods.getClikeId();
		}
		
		//支持数处理
		if(null!=ece){
			Update comptableUpdate= new Update().inc("visitNum", 1);
			if(cId1==id.longValue()) {
				if(beforeClikeId==0){
					//第一次支持
					comptableUpdate.inc("cid1LikeNum", 1);
					ece.setCid1LikeNum(ece.getCid1LikeNum()+1);
				}else if(beforeClikeId>0){
					//非第一次支持
					if(beforeClikeId!=id.longValue()){
						//上次的支持与这次的支持不一致
						comptableUpdate.inc("cid1LikeNum", 1);
						ece.setCid1LikeNum(ece.getCid1LikeNum()+1);
						if(beforeClikeId>0 && ece.getCid2LikeNum()!=null&&ece.getCid2LikeNum()>0&&likeCompareGoods.getClikeId()!=null&&likeCompareGoods.getClikeId()>0){
							comptableUpdate.inc("cid2LikeNum", -1); //cid1LikeNum+1
							ece.setCid2LikeNum(ece.getCid2LikeNum()-1);
						}
					}else{
						//取消
						if(ece.getCid1LikeNum()!=null&&ece.getCid1LikeNum()>0){
							comptableUpdate.inc("cid1LikeNum", -1); //cid1LikeNum+1
							ece.setCid1LikeNum(ece.getCid1LikeNum()-1);
							id=0L;
						}
					}
				}
			} else if(cId2==id.longValue()) {
				if(beforeClikeId==0){
					//第一次支持
					comptableUpdate.inc("cid2LikeNum", 1);
					ece.setCid2LikeNum(ece.getCid2LikeNum()+1);
				}else if(beforeClikeId>0){
					//上次的支持与这次的支持不一致
					if(beforeClikeId!=id.longValue()){
						comptableUpdate.inc("cid2LikeNum", 1);
						ece.setCid2LikeNum(ece.getCid2LikeNum()+1);
						if(beforeClikeId>0 && ece.getCid1LikeNum()!=null&&ece.getCid1LikeNum()>0&&likeCompareGoods.getClikeId()!=null&&likeCompareGoods.getClikeId()>0){
							comptableUpdate.inc("cid1LikeNum", -1); //cid1LikeNum+1
							ece.setCid1LikeNum(ece.getCid1LikeNum()-1);
						}
					} else{
						//取消
						if(ece.getCid2LikeNum()!=null&&ece.getCid2LikeNum()>0){
							comptableUpdate.inc("cid2LikeNum", -1); //cid1LikeNum+1
							ece.setCid2LikeNum(ece.getCid2LikeNum()-1);
							id=0L;
						}
					}
				}
			} else if(id==0) {
				//取消上一次的点击
				if(likeCompareGoods.getClikeId()!=null&&cId1.longValue()==likeCompareGoods.getClikeId()) {
					if(beforeClikeId>0 && ece.getCid1LikeNum()!=null&&ece.getCid1LikeNum()>0){
						comptableUpdate.inc("cid1LikeNum", -1); //cid1LikeNum+1
						ece.setCid1LikeNum(ece.getCid1LikeNum()-1);
					}
				} else if(likeCompareGoods.getClikeId()!=null&&cId2.longValue()==likeCompareGoods.getClikeId()) {
					if(beforeClikeId>0 && ece.getCid2LikeNum()!=null&&ece.getCid2LikeNum()>0){
						comptableUpdate.inc("cid2LikeNum", -1); //cid1LikeNum+1
						ece.setCid2LikeNum(ece.getCid2LikeNum()-1);
					}
				}
			}
			mongoTemplate.updateFirst(new Query(Criteria.where("sid").is(sId)),comptableUpdate, comptable);
			
			//更新支持的实体
			if(likeCompareGoods.getId()>0){
				//跟新支持时间todo--clikeId
				mongoTemplate.updateFirst(new Query(Criteria.where("sid").is(sId).and("userId").is(userInfo.getId())), new Update().set("clikeId", id).set("updateStamp", updateStamp),LikeCompare.class,  liketable);
			}
		}
		
		return new ReturnData(ece);
	}
	
	/**
	 * 
	 * @param tname
	 * @param type=0 最热 1最新
	 * @return
	 */
	public ReturnData compareList(UserInfo userInfo, final String tname, final Integer type, final Integer pager, final Integer pageSize) {
		Map temp=mongoTemplate.findOne(new Query(), HashMap.class,"temp_compare_goods");
		List<EntityCompare> entityCompares=new CacheableTemplate<List<EntityCompare>>(cacheProvider) {
			@Override
			protected List<EntityCompare> getFromRepository() {
				try {
					String table="entity_compare_"+tname;
					//排序 
					Integer skip=(pager-1)*pageSize;
					Query query =new Query(Criteria.where("hidden").is(0)).skip(skip).limit(pageSize);
					
					if(type==0) {
						query.with(new Sort(Direction.DESC, "sort"));
						query.with(new Sort(Direction.DESC, "visitNum"));
					}else if(type==1) {
						query.with(new Sort(Direction.DESC, "createStamp"));
					}

					List<EntityCompare> entityCompares=mongoTemplate.find(query,  EntityCompare.class, table);
					
					List allList=new ArrayList();
					//文案和产品的信息
					entityCompares = addGoodsInfo(entityCompares);

					return entityCompares;
				} catch (Exception e) {
					Map map=new HashMap();
		    		map.put("method", "CompareService.compareList");
		    		map.put("tname", tname);
		    		map.put("type", type);
		    		new LogException(e,map);
					return null;
				}
			}

			@Override
			protected boolean canPutToCache(List<EntityCompare> returnValue) {
				return (returnValue != null  && returnValue.size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_COMPARE_LIST_PREFIX,tname,type+"",pager+"",pageSize+"",temp.get("id")+"")
						),
				true);

		if(null!=entityCompares && entityCompares.size()>0){
			String likeTable="entity_like_compare_"+tname;
	
			List<String> sids=new ArrayList<String>();
			Map<String,EntityCompare> ecps=new HashMap<String,EntityCompare>();
			for(int i=0;i<entityCompares.size();i++) {
				String sid=entityCompares.get(i).getSid();
				sids.add(sid);
				ecps.put(sid, entityCompares.get(i));
				entityCompares.get(i).setClikeId(0L);
			}
	
			//同步用户和对比的关系
			if(null!=userInfo&&userInfo.getId()>0){
				if(sids.size()>0) {
					List<LikeCompare> lcs= mongoTemplate.find(new Query(Criteria.where("sid").in(sids).and("userId").is(userInfo.getId())),LikeCompare.class,  likeTable);
					if(lcs!=null) {
						for(int i=0;i<lcs.size();i++) {
							ecps.get(lcs.get(i).getSid()).setClikeId(lcs.get(i).getClikeId());
						}
					}
				}
			}
	
			//查询支持数不做缓存
			String table="entity_compare_"+tname;
			Query query =new Query(Criteria.where("sid").in(sids));
			String[] fields={"cid1LikeNum","cid2LikeNum","hitNum","sid"};
			this.setQueryFeilds(query, fields);
			List<EntityCompare> ecList=mongoTemplate.find(query,  EntityCompare.class, table);
			for(EntityCompare nec:ecList){
				for(EntityCompare ec:entityCompares){
					if(nec.getSid().equals(ec.getSid())){
						ec.setCid1LikeNum(nec.getCid1LikeNum());
						ec.setCid2LikeNum(nec.getCid2LikeNum());
						ec.setHitNum(nec.getHitNum());
					}
				}
			}
		}

		Map map=new HashMap();

		//列表数据
		map.put("list", entityCompares);
		//全局的数字
		Long compareGoodsVistNum=getGlobalConfigAll().getCompareGoodsVistNum();
		map.put("compareGoodsVistNum", compareGoodsVistNum);


		return new ReturnData(map);
	}


	/**
	 * 发送话题
	 * @param userInfo
	 * @param tname
	 * @param sid
	 * @param content
	 * @return
	 */
	public ReturnData sendDiscuss(UserInfo userInfo, String tname, String sid, Long rid, String content) {
		// TODO Auto-generated method stub
		String table="entity_discuss_compare_"+tname;
		//显示的3条评论
		 List<Map> childs=null;
		 //引用 id
		 List<Long> rids=null;
		 Long pUserId=0L;

		 Map<Long,UserBaseInfo> userMaps=new SearchKeyVal<Long,UserBaseInfo>();
		 //引用处理
		 Map m=new HashMap();
		if(rid!=null&&rid>0) {
			Discuss discuss=mongoTemplate.findOne(new Query(Criteria.where("id").is(rid)),  Discuss.class, table);
			pUserId=discuss.getUserId();
			childs=discuss.getChilds();
			rids=discuss.getRids();
			if(childs==null) {
				childs=new ArrayList<Map>();
			}
			if(rids==null) {
				rids=new ArrayList<Long>();
			}
			
			m.put("id", discuss.getId());
			m.put("content", discuss.getContent());
			m.put("createStamp", discuss.getCreateStamp());
			m.put("userId", discuss.getUserId());
			if(childs.size()==3) {
				childs.set(2, m);
			}else {
				childs.add(m);
			}
			for(int i=0;i<childs.size();i++) {
				Map sm=childs.get(i);
				Long kid= Long.parseLong(sm.get("userId")+"");
				UserBaseInfo u=userMaps.put(userInfo.getId(), new UserBaseInfo());
				sm.put("userInfo", u);
			}
			rids.add(discuss.getId());

		}

		Discuss discuss=new Discuss();
		discuss.setId(this.getId(table));
		discuss.setContent(content);
		discuss.setPid(rid);
		discuss.setUserId(userInfo.getId());
		discuss.setpUserId(pUserId);
		UserBaseInfo u=userMaps.put(userInfo.getId(), new UserBaseInfo());
		discuss.setUserInfo(u);
		discuss.setChilds(childs);
		discuss.setRids(rids);
		discuss.setSid(sid);
		discuss.setSkinResults(userInfo.getSkinResults());
		discuss.setSkin(userInfo.getResult());
		mongoTemplate.save(discuss,table);
		userService.synUserInfo(userMaps);

		//评论数++
		mongoTemplate.updateFirst(new Query(Criteria.where("sid").is(sid)), new Update().inc("commentNum", 1), EntityCompare.class,"entity_compare_"+tname);

		//清理缓存
		cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_DISCUSS_COMPARE_LIST_PREFIX,tname));
		
		//给被回复者发送消息
		if(null!=discuss.getPid() && discuss.getPid()>0){
			EntityCompare enty = mongoTemplate.findOne(new Query(Criteria.where("sid").is(sid)), EntityCompare.class, "entity_compare_"+tname);
			//获取mids
			if(null!=enty){
				String msgCode="msg-reply-discuss_compare_"+tname;
				String msgField="comment";
				Map map= CommonUtils.ObjectToMap(discuss);
				Comment pcmt=new Comment();
				pcmt.setContent(String.valueOf(m.get("content")));
				tname="compare_"+tname;
				messageService.sendEntitySynMessage(userInfo.getId(), discuss.getpUserId(),msgCode,msgField, MsgExtComment.createCommentMsg(tname, map, pcmt, enty, userInfo));
			
			}
			
		}
		
		return new ReturnData<>(discuss);
	}

	/**
	 * 话题列表
	 * @param userId
	 * @param tname 实体信息
	 * @param sId 实体id
	 * @param rids 引用id
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	public ReturnListData discussList(Long userId, String tname, String sId, String rids, int pager,
                                      int pageSize) {
		if(StringUtils.isBlank(rids)) {
			return this.discussMainList( tname, sId, pager, pageSize,userId);
		}
		return new ReturnListData(this.discussListByRids(tname, rids),0);
	}

	/**
	 * 查询主评论
	 * @param userId
	 * @param tname 实体信息
	 * @param sId 实体id
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	public ReturnListData discussMainList(final String tname, final String sId, final int pager,
                                          final int pageSize, final Long userId) {
		ReturnListData entityCompares=new CacheableTemplate<ReturnListData>(cacheProvider) {
			@Override
			protected ReturnListData getFromRepository() {
				try {
					 Map<Long,UserBaseInfo> userMaps=new SearchKeyVal<>();
					 List<Map> childs=new ArrayList<Map>();
					//查询 主评论
					String table="entity_discuss_compare_"+tname;
					Integer skip=(pager-1)*pageSize;
					Query query =new Query(Criteria.where("hidden").is(0).and("deleted").is(0).and("sid").is(sId)).skip(skip).limit(pageSize);
					query.with(new Sort(Direction.DESC, "sort")).with(new Sort(Direction.DESC, "id"));
					List<Discuss> discusss=mongoTemplate.find(query,  Discuss.class, table);
					for(int i=0;i<discusss.size();i++) {
						if(discusss.get(i).getChilds()!=null)
							childs.addAll(discusss.get(i).getChilds());
						UserBaseInfo u=userMaps.put(discusss.get(i).getUserId(), new UserBaseInfo());
						discusss.get(i).setUserInfo(u);

					}
					for(int i=0;i<childs.size();i++) {
						Map sm=childs.get(i);
						UserBaseInfo u=userMaps.put(Long.parseLong(sm.get("userId")+""), new UserBaseInfo());
						sm.put("userInfo", u);
						;
					}

					userService.synUserInfo(userMaps);
			 		long total=mongoTemplate.count(query, table);
			 		
			 		//用户是否点赞
			 		if(null!=userId && userId>0){
			 			for(Discuss dis:discusss){
							 List<CommentLike> likeList=dis.getCommentLikes();
							 boolean flag=false;
							 if(null!=likeList && likeList.size()>0){
								 for(int i=0;!flag && i<likeList.size();i++){
									 if(likeList.get(i).getUserId()==userId.longValue()){
										 //点赞了
										 dis.setIsLike(1);
										 flag=true;
									 }
								 }
							 }
						 }
			 		}
					 
					return new ReturnListData(discusss,total);
				} catch (Exception e) {
					Map map=new HashMap();
		    		map.put("method", "CompareService.discussMainList");
		    		map.put("tname", tname);
		    		map.put("sId", sId);
		    		map.put("pager", pager);
		    		map.put("pageSize", pageSize);
		    		new LogException(e,map);
					return null;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnListData returnValue) {
				return (returnValue != null  && returnValue.Tesult().size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_DISCUSS_COMPARE_LIST_PREFIX,tname,sId,pager+"",pageSize+"")
						),
				true);
		return entityCompares;

	}

	/**
	 * 更加引用id查询话题
	 * @param tname
	 * @param rids 引用id
	 * @return
	 */
	public List<Discuss> discussListByRids(String tname, String rids) {
		 Map<Long,UserBaseInfo> userMaps=new SearchKeyVal<>();
		 String ridss[]=rids.split(",");
		 //有序的
	      List<Long> aryList=new ArrayList<Long>();
		 for(int i=0;i<ridss.length;i++) {
			 Long id= Long.parseLong(ridss[i]);
			 aryList.add(id);
		 }

		 List<Map> childs=new ArrayList<Map>();
			//查询 主评论
			String table="entity_discuss_compare_"+tname;
			Query query =new Query(Criteria.where("hidden").is(0).and("deleted").is(0).and("id").in(aryList));
			query.fields().include("id").include("content").include("userId").include("createStamp");
			List<Discuss> discusss=mongoTemplate.find(query,  Discuss.class, table);
			List<Discuss> sortDiscuss=new ArrayList<Discuss>();
			if(null!=discusss && discusss.size()>0){
				for(int i=0;i<discusss.size();i++) {
					UserBaseInfo u=userMaps.put(discusss.get(i).getUserId(), new UserBaseInfo());
					discusss.get(i).setUserInfo(u);
				}
				
				//排序
				 for(int i=0;i<ridss.length;i++) {
					 Long id= Long.parseLong(ridss[i]);
					 boolean flag=true;
						for(int n=0;flag&&n<discusss.size();n++) {
							if(id==discusss.get(n).getId().longValue()){
								sortDiscuss.add(discusss.get(n));
								flag=false;
							}
						}
				 }
				 userService.synUserInfo(userMaps);
			}
			
		return sortDiscuss;
	}

	public ReturnData sendDiscussLike(UserInfo userInfo, String tname, Long commentId) {

		long userId = 0L;
		String skin = "";
		String skinResults = "";
		try {
			userId = userInfo.getId();
			if (!StringUtils.isBlank(userInfo.getResult())) {
				skin = userInfo.getResult();
			}
			if (!StringUtils.isBlank(userInfo.getSkinResults())) {
				skinResults = userInfo.getSkinResults();
			}
			String actionType="entity_discuss_compare_"+tname;
			CommentLike cl = new CommentLike();
			cl.setUserId(userId);
			cl.setSkin(skin);
			cl.setSkinResults(skinResults);
			Query query = Query.query(new Criteria().andOperator(Criteria.where("id").is(commentId),
					Criteria.where("commentLikes").elemMatch(Criteria.where("userId").is(userId))));
			//当前用户是否点赞过
			Discuss eb = mongoTemplate.findOne(query, Discuss.class, actionType);
			Discuss comment = mongoTemplate.findOne(new Query(Criteria.where("id").is(commentId)), Discuss.class,
					actionType);


			if (eb == null) {
				//用户点赞
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)),
						new Update().addToSet("commentLikes", cl), actionType);
				// 点赞
				this.objectIncById(commentId, actionType, "likeNum", 1);
				// 评论计数
				this.objectIncById(comment.getUserId(), "user_info", "commentLikeNum", 1);
 				Query q=new Query(Criteria.where("msgExt.cId").is(comment.getId()).and("msgExt.rUserId").is(userInfo.getId()));
	            HashMap mes = mongoTemplate.findOne(q, HashMap.class, "user_message");
	            if(mes==null) {
	            	//获取mids
	            	/*query=new Query(Criteria.where("sid").is(comment.getSid()));
	            	String[] fields={"id","sid","mids"};
	            	this.setQueryFeilds(query, fields);
	            	EntityCompare ec=mongoTemplate.findOne(query,EntityCompare.class,"entity_compare_"+tname);
	            	*/
	            	EntityCompare enty = mongoTemplate.findOne(new Query(Criteria.where("sid").is(comment.getSid())), EntityCompare.class, "entity_compare_"+tname);
					String msgCode="msg-discuss_like_compare_"+tname;
					String msgField="commentLike";
					messageService.sendEntitySynMessage(userInfo.getId(), comment.getUserId(),msgCode,msgField, MsgExtCommentLike.createDiscussLikeMsg("compare_"+tname, comment, enty, userInfo));
	            }

				return new ReturnData(1, "点赞成功");
			} else {
				//取消点赞
				Update update = new Update();
				update.pull("commentLikes", new BasicDBObject("userId", userId));
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(commentId)), update, actionType);

				this.objectIncById(commentId, actionType, "likeNum", -1);

				this.objectIncById(comment.getUserId(), "user_info", "commentLikeNum", -1);
				return new ReturnData(2, "取消点赞成功");
			}
		} catch (Exception e) {
			Map map=new HashMap();
    		map.put("method", "CompareService.sendDiscussLike");
    		map.put("tname", tname);
    		map.put("commentId", commentId);
    		map.put("userId", userId);
    		new LogException(e,map);
		}
		return null;
	}

	/**
	 * 后台查询对比的评论
	 * @param type
	 * @param id1
	 * @param id2
	 * @return
	 */
	public ReturnListData commentCompareList(Integer type, Long id1, Long id2) {
		try{
			//获取sid,根据type查询评论
			String sid="";
			if(null!=id1&&id1>0&&null!=id2&&id2>0){
				if(id1>id2) {
					Long t=id1;
					id1=id2;
					id2=t;
				}
				sid=id1+"_"+id2;
			}
			String table="entity_discuss_compare_goods";
			Criteria cr=new Criteria();
			cr.where("hidden").is(0);
			Query query=new Query(cr);
			if(StringUtils.isNotBlank(sid)){
				//根据对比实体查评论
				cr.and("sid").is(sid);
			}
			if(type==1){
				//查一级评论,无引用id
				cr.and("rids").exists(false);
			}
			if(type==2){
				//查二级评论,有引用id
				cr.and("rids").exists(true);
			}
			List<Discuss> copList=mongoTemplate.find(query.skip(0).limit(0), Discuss.class,table);

			long total=mongoTemplate.count(query,Discuss.class,table);

			return new ReturnListData(copList,total);
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "CompareService.commentCompareList");
    		map.put("type", type);
    		map.put("id1", id1);
    		map.put("id2", id2);
    		new LogException(e,map);
		}
		return null;
	}

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
				Goods goods=goodsMapper.getById(Long.parseLong(ids[i]));
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