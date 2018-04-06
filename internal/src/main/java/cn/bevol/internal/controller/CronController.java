package cn.bevol.internal.controller;

import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.service.CronService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by mysens on 17-4-12.
 */
@Controller
public class CronController {
    @Resource
    private CronService cronService;

    /**
     * 停止调度任务
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/attemper/goods/task_stop"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData goodsTaskStop(HttpServletRequest request, @RequestParam("task_name") String taskName) throws Exception{
        return cronService.stopGoodsTask(taskName);
    }

    /**
     * 查看任务状态
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/attemper/goods/task_status"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData goodsTaskStauts(HttpServletRequest request) throws Exception{
        return cronService.goodsTaskingStatus();
    }

    /**
     * 添加任务执行
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/attemper/goods/task_push"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData batchUpdateByGoods(HttpServletRequest request, @RequestParam String sql, @RequestParam(defaultValue="0",required=false) int start, @RequestParam(defaultValue="0",required=false) int pbath, @RequestParam(defaultValue="0",required=false) int rows) throws Exception{
        return cronService.batchUpdateByGoodsId(sql,start, rows, pbath);
    }

    /**
     * 根据更新时间 添加产品
     * @param request
     * @return
     * @throws Exception
     */
		/*@RequestMapping(value={"/attemper/goodsCalculate/info/update_time"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData batchUpdateByGoods(HttpServletRequest request) throws Exception{
			return cronService.batchUpdateByGoods();
		}*/

    /**
     * 根据id添加产品
     * @param request
     * @param ids
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/attemper/goods/info/ids"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData attemperGoods(HttpServletRequest request, @RequestParam String ids) throws Exception{
        return cronService.addGoodsQueue(ids);
    }

    /**
     * 启动调度程序
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/attemper/goods/start"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData attemperStart(HttpServletRequest request) throws Exception{
        return cronService.startGoodsAttemper();
    }

    /**
     * 产品调度开关
     * @param request
     * @param flag
     * @return
     * @throws Exception
     */
    @RequestMapping(value={"/attemper/goods/off_on"}, method = {RequestMethod.POST})
    @ResponseBody
    public ReturnData attemperStart(HttpServletRequest request, @RequestParam Boolean flag) throws Exception{
        return cronService.goodsOffOrOnAttemper(flag);
    }
}
