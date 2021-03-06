package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.MakeCache;
import cn.bevol.app.dao.mapper.*;
import cn.bevol.app.entity.dto.Doyen;
import cn.bevol.app.entity.dto.EssenceComment;
import cn.bevol.app.entity.dto.Label;
import cn.bevol.model.entityAction.Comment;
import cn.bevol.app.entity.model.Find;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.model.entity.EntityFind;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.Log.LogMethod;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FindService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(FindService.class);

    @Autowired
    MongoTemplate mongoTemplate;


    @Autowired
    cn.bevol.app.cache.redis.RedisCacheProvider cacheProvider;

    @Autowired
    private FindOldMapper findMapper;
    
    @Autowired
    EntityFindOldMapper entityFindMapper;
    
    @Autowired
    IndexOldMapper indexMapper;
    
    @Autowired
    GoodsOldMapper goodsMapper;
    
    @Autowired
    DoyenOldMapper doyenMapper;
    
    @Autowired
    UserService userService;
    
    @Autowired
    GoodsService goodsService;

    @Resource
    FreemarkerService freemarkerService;
    
    @Resource
    IndexService indexService;
    
    @Autowired
    EntityService entityService;
    @Autowired
    AliyunService aliyunService;
    
    
    /**
     * v3.0????????????
     * ????????????????????????
     * ??????????????????
     * @return
     */
    public ReturnData findLabelList() {
        return new CacheableTemplate<ReturnData>(cacheProvider) {
            @Override
            protected ReturnData getFromRepository() {
                try {
                    List<Label> list = findMapper.findLabelList();
                    Map map = new HashMap();
                    map.put("labelList", list);
                    return new ReturnData(map);
                } catch (Exception e) {
                	Map map=new HashMap();
                	map.put("method", "FindService.findLabelList");
                	new LogException(e,map);
                    return ReturnData.ERROR;
                }
            }

            @Override
            protected boolean canPutToCache(ReturnData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_FIND_LABLE_PREFIX)), true);
    }


    /**
     * ?????????????????????????????????ID????????????3???
     * ???????????????
     * @param id
     * @return
     */
    @Deprecated
    @LogMethod
    @MakeCache(CACHE_NAME.INSTANCE_FIND_ARTICLE_PREFIX)
    public ReturnData findArticle(Integer id) {
        List<Label> list = findMapper.findArticle(id);
        Map map = new HashMap();
        map.put("findarticle", list);
        return new ReturnData(map);
    }

    /**
     * ??????????????????
     * ???mongo???mysql
     * @param id: ??????id
     * @return
     */
    public ReturnData findArticleInfo(final Integer id) {
        return new CacheableTemplate<ReturnData>(cacheProvider) {
            @Override
            protected ReturnData getFromRepository() {
                try {
                    Find find = findMapper.getById(id.longValue());
                    Criteria cr = Criteria.where("id").is(id);
                    Query query = new Query(cr);
                    EntityFind entityFind = mongoTemplate.findOne(query, EntityFind.class, "entity_find");
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("id", find.getId());
                    dataMap.put("header_image", find.getHeaderImage());
                    dataMap.put("title", find.getTitle());
                    dataMap.put("descp", find.getDescp());
                    dataMap.put("hitNum", entityFind.getHitNum());
                    dataMap.put("commentNum", entityFind.getCommentNum());
                    String tag = find.getTag();
                    if(tag != null){
                        String[] tags = tag.split(",");
                        dataMap.put("tag", tags);
                    }else{
                        dataMap.put("tag", new String[0]);
                    }
                    //???n????????????cdn???????????? 0/1/2
                    dataMap.put("n", new Random().nextInt(2));
                    String descp = freemarkerService.getStringFromFtl("app_find_header", dataMap);
                    if(!"".equals(descp)){
                        find.setDescp(descp);
                    }else{
                        return new ReturnData(-1, "??????????????????");
                    }
                    find.setHitNum(entityFind.getHitNum());
                    find.setCommentNum(entityFind.getCommentNum());
                    find.setLikeNum(entityFind.getLikeNum());
                    find.setCollectionNum(entityFind.getCollectionNum());
                    return new ReturnData(find);
                } catch (Exception e) {
                	Map map=new HashMap();
                	map.put("method", "FindService.findArticleInfo");
                	map.put("id", id);
                	new LogException(e,map);
                    return ReturnData.ERROR;
                }
            }

            @Override
            protected boolean canPutToCache(ReturnData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_FIND_INFO_PREFIX,id+"")), true);
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public ReturnData findtype() {
        return new CacheableTemplate<ReturnData>(cacheProvider) {
            @Override
            protected ReturnData getFromRepository() {
                try {
                    List<Label> list = findMapper.findType();
                    Map map = new HashMap();
                    map.put("findtype", list); 
                    return new ReturnData(map);
                } catch (Exception e) {
                	Map map=new HashMap();
                	map.put("method", "FindService.findtype");
                	new LogException(e,map);
                    return ReturnData.ERROR;
                }
            }

            @Override
            protected boolean canPutToCache(ReturnData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_FIND_TYPE_PREFIX)), true);
    }
    
    
    /**
     * pc
     * ??????????????????
     * ???????????????
     * @param pager
     * @param pageSize
     * @return
     */
    public ReturnListData industryList(final int pager, final int pageSize) {
        return new CacheableTemplate<ReturnListData>(cacheProvider) {
            @Override
            protected ReturnListData getFromRepository() {
                try {
                	long start=0;
                    if (pager > 1) {
                        start = Long.valueOf((pager-1) * pageSize + "");
                    }  
                    	//???hq_industry id??????  ??????
                    List<EntityFind> labelList=entityFindMapper.industryList(start,pageSize);
                    long count=entityFindMapper.count2();
                    return new ReturnListData(labelList,count);
                } catch (Exception e) {
                	Map map=new HashMap();
                	map.put("method", "FindService.industryList");
                	map.put("pager", pager);
                	map.put("pageSize", pageSize);
                	new LogException(e,map);
                }
                return null;
            }
            @Override
            protected boolean canPutToCache(ReturnListData returnValue) {
                return (returnValue.getTotal()>0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_INDUSTRY_PREFIX,pager+"",pageSize+"")), true);
    }

    /**
     * v2.9??????
     * ????????????????????????
     *
     * @return
     */
    @Deprecated
    public ReturnData findOldAarticleList(final int pager, final int pageSize) {
        return new CacheableTemplate<ReturnData>(cacheProvider) {
            @Override
            protected ReturnData getFromRepository() {
                try {
                	long start=0;
                    if (pager > 1) {
                        start = Long.valueOf((pager-1) * pageSize + "");
                    }  
                    long total=0;
                    total=indexMapper.count();
                    List<EssenceComment> list = indexMapper.oldAarticleList(start,pageSize);
                    list=indexService.encodeEntity(list);
                    return new ReturnData(list,total);
                } catch (Exception e) {
                	Map map=new HashMap();
                	map.put("method", "FindService.findOldAarticleList");
                	map.put("pager", pager);
                	map.put("pageSize", pageSize);
                	new LogException(e,map);
                    return ReturnData.ERROR;
                }
                
            }

            @Override
            protected boolean canPutToCache(ReturnData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_FIND_OLDAARTICLE_PREFIX,pager+"",pageSize+"")), true);
    }
    
    
    /**
     * v2.9 ??????????????????
     *
     * @return
     */
    public ReturnData findOldAarticleList2(final Integer pager, final Integer pageSize) {
        return new CacheableTemplate<ReturnData>(cacheProvider) {
            @Override
            protected ReturnData getFromRepository() {
                try {
                	long start=0;
                    if (pager > 1) {
                        start = Long.valueOf((pager-1) * pageSize + "");
                    }  
                    long total=0;
                    total=indexMapper.count();
                    //????????????????????????
                    List<EssenceComment> elist = indexMapper.oldAarticleList(start,pageSize);
                    //??????????????????
                    elist=indexService.encodeEntity(elist);
                    
                    List<Long> userIds=new ArrayList<Long>();
                	for(EssenceComment ec:elist){
                		userIds.add(ec.getUserId());
                	}
                	//?????????????????????????????????
                	List<UserInfo> userInfos=mongoTemplate.find(new Query(Criteria.where("id").in(userIds)), UserInfo.class,"user_info");
                	List<EssenceComment> ecList=new ArrayList<EssenceComment>();
                	//??????????????????
                	for(EssenceComment ec:elist){
                		for(UserInfo user:userInfos){
                			if(user.getId()==ec.getUserId().intValue()){
                				//???????????????????????????
                				ec.setEssenceCommentNum(user.getEssenceCommentNum());
                				//????????????????????????
                				ec.setXxsNum(user.getXxsNum());
                				ecList.add(ec);
                			}
                    	}
            		}
                	
                    return new ReturnData(ecList,total);
                } catch (Exception e) {
                	Map map=new HashMap();
                	map.put("method", "FindService.findOldAarticleList2");
                	map.put("pager", pager);
                	map.put("pageSize", pageSize);
                	new LogException(e,map);
                    return ReturnData.ERROR;
                }
                
            }

            @Override
            protected boolean canPutToCache(ReturnData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_FIND_OLDAARTICLE2_PREFIX,pager+"",pageSize+"")), true);
    }


    /**
     * ??????????????????????????????
     * @param type: 1????????? 2????????????
     * @param typeId: type=1,typeId:?????????id; type=2,typeId:??????id
     * @param publishTime: ????????????
     * @return
     */
	public ReturnData addEssenceComment(int type, long typeId, long publishTime) {
		try {
			
			String actionType = "entity_comment_goods";
			String entityName="user_info";
			//1????????? 2????????????
			if(type==1){
				Doyen doyen=doyenMapper.getDoyenById2(typeId);
				long goodsId=doyen.getGoodsId();
				long userId=doyen.getUserId();
				Goods goods=goodsMapper.getById(goodsId);
				//mongo??????  ???doyen(goods_id user_id)
	            EssenceComment ec=new EssenceComment();
	            ec.setUserId(userId);
	            ec.setType(type);
	            ec.setTypeId(typeId);
	            ec.setContent(doyen.getDoyenComment());
	            ReturnData rd=userService.getUserById(userId);
	            UserInfo userInfo=(UserInfo)rd.getResult();
	            ec.setNickname(userInfo.getNickname());
	            ec.setHeadimgurl(userInfo.getHeadimgurl());
	            ec.setUserDescz(userInfo.getDescz());
	            ec.setSkinResults(userInfo.getSkinResults());
	            ec.setSkin(userInfo.getResult());
	            ec.setGoodsId(goodsId);
	            ec.setGoodsImage(goods.getImage());
	            ec.setGoodsMid(goods.getMid());
	            ec.setGoodsTitle(goods.getTitle());
	            ec.setPublishTime(publishTime);
	            ec.setIsEssence(0);
	            ec.setHiddenStatus(0);
	            ec.setLikeNum(0L);
	            ec.setCreateTime(new Date().getTime()/1000);
	            //insert hq_essence_comment
				int i=indexMapper.addEssenceComment(ec);
				if(i!=1){
					return ReturnData.FAILURE;
				}
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), new Update().inc("xxsNum", 1), UserInfo.class,entityName);
				return ReturnData.SUCCESS;
			}else if(type==2){
				//??????typeId???mongodb???goodsId userId
				Query query = new Query(Criteria.where("id").is(typeId));
	            Comment comment = mongoTemplate.findOne(query, Comment.class, actionType);
	            EssenceComment ec=new EssenceComment();
	            ec.setUserId(comment.getUserId());
	            ec.setType(type);
	            ec.setTypeId(typeId);
	            //??????
	            ec.setContent(StringEscapeUtils.escapeJava(comment.getContent()));
	            ec.setSkinResults(comment.getSkinResults());
	            ec.setSkin(comment.getSkin());
	            ec.setIsEssence(comment.getIsEssence());
	            ec.setLikeNum(comment.getLikeNum());
	            ec.setHiddenStatus(comment.getHidden());
	            //user_id
	            ReturnData rd=userService.getUserById(comment.getUserId());
	            UserInfo userInfo=(UserInfo)rd.getResult();
	            ec.setNickname(userInfo.getNickname());
	            ec.setHeadimgurl(userInfo.getHeadimgurl());
	            ec.setUserDescz(userInfo.getDescz());
	            //goods_id
	            ec.setGoodsId(comment.getEntityId());
	            Goods goods2=goodsMapper.getById(comment.getEntityId());
	            ec.setGoodsImage(goods2.getImage());
	            ec.setGoodsMid(goods2.getMid());
	            ec.setGoodsTitle(goods2.getTitle());
	            ec.setPublishTime(publishTime);
	            ec.setCreateTime(new Date().getTime()/1000);
	            ec.setLikeNum(0L);
	            //insert hq_essence_comment
				int i=indexMapper.addEssenceComment(ec);
				if(i!=1){
					return ReturnData.FAILURE;
				}
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), new Update().inc("essenceCommentNum", 1), UserInfo.class,entityName);
				return ReturnData.SUCCESS;
			}
			return ReturnData.FAILURE;
       } catch (Exception e) {
    	   Map map=new HashMap();
	       map.put("method", "FindService.addEssenceComment");
	       map.put("type", type);
	       map.put("typeId", typeId);
	       map.put("publishTime", publishTime);
	       new LogException(e,map);
           return ReturnData.FAILURE;
       }
	}
    

    public boolean insertFindType(Label test) {
        int i = findMapper.insertFindType(test);
        return i > 0 ? true : false;
    }

    public boolean deleteFindType(Label test) {
        int i = findMapper.deleteFindType(test);
        return i > 0 ? true : false;
    }

    public boolean insertFindTags(Label test) {
        int i = findMapper.insertFindTags(test);
        return i > 0 ? true : false;
    }

    public boolean deleteFindTags(Label test) {
        int i = findMapper.deleteFindTags(test);
        return i > 0 ? true : false;
    }

    public boolean insertNewFind(Label test) {
        int i = findMapper.insertNewFind(test);
        return i > 0 ? true : false;
    }

    public boolean deleteNewFind(Label test) {
        int i = findMapper.deleteNewFind(test);
        return i > 0 ? true : false;
    }

    
    /**
     * ????????????,?????????
     * @param type ????????????
     * @param tag  ????????????
     * @param skin ????????????
     * @param sortType ????????????
     * @param pager 
     * @param pageSize
     * @return
     */
	public ReturnListData findList(final int type, final String tag, final String skin, final String sortType, final int pager, final int pageSize){

		return new CacheableTemplate<ReturnListData>(cacheProvider) {
            @Override
            protected ReturnListData getFromRepository() {
				try {
					//OpenSearch???????????? ??????
					ReturnListData rlds=findListByOpenSearch(type,tag,skin,sortType,pager,pageSize);
					List<Map> maps=rlds.getResult();
					String appName="find";
					if(maps!=null&&maps.size()>0) {
                        List<Long> flis=new ArrayList<Long>();
						for(int i=0;i<maps.size();i++) {
							Map m=maps.get(i);
							if(m!=null) {
								Map efind=maps.get(i);
								String[] imgFields={"image","header_image","pc_image"};
								for(String f:imgFields) {
									if(efind.get(f)!=null&&!StringUtils.isBlank(efind.get(f)+"")) {
										//??????????????????
										efind.put(f+"Src", CommonUtils.getImageSrc(appName,efind.get(f)+""));
									}
								}
								
								/*String image=(String) efind.get("image");
								String headerImage=(String) maps.get(i).get("header_image");
								if(headerImage!=null)
									efind.put("headerImage", headerImage);
								String pcImage=(String) maps.get(i).get("pc_image");
								if(pcImage!=null)
									efind.put("pcImage", pcImage);
								efind.put("path", image+"@50p");*/
								efind.put("collectionNum",efind.get("collection_num"));
								efind.put("commentNum",efind.get("comment_num"));
								efind.put("hitNum",efind.get("hit_num"));
								efind.put("likeNum",efind.get("like_num"));
								efind.put("notLikeNum",efind.get("notlike_Num"));
								efind.put("sub_head",efind.get("subhead"));
							}
						}
					}
					//??????????????????
					addFindUserInfo(maps);
					return rlds; 
			    } catch (Exception e) {
			    	Map map = new HashMap();
					map.put("method", "FindService.findList");
					map.put("type", type);
					map.put("tag", tag);
					map.put("skin", skin);
					map.put("pager", pager);
					map.put("pageSize", pageSize);
					map.put("sortType", sortType);
					new LogException(e, map);
			    }
	        return ReturnListData.ERROR;
            } 
            @Override
            protected boolean canPutToCache(ReturnListData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet()== 0 &&
                        returnValue.getTotal()>0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_FIND_LIST_PREFIX,sortType,type+"",tag,pager+"",pageSize+"")), true);
	
	}
	
	public ReturnListData findListByOpenSearch(int type, String tag, String skin, String sortType, int pager, int pageSize){
		String appName =  "hq_find";
 		String keywords="hidden:'0'";
		if(type>0&&type!=9) {
			keywords=keywords+" AND type:'"+type+"'";
		} 
		if(!StringUtils.isBlank(tag)) {
			String tagss[]=tag.split(",");
			String ts="";
			for(int i=0;i<tagss.length;i++) {
				ts+=" OR tag:'"+tagss[i]+"'";
			}
			ts=ts.substring(4);
			keywords+="AND ("+ts+")";
		}
		
		if(!StringUtils.isBlank(skin)) {
 			String tagss[]=skin.split(",");
			String ts="";
			for(int i=0;i<tagss.length;i++) {
				ts+=" OR skin:'"+tagss[i]+"'";
			}
			ts=ts.substring(4);
			keywords+=" AND ("+ts+")";
		}
		long time=new Date().getTime()/1000;
		String sort="";
		if(StringUtils.isNotBlank(sortType)) {
			sort="sort="+sortType;
		} else {
			sort="sort=+sort;-publish_time";
		}
		String quertyString=keywords+"&&filter=publish_time<"+time+"&&"+sort;
		System.out.println(quertyString);
        long start = 0;
        if (pager < 0) {
            return ReturnListData.ERROR;
        }
		ReturnListData rlds=aliyunService.openSearch(appName, quertyString, pager, pageSize);
		
		if(rlds.getRet()!=0){
			return rlds;
		}
		List<Map> list=rlds.getResult();
		addFindUserInfo(list);
        return rlds;
	
	}
	
	/**
	 * v3.1
	 * ?????????????????????banner??????
	 * @return
	 */
	public Map getFindBannerInfo(){
		List<Map> bannerList=entityService.getConfigList("xxs_button");
		Map bannerMap=new HashMap();
		if(null!=bannerList && bannerList.size()>0){
			for(Map map:bannerList){
				if(null!=map.get("param")){
					Map paramMap=(Map)map.get("param");
					if(null!=paramMap.get("type") && 7== Integer.parseInt(paramMap.get("type")+"")){
						bannerMap.put("image", map.get("image"));
						bannerMap.put("type", paramMap.get("type"));
						bannerMap.put("content", paramMap.get("content"));
						bannerMap.put("id", paramMap.get("id"));
					}
				}
			}
		}
		return bannerMap;
	}
	
	
	/**
	 * ??????????????????
	 * @param list
	 */
	private void addFindUserInfo(List<Map> list) {
        //????????????id
		StringBuffer sb=new StringBuffer();
        if (list!=null&&list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
            	Map map=list.get(i);
            	if(map.get("author_id")!=null) {
                	Long userId= Long.parseLong(map.get("author_id")+"");
                	if(userId>0) {
                		sb.append(",").append(userId);
                	}
            	}
                
            }
            
            //??????????????????
            if(sb.length()>0) {
    			ReturnData rd = userService.findUserinfoByIds(sb.substring(1));
    			List<UserInfo> userInfos=(List<UserInfo>) rd.getResult();
                for(Map map:list){
                	boolean flag=false;
                	Long userId=0L;
                	if(map.get("author_id")!=null) {
                    	 userId= Long.parseLong(map.get("author_id")+"");
                	}
        			for(int i=0;!flag&&i<userInfos.size();i++){
        				if(userId.equals(userInfos.get(i).getId())){
        					flag=true;
        					map.put("baseInfo", userInfos.get(i).getBaseInfo());
        				}
        			}
         		}

            }

        }
        

	}

}
