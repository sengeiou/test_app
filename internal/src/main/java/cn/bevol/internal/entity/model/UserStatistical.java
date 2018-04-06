package cn.bevol.internal.entity.model;

/***
 * 用户统计
 * 2016-12-19
 * @author atao
 *
 */
public class UserStatistical {
	private Integer id;
	/**
	 * 登录数
	 */
	private Integer loginTotalNum;
	/**
	 * 启动数
	 */
	private Integer startTotalNum;
	/**
	 * 注册数
	 */
	private Integer registerTotalNum;
	/**
	 * 活跃数
	 */
	private Integer activeTotalNum;
	/**
	 * 累计登录数
	 */
	private Integer loginGrandTotalNum;
	/**
	 * 累计启动数
	 */
	private Integer startGrandTotalNum;
	/***
	 * 累计注册数
	 */
	private Integer registerGrandTotalNum;
	/**
	 * 累计活跃数
	 */
	private Integer activeGrandTotalNum;
	/**
	 * 活跃数分布
	 */
	private String activeStribution;
	
	private Integer statisticsDate;
	private long createTime;
	private long updateTime;
	private Integer beginTime;
	private Integer endTime;
	private Integer count;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getLoginTotalNum() {
		return loginTotalNum;
	}
	public void setLoginTotalNum(Integer loginTotalNum) {
		this.loginTotalNum = loginTotalNum;
	}
	public Integer getStartTotalNum() {
		return startTotalNum;
	}
	public void setStartTotalNum(Integer startTotalNum) {
		this.startTotalNum = startTotalNum;
	}
	public Integer getRegisterTotalNum() {
		return registerTotalNum;
	}
	public void setRegisterTotalNum(Integer registerTotalNum) {
		this.registerTotalNum = registerTotalNum;
	}
	public Integer getActiveTotalNum() {
		return activeTotalNum;
	}
	public void setActiveTotalNum(Integer activeTotalNum) {
		this.activeTotalNum = activeTotalNum;
	}
	public Integer getLoginGrandTotalNum() {
		return loginGrandTotalNum;
	}
	public void setLoginGrandTotalNum(Integer loginGrandTotalNum) {
		this.loginGrandTotalNum = loginGrandTotalNum;
	}
	public Integer getStartGrandTotalNum() {
		return startGrandTotalNum;
	}
	public void setStartGrandTotalNum(Integer startGrandTotalNum) {
		this.startGrandTotalNum = startGrandTotalNum;
	}
	public Integer getRegisterGrandTotalNum() {
		return registerGrandTotalNum;
	}
	public void setRegisterGrandTotalNum(Integer registerGrandTotalNum) {
		this.registerGrandTotalNum = registerGrandTotalNum;
	}
	public void setActiveStribution(String activeStribution) {
		this.activeStribution = activeStribution;
	}
	public Integer getActiveGrandTotalNum() {
		return activeGrandTotalNum;
	}
	public void setActiveGrandTotalNum(Integer activeGrandTotalNum) {
		this.activeGrandTotalNum = activeGrandTotalNum;
	}
	public String getActiveStribution() {
		return activeStribution;
	}
	public void setActivediStribution(String activeStribution) {
		this.activeStribution = activeStribution;
	}
	public Integer getStatisticsDate() {
		return statisticsDate;
	}
	public void setStatisticsDate(Integer statisticsDate) {
		this.statisticsDate = statisticsDate;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
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
