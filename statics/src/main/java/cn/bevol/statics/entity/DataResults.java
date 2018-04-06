package cn.bevol.statics.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;


/**
 * 数据分类 
 * @author 
 *
 */
@Document(collection="data_results")
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataResults extends MongoBase {

	/*
	 * 对应的类型
	 */
 	private Long dataCategoryListsId;
 	
 	private Long publishTime;




	private DataCategoryRelation dataCategoryRelation;

	private Long endStamp;
 	private String writerName;
 	
	private String listName;
	
 	/**
 	 * 获取的数据列表
 	 */
 	private List<Map> list;
 	
 	
 	private Long searchTime;
 	
 	public String getListName() {
		return listName;
	}
 	

	public void setListName(String listName) {
		this.listName = listName;
	}






	public Long getDataCategoryListsId() {
		return dataCategoryListsId;
	}


	public void setDataCategoryListsId(Long dataCategoryListsId) {
		this.dataCategoryListsId = dataCategoryListsId;
	}


	public Long getPublishTime() {
		return publishTime;
	}

 	public DataCategoryRelation getDataCategoryRelation() {
		return dataCategoryRelation;
	}


	public void setDataCategoryRelation(DataCategoryRelation dataCategoryRelation) {
		this.dataCategoryRelation = dataCategoryRelation;
	}

	public void setPublishTime(Long publishTime) {
		this.publishTime = publishTime;
	}


	public List<Map> getList() {
		return list;
	}


	public void setList(List<Map> list) {
		this.list = list;
	}


	public Long getSearchTime() {
		return searchTime;
	}


	public void setSearchTime(Long searchTime) {
		this.searchTime = searchTime;
	}
 	public Long getEndStamp() {
		return endStamp;
	}


	public void setEndStamp(Long endStamp) {
		this.endStamp = endStamp;
	}


	public String getWriterName() {
		return writerName;
	}


	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}
 	
}
