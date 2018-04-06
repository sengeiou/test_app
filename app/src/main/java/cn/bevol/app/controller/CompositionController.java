package cn.bevol.app.controller;

import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.service.CompositionService;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CompositionController extends BaseController{
	@Autowired
    private CompositionService compositionService;

	

    /**
     * 中英文符号转换
     * @param request
     * @param names  成分以逗号隔开
     * @return
     * @throws Exception
     */
    @RequestMapping("/composition/compares")
    @ResponseBody
    public ReturnListData<Composition> compares(HttpServletRequest request,@RequestParam String names) throws Exception {
    	return compositionService.compares(names);
    }

    
    /**
     * 字段过滤 成分匹配
     * @param request
     * @param names  成分以逗号隔开
     * @return
     * @throws Exception
     */
    @RequestMapping("/composition/compares2")
    @ResponseBody
    public ReturnListData<Composition> compares(HttpServletRequest request, @RequestParam("names[]") List<String> names) throws Exception {
    	return compositionService.compares(names);
    }

    /**
     * 成分名称清洗
     * @param request
     * @param names 
     * @return
     * @throws Exception
     */
    @RequestMapping("/composition/clean_mark/name")
    @ResponseBody
    public ReturnListData<Composition> cleanNarNname(HttpServletRequest request,@RequestParam String names) throws Exception {
    	return compositionService.cleanMarkNames(names);
    }

    /**
     * 获取成分基本信息
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/composition/info/{mid}")
    @ResponseBody
    public ReturnData compositionmid(HttpServletRequest request,@PathVariable String mid) throws Exception {
    	Composition c= compositionService.getCompositionByMid(mid);
    	return new ReturnData(c);
    }
    /**
     * 获取成分详细信息
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/composition/detail/{mid}")
    @ResponseBody
    public ReturnData compositionmid(HttpServletRequest request, @PathVariable String mid, @RequestParam(defaultValue="1",required=false) int pager, @RequestParam(defaultValue="10",required=false) int pageSize) throws Exception {
    	ReturnData c= compositionService.getCompositionDetail(mid,pager,pageSize);
    	return c;
    }

    

}
