package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.AdvertisementService;
import cn.bevol.entity.service.weixin.WxApiClientService;
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
 * Created by Rc. on 2017/3/23.
 */
@Controller
@RequestMapping({"/static", "/"})
public class AdvertisementController {
    @Resource
    private AdvertisementService advertisementService;
    @Resource
    private WxApiClientService wxApiClientService;

    /***
     * 广告列表
     * @param session
     * @param request
     * @param name
     * @param orientation
     * @param type
     * @param publishTime
     * @param overdueTime
     * @param pageSize
     * @param startPage
     * @return
     */
    @RequestMapping(value = {"/static/ad/list"})
    @ResponseBody
    public Object find(HttpSession session, HttpServletRequest request,
                       @RequestParam(required = false) Integer name,
                       @RequestParam(required = false) Integer orientation,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) Integer hidden,
                       @RequestParam(required = false) Integer publishTime,
                       @RequestParam(required = false) Integer overdueTime,
                       @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                       @RequestParam(required = false, defaultValue = "1") Integer startPage){
        return advertisementService.find(name,orientation,type,hidden,publishTime,overdueTime,pageSize,startPage);
    }

    /***
     * 添加广告
     * @param session
     * @param request
     * @param name
     * @param orientation
     * @param type
     * @param entityId
     * @param imgUrl
     * @param positionType
     * @param redirectType
     * @param hidden
     * @param publishTime
     * @param overdueTime
     * @param creater
     * @return
     */
    @RequestMapping(value = {"/static/ad/add"})
    @ResponseBody
    public Object insert(HttpSession session, HttpServletRequest request,
                         @RequestParam Integer name,
                         @RequestParam Integer orientation,
                         @RequestParam (required = false)Integer bannerType,
                         @RequestParam(required = false) String type,
                         @RequestParam Integer entityId,
                         @RequestParam(required = false)String entityName,
                         @RequestParam String imgUrl,
                         @RequestParam String positionType,
                         @RequestParam Integer redirectType,
                         @RequestParam String redirectUrl,
                         @RequestParam(required = false) Integer classifyId,
                         @RequestParam Integer hidden,
                         @RequestParam String publishTime,
                         @RequestParam String overdueTime,
                         @RequestParam String creater){
        return ResponseBuilder.buildResult(advertisementService.insert(name,orientation,bannerType,type,entityId,entityName,imgUrl,positionType,redirectType,redirectUrl,classifyId,hidden,publishTime,overdueTime,creater));
    }

    /***
     * 修改广告
     * @param session
     * @param request
     * @param id
     * @param name
     * @param orientation
     * @param type
     * @param entityId
     * @param imgUrl
     * @param positionType
     * @param redirectType
     * @param hidden
     * @param publishTime
     * @param overdueTime
     * @param updater
     * @return
     */
    @RequestMapping(value = {"/static/ad/edit"})
    @ResponseBody
    public Object update(HttpSession session, HttpServletRequest request,
                         @RequestParam Integer id,
                         @RequestParam Integer name,
                         @RequestParam Integer orientation,
                         @RequestParam(required = false) Integer bannerType,
                         @RequestParam(required = false) String type,
                         @RequestParam Integer entityId,
                         @RequestParam(required = false) String entityName,
                         @RequestParam String imgUrl,
                         @RequestParam String positionType,
                         @RequestParam Integer redirectType,
                         @RequestParam String redirectUrl,
                         @RequestParam(required = false) Integer classifyId,
                         @RequestParam Integer hidden,
                         @RequestParam String publishTime,
                         @RequestParam String overdueTime,
                         @RequestParam String updater){
        return ResponseBuilder.buildResult(advertisementService.update(id,name,orientation,type,bannerType,entityId,entityName,imgUrl,positionType,redirectType,redirectUrl,classifyId,hidden,publishTime,overdueTime,updater));
    }

    @RequestMapping(value = {"/static/ad/status"})
    @ResponseBody
    public Object state(@RequestParam Integer id,
                        @RequestParam Integer hidden,
                        @RequestParam String updater){
        return ResponseBuilder.buildResult(advertisementService.status(id, hidden, updater));
    }
    /***
     * 每日定时检测广告 From hq_goods
     * @param session
     * @param request
     */
    @RequestMapping(value = {"/static/ad/init"})
    @ResponseBody
    public void initAdvertisement(HttpSession session, HttpServletRequest request){
        advertisementService.initAdvertisement();
    }

    /**
     * 根据类型查询广告
     * @param session
     * @param request
     * @param name
     * @param type
     * @return
     */
    @RequestMapping(value = {"/static/ad/findAd"})
    @ResponseBody
    public Object findAdByEntityId(HttpSession session, HttpServletRequest request,
                                   @RequestParam Integer name,
                                   @RequestParam(required = false) String type,
                                   @RequestParam(required = false)Integer classifyId){
        return   ResponseBuilder.buildResult(advertisementService.findAdByType(name,type,classifyId,"1"));
    }
    /**
     * 根据类型查询广告 V2
     * @param session
     * @param request
     * @param name
     * @param type
     * @return
     */
    @RequestMapping(value = {"/static/ad/findAd2"})
    @ResponseBody
    public Object findAdByEntityId2(HttpSession session, HttpServletRequest request,
                                    @RequestParam Integer name,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false)Integer classifyId,
                                    @RequestParam(required = false)String positionType){
        return   ResponseBuilder.buildResult(advertisementService.findAdByType(name,type,classifyId,positionType));
    }

    /***
     * 根据ID查询
     * @param session
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = {"/static/ad/{id}"})
    @ResponseBody
    public Object findAdvertisementById(HttpSession session, HttpServletRequest request,
                                        @PathVariable Integer id){
        return   ResponseBuilder.buildResult(advertisementService.findAdvertisementById(id));
    }

    @Deprecated
    @RequestMapping(value = {"/static/ad/imgupload"})
    @ResponseBody
    public Object upload(HttpSession session, HttpServletRequest request,
                         @RequestParam String picUrl){
        return   ResponseBuilder.buildResult(wxApiClientService.uploadImage(picUrl));
    }

    /***
     * 定时统计广告点击数
     * @param session
     * @param request
     */
    @RequestMapping(value = {"/static/ad/init/logtotal"})
    @ResponseBody
    public void initAdvertisementClickTotal(HttpSession session, HttpServletRequest request){
        advertisementService.initAdvertisementClickTotal();
    }

}
