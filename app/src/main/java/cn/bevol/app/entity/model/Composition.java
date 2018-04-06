package cn.bevol.app.entity.model;

import cn.bevol.util.DateUtils;
import cn.bevol.util.response.ReturnListData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Composition implements Serializable  {
	
	/**
	 * 成分最大对比长度
	 */
	public static final ReturnListData COMPARES_LENGHT_MAX = new ReturnListData(-3,"一次对比的成分不能超过100个");

	/**
	 * 自增业务id
	 */
	private Long id;
	
	/*
	 * 最开始的id
	 */
	private Long srcId;
	
	private Long oid;
	public String getEfficacy() {
		return efficacy; 
	}
	public void setEfficacy(String efficacy) {
		this.efficacy = efficacy;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public Integer getFrequency() {
		return frequency;
	}
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getAcneRisk() {
		return acneRisk;
	}
	public void setAcneRisk(String acneRisk) {
		this.acneRisk = acneRisk;
	}
	public String getCas() {
		return cas;
	}
	public void setCas(String cas) {
		this.cas = cas;
	}
	public String getOtherTitle() {
		return otherTitle;
	}
	public void setOtherTitle(String otherTitle) {
		this.otherTitle = otherTitle;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUsedNum() {
		return usedNum;
	}
	public void setUsedNum(String usedNum) {
		this.usedNum = usedNum;
	}
	public String getShenyong() {
		return shenyong;
	}
	public void setShenyong(String shenyong) {
		this.shenyong = shenyong;
	}
	public String getUsed() {
		return used;
	}
	public void setUsed(String used) {
		this.used = used;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	
	public Long getOid() {
		return oid;
	}
	public void setOid(Long oid) {
		this.oid = oid;
	}

	@JsonIgnore
	private Integer hidden=0;
	@JsonIgnore
	private Integer deleted=0;
	
    /**
     * 修改时间
     */
	@Field
    private Long updateStamp= DateUtils.nowInMillis()/1000;
    /**
     * 数据的系统创建时间 统一用createTime
     */
	@Field
    private Long createStamp=DateUtils.nowInMillis()/1000;
	
    private Date crdate;

	/**
	 * 实体标题
	 */
	private String title;

	/**
	 *  用户输入标题
	 */
	private String userTitle;


	/**
	 *  过滤之后的字段
	 */
	private String fliteredTitle;


	/**
	 *使用功效
	 */
	private String efficacy;
	
	/**
	 *  使用目的
	 */
	private String english;
	
	
	/**
	 * 使用频率
	 */
	private Integer frequency;
	
	/**
	 * 活性成分
	 */
	private String active;
	/**
	 * 治痘风险
	 */
	private String acneRisk;
	private String cas;
	/**
	 * 其他名称
	 */
	private String otherTitle;

	private String remark;
	
	/**
	 * 使用频率
	 */
	private String usedNum;
	
	private String shenyong;
	
	private String used;
	
	private List<Used> useds;
	
	/**
	 * 当前功效对应的使用目的
	 */
	private String curUsedName;

	
	private String safety;
	
	private Long pid;
	
	private String mid;
	
	private String mPid;

	//适合我的肤质
	private String OSPW;
	
	private String OSPT;

	private String OSNW;

	private String OSNT;
	private String ORPW;
	
	
	private String ORNW;
	
	private String ORNT;
	private String DSPW;
	
	
	private String DSPT;
	
	
	private String DSNW;
	
	
	private String DSNT;
	
	
	private String DRPW;
	
	
	private String DRPT;
	private String DRNW;
	private String DRNT;

	private String ORPT;
	
	/**
	 * 脏数据id
	 */
	private Long dirtyId;
	
	private String usedTitle;

	
	/**
	 * 去掉符号的名称
	 */
	private String cmTitle;

	/**
	 * 去掉符号的英文
	 */
	private String cmEnglish;

 	public String getCmTitle() {
		return cmTitle;
	}
	public void setCmTitle(String cmTitle) {
		this.cmTitle = cmTitle;
	}
	public String getCmEnglish() {
		return cmEnglish;
	}
	public void setCmEnglish(String cmEnglish) {
		this.cmEnglish = cmEnglish;
	}
	public Long getSrcId() {
		if(pid!=null&&pid>0) {
			srcId=pid;
		} else {
			srcId=id;
		}
		return srcId;
	}
	public void setSrcId(Long srcId) {
		this.srcId = srcId;
	}
	public Long getDirtyId() {
		return dirtyId;
	}
	public void setDirtyId(Long dirtyId) {
		this.dirtyId = dirtyId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getHidden() {
		return hidden;
	}
	public void setHidden(Integer hidden) {
		this.hidden = hidden;
	}
	public Integer getDeleted() {
		return deleted;
	}
	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}
	public Long getUpdateStamp() {
		return updateStamp;
	}
	public void setUpdateStamp(Long updateStamp) {
		this.updateStamp = updateStamp;
	}
	public Long getCreateStamp() {
		return createStamp;
	}
	public void setCreateStamp(Long createStamp) {
		this.createStamp = createStamp;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSafety() {
		return safety;
	}
	public void setSafety(String safety) {
		this.safety = safety;
	}
	public String getOSPW() {
		return OSPW;
	}
	public void setOSPW(String oSPW) {
		OSPW = oSPW;
	}
	public String getOSPT() {
		return OSPT;
	}
	public void setOSPT(String oSPT) {
		OSPT = oSPT;
	}
	public String getOSNW() {
		return OSNW;
	}
	public void setOSNW(String oSNW) {
		OSNW = oSNW;
	}
	public String getOSNT() {
		return OSNT;
	}
	public void setOSNT(String oSNT) {
		OSNT = oSNT;
	}
	public String getORPW() {
		return ORPW;
	}
	public void setORPW(String oRPW) {
		ORPW = oRPW;
	}
	public String getORNW() {
		return ORNW;
	}
	public void setORNW(String oRNW) {
		ORNW = oRNW;
	}
	public String getORNT() {
		return ORNT;
	}
	public void setORNT(String oRNT) {
		ORNT = oRNT;
	}
	public String getDSPW() {
		return DSPW;
	}
	public void setDSPW(String dSPW) {
		DSPW = dSPW;
	}
	public String getDSPT() {
		return DSPT;
	}
	public void setDSPT(String dSPT) {
		DSPT = dSPT;
	}
	public String getDSNW() {
		return DSNW;
	}
	public void setDSNW(String dSNW) {
		DSNW = dSNW;
	}
	public String getDSNT() {
		return DSNT;
	}
	public void setDSNT(String dSNT) {
		DSNT = dSNT;
	}
	public String getDRPW() {
		return DRPW;
	}
	public void setDRPW(String dRPW) {
		DRPW = dRPW;
	}
	public String getDRPT() {
		return DRPT;
	}
	public void setDRPT(String dRPT) {
		DRPT = dRPT;
	}
	public String getDRNW() {
		return DRNW;
	}
	public void setDRNW(String dRNW) {
		DRNW = dRNW;
	}
	public String getDRNT() {
		return DRNT;
	}
	public void setDRNT(String dRNT) {
		DRNT = dRNT;
	}
	public String getORPT() {
		return ORPT;
	}
	public void setORPT(String oRPT) {
		ORPT = oRPT;
	}
  	public List<Used> getUseds() {
		if(useds==null) useds=new ArrayList<Used>();
		return useds;
	}
	public void setUseds(List<Used> useds) {
		this.useds = useds;
	}
 
	public String getCurUsedName() {
		if(StringUtils.isBlank(curUsedName)) {
			if(this.useds!=null&&this.useds.size()>0) {
				curUsedName=this.useds.get(0).getTitle();
			}
		}
		return curUsedName;
	}
	public void setCurUsedName(String curUsedName) {
		this.curUsedName = curUsedName;
	}
	/*
	 * 复制成分
	 */
	public void copyCompositon(Composition c) {
		this.setId(c.getId());
		this.setTitle(c.getTitle());
		this.setAcneRisk(c.getAcneRisk());
		this.setActive(c.getActive());
		this.setSafety(c.getSafety());
		this.setShenyong(c.getShenyong());
		this.setUseds(c.getUseds());
		this.setUsedNum(c.getUsedNum());
		
		this.setMid(c.getMid());
		this.setmPid(c.getmPid());

	}
	public String getUserTitle() {
		return userTitle;
	}
	public void setUserTitle(String userTitle) {
		this.userTitle = userTitle;
	}
	public String getmPid() {
		return mPid;
	}
	public void setmPid(String mPid) {
		this.mPid = mPid;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	public String getFliteredTitle() {
		return fliteredTitle;
	}
	public void setFliteredTitle(String fliteredTitle) {
		this.fliteredTitle = fliteredTitle;
	}
	public String getUsedTitle() {
		return usedTitle;
	}
	public void setUsedTitle(String usedTitle) {
		this.usedTitle = usedTitle;
	}
	
 }
