package cn.bevol.internal.controller;

import cn.bevol.internal.service.BackCleanCacheService;
import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
public class CacheCleanController {

    @Autowired
    CacheService cacheService;

    @Resource
    BackCleanCacheService backCleanCacheService;


    /*******
     * 2.0**********
     * /**
     * 实体名称tname
     * goods
     * composition
     * find
     */
    @Deprecated
    @RequestMapping(value = {"/cache/clean/entity/list/{tname}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData status(HttpServletRequest request, @PathVariable String tname) throws Exception {

        try {
            cacheService.cleanEntityCacheList(tname);
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }

    /**
     * 清除2.4首页缓存
     */
    @RequestMapping(value = {"/cache/clean/list2/IndexService.index2"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData cleanIndex2Cache(HttpServletRequest request, @PathVariable String key) throws Exception {
        try {
            cacheService.cleanCacheListByKey2(key);
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }

    /**
     * 清除时效性
     * 根据key清除匹配的instance
     * @param request
     * @param key
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/cache/clean/list/{key}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData clean(HttpServletRequest request, @PathVariable String key) throws Exception {

        try {
            cacheService.cleanCacheListByKey(key);
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }


    /**
     * 清除永久
     * 根据key清除匹配的   永不过期的缓存 instance
     * @param request
     * @param key 长度要大于5
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/cache/clean/forever/{key}"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData cleanForever(HttpServletRequest request, @PathVariable String key) throws Exception {

        try {
            cacheService.cleanCacheForeverByKey(key);
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }

    
    /**
     * 所有产品 单个缓存初始化 ()
     * 环境测试中接口 不要使用
     *
     * @return
     * @throws Exception
     */
   /* @SuppressWarnings("test")
    @RequestMapping(value = {"/cache/init/product"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData initProduct() throws Exception {

        try {
            cacheService.initProducts();
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }*/


    /**
     * 产品根据mid淘汰过气的缓存
     * 环境测试中接口 不要使用
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/cache/clean/product"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData cleanProduct(@RequestParam(required = true) String mid) throws Exception {

        try {
            cacheService.cleanProducts(mid);
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }

    /**
     * 产品成分根据mid  或者 id 淘汰过气的缓存
     * @param mid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/cache/clean/composition"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData cleanComposition(@RequestParam(required = true) String mid) throws Exception {

        try {
            cacheService.cleanComposition(mid);
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }



    /**
     * 所有用户 单个缓存初始化
     * 环境测试中接口 不要使用
     *
     * @return
     * @throws Exception
     */
    /*@SuppressWarnings("test")
    @RequestMapping(value = {"/cache/init/composition"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData initComposition() throws Exception {

        try {
            cacheService.initCompositions();
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }*/
    /**
     * 根据key清除匹配的   2天的缓存 instance
     * @param request
     * @param key 长度要大于5
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/cache/clean/2day"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData clean2Day(HttpServletRequest request, @RequestParam String key) throws Exception {

        try {
            cacheService.cleanCache2DayByKey(key);
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }


    /**
     * 一键清除常用的缓存(index,init)
     */
    @RequestMapping(value = {"/cache/clean/home"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData cleanHomeCache(HttpServletRequest request) throws Exception {

        try {
            backCleanCacheService.cleanHomeListByKey();
            return ReturnData.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ReturnData(-1, ex.getMessage());
        }
    }

    /**
     * 获取缓存版本
     * @return
     */
    @RequestMapping(value = "/cache/get/version", method = RequestMethod.POST)
    @ResponseBody
    public ReturnData getCacheVersion(){
        return cacheService.getCacheVersion();
    }

    /**
     * 设置缓存版本
     * @param version
     * @return
     */
    @RequestMapping(value = "/cache/set/version", method = RequestMethod.POST)
    @ResponseBody
    public ReturnData setCacheVersion(@RequestParam String version){
        return cacheService.setCacheVersion(version);
    }
}
