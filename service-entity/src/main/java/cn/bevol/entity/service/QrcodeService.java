package cn.bevol.entity.service;

import cn.bevol.mybatis.dao.QrcodeMapper;
import cn.bevol.mybatis.model.Qrcode;
import com.io97.utils.db.Paged;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class QrcodeService {

	@Resource
	private QrcodeMapper qrcodeMapper;
	
	/***
	 * 添加
	 * @param  parameter
	 * @param androidUrl
	 * @param iosUrl
	 * @param remark
	 * @return
	 */
	public Integer add(String androidUrl,String iosUrl,String name,String ticket,String parameter,String parameter2,String remark) {
		Qrcode record = new Qrcode();
		record.setAndroidUrl(androidUrl);
		record.setIosUrl(iosUrl);
		record.setName(name);
		record.setTicket(ticket);
		record.setParameter(parameter);
		record.setParameter2(parameter2);
		record.setRemark(remark);
	   return	qrcodeMapper.insertOrUpdate(record);
	}
	
	/***
	 * 列表
	 * @param parameter
	 * @param remark
	 * @return
	 */
	public Paged findByPage(String parameter,String remark,Integer startPage ){
		Qrcode record =new Qrcode();
		  if (!StringUtils.isEmpty(parameter)) {
			  record.setParameter(parameter);
		  }
		  if (!StringUtils.isEmpty(remark)) {
			  record.setRemark(remark);
		  }
		 
		Paged<Qrcode> paged = new Paged<Qrcode>();
		paged.setWheres(record);
		paged.setCurPage(startPage);
		paged.setResult(qrcodeMapper.findByPage(paged));
		paged.setTotal(this.qrcodeMapper.findByPageCount(paged));
		return paged;
	}

	public Qrcode findById(Integer qrcodeId) {
		return qrcodeMapper.selectByPrimaryKey(qrcodeId);
	}



	public static void main(String[] args) {
		  ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		  QrcodeService staticRecordService = (QrcodeService) context.getBean("qrcodeServiceService");
//		 Integer result = staticRecordService.add("pc", "ios");
//		 System.out.println(result);
		 Paged ls =staticRecordService.findByPage("20170101", "20170104",1);
		 System.out.println(ls.getTotal());

	}


}
