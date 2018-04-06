package cn.bevol.controller;

import cn.bevol.internal.service.InternalCompositionService;
import cn.bevol.mybatis.dto.CompositionDTO;
import cn.bevol.mybatis.dto.DirtyCompositionDTO;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;
import com.bevol.web.response.ResponseBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by zhangcheng on 17-5-9.
 */
@Controller
public class BackCompositionController {

    @Resource
    private InternalCompositionService internalCompositionService;

    /**
     * 添加新成分
     * @param compositionDTO
     * @return
     */
    @RequestMapping(value="/back/add_new_composition")
    @ResponseBody
    public ReturnData addNewComposition(CompositionDTO compositionDTO){
        return internalCompositionService.addNewComposition(compositionDTO);
    }

    /**
     * 修改单个成分
     * @param compositionDTO
     * @return
     */
    @RequestMapping(value="/back/save_composition_info")
    @ResponseBody
    public ReturnData saveCompositionInfo(CompositionDTO compositionDTO){
        return internalCompositionService.saveCompositionInfo(compositionDTO);
    }

    /**
     * 批量修改成分
     * @param compositionDTO
     * @param ids
     * @return
     */
    @RequestMapping(value="/back/save_composition_list")
    @ResponseBody
    public ReturnData saveCompositionList(CompositionDTO compositionDTO, String ids){
        return internalCompositionService.saveCompositionList(compositionDTO, ids);
    }

    /***
     * 单一成分查询
     * @param session
     * @param request
     * @param names
     * @return //TODO 方法未实现
     */
    @RequestMapping("/static/composition/compare")
    @ResponseBody
    public Object findComposition(HttpSession session, HttpServletRequest request, @RequestParam String names){
        return ResponseBuilder.buildResult(internalCompositionService.findComposition(names));
    }

    @RequestMapping("/back/composition/names")
    @ResponseBody
    public Object findCompositionByIds(HttpSession session, HttpServletRequest request,@RequestParam String ids){
        return ResponseBuilder.buildResult(internalCompositionService.findCompositionByIds(ids));
    }

    @RequestMapping("/back/composition/cps")
    @ResponseBody
    public Object findFormatCpsByids(HttpSession session, HttpServletRequest request,@RequestParam String ids){
        return ResponseBuilder.buildResult(internalCompositionService.findFormatCps(ids));
    }

    @RequestMapping("/back/add_dirty_composition")
    @ResponseBody
    public ReturnData addDirtyComposition(DirtyCompositionDTO dirtyCompositionDTO){
        return internalCompositionService.addDirtyComposition(dirtyCompositionDTO);
    }

    @RequestMapping("/back/save_dirty_composition")
    @ResponseBody
    public ReturnData saveDirtyComposition(DirtyCompositionDTO dirtyCompositionDTO){
        return internalCompositionService.saveDirtyComposition(dirtyCompositionDTO);
    }

    @RequestMapping("/back/save_dirty_composition_list")
    @ResponseBody
    public ReturnData saveDirtyCompositionList(DirtyCompositionDTO dirtyCompositionDTO, String ids){
        return internalCompositionService.saveDirtyCompositionList(dirtyCompositionDTO, ids);
    }

    /**
     * 查询成分是否存在
     * @param names
     * @return
     */
    @RequestMapping("/back/compositions/info/exists")
    @ResponseBody
    public ReturnListData getCompositionInfoByNames(@RequestParam String names){
        return internalCompositionService.getCompositionInfoByNames(names);
    }
}
