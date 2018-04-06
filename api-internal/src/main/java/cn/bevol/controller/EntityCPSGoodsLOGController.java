package cn.bevol.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.internal.service.EntityCPSGoodsLOGService;
import cn.bevol.util.ReturnData;

@Controller
@RequestMapping("/entityCPSGoodsLOG")
public class EntityCPSGoodsLOGController  extends  BaseController {
    
	
	@Resource
	private EntityCPSGoodsLOGService  entityCPSGoodsLOGService;
	

	 
	/**
	 * 查询CPS表中不存在的商品信息
	 * @param pager 当前页
	 * @param size  每页展示数据
	 * @param hidden  0为展示所有数据,1为只展示CPS商品组中没有的数据
	 * @return
	 */
	@RequestMapping("/lisEntityCPSGoodsLOGlatestData")
	@ResponseBody
	public ReturnData<?>  lisEntityCPSGoodsLOGlatestData(Integer pager,Integer size,Integer state){
		return entityCPSGoodsLOGService.lisEntityCPSGoodsLOGlatestData(pager,size, state);
	}
	
	 
	 
	/**
	 * 根据CPS日志ID获取日志
	 * @param cPSGoodsLOGId
	 * @return
	 */
	@RequestMapping("/getCPSGoodsLOG")
	@ResponseBody
	public ReturnData<?>  getCPSGoodsLOG(Long cPSGoodsLOGId){
		return  new ReturnData<>(entityCPSGoodsLOGService.getEntityCPSGoodsLOGByid(cPSGoodsLOGId),0,"成功");
	}
	
	
	
	
	
	
}
