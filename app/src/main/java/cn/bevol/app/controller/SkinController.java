package cn.bevol.app.controller;

import cn.bevol.app.service.SkinService;
import cn.bevol.model.user.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Controller
public class SkinController extends BaseController {


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
    @RequestMapping("/skin/topgood/update")
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
    @RequestMapping("/skin/cache/goods")
    @ResponseBody
    public boolean cacheGoods(HttpServletRequest request) throws Exception {
        Map map = new HashMap();
        boolean falg = skinService.cacheGoods();
        if (falg) {
            return true;
        }
        return false;
    }
    
    /**
     * 查询肤质详情和最爱单品
     *
     * @param request
     * @param skin: 肤质(缩写的格式OSNT)
     * @return
     * @throws Exception
     */
    @RequestMapping("/skin/index")
    @ResponseBody
    public Map findSkinAndGoods(HttpServletRequest request, @RequestParam(required = false) String skin) throws Exception {
        if (StringUtils.isBlank(skin)) {
            UserInfo userInfo = this.getUser(request);
            skin = userInfo.getResult();
        }

        Map map = new HashMap();
        map = skinService.findSkinAndGoods(skin);
        if (null != map) {
            return map;
        }
        return errorAjax();
    }

}