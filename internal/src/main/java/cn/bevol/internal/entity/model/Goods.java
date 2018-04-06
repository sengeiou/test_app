package cn.bevol.internal.entity.model;

import cn.bevol.internal.entity.dto.Doyen;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Goods implements Serializable  {
 
	
	/**
	 * 实体图片
	 */
	private String image;
	
	@Transient
	private String imageSrc;
	
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
    private Long updateStamp= DateUtils.nowInMillis()/1000;
    /**
     * 数据的系统创建时间 统一用createTime
     */
    private Long createStamp=DateUtils.nowInMillis()/1000;
	
    private String cps;
    
    private int category;


	private String brand;
    private String remark;
    private String remark3;
    private String alias;
    private String alias2;
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
    private Integer priceUnit;
    
    /**
     * 普通类和特殊类
     */
    private String categoryStr;
    
    
    /**
     * 用于前台展现
     */
	private Doyen doyen;

    private String dataTypeStr;
    
    /**
     *  产品扩展字段
     */
    private GoodsExt goodsExt;
    

    /**
     *  产品扩展字段--标签
     */
    private GoodsTagResult GoodsTagResult;
    
    /*
     * 成分 对应cps
     */
    private List<Composition> compositions;
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
    	} else if(dataType==6) {
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
	public List<Composition> getCompositions() {
		return compositions;
	}
	public void setCompositions(List<Composition> compositions) {
		this.compositions = compositions;
	}


	public GoodsExt getGoodsExt() {
		return goodsExt;
	}


	public void setGoodsExt(GoodsExt goodsExt) {
		this.goodsExt = goodsExt;
	}


	public String getCategoryStr() {
		return categoryStr;
	}


	public void setCategoryStr(String categoryStr) {
		this.categoryStr = categoryStr;
	}


	public GoodsTagResult getGoodsTagResult() {
		return GoodsTagResult;
	}


	public void setGoodsTagResult(GoodsTagResult goodsTagResult) {
		GoodsTagResult = goodsTagResult;
	}


	public String getAlias2() {
		return alias2;
	}


	public void setAlias2(String alias2) {
		this.alias2 = alias2;
	}


	public String getRemark3() {
		return remark3;
	}


	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}


	public String getImageSrc() {
		imageSrc= CommonUtils.getImageSrc("goods", this.getImage());
		return imageSrc;
	}


	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}


	public Integer getPriceUnit() {
		return priceUnit;
	}


	public void setPriceUnit(Integer priceUnit) {
		this.priceUnit = priceUnit;
	}


    public String getBrand() {
		return brand;
	}


	public void setBrand(String brand) {
		this.brand = brand;
	}


 	
}

