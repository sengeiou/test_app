package com.io97.utils;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Creator: Owen
 * Date: 2015/1/22
 */
public class BeanUtil {


    public static Map<String, String> convertBean(Object bean) {
        Class type = bean.getClass();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            return new HashMap<String, String>();
        }

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        Map<String, String> returnMap = new HashMap<String, String>(propertyDescriptors.length);
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                doPut(descriptor, bean, returnMap, propertyName);
            }
        }
        return returnMap;
    }

    private static void doPut(PropertyDescriptor descriptor, Object bean, Map<String, String> returnMap, String propertyName) {
        Method readMethod = descriptor.getReadMethod();
        Object result = null;
        try {
            result = readMethod.invoke(bean, new Object[0]);
            if (result != null) {
                returnMap.put(propertyName, (String) result);
            } else {
                returnMap.put(propertyName, "");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
