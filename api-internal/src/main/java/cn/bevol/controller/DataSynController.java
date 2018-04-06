package cn.bevol.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.bevol.data.synchronize.service.DataSynService;
import cn.bevol.util.ReturnData;




/**
 * 数据同步接口
 * @author hualong
 *
 */
@Controller
public class DataSynController {
	   
	   
		@Autowired
		private DataSynService dataSynService;
		
		/**
		 * 更新全局缓存
		 * @param request
		 * @param name
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/syn/globle/info"})
		@ResponseBody
		public ReturnData globleInfo(HttpServletRequest request,@RequestParam String name) throws Exception{
				
			    MethodUtils.invokeMethod(dataSynService, name);

				return ReturnData.SUCCESS;
		}

		/**
		 * 同步实体统计数据
		 * @param tname 实体名称
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/syn/statistics/{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData statistics(HttpServletRequest request,@PathVariable String tname) throws Exception{
			//if(type.equals("opensearch")) {
			//	return dataSynService.entityStatisticsToOpeanSearch(tname);
			//} else if(type.equals("mysql")){
				return dataSynService.entityStatisticsToMysql(tname);
			//}
			//return ReturnData.ERROR;
		}
		 
		/**
		 * json方式提交实体搜索
		 * @param index_name 索引名称
		 * @param app_table 搜索表名称
		 * @param vals 提交的json数组 
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/syn/search"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData feedbackReply(HttpServletRequest request,@RequestParam String index_name,@RequestParam String app_table,@RequestParam String vals) throws Exception{
			ReturnData rd=dataSynService.toOpeanSearch( index_name, app_table, vals);
			//重新缓存user
			 return rd;
		}
		
		/**
		 *  mongo 到opeansarch 的同步
		 * @param tname 实体名称
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/syn/mongodb_opeansarch/{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData mongodbOpeansarch(HttpServletRequest request,@PathVariable String tname) throws Exception{
			return dataSynService.mongodbToOpeanSearch(tname);
		}


		/**
		 * mysql到 mongo的同步
		 * @param tname 实体名称
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/syn/mysql_mongo/{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData mysqlMongo(HttpServletRequest request,@PathVariable String tname) throws Exception{
			return dataSynService.mysqlToMongo(tname);
		}
		 
		/**
		 *  mongo 到mysql 的同步
		 * @param tname 实体名称
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/back/syn/mongo_mysql/{tname}"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData mongoMysql(HttpServletRequest request,@PathVariable String tname) throws Exception{
			return dataSynService.mongoToMysql(tname);
		}

		
		
		/**
		 * 根据更新时间 添加产品
		 * @param request
		 * @return
		 * @throws Exception
		 */
		/*@RequestMapping(value={"/attemper/goods/info/update_time"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData batchUpdateByGoods(HttpServletRequest request) throws Exception{
			return dataSynService.batchUpdateByGoods();
		}*/
		

		/**
		 * shell脚本执行
		 * @param request
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value={"/shell"}, method = {RequestMethod.POST})
		@ResponseBody
		public ReturnData shell(HttpServletRequest request, @RequestParam(name="shell_string") String shell) throws Exception{
			return dataSynService.shellExc(shell);
		}



}
