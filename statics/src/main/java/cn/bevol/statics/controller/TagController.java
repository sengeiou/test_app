package cn.bevol.statics.controller;

import cn.bevol.statics.service.StaticFindService;
import cn.bevol.statics.service.StaticGoodsService;
import cn.bevol.statics.service.TagStatiscalService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Rc. on 2017/2/20.
 * 标签管理
 */
@Controller
public class TagController {
    @Resource
    private TagStatiscalService tagStatiscalService;
    @Resource
    private StaticFindService staticFindService;
    @Resource
    private StaticGoodsService staticGoodsService;
    @RequestMapping(value = {"/static/tags/init"})
    @ResponseBody
    public Object insertTags(HttpSession session, HttpServletRequest request){
        return new ReturnData(tagStatiscalService.insertTags());
    }

    /**
     * 标签统计列表
     * @param session
     * @param request
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = {"/static/tags/list"})
    @ResponseBody
    public Object searchTagList(HttpSession session, HttpServletRequest request,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize ){
        return new ReturnData(tagStatiscalService.findTagStatiscal(page,pageSize));
    }
    /***
     * 根据tagId查询发现
     * @param session
     * @param request
     * @param page
     * @param pageSize
     * @param tagId
     * @return
     */
    @RequestMapping(value = {"/static/tags/find"})
    @ResponseBody
    public Object searchTagFindList(HttpSession session, HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required=false)Integer tagId){
        return new ReturnData(staticFindService.getTagFindList(tagId,page,pageSize));
    }
    /***
     * 根据tagId查询产品
     * @param session
     * @param request
     * @param page
     * @param pageSize
     * @param tagId
     * @return
     */
    @RequestMapping(value = {"/static/tags/goods"})
    @ResponseBody
    public Object searchTagGoodsList(HttpSession session, HttpServletRequest request,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required=false)Integer tagId){
        return new ReturnData(staticGoodsService.getTagFindList(tagId,page,pageSize));
    }
    /***
     * 根据tagId查询话题
     * @param session
     * @param request
     * @param page
     * @param pageSize
     * @param tagId
     * @return
     */
    @RequestMapping(value = {"/static/tags/part"})
    @ResponseBody
    public Object searchTagPartList(HttpSession session, HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required=false)Integer tagId){
        return new ReturnData(tagStatiscalService.getTagFindList(tagId,page,pageSize));
    }

    /***
     * 根据tagId查询清单
     * @param session
     * @param request
     * @param page
     * @param pageSize
     * @param tagId
     * @return
     */
    @RequestMapping(value = {"/static/tags/lists"})
    @ResponseBody
    public Object searchTagListsList(HttpSession session, HttpServletRequest request,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required=false)Integer tagId){
        return new ReturnData(tagStatiscalService.getTagListsList(tagId,page,pageSize));
    }
}
