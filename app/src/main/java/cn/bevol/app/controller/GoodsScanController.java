package cn.bevol.app.controller;

import cn.bevol.app.service.GoodsScanService;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.response.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GoodsScanController extends BaseController {
	@Autowired
    private GoodsScanService goodsScanService;
	
	/*
	 * 扫码产品
	 */
	@RequestMapping("ean/goods")
    @ResponseBody
    public ReturnData goodsScan(HttpServletRequest request
    		, @RequestParam(defaultValue="0",required=false) String ean)throws Exception {
		return goodsScanService.selectGoodsByBarcode(ean);
	}
	/**
	 *产品录入
	 */
	@RequestMapping("ean/goods/save")
    @ResponseBody
    public ReturnData saveToMongo(HttpServletRequest request
    		,@RequestParam(defaultValue="0") String eanImg
    		,@RequestParam(defaultValue="0") String ean
    		,@RequestParam(defaultValue="0") String cpsImg
    		,@RequestParam(defaultValue="0") String title
    		,@RequestParam(defaultValue="0") String goodsImg
    		,@RequestParam(defaultValue="0") Integer source
    		,@RequestParam(defaultValue="0") String alias
    		,@RequestParam(defaultValue="0") Long recordId
    		,@RequestParam(defaultValue="0") Long id)throws Exception {
		UserInfo userInfo = this.getUser(request);
		long userId = 0L;
		if(userInfo!=null){
			userId = userInfo.getId();
		}
		return goodsScanService.saveToMongo(eanImg,ean,cpsImg,source,goodsImg,title,alias,userId,id,recordId);
	}
}
