package cn.bevol.statics.controller;

import cn.bevol.statics.entity.model.GoodsUserSubmit;
import cn.bevol.statics.service.GoodsUserSubmitService;
import cn.bevol.statics.service.StaticGoodsService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Controller
public class GoodsController {

	@Resource
	private StaticGoodsService staticGoodsService;
	@Resource
	private GoodsUserSubmitService goodsUserSubmitService;


	@RequestMapping(value = {"/static/goods/pages"})
	@ResponseBody
	public ReturnData staticAllGoodsPage(){
		return staticGoodsService.initStatic(3);
	}

	/**
	 * 静态化产品
	 * @param session
	 * @param request
	 * @param mid
	 * @return
	 */
	@RequestMapping(value = {"/static/goods/{mid}"})
	@ResponseBody
	public Object goodsStaticByMid(HttpSession session, HttpServletRequest request,
                                   @PathVariable String mid){
		return new ReturnData(staticGoodsService.goodsStatic(mid));
	}

	/**
	 * 批量静态化
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = {"/static/goods/batch"})
	@ResponseBody
	public Object batchGoodsStatic(@RequestParam() String ids){
		return new ReturnData(staticGoodsService.batchGoodsStatic(ids));
	}

	/***
	 * 图片上传
	 * @param request
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value={"/static/auth/upfile"},method={RequestMethod.POST})
	@ResponseBody
	public Object upfiledir2(HttpServletRequest request, @RequestParam(required=false) MultipartFile file) throws IOException {
		return goodsUserSubmitService.upFile(file);
	}

	/***
	 * 用户添加商品
	 * @return
	 */
	@RequestMapping(value = {"/static/goods/user_submit/add"})
	@ResponseBody
	public Object insertGoods(GoodsUserSubmit goodsUserSubmit){
		return new ReturnData(goodsUserSubmitService.submitProduct(goodsUserSubmit));
	}

	/***
	 * 修改商品状态
	 * @param request
	 * @param state
	 * @return
	 */
	@RequestMapping(value = {"/static/goods/user_submit/update"})
	@ResponseBody
	public Object updateGoodsOfState(HttpServletRequest request,
                                     @RequestParam() String ids,
                                     @RequestParam( defaultValue = "0") Integer state){
		return new ReturnData(goodsUserSubmitService.bathUpdateGoodsOfState(ids,state));
	}

	/**
	 * 用户提交产品列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = {"/static/goods/user_submit/list"})
	@ResponseBody
	public Object findUserGoods(HttpServletRequest request,
                                @RequestParam(required=false) Integer state,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "") String name){
		return new ReturnData(goodsUserSubmitService.findByPage(state,name,page));
	}
	@RequestMapping(value = {"/static/goods/list/title"})
	@ResponseBody
	public Object findGoodsByName(HttpServletRequest request,
                                  @RequestParam String title){
		return new ReturnData(staticGoodsService.findByName(title));
	}

}
