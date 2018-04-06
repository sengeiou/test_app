package cn.bevol.controller;

import cn.bevol.internal.service.InternalGoodsCalculateService;
import cn.bevol.internal.service.InternalGoodsService;
import cn.bevol.mybatis.dto.GoodsDTO;
import cn.bevol.mybatis.model.GoodsExt;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import com.bevol.web.response.ResponseBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by mysens on 17-4-14.
 */
@Controller
public class BackGoodsController {
    
    @Resource
    private InternalGoodsService internalBackGoodsService;

    @Resource
    private InternalGoodsCalculateService backGoodsCalculateService;

    /**
     * 添加新产品
     * @param goodsDTO
     * @return
     */
    @RequestMapping(value="/back/add_new_goods")
    @ResponseBody
    public ReturnData addNewGoods(GoodsDTO goodsDTO){
        return internalBackGoodsService.addNewGoods(goodsDTO);
    }

    /**
     * 修改单个产品
     * @param goodsDTO
     * @return
     */
    @RequestMapping(value="/back/save_goods_info")
    @ResponseBody
    public ReturnData saveGoodsInfo(GoodsDTO goodsDTO){
        return internalBackGoodsService.saveBaseGoodsInfo(goodsDTO);
    }

    /**
     * 产品成分表变更
     * @param goodsExt
     * @return
     */
    @RequestMapping(value="/back/save_goods_cps")
    @ResponseBody
    public ReturnData saveGoodsCps(GoodsExt goodsExt){
        return internalBackGoodsService.saveGoodsExtCps(goodsExt);
    }

    /**
     * 批量修改产品
     * @param goodsDTO
     * @param ids
     * @return
     */
    @RequestMapping(value="/back/save_goods_list")
    @ResponseBody
    public ReturnData saveGoodsList(GoodsDTO goodsDTO, String ids){
        return internalBackGoodsService.saveBaseGoodsList(goodsDTO, ids);
    }

    @RequestMapping(value = "/static/goods/list/title")
    @ResponseBody
    public Object findGoodsByName(HttpServletRequest request,
                                  @RequestParam String title){
        return ResponseBuilder.buildResult(internalBackGoodsService.findByName(title));
    }

    /**
     * 产品相关的业务(多个)  使用调度程序代替
     * @param request
     * @param type  skin effect category safety
     * @param ids
     * @param update 0 不更新 1 更新
     * @return
     * @throws Exception
     */
    @RequestMapping("/goods/info")
    @ResponseBody
    @Deprecated
    public ReturnListData info(HttpServletRequest request,
                               @RequestParam String ids,
                               @RequestParam(defaultValue="0",required=false) int update,
                               @RequestParam(defaultValue="",required=false) String type) throws Exception {
        ReturnListData d=backGoodsCalculateService.goodsCalculate(ids, update);
        if(StringUtils.isBlank(type)) {
            return ReturnListData.SUCCESS;
        }
        return d;
    }

    /**
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
    public ReturnListData skin(HttpServletRequest request,
                               @RequestParam String ids,
                               @RequestParam(defaultValue="0",required=false) int update,
                               @PathVariable String type) throws Exception {
        return backGoodsCalculateService.goodsCalculate(ids, update, type);
    }

    /**
     * 产品计算相关的数据先缓存
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/goods/category/cache")
    @ResponseBody
    public ReturnData goodsCache(HttpServletRequest request) throws Exception {
        if(InternalGoodsCalculateService.islocalcache){
            backGoodsCalculateService.goodsCalculateLocalCache();
        }else{
            backGoodsCalculateService.goodsCalculateRedisCache();
        }
        return ReturnData.SUCCESS;
    }


    /**
     * 单个产品计算并静态化
     * @param id
     * @return
     */
    @RequestMapping("/goods/calculate")
    @ResponseBody
    public ReturnListData calculateSingleGoods(@RequestParam Integer id){
        ReturnListData returnListData = internalBackGoodsService.calculateGoodsInfo(id);
        internalBackGoodsService.goodsStatic(id);
        return returnListData;
    }
}
