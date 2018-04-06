package cn.bevol.staticc.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.bevol.entity.service.DownloadRecordService;
import cn.bevol.entity.service.QrcodeService;
import cn.bevol.mybatis.model.Qrcode;
import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bevol.web.response.ResponseBuilder;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/static", "/"})
public class DownloadRecordController {
	@Resource
	DownloadRecordService downloadRecordService;
	@Resource
	QrcodeService qrcodeService;


	@RequestMapping(value = {"/static/download/init/{qrcodeId}"})
	public ModelAndView downloadInit(HttpSession session, HttpServletRequest request,
			@PathVariable Integer qrcodeId){
		Qrcode qrcode = qrcodeService.findById(qrcodeId);
			ModelAndView model = new ModelAndView();
			model.setViewName("/download");
			model.addObject("qrcodeId",qrcode.getId());
		    model.addObject("qrcodeName",qrcode.getName());
			model.addObject("androidUrl",qrcode.getAndroidUrl());
			model.addObject("iosUrl",qrcode.getIosUrl());
		return model;
	} 
	
	@RequestMapping(value = {"/static/download/add"})
	  @ResponseBody
	 public Object downloadAdd(HttpSession session, HttpServletRequest request,
			 @RequestParam Integer qrcodeId,
			 @RequestParam String dataSource,
			 @RequestParam String qrcodeName,
			 @RequestParam String dataSource2) throws ClientProtocolException{
	        return ResponseBuilder.buildResult(downloadRecordService.insertOrUpdate(qrcodeId,qrcodeName,dataSource,dataSource2));
	 }
	@RequestMapping(value = {"/static/download/list"})
	@ResponseBody
	public Object downloadByPage(HttpSession session, HttpServletRequest request,
			 @RequestParam String beginTime,
			 @RequestParam String endTime,
			 @RequestParam(required = false, defaultValue = "1") Integer startPage){
		 return ResponseBuilder.buildResult(downloadRecordService.findByPage(beginTime, endTime,startPage));
	}
	
}
