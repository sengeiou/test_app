package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.AdvertisementLogService;
import com.bevol.web.response.ResponseBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Rc. on 2017/3/29.
 */
@Controller
@RequestMapping({"/static", "/"})
public class AdvertisementLogController {
    @Resource
    private AdvertisementLogService advertisementLogService;

    @RequestMapping(value = {"/static/ad/log/add"})
    @ResponseBody
    public void insertAdLog(HttpSession session, HttpServletRequest request,
                              @RequestParam Integer ad_id,
                              @RequestParam(defaultValue = "1") String position_type){
        advertisementLogService.addADLog(ad_id,position_type);
    }
    @RequestMapping(value = {"/static/ad/log/{ad_id}"})
    @ResponseBody
    public Object findAdLog(HttpSession session, HttpServletRequest request,
                            @PathVariable Integer ad_id,
                            @RequestParam(required = false) String startTime,
                            @RequestParam(required = false) String endTime
                          ){
       return ResponseBuilder.buildResult(advertisementLogService.findByadId(ad_id,startTime,endTime));
    }
    @RequestMapping(value = {"/static/ad/log/init"})
    @ResponseBody
    public void initAdLog(HttpSession session, HttpServletRequest request){
        advertisementLogService.initADLog();
    }


    @RequestMapping(value = {"/static/ad/logs/list"})
    @ResponseBody
    public Object findList(
            HttpSession session, HttpServletRequest request,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "1") Integer startPage){
        return advertisementLogService.findAdvertisementLogByPage(startTime,endTime,pageSize,startPage);
    }


}
