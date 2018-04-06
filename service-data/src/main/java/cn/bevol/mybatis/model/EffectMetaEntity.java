package cn.bevol.mybatis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EffectMetaEntity implements Serializable {

	
	private String id;
	
	private String name;
	
	private String type;
	//成分的id
	private List<Long> compositionids;

	
	public EffectMetaEntity(String id, String name,String  type) {
		this.id = id;
		this.name = name;
		this.type=type;
	}

	public String getId() {
		return id;
	} 

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void addCompositionid(Long id) {
		if(this.compositionids==null) compositionids=new ArrayList<Long>();
		compositionids.add(id);
	}

	public List<Long> getCompositionids() {
		return compositionids;
	}

	public void setCompositionids(List<Long> compositionids) {
		this.compositionids = compositionids;
	}
	
}
