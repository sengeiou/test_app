package cn.bevol.util.response;

public interface GlobalResponseCode {

	//正确
	public static final ResponseCode SUCCESS = new ResponseCode("0", "操作成功");
	//失败
	public static final ResponseCode FAILURE = new ResponseCode("-1", "操作失败");
	//系统繁忙
	public static final ResponseCode SYSTEM_IS_BUSY = new ResponseCode("-2", "服务器繁忙");
	//系统错误
	public static final ResponseCode SYSTEM_ERROR = new ResponseCode("-1000", "系统错误");
	//没有权限执行操作
	public static final ResponseCode OPERATION_NOT_PERMIT = new ResponseCode("-6", "没有权限执行操作");
	//用户未登录
	public static final ResponseCode NOT_LOGIN = new ResponseCode("-7", "未登录");
	
	public static class ResponseCode{
		
		private String code;
		
		private String msg;
		
		public ResponseCode(String code, String msg){
			this.code = code;
			this.msg = msg;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
