package cn.bevol.internal.controller;

import cn.bevol.internal.service.BackIndexRecommendService;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import cn.bevol.internal.service.DataCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 *后台榜单管理
 *
 */
@Controller
public class BackDataCategoryController extends BaseController {

    @Autowired
    private DataCategoryService dataCategoryService;


	@Autowired
	private BackIndexRecommendService backIndexRecommendService;
	/**
	 * 新增产品标签和相应的标签规则
     * 添加标签和规则1
     * 
     */
    @RequestMapping("/back/data_category/create")
    @ResponseBody
    public ReturnData tagAdd(HttpServletRequest request,@RequestParam(defaultValue="0",required=false) String title) throws Exception {
        return dataCategoryService.createCategory(title);
    }
    /**
     * 获取分类列表
     * @param request
     * @param pager
     * @param id_1
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/data_category/relation/list")
    @ResponseBody
    public ReturnListData gettagList(HttpServletRequest request
    		,@RequestParam Long pager
    		,@RequestParam Long id_1)
    		throws Exception {
        return dataCategoryService.gettagList(pager,id_1);
    }
    /**
     * 添加/编辑分类信息
     * @param request
     * @param title
     * @param exFeilds
     * @return
     * @throws Exception
     */
    @RequestMapping("/data_category/relation/save_update")
    @ResponseBody
    public ReturnData saveOrUpdate(HttpServletRequest request
    		,@RequestParam(defaultValue="0",required=false)  String title
    		,@RequestParam(defaultValue="0",required=false)  String exFeilds)
    		throws Exception {
    	System.out.println(title+"------"+(String) exFeilds+"====");
        return dataCategoryService.updateCategory(title,exFeilds);
    }
    /**
     * 获取默认数据
     * @param request
     * @param title
     * @param exFeilds
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/data_results/create")
    @ResponseBody
    public ReturnData getdataCategory(HttpServletRequest request
    		,@RequestParam(defaultValue="0",required=false)Long id
    		,@RequestParam(defaultValue="0",required=false)Long brandId)
    		throws Exception {
        return dataCategoryService.getData(id,brandId);
    }
    
    /**
     * 新增/保存数据
     * @param request
     * @param id
     * @param content
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/data_results/save_update")
    @ResponseBody
    public ReturnData saveDataCategory(HttpServletRequest request
    		,@RequestParam(defaultValue="0",required=false)Long id
    		,@RequestParam(defaultValue="0",required=false)String content
    		,@RequestParam(defaultValue="0",required=false)String listName
    		,@RequestParam(defaultValue="0",required=false)Long dataCategoryListsId
    		,@RequestParam(defaultValue="0",required=false)String writerName
    		,@RequestParam(defaultValue="0",required=false)Long brandId )
    		throws Exception {
        return dataCategoryService.saveDataCategory(id,content,listName , dataCategoryListsId,writerName,brandId);
    }
    /**
     * 发布
     * @param request
     * @param id
     * @param publishTime
     * @param state
     * @param endStamp
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/data_results/publish")
    @ResponseBody
    public ReturnData dataResultsPublish(HttpServletRequest request
    		,@RequestParam Long publishTime
    		,@RequestParam Integer state
    		,@RequestParam Long id
    		,@RequestParam Long endStamp)
    		throws Exception {
        return dataCategoryService.publishresults(publishTime,state, id,endStamp);
    }
    
    /**
     * 获取列表
     * @param request
     * @param id
     * @param pager
     * @param state
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/data_results/list")
    @ResponseBody
    public ReturnListData getResults(HttpServletRequest request
    		,@RequestParam Long rid1
    		,@RequestParam Integer pager)
    		throws Exception {
        return dataCategoryService.getResults(rid1, pager);
    }
    
    /**
     * 获取单条
     * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/data_results/one")
    @ResponseBody
    public ReturnData getResult(HttpServletRequest request
    		,@RequestParam Long id)
    		throws Exception {
        return dataCategoryService.getResult(id);
    }
    /**
     * 获取实体信息
     * @param request
     * @param dataCategoryListsId
     * @param pager
     * @param state
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/entity/one/{tname}")
    @ResponseBody
    public ReturnData getEntity(HttpServletRequest request
    		,@RequestParam String tname
    		,@RequestParam Long id)
    		throws Exception {
        return backIndexRecommendService.getEntity(tname, id);
    }
  
 }