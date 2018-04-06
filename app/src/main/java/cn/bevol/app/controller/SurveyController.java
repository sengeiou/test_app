package cn.bevol.app.controller;

import cn.bevol.app.service.SkinService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;


/**
 * 肤质测试相关
 *
 */

@Controller
public class SurveyController extends BaseController {
	   
		@Resource
		private SkinService skinService;

		/**
		 * 测试人数
		 *  
		 */
		@Deprecated
		@RequestMapping("/survey/testcount")
		@ResponseBody
		public ReturnData testcount() throws Exception {
			HashMap mp=new HashMap();
			mp.put("count", 112733);
			return new ReturnData(mp);
		}
		
 
		/**
		 * 用户自己选择肤质(选择模块)
		 * @param id: 模块id(1,3,4,5)
		 * @param key: 类型key(T_20_40_T)
		 * @return
		 * @throws Exception
		 */
		@RequestMapping("/auth/survey/setskin")
		@ResponseBody
		public ReturnData setSkin(HttpServletRequest request, HttpServletResponse response, @RequestParam int id, @RequestParam String key) throws Exception {
			long userId=this.getUserId(request);
			ReturnData rd= skinService.setSkin(userId, id, key);
			//重新加载user
			ReturnData ui=this.reloadUser(request);
			return rd;
		}
		
		/**
		 * 进入某个模块进行每道题的测试,获取分数
		 * @param id: 模块id
		 * @param score: 该模块得到的分数
		 * @return
		 * @throws Exception
		 */
		@RequestMapping("/auth/survey/test")
		@ResponseBody
		public ReturnData addHit(HttpServletRequest request, HttpServletResponse response, @RequestParam int id, @RequestParam Integer score) throws Exception {
			long userId=this.getUserId(request);
			ReturnData rd= skinService.skinTest(userId, id, score);
			//重新加载user
			ReturnData ui=this.reloadUser(request);
			return rd;
		}
		
		
		/**
		 * 获取某个肤质模块的测试题文案
		 * @param ids: 模块id,逗号分隔
		 * @return
		 * @throws Exception
		 */
		@RequestMapping("/survey/info")
		@ResponseBody
		public ReturnData newSkinTest(HttpServletRequest request,HttpServletResponse response,@RequestParam String ids) throws Exception {
			//long userId=this.getUserId(request);
			ReturnData rd= skinService.newSkinTest(ids);
			return rd;

		}
		
}
