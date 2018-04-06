package cn.bevol.internal.listener;

import cn.bevol.internal.log.MonitorService;
import cn.bevol.internal.service.InternalGoodsCalculateService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mysens on 17-6-6.
 */
@WebListener
public class SpringInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext wac= WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        if(MonitorService.PRO.equals(MonitorService.logLevel)){
            InternalGoodsCalculateService backGoodsCalculateService =wac.getBean(InternalGoodsCalculateService.class);
            if(InternalGoodsCalculateService.islocalcache) {
                backGoodsCalculateService.goodsCalculateLocalCache();
            }else{
                backGoodsCalculateService.goodsCalculateRedisCache();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }
    public static void main(String[] args) {

        System.out.println(DateUtils.truncate(DateUtils.addHours(new Date(), -2), Calendar.HOUR));
    }
}
