package cn.bevol.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.bevol.entity.service.CacheService;
import cn.bevol.util.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bevol.web.response.ResponseBuilder;

import cn.bevol.entity.service.IndexService;
import cn.bevol.internal.service.BackCleanCacheService;



@Controller
public class CacheCleanController {

    @Autowired
    CacheService cacheService;
    @Autowired
    IndexService indexService;
    
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
    public Map status(HttpServletRequest request, @PathVariable String tname) throws Exception {

        try {
            cacheService.cleanEntityCacheList(tname);
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
        }
    }

    /**
     * 清除2.4首页缓存
     */
    @RequestMapping(value = {"/cache/clean/list2/IndexService.index2"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map cleanIndex2Cache(HttpServletRequest request, @PathVariable String key) throws Exception {
        try {
            cacheService.cleanCacheListByKey2(key);
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
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
    public Map clean(HttpServletRequest request, @PathVariable String key) throws Exception {

        try {
            cacheService.cleanCacheListByKey(key);
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
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
    public Map cleanForever(HttpServletRequest request, @PathVariable String key) throws Exception {

        try {
            cacheService.cleanCacheForeverByKey(key);
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
        }
    }

    
    /**
     * 重新缓存首页
     * @param request
     * @param key 长度要大于5
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/cache/reset/index"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map resetIndex(HttpServletRequest request) throws Exception {
        try {
        	indexService.cacheInit();
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
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
    public Map initProduct() throws Exception {

        try {
            cacheService.initProducts();
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
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
    public Map cleanProduct(@RequestParam(required = true) String mid) throws Exception {

        try {
            cacheService.cleanProducts(mid);
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
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
    public Map cleanComposition(@RequestParam(required = true) String mid) throws Exception {

        try {
            cacheService.cleanComposition(mid);
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
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
    public Map initComposition() throws Exception {

        try {
            cacheService.initCompositions();
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
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
    public Map clean2Day(HttpServletRequest request, @RequestParam String key) throws Exception {

        try {
            cacheService.cleanCache2DayByKey(key);
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
        }
    }


    /**
     * 一键清除常用的缓存(index,init)
     */
    @RequestMapping(value = {"/cache/clean/home"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map cleanHomeCache(HttpServletRequest request) throws Exception {

        try {
            backCleanCacheService.cleanHomeListByKey();
            return ResponseBuilder.buildSuccessMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBuilder.buildFailureMessage(ex.getMessage());
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
