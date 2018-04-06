package cn.bevol.internal.entity.model;
/***
 * 百度收录记录表
 * @author atao
 *
 */
public class SeoRecord {

	private Integer id;
	private String urls;
	private String results;
	private String operateType;
	private String dataType;
	private Integer state;
	private Integer remain;
	private long createTime;
	private long updateTime;
	private Integer count;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUrls() {
		return urls;
	}
	public void setUrls(String urls) {
		this.urls = urls;
	}
	public String getResults() {
		return results;
	}
	public String getOperateType() {
		return operateType;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getRemain() {
		return remain;
	}
	public void setRemain(Integer remain) {
		this.remain = remain;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public void setResults(String results) {
		this.results = results;
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
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
}
