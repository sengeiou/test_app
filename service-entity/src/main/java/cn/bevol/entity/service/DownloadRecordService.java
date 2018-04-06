package cn.bevol.entity.service;

import java.util.Date;

import javax.annotation.Resource;

import cn.bevol.mybatis.dao.DownloadRecordMapper;
import cn.bevol.mybatis.model.DownloadRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.io97.utils.DateUtils;
import com.io97.utils.db.Paged;

@Service
public class DownloadRecordService {

	@Resource
	private DownloadRecordMapper downloadRecordMapper;

	/***
	 *
	 * @param qrcodeId
	 * @param dataSource 维度1：微信、qq、pc ...
	 * @param dataSource2 维度2：Android、ios
     * @return
     */
	public Integer insertOrUpdate(Integer qrcodeId,String qrcodeName,String dataSource,String dataSource2) {
		Integer date4Today = DateUtils.timeStampParseInt();
		DownloadRecord downloadRecord = downloadRecordMapper.findByQrcodeId(qrcodeId,date4Today);
		int pcTotal =0;
		int weixinTotal =0;
		int androidTotal = 0;
		int iosTotal = 0;
		int mobile = 0;
		int result =0;
		if("pc" .equals(dataSource)){
			pcTotal ++;
		}else if("weixin".equals(dataSource)){
			weixinTotal++;
		}else if("qq".equals(dataSource)){

		}else if("mobile".equals(dataSource)){
			mobile++;
		}
		if("android".equals(dataSource2)){
			androidTotal++;
		}else if("ios".equals(dataSource2)){
			iosTotal++;
		}

		if(downloadRecord!=null) {
			 pcTotal += downloadRecord.getPcTotal();
			 weixinTotal += downloadRecord.getWeixinTotal();
			 androidTotal += downloadRecord.getAndroidTotal();
			 iosTotal += downloadRecord.getIosTotal();
			downloadRecord.setPcTotal(pcTotal);
			downloadRecord.setWeixinTotal(weixinTotal);
			downloadRecord.setAndroidTotal(androidTotal);
			downloadRecord.setIosTotal(iosTotal);
			downloadRecord.setMobileTotal(mobile);
			downloadRecord.setDataType(qrcodeName);
		    result = update(downloadRecord);
		}
		else {
			downloadRecord = new  DownloadRecord();
			downloadRecord.setQrcodeId(qrcodeId);
			downloadRecord.setPcTotal(pcTotal);
			downloadRecord.setWeixinTotal(weixinTotal);
			downloadRecord.setAndroidTotal(androidTotal);
			downloadRecord.setIosTotal(iosTotal);
			downloadRecord.setDataType(qrcodeName);
		    result = add(downloadRecord);
		}
		return result;
	}

	/***
	 * 添加
	 * @param record
	 * @return
     */
	public Integer add(DownloadRecord record) {
		Integer date4Today = DateUtils.timeStampParseInt();
		record.setStatisticsDate(date4Today);
		record.setCreateDate(new Date().getTime()/1000);
	   return	downloadRecordMapper.insert(record);
	}
	/***
	 * 修改
	 * @param record
	 * @return
	 */
	public Integer update(DownloadRecord record) {
		record.setUpdateDate(new Date().getTime()/1000);
		return	downloadRecordMapper.update(record);
	}
	
	/***
	 * 列表
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public Paged findByPage(String beginTime,String endTime,Integer startPage ){
		DownloadRecord record =new DownloadRecord();
		  if (null != beginTime) {
			  record.setBeginTime(Integer.parseInt(beginTime));
		  }
		  if (null != endTime) {
			  record.setEndTime(Integer.parseInt(endTime));
		  }
		 
		Paged<DownloadRecord> paged = new Paged<DownloadRecord>();
		paged.setWheres(record);
		paged.setCurPage(startPage);
		paged.addOrderBy("create_date", "DESC");
		paged.setResult(downloadRecordMapper.findByPage(paged));
		paged.setTotal(this.downloadRecordMapper.findByPageCount(paged));
		return paged;
	}
	
	public static void main(String[] args) {
		  ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		  DownloadRecordService staticRecordService = (DownloadRecordService) context.getBean("downloadRecordService");
//		 Integer result = staticRecordService.add("pc", "ios");
//		 System.out.println(result);

		 Paged ls =staticRecordService.findByPage("20170101", "20170104",1);
		 System.out.println(ls.getTotal());

	}
	
}
