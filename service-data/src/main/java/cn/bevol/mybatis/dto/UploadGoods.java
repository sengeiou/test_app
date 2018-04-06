package cn.bevol.mybatis.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cn.bevol.model.entity.EntityBase;

/**
 * 上传文件的 信息
 * @author chenhaijian
*/
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UploadGoods extends EntityBase{
	
	private String nickname;
	
	private String mid;

    private String image;
	 
    private String nodesc;
    
    private String title;

    private Integer tstamp;

    private Integer crdate;

    private long goodsId;
    
    private Integer deleted;

    private Integer hidden;

    private Integer used;
    
    private Integer type;

    private long userId;

    private Integer notype;
    
    public UploadGoods(){
    	
    }
    
    public UploadGoods(String title,String image,String nickname,long userId,String mid,long goodsId){
    	this.title=title;
    	this.image=image;
    	this.nickname=nickname;
    	this.userId=userId;
    	this.mid=mid;
    	this.goodsId=goodsId;
    }

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNodesc() {
		return nodesc;
	}

	public void setNodesc(String nodesc) {
		this.nodesc = nodesc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public long getGoodsId() {
		return goodsId;
	}

	public void setGoods_id(long goodsId) {
		this.goodsId = goodsId;
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

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getNotype() {
		return notype;
	}

	public void setNotype(Integer notype) {
		this.notype = notype;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}
    
}
