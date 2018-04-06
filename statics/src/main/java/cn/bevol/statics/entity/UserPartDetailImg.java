package cn.bevol.statics.entity;

import cn.bevol.util.CommonUtils;
import org.springframework.data.annotation.Transient;

/**
 * 图片
 * @author hualong
 *
 */
public class UserPartDetailImg extends UserPartDetail {
 	
	private String image;
	
	/**
	 * 图片源路径
	 */
	@Transient
	private String imgSrc;

	public UserPartDetailImg() {
		
	}
	
	public UserPartDetailImg(String image) {
		this.image=image;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getImgSrc() {
		imgSrc= CommonUtils.getImageSrc("user_part/lists", image);
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
 	
	
}
