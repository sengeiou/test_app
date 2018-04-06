package cn.bevol.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.internal.service.BackGoodsService;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;

 

/**
 * 产品标签相关
 * 提供给后台的接口
 * @author hualong
 *
 */
@Controller
public class BackApiGoodsController extends BaseController {
	   @Resource
	   private BackGoodsService backGoodsService;
	
	/**	
	 * 新增产品标签和相应的标签规则
     * 添加标签和规则1
     * 
     */
    @RequestMapping("/back/goods/tag/add")
    @ResponseBody
    public ReturnData tagAdd(HttpServletRequest request,@RequestParam String name,@RequestParam String rules) throws Exception {
        return backGoodsService.addTagRule(name,rules);
    }
    
    /**
	  * 修改标签名或者标签规则
	  * @param tagId: 产品标签id
	  * @param tagName: 标签名
	  * @param rules: 标签规则
	  * @return
	  */
    @RequestMapping("/back/goods/tag/edit")
    @ResponseBody
    public ReturnData tagAdd2(HttpServletRequest request,@RequestParam long id,@RequestParam(required=false) String name,@RequestParam(required=false) String rules) throws Exception {
        return backGoodsService.editTagRule(id,name,rules);
    }
    
    /**
     * 查询产品标签和该标签下的规则
     * @param request
     * @param pager
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/goods/tag")
    @ResponseBody
    public ReturnListData findGoodsTag(HttpServletRequest request,@RequestParam(defaultValue = "0") int pager,
       		@RequestParam(defaultValue = "10") int pageSize) throws Exception {
        return backGoodsService.findGoodsTag(pager,pageSize);
    }
    
    /**
     * 添加产品标签成分关系
     * @param request
     * @param tag_ids: 标签id,逗号分隔
     * @param composition_ids: 产品标签成分
     * @param is_main: 是否是核心标签成分 1是,0不是
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/goods/tag/composition/add")
    @ResponseBody
    public ReturnData addTagComposition(HttpServletRequest request,@RequestParam String tag_ids,
       		@RequestParam String composition_ids,@RequestParam(defaultValue = "0") int is_main) throws Exception {
        return backGoodsService.addTagComposition(tag_ids,composition_ids,is_main);
    }
    
    /**
     * 删除产品标签成分关系
     * 一个标签对应多个成分
     * @param request
     * @param tag_ids: 标签id,逗号分隔
     * @param composition_ids: 产品标签成分
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/goods/tag/composition/del")
    @ResponseBody
    public ReturnData delTagComposition(HttpServletRequest request,@RequestParam String tag_ids,
       		@RequestParam String composition_ids) throws Exception {
        return backGoodsService.delTagComposition(tag_ids,composition_ids);
    }
    
    /**
     * 根据tag_id查成分
     * @param request
     * @param tag_id: 产品标签id
     * @param is_main: 是否是核心标签成分 1是,0不是
     * @param pager
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/goods/tag/composition")
    @ResponseBody
    public ReturnListData findCpsByTagId(HttpServletRequest request,@RequestParam Long tag_id,@RequestParam int is_main,
       		@RequestParam(defaultValue="0") int pager,@RequestParam(defaultValue="10") int pageSize) throws Exception {
        return backGoodsService.findCpsByTagId(tag_id,is_main,pager,pageSize);
    }

    /**
     * 一个产品对应所有tag_ids
     * 手动修改产品标签结果
     * @param request
     * @param tag_ids: 标签id,逗号分隔
     * @param goods_ids: 产品id,逗号分隔
     * @return
     * @throws Exception
     */
	@RequestMapping("/back/goods/tag/result/edit")
	@ResponseBody
	public ReturnData madeEditTagResult(HttpServletRequest request,@RequestParam String tag_ids
			,@RequestParam String goods_ids) throws Exception {
	    return backGoodsService.madeEditTagResult(tag_ids,goods_ids);
	}    
	
	/**
	 * 查询含有某个标签的产品列表
	 * @param request
	 * @param tag_id: 标签id
	 * @param size
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/back/goods/tag/result")
	@ResponseBody
	public ReturnListData findGoodsByTagId(HttpServletRequest request,@RequestParam Long tag_id
			,@RequestParam(defaultValue = "0") int size,@RequestParam(defaultValue = "10") int pageSize) throws Exception {
	    return backGoodsService.findGoodsByTagId(tag_id,size,pageSize);
	}   
	
	/**
	 * 删除含有某个标签的产品 --批量清除产品
	 * @param request
	 * @param tag_id: 产品标签id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/back/goods/tag/result/del")
	@ResponseBody
	public ReturnData findGoodsByTagId(HttpServletRequest request,@RequestParam Long tag_id) throws Exception {
	    return backGoodsService.delGoodsByTagId(tag_id);
	}   
	
	/**
	 * 手动添加/修改产品标签
	 * @param request
	 * @param tag_ids: 手动添加的产品标签
	 * @param goods_ids: 要添加/修改产品id,逗号分隔
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/back/goods/tag/result/add")
	@ResponseBody
	public ReturnData madeAddResult(HttpServletRequest request,@RequestParam String tag_ids,@RequestParam String goods_ids) throws Exception {
	    return backGoodsService.madeAddResult(tag_ids,goods_ids);
	}   
	
	/**
	 * 手动删除产品中含有的的某个标签
	 * 一个产品对应多个标签
	 * @param request
	 * @param tag_ids: 要去除的标签,逗号分隔
	 * @param goods_ids: 产品id,逗号分隔
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/back/goods/tag/result/del/tag")
	@ResponseBody
	public ReturnData madeDelResult(HttpServletRequest request,@RequestParam String tag_ids,@RequestParam String goods_ids) throws Exception {
	    return backGoodsService.madeDelResult(tag_ids,goods_ids);
	}   
	
	/**
	 * 手动修改多分类表中的分类
	 * 一个分类对应goods_ids
	 * @param request
	 * @param category_id: 产品分类的id
	 * @param goods_ids: 产品id,逗号分隔
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/back/goods/poly/category/edit")
	@ResponseBody
	public ReturnData madeEditCategory(HttpServletRequest request,@RequestParam int category_id,@RequestParam String goods_ids) throws Exception {
	    return backGoodsService.madeEditCategory(category_id,goods_ids);
	}  
	
	
	/**
	 * 产品分类规则列表
	 * type 1 普通类 2特殊类
	 * 
	 */
	@RequestMapping("/back/goods/category_rule/list")
	@ResponseBody
	public ReturnData categoryRoleList(HttpServletRequest request,@RequestParam(defaultValue="1") int type) throws Exception {
	    return backGoodsService.categoryRoleList(type);
	}
	
	
	/**
	 * 新增/原有基础上添加产品分类规则
	 * @param request
	 * @param id: 分类规则表的id
	 * @param newRule1: 含有的成分规则
	 * @param newRule1: 不含有的成分规则
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/back/goods/category_rule/add")
	@ResponseBody
	public ReturnData addCategoryRoles(HttpServletRequest request,@RequestParam int id,@RequestParam(required=false,name="new_rule1") String newRule1,@RequestParam(required=false,name="new_rule2") String newRule2) throws Exception {
	    return backGoodsService.addeCategoryRules(id,newRule1,newRule2);
	}
	
	
	/**
	 * 修改某个产品分类规则
	 * 新的规则将覆盖旧的规则
	 * @param id: 分类规则表的id
	 * @param newRule1: 含有的成分规则
	 * @param newRule1: 不含有的成分规则
	 * @throws Exception
	 */
	@RequestMapping("/back/goods/category_rule/update")
	@ResponseBody
	public ReturnData updateCategoryRoles(HttpServletRequest request,@RequestParam int id,@RequestParam(required=false,name="new_rule1") String newRule1,@RequestParam(required=false,name="new_rule2") String newRule2) throws Exception {
	    return backGoodsService.updateCategoryRules(id,newRule1,newRule2);
	}
	
	/**
	 * 新增一个产品分类的规则,只支持普通分类
	 * @param id: 分类id
	 * @param newRule1: 包含的规则
	 * @param newRule2: 不包含的规则
	 * @return
	 */
	@RequestMapping("/back/goods/category_rule/add2")
	@ResponseBody
	public ReturnData addCategoryRoles2(HttpServletRequest request,@RequestParam int id,@RequestParam(required=false) String newRule1,@RequestParam(required=false) String newRule2) throws Exception {
	    return backGoodsService.addCategoryRules(id,newRule1,newRule2);
	}
	
	/**
	 * 产品分类列表
	 */
	@RequestMapping("/back/goods/category/list")
	@ResponseBody
	public ReturnListData goodsCategoryList(HttpServletRequest request) throws Exception {
	    return backGoodsService.goodsCategoryList();
	}
}
