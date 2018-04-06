package cn.bevol.model.entity;

import cn.bevol.util.response.ReturnData;

/**
 * 实体状态的基本信息
 * @author hualong
 *
 */
public class EntityBase extends MongoBase {
 	public static ReturnData ENTITY_HIDDEN=new ReturnData(-4,"实体信息不存在");
 	public static ReturnData ENTITY_NOT_OPT=new ReturnData(-4,"不允许对内容进行操作");
	public static ReturnData ENTITY_NOT_ALLOW_LIKE=new ReturnData(-4,"由于部分用户对该产品存在过多不实操作，目前处于禁止操作状态。多谢谅解！");


	/**
	 * 喜欢数量 数量用Num结尾
	 */
	private Long likeNum;
	
	/**
	 * 不喜欢数量
	 */
	private Long notLikeNum;
	
	/**
	 * 收藏数量
	 */
	private Long collectionNum;
	/**
	 * 评论数量
	 */
	private Long commentNum;
	
	/**
	 * 评论内容的
	 */
	private Long commentContentNum;
	
	/**
	 * 所有点评的数量
	 */
	private Long allCommentNum;
	
	/**
	 * 评论的总数
	 */
	private Long commentSumScore;
	
	/**
	 * 评论的平均分
	 */
	private Integer commentAvgScore;

	
	/**
	 * 点击数量
	 */
	private Long hitNum;

	/**
	 * 实体标题
	 */
	private String title;
	
	/**
	 * 实体标题
	 */
	private String alias;


	/**
	 * 实体图片
	 */
	private String image;
	
	

	/**
	 * mid
	 */
	private String mid;
	
	/**
	 * 父mid
	 */
	private String mPid;
	
	/**
	 * 对比id 组合 ci1 和 cid2
	 */
	private String sid;
	
	/**
	 * 对比的mids
	 */
	private String mids;

	/**
	 * 是否可以评论评论 默认可以 0表示可以评论
	 */
	private Integer allowComment;
	
	/**
	 * 访问时间 用于统计记录时间
	 */
	private Long vistTime;
	
	private String headerImage;
	
	/**
	 * 用户最爱星级数值=(喜欢数-3*心碎数)/(喜欢数+心碎数)
	 * 比例
	 */
	private Float radio;
	
	/**
	 * 同肤质星级星级 
	 */
	private Float grade;
	
	/**
	 * 产品安全星级
	 */
	private Float safety_1_num;
	
	private String capacity;
	
	private Float price;

	/**
	 * 分类的默认排序字段
	 */
	private Long csort;
	
	/**
	 * 实体唯一id
	 */
	private Long uniqueId;
	
	/**
	 * 实体类型
	 */
	private String tname;
	
	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public Long getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Long uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Float getRadio() {
		return radio;
	}

	public void setRadio(Float radio) {
		this.radio = radio;
	}

	
	public Float getGrade() {
		return grade;
	}

	public void setGrade(Float grade) {
		this.grade = grade;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

 	public Long getCommentContentNum() {
		return commentContentNum;
	}

	public void setCommentContentNum(Long commentContentNum) {
		this.commentContentNum = commentContentNum;
	}

	public Long getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Long likeNum) {
		this.likeNum = likeNum;
	}

	public Long getNotLikeNum() {
		return notLikeNum;
	}

	public void setNotLikeNum(Long notLikeNum) {
		this.notLikeNum = notLikeNum;
	}

	public Long getCollectionNum() {
		return collectionNum;
	}

	public void setCollectionNum(Long collectionNum) {
		this.collectionNum = collectionNum;
	}

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
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

	public Long getHitNum() {
		return hitNum;
	}

	public void setHitNum(Long hitNum) {
		this.hitNum = hitNum;
	}

	public Integer getAllowComment() {
		return allowComment;
	}

	public void setAllowComment(Integer allowComment) {
		this.allowComment = allowComment;
	}

	public Long getVistTime() {
		return vistTime;
	}

	public void setVistTime(Long vistTime) {
		this.vistTime = vistTime;
	}

	public String getmPid() {
		return mPid;
	}

	public void setmPid(String mPid) {
		this.mPid = mPid;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public Long getCommentSumScore() {
		return commentSumScore;
	}

	public void setCommentSumScore(Long commentSumScore) {
		this.commentSumScore = commentSumScore;
	}

	public Long getAllCommentNum() {
		return allCommentNum;
	}

	public void setAllCommentNum(Long allCommentNum) {
		this.allCommentNum = allCommentNum;
	}

	public Float getSafety_1_num() {
		return safety_1_num;
	}

	public void setSafety_1_num(Float safety_1_num) {
		this.safety_1_num = safety_1_num;
	}

	public Long getCsort() {
		return csort;
	}

	public void setCsort(Long csort) {
		this.csort = csort;
	}

	public Integer getCommentAvgScore() {
		return commentAvgScore;
	}

	public void setCommentAvgScore(Integer commentAvgScore) {
		this.commentAvgScore = commentAvgScore;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getMids() {
		return mids;
	}

	public void setMids(String mids) {
		this.mids = mids;
	}

	

 	
 }
