package cn.bevol.util;

import java.util.List;
import java.util.Map;

/**
 * 查询模型
 * @author Administrator
 *
 */
public class DataInfo {

	/**
	 * 字段
	 */
	private Map<String,Desc> where;
	
	/**
	 * 字段关系
	 */
	private String relation;
	
	/**
	 * 必须包含字段|
	 */
	private List<String> mustField;
	
	/*
	 * 排序字段
	 */
	private Map<String,String> sort;
	
	/**
	 * 表名称
	 */
	private String tableName;
	/**
	 * 索引名称
	 */
	private String indexName;
 	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Map<String, Desc> getWhere() {
		return where;
	}

	public void setWhere(Map<String, Desc> where) {
		this.where = where;
	}

	public Map<String, String> getSort() {
		return sort;
	}

	public void setSort(Map<String, String> sort) {
		this.sort = sort;
	}

	public List<String> getMustField() {
		return mustField;
	}

	public void setMustField(List<String> mustField) {
		this.mustField = mustField;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

 	
	
	
}
