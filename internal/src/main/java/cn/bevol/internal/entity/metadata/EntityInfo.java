package cn.bevol.internal.entity.metadata;

import cn.bevol.internal.entity.model.Goods;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.util.CommonUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

/**
 * 用户参与
 * @author hualong
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class EntityInfo implements Serializable {

	private Long id;
	
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
	 * 图片源路径
	 */
	@Transient
	private String imgSrc;

	
	/**
	 * mid
	 */
	private String mid;
	
	/**
	 * 父mid
	 */
	private String mPid;
	
	/**
	 * 产品分类
	 */
	private Long categoryId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
	public  static EntityInfo setCusEntiyInfo(String tname, String title, String image) {
		if(StringUtils.isNotBlank(title)) {
			EntityInfo eny=new EntityInfo();
			eny.setTname(tname);
			eny.setTitle(title);
			eny.setImage(image);
			return eny;
		}
		return null;
 	}
	

	public void setEntiyInfo(EntityBase eb) {
		// TODO Auto-generated method stub
		this.setId(eb.getId());
		this.setImage(eb.getImage());
		this.setTitle(eb.getTitle());
		this.setMid(eb.getMid());
		this.setmPid(eb.getmPid());
		this.setAlias(eb.getAlias());
	}
	public void setEntiyInfo(Goods eb) {
		// TODO Auto-generated method stub
		this.setId(eb.getId());
		this.setImage(eb.getImage());
		this.setTitle(eb.getTitle());
		this.setMid(eb.getMid());
		this.setAlias(eb.getAlias());
	}
	

	public String getImgSrc() {
		if(id==null||id==0) {
			imgSrc= CommonUtils.getImageSrc("user_skin_protection/images",image);
		}else {
			imgSrc= CommonUtils.getImageSrc("goods",image);
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

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	

	

}
