package cn.bevol.statics.service;

import cn.bevol.statics.entity.SearchItem;
import cn.bevol.util.DateUtils;
import cn.bevol.util.statistics.StatisticsI;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NoSearchStatisticalService implements StatisticsI {
	private static Logger logger = LoggerFactory.getLogger(NoSearchStatisticalService.class);
	  @Autowired
      MongoTemplate mongoTemplate;
	 
	/****
	 * 搜索不到产品和成分
	 * @param tname:goods、composition
	 * @return
	 */
	public Map<String,  List<SearchItem>> noResult (String tname){
		long previousDay = getPreviousDay();
		long previousWeek = getPreviousWeek();
		long previousMonth =getPreviousMonth();
		Map<String,  List<SearchItem>> map = new HashMap<String, List<SearchItem>>();
	    List<SearchItem> lsDay=	getPageInfosCount(tname,previousDay);
	    List<SearchItem> lsWeek = getPageInfosCount(tname,previousWeek);
	    List<SearchItem> lsMonth =	getPageInfosCount(tname,previousMonth);
	    map.put("day", lsDay);
	    map.put("week", lsWeek);
	    map.put("month", lsMonth);
		return map;
		 
		 
	}
	

	public List<SearchItem> getPageInfosCount(String tname, long searchDate) {
		 long thisDate = DateUtils.nowInMillis()/1000;
		 String groupStr = "{$group:{_id:'$keywords.name', count: {$sum:1}}}";
         DBObject group = (DBObject) JSON.parse(groupStr);
         String matchStr = "{$match:{createStamp:{$gt:"+searchDate+",$lt:"+thisDate+"}}}";
         DBObject match = (DBObject) JSON.parse(matchStr);
         String sortStr = "{$sort:{count:-1}}";
         DBObject sort = (DBObject) JSON.parse(sortStr);
         AggregationOutput outputDay = mongoTemplate.getCollection(StatisticsI.COLLECTION_NOSEARCH_PRE+tname).aggregate(match,group,sort);
         List<SearchItem> ls = new ArrayList<SearchItem>();
		 for(Iterator<DBObject> it = outputDay.results().iterator(); it.hasNext(); ){
         	BasicDBObject dbo = (BasicDBObject) it.next();
         	SearchItem item = new SearchItem();
         	item.setKeyword(dbo.get("_id").toString());
         	item.setCount(Integer.parseInt(dbo.get("count").toString()));
         	ls.add(item);
		 }
        return ls;
	}
	
	public long getPreviousDay(){
		  Calendar calendar = Calendar.getInstance();
		  calendar.add(Calendar.DATE, -1);    //得到前一天
		 return calendar.getTime().getTime()/1000;
	}
	 /**
	  *  获得近一周
	  * @return
	  */
	    public long getPreviousWeek() {
	        Calendar calendar = new GregorianCalendar();
	        calendar.setTime( new Date());
	        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
	        return calendar.getTime().getTime()/1000;
	    }
	    
	    /***
	     * 近一个月
	     * @return
	     */
	    public long getPreviousMonth(){
			  Calendar calendar = Calendar.getInstance();
			  calendar.add(Calendar.MONTH, -1);    //得到前一个月
			  return (calendar.getTime().getTime()/1000);
	    }
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		NoSearchStatisticalService s = (NoSearchStatisticalService)context.getBean("noSearchStatisticalService");
		//String str = s.getTaskStatistic(1475909889,1482829051);
		s.noResult("");
	}
	
	
	
}
