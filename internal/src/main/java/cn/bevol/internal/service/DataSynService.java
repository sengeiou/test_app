package cn.bevol.internal.service;

import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.entity.GoodsTask;
import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.dao.mapper.*;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.Log.LogException;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.xidea.el.Expression;
import org.xidea.el.impl.ExpressionImpl;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
public class DataSynService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(DataSynService.class);

    @Autowired
    MongoTemplate mongoTemplate;


    @Autowired
    DataSynOldMapper dataSynOldMapper;

    @Autowired
    AliyunService apliyunService;


    @Autowired
    GoodsOldMapper goodsOldMapper;

    @Autowired
    GoodsService goodsService;
    @Autowired
    private InitOldMapper initOldMapper;
    @Resource
    private EntityService entityService;

    @Autowired
    VerificationCodeOldMapper verificationCodeOldMapper;



    /**
     * mongodb的实施数据提交到opensearch
     * 添加最爱单品信息进入mysql
     *
     * @return
     */
    public ReturnData mongodbToOpeanSearch(String tname) {
        try {
            long o=0;
            //实体同步时间
            Map<String,String> op= ConfUtils.getJSONMap("mongodb_to_opeansarch_"+tname);
            String indexName=op.get("index_Name");
            String appTable=op.get("app_table");
            String monogoTab=op.get("monogo_tab");

            //主键
            String keys[]=op.get("keys").split(":");

            Date startTime=new Date();
            int total=0;
            List<HashMap> map=null;
            do{
                Integer syntime=null;
                //last_time为 0时全量同步
                Criteria cr= Criteria.where("id").gt(o);
                if(!StringUtils.isBlank(op.get("last_time"))) {
                    syntime= Integer.parseInt(op.get("last_time"));
                    if(syntime>0)
                        cr.and("vistTime").gt(syntime);
                }
                Integer limit = Integer.parseInt(op.get("mongo_limit"));
                Integer mysqlInsertCount= Integer.parseInt(op.get("opensearch_batch_num"));

                String replaceFields=op.get("replace_fields");
                String fields[]=replaceFields.split(",");
                StringBuffer mysqlFields=new StringBuffer();
                Map<String,String> fkvs=new HashMap<String,String>();
                //表达式引擎用于解析map等数据类型
                Map<String,Expression> jselExp=new HashMap<String,Expression>();

                for(int i=0;i<fields.length;i++) {
                    String f[]=fields[i].split(":");
                    mysqlFields.append(f[0].trim()).append(",");
                    fkvs.put(f[0].trim(), f[1].trim());
                    jselExp.put(f[0].trim(), new ExpressionImpl(f[0].trim()));
                }

                map= mongoTemplate.find(new Query(cr).with(new Sort(Direction.ASC, keys[0])).limit(limit), HashMap.class, monogoTab);
                List<Map<String,Object>> insertbatch=new ArrayList<Map<String,Object>>();
                for(int i=0;map!=null&&i<map.size();i++) {
                    Map e=map.get(i);
                    //100个插入一次
                    Map<String,Object> m=new HashMap<String,Object>();
                    for(Map.Entry<String, String> entry:fkvs.entrySet()){
                        Expression exp=jselExp.get(entry.getKey());
                        Object result1=null;
                        if(entry.getKey().lastIndexOf("_str")!=-1){
                            String k=entry.getKey().substring(0, entry.getKey().lastIndexOf("_str"));
                            if(e.containsKey(k)&&e.get(k)!=null) {
                                result1= new JSONSerializer().deepSerialize(e.get(k));
                            }
                        }else {
                            try {
                                //没有
                                result1 = exp.evaluate(e);// 555
                            } catch(Exception tp) {

                            }

                        }
                        if(result1!=null) {
                            if(result1 instanceof List) {
                                //数组转换为 1,3,2
                                //List r=(List) result1;
                                //result1=StringUtils.join(r, ",");
                            }

                            if(result1 instanceof String) {
                                if(StringUtils.isBlank(result1.toString())) result1=null;
                            }
                            if(result1!=null)
                                m.put(entry.getValue(), result1);
                        }
                    }

                    insertbatch.add(m);
                    //批量插入
                    if(insertbatch.size()==mysqlInsertCount) {
                        apliyunService.updateOpearch(indexName, appTable, insertbatch);
                        insertbatch=new ArrayList<Map<String,Object>>();
                    }
                    o= Long.parseLong(e.get(keys[0])+"");
                    total++;
                }
                //插入剩余部分
                if(insertbatch.size()>0) {
                    apliyunService.updateOpearch(indexName, appTable, insertbatch);
                    Date endTime=new Date();
                    total=total+insertbatch.size();
                    long lt=endTime.getTime()/1000;
                    String astr= "{'last_time':'"+(lt)+"','syn_num':'"+total+"','mongo_limit':'"+op.get("mongo_limit")+"','opensearch_batch_num':'"+op.get("opensearch_batch_num")+"'}";
                    ConfUtils.setResourceString("entity_statistics_to_opeansarch_"+tname, astr);
                    String log="{'start_time':'"+ DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss")+"','end_time':'"+DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss")+"'}";
                    logger.info("DataSynService.entityToOpeanSearch"+log);
                    Update updt=new Update();
                    updt.set("last_time", lt);
                    mongoTemplate.updateMulti(new Query(Criteria.where("hidden").is(0)), updt, monogoTab);
                }
            }while(map!=null&&map.size()>0);
            return new ReturnData(0);
        } catch (Exception e) {
            Map map=new HashMap();
            map.put("method", "DataSynService.mongodbToOpeanSearch");
            map.put("tname", tname);
            new LogException(e,map);
            e.printStackTrace();
            return ReturnData.ERROR;
        }
    }



    /**
     * 单个 数据提交opensearch
     * 添加最爱单品信息进入mysql
     *
     * @return
     */
    public ReturnData toOpeanSearch(String indexName, String appTable, String json) {
        try {
            List<Map<String, Object>> insertbatch = new JSONDeserializer<List<Map<String, Object>>>().deserialize(json,
                    ArrayList.class);
            // 批量插入
            apliyunService.updateOpearch(indexName, appTable, insertbatch);
            insertbatch = new ArrayList<Map<String, Object>>();
            return new ReturnData(0);
        } catch (Exception e) {
            Map map = new HashMap();
            map.put("method", "DataSynService.toOpeanSearch");
            map.put("indexName", indexName);
            map.put("appTable", appTable);
            map.put("json", json);
            new LogException(e, map);
            return new ReturnData(-1);
        }
    }



    /**
     * 搜索数据同步到mysql
     * 添加最爱单品信息进入mysql
     *
     * @return
     */
    public ReturnData entityStatisticsToMysql(String tname) {
        Integer isall=0;
        try {
            long o=0;
            //实体同步时间
            Map<String,String> op=ConfUtils.getJSONMap("entity_statistics_to_mysql_"+tname);
            String mysqlTab=op.get("mysql_tab");
            String mysqlTmpTab=op.get("mysql_tmp_tab");
            String monogoTab=op.get("mongo_tab");
            isall = Integer.parseInt(op.get("is_all"));
            Date startTime=new Date();
            int total=0;
            List<EntityBase> map=null;
            //清空一次就可以
            boolean flag=true;
            String vistTimeField="vistTime_"+tname;
            do{
                Integer syntime=null;
                //last_time为 0时全量同步
                Criteria cr= Criteria.where("id").gt(o);

                if(isall!=null&&isall.equals(0)) {

                    Map<String,String> stm=ConfUtils.getJSONMap("entity_statistics_to_mysql_lasttime_"+tname);
                    if(stm!=null&&stm.get("last_time")!=null) {
                        String v=stm.get("last_time");
                        syntime= Integer.valueOf(v);
                        if(syntime>0)
                            cr.and(vistTimeField).gt(syntime);
                    }
                }
                Integer limit = Integer.parseInt(op.get("mongo_limit"));
                Integer mysqlInsertCount= Integer.parseInt(op.get("mysql_batch_num"));

                map= mongoTemplate.find(new Query(cr).with(new Sort(Direction.ASC, "id")).limit(limit), EntityBase.class, monogoTab);
                List<EntityBase> insertbatch=new ArrayList<EntityBase>();
                //清空临时表
                if(map!=null&&map.size()>0&&flag) {
                    flag=false;
                    dataSynOldMapper.deleteBySql("delete from "+mysqlTmpTab);
                }
                for(int i=0;map!=null&&i<map.size();i++) {
                    EntityBase e=map.get(i);
                    //100个插入一次
                    insertbatch.add(e);
                    //批量插入
                    if(insertbatch.size()==mysqlInsertCount) {
                        insertSatistics(mysqlTmpTab,insertbatch);
                        //插入
                        // apliyunService.updateOpearch(indexName, appTable, insertbatch);

                        insertbatch=new ArrayList<EntityBase>();
                    }
                    o=e.getId();
                    total++;
                }
                //插入剩余部分
                if(insertbatch.size()>0) {
                    //插入
                    //	apliyunService.updateOpearch(indexName, appTable, insertbatch);
                    //mongo到mysql映射的语句
                    insertSatistics(mysqlTmpTab,insertbatch);
                }
            }while(map!=null&&map.size()>0);

            if(total>0) {
                //更新
                String sql="update "+mysqlTab+" m,"+mysqlTmpTab+" t set m.hit_num=t.hit_num,m.hit_num=t.hit_num,m.collection_num=t.collection_num,m.notlike_num=t.notlike_num,m.comment_num=t.comment_num,m.like_num=t.like_num,m.radio=t.radio,m.grade=t.grade,m.comment_content_num=t.comment_content_num,m.comment_sum_score=t.comment_sum_score,m.all_comment_num=t.all_comment_num,m.c_sort=t.c_sort,m.comment_avg_score=t.comment_avg_score where m.goods_id=t.goods_id";
                dataSynOldMapper.updateBySql(sql);
                Date endTime=new Date();
                long lt=endTime.getTime()/1000;
                String astr= "{'last_time':'"+lt+"'}";
                ConfUtils.setResourceString("entity_statistics_to_mysql_lasttime_"+tname, astr);
                String log="{'tname':'"+tname+"','start_time':'"+DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss")+"','end_time':'"+DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss")+"','syn_num':'"+total+"'}";
                logger.info("DataSynService.entityStatisticsToMysql"+log);
                //同步访问时间
                Update updt=new Update();
                updt.set(vistTimeField, lt);
                mongoTemplate.updateMulti(new Query(Criteria.where("hidden").is(0)), updt, monogoTab);
            }
            return new ReturnData(0);
        } catch (Exception e) {
            Map map = new HashMap();
            map.put("method", "DataSynService.entityStatisticsToMysql");
            map.put("tname", tname);
            new LogException(e, map);
            return ReturnData.ERROR;
        }
    }


    /**
     * 插入统计表
     * @param tab
     * @param ebs
     */
    public void insertSatistics(String tab, List<EntityBase> ebs) {
        StringBuffer sb=new StringBuffer();
        sb.append("insert into "+tab+"(goods_id,hit_num,collection_num,notlike_num,comment_num,like_num,radio,grade,comment_content_num,comment_sum_score,all_comment_num,c_sort,comment_avg_score) values");
        for(EntityBase e:ebs) {
            sb.append("("+e.getId()+","+e.getHitNum()+","+e.getCollectionNum()+","+e.getNotLikeNum()+","+e.getCommentNum()+","+e.getLikeNum()+","+e.getRadio()+","+e.getGrade()+","+e.getCommentContentNum()+","+e.getCommentSumScore()+","+e.getAllCommentNum()+","+e.getCsort()+","+e.getCommentAvgScore()+")").append(",");
        }
        String sql=sb.substring(0,sb.length()-1);
        dataSynOldMapper.insertBySql(sql);
    }


    /**
     * mysql同步到mongo
     *	@param configName 配置名称
     * @return
     */
    public ReturnData mysqlToMongo(String configName) {

        try {
            if(!StringUtils.isBlank(configName)) {
                Map<String,String> op=ConfUtils.getJSONMap("mysql_to_mongo_"+configName);
                //mysql表
                String mysqlTab=op.get("mysql_tab");
                //monogodb表
                String mongoTab=op.get("mongo_tab");

                //对应主键
                String keys=op.get("keys");

                String ids[]=keys.split(":");
                String mysqlId=ids[0];
                String mongoId=ids[1];
                //同步字段
                String replaceFields=op.get("replace_fields");

                String fields[]=replaceFields.split(",");
                StringBuffer mysqlFields=new StringBuffer();
                Map<String,String> fkvs=new HashMap<String,String>();
                for(int i=0;i<fields.length;i++) {
                    String f[]=fields[i].split(":");
                    mysqlFields.append(f[0]).append(",");
                    fkvs.put(f[0], f[1]);
                }
                //没有匹配时 是否插入
                Integer insert= Integer.parseInt(op.get("insert"));
                Integer limit = Integer.parseInt(op.get("mysql_limit"));
                List<Map<String,Object>> objs=null;
                //开始时间
                Date startTime=new Date();
                int uCount=0;
                int iCount=0;
                int start=0;
                int n=1;
                do{
                    String sql="select "+mysqlId+","+mysqlFields.substring(0, mysqlFields.length()-1)+" from "+mysqlTab+" limit "+start+","+limit;
                    objs=dataSynOldMapper.selectBySql(sql);
                    start=limit*n++;
                    if(objs!=null&&objs.size()>0) {
                        //插入mongo
                        for(int i=0;i<objs.size();i++) {
                            Map<String,Object> o=objs.get(i);
                            Update updt=new Update();
                            //用于插入
                            Map<String,Object> imap=new HashMap<String,Object>();
                            for(Map.Entry<String, String> entry:fkvs.entrySet()){
                                imap.put(entry.getValue(), o.get(entry.getKey()));
                                updt.set(entry.getValue(), o.get(entry.getKey()));
                            }
                            FindAndModifyOptions fmo=new FindAndModifyOptions();
                            if(insert!=null&&insert.equals(1)) {
                                fmo.upsert(true);
                            }
                            Map map = mongoTemplate.findAndModify(new Query(Criteria.where(mongoId).is(o.get(mysqlId))), updt, fmo, HashMap.class, mongoTab);
                            //System.out.println(map);
                            //WriteResult wr=mongoTemplate.updateFirst(new Query(Criteria.where(mongoId).is(o.get(mysqlId))), updt, mongoTab);

                            if(map==null) {
                                //更新不成功 就插入
                                iCount++;
                            } else {
                                uCount++;
                            }
                        }
                    }

                    //日志
                }while(objs!=null&&objs.size()>0);

                Date endTime=new Date();
                long lt=endTime.getTime()/1000;
                String astr= "{'tname':'"+configName+"',last_time':'"+(lt)+"','start_time':'"+DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss")+"','end_time':'"+DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss")+"','insert_num':'"+iCount+"','update_num':'"+uCount+"'}";
                logger.info("DataSynService.entityStatisticsToMysql"+astr);
            }

        }catch(Exception e) {
            Map map = new HashMap();
            map.put("method", "DataSynService.mysqlToMongo");
            map.put("configName", configName);
            new LogException(e, map);
            return ReturnData.ERROR;
        }
        return  ReturnData.SUCCESS;
    }


    /**
     * mongo同步到mysql
     * 添加最爱单品信息进入mysql
     *
     * @return
     */
    public ReturnData mongoToMysql(String tname) {
        Date startTime=new Date();
        try {
            long o=0;
            //实体同步时间
            Map<String,String> op=ConfUtils.getJSONMap("mongo_to_mysql_"+tname);
            String mysqlTab=op.get("mysql_tab");
            String monogoTab=op.get("mongo_tab");
            int total=0;
            int updateTotal=0;
            int insertTotal=0;

            List<HashMap> map=null;

            //对应主键
            String keys=op.get("keys");

            String ids[]=keys.split(":");
            String mysqlId=ids[0];
            String mongoId=ids[1];
            //同步字段
            String replaceFields=op.get("replace_fields");
            String mysqlBatchNum=op.get("mysql_batch_num");
            int mysqlBatchNumInt=300;
            if(!StringUtils.isBlank(mysqlBatchNum)) {
                mysqlBatchNumInt= Integer.parseInt(mysqlBatchNum);
            }

            String fields[]=replaceFields.split(",");
            StringBuffer updateSqls=new StringBuffer();
            StringBuffer insertSqls=new StringBuffer();
            do{
                //last_time为 0时全量同步
                Criteria cr= Criteria.where(mongoId).gt(o);
                Integer limit = Integer.parseInt(op.get("mongo_limit"));

                map= mongoTemplate.find(new Query(cr).with(new Sort(Direction.ASC, "id")).limit(limit), HashMap.class, monogoTab);
                //清空临时表
                for(int i=0;map!=null&&i<map.size();i++) {
                    HashMap e=map.get(i);
                    StringBuffer mysqlUpdateFields=new StringBuffer();
                    StringBuffer mysqlSelectFields=new StringBuffer();
                    StringBuffer mysqlInsertKeys=new StringBuffer();
                    StringBuffer mysqlInsertFeilds=new StringBuffer();

                    for(int j=0;j<fields.length;j++) {
                        String f[]=fields[j].split(":");
                        mysqlUpdateFields.append(",").append(f[0]).append("=");

                        mysqlSelectFields.append(",").append(f[0]);
                        mysqlInsertKeys.append(",").append(f[0]);
                        mysqlInsertFeilds.append(",");

                        if(e.get(f[1])==null|| NumberUtils.isNumber(e.get(f[1])+"")) {
                            mysqlInsertFeilds.append(e.get(f[1]));
                            mysqlUpdateFields.append(e.get(f[1]));
                        }else {
                            mysqlInsertFeilds.append("'"+e.get(f[1])+"'");
                            mysqlUpdateFields.append("'"+e.get(f[1])+"'");
                        }
                    }
                    o= Math.round(Float.parseFloat(e.get(mongoId)+""));

                    String sql="select "+mysqlSelectFields.substring(1)+" from "+mysqlTab+" where "+mysqlId+"="+o;
                    List<Map<String,Object>> m=dataSynOldMapper.selectBySql(sql);
                    if(m==null||m.size()==0) {
                        String insertSql=o+""+mysqlInsertFeilds;
                        String keyfs=mysqlId+""+mysqlInsertKeys;
                        sql="insert into "+mysqlTab+"("+keyfs+")"+" value ("+insertSql.replace("'null'", "null")+") ";
                        //dataSynOldMapper.insertBySql(sql);
                        insertSqls.append(sql).append(";");
                        insertTotal++;
                    }  else {
                        sql="update "+mysqlTab+" set "+mysqlUpdateFields.substring(1)+" where "+mysqlId+"="+o;
                        //dataSynOldMapper.updateBySql(sql);
                        updateSqls.append(sql).append(";");
                        updateTotal++;
                    }
                    if(insertTotal%mysqlBatchNumInt==0&&insertSqls.length()>0) {
                        sql=insertSqls.toString();
                        dataSynOldMapper.insertBySql(sql);
                        insertSqls=new StringBuffer();
                    }
                    if(updateTotal%mysqlBatchNumInt==0&&updateSqls.length()>0) {
                        sql=updateSqls.toString().replace("'null'", "null");
                        dataSynOldMapper.updateBySql(sql);
                        updateSqls=new StringBuffer();
                    }
                    total++;
                }
            }while(map!=null&&map.size()>0);

            if(insertSqls.length()>0) {
                dataSynOldMapper.insertBySql(insertSqls.toString());
            }

            if(updateSqls.length()>0) {
                dataSynOldMapper.updateBySql(updateSqls.toString());
            }

            Date endTime=new Date();
            long lt=endTime.getTime()/1000;
            String astr= "{'tname':'"+tname+"',last_time':'"+(lt)+"','start_time':'"+DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss")+"','end_time':'"+DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss")+"','update_num':'"+updateTotal+"','insert_num':'"+insertTotal+"'}";
            logger.info("DataSynService.entityStatisticsToMysql"+astr);

            return new ReturnData(0);
        } catch (Exception e) {
            Map map = new HashMap();
            map.put("method", "DataSynService.mongoToMysql");
            map.put("tname", tname);
            new LogException(e, map);
            return ReturnData.ERROR;
        }
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
