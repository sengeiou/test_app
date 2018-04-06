package cn.bevol.staticc.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping({"/static", "/"})
public class StateController {

    /**
     * 状态检查用  不要删除!
     * @return
     */
    @RequestMapping(value = {"/state"})
    @ResponseBody
    public Object state() {
        return "OK";
    }
}
