package cn.bevol.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection="entity_ad_cps")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EntityAdCps extends EntityBase {
	/**
	 * 隐藏状态，不隐藏
	 */
	public static final Integer HIDDEN_YES=0;
	/**
	 * 隐藏状态，隐藏
	 */
	public static final Integer HIDDEN_NO=1;
	
 
	
	public static final String ENTITY_AD_CPS="entity_ad_cps";

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    /**
     * 实体类数据是从，日志id的实体对象中取出
     */
	private Long entityCPSLogId;
	
//	//商品id
    private Long goodsId;
    /**
     * 淘宝客中的商品id
     */
    private Long taobaoKeGoodsId;

    @Transient
    private String title;
    //商品名
    private String goodsName;

    //cps开始时间
    private Long startTime;

    //cps结束时间
    private Long endTime;
    /**
     * 淘宝客ID 使用逗号分隔
     */
    private String  taobaoKeId;
    /**
     * 商品组ID
     */
    private String  favoritesId;
    
    /**
     * 原名称，淘宝客查询出来的名称
     */
    private String  oldName;
    
    /**
     * 渠道信息
     * channelType  渠道类型 1.淘宝，  2京东
     * channelLink  渠道链接
     * channelName  渠道名字
     * channelStartTime  渠道开始时间
     * channelEndTime  渠道结束时间
     * channelCommissionType  渠道佣金形式  1.百分比  2.定额
     * channelCommission  渠道佣金
     * channelGoodsId 渠道商品id
     */
    private List<EntityAdCpsChannel> entityAdCpsChannel;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;
    
    /**
     * 记录多个商店的ID
     */
    private String  shopIds;
    
    
    
    
    
   

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getTaobaoKeId() {
		return taobaoKeId;
	}

	public void setTaobaoKeId(String taobaoKeId) {
		this.taobaoKeId = taobaoKeId;
	}

	public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    @JsonIgnore
    private List<Map<String, Object>> entityAdCpsChannelList;
 
    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
   
    
    
    
    public Long getStartTime() {
        return startTime;
    }

    public Long getTaobaoKeGoodsId() {
		return taobaoKeGoodsId;
	}

	public void setTaobaoKeGoodsId(Long taobaoKeGoodsId) {
		this.taobaoKeGoodsId = taobaoKeGoodsId;
	}

	public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<EntityAdCpsChannel> getEntityAdCpsChannel() {
        return entityAdCpsChannel;
    }

    public void setEntityAdCpsChannel(List<EntityAdCpsChannel> entityAdCpsChannel) {
        this.entityAdCpsChannel = entityAdCpsChannel;
    }

    public List<Map<String, Object>> getEntityAdCpsChannelList() {
        return entityAdCpsChannelList;
    }

    public void setEntityAdCpsChannelList(List<Map<String, Object>> entityAdCpsChannelList) {
        this.entityAdCpsChannelList = entityAdCpsChannelList;
    }
 

	public String getShopIds() {
		return shopIds;
	}

	public void setShopIds(String shopIds) {
		this.shopIds = shopIds;
	}
	

	public String getFavoritesId() {
		return favoritesId;
	}

	public void setFavoritesId(String favoritesId) {
		this.favoritesId = favoritesId;
	}
	
	
	
	

	public Long getEntityCPSLogId() {
		return entityCPSLogId;
	}

	public void setEntityCPSLogId(Long entityCPSLogId) {
		this.entityCPSLogId = entityCPSLogId;
	}

	/**
     * 数据传输转换
     */
    @Transient
    public void transferAdCpsChannel(){
        this.entityAdCpsChannel = new ArrayList<EntityAdCpsChannel>();
        for (Map<String, Object> anEntityAdCpsChannelList : entityAdCpsChannelList) {
            EntityAdCpsChannel entityAdCpsChannel = new EntityAdCpsChannel();
            entityAdCpsChannel.setChannelType(
                    Integer.parseInt(anEntityAdCpsChannelList.get("channelType").toString())
            );
            entityAdCpsChannel.setChannelCommission(
                    anEntityAdCpsChannelList.get("channelCommission").toString()
            );
            entityAdCpsChannel.setChannelCommissionType(
                    Integer.parseInt(anEntityAdCpsChannelList.get("channelCommissionType").toString())
            );
            entityAdCpsChannel.setChannelEndTime(
            		Long.parseLong(anEntityAdCpsChannelList.get("channelEndTime").toString())
            );
            entityAdCpsChannel.setChannelStartTime(
            		Long.valueOf(anEntityAdCpsChannelList.get("channelStartTime").toString())
            );
            entityAdCpsChannel.setChannelGoodsId(
                    Long.parseLong(anEntityAdCpsChannelList.get("channelGoodsId").toString())
            );
            entityAdCpsChannel.setChannelLink(
                    anEntityAdCpsChannelList.get("channelLink").toString()
            );
            if (null != anEntityAdCpsChannelList.get("androidLink")) {
                entityAdCpsChannel.setAndroidLink(
                        anEntityAdCpsChannelList.get("androidLink").toString()
                );
            }
            if (null != anEntityAdCpsChannelList.get("iosLink")){
                entityAdCpsChannel.setIosLink(
                        anEntityAdCpsChannelList.get("iosLink").toString()
                );
            }
            if (null != anEntityAdCpsChannelList.get("price")) {
                entityAdCpsChannel.setPrice(
                        Float.valueOf(anEntityAdCpsChannelList.get("price").toString())
                );
            }
            if(null == anEntityAdCpsChannelList.get("hidden") ||
                    StringUtils.isBlank(anEntityAdCpsChannelList.get("hidden").toString())){
                anEntityAdCpsChannelList.put("hidden", 0);
            }
            entityAdCpsChannel.setHidden(
                    Integer.parseInt(anEntityAdCpsChannelList.get("hidden").toString())
            );
            this.entityAdCpsChannel.add(entityAdCpsChannel);
        }
    }
}
