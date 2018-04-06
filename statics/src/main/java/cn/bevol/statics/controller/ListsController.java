package cn.bevol.statics.controller;

import cn.bevol.statics.service.StaticListsService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by mysens on 17-5-15.
 */
@Controller
public class ListsController {
    @Resource
    private StaticListsService staticListsService;


    @RequestMapping(value = {"/static/lists/{id}"})
    @ResponseBody
    public Object staticLists(HttpSession session, HttpServletRequest request,
                              @PathVariable String id) throws IOException {
        return new ReturnData(staticListsService.staticListsPage(id));
    }
}
