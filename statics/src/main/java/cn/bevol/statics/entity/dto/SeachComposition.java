package cn.bevol.statics.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 搜索产品
 * @author ruanchen
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class SeachComposition extends EntityBase{
	
	private String title;

	private String composition_ids;

	private Integer delete_status;

	private Integer hidden_status;

	private Integer update_time;

	private Integer create_time;

	public Integer getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Integer update_time) {
		this.update_time = update_time;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public Integer getDelete_status() {
		return delete_status;
	}

	public void setDelete_status(Integer delete_status) {
		this.delete_status = delete_status;
	}

	public Integer getHidden_status() {
		return hidden_status;
	}

	public void setHidden_status(Integer hidden_status) {
		this.hidden_status = hidden_status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComposition_ids() {
		return composition_ids;
	}

	public void setComposition_ids(String composition_ids) {
		this.composition_ids = composition_ids;
	}

}
