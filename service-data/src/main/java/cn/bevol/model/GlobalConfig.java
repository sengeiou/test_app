package cn.bevol.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import cn.bevol.mybatis.model.Composition;

@Document(collection="global_config")
public class GlobalConfig  implements Serializable{

	/**
	 * 当前注册的数量
	 */
	@Deprecated 
	private Long curRegisterNum;
	
	/**
	 * 当前手机注册量
	 */
	@Deprecated
	private Long curPhoneRegisterNum;
	/**
	 * 获取时间
	 */
	@Deprecated
	private Long curRegisterNumTime;
	
	/**
	 * 短信发送的数量
	 */
	private Long curSMSNum;

	
	/**
	 * 允许发送短信
	 */
	private Boolean allowSMS;

	
	/**
	 * 是否可以注册
	 */
	private Boolean allowReg;
	

	
	/**
	 * 注册人数
	 */
	private Long registerNum;
	
	
	/**
	 * 测试人数
	 */
	private Long testSkinCount;
	
	/**
	 * 产品数据
	 */
	private Long productCount;
	
	/**
	 * 成分数据
	 */
	private Long compositionCount;
	
	/**
	 * 喜欢成分列表
	 */
	private  List<Composition>  likeList;
	
	/**
	 * 不喜欢成分列表
	 */
	private List<Composition>  notLikeList; 
	
	/*
	 * 产品对比访问量接口
	 */
	private Long compareGoodsVistNum;
	
	public Long getRegisterNum() {
		return registerNum;
	}
	public void setRegisterNum(Long registerNum) {
		this.registerNum = registerNum;
	}
	public Long getTestSkinCount() {
		return testSkinCount;
	}
	public void setTestSkinCount(Long testSkinCount) {
		this.testSkinCount = testSkinCount;
	}
	public Long getProductCount() {
		return productCount;
	}
	public void setProductCount(Long productCount) {
		this.productCount = productCount;
	}
	public Long getCompositionCount() {
		return compositionCount;
	}
	public void setCompositionCount(Long compositionCount) {
		this.compositionCount = compositionCount;
	}
	public Long getCurRegisterNum() {
		return curRegisterNum;
	}
	public void setCurRegisterNum(Long curRegisterNum) {
		this.curRegisterNum = curRegisterNum;
	}
 	public List<Composition> getLikeList() {
		return likeList;
	}
	public void setLikeList(List<Composition> likeList) {
		this.likeList = likeList;
	}
	public List<Composition> getNotLikeList() {
		return notLikeList;
	}
	public void setNotLikeList(List<Composition> notLikeList) {
		this.notLikeList = notLikeList;
	}
	public Long getCurRegisterNumTime() {
		return curRegisterNumTime;
	}
	public void setCurRegisterNumTime(Long curRegisterNumTime) {
		this.curRegisterNumTime = curRegisterNumTime;
	}
	public Long getCurPhoneRegisterNum() {
		return curPhoneRegisterNum;
	}
	public void setCurPhoneRegisterNum(Long curPhoneRegisterNum) {
		this.curPhoneRegisterNum = curPhoneRegisterNum;
	}
	public Boolean getAllowReg() {
		return allowReg;
	}
	public void setAllowReg(Boolean allowReg) {
		this.allowReg = allowReg;
	}
	public Long getCurSMSNum() {
		return curSMSNum;
	}
	public void setCurSMSNum(Long curSMSNum) {
		this.curSMSNum = curSMSNum;
	}
	public Boolean getAllowSMS() {
		return allowSMS;
	}
	public void setAllowSMS(Boolean allowSMS) {
		this.allowSMS = allowSMS;
	}
	public Long getCompareGoodsVistNum() {
		return compareGoodsVistNum;
	}
	public void setCompareGoodsVistNum(Long compareGoodsVistNum) {
		this.compareGoodsVistNum = compareGoodsVistNum;
	}
 	
 	
}
