package com.io97.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextUtils implements ApplicationContextAware {

	private volatile static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextUtils.applicationContext = applicationContext;
	}

	public static Object getBean(String name){
		if(ApplicationContextUtils.applicationContext!=null)
			return ApplicationContextUtils.applicationContext.getBean(name);
		return null;
	}
	
	public static <T> T getBean(Class<T> requiredType){
		if(ApplicationContextUtils.applicationContext!=null)
			return ApplicationContextUtils.applicationContext.getBean(requiredType);
		return null;
	}
	
	public static ApplicationContext getApplicationContext(){
		return ApplicationContextUtils.applicationContext;
	}
}
