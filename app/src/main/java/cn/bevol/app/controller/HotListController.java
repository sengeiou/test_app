package cn.bevol.app.controller;

import cn.bevol.app.service.EntityService;
import cn.bevol.app.service.HotListService;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
public class HotListController extends BaseController {

    @Autowired
    private HotListService hotListService;

    @Autowired
    private EntityService entityService;

    /**
     * 热门话题/清单详情
     * 五分钟缓存
     * @param id: 清单id
     * @return
     * @throws Exception
     */
    @RequestMapping("/hotlist/detail")
    @ResponseBody
    public ReturnData detailContent(HttpServletRequest request, @RequestParam Integer id) throws Exception {
        long userId=this.getUserId(request);
        return hotListService.detailContent(id,userId); 
    }
    
    /**
     * ios测试接口
     *
     * @param request
     * @return
     * @throws Exception
     */
    @Deprecated
    @RequestMapping("/hotlist/detail2")
    @ResponseBody
    public ReturnData detailContent2(HttpServletRequest request,@RequestParam Integer id) throws Exception {
        return detailContent(request,id); 
    }
    

    /**
     * 热门清单列表页
     *
     * @param request
     * @param pager
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Deprecated
    @RequestMapping("/hotlist/list")
    @ResponseBody
    public ReturnListData list(HttpServletRequest request, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
        if (pageSize > 10){
        	pageSize = 20;
        }
        if(pageSize==5){
        	pageSize=6;
        }
        return hotListService.moreArticle(pager, pageSize);
    }
    
    
    /**
     * v3.1
     * 话题/清单/试用列表
     * 走openSearch,没有缓存
     * @param request
     * @param listsType 
     * 				1 话题 2试用
     * @param pager
     * @param pageSize
     * @return
     * @throws Exception
     */
     @RequestMapping("/hotlist/list2")
    @ResponseBody
    public ReturnListData partList(HttpServletRequest request, @RequestParam(defaultValue = "1",name="lists_type") int listsType, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
        if (pageSize > 10){
        	pageSize = 20;
        }
        if(pageSize==5){
        	pageSize=6;
        }
        return hotListService.findLists(listsType,pager, pageSize);
    }
    
    
    
    
 }