package cn.bevol.app.controller;

import cn.bevol.app.service.DataCategoryService;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 *榜单
 *
 */
@Controller
public class DataCategoryController extends BaseController {

    @Autowired
    private DataCategoryService dataCategoryService;

	/**
	 * 肤质榜单
	 * @param request
	 * @param type: 0油性 1干性 2敏感性
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("/data_category/skin/{tname}")
    @ResponseBody
    public ReturnData goodsSkinList(HttpServletRequest request, @PathVariable String tname, @RequestParam(defaultValue="0",required=false) Integer type
    		, @RequestParam(defaultValue="10",required=false) Integer pageSize, @RequestParam(defaultValue="0",required=false) Integer pager) throws Exception {
        return dataCategoryService.goodsSkinList(type,tname,pageSize,pager);
    }
    
	/**
	 * 榜单数据列表
	 * @param request
	 * @param type: 0油性 1干性 2敏感性
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("/data_category/list")
    @ResponseBody
    public ReturnData getListDataGory(HttpServletRequest request, @RequestParam(defaultValue="10",required=false) Integer pageSize,
    		@RequestParam(defaultValue="0",required=false) Long dataCategoryListsId,
    		@RequestParam(defaultValue="0",required=false) Integer pager) throws Exception {
        return dataCategoryService.getDataGoryList(dataCategoryListsId,pageSize,pager);
    }
   
 }