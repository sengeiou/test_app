package cn.bevol.internal.entity.model;

public class DownloadRecord {
	private Integer id;
	private Integer qrcodeId;
	private String dataType;
	private Integer pcTotal;
	private Integer weixinTotal;
	private Integer mobileTotal;
	private Integer androidTotal;
	private Integer iosTotal;
	private Integer statisticsDate;
	private Long createDate;
	private Long updateDate;
	private Integer totalNum;
	private Integer beginTime;
	private Integer endTime;
	private Integer count;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQrcodeId() {
		return qrcodeId;
	}

	public void setQrcodeId(Integer qrcodeId) {
		this.qrcodeId = qrcodeId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getPcTotal() {
		return pcTotal;
	}

	public void setPcTotal(Integer pcTotal) {
		this.pcTotal = pcTotal;
	}

	public Integer getWeixinTotal() {
		return weixinTotal;
	}

	public void setWeixinTotal(Integer weixinTotal) {
		this.weixinTotal = weixinTotal;
	}

	public Integer getMobileTotal() {
		return mobileTotal;
	}

	public void setMobileTotal(Integer mobileTotal) {
		this.mobileTotal = mobileTotal;
	}

	public Integer getAndroidTotal() {
		return androidTotal;
	}

	public void setAndroidTotal(Integer androidTotal) {
		this.androidTotal = androidTotal;
	}

	public Integer getIosTotal() {
		return iosTotal;
	}

	public void setIosTotal(Integer iosTotal) {
		this.iosTotal = iosTotal;
	}

	public Integer getStatisticsDate() {
		return statisticsDate;
	}

	public void setStatisticsDate(Integer statisticsDate) {
		this.statisticsDate = statisticsDate;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Long getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Long updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	public Integer getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Integer beginTime) {
		this.beginTime = beginTime;
	}

	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}