package cn.bevol.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 数据分类 
 * @author 
 *
 */
@Document(collection="data_category")
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataCategory extends MongoBase {


	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
