package cn.bevol.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  记录正常日志
 * @author hualong
 *
 */
public class LogRetLt0 {
    private static Logger logger = LoggerFactory.getLogger(LogRetLt0.class);
 	public static void log(String log) {
 		logger.info(log);
	}
 
}
