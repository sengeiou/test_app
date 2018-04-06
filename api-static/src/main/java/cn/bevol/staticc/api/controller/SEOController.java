package cn.bevol.staticc.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.bevol.entity.service.SeoAwaitService;
import cn.bevol.entity.service.SeoService;
import cn.bevol.util.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bevol.web.response.ResponseBuilder;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping({"/static", "/"})
public class SEOController {
	@Resource
	private SeoService seoService;
	@Resource
	private SeoAwaitService seoAwaitSerivce;
	/***
	 * *
	 * @param session
	 * @param request
	 * @param session
	 * @param request
	 * @param mid
	 * @param url 需要推送的url带http的全路径
	 * @param dataType pc
	 * @param dataSource find goods ...
	 * @param operateType add update ...
	 * @return
	 */
	@RequestMapping(value = {"/seo/add"})
	@ResponseBody
	public Object seoAdd(HttpSession session, HttpServletRequest request,
						 @RequestParam(defaultValue="") String mid,
						 @RequestParam(defaultValue="") String url,
						 @RequestParam(defaultValue="") String dataType,
						 @RequestParam(defaultValue="") String dataSource,
						 @RequestParam(defaultValue="") String operateType){
		return ResponseBuilder.buildResult(seoAwaitSerivce.insertOrupdate(mid, url, dataType, dataSource, operateType));
	}

	/**
	 * 需要seo推送的产品
	 * @param mid
	 * @return
	 */
	@RequestMapping(value= {"/seo/product/add"})
	@ResponseBody
	public ReturnData seoProductAdd(@RequestParam String mid){
		return seoAwaitSerivce.insertOrupdatePoduct(mid, "add");
	}

	/**
	 * 需要seo推送的产品
	 * @param mid
	 * @return
	 */
	@RequestMapping(value= {"/seo/composition/add"})
	@ResponseBody
	public ReturnData seoCompositionAdd(@RequestParam String mid){
		return seoAwaitSerivce.insertOrupdateComposition(mid, "add");
	}

	/***
	 *
	 * @param session
	 * @param request
	 * @param dataType
	 * @param dataSource
	 * @param operateType
	 * @param state
	 * @param beginTime
	 * @param endTime
     * @param page
     * @return
     */
	@RequestMapping(value = {"/static/seo/info"})
	@ResponseBody
	public Object findByPage(HttpSession session, HttpServletRequest request,
						 @RequestParam(defaultValue="",required = false) String dataType,
						 @RequestParam(defaultValue="",required = false) String dataSource,
						 @RequestParam(defaultValue="",required = false) String operateType,
						 @RequestParam(required = false) String state,
						 @RequestParam(defaultValue="",required = false) String beginTime,
						 @RequestParam(defaultValue="",required = false) String endTime,
						 @RequestParam(defaultValue="1") int page){
		return ResponseBuilder.buildResult(seoAwaitSerivce.findByPage(dataType, dataSource, operateType, state,beginTime,endTime, page));
	}
	
	
	/***
	 * 每天定时推送新记录的产品或成分
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = {"/static/seo/init"})
	@ResponseBody
	public Object seoAddJob(HttpSession session, HttpServletRequest request	){
		return ResponseBuilder.buildResult( seoAwaitSerivce.addSeoBatchJob());
	}

	@RequestMapping(value = {"static/seo/batch_add"})
	@ResponseBody
	public Object seoAddByDate(HttpSession session, HttpServletRequest request,
								  @RequestParam(defaultValue="") Integer beginTime,
								  @RequestParam(defaultValue="") Integer endTime){
		return ResponseBuilder.buildResult( seoAwaitSerivce.addSeoBatchJob(beginTime,endTime));
	}

	/**
	 * 360 seo 基础xml
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@RequestMapping(value = {"seo/360/base"})
	@ResponseBody
	public Object seoGenerateBaseInfo() throws IOException, ParseException {
		return ResponseBuilder.buildResult(seoService.build360BaseSiteMap());
	}

	/**
	 * 360 seo 文章xml
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@RequestMapping(value = {"seo/360/article"})
	@ResponseBody
	public Object seoGenerateArticleInfo() throws IOException, ParseException {
		return ResponseBuilder.buildResult(seoService.build360ArticleSiteMap());
	}

	@RequestMapping(value = {"seo/360/composition"})
	@ResponseBody
	public Object seoGenerateCompositionInfo() throws IOException, ParseException {
		return ResponseBuilder.buildResult(seoService.build360CompositionSiteMap());
	}

    @RequestMapping(value = {"seo/360/product"})
    @ResponseBody
	public Object seoGenerateProductInfo() throws InterruptedException {
	    return ResponseBuilder.buildResult(seoService.build360AllProductSiteMap());
    }

	@RequestMapping(value = {"seo/360/latest/product"})
	@ResponseBody
	public Object seoGenerateLatestProductInfo() throws InterruptedException {
		return ResponseBuilder.buildResult(seoService.build360LatestProductSiteMap());
	}

	@RequestMapping(value = {"seo/360/index"})
	@ResponseBody
	public Object seoGenerateIndexInfo() throws IOException, InterruptedException {
		return ResponseBuilder.buildResult(seoService.build360SiteMapIndex());
	}
}
