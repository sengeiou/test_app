package cn.bevol.statics.entity;

import cn.bevol.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;

/**
 * 成分
 * @author hualong
 *
 */
public class UserPartDetailGoods extends UserPartDetail {
 	
	private long id;
	
	/**
	 * 实体类型
	 */
	private String tname;

	/**
	 * 实体标题
	 */
	private String title;

	/**
	 * 实体图片
	 */
	private String image;
	
	/**
	 * 英文名称
	 */
	private String alias;

	
	/**
	 * mid
	 */
	private String mid;
	
	/**
	 * 父mid
	 */
	private String mPid;
	
	/**
	 * 图片源路径
	 */
	@Transient
	private String imgSrc;

	private Float price;
	
	private String capacity;
	
	private Float safety_1_num;
	
	private Float grade;
	
	private Long commentNum;
	

	public UserPartDetailGoods() {
		
	}
	public UserPartDetailGoods(Long id) {
		this.id=id;
	}
	
 
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
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

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getmPid() {
		return mPid;
	}

	public void setmPid(String mPid) {
		this.mPid = mPid;
	}
	
	public String getImgSrc() {
		if(StringUtils.isBlank(mid)) {
			//自定义产品图片
			//imgSrc=CommonUtils.getImageSrc("user_part/lists", image);
		} else {
			//产品库的图片
			imgSrc= CommonUtils.getImageSrc("goods", image);
		}
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
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
	
	public Long getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}
	public Float getSafety_1_num() {
		return safety_1_num;
	}
	public void setSafety_1_num(Float safety_1_num) {
		this.safety_1_num = safety_1_num;
	}
	public Float getGrade() {
		return grade;
	}
	public void setGrade(Float grade) {
		this.grade = grade;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}

	
  
}
