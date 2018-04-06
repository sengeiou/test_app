package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.StaticListsService;
import com.bevol.web.response.ResponseBuilder;
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
@RequestMapping({"/static", "/"})
public class ListsController {
    @Resource
    private StaticListsService staticListsService;


    @RequestMapping(value = {"/static/lists/{id}"})
    @ResponseBody
    public Object staticLists(HttpSession session, HttpServletRequest request,
                                   @PathVariable String id) throws IOException {
        return ResponseBuilder.buildResult(staticListsService.staticListsPage(id));
    }
}
