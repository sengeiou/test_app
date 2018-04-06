package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.StaticCompareService;
import cn.bevol.util.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping({"/static", "/"})
public class CompareController {

    @Resource
    private StaticCompareService staticCompareService;

    /**
     * 静态化对比页面接口
     * 返回值中包含url
     * @param mids
     * @return
     */
    @RequestMapping("/static/goods/compare/page")
    @ResponseBody
    public ReturnData staticComparePage(@RequestParam String mids, @RequestParam(required = false, defaultValue = "0") Integer force){
        try {
            return staticCompareService.staticComparePage(mids, force);
        } catch (IOException e) {
            e.printStackTrace();
            return ReturnData.ERROR;
        }
    }
}
