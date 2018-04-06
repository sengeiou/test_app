package cn.bevol.internal.entity.dto;


import cn.bevol.internal.entity.entityAction.EntityActionBase;

/**
 * 喜欢产品
 *
 * @author hualong
 */
public class LikeGoods extends EntityActionBase {

    /**
     * 图片
     */
    private String image;

    /**
     * mid
     */
    private String mid;

    /**
     * 名称
     */
    private String title;

    /**
     * 喜欢次数
     */
    private Integer num;

    /**
     * 喜欢次数
     */
    private Integer hot;

    /**
     * 备案类型
     */
    private Integer data_type;


    /**
     * 产品因为名称
     */
    private String alias;

    /**
     * 产品因为名称
     */
    private String alias_2;

    /**
     * 产品因为名称
     */
    private String remark3;

    /**
     * 产品价格
     */
    private String price;

    /**
     * 产品容量
     */
    private String capacity;

    private String name;
    
    /**
     * 安全星级
     */
    private Double safety_1_num;
    
    /**
     * 评论数
     */
    private Long commentNum;
    
    /**
     * 用户喜爱星级
     */
    private Double grade;
    
    /**
     * 产品喜欢数
     */
    private Long likeNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    public String getAlias_2() {
        return alias_2;
    }

    public void setAlias_2(String alias_2) {
        this.alias_2 = alias_2;
    }


    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getData_type() {
        return data_type;
    }

    public void setData_type(Integer data_type) {
        this.data_type = data_type;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

	public Double getSafety_1_num() {
		return safety_1_num;
	}

	public void setSafety_1_num(Double safety_1_num) {
		this.safety_1_num = safety_1_num;
	}

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public Long getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Long likeNum) {
		this.likeNum = likeNum;
	}
    
    
}
