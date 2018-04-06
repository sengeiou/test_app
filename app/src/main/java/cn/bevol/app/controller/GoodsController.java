package cn.bevol.app.controller;

import cn.bevol.app.service.GoodsService;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GoodsController extends BaseController{
	@Autowired
    private GoodsService goodsService;

	

    /**
     * move to internal project
     * 产品相关的业务(单个)
     * @param request
     * @param type:  skin effect category safety tag
     * @param ids
     * @param update 0 不更新 1 更新
     * @return

    * @throws Exception
     */
    @RequestMapping("/goods/{type}")
    @ResponseBody
    @Deprecated
    public ReturnListData skin(HttpServletRequest request, @RequestParam String ids, @RequestParam(defaultValue="0",required=false) int update, @PathVariable String type) throws Exception {
    	UserInfo userInfo=this.getUser(request);
    	return goodsService.goodsCalculate(ids, update, type,userInfo);
    }
      
    /**
     * move to internal project
     * 产品相关的业务(多个)
     * @param request
     * @param type:  skin effect category safety
     * @param ids
     * @param update 0 不更新 1 更新
     * @return
     * @throws Exception
     */
    @RequestMapping("/goods/info")
    @ResponseBody
    @Deprecated
    public ReturnListData info(HttpServletRequest request, @RequestParam String ids, @RequestParam(defaultValue="0",required=false) int update, @RequestParam(defaultValue="",required=false) String type) throws Exception {
    	ReturnListData d=goodsService.goodsCalculate(this.getUser(request),ids, update);
    	if(StringUtils.isBlank(type)) {
    		return ReturnListData.SUCCESS;
    	}
    	return d;
    }
    
    /**
     * 本地缓存
     * 产品计算相关的数据先缓存
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/goods/cache")
    @ResponseBody
    public ReturnData goodsCache(HttpServletRequest request) throws Exception {
    	 goodsService.goodsCache();
    	return ReturnData.SUCCESS;
    }
    
    /**
     * 临时操作
     * 更新hq_goods_search
     * type 是否清空临时表 1清除
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/goods/search/update")
    @ResponseBody
    @Deprecated
    public ReturnData searchUpdate(HttpServletRequest request,@RequestParam(defaultValue="0") int type) throws Exception {
    	ReturnData d=goodsService.goodsSearchUpdate(type);
    	return d;
    }
    
    /**
     * 一次性接口
     * 更新hq_goods_search
     * 为p_category填充值
     */
    @RequestMapping("/goods/search/update/pCategory")
    @ResponseBody
    @Deprecated
    public ReturnData searchPcategory(HttpServletRequest request) throws Exception {
    	ReturnData d=goodsService.goodsSearchPcategory();
    	return d;
    }
}
