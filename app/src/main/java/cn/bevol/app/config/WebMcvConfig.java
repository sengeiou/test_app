package cn.bevol.app.config;

import cn.bevol.app.log.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author mysens
 * @date 17-12-26 下午1:59
 */
@Configuration
public class WebMcvConfig extends WebMvcConfigurerAdapter{

    @Autowired
    AllInterceptor allInterceptor;

    @Autowired
    LoginInterceptor loginInterceptor;

    @Autowired
    UserInterceptor userInterceptor;

    @Autowired
    StatisticsInterceptor statisticsInterceptor;



    /**
     * 拦截器注册
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(allInterceptor).addPathPatterns("/**").excludePathPatterns("/", "/state");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/my/**").addPathPatterns("/**/auth/**")
                .addPathPatterns("/**/u/**").addPathPatterns("/auth/entity/relation/**");
        registry.addInterceptor(userInterceptor).addPathPatterns("/**/auth/**");
        registry.addInterceptor(statisticsInterceptor).addPathPatterns("/**/survey/**");
        super.addInterceptors(registry);
    }

    /**
     * returnValueHandler配置
     * @param returnValueHandlers
     */
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(new ArgsHandlerMethodReturnValueHandler());
        returnValueHandlers.add(new CacheApiReturnValueHandler());
    }

    /**
     * 跨域共享
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //小程序单独配置
        registry.addMapping("/widget/**")
                .allowedOrigins("*").allowedMethods("POST");

        registry.addMapping("/**")
                .allowedOrigins("http://demo.bevol.cn",
                        "https://istatic.bevol.cn",
                        "https://static.bevol.cn",
                        "https://localpm.bevol.cn",
                        "https://local.bevol.cn",
                        "https://manage.bevol.cn",
                        "https://pm.bevol.cn",
                        "https://t.bevol.cn",
                        "https://www.bevol.cn",
                        "https://mv.bevol.cn",
                        "https://m.bevol.cn",
                        "http://static.bevol.cn",
                        "file://",
                        "http://localpm.bevol.cn",
                        "http://local.bevol.cn",
                        "http://manage.bevol.cn",
                        "http://pm.bevol.cn",
                        "http://t.bevol.cn",
                        "http://www.bevol.cn",
                        "http://m.bevol.cn",
                        "http://mv.bevol.cn",
                        "http://istatic.bevol.cn",
                        "127.0.0.1")
                .allowedMethods("GET", "POST")
                .allowCredentials(true).maxAge(1800);
    }

}
