package cn.bevol.staticc.api.controller;

import cn.bevol.entity.service.StaticSkinPlanService;
import com.bevol.web.response.ResponseBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by zhangcheng on 17-2-27.
 */
@Controller
@RequestMapping({"/static", "/"})
public class SkinPlanController {
    @Resource
    private StaticSkinPlanService staticSkinPlanService;

    @RequestMapping(value = {"/skin_plan/user_id/{user_id}/category_pid/{category_pid}"})
    @ResponseBody
    public Object staticSkinPlan(HttpSession session, HttpServletRequest request, @PathVariable String user_id, @PathVariable String category_pid) throws IOException {
        return staticSkinPlanService.staticSkinPlan(user_id, category_pid);
    }

    /**
     * 清理过期文件
     */
    @RequestMapping(value = {"/skin_plan/clear"})
    @ResponseBody
    public Map<String, Object> removeInvalidSkinPlan(){
        staticSkinPlanService.delete7DaysAgoFiles();
        return ResponseBuilder.success("清除成功");
    }

}
