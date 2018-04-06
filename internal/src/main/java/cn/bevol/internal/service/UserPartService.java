package cn.bevol.internal.service;


import cn.bevol.model.entity.*;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.mapper.GoodsOldMapper;
import cn.bevol.internal.dao.mapper.HotListOldMapper;
import cn.bevol.internal.dao.mapper.UserInfoOldMapper;
import cn.bevol.internal.dao.mapper.VerificationCodeOldMapper;
import cn.bevol.internal.entity.*;
import cn.bevol.internal.entity.dto.HotList;
import cn.bevol.internal.entity.entityAction.ApplyGoodsUser;
import cn.bevol.internal.entity.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.Log.LogMethod;
import cn.bevol.util.cache.CACHE_NAME;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPartService extends BaseService {

	@Autowired
    MongoTemplate mongoTemplate;

	@Autowired
	UserInfoOldMapper userInfoOldMapper;
	@Autowired
	VerificationCodeOldMapper verificationCodeOldMapper;

	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	EntityService entityService;

	@Autowired
	CommentService commentService;

	@Autowired
	MessageService messageService;

	@Autowired
	UserService userService;

	@Autowired
	AliyunService aliyunService;

	@Autowired
	CacheService cacheService;

	@Autowired
	private HotListOldMapper hotListOldMapper;

	private static Logger logger = LoggerFactory.getLogger(UserPartService.class);

	@Autowired
	ValidateService validateService;
	@Autowired
	GoodsOldMapper goodsOldMapper;



	/**
	 * 某个类型的实体的心得个数
	 * @param tname	实体类型(lists)
	 * @param type	类型1普通心得 2试用心得 3自由发布的心得(不支持)
	 * @param pEntityId	实体的id
	 * @return
	 */
	public long findUserPartCount2(String tname, int type, Long pEntityId) {
		String actype="entity_user_part_"+tname;
		Query query = Query.query(new Criteria().andOperator(Criteria.where("pEntityId").is(pEntityId)).and("hidden").is(0).and("type").is(type));
		long count=this.mongoTemplate.count(query,EntityUserPart.class, actype);
		return count;

	}


	/**
	 * 查找试用(心得)列表
	 * @param tname
	 * @param pEntityId
	 * @param p
	 * @param size
	 * @return
	 */
	public List<Map> findUserPartListByApply(String tname, int type, long pEntityId, int p, int size) {
		String actype="entity_user_part_"+tname;
		if (p > 0) {
			p = (p - 1);
		} else {
			p = 0;
		}
		Integer startId = Integer.valueOf((p * size) + "");

		Query query = Query.query(new Criteria().andOperator(Criteria.where("pEntityId").is(pEntityId)).and("hidden").is(0).and("type").is(type));
		List<EntityUserPart> eups=this.mongoTemplate.find(query.skip(startId).limit(size),EntityUserPart.class, actype);
		//参与时间 用户的修行值
		List<Map> listMap=new ArrayList();
		for(EntityUserPart eup:eups){
			long entityId=eup.getpEntityId();
			long userId=eup.getUserId();
			query = new Query(Criteria.where("entityId").is(entityId).and("userId").is(userId));
			//该活动下的申请
			ApplyGoodsUser agu=this.mongoTemplate.findOne(query,ApplyGoodsUser.class,"apply_goods_user");

			query = new Query(Criteria.where("id").is(userId));
			UserInfo userInfo=this.mongoTemplate.findOne(query,UserInfo.class,"user_info");
			try {
				JSONObject map = JSONObject.fromObject(eup);
				if(null!=agu){
					//参与时间
					map.put("applyTime", agu.getCreateStamp());
				}
				if(null!=userInfo && null!=userInfo.getScore()){
					//用户信息
					map.put("userDoyenScore", userInfo.getScore());
					if(StringUtils.isNotBlank(userInfo.getNickname())){
						map.put("nickname", userInfo.getNickname());
					}
				}

				listMap.add(map);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		return listMap;
	}


	public List<EntityUserPart> list(int type, int pager, int rows, String... fields) {
		String actionType="entity_user_part_lists";

		Criteria crt= Criteria.where("hidden").is(0);
		if(type>0)
			crt.and("type").is(type);
		Query query = Query.query(crt);
		if(fields!=null) {
			for(int i=0;i<fields.length;i++) {
				query.fields().include(fields[i]);
			}
		}
		if(pager<=1) {
			pager=0;
		} else {
			pager=(pager-1);
		}
		Integer startId = Integer.valueOf((pager * rows) + "");
		query.skip(startId).limit(rows).with(new Sort(Direction.DESC, "sort")).with(new Sort(Direction.DESC, "type2Sort")).with(new Sort(Direction.DESC, "id"));
		List<EntityUserPart> uais = mongoTemplate.find(query, EntityUserPart.class, actionType);

		//获取心得对应的话题的名称
		if(null!=uais && uais.size()>0){
			for(EntityUserPart eup:uais){
				if(null!=eup.getpEntityId() && eup.getpEntityId()>0){
					HotList hotList=hotListOldMapper.findDetail(eup.getpEntityId().intValue());
					if(null!=hotList && null!=hotList.getTitle()){
						eup.setpEntityName(hotList.getTitle());
					}
				}
			}
		}


		return uais;
	}

	/**
	 * 添加试用报告
	 * @param userId
	 * @param pEntityId
	 * @param type
	 * @param title
	 * @param content
	 * @param tname
	 * @param srcTname
	 * @return
	 */
	public ReturnData addUserApply(Long userId, Integer type, String title,
								   String tname, String exFeilds, String details, Long createStamp, Long updateStamp, String image) {
		try{
			String actype="entity_user_part_"+tname;
			ReturnData rd=userService.getUserById(userId);
			if(rd.getRet()!=0){
				return rd;
			}
			UserInfo userInfo=(UserInfo)rd.getResult();
			if(null==userInfo){
				return ReturnData.ERROR;
			}
			//手机绑定
			ReturnData prd=commentService.switchOfPhoneCheck(userInfo);
			if(prd.getRet()!=0){
				return prd;
			}
			//实名认证拦截
			prd=this.oldVerifyState(userInfo);
			if(prd.getRet()!=0){
				return prd;
			}

			EntityUserPart userPart=new EntityUserPart();

			userPart.setTitle(title);
			userPart.setId(this.getId(actype));

			userPart.setUserRole(userInfo.getRole());
			userPart.setType(type);
			userPart.setImage(image);
			if(null!=createStamp && createStamp>0){
				userPart.setCreateStamp(createStamp);
			}
			if(null!=updateStamp && updateStamp>0){
				userPart.setUpdateStamp(updateStamp);
			}

			//心得内容处理
			JSONArray  jasonArray  =null;
			if(StringUtils.isNotBlank(details)){
				jasonArray=JSONArray.fromObject(details);
			}
			userPart.setUserPartDetails(toUserPartDetails(jasonArray));
			userPart.setUniqueId(userPart.getId());
			userPart.setTname("user_part_lists");
			if(StringUtils.isNotBlank(exFeilds)) {
				Map map=new HashMap();
				map.putAll(JSONObject.fromObject(exFeilds));
				userPart.setExFeilds(map);
			}

			//设置用户基本信息
			userPart.setUserBaseInfo(userInfo.getBaseInfo());
			userPart.setUserId(userInfo.getId());

			this.mongoTemplate.save(userPart,actype);


			//清除缓存
			cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_USERPART_LIST_PREFIX,tname,userPart.getpEntityId()+""));
			cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_USERPART_APPGOODS_LIST_PREFIX));


			return new ReturnData(userPart);
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "ApplyGoodsService.addUserApply");
			map.put("userId", userId);
			map.put("type", type);
			map.put("title", title);
			map.put("details", details);
			map.put("createStamp", createStamp);
			map.put("updateStamp", updateStamp);
			new LogException(e,map);
			return ReturnData.ERROR;
		}
	}

	private List<UserPartDetail> toUserPartDetails(JSONArray details) {
		List<UserPartDetail> upds=null;
		//if(StringUtils.isNotBlank(details)) {
		if(null!=details && details.size()>0) {
			upds=new ArrayList<UserPartDetail>();
			boolean flag=false;
			//details=details.replaceAll("\\\\","");
			//json转移字符的处理
			UserPartDetail upd=null;
			/*org.json.JSONObject  j=new org.json.JSONObject ("  {'type':'ONLINE_SHIPS','details':'"+details+"'}");
			//先通过字符串的方式得到,转义字符自然会被转化掉
			String jsonstrtemp = j.getString("details");	*/
			JSONArray  jasonObject  =JSONArray.fromObject(details);
			for(int i=0;i<jasonObject.size();i++) {
				Map<String,String> mds=jasonObject.getJSONObject(i);
				String type=mds.get("type");
				if(StringUtils.isNotBlank(type)) {
					if(type.equals("1")) {
						UserPartDetailGoods u=new UserPartDetailGoods();
						if(StringUtils.isNotBlank(mds.get("id"))) {
							long tid= Long.parseLong(mds.get("id"));
							u.setId(tid);
							String tn=mds.get("tname");
							//加载entity
							//Goods e=goodsOldMapper.getById(tid);
							EntityGoods eb=mongoTemplate.findOne(new Query(Criteria.where("id").is(tid)), EntityGoods.class,"entity_goods");
							u.setImage(eb.getImage());
							u.setTitle(eb.getTitle());
							u.setMid(eb.getMid());
							u.setAlias(eb.getAlias());
							u.setCapacity(eb.getCapacity());
							u.setSafety_1_num(eb.getSafety_1_num());
							u.setPrice(eb.getPrice());
							u.setGrade(eb.getGrade());
							u.setCommentNum(eb.getCommentNum());
							u.setTname(tn);
						} else {
							u.setTname(mds.get("tname"));
							u.setTitle(mds.get("title"));
						}
						upd=u;
					} else if(type.equals("2")) {
						if(StringUtils.isNotBlank(mds.get("content"))) {
							flag=true;
							upd=new UserPartDetailText(mds.get("content"));
						}
					} else  if(type.equals("3")) {
						upd=new UserPartDetailImg(CommonUtils.getImag(mds.get("image")));
					}
					if(upd!=null) {
						upd.setType(Integer.parseInt(type));
						upds.add(upd);
					}
				}
			}
		}
		return upds;
	}


	public ReturnData editSort(String ids) {
		try{
			clearSort();
			//有ids就排序
			if(StringUtils.isNotBlank(ids)){
				String[] idss=ids.split(",");
				for(int i=0;i<idss.length;i++){
					Criteria cr2= Criteria.where("id").is(Integer.parseInt(idss[i]));
					Update update2 =new Update();
					update2.set("sort", idss.length-i);
					mongoTemplate.updateFirst(new Query(cr2), update2, EntityUserPart.class,"entity_user_part_lists");
				}
				entityService.updateValue("userPart_sort",ids);
			}
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			logger.error("method:UserPartService.editSort arg:{ids:" + ids+"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnData.ERROR;
		}
	}



	/**
	 * 清除心得所有的sort
	 * @return
	 */
	public ReturnData clearSort() {
		try{
			//清除全部的sort字段
			Update update=new Update();
			update.unset("sort");
			Criteria cr= Criteria.where("sort").gt(0);
			mongoTemplate.updateMulti(new Query(cr), update, EntityUserPart.class,"entity_user_part_lists");
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			logger.error("method:UserPartService.clearSort "
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnData.ERROR;
		}
	}

	/**
	 * 设置一天的排序规则
	 * @return
	 */
	public ReturnData resetDaySort() {
		try{
			//1、清空等于0的
			Update update=new Update();
			update.unset("sort");
			Criteria cr= Criteria.where("sort").is(0);
			mongoTemplate.updateMulti(new Query(cr), update, EntityUserPart.class,"entity_user_part_lists");
			//2、重新设置等于0的
			update=new Update();
			update.set("sort",0);
			long start= DateUtils.addHours(new Date(), -24).getTime()/1000;
			long end=cn.bevol.util.DateUtils.nowInMillis()/1000;
			//当前时间-24h --- 当前时间
			cr= Criteria.where("createStamp").gt(start).lt(end);
			mongoTemplate.updateMulti(new Query(cr), update, EntityUserPart.class,"entity_user_part_lists");
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			logger.error("method:UserPartService.clearSort "
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnData.ERROR;
		}
	}

	/**
	 * 心得列表 TODO
	 * @param type 0是所有心得 1普通心得 2试用心得 3自由发布的心得
	 * @param pager
	 * @param pageSize
	 */
	public ReturnListData userPartLists(int type, int pager, int pageSize) {
		try{
			int skipId=0;
			if(pager>1){
				skipId=(pager-1)*pageSize;
			}
			Criteria cr= new Criteria();
			cr.where("hidden").is(0).and("deleted").is(0);
			if(type>0){
				cr.and("type").is(type);
			}
			List<EntityUserPart> eupList=mongoTemplate.find(new Query(cr).skip(skipId).limit(pageSize), EntityUserPart.class,"entity_user_part_lists");
			long total=mongoTemplate.count(new Query(cr), EntityUserPart.class,"entity_user_part_lists");
			return new ReturnListData(eupList,total);
		}catch (Exception e) {
			logger.error("method:UserPartService.userPartLists "
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnListData.ERROR;
		}
	}



	/**
	 * 设置人工排序
	 * @param id
	 * @param sort
	 * @param sortField
	 * @return
	 */
	public ReturnData userPartListSort(Integer id, Integer sort, String sortField){
		return this.ManualSetSort(EntityUserPart.class, id, sort, sortField);
	}


	/**
	 * 修改试用报告的内容
	 * @param id
	 * @param hidden
	 * @param userId
	 * @param title
	 * @param srcTname
	 * @param details
	 * @param createStamp
	 * @param updateStamp
	 * @return
	 */
	public ReturnData updateUserApply(Long id, Integer hidden, Long userId, String title,
									  String srcTname, String details, Long createStamp, Long updateStamp) {
		try{
			//查找要修改的
			EntityUserPart eup=mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), EntityUserPart.class,"entity_user_part_lists");
			if(null==eup){
				return new ReturnData(-1,"心得不存在");
			}
			Update update=new Update();
			if(null!=hidden && (hidden==1 || hidden==0)){
				update.set("hidden", hidden);
			}
			//用户信息
			if(null!=userId && userId>0){
				update.set("userId", userId);
				ReturnData rd=userService.getUserById(userId);
				if(rd.getRet()!=0){
					return rd;
				}
				UserInfo userInfo=(UserInfo)rd.getResult();
				if(null==userInfo){
					return ReturnData.ERROR;
				}
				update.set("userBaserInfo", userInfo.getBaseInfo());
				update.set("userRole", userInfo.getRole());
			}
			if(StringUtils.isNotBlank(title)){
				update.set("title", title);
			}
			if(StringUtils.isNotBlank(details)){
				//心得内容处理
				JSONArray jasonArray=JSONArray.fromObject(details);
				update.set("userPartDetails", toUserPartDetails(jasonArray));
			}
			if(StringUtils.isNotBlank(srcTname)){
				update.set("srcTname", srcTname);
			}
			if(null!=createStamp && createStamp>0){
				update.set("createStamp", createStamp);
			}
			if(null!=updateStamp && updateStamp>0){
				update.set("updateStamp", updateStamp);
			}
			Criteria cr= Criteria.where("id").is(id);
			mongoTemplate.updateMulti(new Query(cr), update, EntityUserPart.class,"entity_user_part_lists");
			eup=mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), EntityUserPart.class,"entity_user_part_lists");
			return new ReturnData(eup);
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "ApplyGoodsService.updateUserApply");
			map.put("id", id);
			map.put("userId", userId);
			map.put("title", title);
			map.put("hidden", hidden);
			map.put("srcTname", srcTname);
			map.put("details", details);
			map.put("createStamp", createStamp);
			map.put("updateStamp", updateStamp);
			new LogException(e,map);
			return ReturnData.ERROR;
		}
	}

	/**
	 * 心得反垃圾
	 * @return
	 * @throws Exception
	 */
	@LogMethod
	public ReturnData userPartAntiSpam() throws Exception {
		List<EntityUserPart> userPartList;
		String collectionName = "entity_user_part_lists";
		ArrayList<String> blankContent = new ArrayList<String>();
		blankContent.add("");
		blankContent.add(null);
		Query query = new Query(Criteria.where("marker").is(null).and("userPartDetails").ne(null).and("hasContent").exists(false)).limit(100);
		query.fields().include("id").include("userPartDetails");
		do {
			userPartList = mongoTemplate.find(query, EntityUserPart.class, collectionName);
			Collection<List<UserPartDetail>> userPartDetailsCollection = CollectionUtils.collect(userPartList, new BeanToPropertyValueTransformer("userPartDetails"));
			Collection<Long> userPartIdCollection = CollectionUtils.collect(userPartList, new BeanToPropertyValueTransformer("id"));
			List<List<UserPartDetail>> userPartDetailsList = new ArrayList<List<UserPartDetail>>(userPartDetailsCollection);
			List<Long> userPartIdList = new ArrayList<Long>(userPartIdCollection);
			List<String> userPartContentList = new ArrayList<String>();
			List<Long> userPartContentIdList = new ArrayList<Long>();
			for(int i=0; i<userPartIdList.size(); i++){
				int hasContent = 0;
				if(userPartDetailsList.get(i) != null) {
					for (UserPartDetail userPartDetail : userPartDetailsList.get(i)) {
						if (userPartDetail.getType() == 2) {
							JSONObject userPartJson = JSONObject.fromObject(userPartDetail);
							if(userPartJson.has("content")){
								userPartContentList.add(userPartJson.getString("content"));
								userPartContentIdList.add(userPartIdList.get(i));
								hasContent = 1;
							}
						}
					}
					//心得中不含content
					if(hasContent == 0){
						mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(userPartIdList.get(i))),
								new Update().set("hasContent", hasContent),
								EntityUserPart.class,
								collectionName);
					}
				}
			}
			if(userPartContentList.size()>0 && userPartContentList.size() == userPartContentIdList.size()){
				for(int j=0; j<userPartContentList.size(); j++){
					List<Map> userPartMarkerList = aliyunService.textKeywordScan(userPartContentList.get(j));
					for (Map results : userPartMarkerList) {
						//marker更新到mongo
						mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(userPartContentIdList.get(j))),
								new Update().set("marker", results),
								EntityUserPart.class,
								collectionName);
					}
				}
			}


		}while(userPartList.size() > 0);
		return ReturnData.SUCCESS;
	}

}


