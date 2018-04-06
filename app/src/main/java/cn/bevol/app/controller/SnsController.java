package cn.bevol.app.controller;

import cn.bevol.app.service.SnsService;
import cn.bevol.util.response.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * 社区
 * @author hualong
 *
 */
@Controller
public class SnsController extends BaseController {
	   
		   
	
		@Autowired
		private SnsService snsService;
		
		/**
		 * v3.0之前
		 * 修行说首页,只支持普通心得
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/sns/index"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData statistics(HttpServletRequest request, @RequestParam(defaultValue="0") int pager, @RequestParam(defaultValue="10") int pageSize) throws Exception {
				return snsService.index(1,pager,pageSize);
		}
		
		/**
		 * v3.0
		 * 修行说首页
		 * 五分钟缓存
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/sns/index2"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData index2(HttpServletRequest request, @RequestParam(defaultValue="0") int pager, @RequestParam(defaultValue="10") int pageSize) throws Exception {
			return snsService.index(0,pager,pageSize);
		}
		

		
 
}
