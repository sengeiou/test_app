package cn.bevol.internal.controller;

import cn.bevol.internal.service.BackTagService;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 后台标签管理的接口
 * @author chenHaiJian
 *
 */
@Controller
public class BackTagController extends BaseController {
	   @Resource
	   private BackTagService backTagService;
	
	  
			
	   /**
	    * {category:6,categoryNanme:"美白",start_1:2,start_2:3}
		 * 评论标签--分类与标签的信息
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/tag/comment/goods"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnListData commentGoodsTags(HttpServletRequest request,@RequestParam(required=false,defaultValue="goods_category_goods_comment_") String tabs) throws Exception{
			return backTagService.getCommentGoodsTags(tabs);
		}
		
		/**
		 * 修改隐藏二级标签,分类和星级中的标签
		 * @param request
		 * @param tabs
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/tag/category/update"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData updateCategoryTag(HttpServletRequest request, @RequestParam(required=false) String tagIds, @RequestParam(required=false) Integer tabId, @RequestParam(required=false) Integer hidden) throws Exception{
			return backTagService.updateCategoryTag(tagIds,tabId);
		}
		
		/**
		 * 一级列表详情
		 */
		@RequestMapping(value={"/back/tag/category/info"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData commentGoodsTags(HttpServletRequest request,@RequestParam Integer tabId) throws Exception{
			return backTagService.getTagCategoryInfo(tabId);
		}
		
		/**
		 * 一级产品分类列表
		 * @param request
		 * @param type: 0一级产品分类,1所有分类
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/goods/category"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData goodsCategoryList(HttpServletRequest request,@RequestParam(required=false,defaultValue="0") int type) throws Exception{
			return backTagService.goodsCategoryList(type);
		}
		
		/**
		 * 新增或者修改标签或者分类
		 * @param request
		 * @param title
		 * @param tabs
		 * @param tabId
		 * @return
		 */
		@RequestMapping(value={"/back/tag/update"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData addTags(HttpServletRequest request, @RequestParam(required=false) String tagIds
				, @RequestParam(required=false) String tabs, @RequestParam Integer tabId, @RequestParam(required=false) Integer hidden){
			return backTagService.addTags(tagIds,tabs,tabId,hidden);
		}
		
		
		/**
		 * v3.1
		 * 新增/修改二级标签
		 * @param tabs: user,find,userPart
		 * @param pid: 一级标签库的id
		 * @param id: 二级标签库的id
		 * @param top: 排序 正序
		 * @param hidden: 1隐藏
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/add/tags"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData addOrUpdateTags(HttpServletRequest request, @RequestParam(required=false) String tabs, @RequestParam(required=false) Integer pid, @RequestParam(required=false) Integer id, @RequestParam(defaultValue="0",required=false) Integer top, @RequestParam(required=false) Integer hidden) throws Exception {
			return backTagService.addOrUpdateSubTags(pid,id,tabs,top,hidden);
		}
		
		/**
		 * 根据标签类型,获取二级标签列表
		 * @param request
		 * @param tabs: 标签类型
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/new_tags/list"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData addTags(HttpServletRequest request, @RequestParam(defaultValue="user_skin") String tabs, @RequestParam(defaultValue="0") Integer pager, @RequestParam(defaultValue="15") Integer pageSize) throws Exception {
			return backTagService.newFindList(tabs,pager,pageSize);
		}
		
		
		/**
		 * 查询一级标签列表
		 * hq_tag_list,即一级标签库的标签
		 */
		@RequestMapping(value={"/back/tags"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData getTags(HttpServletRequest request, @RequestParam(defaultValue="0") int pager, @RequestParam(defaultValue="10") int pageSize){
			return backTagService.getTags(pager,pageSize);
		}
		
		/**
		 * 新增一/二级标签
		 * @param request
		 * @param title
		 * @param tabs
		 * @param top
		 * @return
		 */
		@RequestMapping(value={"/back/tags/add"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData addTags(HttpServletRequest request, @RequestParam String title, @RequestParam String tabs, @RequestParam(required=false,defaultValue="0") Integer tabId){
			return backTagService.addTags(title,tabs,tabId);
		}
		
		/**
		 * todo:心得等特殊标签不能删除
		 * 删除一级标签和关联的二级标签
		 */
		@RequestMapping(value={"/back/tags/del"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData delTags(HttpServletRequest request,@RequestParam long id){
			return backTagService.delTags(id);
		}
		
		/**
		 * 删除二级标签的类型(不能全删)/修改一级标签名(会关联二级标签和实体的标签名)
		 * @param request
		 * @param id: 一级标签id
		 * @param newTitle:	新的一级标签名
		 * @param tabs:	最终的标签类型,逗号分隔
		 * @return
		 */
		@RequestMapping(value={"/back/tags/edit"},method={RequestMethod.POST})
		@ResponseBody
		public ReturnData editTags(HttpServletRequest request, @RequestParam Long id, @RequestParam(required=false) String newTitle, @RequestParam(required=false) String tabs){
			return backTagService.editTags(id,newTitle,tabs);
		}
}
