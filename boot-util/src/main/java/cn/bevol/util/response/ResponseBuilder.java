package cn.bevol.util.response;


import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Map;

public class ResponseBuilder {
    public static final String OPERATE_SUCCESS = "操作成功";
    public static final String OPERATE_FAIL = "操作失败";


    public static Map<String, Object> buildMessage(GlobalResponseCode.ResponseCode responseCode, String message) {
        Assert.notNull(responseCode);
        Map<String, Object> result = Maps.newHashMap();
        result.put(GlobalResponseKey.RETURN, responseCode.getCode());
        if (StringUtils.isNotBlank(message))
            result.put(GlobalResponseKey.MSG, message);
        return result;
    }

    public static Map<String, Object> buildResult(GlobalResponseCode.ResponseCode responseCode, Object bean) {
        Assert.notNull(responseCode);
        Map<String, Object> result = Maps.newHashMap();
        result.put(GlobalResponseKey.RETURN, responseCode.getCode());
        if (bean != null)
            result.put(GlobalResponseKey.RESULT, bean);
        return result;
    }


    public static Map<String, Object> buildSuccessMessage(String message) {
        return buildMessage(GlobalResponseCode.SUCCESS, message);
    }

    public static Map<String, Object> buildFailureMessage(String message) {
        return buildMessage(GlobalResponseCode.FAILURE, message);
    }

    public static Map<String, Object> buildFailureMessage() {
        return buildMessage(GlobalResponseCode.FAILURE, OPERATE_FAIL);
    }

    public static Map<String, Object> buildResult(Object bean) {
        return buildResult(GlobalResponseCode.SUCCESS, bean);
    }


    public static Map<String, Object> success(String message) {
        return ResponseBuilder.buildSuccessMessage(message);
    }

    public static Map<String, Object> failure(String message) {
        return ResponseBuilder.buildFailureMessage(message);
    }

    public static Map<String, Object> success() {
        return ResponseBuilder.buildSuccessMessage(null);
    }

    public static Map<String, Object> failure() {
        return ResponseBuilder.buildFailureMessage(null);
    }

    public static Map<String, Object> error(GlobalResponseCode.ResponseCode responseCode) {
        return ResponseBuilder.buildMessage(responseCode, responseCode.getMsg());
    }

    public static Map<String, Object> error(GlobalResponseCode.ResponseCode responseCode, String message) {
        return ResponseBuilder.buildMessage(responseCode, message);
    }

    public static Map buildSuccessMessage() {
        return buildSuccessMessage(OPERATE_SUCCESS);
    }
}
