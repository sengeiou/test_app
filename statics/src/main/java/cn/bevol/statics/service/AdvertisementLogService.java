package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.entity.Advertisement;
import cn.bevol.statics.entity.AdvertisementLog;
import cn.bevol.statics.entity.items.AdLogCreateTimeItem;
import cn.bevol.statics.entity.items.AdLogItem;
import cn.bevol.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Rc. on 2017/3/24.
 * 广告日志记录
 */
@Service
public class AdvertisementLogService extends BaseService{
    private static Logger logger = LoggerFactory.getLogger(AdvertisementLogService.class);

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    AdvertisementService advertisementService;

    String tname = "entity_advertisement_log";
    Map<String,Integer> map = new HashMap<String,Integer>();
    public  void initADLog(){
        Executors.newFixedThreadPool(1).submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if(getMaxValue(map)>10) {
                            subADLog();
                        }
                        Thread.sleep(1000 * 10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                }
            }
        });
    }

    //Map<String,AtomicInteger> map = Collections.synchronizedMap(new HashMap<String,AtomicInteger>());
   Map<String,AtomicInteger> map2 = new HashMap<String,AtomicInteger>();


    //存Map
    public void addADLog2(Integer id) {
    //可保证该同步块内的所有代码对map是一个原子操作。
    Integer total = 1;
    Integer day =  DateUtils.timeStampParseInt();
    Integer hour = DateUtils.getHour(new Date());
    String mapKey = id+"_"+day+"_"+hour;

    if(map.containsKey(mapKey)){
        map2.get(mapKey).getAndIncrement();
    }
}



    /**
     * 存MAP
     * @param id
     */
   public void addADLog(Integer id, String positionType) {
       // synchronized(map){//可保证该同步块内的所有代码对map是一个原子操作。
            Integer total = 1;
            Integer day =  DateUtils.timeStampParseInt();
            Integer hour = DateUtils.getHour(new Date());
            String mapKey = id+"_"+day+"_"+hour+"_"+positionType;
            if(map.containsKey(mapKey)){
                Integer tTotal = Integer.parseInt(map.get(mapKey).toString());
                total = tTotal+1;

            }
        map.put(mapKey,total);
       // }
       logger.info("'==========广告被点击=============");
    }

    //批量修改入库
      void subADLog(){
          Advertisement ad = new Advertisement();
          Map<String,Integer> tmpMap = new HashMap<String, Integer>();
          List<AdvertisementLog> ls = new ArrayList<AdvertisementLog>();
          synchronized(map) {
              tmpMap.putAll(map);
              map.clear();
          }
          for (Map.Entry<String, Integer> entry : tmpMap.entrySet()) {
              AdvertisementLog adLog = new AdvertisementLog();
              String mapKey = entry.getKey();
              String[] keys = mapKey.split("_");
              Integer adId = Integer.valueOf(keys[0]);
              String adDate = keys[1];
              Integer hour = Integer.valueOf(keys[2]);
              String positionType = keys[3];
              Integer newTotal = entry.getValue();
              adLog.setAdId(adId);
              adLog.setAdDate(adDate);
              adLog.setHour(hour);
              adLog.setPositionType(positionType);
              adLog.setId(this.getId(tname));
              adLog.setTotal(newTotal);
              adLog.setCreateTime(DateUtils.nowInSeconds());
              ls.add(adLog);
          }
          mongoTemplate.insertAll(ls);
          logger.info("==========日志入库成功！插入条数"+ls.size()+"============");
    }


    //批量修改入库
    @Deprecated
    void subADLog2(){
        Advertisement ad = new Advertisement();
        List<AdvertisementLog> ls = new ArrayList<AdvertisementLog>();
        synchronized(map) {
            Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                AdvertisementLog adLog = new AdvertisementLog();
                String mapKey = entry.getKey();
                String[] keys = mapKey.split("_");
                Integer adId = Integer.valueOf(keys[0]);
                String adDate = keys[1];
                Integer hour = Integer.valueOf(keys[2]);
                Integer newTotal = entry.getValue();
                adLog.setAdId(adId);
                adLog.setAdDate(adDate);
                adLog.setHour(hour);
                adLog.setTotal(newTotal);
                adLog.setCreateTime(DateUtils.nowInSeconds());
                ls.add(adLog);

                Query query = new Query(Criteria.where("id").is(adId));
                Update update = new Update();
                update.set("logs",ls);
                mongoTemplate.findAndModify(query,update,Advertisement.class);
                 //   mongoTemplate.upsert(query,update,Advertisement.class);

            }


            map.clear();
        }

        logger.info("==========日志入库成功！============");
    }



    /**
     * 求Map<K,V>中Value(值)的最大值
     *
     * @param map
     * @return
     */
    public static Integer getMaxValue(Map<String, Integer> map) {
        if (map == null||map.size()==0)
            return 0;
        int length =map.size();
        Collection<Integer> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        return Integer.parseInt(obj[length-1].toString());
    }




    /***
     * 根据广告ID查询点击量
     */
    public List<AdLogCreateTimeItem> findByadId(Integer adId, String startTime, String endTime){
        Query query =   new Query();
        Criteria criteria = new Criteria();
        Integer sTime =0;
        Integer eTime = 0;
        try {
            if(!StringUtils.isEmpty(startTime)) {
                sTime = DateUtils.dateParseIntDate(startTime);
            }
            if(!StringUtils.isEmpty(endTime)) {
                eTime = DateUtils.dateParseIntDate(endTime);
            }
            if(sTime>0&&eTime>0)
            criteria.and("create_time").gte(sTime).lte(eTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        criteria.and("ad_id").is(adId);

        query.addCriteria(criteria);
        List<AdvertisementLog> ls = mongoTemplate.find(query, AdvertisementLog.class, tname);
        Map<String,AdLogCreateTimeItem> totalMap = new HashMap<String,AdLogCreateTimeItem>();
        sort:
        for (AdvertisementLog log:ls) {
            String key = log.getAdDate()+"_"+log.getHour();
            if(!totalMap.containsKey(key)){
                AdLogCreateTimeItem item = new  AdLogCreateTimeItem();
                item.setTotal(log.getTotal());
                item.setAdCreateTime(log.getCreateTime());
                totalMap.put(key,item);
                continue sort;
            }
            AdLogCreateTimeItem timeItem =  totalMap.get(key);
            timeItem.setTotal(log.getTotal() + timeItem.getTotal());
            totalMap.put(key,timeItem);
        }

       List<AdLogCreateTimeItem> list = new ArrayList<>();
        for (Map.Entry <String,AdLogCreateTimeItem> entity: totalMap.entrySet()) {

            AdLogCreateTimeItem totalItem = new AdLogCreateTimeItem();
            totalItem.setAdDate( entity.getKey());
            totalItem.setTotal(entity.getValue().getTotal());
            totalItem.setAdCreateTime(entity.getValue().getAdCreateTime());
            list.add(totalItem);
        }
        Collections.sort(list, new Comparator<AdLogCreateTimeItem>() {
            @Override
            public int compare(AdLogCreateTimeItem arg0, AdLogCreateTimeItem arg1) {
                return arg1.getAdCreateTime().compareTo(arg0.getAdCreateTime());
            }

        });

        return list;

    }

    /***
     * 分页查询点击量
     */
    public Paged<AdLogItem> findAdvertisementLogByPage(String startTime, String endTime, Integer pageSize, Integer startPage){
        Query query =   new Query();
        Criteria criteria = new Criteria();
        Integer sTime =0;
        Integer eTime = 0;
        try {
            if(!StringUtils.isEmpty(startTime)) {
                sTime = DateUtils.dateParseIntTime(startTime);
            }
            if(!StringUtils.isEmpty(endTime)) {
                eTime = DateUtils.dateParseIntTime(endTime);
            }
            if(sTime>0&&eTime>0)
                criteria.and("create_time").gt(sTime).lte(eTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        query.addCriteria(criteria);
        long total = mongoTemplate.count(query, AdvertisementLog.class);
        query.skip((startPage-1)*pageSize).limit(pageSize);
        List<AdvertisementLog> ls = mongoTemplate.find(query, AdvertisementLog.class, tname);
        Map<Integer,Integer> totalMap = new HashMap<>();
        sort:
        for (AdvertisementLog lg:ls) {
           if(!totalMap.containsKey(lg.getAdId())){
               totalMap.put(lg.getAdId(),lg.getTotal());
               continue sort;
           }
            totalMap.put(lg.getAdId(), lg.getTotal() + totalMap.get(lg.getAdId()));
        }
        List<AdLogItem> logItem = new ArrayList<AdLogItem>();
        for (Map.Entry <Integer,Integer> entity: totalMap.entrySet()) {
            AdLogItem item = new AdLogItem();
            Integer adId = entity.getKey();
            Integer logTotal = entity.getValue();
            Advertisement ad =  advertisementService.findAdvertisementById(adId);
            if(!StringUtils.isEmpty(ad)){
                item.setType(ad.getType());
                item.setAdName(ad.getName());
                item.setClassifyId(ad.getClassifyId());
                item.setBannerType(ad.getBannerType());
                item.setPositionType(ad.getPositionType());
                item.setOrientation(ad.getOrientation());
                item.setRedirectType(ad.getRedirectType());
                item.setType(ad.getType());

            }
            item.setAdId(adId);
            item.setLogTotal(logTotal);
            logItem.add(item);
        }
        Paged<AdLogItem> itemPage = new Paged<AdLogItem>();
        itemPage.setResult(logItem);
        itemPage.setTotal((int)total);
        return itemPage;
    }






    public static void main(String[] args) {
//        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
//       final AdvertisementLogService s = (AdvertisementLogService) context.getBean("advertisementLogService");
//
//        Paged<AdLogItem> ls =s.findAdvertisementLogByPage("2017-05-01","2017-07-01",20,1);
//        System.out.println(ls.getTotal());
//
//        Executors.newFixedThreadPool(60).submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        s.addADLog(new Random().nextInt(10));
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        long startTime = System.currentTimeMillis();
//        Iterator<Map.Entry<String, Integer>> it = s.map.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, Integer> entry = it.next();
//            System.out.println("Key:"+entry.getKey()+"   Value:"+entry.getValue());
//        }
      // System.out.println( s.getMaxValue(s.map));
      //  s.initADLog();
       // System.out.println(s.findByadId(5,null,null));


    }

}

