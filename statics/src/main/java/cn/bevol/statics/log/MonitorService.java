package cn.bevol.statics.log;

import cn.bevol.util.Log.LogException;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mysens
 */
@Aspect
@Order(1)
public class MonitorService {

    public static String logLevel;

    /**
     * 线上级别，不打印异常
     */
    public final static String PRO = "pro";

    /**
     * 注解@LogMethod的切点
     * 用于方法
     */
    @Pointcut("@annotation(cn.bevol.util.Log.LogMethod)")
    public void logMethod(){}

    /**
     * 注解@LogClass的切点
     * 用于类
     */
    @Pointcut("@within(cn.bevol.util.Log.LogClass)")
    public void logClass(){}

    /**
     * service包下结尾为Log方法的切点
     */
    @Pointcut("execution(* cn.bevol..service.*Log(..))")
    public void logExecution(){}


    @Around("logMethod() || logClass() || logExecution()")
    public Object exceptionHandler(ProceedingJoinPoint joinPoint){
        Object returnValue = null;
        try{
            returnValue = joinPoint.proceed();
        }catch(Throwable e){
            Signature signature =  joinPoint.getSignature();
            //获取方法声明
            MethodSignature methodSignature = (MethodSignature) signature;
            //判断返回类型
            Class returnType = methodSignature.getReturnType();
            //获取异常返回值
            returnValue = getReturnValue(returnType);

            doLog(joinPoint, e);

        }
        return returnValue;
    }

    public void setLogLevel(String logLevel) {
        MonitorService.logLevel = logLevel;
    }

    public String getLogLevel() {
        return logLevel;
    }

    /**
     * 获取异常返回值
     * @param returnType
     * @return
     */
    public static Object getReturnValue(Object returnType){
        Object returnValue = null;
        if(returnType == ReturnData.class  ) {
            returnValue = ReturnData.ERROR;
        } else if(returnType == ReturnListData.class ){
            returnValue = ReturnListData.ERROR;
        } else if(returnType == Map.class ) {
            Map returnMap = new HashMap<>();
            returnMap.put("ret", -1);
            returnMap.put("msg", "异常错误");
            returnValue = returnMap;
        }
        return returnValue;
    }

    /**
     * 记录日志
     * @param joinPoint
     * @param e
     */
    public static void doLog(ProceedingJoinPoint joinPoint, Throwable e){
        Signature signature =  joinPoint.getSignature();
        //获取类名
        String clazzName = joinPoint.getTarget().getClass().getName();
        //获取方法名
        String methodName = joinPoint.getSignature().getName();
        //获取传参
        Object[] params = joinPoint.getArgs();
        //获取方法声明
        MethodSignature methodSignature = (MethodSignature) signature;
        //获取参数名
        String[] parameterNames =  methodSignature.getParameterNames();

        Map map = new HashMap(params.length+1);
        map.put("method", clazzName + "." +methodName);
        for(int i=0; i<params.length; i++){
            map.put(parameterNames[i], params[i]);
        }
        new LogException(e, map);

        if(!PRO.equals(logLevel)){
            e.printStackTrace();
        }
    }
}
