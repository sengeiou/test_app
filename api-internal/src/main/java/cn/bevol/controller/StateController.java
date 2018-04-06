package cn.bevol.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by mysens on 17-5-10.
 */
@Controller
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
