package cn.bevol.app.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.app.service.CpsService;
import cn.bevol.util.response.ReturnData;


@Controller
@RequestMapping("/cps")
public class CpsController {
    
	
	
	@Resource   
	private CpsService     entityCPSGoodsService;
	
	
	   
    /**
     * 保存用户点击次数
     * @param uniqueId     CPS 唯一Id
     * @param channelType  渠道类型
     * @param systemType   系统类型  1安卓 2苹果
     * @return 是否保存成功!
     */
  @RequestMapping(value="/click")
  @ResponseBody
    public ReturnData<?> clickCpsLink(Long  cpsId,Integer channelType
			 ,Integer systemType) {
    	return  entityCPSGoodsService.savaClickCpsLink(cpsId, channelType, systemType);
    }
	
	
}
