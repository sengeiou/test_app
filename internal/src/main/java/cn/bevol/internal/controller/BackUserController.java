package cn.bevol.internal.controller;

import cn.bevol.internal.service.BackUserService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 后台管理用户的接口
 * @author chenHaiJian
 *
 */
@Controller
public class BackUserController extends BaseController {
	   @Resource
	   private BackUserService backUserService;
	
	   /**
	    * 把用户拉进黑名单
	    * @param request
	    * @param user_id
	    * @param state 1永久禁言 2时效性
	    * @param description 禁言原因描述
	    * @param start_time	开始禁言的时间
	    * @param end_time	结束禁言的时间
	    * @return
	    * @throws Exception
	    */
		@RequestMapping(value={"/back/user/blacklist/add"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData blackListAdd(HttpServletRequest request, @RequestParam Long user_id, @RequestParam Integer state, @RequestParam(required=false) String description, @RequestParam(required=false) Long start_time, @RequestParam(required=false) Long end_time ) throws Exception {
			return backUserService.addBlcakList(user_id,state,description,start_time,end_time);
		}   
		
		
		/**
		    * 把用户移出黑名单
		    * @param request
		    * @param user_id
		    * @return
		    * @throws Exception
		    */
			@RequestMapping(value={"/back/user/blacklist/remove"},method={RequestMethod.POST})
			@ResponseBody
			public ReturnData blackListRemove(HttpServletRequest request,@RequestParam Long user_id) throws Exception {
				return backUserService.removeBlcakList(user_id);
			}   
			
			/**
			 * 修改黑名单信息
			 * @param request
			 * @param user_id
			 * @param state
			 * @param description
			 * @param start_time
			 * @param end_time
			 * @return
			 * @throws Exception
			 */
			@RequestMapping(value={"/back/user/blacklist/update"},method={RequestMethod.POST})
			@ResponseBody
			public ReturnData blackListUpdate(HttpServletRequest request, @RequestParam Long user_id, @RequestParam(required=false) Integer state, @RequestParam(required=false) String description, @RequestParam(required=false) Long start_time, @RequestParam(required=false) Long end_time) throws Exception {
				return backUserService.updateBlcakList(user_id,state,description,start_time,end_time);
			}
			
			/**
		     * 1.为达人以前的精华点评和修行说计数,更新mongo的user表
		     * 2.有的达人没有注册,精华点评和修行说的数量为0.注册后调用该接口,进行计数
		     * @return
		     */
			@RequestMapping(value={"/back/essenceCommentAndXxs/update"},method={RequestMethod.POST})
			@ResponseBody
			public ReturnData getEssenceCommentAndXxsNum(HttpServletRequest request) throws Exception {
				return backUserService.getEssenceCommentAndXxsNum();
			}
}
