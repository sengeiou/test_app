package cn.bevol.entity.service;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.bevol.entity.service.statistics.StatisticsI;
import cn.bevol.model.statistics.DailyActive;
import cn.bevol.mybatis.dao.UserStatisticalMapper;
import cn.bevol.mybatis.model.UserStatistical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.io97.utils.DateUtils;
import com.io97.utils.db.Paged;
import com.io97.utils.http.HttpUtils;

import net.sf.json.JSONObject;

@Service
public class UserStatisticalService implements StatisticsI {
	private static Logger logger = LoggerFactory.getLogger(UserStatisticalService.class);
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserStatisticalMapper userStatisticalMapper;

	/****
	 * 记录当天用户登录数、注册数、活跃数
	 * @return
	 */
	public int  addUserStatistical(){
		//int numA = 0,numB =0,numC = 0,numD = 0,numE = 0, numF = 0, numG = 0, numH = 0, numI = 0;
		UserStatistical userStatistical = new UserStatistical();
		int date4Today = Integer.parseInt(DateUtils.timeStampParseDate(DateUtils.nowInSeconds() - 24 * 60 * 60));
		// int date4Today = DateUtils.timeStampParseInt();
		int grandTotalNum = 0;
		List<DailyActive> ls = mongoTemplate.find(new Query(),DailyActive.class,StatisticsI.COLLECTION_DAILY_LOGIN_PRE+date4Today);
		for (DailyActive dailyActive : ls) {
			grandTotalNum+=dailyActive.getTotalNum();
		}
		userStatistical.setLoginTotalNum(ls.size());
		userStatistical.setLoginGrandTotalNum(grandTotalNum);
		grandTotalNum = 0;
		ls = mongoTemplate.find(new Query(),DailyActive.class, StatisticsI.COLLECTION_DAILY_REGISTER_PRE+date4Today);
		for (DailyActive dailyActive : ls) {
			grandTotalNum+=dailyActive.getTotalNum();
		}
		userStatistical.setRegisterTotalNum(ls.size());
		userStatistical.setRegisterGrandTotalNum(grandTotalNum);
		grandTotalNum = 0;
		ls = mongoTemplate.find(new Query(),DailyActive.class,StatisticsI.COLLECTION_DAILY_INIT_PRE+date4Today);
		for (DailyActive dailyActive : ls) {
			grandTotalNum+=dailyActive.getTotalNum();
		}
		userStatistical.setStartTotalNum(ls.size());
		userStatistical.setStartGrandTotalNum(grandTotalNum);
		grandTotalNum = 0;
		Integer[] nums ={0,0,0,0,0,0,0,0,0};
		JSONObject obj = new JSONObject();
		ls = mongoTemplate.find(new Query(),DailyActive.class,StatisticsI.COLLECTION_DAILY_ACTIVE_PRE+date4Today);
		List<Integer> num = new ArrayList<Integer>();
		num.add(0);
		for (DailyActive dailyActive : ls) {
			nums = getActivedNum(dailyActive.getTotalNum(),nums);
			grandTotalNum+=dailyActive.getTotalNum();
		}
		obj= getActivediStribution(nums);
		userStatistical.setActiveTotalNum(ls.size());
		userStatistical.setActiveGrandTotalNum(grandTotalNum);
		userStatistical.setActivediStribution(obj.toString());
		userStatistical.setStatisticsDate(date4Today);
		userStatistical.setCreateTime(new Date().getTime()/1000);
		userStatistical.setUpdateTime(new Date().getTime()/1000);
		int flag = userStatisticalMapper.insertOrUpdate(userStatistical);

		logger.info("添加状态："+flag);

		return flag;


	}


	/****
	 * 手动查询某天用户登录数、注册数、活跃数
	 * @param date4Today
	 * @return
	 */
	public UserStatistical  findUserStatisticalByDay(String date4Today){

		int numA = 0,numB =0,numC = 0,numD = 0,numE = 0, numF = 0, numG = 0, numH = 0, numI = 0;
		UserStatistical userStatistical = new UserStatistical();
		//int date4Today = DateUtils.timeStampParseInt();
		int grandTotalNum = 0;
		JSONObject obj = new JSONObject();
		List<DailyActive> ls = mongoTemplate.find(new Query(),DailyActive.class,StatisticsI.COLLECTION_DAILY_LOGIN_PRE+date4Today);
		for (DailyActive dailyActive : ls) {
			grandTotalNum+=dailyActive.getTotalNum();
		}
		userStatistical.setLoginTotalNum(ls.size());
		userStatistical.setLoginGrandTotalNum(grandTotalNum);
		grandTotalNum = 0;
		ls = mongoTemplate.find(new Query(),DailyActive.class, StatisticsI.COLLECTION_DAILY_REGISTER_PRE+date4Today);
		for (DailyActive dailyActive : ls) {
			grandTotalNum+=dailyActive.getTotalNum();
		}
		userStatistical.setRegisterTotalNum(ls.size());
		userStatistical.setRegisterGrandTotalNum(grandTotalNum);
		grandTotalNum = 0;
		ls = mongoTemplate.find(new Query(),DailyActive.class,StatisticsI.COLLECTION_DAILY_INIT_PRE+date4Today);
		for (DailyActive dailyActive : ls) {
			grandTotalNum+=dailyActive.getTotalNum();
		}
		userStatistical.setStartTotalNum(ls.size());
		userStatistical.setStartGrandTotalNum(grandTotalNum);
		grandTotalNum = 0;
		Integer[] nums ={0,0,0,0,0,0,0,0,0};
		ls = mongoTemplate.find(new Query(),DailyActive.class,StatisticsI.COLLECTION_DAILY_ACTIVE_PRE+date4Today);
		for (DailyActive dailyActive : ls) {
			nums = getActivedNum(dailyActive.getTotalNum(),nums);
			grandTotalNum+=dailyActive.getTotalNum();
		}
		obj= getActivediStribution(nums);
		userStatistical.setActiveTotalNum(ls.size());
		userStatistical.setActiveGrandTotalNum(grandTotalNum);
		userStatistical.setActivediStribution(obj.toString());
		userStatistical.setStatisticsDate(Integer.parseInt(date4Today));
		userStatistical.setCreateTime(new Date().getTime()/1000);
		userStatistical.setUpdateTime(new Date().getTime()/1000);
		int flag = userStatisticalMapper.insertOrUpdate(userStatistical);

		logger.info("添加状态："+flag);

		return userStatistical;


	}
	/***
	 * 查询时间段内活跃数据(最多返回60条记录)
	 * @param beginTime 毫秒
	 * @param endTime  毫秒
	 * @return
	 */
	public List<UserStatistical> findByPage(String beginTime,String endTime ){
		UserStatistical statistical =new UserStatistical();
		if (null != beginTime) {
			statistical.setBeginTime(Integer.parseInt(beginTime));
		}
		if (null != endTime) {
			statistical.setEndTime(Integer.parseInt(endTime));
		}

		Paged<UserStatistical> paged = new Paged<UserStatistical>();

		paged.setWheres(statistical);
		paged.setPageSize(60);
		paged.addOrderBy("statistics_date", "ASC");
		return userStatisticalMapper.findByPage(paged);
	}
	/***
	 * 查询某一天活跃数据
	 * @param statisticsDate  20161212
	 * @return
	 */
	public UserStatistical findByStatisticsDate(String statisticsDate){

		return userStatisticalMapper.findByStatisticsDate(Integer.parseInt(statisticsDate));
	}


	private Integer[] getActivedNum(int size,Integer[] nums){
		int A1= 15;
		int B1= 30;
		int C1= 60;
		int D1= 120;
		int E1= 250;
		int F1= 500;
		int G1= 800;
		int H1= 1200;
		if(size<=A1){
			nums[0]  += 1;
		}
		else if(size>A1 && size<=B1){
			nums[1] += 1;
		}
		else if(size>B1 && size<=C1){
			nums[2]  += 1;
		}
		else if(size>C1 && size<=D1){
			nums[3]  += 1;
		}
		else if(size>D1 && size<=E1){
			nums[4]  += 1;
		}
		else if(size>E1 && size<=F1){
			nums[5]  += 1;
		}
		else if(size>F1 && size<=G1){
			nums[6]  += 1;
		}
		else if(size>G1 && size<=H1){
			nums[7]  += 1;
		}
		else {
			nums[8] += 1;
		}


		return nums;
	}


	private JSONObject getActivediStribution(Integer[] num){
		String A= "S0";
		String B= "S15";
		String C= "S30";
		String D= "S60";
		String E= "S120";
		String F= "S250";
		String G= "S500";
		String H= "S800";
		String I= "S1200";
		String json = format("{%s:%d,%s:%d,%s:%d,%s:%d,%s:%d,%s:%d,%s:%d,%s:%d,%s:%d}", A, num[0], B, num[1], C, num[2], D, num[3], E, num[4], F, num[5], G, num[6], H, num[7], I, num[8]);
		JSONObject.fromObject(json);
		return JSONObject.fromObject(json);
	}


	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		UserStatisticalService service = (UserStatisticalService) context.getBean("userStatisticalService");
		//service.find(20161111);
		// List<UserStatistical> ls=  service.findByPage(20161111,20161211);


		String url = "http://istatic.bevol.cn/static/findUserStatistical/";
		int d = 20161200;
		for (int i = 1; i <=31; i++) {
			d++;
			String result =  HttpUtils.post(url+d,new HashMap<String, String>());
			System.out.println(result);
		}

	}

}
