package cn.bevol.app.controller;

import cn.bevol.app.service.CommentService;
import cn.bevol.app.service.EntityService;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CommentController extends BaseController {

	@Autowired
	private EntityService entityService;

	@Autowired
	private CommentService commentService;
	

	/**
	 * 发送评论
	 *
	 */
	@Deprecated
	@RequestMapping(value = { "/auth/entity/comment/send/{tname}" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData send(HttpServletRequest request, @RequestParam long id, @RequestParam String content,
						   @RequestParam(defaultValue = "0") int score, @RequestParam(required = false) String image,
						   @PathVariable String tname) throws Exception {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);
		UserInfo userInfo = this.getUser(request);
		ReturnData rd2 = commentService.sendComment(tname, id, userInfo, content, score, image);
		return rd2;
	}

	/**
	 * 回复
	 * 
	 * @param request
	 * @param id
	 *            产品id
	 * @param content
	 * @param score
	 * @param image
	 * @param comment_pid
	 *            评论的pid
	 * @param tname
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/entity/comment2/send/{tname}" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData replySend(HttpServletRequest request, @RequestParam long id, @RequestParam String content,
                                @RequestParam(defaultValue = "0") int score, @RequestParam(required = false) String image,
                                @RequestParam(required = false, defaultValue = "0") Long comment_pid, @PathVariable String tname)
			throws Exception {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);
		UserInfo userInfo = this.getUser(request);
		ReturnData rd2 = commentService.replySend(tname, id, comment_pid, userInfo, content, score, image,
				true);
		return rd2;
	}
	
	
	
	/**
	 * v3.1
	 * 将评星 和评论内容分开提交 
	 * @param request
	 * @param id: 实体id
	 * @param content: 评论内容
	 * @param score: 给产品打的星级
	 * @param image: 图片
	 * @param comment_pid:	评论的父id
	 * @param tname
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/entity/comment3/send/{tname}" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData replySend3(HttpServletRequest request, @RequestParam long id, @RequestParam(required = false) String content,
                                 @RequestParam(defaultValue = "0") int score, @RequestParam(required = false) String image,
                                 @RequestParam(required = false, defaultValue = "0") Long comment_pid, @PathVariable String tname)
			throws Exception {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);
		UserInfo userInfo = this.getUser(request);
		ReturnData rd2 = commentService.replySend3_0(tname, id, comment_pid, userInfo, content, score, image,
				true);
		return rd2;
	}
	
	/**
	 * v3.2
	 * 新增特性:
	 * 	1.图片可以上传三张
	 * 	2.提交的时候有标签
	 * @param request
	 * @param id: 实体id
	 * @param content: 评论内容
	 * @param score: 给产品打的星级
	 * @param image: 图片
	 * @param comment_pid:	评论的父id
	 * @param tags:	标签
	 * @param images:	图片(三张)	
	 * @param tname
	 * @param reason: 申请理由(福利社特有)
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/entity/comment4/send/{tname}" }, method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData replySend4(HttpServletRequest request, @RequestParam long id, @RequestParam(required = false) String content,
                                 @RequestParam(defaultValue = "0") int score, @RequestParam(required = false) String image,
                                 @RequestParam(required = false, defaultValue = "0") Long comment_pid, @PathVariable String tname
			, @RequestParam(required = false) String tags, @RequestParam(required = false) String images
			, @RequestParam(required = false) Integer reason)
			throws Exception {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);
		UserInfo userInfo = this.getUser(request); 
		ReturnData rd2 = commentService.replySend3_2(tname, id, comment_pid, userInfo, content, score, image,
				true,tags,images,reason);
		return rd2;
	}
	
	
	/**
	 * 评论列表
	 * 
	 * @param request
	 * @param tname
	 * @param id
	 * @param type
	 * @param pager
	 *            页数，第一页，第二页
	 * @param pageSize
	 * @return
	 *
	 */
	@Deprecated
	@RequestMapping(value = { "/entity/comment/lists/{tname}" })
	@ResponseBody
	public ReturnListData sendComment2(HttpServletRequest request, @PathVariable String tname, @RequestParam long id,
									   @RequestParam(defaultValue = "0") int type, @RequestParam(defaultValue = "", required = false) String skin,
									   @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);

		// 总的喜欢数
		// 总的收藏数
		// 总的访问数
		UserInfo userInfo = this.getUser(request);
		long userId = 0L;
		String skinResults = "";
		if (userInfo != null) {
			userId = userInfo.getId();
			skinResults = userInfo.getSkinResults();
			if(type!=1 && !"goods".equals(tname)){
				skinResults="";
			}
		}
		if (pager > 0) {
			pager = (pager - 1);
		} else {
			pager = 0;
		}

		ReturnListData map = commentService.findComments(tname, id, type, userId, skinResults, pager, pageSize);
		return map;
	}

	/**
	 * 2.5评论列表
	 * 
	 * @param request
	 * @param tname
	 * @param id
	 * @param type
	 * @param pager
	 *            页数，第一页，第二页
	 * @param pageSize
	 * @return
	 *
	 */
	@Deprecated
	@RequestMapping(value = { "/entity/comment2/lists/{tname}" })
	@ResponseBody
	public ReturnListData CommentList(HttpServletRequest request, @PathVariable String tname, @RequestParam long id,
                                      @RequestParam(defaultValue = "0") int type, @RequestParam(defaultValue = "", required = false) String skin,
                                      @RequestParam(defaultValue = "0") int comment_main_id, @RequestParam(defaultValue = "0") int pager,
                                      @RequestParam(defaultValue = "10") int pageSize) {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);

		// 总的喜欢数
		// 总的收藏数
		// 总的访问数
		UserInfo userInfo = this.getUser(request);
		long userId = 0L;
		String skinResults = "";
		if (userInfo != null) {
			userId = userInfo.getId();
			skinResults = userInfo.getSkinResults();
			if(type!=1 && !"goods".equals(tname)){
				skinResults="";
			}
		}
		if (pager > 0) {
			pager = (pager - 1);
		} else {
			pager = 0;
		}
		ReturnListData map = null;
		if (comment_main_id > 0) {
			map = commentService.findSubComments2_5(tname, comment_main_id, userId, pager, pageSize);
		} else {
			map = commentService.findComments2_5(tname, id, type, userId, skinResults, pager, pageSize);
		}
		return map;
	}
	
 	/**
	 * 2.5.5评论列表
	 * 
	 * @param request
	 * @param tname
	 * @param id
	 * @param type
	 * @param pager
	 *            页数，第一页，第二页
	 * @param pageSize
	 * @return
	 *
	 */
	@RequestMapping(value = { "/entity/comment3/lists/{tname}" })
	@Deprecated
	@ResponseBody
	public ReturnData CommentList3(HttpServletRequest request, @PathVariable String tname, @RequestParam long id,
                                   @RequestParam(defaultValue = "0") int type, @RequestParam(defaultValue = "", required = false) String skin,
                                   @RequestParam(defaultValue = "0") int comment_main_id, @RequestParam(defaultValue = "0") int pager,
                                   @RequestParam(defaultValue = "10") int pageSize) {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);

		// 总的喜欢数
		// 总的收藏数
		// 总的访问数
		UserInfo userInfo = this.getUser(request);
		long userId = 0L;
		String skinResults = "";
		if (userInfo != null) {
			userId = userInfo.getId();
			skinResults = userInfo.getSkinResults();
			if(type!=1 && !"goods".equals(tname)){
				skinResults="";
			}
		}
		if (pager > 0) {
			pager = (pager - 1);
		} else {
			pager = 0;
		}
		Map m=new HashMap();
		ReturnData ret =null;
		if (comment_main_id > 0) {
			ReturnData rd = commentService.findSubComments(tname, id, comment_main_id, userId, pager, pageSize);
			ret= new ReturnData(rd.getResult());
		} else {
			ReturnListData rd = commentService.findComments2_5(tname, id, type, userId, skinResults, pager, pageSize);
			m.put("list", rd.getResult());
			m.put("total", rd.getTotal());
			ret= new ReturnData(m);
		}
		return ret;
	}
	
	
	/**
	 * 2.7评论列表 用户点评排序 同肤质排序
	 * 缓存:五分钟过期
	 * 
	 * @param request
	 * @param tname
	 * @param id: 实体id
	 * @param comment_main_id: 一级评论id
	 * @param type: 0默认 1肤质 好的 一般的 不好用的
	 * @param pager
	 *            页数，第一页，第二页
	 * @param pageSize
	 * @return
	 *
	 */
	@RequestMapping(value = { "/entity/comment4/lists/{tname}" })
	@ResponseBody
	public ReturnData CommentList4(HttpServletRequest request, @PathVariable String tname, @RequestParam long id,
                                   @RequestParam(defaultValue = "0") int type, @RequestParam(defaultValue = "", required = false) String skin,
                                   @RequestParam(defaultValue = "0") long comment_main_id, @RequestParam(defaultValue = "1") int pager,
                                   @RequestParam(defaultValue = "10") int pageSize) {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);

		// 总的喜欢数
		// 总的收藏数
		// 总的访问数
		UserInfo userInfo = this.getUser(request);
		long userId = 0L;
		String skinResults = "";
		if (userInfo != null) {
			userId = userInfo.getId();
			skinResults = userInfo.getSkinResults();
			if(type!=1 && !"goods".equals(tname)){
				skinResults="";
			}
		}
		if (pager > 0) {
			pager = (pager - 1);
		} else {
			pager = 0;
		}
		Map m=new HashMap();
		ReturnData ret =null;
		if (comment_main_id > 0) {
			//一级评论的子评论列表
			ReturnData rd = commentService.findSubComments(tname, id, comment_main_id, userId, pager, pageSize);
			ret= new ReturnData(rd.getResult());
		} else {
			//实体的一级评论列表
			Map map = commentService.findComments2_7(tname, id, type, userId, skinResults, pager, pageSize);
			m.put("list", map.get("mainList"));
			//m.put("type"+type+"Count", rd.getTotal());
			m.put("count", map.get("typeCount"));
			m.put("total", map.get("total"));
			m.put("userParts", map.get("userParts"));
			ret= new ReturnData(m);
		}
		return ret;
	}

	
	/**
	 * 3.0源生评论列表 最热和最新排序 
	 * 缓存:五分钟过期
	 * @param request
	 * @param tname
	 * @param id: 实体id
	 * @param comment_main_id: 一级评论id
	 * @param type: 0默认 1肤质 好的 一般的 不好用的
	 * @param pager
	 *            页数，第一页，第二页
	 * @param pageSize
	 * @return
	 *
	 */
	@RequestMapping(value = { "/entity/comment5/lists/{tname}" })
	@ResponseBody
	public ReturnData CommentList5(HttpServletRequest request, @PathVariable String tname, @RequestParam long id,
                                   @RequestParam(defaultValue = "0") int type, @RequestParam(defaultValue = "", required = false) String skin,
                                   @RequestParam(defaultValue = "0") long comment_main_id, @RequestParam(defaultValue = "1") int pager,
                                   @RequestParam(defaultValue = "10") int pageSize) {
		/* 记录访问日志 */
		// Map mp=entityService.addHit(tname, entityid);

		// 总的喜欢数
		// 总的收藏数
		// 总的访问数
		UserInfo userInfo = this.getUser(request);
		long userId = 0L;
		String skinResults = "";
		if (userInfo != null) {
			userId = userInfo.getId();
			skinResults = userInfo.getSkinResults();
		}
		if (pager > 0) {
			pager = (pager - 1);
		} else {
			pager = 0;
		}
		ReturnData ret =null;
		if("apply_goods".equals(tname)){
			tname="apply_goods2";
		}
		if (comment_main_id > 0) {
			//一级评论的子评论列表
			ReturnData rd = commentService.findSubComments(tname, id, comment_main_id, userId, pager, pageSize);
			ret= new ReturnData(rd.getResult());
		} else {
			//实体的一级评论列表
			Map map = commentService.findSourceComments(tname, id, userId, pager, pageSize,type,skinResults);
			ret= new ReturnData(map);
		}
		return ret;
	}
	
	/**
	 * 评论点赞+发送被点赞人信息
	 * 
	 * @param request
	 * @param tname
	 * @param commentId
	 * @return
	 */
	@RequestMapping(value = { "/auth/entity/comment/like/{tname}" }, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> like2(HttpServletRequest request, @PathVariable String tname,
                                     @RequestParam long commentId, @RequestParam(defaultValue="0",required=false) int pager,
                                     @RequestParam(defaultValue="20",required=false) int pageSize,
                                     @RequestParam(defaultValue = "0",required=false) int type) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map state = new HashMap();
		UserInfo userInfo = this.getUser(request);
		
		if (userInfo == null) {
			return super.returnLogin();
		}
		if (pager > 0) {
			pager = (pager - 1);
		} else {
			pager = 0;
		}
		ReturnData jb = commentService.sendCommentLike(tname, commentId, userInfo,pager,pageSize,type);
		if (jb == null) {
			return errorAjax();
		} else {
			state.put("type", jb.getRet() == 1 ? 1 : -1);
			return returnAjax(state, 0, "成功");
		}
	}

	/**
	 * 评论举报
	 * 
	 * @param request
	 * @param tname
	 * @param commentId
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = { "/comment/jubao/{tname}" }, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> jubao(HttpServletRequest request, @PathVariable String tname,
                                     @RequestParam long commentId) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (entityService.jubaoComment(tname, commentId)) {
			return defaultAjax();
		} else {
			return errorAjax();
		}
	}

	/**
	 * 评论反垃圾数据接口
	 * @param tname
	 * @return
	 */
	@RequestMapping(value = "/comment/spam/{tname}")
	@ResponseBody
	public ReturnData antiSpamController(@PathVariable String tname){
		try {
			return commentService.commentAntiSpam(tname);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnData.ERROR;
		}
	}

}
