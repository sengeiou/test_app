package cn.bevol.model.entity;

import java.util.List;

import org.springframework.data.annotation.Transient;

/**
 * 实体对比
 * @author hualong
 *
 */
public class EntityCompare extends EntityBase {
	
	/**
	 * id 组合 ci1 和 cid2
	 */
	//private String sid;
	
	/*
	 * 对比id1id
	 */
	private Long cid1;
	
	/**
	 * cid2对比2 id  
	 * cid1<cid2 不能等于
	 */
	private Long cid2;
	
	/**
	 * cid1的支持数量
	 */
	private Long cid1LikeNum;
	
	/**
	 * cid2的支持数量
	 */
	private Long cid2LikeNum;
	
	/**
	 * 热度 = hitNum+commentNum
	 */
	private Long hotNum;
	
	/**
	 * 访问数量=hitNum+cid1LikeNum+cid2LikeNum
	 * 用于对比广场的显示
	 */
	private Long visitNum;
	
	//private String mids;
	
	/**
	 * 第一个创建对比的人
	 */
	private Long userId;

	/**
	 * 人工排序
	 */
	private Integer sort;
	
	/**
	 * 用于列表显示的支持id
	 */
	@Transient
	private Long clikeId;
	
	/**
	 * 用于展示产品信息
	 */
	@Transient
	private List objList;

	public Long getCid1() {
		return cid1;
	}

	public void setCid1(Long cid1) {
		this.cid1 = cid1;
	}

	public Long getCid2() {
		return cid2;
	}

	public void setCid2(Long cid2) {
		this.cid2 = cid2;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCid1LikeNum() {
		return cid1LikeNum;
	}

	public void setCid1LikeNum(Long cid1LikeNum) {
		this.cid1LikeNum = cid1LikeNum;
	}

	public Long getCid2LikeNum() {
		return cid2LikeNum;
	}

	public void setCid2LikeNum(Long cid2LikeNum) {
		this.cid2LikeNum = cid2LikeNum;
	}

	public Long getHotNum() {
		return hotNum;
	}

	public void setHotNum(Long hotNum) {
		this.hotNum = hotNum;
	}

	public Long getVisitNum() {
		return visitNum;
	}

	public void setVisitNum(Long visitNum) {
		this.visitNum = visitNum;
	}

	public Long getClikeId() {
		return clikeId;
	}

	public void setClikeId(Long clikeId) {
		this.clikeId = clikeId;
	}

	public List getObjList() {
		return objList;
	}

	public void setObjList(List objList) {
		this.objList = objList;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	
}


