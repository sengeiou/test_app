package cn.bevol.app.controller;

import cn.bevol.model.user.UserInfo;
import cn.bevol.app.service.ApplyGoods2Service;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 福利社2
 *
 */
@Controller
public class ApplyGoods2Controller extends BaseController {

    @Autowired
    private ApplyGoods2Service applyGoods2Service;


    /**
     * 福利社全部活动/文章列表
     * @param request
     * @param pager
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/apply_goods2/list"},method={RequestMethod.POST})
    @ResponseBody
    public ReturnListData list(HttpServletRequest request, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
    	UserInfo userInfo=this.getUser(request);
    	Long userId=0L;
    	if(null!=userInfo && userInfo.getId()>0){
    		userId=userInfo.getId();
    	}
    	return applyGoods2Service.findApplyGoodslist(userId,pager, pageSize);
    }
    
	
 	
	/**
	 * 用户申请试用
	 * @param id: 活动id
	 * @return
	 * @throws Exception
	 */      
	@RequestMapping(value={"/auth/apply_goods2/used"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData applyUsed(HttpServletRequest request, @RequestParam Long id) throws Exception {
		UserInfo userInfo=this.getUser(request);
		return applyGoods2Service.applyUsed(userInfo,id);
	}   
	
	
	/**
	 * 申请人列表
	 * 
	 * @id 试用id
	 * @state =1 申请人 2中奖的人
	 * @throws Exception
	 * 
	 * 
	 */      
	@RequestMapping(value={"/apply_goods2/user/list"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData applyUser(HttpServletRequest request, @RequestParam Long id, @RequestParam(defaultValue = "1",required=false) int state, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize ) throws Exception {
		return applyGoods2Service.findApplyUserList(id, null, state,null,0,0,0,0,0,pager,pageSize);
	}   
	

	
	/**
	 * 报告列表
	 * 
	 * @id 试用id
	 * 
	 * @throws Exception
	 * 
	 * 
	 */      
	@RequestMapping(value={"/apply_goods2/user_part/list"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnListData userPartList(HttpServletRequest request, @RequestParam Long id, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize ) throws Exception {
		return applyGoods2Service.findApplyGoods(id,pager,pageSize);
	}   
	
	
	/**
	 * 申请理由
	 * @id 试用id
	 * @content 理由
	 * @images 图片
	 * @throws Exception
	 * 
	 * 
	 */      
	@RequestMapping(value={"/auth/apply_goods2/reason"},method={RequestMethod.POST})
	@ResponseBody
	public ReturnData userPartList(HttpServletRequest request, @RequestParam Long id, @RequestParam String content, @RequestParam(required=false) String images) throws Exception {
		return  applyGoods2Service.applyReason(this.getUserId2(request),id,content,images,true);
	}   
    
    
    
 }