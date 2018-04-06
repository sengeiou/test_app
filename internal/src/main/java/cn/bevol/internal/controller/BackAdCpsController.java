package cn.bevol.internal.controller;

import cn.bevol.internal.service.InternalAdCpsService;
import cn.bevol.model.entity.EntityAdCps;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class BackAdCpsController {

    @Resource
    private InternalAdCpsService internalAdCpsService;

    /**
     * 获取广告cps列表
     * @param id
     * @param title
     * @param startTime
     * @param endTime
     * @param hidden
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value="/back/ad/cps/list")
    @ResponseBody
    public ReturnListData getAdCpsList(@RequestParam(required = false) Integer id,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(required = false) Integer startTime,
                                       @RequestParam(required = false) Integer endTime,
                                       @RequestParam(required = false) Integer hidden,
                                       @RequestParam(required = false, defaultValue = "1") Integer page,
                                       @RequestParam(required = false, defaultValue = "10") Integer rows){
        return internalAdCpsService.getAdCpsList(id, title, startTime, endTime, page, rows, hidden);
    }

    /**
     * 编辑/新增广告csp
     * @param entityAdCps
     * @return
     */
    @RequestMapping(value="/back/ad/cps/upsert")
    @ResponseBody
    public ReturnData upsertAdCps(EntityAdCps entityAdCps){
        return internalAdCpsService.upsertAdCps(entityAdCps);
    }

    /**
     * 广告cps状态设置
     * @param ids
     * @param hidden
     * @param deleted
     * @return
     */
    @RequestMapping(value="/back/ad/cps/state/set")
    @ResponseBody
    public ReturnData setAdCpsState(@RequestParam String ids,
                                    @RequestParam(required = false) Integer hidden,
                                    @RequestParam(required = false) Integer deleted){
        return internalAdCpsService.setAdCpsState(ids, hidden, deleted);
    }
}
