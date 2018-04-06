package cn.bevol.model.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import cn.bevol.util.CommonUtils;

/**
 * 申请产品
 * @author hualong
 *
 */
@Document(collection="entity_apply_goods2")
public class EntityApplyGoods2 extends EntityBase {



	private String tag;

	private String tagIds;


	private String goodsIds;

	private Integer sort;

	private Integer publishTime;

	private List<Map> descp;

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
	 * 1、申请中
	 * 2、即将开始
	 * 3、活动结束
	 * 4、名单筛选中
	 * 5、测试报告收集中
	 * 6、未展现
	 *
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


	/***    new 20170815 start***/

	/**
	 * 申请结束时间
	 */
	private Long applyEndTime;

	/**
	 * 开奖时间
	 */
	private Long prizeEndTime;


	/**
	 * 最后提交试用报告时间
	 */
	private Long lastUserPartTime;

	/**
	 * 产品市场价
	 */
	//private Float price;


	/**
	 * 获奖人数
	 */
	private Integer prizeNum;

	/**
	 * 分享状态
	 * shareState=1 是
	 * shareState=0 否
	 */
	private Integer shareState;

	/**
	 * 是否监控同步
	 * １.以同步
	 */
	private Integer isSync;

	public Integer getIsSync() {
		return isSync;
	}

	public void setIsSync(Integer isSync) {
		this.isSync = isSync;
	}

	public void setDescp(List<Map> descp) {
		this.descp = descp;
	}

	/***     20170815 end***/




	public String getImgSrc() {
		if(this.getImage() != null) {
			if (this.getImage().indexOf("http") == -1) {
				this.imgSrc = CommonUtils.getImageSrc("apply_goods", this.getImage());
			}
		}
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}


	public Integer getPrizeNum() {
		return prizeNum;
	}

	public void setPrizeNum(Integer prizeNum) {
		this.prizeNum = prizeNum;
	}


	/**
	 * 图片源路径
	 */
	@Transient
	private String miniImgSrc;

	public List<Map> getDescp() {
		return descp;
	}

	public String getMiniImgSrc() {
		miniImgSrc=CommonUtils.getImageSrc("apply_goods", this.getMiniImage());
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
			if (startTime != null &&
					applyEndTime != null &&
					prizeEndTime != null &&
					lastTime != null &&
					publishTime != null) {
				if(curTime < publishTime){
					//未展现
					activeState = 6;
					activeStateDesc = "未展现";
				} else if(curTime<startTime) {
					//未开始、即将开始
					activeState=2;
					activeStateDesc="即将开始";
				} else if(curTime>startTime&&curTime<applyEndTime) {
					//申请中
					activeState=1;
					activeStateDesc="申请中";
				} else if(curTime>applyEndTime&&curTime<prizeEndTime) {
					//开奖中   名单筛选中
					activeState=4;
					activeStateDesc="体验中";
				} else if(curTime>prizeEndTime&&curTime<lastTime) {
					//报告收取中  测试报告收集中
					activeState=5;
					activeStateDesc="体验中";
				} else if(curTime>lastTime) {
					//报告收取中  测试报告收集中
					activeState=3;
					activeStateDesc="已经结束";
				}
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

	public Long getLastUserPartTime() {
		return lastUserPartTime;
	}

	public void setLastUserPartTime(Long lastUserPartTime) {
		this.lastUserPartTime = lastUserPartTime;
	}

	public Long getApplyEndTime() {
		return applyEndTime;
	}

	public void setApplyEndTime(Long applyEndTime) {
		this.applyEndTime = applyEndTime;
	}

	public Long getPrizeEndTime() {
		return prizeEndTime;
	}

	public void setPrizeEndTime(Long prizeEndTime) {
		this.prizeEndTime = prizeEndTime;
	}

	public Integer getShareState() {
		return shareState;
	}

	public void setShareState(Integer shareState) {
		this.shareState = shareState;
	}



}
