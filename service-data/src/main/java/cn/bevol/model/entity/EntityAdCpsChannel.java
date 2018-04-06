package cn.bevol.model.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CPS 渠道
 *
 */
@Document(collection="entityAdCpsChannel")
public class EntityAdCpsChannel  extends EntityBase {
	private static final long serialVersionUID = 1L;
	 
	/**
	 * 淘宝
	 */
	public static final Integer CHANNEL_TYPE_TAOBAO   = 1;
	/**
	 * 天猫
	 */
	public static final Integer CHANNEL_TYPE_TIANMAO  = 3;
	/**
	 * 京东
	 */
	public static final Integer CHANNEL_TYPE_JD       = 2;
	
	/**
	 * 店鋪名称
	 */
	 private String goodsSellerName;
	
	
	/**
	 * 是高佣金
	 */
	public static final Integer ISCOMMISSION_YES=0;
	
	/**
	 * 非高佣金
	 */
	public static final Integer ISCOMMISSION_NO=1;
 

///**
//    * 主键,id 表示实体,父类已经有定义
//    */
//   private String id;
   
	/**
	 * 是否是高佣金
	 * 1非高佣金,0是高佣金
	 */
	private Integer isCommission;
	
	
	
	 
    /**
     * 渠道类型 1.淘宝，  2京东 3天猫
     */
    private Integer channelType;

    /**
     * 渠道链接
     */
    private String channelLink;
    /*
     * 商品价格 已在父类定义
     */
    //private String price;
    /**
     * 安卓链接
     */
    private String androidLink;//和channelLink链接一样

	/**
     * ios链接
     */
    private String iosLink;

	/**
     * 商品链接图标
     */
    private String imgSrc;
    
    /**
     * 商品原价
     */
    private String OriginalPrice;
    
    /**
     * 渠道名字
     */
    private String channelName;

    /**
     * 普通 渠道开始时间 
     * 
     */
    private Long channelStartTime=0L;;
    

    /**
     * 渠道结束时间
     * 
     */
    private Long channelEndTime=0L;;
    
    /**
     * 高佣金模式开始时间
     */
    private Long  channelStartTimeCommission=0L;;
    /**
     * 高佣金结束时间
     */
    private Long  channelEndTimeCommission=0L;;
    
    /**
     * 渠道佣金形式  1.百分比  2.定额
     */
    private Integer channelCommissionType;

    /**
     * 渠道佣金
     */
    private String channelCommission;

    /**
     * 渠道商品id
     */
    private Long   channelGoodsId;
    
//    /**
//     * 是否隐藏 0为隐藏 、1为显示
//     */
//    private Integer hidden;
    
    /**
     * 安卓渠道点击次数
     */
    private Integer andrioCount=0;
    
    /**
     * 苹果渠道点击次数
     */
    private Integer iosCount=0;
    
    /**
     * 小图片
     */
    private List<String> smallImages;
    
    
    
   
    

	public String getGoodsSellerName() {
		return goodsSellerName;
	}

	public void setGoodsSellerName(String goodsSellerName) {
		this.goodsSellerName = goodsSellerName;
	}

	public List<String> getSmallImages() {
		return smallImages;
	}

	public void setSmallImages(List<String> smallImages) {
		this.smallImages = smallImages;
	}

	public Long getChannelStartTimeCommission() {
		return channelStartTimeCommission;
	}

	public void setChannelStartTimeCommission(Long channelStartTimeCommission) {
		this.channelStartTimeCommission = channelStartTimeCommission;
	}

	public Long getChannelEndTimeCommission() {
		return channelEndTimeCommission;
	}

	public void setChannelEndTimeCommission(Long channelEndTimeCommission) {
		this.channelEndTimeCommission = channelEndTimeCommission;
	}
 

	public Integer getAndrioCount() {
		return andrioCount;
	}

	public void setAndrioCount(Integer andrioCount) {
		this.andrioCount = andrioCount;
	}

	public Integer getIosCount() {
		return iosCount;
	}

	public void setIosCount(Integer iosCount) {
		this.iosCount = iosCount;
	}

	public Integer getChannelType() {
        return channelType;
    }
	
    public void setChannelType(Integer channelType) {
        if(channelType == 1){
            this.channelName = "淘宝";
        }else if(channelType == 2){
            this.channelName = "京东";
        }else if(channelType == 3){
            this.channelName = "天猫";
        }
        this.channelType = channelType;
    }

    public String getChannelLink() {
        return channelLink;
    }
    
    /**
     * 该字段和 androidLink 字段一致
     * @param channelLink
     */
    public void setChannelLink(String channelLink) {
        this.channelLink = channelLink;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Long getChannelStartTime() {
        return channelStartTime;
    }

    public void setChannelStartTime(Long channelStartTime) {
        this.channelStartTime = channelStartTime;
    }

    public Long getChannelEndTime() {
        return channelEndTime;
    }

    public void setChannelEndTime(Long channelEndTime) {
        this.channelEndTime = channelEndTime;
    }

    public Integer getChannelCommissionType() {
        return channelCommissionType;
    }

    public void setChannelCommissionType(Integer channelCommissionType) {
        this.channelCommissionType = channelCommissionType;
    }

    public String getChannelCommission() {
        return channelCommission;
    }

    public void setChannelCommission(String channelCommission) {
        this.channelCommission = channelCommission;
    }

    public Long getChannelGoodsId() {
        return channelGoodsId;
    }

    public void setChannelGoodsId(Long channelGoodsId) {
        this.channelGoodsId = channelGoodsId;
    }

	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	public String getOriginalPrice() {
		return OriginalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		OriginalPrice = originalPrice;
	}
	
    public String getAndroidLink() {
		return this.channelLink;
	}

	public void setAndroidLink(String androidLink) {
		this.androidLink = channelLink;
	}

	public String getIosLink() {
		return iosLink;
	}

	public void setIosLink(String iosLink) {
		this.iosLink = iosLink;
	}

	public Integer getIsCommission() {
		return isCommission;
	}

	public void setIsCommission(Integer isCommission) {
		this.isCommission = isCommission;
	}

	
	
	
	@Override
	public String toString() {
		return "EntityAdCpsChannel [id=" + super.get_id() + ", isCommission=" + isCommission + ", channelType=" + channelType
				+ ", channelLink=" + channelLink + ", price=" + super.getPrice() + ", androidLink=" + androidLink + ", iosLink="
				+ iosLink + ", imgSrc=" + imgSrc + ", OriginalPrice=" + OriginalPrice + ", channelName=" + channelName
				+ ", channelStartTime=" + channelStartTime + ", channelEndTime=" + channelEndTime
				+ ", channelCommissionType=" + channelCommissionType + ", channelCommission=" + channelCommission
				+ ", channelGoodsId=" + channelGoodsId + ", hidden=" + super.getHidden() + "]";
	}
    
	
	
	 
		
    
    
}
