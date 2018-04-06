package cn.bevol.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection="entity_ad_cps")
public class EntityAdCps extends EntityBase {

    //商品id
    private Long goodsId;

    //商品名
    @Transient
    private String title;

    //cps开始时间
    private Integer startTime;

    //cps结束时间
    private Integer endTime;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
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

    /**
     * 数据传输转换
     */
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
                    Integer.parseInt(anEntityAdCpsChannelList.get("channelEndTime").toString())
            );
            entityAdCpsChannel.setChannelStartTime(
                    Integer.parseInt(anEntityAdCpsChannelList.get("channelStartTime").toString())
            );
            entityAdCpsChannel.setChannelGoodsId(
                    Integer.parseInt(anEntityAdCpsChannelList.get("channelGoodsId").toString())
            );
            entityAdCpsChannel.setChannelLink(
                    anEntityAdCpsChannelList.get("channelLink").toString()
            );
            this.entityAdCpsChannel.add(entityAdCpsChannel);
        }
    }
}
