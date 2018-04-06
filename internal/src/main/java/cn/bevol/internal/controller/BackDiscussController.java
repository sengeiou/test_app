package cn.bevol.internal.controller;

import cn.bevol.internal.service.BackDiscussService;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class BackDiscussController {

    @Resource
    private BackDiscussService backDiscussService;

    /**
     * 获取产品对比讨论列表
     * @param sid
     * @param userId
     * @param content
     * @param startTime
     * @param endTime
     * @param isRids
     * @param hidden
     * @return
     */
    @RequestMapping(value="/back/discuss/compare/goods/list", method = RequestMethod.POST)
    @ResponseBody
    public ReturnListData getBackDiscussCompareGoodsList(@RequestParam(required = false) String sid,
                                                         @RequestParam(required = false) Integer userId,
                                                         @RequestParam(required = false) String content,
                                                         @RequestParam(required = false) Integer startTime,
                                                         @RequestParam(required = false) Integer endTime,
                                                         @RequestParam(required = false) Integer isRids,
                                                         @RequestParam(required = false) Integer hidden,
                                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                                         @RequestParam(required = false, defaultValue = "10") Integer rows){
        return backDiscussService.getDiscussList(sid, userId, content, startTime, endTime, isRids, hidden, page, rows);
    }

    /**
     * 设置产品对比讨论状态
     * @param id
     * @param hidden
     * @return
     */
    @RequestMapping(value="/back/discuss/compare/goods/set/state", method = RequestMethod.POST)
    @ResponseBody
    public ReturnData getBackDiscussCompareGoodsSetting(@RequestParam Integer id,
                                                        @RequestParam(required = false) Integer hidden,
                                                        @RequestParam(required = false) Integer isEssence){
        return backDiscussService.setDiscussState(id, hidden, isEssence);
    }
}
