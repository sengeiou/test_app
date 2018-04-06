package cn.bevol.app.entity.dto;

import cn.bevol.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by mysens on 17-5-9.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class CompositionDTO {
    private Long id;
    @JsonIgnore
    private Integer[] idsArr;
    private String name;
    private String english;
    private String used;
    private String efficacy;
    private Integer usedNum;
    private String active;
    private String safety;
    private Integer acneRisk;
    private String shenyong;
    private String cas;
    private Integer pid;
    private String otherTitle;
    private String remark;
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
    private String mid;
    private Integer tstamp;
    private Integer crdate;
    private Integer namelength;
    private Integer u1;
    private String usedName;
    @JsonIgnore
    private String cmName;
    @JsonIgnore
    private String cmEnglish;

    public Integer getU1() {
        return u1;
    }

    public void setU1(Integer u1) {
        this.u1 = u1;
    }

    public String getUsedName() {
        return usedName;
    }

    public void setUsedName(String usedName) {
        this.usedName = usedName;
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

    private Integer hidden;
    private Integer deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtil.sanitizedName(name);
        setCmName(name);
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = StringUtil.sanitizedName(english);
        setCmEnglish(english);
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getEfficacy() {
        return efficacy;
    }

    public void setEfficacy(String efficacy) {
        this.efficacy = efficacy;
    }

    public Integer getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }

    public Integer getAcneRisk() {
        return acneRisk;
    }

    public void setAcneRisk(Integer acneRisk) {
        this.acneRisk = acneRisk;
    }

    public String getShenyong() {
        return shenyong;
    }

    public void setShenyong(String shenyong) {
        this.shenyong = shenyong;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
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

    public String getOSPW() {
        return OSPW;
    }

    public void setOSPW(String OSPW) {
        this.OSPW = OSPW;
    }

    public String getOSPT() {
        return OSPT;
    }

    public void setOSPT(String OSPT) {
        this.OSPT = OSPT;
    }

    public String getOSNW() {
        return OSNW;
    }

    public void setOSNW(String OSNW) {
        this.OSNW = OSNW;
    }

    public String getOSNT() {
        return OSNT;
    }

    public void setOSNT(String OSNT) {
        this.OSNT = OSNT;
    }

    public String getORPW() {
        return ORPW;
    }

    public void setORPW(String ORPW) {
        this.ORPW = ORPW;
    }

    public String getORNW() {
        return ORNW;
    }

    public void setORNW(String ORNW) {
        this.ORNW = ORNW;
    }

    public String getORNT() {
        return ORNT;
    }

    public void setORNT(String ORNT) {
        this.ORNT = ORNT;
    }

    public String getDSPW() {
        return DSPW;
    }

    public void setDSPW(String DSPW) {
        this.DSPW = DSPW;
    }

    public String getDSPT() {
        return DSPT;
    }

    public void setDSPT(String DSPT) {
        this.DSPT = DSPT;
    }

    public String getDSNW() {
        return DSNW;
    }

    public void setDSNW(String DSNW) {
        this.DSNW = DSNW;
    }

    public String getDSNT() {
        return DSNT;
    }

    public void setDSNT(String DSNT) {
        this.DSNT = DSNT;
    }

    public String getDRPW() {
        return DRPW;
    }

    public void setDRPW(String DRPW) {
        this.DRPW = DRPW;
    }

    public String getDRPT() {
        return DRPT;
    }

    public void setDRPT(String DRPT) {
        this.DRPT = DRPT;
    }

    public String getDRNW() {
        return DRNW;
    }

    public void setDRNW(String DRNW) {
        this.DRNW = DRNW;
    }

    public String getDRNT() {
        return DRNT;
    }

    public void setDRNT(String DRNT) {
        this.DRNT = DRNT;
    }

    public String getORPT() {
        return ORPT;
    }

    public void setORPT(String ORPT) {
        this.ORPT = ORPT;
    }

    public Integer[] getIdsArr() {
        return idsArr;
    }

    public void setIdsArr(Integer[] idsArr) {
        this.idsArr = idsArr;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Integer getTstamp() {
        return tstamp;
    }

    public void setTstamp(Integer tstamp) {
        this.tstamp = tstamp;
    }

    public Integer getCrdate() {
        return crdate;
    }

    public void setCrdate(Integer crdate) {
        this.crdate = crdate;
    }

    public Integer getNamelength() {
        return namelength;
    }

    public void setNamelength(Integer namelength) {
        this.namelength = namelength;
    }

    public String getCmName() {
        return cmName;
    }

    public void setCmName(String cmName) {
        this.cmName = StringUtil.formatStandardName(cmName);
    }

    public String getCmEnglish() {
        return cmEnglish;
    }

    public void setCmEnglish(String cmEnglish) {
        this.cmEnglish = StringUtil.formatStandardName(cmEnglish);
    }
}
