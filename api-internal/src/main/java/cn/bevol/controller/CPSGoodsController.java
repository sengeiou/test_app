package cn.bevol.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.fabric.xmlrpc.base.Data;

import cn.bevol.internal.service.EntityCPSGoodsService;
import cn.bevol.internal.service.InternalAdCpsService;
import cn.bevol.internal.service.TaoBaoKeService;
import cn.bevol.util.ReturnData;

/**
 * CPS商品相关接口
 * @author zqc
 *
 */
@Controller
@RequestMapping("/cps")
public class CPSGoodsController {

	
	@Resource
	private TaoBaoKeService          taoBaoKeService;
	
	@Resource
	private EntityCPSGoodsService     entityCPSGoodsService;
	//EntityCPSGoodsLOG
 
    @Resource
	private InternalAdCpsService internalAdCpsService;
	    
	
	/**
	 * 获取全部的CPS数据
	 * @param pager       当期页
	 * @param size        每页展示数据
	 * @param selectCheck 查询条件json 
	 * @return
	 */
	@RequestMapping(value="/getCPSAll",method = { RequestMethod.POST })
	@ResponseBody
	public  ReturnData<?>  getCPSAll(Integer pager,Integer size, String  selectCheck) {
		return   entityCPSGoodsService.listEntityAdCps(pager,size,null);
	}
	 
    /**
     * 添加cps
	 * @param entityCPSGoodsLOGId   CPS日志的ID 
	 * @param title                 被添加的CPS商品名称
	 * @param isCommission          是否为高佣金
	 * @param channelType           渠道类型
	 * @param channelCommissionType 渠道佣金形式  1.百分比  2.定额
	 * @param channelCommission     渠道佣金
	 * @param channelStartTime      佣金开始时间
	 * @param channelEndTime        佣金结束时间
	 * @param creator               创建人
	 * @param shopIds               多个店铺
	 * @param goodsid               美修产品Id
	 * @return 保存数据的结果
	 */
	@RequestMapping(value="/addCps",method = { RequestMethod.POST })
	@ResponseBody
	public  ReturnData<?> addCps(Long  entityCPSGoodsLOGId ,  String  title 
			,Integer  isCommission,Integer channelType ,Integer channelCommissionType,String channelCommission
			,Long  channelStartTime,Long channelEndTime,String creator,String shopIds,Long goodsid) {
		return entityCPSGoodsService.insertAdCps(entityCPSGoodsLOGId, title, isCommission, channelType
				, channelCommissionType, channelCommission, channelStartTime, channelEndTime, creator,shopIds,goodsid);
	}
	
 
	
	/**
	 * 根据CPS的id 获取CPS的信息
	 * @param cpsId  CPS的ID
	 * @return  查询到的结果
	 */
	@RequestMapping(value ="/getCps/{cpsId}",method = { RequestMethod.POST })
	@ResponseBody
	public    ReturnData<?>    getCps(@PathVariable("cpsId") Long   cpsId) {
		return entityCPSGoodsService.getCpsByid(cpsId);
	}
	
	/**
	 * 修改     CPS信息
	 * @param cpsId                  被修改的CPS编号
	 * @param title                  商品名称
	 * @param isCommission           是否为高佣金
	 * @param channelCommissionType  渠道佣金形式  1.百分比  2.定额
	 * @param channelCommission      渠道佣金
	 * @param channelStartTime       开始时间
	 * @param channelEndTime         结束时间
	 * @param updater                修改人
	 * @param shopIds                店铺id
	 * @param entityAdCpsChannelId   商品渠道Id
	 * @throws Exception 
	 * @return  结果
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/updCps",method = { RequestMethod.POST })
	@ResponseBody
    public  ReturnData updCps(Long cpsId
    				,String title  ,Integer  isCommission   
    				,Integer channelCommissionType ,String channelCommissio
    				,Long  channelStartTime,Long channelEndTime
    				,String updater,String shopIds  ,Long entityAdCpsChannelId,Long goodsId) throws Exception {
		return entityCPSGoodsService.updateAdCps(cpsId, title, isCommission, channelCommissionType, channelCommissio
				, channelStartTime, channelEndTime, updater, shopIds, entityAdCpsChannelId,goodsId);
	
	}
	
	/**
	 * 
	 * 隐藏cps
	 * CPS 商品隐藏
       	不删除，只做隐藏操作(隐藏只是前端隐藏,后台模块仍然可以查看)
	 * @param cpsId cps的id主键
	 * @param state
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/cpsHidden",method = { RequestMethod.POST })
	@ResponseBody
	public ReturnData hideCps(Long cpsId,Integer state) {
		return entityCPSGoodsService.hidden(cpsId,state);
	}
	
}
