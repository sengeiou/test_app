package cn.bevol.app.controller;


import cn.bevol.app.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class SearchController extends BaseController {

	@Resource
	private SearchService searchService;

	/**
	 * 搜获列表排除成分
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/seach/ruleoutcomposition")
	@ResponseBody
	public Map ruleOutComposition(HttpServletRequest request) throws Exception {
		Map map=new HashMap();
		map=searchService.ruleOutComposition();
		if(null!=map){
			return  map;
		}
		return errorAjax();
	}

	/**
	 * 搜获列表获取产品
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/seach/ruleoutgoods")
	@ResponseBody
	public List ruleOutGoods(HttpServletRequest request) throws Exception {
		List list=new ArrayList();
		list=searchService.ruleOutGoods();
		if(null!=list){
			return  list;
		}
		return new ArrayList();
	}
	
}