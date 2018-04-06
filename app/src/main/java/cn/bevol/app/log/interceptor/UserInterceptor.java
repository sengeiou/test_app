package cn.bevol.app.log.interceptor;

import cn.bevol.app.entity.model.UserBlackList;
import cn.bevol.app.service.UserService;
import cn.bevol.util.ComReqModel;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.statistics.StatisticsI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 黑名单用户行为拦截
 * @author Administrator
 *
 */
@Component
public class UserInterceptor  extends HandlerInterceptorAdapter {

	@Autowired
	UserService userService;
	
	 @Autowired
     MongoTemplate mongoTemplate;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		long id = CommonUtils.getLoginCookieById(request);
		boolean longin = false;
		boolean blackName = false;
		if (id > 0) {
			longin = true;
		}

		// 没有登陆
		if (!longin) {
			String nologin = "{\"ret\":-5}";
			response.getWriter().println(nologin);
			return longin;
		}
		
		boolean flag=true;
		String reqUrl = request.getRequestURI().replace(request.getContextPath(), "").replace("//", "/");
		//实名认证拦截
		if (reqUrl.contains("/auth/idcard/verifyIdcardv2")) {
			flag=false;
		}
		
		if(!flag){
			//开关是否打开open:0开 1没开,最大次数maxNum:5
			Map<String,String> aMap = ConfUtils.getJSONMap("user_authentication");
			int open=Integer.parseInt(aMap.get("open"));
			if(0==open){
				int maxNum=Integer.parseInt(aMap.get("uuidMaxNum"));

				String uuid = request.getParameter(StatisticsI.KEY_UUID);
				String ip = ComReqModel.getIpAddr(request);
				String ret ="";
				//ip限制
				if(StringUtils.isNotBlank(ip)){
					//读配置
					Map<String,String> ipMap = ConfUtils.getJSONMap("interceptor_user_authentication_ip");
					boolean find=false;
					if(null!=ipMap && null!=ipMap.get("ip")){
						String[] ips=ipMap.get("ip").split(",");
						for(int i=0;!find&&i<ips.length;i++){
							if(ips[i].equals(ip)){
								find=true;
							}
						}
					}

					if(find){
						//ip被封
						ret = "{\"ret\":-4,\"msg\":\"请求次数过多!\"}";
						response.setCharacterEncoding("UTF-8");
						PrintWriter out = response.getWriter();
						out.println(ret);
						out.flush();
						out.close();
						return false;
					}
				}
				//uuid次数限制
				//uuid="12345";
				if(StringUtils.isNotBlank(uuid)){
					Map map=mongoTemplate.findOne(new Query(Criteria.where("uuid").is(uuid).and("updateStamp").lte(CommonUtils.curDayMaxAndMinTime(0)).gte(CommonUtils.curDayMaxAndMinTime(1))),HashMap.class,"user_authentication");
					if(null!=map&&null!=map.get("total")&&Long.parseLong(String.valueOf(map.get("total")))>maxNum){
						//大于次数
						ret = "{\"ret\":-4,\"msg\":\"请求次数过多!\"}";
						response.setCharacterEncoding("UTF-8");
						PrintWriter out = response.getWriter();
						out.println(ret);
						out.flush();
						out.close();
						return false;
					}
				}
			}
		}
		
		
		// 已经登录 是否是黑名单
		UserBlackList userBlack = userService.getUserBlackById(id);
		if(null!=userBlack && userBlack.getId()>0){
			if(userBlack.getUserId().intValue()==id){
				blackName = true;
			}
		}
		
		// 如果是黑名单的 不能评论,修改评论等
		if (blackName) {
			String blackRet = "{\"ret\":-4,\"msg\":\"你已被禁言,不能进行该操作!\"}";
			/**
			 * 需要拦截的接口
			 */
			// 发评论
			if (reqUrl.contains("/auth/entity/comment")) {
				flag=false;
				// 旧发评论
			} else if (reqUrl.contains("/auth/my/comment/update")) {
				flag=false;
				// 收藏
			} else if (reqUrl.contains("auth/entity/collection")) {
				flag=false;
				// 喜欢/心碎
			} else if (reqUrl.contains("auth/entity/like")) {
				flag=false;
				// 评论举报
			} else if (reqUrl.contains("/auth/user_part")) {
				flag=false;
				// 福利社 参与试用
			} else if (reqUrl.contains("/auth/apply_goods")) {
				flag=false;
				// 发送对比讨论
			} else if (reqUrl.contains("/auth/discuss/send")) {
				flag=false;
				// 对比的支持产品
			} else if (reqUrl.contains("/auth/like/")) {
				flag=false;
			}
			
			
			//拦截
			if(!flag){
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.println(blackRet);
				out.flush();
				out.close();
				return false;
			}else{//不拦截
				return true;
			}
			
		}
		
		
		return true;
	}

	@Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
