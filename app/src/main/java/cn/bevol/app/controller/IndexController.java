package cn.bevol.app.controller;

import cn.bevol.app.service.CommenStatisticsService;
import cn.bevol.app.service.IndexService;
import cn.bevol.util.response.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Controller
public class IndexController extends BaseController {
    @Autowired
    private IndexService indexService;
    @Autowired
    private CommenStatisticsService commenStatisticsService;

    /**
     * v3.1之前
     * @param sys: 0 Android 1 ios
     * @return
     */
    @RequestMapping("/checkversion/{sys}")
    @ResponseBody
    public ReturnData checkVersion(@PathVariable int sys) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        return indexService.checkVersion(sys);
    }

    /**
     * @return
     */
    @RequestMapping("/state")
    @ResponseBody
    public ReturnData state() throws Exception {
        return new ReturnData(0,"App State OK");
    }

    /**
     * 缓存测试
     * @return
     */
    @RequestMapping("test/cache/c5m")
    @ResponseBody
    public ReturnData testCache(){
        return indexService.index2();
    }


    /**
     * @return
     */
    @RequestMapping("/index")
    @ResponseBody
    public Map<String, Object> addCollection() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("image",indexService.indexImage());
        map.put("classification",indexService.indexClassification());
        map.put("article",indexService.indexArticle());
        return map;
    }


    /**
     * 2.4首页
     * @return
     */
    @RequestMapping("/index2")
    @ResponseBody
    public ReturnData index() throws Exception {
        return indexService.index2();
    }

    /**
     * 2017 包含话题
     * @return
     */
    @RequestMapping("/index3")
    @ResponseBody
    public ReturnData index3() throws Exception {
        return indexService.index3();
    }

    /**
     * 2017 包含话题
     * @return
     */
    @RequestMapping("/index4")
    @ResponseBody
    public ReturnData index4() throws Exception {
        return indexService.index4();
    }

    /**
     * 2017 v2.9-v3.0
     * @return
     */
    @RequestMapping("/index5")
    @ResponseBody
    public ReturnData index5(@RequestParam(defaultValue="1",name="position_type") Integer positionType) throws Exception {
        if(null==positionType){
            positionType=1;
        }

        if(0<positionType && positionType<4){
            return indexService.index5(positionType+"");
        }
        return ReturnData.ERROR;
    }


    /**
     * 2017 v3.1
     * @param positionType: 1app 2移动 3pc
     * @param pager: 页数
     * @return
     * @throws Exception
     */
    @RequestMapping("/index6")
    @ResponseBody
    public ReturnData index6(@RequestParam(defaultValue="1",name="position_type") Integer positionType, @RequestParam(defaultValue = "1") int pager) throws Exception {
        if(null==positionType){
            positionType=1;
        }

        if(0<positionType && positionType<4){
            return indexService.index6(positionType+"",pager);
        }
        return ReturnData.ERROR;
    }

    /**
     * 2017 v3.1
     * @param positionType: 1app 2移动 3pc
     * @param pager: 页数
     * @return
     * @throws Exception
     */
    @RequestMapping("/index7")
    @ResponseBody
    public ReturnData index7(@RequestParam(defaultValue="1",name="position_type") Integer positionType, @RequestParam(defaultValue = "1") int pager) throws Exception {
        if(null==positionType){
            positionType=1;
        }
        
        if(0<positionType && positionType<4){
            return indexService.index7(positionType+"",pager);
        }
        return ReturnData.ERROR;
    }

    /**
     * 2017 v3.3
     * @param positionType 1app 2移动 3pc
     * @param pager  页数
     * @return
     * @throws Exception
     */
    @RequestMapping("/index8")
    @ResponseBody
    public ReturnData index8(@RequestParam(defaultValue="1",name="position_type") Integer positionType, @RequestParam(defaultValue = "1") int pager) throws Exception {
        if(null==positionType){
            positionType=1;
        }
        
        if(0<positionType && positionType<4){
            return indexService.index8(positionType+"",pager);
        }
        return ReturnData.ERROR;
    }
    
    /**
     * 2018  小程序banner接口
     * @param pager: 页数
     * @return
     * @throws Exception
     */
    @RequestMapping("/mIndexBanner1")
    @ResponseBody
    public ReturnData mIndexBanner1(@RequestParam(defaultValue = "1") int pager) throws Exception {
        return indexService.mIndexBanner1(pager);
    }

    /**
     * @return
     */
    @RequestMapping("/init")
    @ResponseBody
    public ReturnData initApp(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer userId) throws Exception {
        return indexService.initApp(this.getUserId(request));
    }

    /**
     * @return
     */
    @RequestMapping("/statistics/search/noresult")
    @ResponseBody
    public ReturnData initApp(HttpServletRequest request) throws Exception {
        return commenStatisticsService.userSearchActive(request);
    }


    /**
     * @return
     */
    @RequestMapping("/init2")
    @ResponseBody
    public ReturnData initApp2(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer userId) throws Exception {
        return indexService.initApp2(this.getUserId(request));
    }


    /**
     * @return
     */
    @RequestMapping("/init3")
    @ResponseBody
    public ReturnData initApp3(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer userId) throws Exception {
        return indexService.initApp3(this.getUserId(request));
    }


    /**
     * @return
     */
    @RequestMapping("/init4")
    @ResponseBody
    public ReturnData initApp4(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer userId) throws Exception {
        return indexService.initApp4(this.getUserId(request));
    }

    /**
     * @return
     */
    @RequestMapping("/init5")
    @ResponseBody
    public ReturnData initApp5(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer userId) throws Exception {
        return indexService.initApp5(this.getUserId(request));
    }


    /**
     * @return
     */
    @RequestMapping("/init6")
    @ResponseBody
    public ReturnData initApp6(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer userId) throws Exception {
        return indexService.initApp6(this.getUserId(request));
    }

    /**
     * v2.9-v3.0
     * 首页初始化接口
     * @return
     */
    @RequestMapping("/init7")
    @ResponseBody
    public ReturnData initApp7(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer userId) throws Exception {
        return indexService.initApp7(this.getUserId(request));
    }

    /**
     * v3.1
     * 首页初始化接口
     * @return
     */
    @RequestMapping("/init8")
    @ResponseBody
    public ReturnData init8(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer userId) throws Exception {
        return indexService.initApp8(this.getUserId(request));
    }

    /**
     * v3.1
     * 打开app
     * @param request
     * @param o: 0 android 1 ios
     * @param v: app版本
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/open/app2"},method={RequestMethod.POST})
    @ResponseBody
    public ReturnData app2(HttpServletRequest request,@RequestParam(name="open_o") int o,@RequestParam(name="open_v") String  v) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        return indexService.opanApp2(this.getUserId(request),o,v);
    }

}