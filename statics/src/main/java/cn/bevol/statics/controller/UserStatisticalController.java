package cn.bevol.statics.controller;

import cn.bevol.statics.service.NoSearchStatisticalService;
import cn.bevol.statics.service.UserStatisticalService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserStatisticalController {
	@Resource
	private UserStatisticalService userStatisticalService;
	@Resource
	private NoSearchStatisticalService noSearchStatisticalService;
	
	/***
	 * 查询某一天活跃数据
	 * @param session
	 * @param request
	 * @param statisticsDate
	 * @return
	 */
	@RequestMapping(value = {"/static/Statistical/{statisticsDate}"})
	  @ResponseBody
	  public Object findByStatisticsDate(HttpSession session, HttpServletRequest request,
                                         @PathVariable String statisticsDate){
		return new ReturnData(userStatisticalService.findByStatisticsDate(statisticsDate));
	}
	/***
	 * 查询时间段内活跃数据
	 * @param session
	 * @param request
	 * @param beginTime 毫秒
	 * @param endTime 毫秒
	 * @return
	 */
	@RequestMapping(value = {"/static/findStatistical/{beginTime}/{endTime}"})
	  @ResponseBody
	  public Object findByDate(HttpSession session, HttpServletRequest request,
                               @PathVariable String beginTime, @PathVariable String endTime){
		return new ReturnData(userStatisticalService.findByPage(beginTime,endTime));
	}
	/***
	 * 近一天，近一周、近一月没搜索到的产品或成分统计
	 * @param session
	 * @param request
	 * @param tname
	 * @return
	 */
	@RequestMapping(value = {"/static/findNosearch/{tname}"})
	  @ResponseBody
	  public Object findByNoSerchResult(HttpSession session, HttpServletRequest request,
                                        @PathVariable String tname){
		return new ReturnData(noSearchStatisticalService.noResult(tname));
	}
	
	/***
	 * 记录当天活跃人数、登录数、启动数(定时任务)
	 * @param session
	 * @param request
	 * @return
	 */
	 @RequestMapping(value = {"/static/addUserStatisticalJob"})
	  @ResponseBody
	  public Object addUserStatisticalJob(HttpSession session, HttpServletRequest request){
		return new ReturnData(userStatisticalService.addUserStatistical());
	}
	 /***
	  * 查询某天活跃人数、登录数、启动数
	  * @param session
	  * @param request
	  * @param date4Today 20161212
	  * @return
	  */
	 @RequestMapping(value = {"/static/findUserStatistical/{date4Today}"})
	  @ResponseBody
	  public Object findUserStatisticalByDay(HttpSession session, HttpServletRequest request,
                                             @PathVariable String date4Today){
		return new ReturnData(userStatisticalService.findUserStatisticalByDay(date4Today));
	}
	  
}
