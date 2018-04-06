package cn.bevol.internal.controller;

import cn.bevol.internal.service.SkinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Controller
public class BackSkinController extends BaseController {


    @Resource
    private SkinService skinService;




    /**
     * daily   cron
     * 每天更新
     * 缓存mongo数据  (更新通肤质最爱单品统计表)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/skin/topgood/update")
    @ResponseBody
    public boolean updateTopGood(HttpServletRequest request) throws Exception {
        Map map = new HashMap();
        boolean falg = skinService.updateTopGood();
        if (falg) {
            return true;
        }
        return false;
    }

    /**
     * daily   cron
     * 更新mysql
     * 缓存商品  (更新同肤质最爱单品列表)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/back/skin/cache/goods")
    @ResponseBody
    public boolean cacheGoods(HttpServletRequest request) throws Exception {
        Map map = new HashMap();
        boolean falg = skinService.cacheGoods();
        if (falg) {
            return true;
        }
        return false;
    }

}