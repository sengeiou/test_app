package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.entity.Advertisement;
import cn.bevol.statics.entity.AdvertisementLog;
import cn.bevol.statics.entity.vo.Advertisement2VO;
import cn.bevol.statics.entity.vo.AdvertisementVO;
import cn.bevol.util.DateUtils;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.statistics.StatisticsI;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Rc. on 2017/3/22.
 * 广告位管理
 */
@Service
public class AdvertisementService extends BaseService implements StatisticsI {
    @Autowired
    MongoTemplate mongoTemplate;

    String tname = "entity_advertisement";
    /**
     * 添加广告位
     * state:0 发布中 1：已过期 2：未开始
     * positionType：1 app;2 m;3 pc
     * redirectType：1 站内； 2 站外
     * @return
     */
    public ReturnData insert(Integer name, Integer orientation, Integer bannerType, String type, Integer entityId, String entityName, String imgUrl, String positionType, Integer redirectType, String redirectUrl, Integer classifyId, Integer hidden, String publishTime, String overdueTime, String creater){
        Integer pTime =0;
        Integer oTime = 0;
        try {
            pTime = DateUtils.dateParseIntDate(publishTime);
            oTime =  DateUtils.dateParseIntDate(overdueTime);
            if(oTime<pTime){
                return ReturnData.WARN_PUBLISHTIMEGTOVERDUETIME;
            }
            if(hidden == 0){
                Advertisement ad=   findByOrientation(null,name,orientation,type,positionType,classifyId,pTime);
                if (ad!=null){
                    return ReturnData.WARN_ADVERTISEMENT;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return ReturnData.ERROR;
        }
        Advertisement advertisement = new Advertisement();
        advertisement.setId(this.getId(tname));
        advertisement.setOrientation(orientation);
        advertisement.setEntityId(entityId);
        advertisement.setImgUrl(imgUrl);
        advertisement.setName(name);
        advertisement.setEntityName(entityName);
        advertisement.setRedirectType(redirectType);
        advertisement.setPositionType(positionType);
        advertisement.setRedirectUrl(redirectUrl);
        advertisement.setState(0);
        if(pTime>DateUtils.nowInSeconds()){
            advertisement.setState(2);
        }
        if(oTime<DateUtils.nowInSeconds()){
            advertisement.setState(1);
        }
        advertisement.setClassifyId(classifyId);
        advertisement.setType(type);
        advertisement.setHidden(hidden);
        advertisement.setBannerType(bannerType);
        advertisement.setIsReplace(0);
        advertisement.setPublishTime(pTime);
        advertisement.setOverdueTime(oTime);
        advertisement.setCreateTime(DateUtils.nowInSeconds());
        advertisement.setCreater(creater);
        mongoTemplate.save(advertisement,tname);
        return ReturnData.SUCCESS;
    }

    /***
     * 查询当前位置是否有正在运行的广告通过id
     * @return
     */
    public Advertisement findByOrientation(Integer id){
        Advertisement advertisement = mongoTemplate.findOne(new Query(Criteria.where("id").is(id)),Advertisement.class, tname);
        Integer name = advertisement.getName();
        Integer orientation = advertisement.getOrientation();
        String type = advertisement.getType();
        String positionType = advertisement.getPositionType();
        Integer classifyId = advertisement.getClassifyId();
        Integer publishTime = advertisement.getPublishTime();
        return findByOrientation(id,name, orientation, type,positionType, classifyId, publishTime);
    }


    /***
     * 查询当前位置是否有正在运行的广告
     * @return
     */
    public Advertisement findByOrientation(Integer id, Integer name, Integer orientation, String type, String positionType, Integer classifyId, Integer publishTime){
        Criteria criteria = new Criteria();
        criteria.and("name").is(name);
        criteria.and("hidden").is(0);//未被隐藏
        criteria.and("state").is(0);
        criteria.and("orientation").is(orientation);
        criteria.and("overdue_time").gte(publishTime);
        if(!StringUtils.isEmpty(type)){
            criteria.and("type").is(type);
        }
        if(StringUtils.isEmpty(classifyId)){
            criteria.and("classify_id").is(classifyId);
        }
        if(null!=id&&id>0){
            criteria.and("id").ne(id);
        }
        if(!StringUtils.isEmpty(positionType)){
            //模糊匹配
            Pattern pattern = Pattern.compile("^.*"+positionType+".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("position_type").regex(pattern);
        }
        Advertisement advertisement = mongoTemplate.findOne(new Query(criteria),Advertisement.class, tname);
        return  advertisement;
    }
    /***
     * 修改广告位
     */
    public  ReturnData update(Integer id, Integer name, Integer orientation, String type, Integer bannerType, Integer entityId, String entityName, String imgUrl, String positionType, Integer redirectType, String redirectUrl, Integer classifyId, Integer hidden, String publishTime, String overdueTime, String updater){
        Integer pTime =0;
        Integer oTime =0;
        try {
            pTime = DateUtils.dateParseIntDate(publishTime);
            oTime = DateUtils.dateParseIntDate(overdueTime);
            if(oTime<pTime){
                return ReturnData.ERROR.WARN_PUBLISHTIMEGTOVERDUETIME;
            }
            if(hidden == 0){
                Advertisement ad= findByOrientation(id,name,orientation,type,positionType,classifyId,pTime);
                if (ad!=null){
                    return ReturnData.WARN_ADVERTISEMENT;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Query query = new Query(Criteria.where("id").is(id));
        Update update =  new Update();
        if(name!=null&&name>0){
            update.set("name", name);
        }if(orientation!=null&&orientation>0){
            update.set("orientation",orientation);
        }if(!StringUtils.isEmpty(type)){
            update.set("type",type);
        }if(entityId!=null&&entityId>0){
            update.set("entity_id",entityId);
        }if(!StringUtils.isEmpty(imgUrl)) {
            update.set("img_url", imgUrl);
        }if(!StringUtils.isEmpty(positionType)){
            update.set("position_type",positionType);
        }if(null!=redirectType&&redirectType>0){
            update.set("redirect_type",redirectType);
        }if(!StringUtils.isEmpty(redirectUrl)){
            update.set("redirect_url",redirectUrl);
        }if(null!=classifyId&&classifyId>0) {
            update.set("classify_id", classifyId);
        }if(!StringUtils.isEmpty(publishTime)){
            update.set("publish_time",pTime);
        }if(!StringUtils.isEmpty(overdueTime)){
            update.set("overdue_time",oTime);
        }if(hidden!=null){
            update.set("hidden",hidden);
        }if(!StringUtils.isEmpty(entityName)){
            update.set("entity_name",entityName);
        }
        update.set("state",0);
        if(pTime>DateUtils.nowInSeconds()){
            update.set("state",2);
        }
        if(oTime<DateUtils.nowInSeconds()){
            update.set("state",1);
        }
        if(null!=bannerType){
            update.set("banner_type",bannerType);
        }
        update.set("update_time", DateUtils.nowInSeconds());
        update.set("updater",updater);
        mongoTemplate.findAndModify(query,update,new FindAndModifyOptions().upsert(true), Advertisement.class, tname);
        return ReturnData.SUCCESS;
    }

    public ReturnData status(Integer id, Integer hidden, String updater){
        try {
            if(hidden == 0){
                Advertisement ad = findByOrientation(id);
                if (ad!=null){
                    return ReturnData.WARN_ADVERTISEMENT;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnData.ERROR;
        }
        Query query = new Query(Criteria.where("id").is(id));
        Update update =  new Update();
        if(hidden != null){
            update.set("hidden", hidden);
        }
        if(updater != null){
            update.set("updater",updater);
        }
        update.set("update_time", DateUtils.nowInSeconds());
        mongoTemplate.findAndModify(query,update,new FindAndModifyOptions().returnNew(true).upsert(true), Advertisement.class, tname);
        return ReturnData.SUCCESS;
    }
    /***
     *广告未发布
     * state：0发布中
     * @return
     */
    private ReturnData publishingAdadvertisement() {

       SimpleDateFormat sf = DateUtils.COMMON.getFormat();
        try {
            Integer now =  DateUtils.dateParseIntDate(sf.format(new Date()));
            Query query = new Query(Criteria.where("publish_time").lte(DateUtils.nowInSeconds()).and("overdue_time").gte(DateUtils.nowInSeconds()));
            Update update =  new Update();
            update.set("state",0);
            mongoTemplate.updateMulti(query,update, Advertisement.class, tname);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ReturnData.SUCCESS;
    }

    /***
     *广告未发布
     * state：2未发布
     * @return
     */
    private ReturnData publishAdadvertisement() {

        Integer publishTime =  DateUtils.nowInSeconds();
        Query query = new Query(Criteria.where("publish_time").gt(publishTime));
        Update update =  new Update();
        update.set("state",2);
        mongoTemplate.updateMulti(query,update, Advertisement.class, tname);
        return ReturnData.SUCCESS;
    }
    /***
     *广告位取消发布
     * state:1 已过期
     * @return
     */
    private ReturnData overdueAdadvertisement() {

        Integer overdueTime =  DateUtils.nowInSeconds();
        Query query = new Query(Criteria.where("overdue_time").lte(overdueTime));
        Update update =  new Update();
        update.set("state",1);
        mongoTemplate.updateMulti(query,update, Advertisement.class, tname);
        return ReturnData.SUCCESS;
    }

    /***
     *广告位删除（逻辑删除）
     * @param id
     * @return
     */
    private ReturnData delAdadvertisement(Integer id) {
        mongoTemplate.upsert(new Query(Criteria.where("id").is(id)),new Update().set("hidden",1), Advertisement.class);
        return ReturnData.SUCCESS;
    }
    /***
     *根据ID查询广告位
     * @param id
     * @return
     */
    public Advertisement findAdvertisementById(Integer id) {
        Advertisement ad =  mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), Advertisement.class);
        return ad;
    }

    /***
     * 广告位列表
     */
    public Paged<Advertisement> find(Integer name, Integer orientation, String type, Integer hidden, Integer publishTime, Integer overdueTime, Integer pageSize, Integer startPage){
        Paged<Advertisement> paged = new Paged<Advertisement>() ;
        Query query =   new Query();
        paged.setPageSize(pageSize);
        paged.setCurPage(startPage);
        Criteria criteria = new Criteria();
        if(name!=null&&name>0){
            criteria.and("name").is(name);
        } if(orientation!=null&&orientation>0){
            criteria.and("orientation").is(orientation);
        } if(!StringUtils.isEmpty(type)){
            criteria.and("type").is(type);
        }if(hidden!=null){
            criteria.and("hidden").is(hidden);
        }if(publishTime!=null&&publishTime>0){
            criteria.and("publish_time").gte(publishTime);
        }if(overdueTime!=null&&overdueTime>0){
            criteria.and("overdue_time").lte(overdueTime);
        }
        //criteria.and("hidden").is(0);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "create_time")).skip((startPage-1)*pageSize).limit(pageSize);
        List<Advertisement> ls = mongoTemplate.find(query, Advertisement.class, tname);
        long total= mongoTemplate.count(query,tname);
        paged.setResult(ls);
        paged.setTotal((int)total);
        return paged;
    }

    /***
     * 根据类型查询广告位
     */
    public List<Advertisement> findAd(Integer name, String type, Integer classifyId, String positionType){
        Criteria c = Criteria.where("hidden").is(0);
        c.and("name").is(name);
        c.and("overdue_time").gte(DateUtils.nowInSeconds());
        c.and("publish_time").lte(DateUtils.nowInSeconds());
        if(!StringUtils.isEmpty(type)) {
            c.and("type").is(type);
        }
        if(classifyId!=null&&classifyId>0){
            c.and("classify_id").is(classifyId);
        }
        if(!StringUtils.isEmpty(positionType)){
            //模糊匹配
            Pattern pattern = Pattern.compile("^.*"+positionType+".*$", Pattern.CASE_INSENSITIVE);
            c.and("position_type").regex(pattern);
        }
        List<Advertisement> ls = mongoTemplate.find(new Query(c), Advertisement.class, tname);

        return ls;
    }

    /**
     * 广告查询，返回结果再次封装
     * @param name
     * @param type
     * @param classifyId
     * @param positionType
     * @return
     */
    public List<Advertisement2VO> findAdByType(Integer name, String type, Integer classifyId, String positionType){
        List<Advertisement> ls = this.findAd(name,type,classifyId,positionType);
        List<Advertisement2VO> listVo = new ArrayList<Advertisement2VO>();
        for (Advertisement ad:ls){
            Advertisement2VO vo = new Advertisement2VO();
            vo.setBannerType(ad.getBannerType());
            vo.setId(ad.getId());
            vo.setIsReplace(ad.getIsReplace());
            vo.setOrientation(ad.getOrientation());
            vo.setEntityId(ad.getEntityId());
            vo.setImgUrl(ad.getImgUrl());
            vo.setType(ad.getType());
            vo.setRedirectType(ad.getRedirectType());
            vo.setRedirectUrl(ad.getRedirectUrl());
            vo.setPositionType(ad.getPositionType());
            listVo.add(vo);
        }
        return listVo;
    }



    /***
     * 根据类型和位置查询广告位
     */
    public Map<Integer,AdvertisementVO> findAdByEntityId(Integer name, Integer entityId, String type, Integer orientation){
        Advertisement ad = mongoTemplate.findOne(new Query(Criteria.where("hidden").is(0).and("entity_id").is(entityId)
                .and("name").is(name) .and("type").is(type).and("orientation").is(orientation)), Advertisement.class, tname);

        Map<Integer,AdvertisementVO> map = new HashMap<Integer,AdvertisementVO>();
        AdvertisementVO vo = new AdvertisementVO();
        if(ad!=null){
            vo.setIsAd(1);
            vo.setState(ad.getIsReplace());
        }else {
            vo.setIsAd(0);
        }
        map.put(entityId,vo);
        return map;
    }


    /***
     * 广告时间检测
     */
    public void initAdvertisement(){
        publishingAdadvertisement();
        publishAdadvertisement();
        overdueAdadvertisement();
    }

    /***
     * 同步广告点击量
     */
    public void initAdvertisementClickTotal(){
        BasicDBList dbList = findLog();
        if (dbList != null) {
            for (int i = 0; i < dbList.size(); i++) {
                DBObject obj = (DBObject) dbList.get(i);
                Object adId = obj.get("ad_id");
                Object total = obj.get("total");
                mongoTemplate.upsert(new Query(Criteria.where("id").is(adId)),new Update().set("click_total",total), Advertisement.class);
            }
        }
    }


    /***
     * 查询广告点击量(总量)
     * @return
     */
    public BasicDBList findLog(){
        GroupBy groupBy = GroupBy.key("ad_id").initialDocument("{total:0}")
                .reduceFunction("function(doc, out){out.total+=doc.total}")
                .finalizeFunction("function(out){return out;}");
        GroupByResults<AdvertisementLog> ls = mongoTemplate.group( "entity_advertisement_log",groupBy, AdvertisementLog.class);
        BasicDBList dbList = (BasicDBList)  ls.getRawResults().get("retval");
        return dbList;
    }



    public static void main(String[] args) throws ParseException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        AdvertisementService service = (AdvertisementService) context.getBean("advertisementService");
        //service.insert(1,2,"",201,"https://img1.bevol.cn/Goods/source/5628a86300ad7.jpg",null,1,"2017-02-11 00:00:00","2017-03-11 00:00:00","管理员");
        //service.update(1,"33333","2222","3",2,2,2);
        // Paged<Advertisement> ls =  service.find(null,null,null,null,null,null,1,2);
//        ls.size();
        //   service.delAdadvertisement(10);
        //service.overdueAdadvertisement();
        service.findLog();
        //    service.initAdvertisementClickTotal();
    }
}


