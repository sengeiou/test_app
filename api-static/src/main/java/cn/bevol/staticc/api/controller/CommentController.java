package cn.bevol.staticc.api.controller;

import javax.servlet.http.HttpServletRequest;

import com.bevol.web.response.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.entity.service.StaticShareGoodsService;
@Controller
@RequestMapping({"/static", "/"})
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
			 throws Exception{
				 return ResponseBuilder.buildResult(staticShareGoodsService.staticShareGoods(id,switchOfcreate));
			 }
}
