package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.StaticTrialService;
import cn.bevol.util.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by mysens on 17-6-1.
 */
@Controller
@RequestMapping({"/static", "/"})
public class TrialController {
    @Resource
    private StaticTrialService staticTrialService;

    /**
     * 静态化福利社app
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/static/app/apply_goods/{id}")
    @ResponseBody
    public ReturnData staticTrialAppPage(@PathVariable String id) throws IOException {
        return staticTrialService.staticTrailAppPage(id);
    }

    /**
     * 静态化福利社微信
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/static/app_share/apply_goods/{id}")
    @ResponseBody
    public ReturnData staticTrialWxPage(@PathVariable String id) throws IOException {
        return staticTrialService.staticTrailWxPage(id);
    }

    /**
     * 静态化福利社全平台
     * @param id
     * @return
     */
    @RequestMapping(value="/static/apply_goods/{id}")
    @ResponseBody
    public ReturnData staticTrialPages(@PathVariable String id){
        return staticTrialService.staticTrailPages(id);
    }

    /**
     * 全量静态化
     * @return
     */
    @RequestMapping(value="/static/apply_goods/init")
    @ResponseBody
    public ReturnData staticTrialInit(){
        return staticTrialService.staticTrailInit();
    }
}
