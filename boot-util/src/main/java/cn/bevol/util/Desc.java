package cn.bevol.util;

/**
 * 字段说明
 * @author Administrator
 *
 */
public class Desc {
 	
	/*
	 * 数据类型
	 */
	private String type;
	
	/**
	 * 数据间关系
	 */
	private String relation;
	
	/**
	 * 数据库映射字段
	 */
	private String dbFeild;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getDbFeild() {
		return dbFeild;
	}

	public void setDbFeild(String dbFeild) {
		this.dbFeild = dbFeild;
	}
	
}
