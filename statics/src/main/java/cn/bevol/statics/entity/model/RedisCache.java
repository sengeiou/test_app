package cn.bevol.statics.entity.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * redis缓存
 * @author chenHaiJian
 *
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class RedisCache implements Serializable  {

	private Long id; 
	
	/**
	 * CACHE_NAME中的常量名
	 */
    private String redisKey;

    /**
     * 接口入参中需要缓存的参数(逗号分隔)
     */
    private String params;

    /**
     * 缓存的版本号
     */
    private String cacheVersion;
    
    /**
     * 最终生成的缓存
     */
    private String cache;
    
    /**
     * 对相应缓存的注解
     */
    private String desc;
    
    /**
     * 实体类型
     */
    private String entityType;
    
    /**
     * 缓存类型(时效,永久)
     */
    private Integer cacheType;

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getCacheVersion() {
		return cacheVersion;
	}

	public void setCacheVersion(String cacheVersion) {
		this.cacheVersion = cacheVersion;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRedisKey() {
		return redisKey;
	}

	public void setRedisKey(String redisKey) {
		this.redisKey = redisKey;
	}

	public Integer getCacheType() {
		return cacheType;
	}

	public void setCacheType(Integer cacheType) {
		this.cacheType = cacheType;
	}


    

}
