package cn.bevol.statics.entity.metadata;

import cn.bevol.statics.entity.MongoBase;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@Document(collection="user_goods_category")
public class UserGoodsCategory extends MongoBase{

	
	/**
	 * 原始分类
	 */
	private Long srcCategoryId;

	/**
	 * 原始分类名称
	 */
	private String srcCategoryName;
	
	/**
	 * 类型
	 * 1 默认分类
	 * 2、其他分类
	 */
	private Integer type;
	

	/**
	 * 是否基础分类
	 */
	private boolean base;
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 分类名称
	 */
	private String categoryName;
	
	
	/**
	 * 基础分类
	 */
	private Long pid=0L;
	
	/**
	 * 排序的时间戳
	 */
	private Long sort;
	
	/**
	 * 方案是否存在没查看过的产品
	 * true存在
	 */
	private Boolean exprieGoods=false;
	
	
	public String getCategoryName() {
		return categoryName;
	}


	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}


	public Long getSrcCategoryId() {
		return srcCategoryId;
	}


	public void setSrcCategoryId(Long srcCategoryId) {
		this.srcCategoryId = srcCategoryId;
	}


	public String getSrcCategoryName() {
		return srcCategoryName;
	}


	public void setSrcCategoryName(String srcCategoryName) {
		this.srcCategoryName = srcCategoryName;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Integer getType() {
		return type;
	}


	public void setType(Integer type) {
		this.type = type;
	}


	public Long getSort() {
		return sort;
	}


	public void setSort(Long sort) {
		this.sort = sort;
	}


	public Long getPid() {
		return pid;
	}


	public void setPid(Long pid) {
		this.pid = pid;
	}


	public boolean isBase() {
		return base;
	}


	public void setBase(boolean base) {
		this.base = base;
	}


	public Boolean getExprieGoods() {
		return exprieGoods;
	}


	public void setExprieGoods(Boolean exprieGoods) {
		this.exprieGoods = exprieGoods;
	}


	


}
