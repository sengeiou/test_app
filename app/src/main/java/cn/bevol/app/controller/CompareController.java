package cn.bevol.app.controller;

import cn.bevol.app.service.CompareService;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * 对比
 * @author hualong
 *
 */
@Controller
public class CompareController extends BaseController {
	   
		   
		@Autowired
		private CompareService compareService;
		

 		 /**
 		  * 产品对比
 		  * @param request
 		  * @param mids 产品的mid
 		  * @return
 		  * @throws Exception
 		  */
		@RequestMapping(value={"/compare/goods"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData compareTname(HttpServletRequest request, @RequestParam String mids) throws Exception {
			UserInfo userInfo=this.getUser(request);
			return compareService.compareGoods(mids,userInfo);
		}
		 
		 


		 /**
		  * 支持的产品
		  * @param request
		  * @param id 支持的id 为0时取消支持
		  * @return
		  * @throws Exception
		  */
		@RequestMapping(value={"/auth/like/compare_{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData likeCompare(HttpServletRequest request, @PathVariable String tname, @RequestParam(name="s_id") String sId, @RequestParam(required=false) Long id) throws Exception {
			UserInfo userInfo=this.getUser(request);
			tname="goods";
			return compareService.likeCompare(userInfo,tname,sId,id);
		}
		 

		

		 /**
		  * 话题列表
		  * @param request
		  * @param rids 关联id（前端分页）
		  * @return
		  * @throws Exception
		  */
		@RequestMapping(value={"/discuss/list/compare_{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnListData discussCompareList(HttpServletRequest request, @PathVariable String tname, @RequestParam(name="s_id") String sId, @RequestParam(name="r_ids",required=false) String rids, @RequestParam(defaultValue = "1",required=false) int pager, @RequestParam(defaultValue = "10",required=false) int pageSize) throws Exception {
			tname="goods";
			if(pageSize>20){
				pageSize=20;
			}
			Long userId=this.getUserId2(request);
			return compareService.discussList(userId,tname,sId,rids,pager,pageSize);
		}

		 /**
		  * 发送话题
		  * @param request
		  * @param sId 对比id
		  * @param rid 引用id
		  * @return
		  * @throws Exception
		  */
		@RequestMapping(value={"/auth/discuss/send/compare_{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData sendDiscuss(HttpServletRequest request, @PathVariable String tname, @RequestParam(name="s_id") String sId, @RequestParam(name="r_id",required=false) Long rid, @RequestParam String content) throws Exception {
			tname="goods";
			UserInfo userInfo=this.getUser(request);
			return  compareService.sendDiscuss(userInfo,tname,sId,rid,content);
		}
		 
		 /**
		  * 话题点赞
		  * @param request
		  * @param id 
		  * @return
		  * @throws Exception
		  */
		@RequestMapping(value={"/auth/discuss/like/compare_{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData sendDiscussLike(HttpServletRequest request, @PathVariable String tname, @RequestParam Long id) throws Exception {
			tname="goods";
			UserInfo userInfo=this.getUser(request);
			return  compareService.sendDiscussLike(userInfo,tname,id);
		}
		 

		 /**
		  * 对比广场列表
		  * @param request
		  * @param type 0 最热 1最新
		  * @return
		  * @throws Exception
		  */
		@RequestMapping(value={"/compare/list/{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData compareList(HttpServletRequest request, @PathVariable String tname, @RequestParam(defaultValue="0") Integer type, @RequestParam(defaultValue = "1") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
			tname="goods";
			if(pageSize>20){
				pageSize=20;
			}
			if(pager<=0){
				pager=1;
			}
			UserInfo userInfo=this.getUser(request);
			return compareService.compareList(userInfo,tname,type,pager,pageSize);
		}
		 

}
