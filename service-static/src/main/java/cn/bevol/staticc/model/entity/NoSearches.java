package cn.bevol.staticc.model.entity;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class NoSearches {
	@Id
    private String _id;
    @Field
	private String _class;
	@Field
	private String uuid;
	@Field
	private Integer uid;
	@Field
    private String platform;
    @Field
    private String model;
    @Field
    private long createStamp;
    public long getCreateStamp() {
		return createStamp;
	}
	public void setCreateStamp(long createStamp) {
		this.createStamp = createStamp;
	}
	@Field
    private Map<String,String> keywords;
    @Field
    private String name;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_class() {
		return _class;
	}
	public void set_class(String _class) {
		this._class = _class;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public Map<String,String> getKeywords() {
		return keywords;
	}
	public void setKeywords(Map<String,String> keywords) {
		this.keywords = keywords;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
    
    
}
