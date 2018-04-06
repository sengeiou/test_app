package cn.bevol.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.internal.service.TaoBaoKeService;
import cn.bevol.util.ReturnData;

/**
 * 淘宝联盟商品id相关
 * @author Administrator
 *
 */
@Controller
@RequestMapping("taoBaioKeAlliance")
public class TaoBaioKeAllianceController {
   
	    
	 
	@Resource
	private TaoBaoKeService  taobaoKeService;
   
 
	
	/**
	 * 数据同步
	 * @return
	 */
	@RequestMapping("/refreshCps")
	@ResponseBody
	public ReturnData<?>   dome(){
		//刷新同步数据
		try {
			taobaoKeService.refreshCps();
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnData.ERROR;
		}
		return ReturnData.SUCCESS;
	}
	  
	
}
