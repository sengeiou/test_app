package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.*;
import cn.bevol.util.ReturnData;
import com.bevol.web.response.ResponseBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by zhangcheng on 17-2-8.
 */

@Controller
@RequestMapping({"/static", "/"})
public class SinglePageController {
    @Resource
    private SinglePageService singlePageService;
    @Resource
    private SidebarService sidebarService;
    @Resource
    private StaticFindService staticFindService;
    @Resource
    private StaticTopicService staticTopicService;

    /**
     * 单个页面静态化 （例：PC站一级）
     * @param session
     * @param request
     * @param platform
     * @param name
     * @param serviceName
     * @param htmlName
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/static/page/{platform}/{name}/{htmlName}"})
    @ResponseBody
    public Object staticSinglePage(HttpSession session, HttpServletRequest request,
                                   @PathVariable String platform,
                                   @PathVariable String name,
                                   @RequestParam(defaultValue ="", required = false) String serviceName,
                                   @RequestParam(defaultValue ="", required = false) String ext,
                                   @RequestParam(defaultValue ="", required = false) String path,
                                   @PathVariable String htmlName) throws IOException {
        return ResponseBuilder.buildResult(singlePageService.staticPage(name, htmlName, serviceName, platform, ext, path));
    }

    /**
     * 静态化全首页
     * @return
     */
    @RequestMapping(value={"/static/index/page"})
    @ResponseBody
    public ReturnData staticIndexPage(){
        //移动站首页
        Boolean res1 = singlePageService.staticPage("index", "index", "index", "m", "html", "");
        //pc首页
        Boolean res2 = singlePageService.staticPage("pc_index", "index", "index", "pc", "html", "");
        if(res1 && res2){
            return ReturnData.SUCCESS;
        }
        return ReturnData.FAILURE;
    }

    /**
     * 静态化全列表
     * @return
     */
    @RequestMapping(value={"/static/list/page"})
    @ResponseBody
    public ReturnData staticListPage(){
        //移动站话题列表
        Boolean res1 = singlePageService.staticPage("m_topic", "topic", "topic", "m", "", "");
        //移动站发现列表
        Boolean res2 = singlePageService.staticPage("m_find", "find", "find", "m", "", "");
        if(res1 && res2){
            return ReturnData.SUCCESS;
        }
        return ReturnData.FAILURE;
    }

    /**
     * 每小时静态化
     * @return
     */
    @RequestMapping(value={"/static/hourly"})
    @ResponseBody
    public Object staticHourly(){
        //清理PC sidebar缓存
        sidebarService.generateSideBarCache();
        //静态化全列表
        ReturnData res1 = staticListPage();
        //发现全量静态化
        Boolean res2 = staticFindService.initFindStatic(1);
        //话题全量静态化
        Boolean res3 = staticTopicService.initTopicStatic(1);
        if(res1.getRet() == 0 && res2 && res3){
            return ReturnData.SUCCESS;
        }else{
            return ReturnData.FAILURE;
        }
    }

    /**
     * 每日静态化
     * @return
     */
    @RequestMapping(value={"/static/daily"})
    @ResponseBody
    public Object staticDaily(){
        //静态化全首页
        ReturnData res1 = staticIndexPage();
        //静态化移动站搜索页面
        Boolean res2 = singlePageService.staticPage("search", "search", "", "m", "html", "search");
        if(res1.getRet() == 0 && res2){
            return ReturnData.SUCCESS;
        }else{
            return ReturnData.FAILURE;
        }
    }

    /**
     * PC我的页面
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/static/page/pc/pc_my/my/index"})
    @ResponseBody
    public Object staticMyPage() throws IOException {
        return ResponseBuilder.buildResult(singlePageService.staticPage("pc_my", "my/index", "", "pc", "html"));
    }

    /**
     * 行业资讯
     * @return
     */
    @RequestMapping(value = {"/static/page/pc/pc_industry"})
    @ResponseBody
    public Object staticIndustryPage(){
        return ResponseBuilder.buildResult(singlePageService.staticLoopPage("pc_industry", "industry", "industry", "pc", "", ""));
    }

    /**
     * 专题列表
     * @return
     */
    @RequestMapping(value = {"/static/page/pc/pc_subject_list"})
    @ResponseBody
    public Object staticSubjectListPage(){
        return ResponseBuilder.buildResult(singlePageService.staticLoopPage("pc_subject_list", "zt", "subjectList", "pc", "", ""));
    }

    /**
     * 微信分享页面
     * @param session
     * @param request
     * @param name
     * @param serviceName
     * @param htmlName
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/static/page/m/{name}/app_share/{htmlName}"})
    @ResponseBody
    public Object staticAppSharePage(HttpSession session, HttpServletRequest request,
                                     @PathVariable String name,
                                     @RequestParam(defaultValue ="", required = false) String serviceName,
                                     @PathVariable String htmlName) throws IOException {
        return ResponseBuilder.buildResult(singlePageService.staticPage(name, "app_share/" + htmlName, serviceName, "m"));
    }

    /**
     * 发现页面微信分享
     * @param session
     * @param request
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/static/page/m/wx_find/app_share/article/{id}"})
    @ResponseBody
    @Deprecated
    public Object staticFindSharePage(HttpSession session, HttpServletRequest request,
                                      @PathVariable String id) throws IOException {
        return ResponseBuilder.buildResult(singlePageService.staticFindPage("wx_find", "app_share/article/" + id, id, "m"));
    }

    /**
     * 清单页面微信分享
     * @param session
     * @param request
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/static/page/m/wx_lists/app_share/lists/{id}"})
    @ResponseBody
    public Object staticListsSharePage(HttpSession session, HttpServletRequest request,
                                       @PathVariable String id) throws IOException {
        return ResponseBuilder.buildResult(singlePageService.staticListsPage("wx_lists", "app_share/lists/" + id, id, "m"));
    }

    /**
     * 兼容旧版的发现json静态化
     * @param session
     * @param request
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/static/app/find_json/{id}"})
    @ResponseBody
    public Object staticFindSharePage(HttpSession session, HttpServletRequest request,
                                      @PathVariable Integer id) throws IOException {
        return ResponseBuilder.buildResult(singlePageService.staticFindJson(id));
    }

    /**
     * app用发现文章页面
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/static/app/find/{id}"})
    @ResponseBody
    @Deprecated
    public Object staticAppFindPage(@PathVariable String id) throws IOException {
        return ResponseBuilder.buildResult(
                singlePageService.staticFindPage("app_find", "app/article/" + id, id, "m"));
    }

    @RequestMapping(value = {"/static/setContentType"})
    @ResponseBody
    public ReturnData setContentType(@RequestParam String contentType, @RequestParam String key, @RequestParam String bucket){
        return OSSService.resetContentType(contentType, key, bucket);
    }
}
