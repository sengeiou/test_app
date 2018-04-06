package cn.bevol.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 商品组
 * @author zqc
 * 数据主要在Mongodb中缓存
 */
@Document(collection="entityCPSGoodsGroup")
public class EntityCPSGoodsGroup extends EntityBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String  ENTITY_CPS_GOODS_GROUP="entityCPSGoodsGroup";
	/**
	 * 商品组id 
	 * id
	 */
 
    /**
     * 商品组名称
     */
    private String goodsName;
    
    /**
     * 添加人
     */
    private String username;
    
    /**
     * 淘宝客联盟id
     */
    private Long favoritesId;
    
    /**
     * 商品组扩展
     */
    private String goodsExpansion;
    /**
     * 是否能正确获取到数据 ,1能获取，0不能正常获取到数据
     */
    private Integer correct; 
    
    
    
    
 
	public Integer getCorrect() {
		return correct;
	}
	public void setCorrect(Integer correct) {
		this.correct = correct;
	}
	public Long getFavoritesId() {
		return favoritesId;
	}
	public void setFavoritesId(Long favoritesId) {
		this.favoritesId = favoritesId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsExpansion() {
		return goodsExpansion;
	}
	public void setGoodsExpansion(String goodsExpansion) {
		this.goodsExpansion = goodsExpansion;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
 
  
	
	
	
	
	
    
}
