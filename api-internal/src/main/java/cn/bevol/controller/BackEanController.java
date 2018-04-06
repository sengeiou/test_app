package cn.bevol.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.internal.BackGoodsEanService;
import cn.bevol.util.ReturnData;

@Controller
public class BackEanController extends BaseController{
	
	@Autowired
	private BackGoodsEanService backGoodsEanService;
	/**
	 * 用户提交产品审核
	 * @param request
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ={ "ean/check/goods"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData checkGoods(HttpServletRequest request
			,@RequestParam(defaultValue="0",required=false) Long id
			,@RequestParam(defaultValue="0",required=false) String message
			,@RequestParam(defaultValue="0",required=false) Integer isPass) throws Exception{
		return backGoodsEanService.CheckGoods(id,message,isPass);
	}
	/**
	 * 产品审核列表
	 * @param request
	 * @param pager
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ={ "ean/goods/list"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData getListOfCheck(HttpServletRequest request
			,@RequestParam(defaultValue="0",required=false) int pager
			,@RequestParam(defaultValue="0",required=false) Long userId
			,@RequestParam(defaultValue="0",required=false) int source
			,@RequestParam(defaultValue="0",required=false) int id
			,@RequestParam(defaultValue="3",required=false) int isPass	
			) throws Exception{
		return backGoodsEanService.getCheckGoodsList(pager,userId,source,id,isPass);
	}
	
	//产品条形码入库
	@RequestMapping(value ={ "ean/goods/connection"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData goodsToUpc(HttpServletRequest request
			,@RequestParam(defaultValue="0",required=false) String ean
			,@RequestParam(defaultValue="0",required=false) Long goodsId
			,@RequestParam(defaultValue="0",required=false) Long id
			) throws Exception{
		return backGoodsEanService.goodsToUpc(ean,goodsId,id);
	}
	
	/*
	 * 获取扫码记录列表
	 */
	@RequestMapping(value ={ "ean/record/list"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData getRecordEanList(HttpServletRequest request
			,@RequestParam(defaultValue="0",required=false) int pager
			,@RequestParam(defaultValue="3",required=false) int isPass
			,@RequestParam(defaultValue="0",required=false) Long beginStamp
			,@RequestParam(defaultValue="0",required=false) Long endStamp
			,@RequestParam(defaultValue="0",required=false) String  ean
			,@RequestParam(defaultValue="0",required=false) int  source
			,@RequestParam(defaultValue="0",required=false) int pagerSize
			)throws Exception{
		return backGoodsEanService.getRecordList(isPass,beginStamp,endStamp,ean,pager,source,pagerSize);
	}
	
	/**
	 * 获得产品实体
	 * @param request
	 * @param goodsId 产品ID
	 * @param source 数据来源 1 正式库  2 预备库
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ={ "ean/record/info"}, method = {RequestMethod.POST})
	@ResponseBody
	public ReturnData getRecordEanInfo(HttpServletRequest request
			,@RequestParam(defaultValue="0",required=false) Long goodsId
			,@RequestParam(defaultValue="0",required=false) int  source
			)throws Exception{
		return backGoodsEanService.getInfoById(goodsId,source);
	}
	
}
