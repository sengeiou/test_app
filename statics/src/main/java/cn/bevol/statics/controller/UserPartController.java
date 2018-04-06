package cn.bevol.statics.controller;

import cn.bevol.statics.service.StaticUserPartService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by mysens on 17-6-20.
 */
@Controller
public class UserPartController {
    @Resource
    private StaticUserPartService staticUserPartService;

    @RequestMapping("/static/topic/user/part/{id}")
    @ResponseBody
    public Object staticTopicUserPartInfo(@PathVariable String id){
        return new ReturnData(staticUserPartService.staticUserPartPage(id));
    }
}
