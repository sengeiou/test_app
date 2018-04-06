package cn.bevol.statics.controller;

import cn.bevol.statics.service.StaticSubjectService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by mysens on 17-7-12.
 */
@Controller
public class SubjectController {
    @Resource
    private StaticSubjectService staticSubjectService;
    /**
     * 专题内容页
     * @return
     */
    @RequestMapping(value = {"/static/subject/{id}"})
    @ResponseBody
    public ReturnData staticSubjectPage(@PathVariable Integer id){
        return staticSubjectService.staticSubjectPage(id);
    }
}
