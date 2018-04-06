package cn.bevol.statics.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author chenHaiJian
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class UserSubmitGoods extends EntityBase{

	private String goodsMid;
	/**
	 * 图片Id
	 */
	private String images;
	private String image1;
	private String image2;
	private String image3;
	private Long userId;
	
	/**
	 * 产品名称
	 */
	private String name;
	/**
	 * 来源
	 */
	private String source;
	/**
	 * 状态 0正在审核 1审核通过 2审核不通过
	 */
	private Long state;
	private String compositionIds;
	
	private String compositionNames;
	/**
	 * 库中没有的成分
	 */
	private String compositionNo;
	/**
	 * 添加时间 
	 */
	private Long addDate;
	/**
	 * 修改时间
	 */
	private Long updateDate;
	   
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
    	if(StringUtils.isNotBlank(this.images)){
    		String[] imagess=this.images.split(",");
    		this.image1=imagess[0];
    		if(imagess.length>=2){
    			this.image2=imagess[1];
    		}
    		if(imagess.length>=3){
    			this.image3=imagess[2];
    		}
    	}
		
		
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Long getState() {
		return state;
	}
	public void setState(Long state) {
		this.state = state;
	}
	public String getCompositionIds() {
		return compositionIds;
	}
	public void setCompositionIds(String compositionIds) {
		this.compositionIds = compositionIds;
	}
	public String getCompositionNames() {
		return compositionNames;
	}
	public void setCompositionNames(String compositionNames) {
		this.compositionNames = compositionNames;
	}
	public String getCompositionNo() {
		return compositionNo;
	}
	public void setCompositionNo(String compositionNo) {
		this.compositionNo = compositionNo;
	}
	public Long getAddDate() {
		return addDate;
	}
	public void setAddDate(Long addDate) {
		this.addDate = addDate;
	}
	public Long getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Long updateDate) {
		this.updateDate = updateDate;
	}
	public String getImage1() {
		return image1;
	}
	public void setImage1(String image1) {
		this.image1 = image1;
	}
	public String getImage2() {
		return image2;
	}
	public void setImage2(String image2) {
		this.image2 = image2;
	}
	public String getImage3() {
		return image3;
	}
	public void setImage3(String image3) {
		this.image3 = image3;
	}
	public String getGoodsMid() {
		return goodsMid;
	}
	public void setGoodsMid(String goodsMid) {
		this.goodsMid = goodsMid;
	}
	
	
	
	
}
