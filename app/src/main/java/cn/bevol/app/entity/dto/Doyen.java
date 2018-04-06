package cn.bevol.app.entity.dto;

import cn.bevol.util.CommonUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 修行说
 *
 * @author ruanchen
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Doyen extends EntityBase{

    private Long goodsId;

    private Long userId;

    private String doyenComment;

    private String mid;

    private String image;

    private String title;

    private String nickname;
    private String headimgurl;
    private String userDescz;
    private String skin;
    private String skinResults;
    
    private String imageSrc;

    public String getImageSrc() {
    	imageSrc= CommonUtils.getImageSrc("goods", this.getImage());
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoyenComment() {
        return doyenComment;
    }

    public void setDoyenComment(String doyenComment) {
        this.doyenComment = doyenComment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getUserDescz() {
		return userDescz;
	}

	public void setUserDescz(String userDescz) {
		this.userDescz = userDescz;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public String getSkinResults() {
		return skinResults;
	}

	public void setSkinResults(String skinResults) {
		this.skinResults = skinResults;
	}

}
