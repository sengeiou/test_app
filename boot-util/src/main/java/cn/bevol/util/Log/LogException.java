package cn.bevol.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 *  记录异常日志
 * @author hualong
 *
 */
public class LogException extends Exception {
	private static Logger logger = LoggerFactory.getLogger(LogException.class);

	public static void error(String log) {
		logger.error(log);
	}

	public LogException(Throwable e,Map<String,Object> args) {
		//异常日志
		LogStatisticsUtils.putData("exception", true);

		Integer num=(Integer) LogStatisticsUtils.getData("num");
		if(num==null) num=0;

		num++;
		LogStatisticsUtils.putData("num",num);

		LogStatisticsUtils.putData("trace_"+num, ExceptionUtils.getRootCauseMessage(e));

		StackTraceElement ste = e.getStackTrace()[0];
		LogStatisticsUtils.putData("trace_line_"+num, ste.getLineNumber());
		LogStatisticsUtils.putData("trace_method_"+num, ste.getClassName()+"."+ste.getMethodName());

		for(String key:args.keySet()) {
			LogStatisticsUtils.putData("exf_"+num+"_"+key, args.get(key));
		}
	}

	public LogException(Throwable e) {
		//异常日志
		LogStatisticsUtils.putData("exception", true);
		LogStatisticsUtils.putData("trace", ExceptionUtils.getStackTrace(e));
	}



}
