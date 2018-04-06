package cn.bevol.util.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * 返回对象
 *
 * @author hualong
 */
public class ReturnData<T> implements Serializable {
	
	/**
	 * 只用于异常处理
	 */

    //正确
    public static final ReturnData SUCCESS = new ReturnData(0, "操作成功");
    //失败
    public static final ReturnData FAILURE = new ReturnData(-3, "操作失败");
    //系统繁忙
    public static final ReturnData SYSTEM_IS_BUSY = new ReturnData(-2, "服务器繁忙");
    //系统错误
    public static final ReturnData SYSTEM_ERROR = new ReturnData(-1000, "系统错误");
    //没有权限执行操作
    public static final ReturnData OPERATION_NOT_PERMIT = new ReturnData(-6, "没有权限执行操作");
    //用户未登录
    public static final ReturnData NOT_LOGIN = new ReturnData(-7, "您还未登录或者登录超时，请重新登录");

    public static final ReturnData ERROR = new ReturnData(-1, "异常错误");

    public static  final ReturnData WARN_ADVERTISEMENT = new ReturnData(-1001,"该时间区间内有正在运行的广告！");
    public static  final ReturnData WARN_PUBLISHTIMEGTOVERDUETIME = new ReturnData(-1001,"结束时间小于开始时间！");
    public static  final ReturnData  INVALID_VISIT = new ReturnData(-1002,"非法访问");

    private T result;

    private Integer ret = 0;

    private String msg = "";

    public ReturnData() {
    }

    ;

    public ReturnData(T result) {
        this.result = result;
    }

    ;
    
    
    public ReturnData(T result,long total) {
        this.result = result;
    }

    ;

    public ReturnData(T result, int ret, String msg) {
        this.result = result;
        this.ret = ret;
        this.msg = msg;
    }
    public ReturnData(T result, int ret) {
        this.result = result;
        this.ret = ret;
    }

    ;

    public ReturnData(int ret, String msg) {
        this.ret = ret;
        this.msg = msg;
    }

    ;

    public Object getResult() {
        return result;
    }
    
    @JsonIgnore
    public T TResult() {
        return result;
    }

    private void setResult(T result) {
        this.result = result;
    }

    public Integer getRet() {
        return ret;
    }

    private void setRet(Integer ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
