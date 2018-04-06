package cn.bevol.entity.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

import cn.bevol.mybatis.dao.SeoAwaitMapper;
import cn.bevol.mybatis.model.SeoAwait;
import cn.bevol.util.ReturnData;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.io97.utils.db.Paged;

import cn.bevol.conf.client.ConfClient;

@Service
public class SeoAwaitService {
	  private static Logger logger = LoggerFactory.getLogger(SeoAwaitService.class);
	private final static String WWW_BEVOL_DOMAIN = "https://www.bevol.cn";
	private final static String M_BEVOL_DOMAIN = "https://m.bevol.cn";
	  final ConfClient conf = new ConfClient("api");

	@Resource
	SeoAwaitMapper seoAwaitMapper;
	@Resource
	SeoService seoService;

	public ReturnData insertOrupdatePoduct(String mid, String operateType){
		String pc_url = WWW_BEVOL_DOMAIN+"/product/"+mid+".html";
		String m_url = M_BEVOL_DOMAIN+"/product/"+mid+".html";
		insertOrupdate(mid, pc_url, "pc", "product", operateType);
		insertOrupdate(mid, m_url, "m", "product", operateType);
		return ReturnData.SUCCESS;
	}

	public ReturnData insertOrupdateComposition(String mid, String operateType){
		String pc_url = WWW_BEVOL_DOMAIN+"/composition/"+mid+".html";
		String m_url = M_BEVOL_DOMAIN+"/composition/"+mid+".html";
		insertOrupdate(mid, pc_url, "pc", "composition", operateType);
		insertOrupdate(mid, m_url, "m", "composition", operateType);
		return ReturnData.SUCCESS;
	}

	/***
	 * 
	 * @param url 
	 * @param dataType : pc|mobile
	 * @param dataSource: goods|find|composition
	 * @param operateType: add|update|delete
	 * @return
	 */
	public int insert(String mid,String url,String dataType,String dataSource,String operateType){
		SeoAwait seoAwait = new SeoAwait();
		seoAwait.setCreateTime(new Date().getTime()/1000);
		seoAwait.setMid(mid);
		seoAwait.setUrl(url);
		seoAwait.setDataSource(dataSource);
		seoAwait.setOperateType(operateType);
		seoAwait.setDataType(dataType);
		return seoAwaitMapper.insert(seoAwait);
	}
	/***
	 * 
	 * @param url 
	 * @param dataType : pc|mobile
	 * @param dataSource: goods|find|composition
	 * @param operateType: add|update|delete
	 * @return
	 */
	public int insertOrupdate(String mid,String url,String dataType,String dataSource,String operateType){
		SeoAwait seoAwait = new SeoAwait();
		seoAwait.setCreateTime(new Date().getTime()/1000);
		seoAwait.setMid(mid);
		seoAwait.setUrl(url);
		seoAwait.setDataSource(dataSource);
		seoAwait.setOperateType(operateType);
		seoAwait.setDataType(dataType);
		return seoAwaitMapper.insertOrUpdate(seoAwait);
	}
	
	public Paged findByPage(String dataType,String dataSource,String operateType,String state,String beginTime,String endTime, Integer page){
		SeoAwait seoAwait =new SeoAwait();
		 if (!StringUtils.isEmpty(beginTime)) {
			 seoAwait.setBeginTime(Integer.parseInt(beginTime));
		  }
		  if (!StringUtils.isEmpty(endTime)) {
			  seoAwait.setEndTime(Integer.parseInt(endTime));
		  }
		 if (!StringUtils.isEmpty(dataType)) {
			 seoAwait.setDataType(dataType);
         }
		  if (!StringUtils.isEmpty(operateType)) {
			  seoAwait.setOperateType(operateType);
         }
		  if (!StringUtils.isEmpty(dataSource)) {
			  seoAwait.setDataSource(dataSource);
         }
		  if (!StringUtils.isEmpty(state)) {
			  seoAwait.setState(Integer.parseInt(state));
		  }
		  Paged<SeoAwait> paged = new Paged<SeoAwait>();
		  paged.setWheres(seoAwait);
		  paged.setCurPage(page);
		  paged.addOrderBy("create_time", "desc");
		  paged.setResult(seoAwaitMapper.findByPage(paged));
		 paged.setTotal(seoAwaitMapper.selectTotal(paged));
		return paged;
	}
	
	/**
	 * 根据条件查询总条数
	 * @param dataType
	 * @param dataSource
	 * @param operateType
	 * @return
	 */
	public int selectTotalByWhere(String dataType,String dataSource,String operateType,int state){
		return seoAwaitMapper.selectTotalByWhere(dataType,dataSource,operateType);
	}
	
	public String addSeoBatchJob(){
		String result="";
		 if (conf.getResourceNum("seo_job_switch")!=0){
			 List<SeoAwait> ls = seoAwaitMapper.findAll();
			 return seoBatch(ls);
		 }
		 return result;
	}


	public String addSeoBatchJob(Integer beginTime,Integer endTime){
		String result="";
		if (conf.getResourceNum("seo_job_switch")!=0) {
			List<SeoAwait> ls = seoAwaitMapper.findByDate(beginTime, endTime);
			return seoBatch(ls);
		}
		return result;
	}




	/***
	 * 手动提交后批量处理
	 */
	public String seoBatch(List<SeoAwait> ls){
		String result ="";
		 List<String> mAdds = new ArrayList<String>();
		 List<String> mUpdates =new ArrayList<String>();
		 List<String> mDels =new ArrayList<String>();
		 List<String> pAdds = new ArrayList<String>();
		 List<String> pUpdates =new ArrayList<String>();
		 List<String> pDels =new ArrayList<String>();
		 for (SeoAwait seoAwait : ls) {
			if("m".equals(seoAwait.getDataType())){
				if("add".equals(seoAwait.getOperateType()))
					mAdds.add(seoAwait.getUrl());
				else if("update".equals(seoAwait.getOperateType()))
					mUpdates.add(seoAwait.getUrl());
				else
					mDels.add(seoAwait.getUrl());
			}
			if("pc".equals(seoAwait.getDataType())){
				if("add".equals(seoAwait.getOperateType()))
					pAdds.add(seoAwait.getUrl());
				else if("update".equals(seoAwait.getOperateType()))
					pUpdates.add(seoAwait.getUrl());
				else
					pDels.add(seoAwait.getUrl());
			}

		}
		 if(pAdds!=null&&pAdds.size()>0){
			 result = seoService.add(pAdds, "pc");
			 if("-1".equals(result)){
				 return "当天推送次数已用完！";
			 }
			 updateSeoAwaitState(result,pAdds,"add","pc");
		 	logger.info("pc站新增成功"+pAdds.size());
		 }
		 if(pUpdates!=null&&pUpdates.size()>0){
			 result = seoService.update(pUpdates, "pc");
			 if("-1".equals(result)){
				 return "当天推送次数已用完！";
			 }
			 updateSeoAwaitState(result,pUpdates,"update","pc");
		 	logger.info("pc站修改成功"+pUpdates.size());
		 }
		 if(pDels!=null&&pDels.size()>0){
			 result = seoService.delete(pDels, "pc");
			 if("-1".equals(result)){
				 return "当天推送次数已用完！";
			 }
			 updateSeoAwaitState(result,pDels,"delete","pc");
			 logger.info("pc站删除成功"+pDels.size());
		 }
		 if(mAdds!=null&&mAdds.size()>0){
			 result = seoService.add(mAdds, "m");
			 if("-1".equals(result)){
				 return "当天推送次数已用完！";
			 }
			 updateSeoAwaitState(result,mAdds,"add","m");
			 logger.info("m站新增成功"+mAdds.size());
		 }
		 if(mUpdates!=null&&mUpdates.size()>0){
			 result = seoService.update(mUpdates, "m");
			 if("-1".equals(result)){
				 return "当天推送次数已用完！";
			 }
			 updateSeoAwaitState(result,mUpdates,"update","m");
			 logger.info("m站修改成功"+mUpdates.size());
		 }
		 if(mDels!=null&&mDels.size()>0){
			result = seoService.delete(mDels, "m");
			 if("-1".equals(result)){
				 return "当天推送次数已用完！";
			 }
			 updateSeoAwaitState(result,mDels,"delete","m");
			 logger.info("m站删除成功"+mDels.size());
		 }
		 return result;
	}

	public void updateSeoAwaitState(String message, List<String> urls,String operateType,String dataType) {
		if (!"fault".equals(message)) {//http请求异常！
			JSONObject dataObj = JSONObject.fromObject(message);
			int remain = dataObj.getInt("remain");
			if (remain > 0) {
				if (!dataObj.has("result")) {//没有错误
					for (int i = 0; i < urls.size(); i++) {
						SeoAwait seoAwait = new SeoAwait();
						seoAwait.setState(1);
						seoAwait.setUpdateTime(new Date().getTime() / 1000);
						seoAwait.setUrl(urls.get(i));
						seoAwait.setDataType(dataType);
						seoAwait.setOperateType(operateType);
						seoAwaitMapper.update(seoAwait);
					}
				}
			}
		}
	}

	/***
	 * 定时任务：每天十二点执行一次
	 * 批量处理管理后台提交的SEO推送
	 * param arg 6
	 */
	  public void seoJob(int arg) {
		 if(arg==7){
		    Calendar calendar = Calendar.getInstance();
		    calendar.set(Calendar.HOUR_OF_DAY, 24); // 控制时
		    calendar.set(Calendar.MINUTE, 0);    // 控制分
		    calendar.set(Calendar.SECOND, 0);    // 控制秒
		    Date time = calendar.getTime();     // 得出执行任务的时间,此处为今天的12：00：00
		    Timer timer = new Timer();
		    timer.scheduleAtFixedRate(new TimerTask() {
		      public void run() {
		    	  if (conf.getResourceNum("seo_job_switch")!=0) {
					  // seoBatch();
				  }
		      }
		    }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行
		  }
	  }


}
