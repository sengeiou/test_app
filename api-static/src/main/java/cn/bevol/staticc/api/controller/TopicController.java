package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.StaticTopicService;
import cn.bevol.util.ReturnData;
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
public class TopicController {

    @Resource
    StaticTopicService staticTopicService;

    /**
     * 话题静态化
     * @param id
     * @return
     */
    @RequestMapping("/static/topic/{id}")
    @ResponseBody
    public Object staticTopicInfo(@PathVariable String id){
        return ResponseBuilder.buildResult(staticTopicService.staticTopicPage(id));
    }

    /**
     * 话题全量静态化
     * @return
     */
    @RequestMapping("/static/topic/init")
    @ResponseBody
    public ReturnData staticTopicInit(){
        staticTopicService.initTopicStatic(1);
        return ReturnData.SUCCESS;
    }
}
