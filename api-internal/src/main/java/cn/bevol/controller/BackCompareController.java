package cn.bevol.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.entity.service.CompareService;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;

@Controller
public class BackCompareController {

    @Resource
    private CompareService compareService;

    /**
     * 对比广场列表
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/back/compare/list/{tname}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnListData compareList(@PathVariable String tname,
                                      @RequestParam(required = false) String sid,
                                      @RequestParam(defaultValue="0") Integer type,
                                      @RequestParam(required = false) Integer hidden,
                                      @RequestParam(defaultValue="0") Integer sort,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int rows) throws Exception{
        return compareService.backCompareList(tname, sid, type, hidden, sort, page, rows);
    }

    /**
     * 对比广场人工排序
     * @param tname
     * @param id
     * @param sort
     * @param sortField
     * @return
     */
    @RequestMapping(value={"/back/compare/sort/{tname}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData compareStateSetting(@PathVariable String tname,
                                          @RequestParam Integer id,
                                          @RequestParam Integer sort,
                                          @RequestParam String sortField){
        return  compareService.compareSort(tname, id, sort, sortField);
    }

    /**
     * 对比广场设置状态
     * @param tname
     * @param id
     * @param hidden
     * @return
     */
    @RequestMapping(value={"/back/compare/state/{tname}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData compareStateSetting(@PathVariable String tname,
                                          @RequestParam Integer id,
                                          @RequestParam Integer hidden){
        return  compareService.compareState(tname, id, hidden);
    }
}
