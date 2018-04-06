package cn.bevol.data.synchronize.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

import com.io97.utils.http.HttpUtils;

import cn.bevol.conf.client.ConfUtils;
import cn.bevol.entity.service.AliyunService;
import cn.bevol.entity.service.BaseService;
import cn.bevol.entity.service.EntityService;
import cn.bevol.entity.service.GoodsService;
import cn.bevol.model.GlobalConfig;
import cn.bevol.model.GoodsTask;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.user.VerificationCodeEntity;
import cn.bevol.mybatis.dao.DataSynMapper;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.dao.InitMapper;
import cn.bevol.mybatis.dao.UserInfoMapper;
import cn.bevol.mybatis.dao.VerificationCodeMapper;
import cn.bevol.mybatis.model.Composition;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Service
public class DataSynService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(DataSynService.class);

    @Autowired
    MongoTemplate mongoTemplate; 


    @Autowired
    DataSynMapper dataSynMapper;
    
    @Autowired
    AliyunService apliyunService;
    
    
	 @Autowired
	 GoodsMapper goodsMapper;
	 
	 @Autowired
	 GoodsService goodsService;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    private InitMapper initMapper;
    @Resource
    private EntityService entityService;
    
    @Autowired
    VerificationCodeMapper verificationCodeMapper;



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
            Map<String,String> op=ConfUtils.getJSONMap("mongodb_to_opeansarch_"+tname);
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
            	Criteria cr=Criteria.where("id").gt(o);
            	if(!StringUtils.isBlank(op.get("last_time"))) {
            		 syntime=Integer.parseInt(op.get("last_time"));
            		 if(syntime>0)
            			 cr.and("vistTime").gt(syntime);
            	}
                Integer limit =Integer.parseInt(op.get("mongo_limit"));
                Integer mysqlInsertCount=Integer.parseInt(op.get("opensearch_batch_num"));
                
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
                    o=Long.parseLong(e.get(keys[0])+"");
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
                   String log="{'start_time':'"+DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss")+"','end_time':'"+DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss")+"'}";
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
    		new cn.bevol.log.LogException(e,map);
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
			new cn.bevol.log.LogException(e, map);
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
            isall =Integer.parseInt(op.get("is_all"));
             Date startTime=new Date();
            int total=0;
            List<EntityBase> map=null;
            //清空一次就可以
            boolean flag=true;
        	String vistTimeField="vistTime_"+tname;
            do{
            	Integer syntime=null;
            	//last_time为 0时全量同步
            	Criteria cr=Criteria.where("id").gt(o);
            	
            	if(isall!=null&&isall.equals(0)) {
            		
                    Map<String,String> stm=ConfUtils.getJSONMap("entity_statistics_to_mysql_lasttime_"+tname);
                	if(stm!=null&&stm.get("last_time")!=null) {
                		String v=stm.get("last_time");
                		 syntime=Integer.valueOf(v);
                		if(syntime>0)
                			 cr.and(vistTimeField).gt(syntime);
                	}
            	}
                Integer limit =Integer.parseInt(op.get("mongo_limit"));
                Integer mysqlInsertCount=Integer.parseInt(op.get("mysql_batch_num"));

            	map= mongoTemplate.find(new Query(cr).with(new Sort(Direction.ASC, "id")).limit(limit), EntityBase.class, monogoTab);
                List<EntityBase> insertbatch=new ArrayList<EntityBase>();
                //清空临时表
                if(map!=null&&map.size()>0&&flag) {
                	flag=false;
                	dataSynMapper.deleteBySql("delete from "+mysqlTmpTab);
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
            	dataSynMapper.updateBySql(sql);
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
			new cn.bevol.log.LogException(e, map);
            return ReturnData.ERROR;
        }
    }
    
    
    /**
     * 插入统计表
     * @param tab
     * @param ebs
     */
    public void insertSatistics(String tab,List<EntityBase> ebs) {
    	StringBuffer sb=new StringBuffer();
    	sb.append("insert into "+tab+"(goods_id,hit_num,collection_num,notlike_num,comment_num,like_num,radio,grade,comment_content_num,comment_sum_score,all_comment_num,c_sort,comment_avg_score) values");
    	for(EntityBase e:ebs) {
        	sb.append("("+e.getId()+","+e.getHitNum()+","+e.getCollectionNum()+","+e.getNotLikeNum()+","+e.getCommentNum()+","+e.getLikeNum()+","+e.getRadio()+","+e.getGrade()+","+e.getCommentContentNum()+","+e.getCommentSumScore()+","+e.getAllCommentNum()+","+e.getCsort()+","+e.getCommentAvgScore()+")").append(",");
    	}
    	String sql=sb.substring(0,sb.length()-1);
    	dataSynMapper.insertBySql(sql);
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
                Integer insert=Integer.parseInt(op.get("insert"));
                Integer limit =Integer.parseInt(op.get("mysql_limit"));
                List<Map<String,Object>> objs=null;
                //开始时间
                Date startTime=new Date();
                int uCount=0;
                int iCount=0;
                int start=0;
            	int n=1;
                do{
                	String sql="select "+mysqlId+","+mysqlFields.substring(0, mysqlFields.length()-1)+" from "+mysqlTab+" limit "+start+","+limit;
                	objs=dataSynMapper.selectBySql(sql);
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
			new cn.bevol.log.LogException(e, map);
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
           	mysqlBatchNumInt=Integer.parseInt(mysqlBatchNum);
           }
            
            String fields[]=replaceFields.split(",");
            StringBuffer updateSqls=new StringBuffer();
            StringBuffer insertSqls=new StringBuffer();
            do{
            	//last_time为 0时全量同步
            	Criteria cr=Criteria.where(mongoId).gt(o);
                Integer limit =Integer.parseInt(op.get("mongo_limit"));

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

                    	if(e.get(f[1])==null||NumberUtils.isNumber(e.get(f[1])+"")) {
                    		mysqlInsertFeilds.append(e.get(f[1]));
                    		mysqlUpdateFields.append(e.get(f[1]));
                    	}else {
                    		mysqlInsertFeilds.append("'"+e.get(f[1])+"'");
                    		mysqlUpdateFields.append("'"+e.get(f[1])+"'");
                    	}
                     }
                    o=Math.round(Float.parseFloat(e.get(mongoId)+""));
                    
                    String sql="select "+mysqlSelectFields.substring(1)+" from "+mysqlTab+" where "+mysqlId+"="+o;
                    List<Map<String,Object>> m=dataSynMapper.selectBySql(sql);
                    if(m==null||m.size()==0) {
                    	String insertSql=o+""+mysqlInsertFeilds;
                    	String keyfs=mysqlId+""+mysqlInsertKeys;
                        sql="insert into "+mysqlTab+"("+keyfs+")"+" value ("+insertSql.replace("'null'", "null")+") ";
                    	//dataSynMapper.insertBySql(sql);
                    	insertSqls.append(sql).append(";");
                    	insertTotal++;
                    }  else {
                        sql="update "+mysqlTab+" set "+mysqlUpdateFields.substring(1)+" where "+mysqlId+"="+o;
                    	//dataSynMapper.updateBySql(sql);
                        updateSqls.append(sql).append(";");
                    	updateTotal++;
                    }
                    if(insertTotal%mysqlBatchNumInt==0&&insertSqls.length()>0) {
                    	sql=insertSqls.toString();
                    	dataSynMapper.insertBySql(sql);
                    	insertSqls=new StringBuffer();
                    }                   
                    if(updateTotal%mysqlBatchNumInt==0&&updateSqls.length()>0) {
                    	sql=updateSqls.toString().replace("'null'", "null");
                    	dataSynMapper.updateBySql(sql);
                    	updateSqls=new StringBuffer();
                    }
                    total++;
                }
            }while(map!=null&&map.size()>0);
            
            if(insertSqls.length()>0) {
            	dataSynMapper.insertBySql(insertSqls.toString());
            }
            
            if(updateSqls.length()>0) {
            	dataSynMapper.updateBySql(updateSqls.toString());
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
			new cn.bevol.log.LogException(e, map);
            return ReturnData.ERROR;
        }
    }
    
    //单任务锁
    private static  Lock lock = new ReentrantLock();  
    
    //处理数据的队列
    private static ArrayBlockingQueue<String>   allGoods=new ArrayBlockingQueue<String>(3000000);


    
    /**
     * 加入产品id到队列
     * @param ids
     */
    public ReturnData addGoodsQueue(String ids) {
		if(StringUtils.isBlank(ids)) {
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
    public static Map<String,GoodsTask> tasking=new HashMap<String,GoodsTask>();

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
	 *提交任务 
     */
	public ReturnData batchUpdateByGoodsId(String sql, int sts, int rws, int pbath) {
		GoodsTask gt = task.poll();
		if (gt == null) {
			return new ReturnData("任务已经排满");
		}
		gt.init();
		gt.setSql(sql);// 查询产品列表sql
		gt.setRows(rws);
		gt.setPbatch(pbath);
		gt.setStart(sts);
		// 正在运行的任务
		tasking.put(gt.getTaskName(), gt);
		try {

			// 拿到锁
			// 开始处理时间
			long startTime = new Date().getTime() / 1000;

			// 开始下标
			int start = gt.getStart();
			// 本次更新总数量
			int count = 0;
			// mysql一次新获取数据量
			int rows = gt.getRows();
			// 一次新处理提交的个数
			int pbatch = gt.getPbatch();

			// 确定没有更新的数据
			// String searchsql="select goods_id id from hq_goods_search where
			// update_time<"+startTime;
			// String searchsql="SELECT g.id FROM hq_goods g LEFT JOIN
			// hq_goods_tag_result r ON r.goods_id = g.id WHERE ( deleted = 0
			// and data_type=3 and g.flag = 1 and
			// (if(r.made_tag_ids,r.made_tag_ids,r.auto_tag_ids) like '1,%' OR
			// if(r.made_tag_ids,r.made_tag_ids,r.auto_tag_ids) like '%,1' OR
			// if(r.made_tag_ids,r.made_tag_ids,r.auto_tag_ids) like '%,1,%' OR
			// if(r.made_tag_ids,r.made_tag_ids,r.auto_tag_ids) = '1') ) ORDER
			// BY id desc ";

			Map<String, String> mall = new HashMap<String, String>(0);
			List<Map<String, Object>> mps = null;
			StringBuffer sb = new StringBuffer();
			int n = 1;
			do {
				// 每次开始时间
				long everyTimeStart = new Date().getTime() / 1000;
				long everyTime = 0L;
				// 任务终止
				if (!gt.isStatus()) {
					// 任务移除
					tasking.remove(gt.getTaskName());
					// 任务完成
					task.add(gt);
					return new ReturnData(gt.isStatus() + "任务终止");
				}
				gt.setStart(start);
				gt.setFinished(count);
				String strsql = sql + " limit " + start + "," + rows;
				mps = goodsMapper.select(strsql);
				String id = "";
				for (Map<String, Object> m : mps) {
					Long l = Long.parseLong(m.get("id") + "");
					id = l + "";
					n++;
					sb.append(l).append(",");

					// 一次性提交数据分组
					if (n % pbatch == 0) {
						String str = sb.toString();
						n = 1;
						// mall.put(str, str);
						// putGoodsQueue(str);
						// updateGoods(str);

						ReturnListData d = goodsService.goodsCalculate(null, str.substring(0, str.length() - 1), 1);

						sb = new StringBuffer();
					}
				}
				start = start + rows;
				count += mps.size();
				// 每次执行时间
				everyTime = new Date().getTime() / 1000 - everyTimeStart;
				gt.setExectime(everyTime);
			} while (mps != null && mps.size() > 0);

			// 最后的id加入队列
			if (n > 1) {
				String str = sb.toString();
				ReturnListData d = goodsService.goodsCalculate(null, str, 1);
			}

			// 任务移除
			tasking.remove(gt.getTaskName());

			// 任务完成
			task.add(gt);
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "DataSynService.batchUpdateByGoodsId");
			map.put("sql", sql);
			map.put("sts", sts);
			map.put("rws", rws);
			map.put("pbath", pbath);
			new cn.bevol.log.LogException(e, map);
		}
		return ReturnData.ERROR;
	}
    
    
    /**
     * 调度锁
     */
    private static  Lock execLock = new ReentrantLock();
    
    /**
     * 调度开关
     */
    private static  boolean atteper=true;
    /**
     * 请求接口 调度
     * @return
     */
	public ReturnData startGoodsAttemper() {
		if (!atteper) {
			return new ReturnData("调度开关关闭", 1);
		}
		// if(execLock.tryLock() ) {
		// 拿到锁
		if (execLock.tryLock()) {
			try {
				while (atteper) {
					// 请求计算产品接口
					String ids = null;
					do {
						if (allGoods != null && allGoods.size() > 0) {
							ids = allGoods.poll();
							if (ids != null) {
								// ReturnListData
								// d=goodsService.goodsCalculate(null,ids, 1);
								updateGoods(ids);
							}
						}
					} while (ids != null);
				}

			} finally {
				execLock.unlock();
				System.out.println("gx");
			}
			return new ReturnData("调度开关关闭", 1);
		} else {
			return new ReturnData("有线程调度执行中", 3);
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
	public void updateGoods(String ids) {
		// 微信回调地址
		Date s = new Date();
		// 域名
		String domain = "http://api.cache.internal.bevol.cn";
		String url = domain + "/goods/info";
		// 请求url
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("update", "1");
		postData.put("ids", ids);
		try {
			String resultJson = HttpUtils.post(url, postData);
			Date e = new Date();
			if (resultJson.indexOf("\"ret\":0") != -1) {
				logger.info("ids " + ids + "  time:" + (e.getTime() - s.getTime()) + " ms");
			} else {
				logger.error(
						"ids " + ids + " time:" + (e.getTime() - s.getTime()) + " ms  after resultJson:" + resultJson);
				logger.warn("ids1:" + ids);
			}
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "DataSynService.updateGoods");
			map.put("ids", ids);
			new cn.bevol.log.LogException(e, map);
		}

	}

	
    
    /**
     * 更新当天短信发送的数量
     */
    @SuppressWarnings("rawtypes")
	public ReturnData updateCurSMSNum() {
    	boolean flag=false;
		//用户每日注册人数(手机注册)
 		//读配置得到每日限制人数
		Integer maxRegisterNum=ConfUtils.getResourceNum("register_day_num");
		Map<String,Object> reg=new HashMap<String,Object>();
		//查询当天的短信发送的数量
		//Long num=verificationCodeMapper.getVerificationNum();
		long current = System.currentTimeMillis();
        long zero = (current/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset())/1000;
		Long num=mongoTemplate.count(new Query(Criteria.where("phone").exists(true).and("createStamp").gte(zero)), VerificationCodeEntity.class,"verification_code");
		
		if(num.longValue()>=maxRegisterNum){
			flag=true;
		}
		if(maxRegisterNum==null) maxRegisterNum=0;
		if(maxRegisterNum==1) {
			//打开注册
			flag=false;
		} else if(maxRegisterNum==0) {
			//关闭注册
			flag=true;
		}
		reg.put("allowSMS", flag);
		reg.put("curSMSNum", num);
		
		this.putGlobalConfig(reg);
		return ReturnData.SUCCESS;
    }

    /**
     * 每天数据更新
     * @return
     */
    public ReturnData dveryDayUpdateGlobleInfo() {
		int testSkinCount = initMapper.countTestResult();
        int productCount = initMapper.countProduct();
        int compositionCount = initMapper.countComposition();
        int registerNum = initMapper.countRegUser();

        List<Composition>  likeList=entityService.collectionCompositionSort();
        List<Composition>  notLikeList=entityService.notLikeCompositionSort();
		Map<String,Object> reg=new HashMap<String,Object>();
		reg.put("testSkinCount", testSkinCount);
		reg.put("productCount", productCount);
		reg.put("compositionCount", compositionCount);
		reg.put("curRegisterNum", 0);
		reg.put("curPhoneRegisterNum", 0);
		reg.put("allowReg", false);
		reg.put("likeList", likeList);
		reg.put("notLikeList", notLikeList);
		reg.put("registerNum", registerNum);
		GlobalConfig gcfig=this.putGlobalConfig(reg);
		return ReturnData.SUCCESS;
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
