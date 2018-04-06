package cn.bevol.staticc.model.dto;

/**
 * Created by mysens on 17-3-15.
 */
public class GoodsDTO {
    private Long id;
    private Integer[] idsArr;
    private Long brandId;
    private Integer tstamp;
    private Integer crdate;
    private Integer deleted;
    private Integer hidden;
    private Integer hot;
    private Integer top;
    private Long sorting;
    private String title;
    private String alias;
    private String alias2;
    private String mid;
    private String image;
    private Integer category;
    private String company;
    private String companyEnglish;
    private String address;
    private Integer state;
    private String approval;
    private Integer approvalDate;
    private Integer flag;
    private String actualCompany;
    private String actualCompanyAddress;
    private Integer dataType;
    private String chinaCompany;
    private String chinaAddress;
    private String country;
    private String brand;
    private String categoryStr;
    private Integer doyenId;
    private String cps;
    private String price;
    private String capacity;
    private Integer hiddenSkin;
    private String cpsType;
    private Integer recordDate;
    private String recordNum;
    private String license;
    private String skills;
    private String content;
    private String remark;
    private Integer allowComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer[] getIdsArr() {
        return idsArr;
    }

    public void setIdsArr(Integer[] idsArr) {
        this.idsArr = idsArr;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getHidden() {
        return hidden;
    }

    public void setHidden(Integer hidden) {
        this.hidden = hidden;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Long getSorting() {
        return sorting;
    }

    public void setSorting(Long sorting) {
        this.sorting = sorting;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias2() {
        return alias2;
    }

    public void setAlias2(String alias2) {
        this.alias2 = alias2;
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

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyEnglish() {
        return companyEnglish;
    }

    public void setCompanyEnglish(String companyEnglish) {
        this.companyEnglish = companyEnglish;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public Integer getApprovalDate() {
        return approvalDate == null?0:approvalDate;
    }

    public void setApprovalDate(Integer approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getActualCompany() {
        return actualCompany;
    }

    public void setActualCompany(String actualCompany) {
        this.actualCompany = actualCompany;
    }

    public String getActualCompanyAddress() {
        return actualCompanyAddress;
    }

    public void setActualCompanyAddress(String actualCompanyAddress) {
        this.actualCompanyAddress = actualCompanyAddress;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getChinaCompany() {
        return chinaCompany;
    }

    public void setChinaCompany(String chinaCompany) {
        this.chinaCompany = chinaCompany;
    }

    public String getChinaAddress() {
        return chinaAddress;
    }

    public void setChinaAddress(String chinaAddress) {
        this.chinaAddress = chinaAddress;
    }

    public String getCountry() {
        return country == null?"":country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategoryStr() {
        return categoryStr;
    }

    public void setCategoryStr(String categoryStr) {
        this.categoryStr = categoryStr;
    }

    public Integer getDoyenId() {
        return doyenId;
    }

    public void setDoyenId(Integer doyenId) {
        this.doyenId = doyenId;
    }

    public String getCps() {
        return cps;
    }

    public void setCps(String cps) {
        this.cps = cps;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public Integer getHiddenSkin() {
        return hiddenSkin;
    }

    public void setHiddenSkin(Integer hiddenSkin) {
        this.hiddenSkin = hiddenSkin;
    }

    public String getCpsType() {
        return cpsType;
    }

    public void setCpsType(String cpsType) {
        this.cpsType = cpsType;
    }

    public Integer getRecordDate() {
        return recordDate==null?0:recordDate;
    }

    public void setRecordDate(Integer recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getAllowComment() {
        return allowComment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setAllowComment(Integer allowComment) {
        this.allowComment = allowComment;
    }
}
