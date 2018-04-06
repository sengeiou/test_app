package cn.bevol.statics.entity.user;


import cn.bevol.util.response.ReturnData;

import java.io.Serializable;

/**
 * 
 * 类型说明
 * 			surveys.put("1_D_11_16", "重度干性皮肤");
			surveys.put("1_D_17_26", "轻度干性皮肤");
			surveys.put("1_O_27_33", "轻度油性皮肤");
			surveys.put("1_O_34_44", "重度油性皮肤");
			
			surveys.put("3_R_18_24", "重度耐受性皮肤");
			surveys.put("3_R_25_29", "轻度耐受性皮肤");
			surveys.put("3_S_30_33", "轻度敏感性皮肤");
			surveys.put("3_S_34_72", "重度敏感性皮肤");
			
			surveys.put("4_N_10_30", "非色素沉着性皮肤");
			surveys.put("4_P_31_45", "色素沉着性皮肤");

			surveys.put("5_T_20_40", "紧致性皮肤");
			surveys.put("5_W_41_85", "皱纹性皮肤");

 * @author Administrator
 *
 */
public class SkinTestResult implements Serializable {
	
	public final static ReturnData ERROR_NOT_SKIN=new ReturnData(1,"没有此测试题");
	
//{"do":[],"done":{"1":{"id":"1","score":33,"result":"O","result_msg":"\u8f7b\u5ea6\u6cb9\u6027\u76ae\u80a4"},"3":{"id":"3","result":"S","result_msg":"\u8f7b\u5ea6\u654f\u611f\u6027\u76ae\u80a4"},"4":{"id":"4","score":28,"result":"N","result_msg":"\u975e\u8272\u7d20\u6c89\u7740\u6027\u76ae\u80a4"},"5":{"id":"5","result":"W","result_msg":"\u76b1\u7eb9\u6027\u76ae\u80a4"}},"result":"OSNW"}
	private Long id;
	/**
	 * 分数
	 */
	private Integer score=0;
	
	/**
	 * 肤质
	 */
	private String result;
	/**
	 * 
	 */
	private String skinResults;
	
	/**
	 * 肤质返回值
	 */
	private String result_msg;

	/**
	 * 肤质返回值
	 */
	private String resultMsg;
	
	

	public String getResult_msg() {
		return result_msg;
	}
	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
 	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public String getSkinResults() {
		return skinResults;
	}
	public void setSkinResults(String skinResults) {
		this.skinResults = skinResults;
	}
  	
	
}
