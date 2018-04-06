package cn.bevol.internal.entity.model;

/**
 * 发现
 * @author hualong
 *
 */
public class Industry {
	private Integer id;
	private String title;
	private String image;
	private String descp;
	
	private Integer sort=100;
 	
	private String tag;
	
	private String pccontent;
	
	private long publishTime;
	
	private String skin;
	
	private String path;
	
	private String headerImage;
	
	private String subhead;
	
	private String pcImage; 

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescp() {
		return descp;
	}

	public void setDescp(String descp) {
		this.descp = descp;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}

	public String getPccontent() {
		return pccontent;
	}

	public void setPccontent(String pccontent) {
		this.pccontent = pccontent;
	}

 	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getSubhead() {
		return subhead;
	}

	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}

	public String getPcImage() {
		return pcImage;
	}

	public void setPcImage(String pcImage) {
		this.pcImage = pcImage;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	
	
}
