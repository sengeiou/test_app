package cn.bevol.model.entityAction;

/**
 * 纠错
 * @author hualong
 *
 */
public class Recovery extends  EntityActionBase{

	private String content;
	
	/**
	 * 0 提交 1 采纳 2不采纳
	 */
	private Integer state=0;
	
	/**
	 * 实体名称
	 */
	private String tname;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}
	
	
}
