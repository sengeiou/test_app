package cn.bevol.model.entityAction;

/**
 * 喜欢对比的产品
 *
 * @author hualong
 */
public class LikeCompare extends EntityActionBase {
	
	/**
	 * cid1和cid2组合=cid1_cid2
	 */
	//private String sid;
	
	/*
	 * 冗余
	 * 对比id1id
	 */
	private Long cid1;
	
	/**
	 * 冗余
	 * cid2对比2 id  
	 */
	private Long cid2;
	

	/**
	 * 喜欢的 id 对于 cid1或cid2
	 */
	private Long clikeId;


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


	public Long getClikeId() {
		return clikeId;
	}


	public void setClikeId(Long clikeId) {
		this.clikeId = clikeId;
	}


	
	
}
