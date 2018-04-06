package cn.bevol.internal.controller;

import cn.bevol.internal.service.InternalSpiderService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by mysens on 17-6-19.
 */
@Controller
public class BackSpiderController {

    @Resource
    InternalSpiderService internalSpiderService;

    /**
     * 爬虫入库统计
     * @return
     */
    @RequestMapping("/back/spider/into/statistic")
    @ResponseBody
    public ReturnData statisticSpiderInto(){
        return internalSpiderService.statisticSpiderInto();
    }
}
