package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.StaticUserPartService;
import com.bevol.web.response.ResponseBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by mysens on 17-6-20.
 */
@Controller
@RequestMapping({"/static", "/"})
public class UserPartController {
    @Resource
    private StaticUserPartService staticUserPartService;

    @RequestMapping("/static/topic/user/part/{id}")
    @ResponseBody
    public Object staticTopicUserPartInfo(@PathVariable String id){
        return ResponseBuilder.buildResult(staticUserPartService.staticUserPartPage(id));
    }
}
