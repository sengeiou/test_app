package cn.bevol.internal.log.interceptor;

import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import cn.bevol.util.Log.LogStatisticsUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

/**
 * 拦截 返回值 
 * @author Administrator
 *
 */
public class ArgsHandlerMethodReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> type = returnType.getParameterType();  
            return true;  
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
		// TODO Auto-generated method stub
		if(returnValue instanceof ReturnData) {
			ReturnData rd=(ReturnData) returnValue;
			LogStatisticsUtils.putData("ret", rd.getRet());
			LogStatisticsUtils.putData("msg", rd.getMsg());
		} else if(returnValue instanceof ReturnListData){
			ReturnListData rd=(ReturnListData) returnValue;
			LogStatisticsUtils.putData("ret", rd.getRet());
			LogStatisticsUtils.putData("msg", rd.getMsg());
		} else if(returnValue instanceof Map) {
			Map rd = (Map) returnValue;
			if (rd.get("ret") == null)
				LogStatisticsUtils.putData("ret", -14);
			else
				LogStatisticsUtils.putData("ret", rd.get("ret"));
			if (rd.get("msg") != null)
				LogStatisticsUtils.putData("msg", rd.get("msg"));
		} else if(returnValue instanceof String) {
			//do nothing
		} else {
		 	LogStatisticsUtils.putData("ret", -15);
			LogStatisticsUtils.putData("msg", "");
		}
		return false;
	}


}
