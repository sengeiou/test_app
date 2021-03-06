package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.GoodsOldMapper;
import cn.bevol.app.dao.mapper.HotListOldMapper;
import cn.bevol.app.dao.mapper.UserInfoOldMapper;
import cn.bevol.app.dao.mapper.VerificationCodeOldMapper;
import cn.bevol.app.entity.dto.HotList;
import cn.bevol.model.entityAction.ApplyGoodsUser;
import cn.bevol.model.entity.*;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.Log.LogMethod;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
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
	UserInfoOldMapper userInfoMapper;
	@Autowired
	VerificationCodeOldMapper verificationCodeMapper;

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
	private HotListOldMapper hotListMapper;

	@Autowired
	private HotListService hotListService;
	private static Logger logger = LoggerFactory.getLogger(UserPartService.class);

	@Autowired
	ValidateService validateService;
	@Autowired
	GoodsOldMapper goodsMapper;


	/**
	 * ????????????????????????
	 * @param tname
	 * @param sort
	 * @param pEntityId
	 * @param p
	 * @param size
	 * @return
	 */
	public ReturnListData findUserPart(final String tname,final int sort,final Long pEntityId,final int p,final int size) {
		return new CacheableTemplate<ReturnListData<List<EntityUserPart>>>(cacheProvider) {
			@Override
			protected ReturnListData getFromRepository() {
				try {
					return new ReturnListData(findUserPartList(tname,sort,pEntityId,p,size), findUserPartCount(tname,sort,pEntityId));
				} catch (Exception e) {
					logger.error("method:findUserPart arg:{tname:" + tname + ",sort:" + sort + ",pEntityId:"+pEntityId+",p:"+p+",pEntityId:"+size+"}"
							+ "   desc:" + ExceptionUtils.getStackTrace(e));
					return ReturnListData.ERROR;
				}
			}

			@Override
			protected boolean canPutToCache(ReturnListData returnValue) {
				return (returnValue != null && returnValue.Tesult() != null && returnValue.Tesult().size() > 0);
			}
		}.execute(
				new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
						CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_USERPART_LIST_PREFIX,tname,pEntityId+"",sort+"",p+"",size+"")),
				true);
	}

	public long findUserPartCount( String tname, int type, Long pEntityId) {
		String actype="entity_user_part_"+tname;
		Query query = Query.query(new Criteria().andOperator(Criteria.where("pEntityId").is(pEntityId)).and("hidden").is(0).and("type").is(1));
		long count=this.mongoTemplate.count(query,EntityUserPart.class, actype);
		return count;

	}

	/**
	 * ????????????????????????????????????
	 * @param tname	????????????(lists)
	 * @param type	??????1???????????? 2???????????? 3?????????????????????(?????????)
	 * @param pEntityId	?????????id
	 * @return
	 */
	public long findUserPartCount2( String tname, int type, Long pEntityId) {
		String actype="entity_user_part_"+tname;
		Query query = Query.query(new Criteria().andOperator(Criteria.where("pEntityId").is(pEntityId)).and("hidden").is(0).and("type").is(type));
		long count=this.mongoTemplate.count(query,EntityUserPart.class, actype);
		return count;

	}

	public List<EntityUserPart> findUserPartList( String tname, int sort, Long pEntityId, int p, int size) {
		String actype="entity_user_part_"+tname;
		if (p > 0) {
			p = (p - 1);
		} else {
			p = 0;
		}
		Integer startId = Integer.valueOf((p * size) + "");

		Query query = Query.query(new Criteria().andOperator(Criteria.where("pEntityId").is(pEntityId)).and("hidden").is(0).and("type").is(1));
		if (sort == 1 ) {
			//??????????????? ??????
			query.with(new Sort(new Order(Direction.DESC, "hitNum")));
		} else {
			//???????????? ??????
			query.with(new Sort(new Order(Direction.DESC, "id")));
		}
		List<EntityUserPart> eups=this.mongoTemplate.find(query.skip(startId).limit(size),EntityUserPart.class, actype);
		return eups;
	}

	/**
	 * ????????????(??????)??????
	 * @param tname
	 * @param pEntityId
	 * @param p
	 * @param size
	 * @return
	 */
	public List<Map> findUserPartListByApply( String tname, int type, long pEntityId, int p, int size) {
		String actype="entity_user_part_"+tname;
		if (p > 0) {
			p = (p - 1);
		} else {
			p = 0;
		}
		Integer startId = Integer.valueOf((p * size) + "");

		Query query = Query.query(new Criteria().andOperator(Criteria.where("pEntityId").is(pEntityId)).and("hidden").is(0).and("type").is(type));
		List<EntityUserPart> eups=this.mongoTemplate.find(query.skip(startId).limit(size),EntityUserPart.class, actype);
		//???????????? ??????????????????
		List<Map> listMap=new ArrayList();
		for(EntityUserPart eup:eups){
			long entityId=eup.getpEntityId();
			long userId=eup.getUserId();
			query = new Query(new Criteria().where("entityId").is(entityId).and("userId").is(userId));
			//?????????????????????
			ApplyGoodsUser agu=this.mongoTemplate.findOne(query,ApplyGoodsUser.class,"apply_goods_user");

			query = new Query(new Criteria().where("id").is(userId));
			UserInfo userInfo=this.mongoTemplate.findOne(query,UserInfo.class,"user_info");
			try {
				JSONObject map = JSONObject.fromObject(eup);
				if(null!=agu){
					//????????????
					map.put("applyTime", agu.getCreateStamp());
				}
				if(null!=userInfo && null!=userInfo.getScore()){
					//????????????
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

	public ReturnData getUserPartById(String tname,Long id) {
		try {

			String actype="entity_user_part_lists";
			Query query = Query.query(new Criteria().andOperator(Criteria.where("id").is(id).and("hidden").is(0)));
			EntityUserPart eup=this.mongoTemplate.findOne(query,EntityUserPart.class, actype);
			return new ReturnData<>(eup);
		} catch (Exception e) {
			logger.error("method:UserPartService.getUserPartById arg:{tname:" + tname + ",id:" + id + "}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnData.ERROR;
		}
	}

	/**
	 * ?????????
	 * @param tname
	 * @param title
	 * @param tags
	 * @param p_entity_id
	 * @param details
	 * @return
	 */
	public ReturnData addUserPart(UserInfo userInfo,String tname,Integer type, String title, String image, String tags, Long p_entity_id,
								  JSONArray details) {
		//???????????????
		ReturnData prd=commentService.oldSwitchOfphoneCheck(userInfo);
		if(prd.getRet()!=0){
			return prd;
		}

		//???????????????
		ReturnData rd1=this.oldVerifyState(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		return this.saveUserPart(userInfo,tname,type,title,image,tags,p_entity_id,details);
	}

	/**
	 * ?????????
	 * @param tname
	 * @param title
	 * @param tags
	 * @param p_entity_id
	 * @param details
	 * @return
	 */
	public ReturnData addUserPart2(UserInfo userInfo,String tname,Integer type, String title, String image, String tags, Long p_entity_id,
								   JSONArray details) {
		//???????????????
		ReturnData prd=this.oldSwitchOfphoneCheck(userInfo);
		if(prd.getRet()!=0){
			return prd;
		}

		//???????????????
		ReturnData rd1=this.verifyState(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		return this.saveUserPart(userInfo,tname,type,title,image,tags,p_entity_id,details);

	}

	public ReturnData saveUserPart(UserInfo userInfo,String tname,Integer type, String title, String image, String tags, Long p_entity_id,
								   JSONArray details){
		try {
			String actype="entity_user_part_"+tname;
			//??????????????????
			ReturnData rd=validateService.vSendTime(userInfo.getId(), actype);
			if(rd.getRet()!=0) return rd;

			EntityUserPart userPart=new EntityUserPart();
			//??????????????????????????????????????????
			rd=this.isFind(tname,p_entity_id,type);
			if(rd.getRet()!=0){
				return rd;
			}
			userPart.setpEntityId(p_entity_id);
			userPart.setTitle(title);
			userPart.setImage(CommonUtils.getImag(image));
			userPart.setId(this.getId(actype));

			userPart.setUserRole(userInfo.getRole());
			//????????????
			if(type==null) type=1;
			userPart.setType(type);
			//??????????????????
			userPart.setUserPartDetails(toUserPartDetails(details));
			userPart.setUniqueId(userPart.getId());
			userPart.setTname("user_part_"+tname);

			if(StringUtils.isNotBlank(tags)){
				String [] ts=tags.split(",");
				if(ts.length>0) userPart.setTags(new ArrayList<Integer>());
				for(int i=0;i<ts.length;i++) {
					userPart.getTags().add(Integer.parseInt(ts[i]));
				}
			}

			//????????????????????????
			userPart.setUserBaseInfo(userInfo.getBaseInfo());
			userPart.setUserId(userInfo.getId());

			String partStr="partNum";
			if(type==1 || type==0){
				//????????????
				this.entityInc(p_entity_id, tname, partStr, 1);
				//???????????????????????????
				cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_USERPART_LIST_PREFIX,tname,userPart.getpEntityId()+""));
			}
			if(type==2){
				partStr="userPartNum";
				this.entityInc(p_entity_id, "apply_goods2", "userPartNum", 1);

				//????????????????????????????????????
				cacheService.cleanCacheListByKey(CACHE_NAME.createInstanceCleanCacheKey(CACHE_NAME.INSTANCE_USERPART_APPGOODS_LIST_PREFIX,userPart.getpEntityId()+""));

				//????????????
				Query query=new Query(Criteria.where("userId").is(userInfo.getId()).and("entityId").is(p_entity_id).and("hidden").is(0).and("deleted").is(0));
				Update update=new Update();
				update.set("userPartState", 1);
				ApplyGoodsUser agu=mongoTemplate.findAndModify(query, update, ApplyGoodsUser.class,"apply_goods_user");
				//??????????????????
				if(null!=agu){
					userPart.setApplyState(agu.getState());
				}
			}
			this.mongoTemplate.save(userPart,actype);

			return new ReturnData(userPart);
		} catch (Exception e) {
			logger.error("method:UserPartService.saveUserPart arg:{tname:" + tname + ",title:" + title + ",image:"+image+",tags:"+tags+",p_entity_id:"+p_entity_id+"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
		}
		return ReturnData.ERROR;
	}



	public ReturnData addUserPart2(UserInfo userInfo,String tname,Integer type, String title, String image, String tags,JSONArray details) {
		//???????????????
		ReturnData prd=commentService.switchOfPhoneCheck(userInfo);
		if(prd.getRet()!=0){
			return prd;
		}

		//???????????????
		ReturnData rd1=this.verifyState(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		type=3;
		return this.saveFreedomUserPart(userInfo,tname,type,title,image,tags,details);
	}

	public ReturnData addUserPart3(UserInfo userInfo,String tname,Integer type, String title, String image, String tags,JSONArray details) {
		//???????????????
		ReturnData prd=commentService.switchOfPhoneCheck(userInfo);
		if(prd.getRet()!=0){
			return prd;
		}

		//???????????????
		ReturnData rd1=this.verifyState(userInfo);
		if(rd1.getRet()!=0){
			return rd1;
		}
		type=3;
		return this.saveFreedomUserPart(userInfo,tname,type,title,image,tags,details);
	}

	public ReturnData saveFreedomUserPart(UserInfo userInfo,String tname,Integer type, String title, String image, String tags,JSONArray details){
		try {
			String actype="entity_user_part_"+tname;

			ReturnData rd=validateService.vSendTime(userInfo.getId(), actype);
			if(rd.getRet()!=0) return rd;

			EntityUserPart userPart=new EntityUserPart();

			userPart.setTitle(title);
			userPart.setImage(CommonUtils.getImag(image));
			userPart.setId(this.getId(actype));
			userPart.setUserRole(userInfo.getRole());
			//????????????
			//if(type==null) type=1;
			userPart.setType(type);
			userPart.setUserPartDetails(toUserPartDetails(details));

			if(StringUtils.isNotBlank(tags)){
				String [] ts=tags.split(",");
				if(ts.length>0) userPart.setTags(new ArrayList<Integer>());
				for(int i=0;i<ts.length;i++) {
					userPart.getTags().add(Integer.parseInt(ts[i]));
				}
			}

			//????????????????????????
			userPart.setUserBaseInfo(userInfo.getBaseInfo());
			userPart.setUserId(userInfo.getId());

			this.mongoTemplate.save(userPart,actype);

			return new ReturnData(userPart);
		} catch (Exception e) {
			logger.error("method:UserPartService.saveFreedomUserPart arg:{tname:" + tname + ",title:" + title + ",image:"+image+",tags:"+tags+"}"
					+ "   desc:" + ExceptionUtils.getStackTrace(e));
			return ReturnData.ERROR;
		}
	}

	private ReturnData isFind(String tname,long p_entity_id,int type){
		if(tname.indexOf("lists")!=-1 && 1==type) {
			HotList lt=hotListMapper.findDetail2(p_entity_id);
			if(lt==null ) return new ReturnData(-2,"???????????????");
			if(lt.getActiveState()!=null) {
				if(lt.getActiveState()==2) {
					return new ReturnData(-2,"??????????????????");
				} else if(lt.getActiveState()==3 && type!=2) {
					return new ReturnData(-2,"??????????????????");
				}
			}
		} else if(2==type) {
			String tname2="apply_goods2";
			EntityBase eb=this.getEntityById(tname2, p_entity_id);
			if(eb==null )return new ReturnData(-2,"???????????????");
		}
		return ReturnData.SUCCESS;
	}


	private  List<UserPartDetail> toUserPartDetails(JSONArray details) {
		List<UserPartDetail> upds=null;
		//if(StringUtils.isNotBlank(details)) {
		if(null!=details && details.size()>0) {
			upds=new ArrayList<UserPartDetail>();
			boolean flag=false;
			//details=details.replaceAll("\\\\","");
			//json?????????????????????
			UserPartDetail upd=null;
			/*org.json.JSONObject  j=new org.json.JSONObject ("  {'type':'ONLINE_SHIPS','details':'"+details+"'}");
			//?????????????????????????????????,?????????????????????????????????
			String jsonstrtemp = j.getString("details");	*/
			JSONArray  jasonObject  =JSONArray.fromObject(details);
			for(int i=0;i<jasonObject.size();i++) {
				Map<String,String> mds=jasonObject.getJSONObject(i);
				String type=mds.get("type");
				if(StringUtils.isNotBlank(type)) {
					if(type.equals("1")) {
						UserPartDetailGoods u=new UserPartDetailGoods();
						if(StringUtils.isNotBlank(mds.get("id"))) {
							long tid=Long.parseLong(mds.get("id"));
							u.setId(tid);
							String tn=mds.get("tname");
							//??????entity
							//Goods e=goodsMapper.getById(tid);
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
	/**
	 * ????????????
	 * @param userInfo
	 * @param id
	 * @param title
	 * @param image
	 * @param tags
	 * @param details
	 * @return
	 */
	public ReturnData updateUserPart(UserInfo userInfo,Long id, String title, String image, String tags,JSONArray details) {
		//???????????????
		ReturnData prd=commentService.oldSwitchOfphoneCheck(userInfo);
		if(prd.getRet()!=0){
			return prd;
		}
		Boolean verifyState=userInfo.getVerifyState();
		//???????????????????????? 1????????? 0?????????
		//???????????????
		ReturnData rd=this.oldVerifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}
		return this.updateMyUserPart(userInfo,id,title,image,tags,details);
	}

	/**
	 * ????????????
	 * @param userInfo
	 * @param id
	 * @param title
	 * @param image
	 * @param tags
	 * @param details
	 * @return
	 */
	public ReturnData updateUserPart2(UserInfo userInfo,Long id, String title, String image, String tags,JSONArray details) {
		//???????????????
		ReturnData prd=commentService.switchOfPhoneCheck(userInfo);
		if(prd.getRet()!=0){
			return prd;
		}
		//???????????????
		ReturnData rd=this.verifyState(userInfo);
		if(rd.getRet()!=0){
			return rd;
		}
		return this.updateMyUserPart(userInfo,id,title,image,tags,details);
	}

	public ReturnData updateMyUserPart(UserInfo userInfo,Long id, String title, String image, String tags,JSONArray details){
		ReturnData eb=getUserPartById("lists",id);
		if(eb.getResult()==null )return new ReturnData(-2,"???????????????");
		String actionType="entity_user_part_lists";
		Update update=new Update();
		update.set("updateStamp", new Date().getTime()/1000);
		if(StringUtils.isNotBlank(title)) {
			update.set("title", title);
		}
		if(StringUtils.isNotBlank(image)) {
			update.set("image", image);
		}
		//??????
		if(StringUtils.isNotBlank(tags)) {
			String [] ts=tags.split(",");
			List<Integer> list=null;
			if(ts.length>0)  list=new ArrayList<Integer>();
			for(int i=0;i<ts.length;i++) {
				list.add(Integer.parseInt(ts[i]));
			}
			if(ts.length>0) {
				update.set("tags", list);
			}
		}
		if(null!=details && details.size()>0) {
			//??????????????????
			update.set("userPartDetails", toUserPartDetails(details));
		}
		mongoTemplate.updateFirst(new Query(new Criteria().where("id").is(id).and("userId").is(userInfo.getId())),update, actionType);
		return ReturnData.SUCCESS;
	}

	/**
	 * ??????????????????
	 * @param tname
	 * @param userId
	 * @param start_id
	 * @param page_size
	 * @return
	 */
	public ReturnListData myUserPartLists(String tname,Long userId, long start_id, int page_size) {
		String actionType="entity_user_part_"+tname;
		Criteria crt= Criteria.where("userId").is(userId).and("hidden").is(0).and("deleted").is(0);
		Query query = Query.query(crt).limit(page_size).with(new Sort(Direction.DESC, "id"));
		if (start_id > 0) {
			crt.and("id").lt(start_id);
		}
		List<EntityUserPart> uais = mongoTemplate.find(query, EntityUserPart.class, actionType);
		long count = mongoTemplate.count(Query.query(crt), actionType);
		return new ReturnListData(uais,count);
	}


	/**
	 * ??????????????????????????????
	 * @param entityId
	 * @limit ????????????
	 * @fields ??????????????????
	 * @return
	 */
	public List<EntityUserPart>  findUserPartGoods(long entityId, Integer limit, String ... fields) {
		String actionType="entity_user_part_lists";
		Criteria crt= Criteria.where("userPartDetails._id").is(entityId).and("hidden").is(0).and("userPartDetails.tname").is("goods");
		Query query = Query.query(crt);
		if(fields!=null) {
			for(int i=0;i<fields.length;i++) {
				query.fields().include(fields[i]);
			}
		}
		query.limit(limit).with(new Sort(Direction.DESC, "hitNum")).with(new Sort(Direction.DESC, "_id"));
		List<EntityUserPart> uais = mongoTemplate.find(query, EntityUserPart.class, actionType);
		return uais;
	}

	public List<EntityUserPart> list(int type,int pager, int rows,String ... fields) {
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

		//????????????????????????????????????
		if(null!=uais && uais.size()>0){
			for(EntityUserPart eup:uais){
				if(null==eup.getCommentNum()){
					eup.setCommentNum(0L);
				}
				if(null!=eup.getpEntityId() && eup.getpEntityId()>0 && null!=eup.getType()){
					if(eup.getType()==1){
						//??????(??????)??????
						ReturnData rd=hotListService.detailContent(eup.getpEntityId().intValue(),null);
						if(rd.getRet()==0) {
							Map map = (Map) rd.getResult();
							if (null != map && map.size() > 0 && null != map.get("detail")) {
								HotList hotList = (HotList) map.get("detail");
								if (null != hotList && null != hotList.getTitle()) {
									eup.setpEntityName(hotList.getTitle());
								}
							}
						}
					}else if(eup.getType()==2){
						//????????????(?????????)
						Query q=new Query(Criteria.where("id").is(eup.getpEntityId()));
						String[] includeFiles={"id","title"};
						this.setQueryFeilds(q,includeFiles);
						EntityApplyGoods2 eag=mongoTemplate.findOne(q,EntityApplyGoods2.class);
						if(null!=eag){
							eup.setpEntityName(eag.getTitle());
						}
					}
				}
			}
		}


		return uais;
	}






	public ReturnData editSort(String ids) {
		try{
			clearSort();
			//???ids?????????
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
	 * ?????????????????????sort
	 * @return
	 */
	public ReturnData clearSort() {
		try{
			//???????????????sort??????
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
	 * ???????????????????????????
	 * @return
	 */
	public ReturnData resetDaySort() {
		try{
			//1???????????????0???
			Update update=new Update();
			update.unset("sort");
			Criteria cr= Criteria.where("sort").is(0);
			mongoTemplate.updateMulti(new Query(cr), update, EntityUserPart.class,"entity_user_part_lists");
			//2?????????????????????0???
			update=new Update();
			update.set("sort",0);
			long start= DateUtils.addHours(new Date(), -24).getTime()/1000;
			long end=new Date().getTime()/1000;
			//????????????-24h --- ????????????
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
	 * ???????????? TODO
	 * @param type 0??????????????? 1???????????? 2???????????? 3?????????????????????
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
	 * ??????????????????
	 * @param id
	 * @param sort
	 * @param sortField
	 * @return
	 */
	public ReturnData userPartListSort(Integer id, Integer sort, String sortField){
		return this.ManualSetSort(EntityUserPart.class, id, sort, sortField);
	}

	/**
	 * ????????????
	 * @param userId
	 * @param nickname
	 * @param entityId
	 * @param id
	 * @param title
	 * @param sortField
	 * @param applyState
	 * @param hidden
	 * @return
	 */
	public ReturnListData listApplyUserPart(Integer userId,
											String nickname,
											Integer entityId,
											Integer id,
											Integer type,
											String title,
											String sortField,
											Integer applyState,
											Integer hidden,
											Integer page,
											Integer rows){
		try {
			Query query = new Query();
			Criteria cr = Criteria.where("deleted").is(0);
			if(null != hidden){
				cr.and("hidden").is(hidden);
			}
			if(null != entityId){
				cr.and("pEntityId").is(entityId);
			}
			if(null != sortField){
				query.with(new Sort(Direction.DESC, sortField));
				cr.and(sortField).exists(true).ne(null);
			}else{
				query.with(new Sort(Direction.DESC, "id"));
				query.skip((page-1) * rows).limit(rows);
			}
			if(null != type){
				cr.and("type").is(type);
			}
			if (null != userId) {
				cr.and("userId").is(userId);
			}
			if (null != nickname) {
				cr.and("userBaseInfo.nickname").regex(nickname);
			}
			if (null != id) {
				cr.and("id").is(id);
			}
			if (null != title) {
				cr.and("title").regex(title);
			}
			if (null != applyState) {
				cr.and("applyState").is(applyState);
			}
			List<EntityUserPart> entityUserPartList = mongoTemplate.find(query.addCriteria(cr), EntityUserPart.class);
			long total = mongoTemplate.count(query, EntityUserPart.class);
			return new ReturnListData(entityUserPartList, total);
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method","ApplyGoodsService.listApplyUserPart");
			map.put("id", id);
			map.put("userId", userId);
			map.put("nickname", nickname);
			map.put("entityId", entityId);
			map.put("title", title);
			map.put("applyState", applyState);
			map.put("hidden", hidden);
			new LogException(e,map);
			return ReturnListData.ERROR;
		}
	}

	/**
	 * ???????????????
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
					//???????????????content
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
						//marker?????????mongo
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
/**
 * ??????????????????(hidden=2)
 * @param userInfo
 * @param id
 * @return
 */
	public ReturnData hiddenUserpartLists(UserInfo userInfo, Long id) {
		String actionType="entity_user_part_lists";
		Update update =new Update().set("hidden",2);
		mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id).and("userId").is(userInfo.getId())),update, actionType);

		return ReturnData.SUCCESS;
	}
}


