package cn.bevol.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.internal.service.EntityCPSGoodsGroupService;
import cn.bevol.model.entity.*;
import cn.bevol.util.ReturnData;

/**
 * cps商品组 相关接口
 * @author zqc
 *
 */
@Controller
@RequestMapping("cpsGroup")
public class CpsGroupController  {
     
	 
	@Resource
	private EntityCPSGoodsGroupService  goodsGroupService;
	
	/**
	 * 获取cps 商品组信息
	 * @param pager       当前页
	 * @param size        每页展示数据
	 * @param selectCheck 查询条件,暂时为空 
	 * @return
	 */
	@RequestMapping(value="getcpsGroupAll",method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData<?>  getCpsGroupAll(Integer pager,Integer size, String selectCheck) {
		return goodsGroupService.listEntityCPSGoodsGroup(pager,size,selectCheck);
	}
	
 
	/**
	 * 添加 cps商品组
	 * @param goodsGroup  cps 商品组    参数goodsName:商品组名称 ，username添加人,favoritesId淘宝客联盟id
	 * @param goodsGroup
	 * @return
	 */
	@RequestMapping(value="addCpsGroup",method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData<?>  addCpsGroup(EntityCPSGoodsGroup goodsGroup ) {
		return goodsGroupService.insertEntityCPSGoodsGroup(goodsGroup);
	}
	
	
	
	
	
}
