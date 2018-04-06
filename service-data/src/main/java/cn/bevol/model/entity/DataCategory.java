package cn.bevol.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.bevol.model.Base;

/**
 * 数据分类 
 * @author 
 *
 */
@Document(collection="data_category")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class DataCategory extends Base {


	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
