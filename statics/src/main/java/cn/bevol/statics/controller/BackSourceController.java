package cn.bevol.statics.controller;

import cn.bevol.statics.service.*;
import cn.bevol.util.IPUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Controller
public class BackSourceController {
    private static Logger logger = LoggerFactory.getLogger(BackSourceController.class);
    @Resource
    private StaticGoodsService staticGoodsService;
    @Resource
    private StaticCompositionService staticCompositionService;
    @Resource
    private StaticFindService staticFindService;
    @Resource
    private StaticSkinPlanService staticSkinPlanService;
    @Resource
    private StaticTopicService staticTopicService;
    @Resource
    private StaticUserPartService staticUserPartService;
    @Resource
    private StaticTrialService staticTrialService;

    /***
     * 产品pc回源
     * @param session
     * @param request
     * @param mid
     * @return
     */
    @RequestMapping(value = {"/pc/product/{mid}"})
    public ModelAndView getPCBackGoods(HttpSession session, HttpServletRequest request,
                                       HttpServletResponse res,
                                       @PathVariable String mid){
        try {
            String html =  staticGoodsService.getBackGoods(mid, "pc");
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/pc/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            String guestIP = IPUtils.getIp(request);
            logger.error("回源错误访问IP:"+guestIP);
            return new ModelAndView("forward:/pc/404");
        }
    }

    /***
     *产品移动回源
     * @param session
     * @param request
     * @param mid
     * @return
     */
    @RequestMapping(value = {"/m/product/{mid}"})
    public ModelAndView getMBackGoods(HttpSession session, HttpServletRequest request,
                                      HttpServletResponse res,
                                      @PathVariable String mid){
        try {
            String html =  staticGoodsService.getBackGoods(mid, "mobile");
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/m/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            String guestIP = IPUtils.getIp(request);
            logger.error("回源错误访问IP:"+guestIP);
            return new ModelAndView("forward:/m/404");
        }
    }

    @RequestMapping(value = {"/pc/composition/{mid}"})
    public ModelAndView getPCBackComposition(HttpSession session, HttpServletRequest request,
                                             HttpServletResponse res,
                                             @PathVariable String mid){
        try {
            String html = staticCompositionService.getBackComposition(mid, "pc");
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/pc/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("forward:/pc/404");
        }
    }

    @RequestMapping(value = {"/m/composition/{mid}"})
    public ModelAndView getMBackComposition(HttpSession session, HttpServletRequest request,
                                            HttpServletResponse res,
                                            @PathVariable String mid){
        try {
            String html = staticCompositionService.getBackComposition(mid, "mobile");
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/m/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("forward:/m/404");
        }
    }

    @RequestMapping(value= {"/pc/industry/{id}"})
    public ModelAndView getPCBackIndsutry(HttpServletResponse res, @PathVariable String id){
        try {
            String html = staticFindService.getBackIndustry(id);
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/pc/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return new ModelAndView("forward:/pc/404");
        }
    }

    @RequestMapping(value = {"/pc/find/{id}"})
    public ModelAndView getPCBackFind(HttpSession session, HttpServletRequest request,
                                      HttpServletResponse res,
                                      @PathVariable String id) throws ClientProtocolException {
        try {
            String html = staticFindService.getBackFind(id,"pc");
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/pc/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return new ModelAndView("forward:/pc/404");
        }
    }

    @RequestMapping(value = {"/m/find/{id}"})
    public ModelAndView getMBackFind(HttpSession session, HttpServletRequest request,
                                     HttpServletResponse res,
                                     @PathVariable String id) throws ClientProtocolException {
        try {
            String html = staticFindService.getBackFind(id,"mobile");
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/m/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return new ModelAndView("forward:/m/404");
        }
    }

    @RequestMapping(value= {"/m/topic/{id}"})
    public ModelAndView getMBackTopic(HttpServletResponse res, @PathVariable String id){
        try{
            String html = staticTopicService.getBackTopic(id, "mobile");
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/m/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return new ModelAndView("forward:/m/404");
        }
    }

    @RequestMapping(value= {"/m/topic/user_part/{id}"})
    public ModelAndView getMBackTopicUserPartLists(HttpServletResponse res, @PathVariable String id){
        try{
            String html = staticUserPartService.getBackUserPartLists(id, "mobile");
            if("404".equals(html)){
                res.setStatus(404);
                return new ModelAndView("forward:/m/404");
            }
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter p = res.getWriter();
            p.println(html);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return new ModelAndView("forward:/m/404");
        }
    }

    /*@Deprecated
    @RequestMapping(value = {"/pc/industry/{id}"})
    public ModelAndView getPCBackIndustry(HttpSession session, HttpServletRequest request,
                                          HttpServletResponse res,
                                          @PathVariable Integer id) throws ClientProtocolException {
        Map<String,Object> o =  findService.getBackIndustry(id, "pc");
        String path = o.get("path").toString();
        if("404".equals(path)){
            res.setStatus(404);
            return new ModelAndView("forward:/pc/404");
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(path);
        modelAndView.addObject("data",o.get("data"));
        modelAndView.addObject("title",o.get("title"));
        modelAndView.addObject("keywords",o.get("keywords"));
        modelAndView.addObject("description",o.get("description"));
        modelAndView.addObject("js", o.get("js"));
        modelAndView.addObject("css", o.get("css"));
        modelAndView.addObject("url", o.get("url"));
        modelAndView.addObject("img", o.get("img"));
        modelAndView.addObject("staticType", "find");
        return modelAndView;
    }*/

    @RequestMapping(value = {"/pc/404"})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView getNoFind(){

        return new ModelAndView("/pc/404");
    }
    @RequestMapping(value = {"/m/404"})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView getMNoFind(){

        return new ModelAndView("/mobile/404");
    }

    @RequestMapping(value = {"/m/plan/{date}/{uid}_{category_pid}_{timestamp}"})
    public ModelAndView getMBackSkinPlan(HttpSession session, HttpServletRequest request,
                                         HttpServletResponse res,
                                         @PathVariable String date,
                                         @PathVariable String uid,
                                         @PathVariable String category_pid,
                                         @PathVariable String timestamp) throws IOException {
        Map<String,Object> o =  staticSkinPlanService.getBackSkinPlan(uid, category_pid);
        String path = o.get("path").toString();
        if("404".equals(path)){
            res.setStatus(404);
            return new ModelAndView("forward:/m/404");

        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(o.get("path").toString());
        modelAndView.addObject("result",o.get("result"));
        modelAndView.addObject("js", o.get("js"));
        modelAndView.addObject("css", o.get("css"));
        modelAndView.addObject("url", o.get("url"));
        modelAndView.addObject("img", o.get("img"));
        return modelAndView;
    }
}
