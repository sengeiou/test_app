package cn.bevol.staticc.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.bevol.entity.service.StaticRecordService;
import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bevol.web.response.ResponseBuilder;

@Controller
@RequestMapping({"/static", "/"})
public class StaticRecordController {
	@Resource
	private StaticRecordService recordService;
	
	@RequestMapping(value = {"/static/record"})
	  @ResponseBody
	 public Object searchWorkByName(HttpSession session, HttpServletRequest request,
			 @PathVariable String mid) throws ClientProtocolException{
	        return ResponseBuilder.buildResult(recordService.recordTotal());
	 }
	/***
	 * 日运行8W条
	 * @param session
	 * @param request
	 */
	@RequestMapping("/static/record/evaryDay8W")
	@ResponseBody
	public void evaryDay8W(HttpSession session, HttpServletRequest request){
		recordService.batch8wGoodsStatics();
	}

}
