package cn.bevol.statics.controller;

import cn.bevol.statics.service.StaticShareGoodsService;
import cn.bevol.util.response.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommentController {
	
	@Autowired
	private StaticShareGoodsService staticShareGoodsService;
	/**
	 * 产品分享静态页面
	 * @Param id  评论ID
	 * @Param switchOfcreate 强制生成开关  1:强制生成
	 */
	@RequestMapping(value = "/comment/share", method = {RequestMethod.POST})
	@ResponseBody
	public Object goodsShareController(HttpServletRequest request,
                                       @RequestParam(defaultValue="0",required=false) Integer id,
                                       @RequestParam(defaultValue="0",required=false) Integer switchOfcreate)
			 throws Exception {
				 return new ReturnData(staticShareGoodsService.staticShareGoods(id,switchOfcreate));
			 }
}
