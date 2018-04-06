package cn.bevol.app.entity.dto;

import cn.bevol.util.CommonUtils;
import org.springframework.data.annotation.Transient;

/**
 * 分类
 *
 * @author ruanchen
 */
public class Classification extends EntityBase{


    private String title;

    private String image;

    private Integer sorting;

    @Transient
    private String imageSrc;
    
    public String getImageSrc() {
    	imageSrc= CommonUtils.getImageSrc("back/images", this.getImage());
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public Integer getSorting() {
        return sorting;
    }

    public void setSorting(Integer sorting) {
        this.sorting = sorting;
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
