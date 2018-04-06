package cn.bevol.app.service;

import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.HotListOldMapper;
import cn.bevol.app.entity.dto.HotList;
import cn.bevol.app.entity.dto.HotListGood;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotListService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(HotListService.class);

    @Autowired
    MongoTemplate mongoTemplate;


    @Autowired
    RedisCacheProvider cacheProvider;

    @Autowired
    private HotListOldMapper hotListMapper;


    @Autowired
    private CommentService commentService;
    @Autowired
    AliyunService aliyunService;

    /**
     * 热门清单详情
     * 缓存了3个一级评论
     * @param id
     * @return
     */
    public ReturnData detailContent(final Integer id, final Long userId){
        return new CacheableTemplate<ReturnData>(cacheProvider) {
            @Override
            protected ReturnData getFromRepository() { 
                try {
                	//获取清单信息
                    HotList hotList=hotListMapper.findDetail(id);
                    Map map = new HashMap();
                    map.put("detail",hotList);
                    if(null!=hotList && (hotList.getType()==null||hotList.getType()==0)) {
                    	//获取清单关联的产品信息
                        List<HotListGood> goodList=hotListMapper.findDetailGoods(hotList.getGoodsIds(),hotList.getId());
                        //获取该清单的3个一级评论
                        ReturnListData comments=commentService.findComments2_5("lists", id, 0,userId,"", 0, 3);
                        map.put("goods",goodList);
                        map.put("comments",comments.getResult());
                    }
                    return new ReturnData(map);
                } catch (Exception e) {
                    logger.error("method:detailContent arg:{id:" + id +"userId:"+ userId+"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
                    return ReturnData.ERROR;
                }
            }
            @Override
            protected boolean canPutToCache(ReturnData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0 );
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_LISTS_ID_PREFIX, id+"")), true);

    }
    

    /**
     * 热门清单列表
     * @param pager
     * @param pageSize
     * @return
     */
    @Deprecated
    public ReturnListData moreArticle(final int pager, final int pageSize) {
        return new CacheableTemplate<ReturnListData>(cacheProvider) {
            @Override
            protected ReturnListData getFromRepository() {
                try {

                    long start = 0;
                    if (pager >= 1) {
                        start = Long.valueOf(((pager-1) * pageSize) + "");
                    } else if (pager < 0) {
                        return null;
                    }

                    List<HotList> efs = hotListMapper.list(start, pageSize);
                    long total = 0;
                    if (efs.size() > 0) {
                        total = hotListMapper.count();
                    }
                    return new ReturnListData(efs, total);
                } catch (Exception e) {
                    logger.error("method:moreArticle arg:{pager:" + pager + ",pageSize:" + pageSize + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
                    return ReturnListData.ERROR;
                }
            }

            @Override
            protected boolean canPutToCache(ReturnListData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0 &&
                        returnValue.getTotal() > 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "HotListService.moreArticle_" + pager + "_" + pageSize), true);
    }
    


	/**
	 * 根据opansearch排序
	 * @param quertyString
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	public ReturnListData findListsByOpenSearch(String quertyString, int pager, int pageSize) {
		String appName =  "hq_lists";
		String dirName ="lists";
        if (pager < 0) {
            return ReturnListData.ERROR;
        }
		ReturnListData rlds=aliyunService.openSearch(appName, quertyString, pager, pageSize);
		List<Map<String,Object>> lists=rlds.Tesult();
		
		this.createImageSrc(dirName,lists,"mini_image","image");
		
		
		
		
		//计算活动时间
	/*	for(int i=0;i<lists.size();i++) {
			Map<String,Object> mbjs=lists.get(i);
			Object type=mbjs.get("type");
			if(type!=null) {
				Integer intType=Integer.parseInt(type+"");
				//时效性
				if(intType==2){
					Long lastTime=Long.parseLong(mbjs.get("last_time")+"");
					Long startTime=Long.parseLong(mbjs.get("start_time")+"");
					//实效性  计算
					Long curTime=new Date().getTime()/1000;
					int activeState=0;
					String activeStateDesc="";
						if(curTime>lastTime) {
							activeState=3;
							activeStateDesc="已结束 ";
						} else if(curTime<startTime) {
							activeState=2;
							activeStateDesc="未开始";
						}else{
							//一小时
							long hourt=60*60;
							//一天
							long day=hourt*24;
							//计算剩余天数
							long sq=lastTime-curTime;
							
							//计算剩余天数
							long lastDay=sq/day;
							
							//计算剩余小时
							long lastHourt=(sq%day)/hourt;
							if(lastDay==0) {
								activeStateDesc=lastHourt+"小时";
							} else {
								activeStateDesc=lastDay+"天"+lastHourt+"小时";
							}
							activeState=1;
						}
						mbjs.put("active_state", activeState);
						mbjs.put("active_state_desc", activeStateDesc);
					}
			}
  		}*/
		
		return rlds; 
	}
	/**
	 * 话题/清单/试用 列表
	 * @param listsType
	 * 		1 话题 2试用 
	 * @param pager
	 * @param pageSize
	 * @return
	 */
	public ReturnListData findLists(int listsType, int pager,int pageSize){
 		String keywords="hide_status:'0'";
 		long time=new Date().getTime()/1000;
		String sort="sort=-sort;-publish_time";
		String quertyString=keywords+"&&filter=publish_time<"+time+"&&"+sort;
		return this.findListsByOpenSearch(quertyString, pager, pageSize); 
	}

    public  boolean insertHotList(HotList test){
        int i=hotListMapper.insertHotList(test);
        return i>0?true:false;
    }

    public  boolean deleteHotList(HotList test){
        int i=hotListMapper.deleteHotList(test);
        return i>0?true:false;
    }

    public  boolean insertHotListGood(HotListGood test){
        int i=hotListMapper.insertHotListGood(test);
        return i>0?true:false;
    }

    public  boolean deleteHotListGood(HotListGood test){
        int i=hotListMapper.deleteHotListGood(test);
        return i>0?true:false;
    }

    public Integer updateGoodById(Integer id, Integer good_id, String content) {
        return hotListMapper.updateGoodById(id,good_id,content);
    }

    public Integer findListId(HotList test) {
        return hotListMapper.findListId(test);
    }
}
