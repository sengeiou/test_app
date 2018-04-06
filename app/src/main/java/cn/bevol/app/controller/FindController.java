package cn.bevol.app.controller;


import cn.bevol.app.service.FindService;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Controller
public class FindController extends BaseController {

    @Autowired
    private FindService findService;

    /**
     * 达人原创热门标签列表
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/find/labellist")
    @ResponseBody
    public ReturnData findLabelList(HttpServletRequest request) throws Exception {
        return findService.findLabelList();
    }

    /**
     * v3.0以前接口
     * 获取发现版块文章
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/find/article")
    @ResponseBody
    public ReturnData findArticle(HttpServletRequest request,@RequestParam Integer id) throws Exception {
        return findService.findArticle(id);
    }

    /**
     * v3.1以前接口
     * 达人原创文章详情
     * @param id: 文章id
     * @return
     * @throws Exception
     */
    @RequestMapping("/find/info/{id}")
    @ResponseBody
    public ReturnData findArticleInfo(HttpServletRequest request,@PathVariable Integer id) throws Exception {
        return findService.findArticleInfo(id);
    }

    /**
     * 达人原创分类列表
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/find/type")
    @ResponseBody
    public ReturnData findType(HttpServletRequest request) throws Exception {
        return findService.findtype();
    }
    

    /**
     * 适用于pc端
    * 行业资讯列表页
    *
    * @param request
    * @param pager
    * @param pageSize
    * @return
    * @throws Exception
    */
    @RequestMapping(value={"/list/industry"})
	@ResponseBody
	public ReturnListData articleListPC(HttpServletRequest request, @RequestParam(defaultValue = "0") int pager,
										@RequestParam(defaultValue = "10") int pageSize) throws Exception {
    	if (pageSize > 10){
    		pageSize = 20;
    	}
    	ReturnListData alist=findService.industryList(pager, pageSize);
        return alist;
	}
    
    
    /**
     * v2.4
     * 获取往期文章列表
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/essence_comment/list")
    @ResponseBody
    public ReturnData findOldArticleList(HttpServletRequest request, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
    	if (pageSize > 10){
    		pageSize = 20;
    	}
    	return findService.findOldAarticleList(pager,pageSize);
    }
    
    /**
     * v2.9
     * 精选点评列表
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/essence_comment/list2")
    @ResponseBody
    public ReturnData findOldArticleList2(HttpServletRequest request, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
    	if (pageSize > 10){
    		pageSize = 20;
    	}
    	return findService.findOldAarticleList2(pager,pageSize);
    }
    
	

	/**
	 * v3.0之前
	 * 达人原创文章列表
	 * @param request
	 * @param type: 发现类型的id
	 * @param tag: v3.0之后无用,发现的标签
	 * @param skin: 用户肤质
	 * @param pager
	 * @param pageSize
	 * @param sort_type: 0最新 1最热 默认为0
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/entity/list2/find"})
	@ResponseBody
	public ReturnListData list2(HttpServletRequest request, @RequestParam(defaultValue="0",required=false) int type, @RequestParam(defaultValue="",required=false) String tag,
                                @RequestParam(defaultValue="",required=false) String skin, @RequestParam(defaultValue="0") int pager, @RequestParam(defaultValue="10") int pageSize, @RequestParam(defaultValue="0",required=false) int sort_type) throws Exception {
		/*记录访问日志*/
		//总的喜欢数 
		//总的收藏数 
		//总的访问数
		if(pageSize>10){
			pageSize=20;
		}
		
		//序列化异常pageSize==5 时
		if(pageSize==5){
			pageSize=6;
		}
		
		ReturnListData rd=new ReturnListData();
		//最新  最热
		if(sort_type==0){
			//最新 
			rd=findService.findList(type, tag, skin,"", pager, pageSize);
		}else{
			// 最热
			rd=findService.findList(type, tag, skin,"-hit_num", pager, pageSize);
		}
		return rd;
	}
	
	
	/**
	 * v3.1
	 * 达人原创文章列表
	 * 新增文章的banner信息
	 * @param request
	 * @param type: 发现类型的id
	 * @param tag: v3.0之后无用,发现的标签
	 * @param skin: 用户肤质
	 * @param pager
	 * @param pageSize
	 * @param sort_type: 0最新 1最热 默认为0
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/entity/list3/find"})
	@ResponseBody
	public ReturnData list3(HttpServletRequest request, @RequestParam(defaultValue="0",required=false) int type, @RequestParam(defaultValue="",required=false) String tag,
                            @RequestParam(defaultValue="",required=false) String skin, @RequestParam(defaultValue="0") int pager, @RequestParam(defaultValue="10") int pageSize, @RequestParam(defaultValue="0",required=false) int sort_type) throws Exception {
		if(pageSize>10){
			pageSize=20;
		}
		
		//序列化异常pageSize==5 时
		if(pageSize==5){
			pageSize=6;
		}
		
		ReturnListData rd=new ReturnListData();
		//最新  最热
		if(sort_type==0){
			//最新 
			rd=findService.findList(type, tag, skin,"", pager, pageSize);
		}else{
			// 最热
			rd=findService.findList(type, tag, skin,"-hit_num", pager, pageSize);
		}
		Map allMap=new HashMap();
		if(pager<=1){
			allMap.put("bannerInfo", findService.getFindBannerInfo());
		}
		allMap.put("list", rd.getResult());
		allMap.put("total", rd.getTotal());
		return new ReturnData(allMap);
	}
	
	/**
	 * 
	 * v2.9 精选点评列表
	 * @param request
	 * @param pager
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/find/user_tags/list2")
    @ResponseBody
    public ReturnData findByUserTags(HttpServletRequest request, @RequestParam(defaultValue = "0") int pager, @RequestParam(defaultValue = "10") int pageSize) throws Exception {
    	if (pageSize > 10){
    		pageSize = 20;
    	}
    	return findService.findOldAarticleList2(pager,pageSize);
    }

}