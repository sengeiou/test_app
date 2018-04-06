package cn.bevol.staticc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StaticStarter {
    private static final Logger logger = LoggerFactory.getLogger(StaticStarter.class);

    /***
     * @param args 1:发现 2:资讯  3:产品 4:成分
     * @param args 5: PC站SEO推送 6: M站SEO推送 7:SEO定时任务
     * @param args 8:全部（不包含SEO）
     */
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
//        System.out.println("初始化成功！");
        logger.info("初始化成功！");
//        StaticLoggerBinder
        StaticFindService staticFindService = (StaticFindService) context.getBean("staticFindService");
        staticFindService.initFindStatic(Integer.parseInt(args[0])); //1
        staticFindService.initIndustryStatic(Integer.parseInt(args[0]));//2

        StaticGoodsService staticGoodsService = (StaticGoodsService) context.getBean("staticGoodsService");
        staticGoodsService.initStatic(Integer.parseInt(args[0]));//3

        StaticCompositionService compService = (StaticCompositionService) context.getBean("staticCompositionService");
        compService.initCompositionStatic(Integer.parseInt(args[0]));//
        SeoService seoService = (SeoService) context.getBean("seoService");
        seoService.seoMBatch(Integer.parseInt(args[0]));
        seoService.seoPCBatch(Integer.parseInt(args[0]));
        SeoAwaitService awaitService = new SeoAwaitService();
        awaitService.seoJob(Integer.parseInt(args[0]));

    }

}
