package cn.bevol.staticc.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.bevol.entity.service.StaticFindService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bevol.web.response.ResponseBuilder;

import java.io.IOException;

@Controller
@RequestMapping({"/static", "/"})
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
		return ResponseBuilder.buildResult(staticFindService.staticFindPage(id));
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
		return ResponseBuilder.buildResult(staticFindService.staticIndustryPage(id));
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
		return ResponseBuilder.buildResult(staticFindService.initFindStatic(1));
	}

}
