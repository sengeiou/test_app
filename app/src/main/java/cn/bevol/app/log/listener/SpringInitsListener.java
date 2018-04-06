package cn.bevol.app.log.listener;

import cn.bevol.app.config.CloudSearchClientConfig;
import cn.bevol.app.entity.vo.GoodsExplain;
import cn.bevol.app.service.*;
import cn.bevol.util.Log.LogMethod;
import cn.bevol.util.response.ReturnData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 初始化spring
 * @author Administrator
 *
 */
@WebListener
@Configuration
public class SpringInitsListener implements ServletContextListener  {
    private static final Logger logger = LoggerFactory.getLogger(SpringInitsListener.class);

    @Autowired
    CloudSearchClientConfig client;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        WebApplicationContext wac= WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        //this.hotCache(wac);
        AdvertisementLogService service =wac.getBean(AdvertisementLogService.class);
        service.initADLog();
        logger.info("===========日志监听开启================");

        //启动pc侧边栏缓存
		/*SidebarService sidebarService = wac.getBean("sidebarService", SidebarService.class);
		sidebarService.generateSideBarCache();*/
    }

    @LogMethod
    public void hotCache(WebApplicationContext wac){
        GoodsService goodsService =wac.getBean(GoodsService.class);
		/*if(GoodsService.islocalcache) {
			//本地缓存
			goodsService.goodsCache();
		} else {
			//redis缓存
			goodsService.goodsCacheByRedis();
		}*/


        /**
         *  1各热门产品或资源评论首页列表,缓存产品详情和产品评论---[{"id":283202,"mid":"123teh"}]读配置
         *  2初始化接口,init系列,index系列
         *  3其他可能较慢资源(根据响应速度)
         */
        //产品和产品评论
        String hotState=client.getHotEntity();
        if(hotState!=null&&hotState.equals("0")) {
            EntityService entityService =wac.getBean(EntityService.class);
            //Map midsMap=entityService.getConfigMap("goods_mids");
            Map midsMap=goodsService.getHotGoodsMids();
            String tname="goods";
            if(null!=midsMap&&null!=midsMap.get("mids")){
                CommentService commentService =wac.getBean(CommentService.class);
                int type=0;
                int startId=0;
                int pageSize=20;
                long userId=0L;
                String[] mids=String.valueOf(midsMap.get("mids")).split(",");
                for(int i=0;i<mids.length;i++){
                    //产品
                    ReturnData<GoodsExplain> rd=goodsService.getGoodsExplain(mids[i],null);
                    GoodsExplain goodsExplain = (GoodsExplain) rd.getResult();
                    if(null!=goodsExplain&&null!=goodsExplain.getGoods()){
                        long entityId=goodsExplain.getGoods().getId();
                        //产品评论列表
                        type=1;
                        commentService.findSourceComments(tname, entityId, userId, startId, pageSize,type,"");
                    }
                }
            }

            //init,index
            List<Map<String,Object>> serviceList=entityService.getConfigList("home_method");
            IndexService indexService =wac.getBean(IndexService.class);
            if(null!=serviceList&&serviceList.size()>0&&null!=serviceList.get(0)){
                for(Map map:serviceList){
                    if(null!=map.get("method")){
                        Object[] obj=null;
                        if(null!=map.get("params")&& StringUtils.isNotBlank(String.valueOf(map.get("params")))){
                            String[] params=String.valueOf(map.get("params")).split(",");
                            obj=new Object[params.length];
                            for(int i=0;i<params.length;i++){
                                if(params[i].indexOf("int")!=-1){
                                    String p=params[i].substring(0,params[i].lastIndexOf("."));
                                    obj[i]=Integer.parseInt(p);
                                }else if(params[i].indexOf("long")!=-1){
                                    String p=params[i].substring(0,params[i].lastIndexOf("."));
                                    obj[i]=Long.parseLong(p);

                                }else{
                                    obj[i]=params[i];
                                }
                            }

                        }

                        try {
                            if(null!=obj){
                                MethodUtils.invokeMethod(indexService, String.valueOf(map.get("method")),obj);
                            }else{
                                MethodUtils.invokeMethod(indexService, String.valueOf(map.get("method")));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
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
