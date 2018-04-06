package cn.bevol.internal.service;

import cn.bevol.model.entity.GoodsTask;
import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.dao.mapper.GoodsOldMapper;
import cn.bevol.internal.dao.mapper.SqlOldMapper;
import cn.bevol.util.DateUtils;
import cn.bevol.util.http.HttpUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangcheng on 17-4-12.
 */
@Service
public class CronService {
    private static Logger logger = LoggerFactory.getLogger(CronService.class);

    @Autowired
    MongoTemplate mongoTemplate;


    @Autowired
    SqlOldMapper sqlOldMapper;

    @Autowired
    AliyunService apliyunService;


    @Autowired
    GoodsOldMapper goodsOldMapper;

    @Autowired
    InternalGoodsCalculateService internalGoodsCalculateService;

    @Resource
    private CacheService cacheService;

    @Resource
    private StaticClientService staticClientService;

    private Logger log;

    //单任务锁
    private static Lock lock = new ReentrantLock();

    //处理数据的队列
    private static ArrayBlockingQueue<String> allGoods=new ArrayBlockingQueue<String>(3000000);



    /**
     * 加入产品id到队列
     * @param ids
     */
    public ReturnData addGoodsQueue(String ids) {
        if(org.apache.commons.lang3.StringUtils.isBlank(ids)) {
            return ReturnData.ERROR;
        }
        int pbatch=5;
        String idss[]=ids.split(",");
        StringBuffer sb=new StringBuffer();
        int n=1;
        for(int i=0;i<idss.length;i++) {
            sb.append(idss[i]).append(",");
            if(n%pbatch==0) {
                String str=sb.toString();
                n=1;
                allGoods.add(str);
                sb=new StringBuffer();
            }
            n++;
        }
        if(n>1) {
            String str=sb.toString();
            allGoods.add(str);
        }
        return ReturnData.SUCCESS;
    }


    /**
     * 产品计算执行任务数量
     */
    public static BlockingQueue<GoodsTask> task=new ArrayBlockingQueue<GoodsTask>(3);
    static{

        task.add(new GoodsTask("task-1"));

        task.add(new GoodsTask("task-2"));

        task.add(new GoodsTask("task-3"));
    }

    /**
     * 增长处理的任务
     */
    public static Map<String, GoodsTask> tasking=new HashMap<String, GoodsTask>();

    /**
     * 正在执行的任务
     * @return
     */
    public ReturnData goodsTaskingStatus() {
        return new ReturnData(tasking);
    }

    /**
     * 任务终止
     * @return
     */
    public ReturnData stopGoodsTask(String taskName) {
        tasking.get(taskName).setStatus(false);
        return new ReturnData("请稍候");
    }

    /**
     * 提交任务　--执行所有类型
     * @param sql
     * @param sts
     * @param rws
     * @param pbath
     * @return
     */
    public ReturnData batchUpdateByGoodsId(String sql, int sts, int rws, int pbath) {
        return batchUpdateByGoodsId(sql, sts, rws, pbath, 3);
    }


    /**
     * 提交任务
     * @param sql
     * @param sts
     * @param rws
     * @param pbath
     * @param type  1.产品计算　2.产品静态化 3.计算+静态化
     * @return
     */
    public ReturnData batchUpdateByGoodsId(String sql, int sts, int rws, int pbath, Integer type) {
        GoodsTask gt=task.poll();
        if(gt==null) {
            return new ReturnData("任务已经排满");
        }
        gt.init();
        gt.setSql(sql);//查询产品列表sql
        gt.setRows(rws);
        gt.setPbatch(pbath);
        gt.setStart(sts);
        //正在运行的任务
        tasking.put(gt.getTaskName(), gt);
        try {

            //拿到锁
            //开始处理时间
            long startTime= DateUtils.nowInMillis()/1000;

            //开始下标
            int start=gt.getStart();
            //本次更新总数量
            int count=0;
            //mysql一次新获取数据量
            int rows=gt.getRows();
            //一次新处理提交的个数
            int pbatch=gt.getPbatch();

            //确定没有更新的数据
            //String searchsql="select goods_id id from hq_goods_search where update_time<"+startTime;
            //String searchsql="SELECT g.id FROM hq_goods g LEFT JOIN hq_goods_tag_result r ON r.goods_id = g.id WHERE ( deleted = 0 and data_type=3 and g.flag = 1 and (if(r.made_tag_ids,r.made_tag_ids,r.auto_tag_ids) like '1,%' OR if(r.made_tag_ids,r.made_tag_ids,r.auto_tag_ids) like '%,1' OR if(r.made_tag_ids,r.made_tag_ids,r.auto_tag_ids) like '%,1,%' OR if(r.made_tag_ids,r.made_tag_ids,r.auto_tag_ids) = '1') ) ORDER BY id desc ";

            Map<String,String> mall=new HashMap<String,String>(0);
            List<Map<String,Object>> mps=null;
            StringBuffer sb=new StringBuffer();
            int n=1;
            do{
                //每次开始时间
                long everyTimeStart=DateUtils.nowInMillis()/1000;
                long everyTime=0L;
                //任务终止
                if(!gt.isStatus()) {
                    //任务移除
                    tasking.remove(gt.getTaskName());
                    //任务完成
                    task.add(gt);
                    return new ReturnData(gt.isStatus()+"任务终止");
                }
                gt.setStart(start);
                gt.setFinished(count);
                String strsql=sql+" limit "+start+","+rows;
                mps=goodsOldMapper.select(strsql);
                for(Map<String,Object> m:mps) {
                    Long l= Long.parseLong(m.get("id")+"");
                    n++;
                    sb.append(l).append(",");

                    //一次性提交数据分组
                    if(n%pbatch==0) {
                        String str=sb.toString();
                        n=1;
                        //	mall.put(str, str);
                        //putGoodsQueue(str);
                        //updateGoods(str);
                        String ids = str.substring(0,str.length()-1);
                        calculateProducts(type, ids);

                        sb=new StringBuffer();
                    }
                }
                start=start+rows;
                count+=mps.size();
                //每次执行时间
                everyTime=DateUtils.nowInMillis()/1000-everyTimeStart;
                gt.setExectime(everyTime);
            }while(mps!=null&&mps.size()>0);

            //最后的id加入队列
            if(n>1) {
                String str=sb.toString();
                calculateProducts(type, str);
            }

            //任务移除
            tasking.remove(gt.getTaskName());

            //任务完成
            task.add(gt);
            return ReturnData.SUCCESS;
        }  catch (Exception e) {
            logger.error("CronService.goodsBatchByUpdate:"+ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }


    private void calculateProducts(Integer type, String ids){
        if(1 == type){
            //计算
            internalGoodsCalculateService.goodsCalculate(ids, 1);

            //清除缓存
            cacheService.cleanProductsBatch(ids);
        }else if(2 == type){
            //静态化
            staticClientService.batchGoodsStatic(ids);
        }else if (3==type){
            //计算
            internalGoodsCalculateService.goodsCalculate(ids, 1);

            //清除缓存
            cacheService.cleanProductsBatch(ids);

            //静态化
            staticClientService.batchGoodsStatic(ids);
        }
    }


    /**
     * 调度锁
     */
    private static Lock execLock = new ReentrantLock();

    /**
     * 调度开关
     */
    private static  boolean atteper=true;
    /**
     * 请求接口 调度
     * @return
     */
    public ReturnData startGoodsAttemper() {
        if(!atteper) {
            return new ReturnData("调度开关关闭",1);
        }
        //	 if(execLock.tryLock() ) {
        //拿到锁
        if(execLock.tryLock()) {
            try {
                while(atteper) {
                    //请求计算产品接口
                    String ids=null;
                    do{
                        if(allGoods!=null&&allGoods.size()>0) {
                            ids = allGoods.poll();
                            if(ids!=null) {
                                //ReturnListData d=goodsService.goodsCalculate(null,ids, 1);
                                updateGoods(ids);
                            }
                        }
                    }while(ids!=null);
                }

            }finally{
                execLock.unlock();
                System.out.println("gx");
            }
            return new ReturnData("调度开关关闭",1);
        }else {
            return new ReturnData("有线程调度执行中",3);
        }


    }

    /**
     * 产品调度开关
     * @param flag
     * @return
     */
    public ReturnData goodsOffOrOnAttemper(Boolean flag) {
        atteper=flag;
        return ReturnData.SUCCESS;
    }
    /**
     * 更新产品
     * @param ids 产品id
     * @throws Exception
     */
    public  void updateGoods(String ids)   {
        //微信回调地址
        Date s=new Date();
        //域名
        String domain="http://api.cache.internal.bevol.cn";
        String url = domain+"/goods/info";
        //请求url
        Map<String,String> postData=new HashMap<String,String>();
        postData.put("update","1");
        postData.put("ids", ids);
        try {
            String resultJson = HttpUtils.post(url, postData);
            Date e=new Date();
            if(resultJson.indexOf("\"ret\":0")!=-1) {
                logger.info("ids "+ids+"  time:"+(e.getTime()-s.getTime())+" ms");
            } else {
                logger.error("ids "+ids+" time:"+(e.getTime()-s.getTime())+" ms  after resultJson:"+resultJson);
                logger.warn("ids1:"+ids);
            }
        } catch (Exception e) {
            logger.error("ids "+ids+"  before"+ExceptionUtils.getStackTrace(e));
            logger.warn("ids2:"+ids);
        }

    }

    public ReturnData shellExc(String sellString) {
        try {
            String[] cmdA = { "/bin/sh", "-c", sellString };
            Process process = Runtime.getRuntime().exec(cmdA);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                return new ReturnData("call shell failed. error code is :" + exitValue,-4);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return ReturnData.ERROR;
        }
        return ReturnData.SUCCESS;

    }
}
