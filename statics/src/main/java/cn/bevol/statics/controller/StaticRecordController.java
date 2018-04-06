package cn.bevol.statics.controller;

import cn.bevol.statics.service.StaticRecordService;
import cn.bevol.util.response.ReturnData;
import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class StaticRecordController {
	@Resource
	private StaticRecordService recordService;
	
	@RequestMapping(value = {"/static/record"})
	  @ResponseBody
	 public Object searchWorkByName(HttpSession session, HttpServletRequest request,
                                    @PathVariable String mid) throws ClientProtocolException {
	        return new ReturnData(recordService.recordTotal());
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
