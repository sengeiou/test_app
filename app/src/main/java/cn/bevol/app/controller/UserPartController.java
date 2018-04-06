package cn.bevol.app.controller;

import cn.bevol.app.service.EntityService;
import cn.bevol.app.service.LoginService;
import cn.bevol.app.service.UserPartService;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
public class UserPartController extends BaseController {
	
	@Resource
	private UserPartService userPartService;
	
	@Resource
	private LoginService loginService;
	
	@Resource
	private EntityService entityService;
	
	
	/**
	 * v2.9
	 * 发心得,支持普通心得和试用心得
	 * @param request
	 * @param tname
	 * @param type 1 心得  2试用
	 * @param tags 标签
	 * @param image banner图片
	 * @param title 标题
	 * @param details  用户发送的内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/user_part/add/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	@Deprecated
	public ReturnData addUserPart(HttpServletRequest request
			, @PathVariable String tname,
								  @RequestParam(defaultValue="1",required=false) Integer type,
								  @RequestParam(defaultValue="",required=false) String tags
			, @RequestParam(required=false) String image,
								  @RequestParam String title,
								  @RequestParam(name="p_entity_id") Long pEntityId,
								  @RequestParam(required=false) String details
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		// 过滤词
		Map map=entityService.Infiltration("notkeyword", title,details);
		if(null!=map){
			if(null!=map.get("title")){
				title=(String)map.get("title");
			}
			if(null!=map.get("details")){
				details=(String)map.get("details");
			}
		}
		
		JSONArray  jasonArray  =null;
		if(StringUtils.isNotBlank(details)){
			org.json.JSONObject  j=new org.json.JSONObject ("  {'type':'ONLINE_SHIPS','details':'"+details+"'}");
			//先通过字符串的方式得到,转义字符自然会被转化掉
			String jsonstrtemp = j.getString("details");
			jasonArray  =JSONArray.fromObject(jsonstrtemp);
		}
		return userPartService.addUserPart(userInfo,tname,type,title,image,tags,pEntityId,jasonArray);
	}   
	
	/**
	 * v3.0
	 * 发心得,支持普通心得和试用心得
	 * 适用于ios(格式问题)
	 * @param request
	 * @param tname
	 * @param type 1 心得  2试用
	 * @param tags 标签
	 * @param image banner图片
	 * @param title 标题
	 * @param details  用户发送的内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/user_part/add2/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	@Deprecated
	public ReturnData addUserPart2(HttpServletRequest request
			,@PathVariable String tname,
			@RequestParam(defaultValue="1",required=false) Integer type,
			@RequestParam(defaultValue="",required=false) String tags
			,@RequestParam(required=false) String image,
			@RequestParam String title,
			@RequestParam(name="p_entity_id") Long pEntityId,
			@RequestParam(required=false) String details
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		// 过滤词
		JSONArray  jasonArray  =this.validContent(title,details);
		return userPartService.addUserPart(userInfo,tname,type,title,image,tags,pEntityId,jasonArray);
	}


	/**
	 * v3.0
	 * 发心得,支持普通心得和试用心得
	 * 适用于ios(格式问题)
	 * @param request
	 * @param tname
	 * @param type 1 心得  2试用
	 * @param tags 标签
	 * @param image banner图片
	 * @param title 标题
	 * @param details  用户发送的内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/user_part/add5/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addUserPart5(HttpServletRequest request
			,@PathVariable String tname,
			@RequestParam(defaultValue="1",required=false) Integer type,
			@RequestParam(defaultValue="",required=false) String tags
			,@RequestParam(required=false) String image,
			@RequestParam String title,
			@RequestParam(name="p_entity_id") Long pEntityId,
			@RequestParam(required=false) String details
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		// 过滤词
		JSONArray  jasonArray  =this.validContent(title,details);
		return userPartService.addUserPart2(userInfo,tname,type,title,image,tags,pEntityId,jasonArray);
	}   

	
	/**
	 * v3.1
	 * 自由发布心得,只支持自由心得
	 * @param request
	 * @param tname: lists
	 * @param type 1 心得  2试用 3自由发布(没有话题)
	 * @param tags 标签
	 * @param image: 封面图
	 * @param title 标题
	 * @param details  用户发送的内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/user_part/add3/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	@Deprecated
	public ReturnData addUserPart3(HttpServletRequest request
			,@PathVariable String tname,
			@RequestParam(defaultValue="3",required=false) Integer type,
			@RequestParam(defaultValue="",required=false) String tags
			,@RequestParam(required=false) String image,
			@RequestParam String title,
			@RequestParam(required=false) String details
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		// 过滤词
		JSONArray  jasonArray  =this.validContent(title,details);
		return userPartService.addUserPart2(userInfo,tname,type,title,image,tags,jasonArray);
	}   
	
	/**
	 * v3.2
	 * 新特性: 实名认证验证
	 * 自由发布心得,只支持自由心得
	 * @param request
	 * @param tname: lists
	 * @param type 1 心得  2试用 3自由发布(没有话题)
	 * @param tags 标签
	 * @param image: 封面图
	 * @param title 标题
	 * @param details  用户发送的内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/user_part/add4/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addUserPart4(HttpServletRequest request
			,@PathVariable String tname,
			@RequestParam(defaultValue="3",required=false) Integer type,
			@RequestParam(defaultValue="",required=false) String tags
			,@RequestParam(required=false) String image,
			@RequestParam String title,
			@RequestParam(required=false) String details
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		// 过滤词
		JSONArray  jasonArray  =this.validContent(title,details);
		return userPartService.addUserPart3(userInfo,tname,type,title,image,tags,jasonArray);
	}

	public JSONArray validContent(String title,String details){
		Map map=entityService.Infiltration("notkeyword", title,details);
		if(null!=map){
			if(null!=map.get("title")){
				title=(String)map.get("title");
			}
			if(null!=map.get("details")){
				details=(String)map.get("details");
			}
		}
		JSONArray  jasonArray  =null;
		if(StringUtils.isNotBlank(details)){
			jasonArray=JSONArray.fromObject(details);
		}
		return jasonArray;
	}
 
	/**
	 * 修改心得
	 * @param request
	 * @param tags: 心得标签
	 * @param image: 封面图
	 * @param title: 标题
	 * @param id: 心得id
	 * @param details: 新的心得内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/user_part/update/lists"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData addUserPart(HttpServletRequest request,
			@RequestParam(defaultValue="",required=false) String tags
			,@RequestParam(required=false) String image,
			@RequestParam String title,
			@RequestParam Long id,
			@RequestParam(required=false) String details
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		// 过滤词
		JSONArray  jasonArray  =this.validContent(title,details);
		return userPartService.updateUserPart(userInfo, id, title, image, tags, jasonArray);
	}   

	
	/**
	 * 修改心得
	 * @param request
	 * @param tags: 心得标签
	 * @param image: 封面图
	 * @param title: 标题
	 * @param id: 心得id
	 * @param details: 新的心得内容
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/user_part/update2/lists"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData updateUserPart(HttpServletRequest request,
			@RequestParam(defaultValue="",required=false) String tags
			,@RequestParam(required=false) String image,
			@RequestParam String title,
			@RequestParam Long id,
			@RequestParam(required=false) String details
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		// 过滤词
		JSONArray  jasonArray  =this.validContent(title,details);
		return userPartService.updateUserPart2(userInfo, id, title, image, tags, jasonArray);
	}  
	/**
	 * 删除用户心得(hidden=2)
	 * @param request
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/user_part/delete"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData hiddenUserpartLists(HttpServletRequest request,
			@RequestParam Long id
			) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return userPartService.hiddenUserpartLists(userInfo, id);
	} 
	/**
	 * 查询某个话题心得列表    
	 * @param request
	 * @param tname: lists
	 * @param p_entity_id: 话题/福利社id
	 * @param sort: 1点击数排序,其它为点击数排序----todo范围控制
	 * @param pager
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/user_part/list/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData findUserPart(HttpServletRequest request
			, @PathVariable String tname
			, @RequestParam Long p_entity_id, @RequestParam(defaultValue = "0") int sort, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize ) throws Exception {
		return userPartService.findUserPart(tname,sort,p_entity_id,pager,pageSize);
	}   
	
	
	/**
	 * 心得详细     
	 * @param request
	 * @param tname: lists
	 * @param id: 心得id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/user_part/detail/{tname}"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData detailUserPart(HttpServletRequest request
			,@PathVariable String tname
			,@RequestParam Long id ) throws Exception {
		return userPartService.getUserPartById(tname,id);
	}

	/**
	 * 获取福利试用报告/话题心得
	 * @param userId
	 * @param nickname
	 * @param entityId
	 * @param id
	 * @param title
	 * @param hidden
	 * @param applyState
	 * @return
	 */
	@RequestMapping(value="/back/user_part/list2/goods", method = RequestMethod.POST)
	@ResponseBody
	public ReturnListData getUserPartList(@RequestParam(required = false) Integer userId,
										  @RequestParam(required = false) String nickname,
										  @RequestParam(required = false) Integer entityId,
										  @RequestParam(required = false) Integer id,
										  @RequestParam(required = false) Integer type,
										  @RequestParam(required = false, defaultValue = "1") Integer page,
										  @RequestParam(required = false, defaultValue = "10") Integer rows,
										  @RequestParam(required = false) String title,
										  @RequestParam(required = false) String sortField,
										  @RequestParam(required = false) Integer hidden,
										  @RequestParam(required = false) Integer applyState){
		return userPartService.listApplyUserPart(userId, nickname, entityId, id, type, title, sortField, applyState, hidden, page, rows);
	}

}
