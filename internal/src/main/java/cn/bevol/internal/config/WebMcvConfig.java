package cn.bevol.internal.config;

import cn.bevol.internal.log.interceptor.AllInterceptor;
import cn.bevol.internal.log.interceptor.ArgsHandlerMethodReturnValueHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author mysens
 * @date 17-12-26 下午1:59
 */
@Configuration
public class WebMcvConfig extends WebMvcConfigurerAdapter{

    /**
     * 拦截器注册
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AllInterceptor()).addPathPatterns("/**");
    }

    /**
     * returnValueHandler配置
     * @param returnValueHandlers
     */
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(new ArgsHandlerMethodReturnValueHandler());
    }

}
