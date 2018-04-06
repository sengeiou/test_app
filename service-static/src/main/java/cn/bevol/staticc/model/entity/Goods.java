package cn.bevol.staticc.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Goods implements Serializable  {
 
	
	/**
	 * 实体图片
	 */
	private String image;
	
	private String mid;
	
	/**
	 * 实体标题
	 */
	private String title;

	/**
	 * 自增业务id
	 */
	private Long id;
	
	@JsonIgnore
	private Integer hidden=0;
	@JsonIgnore
	private Integer deleted=0;
	
    /**
     * 修改时间
     */
    private Long updateStamp=new Date().getTime()/1000;
    /**
     * 数据的系统创建时间 统一用createTime
     */
    private Long createStamp=new Date().getTime()/1000;
	
    private String cps;
    
    private int category;
   
    private String remark;
    private String alias;
    private String capacity;
    private String sellCapacity;
    private String approval;
    private String companyEnglish;
    private String company;
    private String price;
    private String country;
    private double sellPrice;
    private int dataType;
    private long approvalDate;
    
	private Doyen doyen;

    private String dataTypeStr;
    private Integer beginTime;
    
    /**
     * 成分排序顺序类型
     */
    private String cpsType;

    public String getDataTypeStr() {
    	if(dataType==1) {
    		dataTypeStr="进口备案";
    	}else if(dataType==2) {
    		dataTypeStr="国产备案";
    	}else if(dataType==3) {
    		dataTypeStr="国产备案";
    	} else if(dataType==4) {
    		dataTypeStr="产品标签";
    	}
    	
		return dataTypeStr;
	}
	public void setDataTypeStr(String dataTypeStr) {
		this.dataTypeStr = dataTypeStr;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
    
	public String getCps() {
		return cps;
	}
	public void setCps(String cps) {
		this.cps = cps;
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
 

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

 
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public String getSellCapacity() {
		return sellCapacity;
	}
	public void setSellCapacity(String sellCapacity) {
		this.sellCapacity = sellCapacity;
	}
	public String getApproval() {
		return approval;
	}
	public void setApproval(String approval) {
		this.approval = approval;
	}
	public String getCompanyEnglish() {
		return companyEnglish;
	}
	public void setCompanyEnglish(String companyEnglish) {
		this.companyEnglish = companyEnglish;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDatatype(int dataType) {
		this.dataType = dataType;
	}
	public long getApprovalDate() {
		return approvalDate;
	}
	public void setApprovalDate(long approvalDate) {
		this.approvalDate = approvalDate;
	}
	public Doyen getDoyen() {
		return doyen;
	}
	public void setDoyen(Doyen doyen) {
		this.doyen = doyen;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCpsType() {
		
		return cpsType;
	}
	public void setCpsType(String cpsType) {
		this.cpsType = cpsType;
	}
	public Integer getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Integer beginTime) {
		this.beginTime = beginTime;
	}

	
}

