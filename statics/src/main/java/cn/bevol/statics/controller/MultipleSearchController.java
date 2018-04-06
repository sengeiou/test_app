package cn.bevol.statics.controller;

import cn.bevol.statics.service.MultipleSearchService;
import cn.bevol.util.IPUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by zhangcheng on 17-2-13.
 */

@Controller
public class MultipleSearchController {
    @Resource
    private MultipleSearchService multipleSearchService;

    @RequestMapping(value = {"/multiple_search"}, method = RequestMethod.POST)
    @ResponseBody
    public Object staticSinglePage(HttpSession session, HttpServletRequest request,
                                   @RequestParam String keywords) throws IOException {
        String ip = IPUtils.getIp(request);
        return multipleSearchService.multiple_search(keywords, ip);
    }
}
