package cn.bevol.statics.listener;

import cn.bevol.statics.service.AdvertisementLogService;
import cn.bevol.statics.service.SidebarService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Calendar;
import java.util.Date;

/**
 * 初始化spring
 * @author Administrator
 *
 */
@WebListener
public class SpringInitsListener implements ServletContextListener  {
	private static final Logger logger = LoggerFactory.getLogger(SpringInitsListener.class);
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		WebApplicationContext wac= WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		AdvertisementLogService service =wac.getBean(AdvertisementLogService.class);
		service.initADLog();
		logger.info("===========日志监听开启================");

		//启动pc侧边栏缓存
		SidebarService sidebarService = wac.getBean("sidebarService", SidebarService.class);
		sidebarService.generateSideBarCache();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}  	
	public static void main(String[] args) {
		
		System.out.println(DateUtils.truncate(DateUtils.addHours(new Date(), -2), Calendar.HOUR));
	}


}
