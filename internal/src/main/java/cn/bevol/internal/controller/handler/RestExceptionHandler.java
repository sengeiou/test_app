package cn.bevol.internal.controller.handler;

import cn.bevol.util.response.ReturnData;
import cn.bevol.util.Log.LogStatisticsUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mysens
 * @date 17-12-26 下午3:43
 */
@ControllerAdvice
public class RestExceptionHandler {
    /**
     * 文件大小控制
     * @param ex
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public ReturnData handleException(Exception ex) {
        Map<Object, Object> model = new HashMap<Object, Object>();
        //错误请求
        LogStatisticsUtils.putData("exception", true);
        LogStatisticsUtils.putData("trace", ExceptionUtils.getRootCauseMessage(ex));
        LogStatisticsUtils.putData("base_control",1);

        if (ex instanceof MaxUploadSizeExceededException){
            return new ReturnData(-1,"文件应不大于 "+ getFileKB(((MaxUploadSizeExceededException)ex).getMaxUploadSize()));
        } else{
            return ReturnData.ERROR;
        }
    }

    private String getFileKB(long byteFile){
        if(byteFile==0)
            return "0KB";
        long kb=1024;
        return ""+byteFile/kb+"KB";
    }
    private String getFileMB(long byteFile){
        if(byteFile==0)
            return "0MB";
        long mb=1024*1024;
        return ""+byteFile/mb+"MB";
    }
}
