package cn.bevol.app.controller;

import cn.bevol.app.service.SkinProtectionService;
import cn.bevol.util.response.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * 数据同步接口
 * @author hualong
 *
 */
@Controller
public class CommonController extends BaseController {
	   
		   
		@Autowired
		private SkinProtectionService skinProtectionService;
		

 		 /**
 		  * 用于护肤方案分享
 		  * @param request
 		  * @param user_id 用户id
 		  * @param category_pid 肤质方案id
 		  * @return
 		  * @throws Exception
 		  */
		@RequestMapping(value={"/common/skin_protection_goods/list"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData findSkinProtectionGoods(HttpServletRequest request, @RequestParam long user_id, @RequestParam long category_pid) throws Exception {
			return skinProtectionService.findCommonSkinProtectionGoods(user_id,category_pid,0,30);
		}
		 
		 



}
