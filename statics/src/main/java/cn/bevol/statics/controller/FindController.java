package cn.bevol.statics.controller;

import cn.bevol.statics.service.StaticFindService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class FindController {
	//tmp
	@Resource
	private StaticFindService staticFindService;

	/**
	 * 静态化发现
	 * @param session
	 * @param request
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = {"/static/find/{id}"})
	@ResponseBody
	public Object searchWorkByName(HttpSession session, HttpServletRequest request,
                                   @PathVariable String id) throws IOException {
		return new ReturnData(staticFindService.staticFindPage(id));
	}

	/**
	 * 静态化行业资讯
	 * @param session
	 * @param request
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = {"/static/industry/{id}"})
	@ResponseBody
	public Object searchWorkByName1(HttpSession session, HttpServletRequest request,
                                    @PathVariable String id) throws IOException {
		return new ReturnData(staticFindService.staticIndustryPage(id));
	}

	/**
	 * 静态化发现全部文章
	 * @param session
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = {"/static/find/all"})
	@ResponseBody
	public Object searchWorkByName2(HttpSession session, HttpServletRequest request) throws IOException {
		return new ReturnData(staticFindService.initFindStatic(1));
	}

}
