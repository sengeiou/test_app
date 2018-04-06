package cn.bevol.util.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 返回对象
 * @author hualong
 *
 */
public class ReturnListData<T> implements Serializable {
	
	public static final ReturnListData ERROR = new ReturnListData(-1,"错误");
	public static final ReturnListData SUCCESS = new ReturnListData(0,"");

	private List<T> result=new ArrayList<T>();
	
	private long total;
	private Integer ret=0;
	
	private String msg="";

	public ReturnListData(){};
	public ReturnListData(List<T> result,long total){
		this.result=result;
		this.total=total;
	};
	public ReturnListData(List<T> result,long total,int ret,String msg){
		this.result=result;
		this.ret=ret;
		this.msg=msg;
		this.total=total;
	};
	public ReturnListData(int ret,String msg){
		this.ret=ret;
		this.msg=msg;
	};
	
	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}
	public List<T> Tesult() {
		return result;
	}

	public Integer getRet() {
		return ret;
	}

	public void setRet(Integer ret) {
		this.ret = ret;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
