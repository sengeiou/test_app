package cn.bevol.config.controller;

import cn.bevol.config.service.SystemConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
public class SystemConfController {

    @Autowired
    private SystemConfService systemConfService;

    @RequestMapping(value = {"/state"})
    @ResponseBody
    public String state() {
        return "ok";
    }


    @RequestMapping(value = {"/resource"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map getResource(HttpServletRequest request, @RequestParam String key) throws Exception {
        return systemConfService.getResource(key);
    }


    @RequestMapping(value = {"/resource/upsert"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map updateResource(HttpServletRequest request, @RequestParam String key,
                              @RequestParam String value, @RequestParam(required = false) String name) throws Exception {
        return systemConfService.upsertResource(key, value, name);
    }


    @RequestMapping(value = {"/resource/page"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map findResourceByPage(HttpServletRequest request, @RequestParam String module,
                                  @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws Exception {
        return systemConfService.findResourceByPage(module, pageNo, pageSize);
    }


    @RequestMapping(value = {"/resource/modules"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map allModuleList(HttpServletRequest request) throws Exception {
        return systemConfService.findAllModule();
    }

}
