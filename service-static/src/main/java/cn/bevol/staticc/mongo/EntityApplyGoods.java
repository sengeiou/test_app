package cn.bevol.staticc.mongo;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 申请产品
 * @author hualong
 *
 */
@Document(collection="entity_apply_goods")
public class EntityApplyGoods extends EntityBase {



    private String tag;
    
    private String tagIds;

    private String descp;

    private String goodsIds;

    private Integer sort;

    private Integer publishTime;

    
    /**
     * 0普通、1长久性、2实效性
     */
    private Integer type;
    
    /**
     * 活动开始时间
     */
    private Long startTime;
    
    /**
     * 活动结束时间
     */
    private Long lastTime;
    
    /**
     * 开始时间
     */
    private Long curTime;
    
    
    /**
     * 活动状态
     * 1、进行中
     * 2、未开始
     * 3、已结束 
     */
    private Integer activeState;
    
    /**
     * 用户试用心得数
     */
    private Integer userPartNum;
    
    /**
     * 剩余时间表示
     */
    private String activeStateDesc;
    
    private String miniImage;
    
    /**
	 * 图片源路径
	 */
	@Transient
	private String imgSrc;
	
	/**
	 * 产品列表
	 */
	@Transient
	private List<EntityGoods> goods; 
	
	/**
	 * 所需修行值
	 */
	private Integer doyenScore;
	
	/**
	 * 产品数量
	 */
	private Integer goodsNum;
	
	/**
	 * 申请人数
	 */
	private Integer applyNum;
	
	
	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	
	/**
	 * 图片源路径
	 */
	@Transient
	private String miniImgSrc;

    public String getMiniImgSrc() {
		return miniImgSrc;
	}

	public void setMiniImgSrc(String miniImgSrc) {
		this.miniImgSrc = miniImgSrc;
	}

	public Long getCurTime() {
		return curTime;
	}

	public void setCurTime(Long curTime) {
		this.curTime = curTime;
	}
	
	public String getActiveStateDesc() {
		return activeStateDesc;
	}

	public void setActiveStateDesc(String activeStateDesc) {
		this.activeStateDesc = activeStateDesc;
	}
	
	public Integer getDoyenScore() {
		return doyenScore;
	}

	public void setDoyenScore(Integer doyenScore) {
		this.doyenScore = doyenScore;
	}

	public Integer getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(Integer goodsNum) {
		this.goodsNum = goodsNum;
	}

	public Integer getApplyNum() {
		return applyNum;
	}

	public void setApplyNum(Integer applyNum) {
		this.applyNum = applyNum;
	}

	/*
	 * 获取活动状态
	 */
	public Integer getActiveState() {
		//实效性  计算
		curTime=new Date().getTime()/1000;
		if(type!=null&&type==2) {
			if(curTime>lastTime) {
				activeState=3;
				activeStateDesc="已结束 ";
			} else if(curTime<startTime) {
				activeState=2;
				activeStateDesc="未开始";
			}else{
				//一小时
				long hourt=60*60;
				//一天
				long day=hourt*24;
				//计算剩余天数
				long sq=lastTime-curTime;
				
				//计算剩余天数
				long lastDay=sq/day;
				
				//计算剩余小时
				long lastHourt=(sq%day)/hourt;
				if(lastDay==0) {
					activeStateDesc=lastHourt+"小时";
				} else {
					activeStateDesc=lastDay+"天"+lastHourt+"小时";
				}
				activeState=1;
			}
		}
		return activeState;
	}

	public void setActiveState(Integer activeState) {
		this.activeState = activeState;
	}
 

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Integer publishTime) {
        this.publishTime = publishTime;
    }
 
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getLastTime() {
		return lastTime;
	}

	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}

	public String getTagIds() {
		return tagIds;
	}

	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
	}

	public String getMiniImage() {
		return miniImage;
	}

	public void setMiniImage(String miniImage) {
		this.miniImage = miniImage;
	}

	public List<EntityGoods> getGoods() {
		return goods;
	}

	public void setGoods(List<EntityGoods> goods) {
		this.goods = goods;
	}

	public Integer getUserPartNum() {
		return userPartNum;
	}

	public void setUserPartNum(Integer userPartNum) {
		this.userPartNum = userPartNum;
	}

	

 }
