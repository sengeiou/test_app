package cn.bevol.model.entity;

public class EntityAdCpsChannel {

    /**
     * 渠道类型 1.淘宝，  2京东
     */
    private Integer channelType;

    /**
     * 渠道链接
     */
    private String channelLink;

    /**
     * 渠道名字
     */
    private String channelName;

    /**
     * 渠道开始时间
     */
    private Integer channelStartTime;

    /**
     * 渠道结束时间
     */
    private Integer channelEndTime;

    /**
     * 渠道佣金形式  1.百分比  2.定额
     */
    private Integer channelCommissionType;

    /**
     * 渠道佣金
     */
    private String channelCommission;

    /**
     * 渠道商品id
     */
    private Integer channelGoodsId;

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        if(channelType == 1){
            this.channelName = "淘宝";
        }else if(channelType == 2){
            this.channelName = "京东";
        }
        this.channelType = channelType;
    }

    public String getChannelLink() {
        return channelLink;
    }

    public void setChannelLink(String channelLink) {
        this.channelLink = channelLink;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getChannelStartTime() {
        return channelStartTime;
    }

    public void setChannelStartTime(Integer channelStartTime) {
        this.channelStartTime = channelStartTime;
    }

    public Integer getChannelEndTime() {
        return channelEndTime;
    }

    public void setChannelEndTime(Integer channelEndTime) {
        this.channelEndTime = channelEndTime;
    }

    public Integer getChannelCommissionType() {
        return channelCommissionType;
    }

    public void setChannelCommissionType(Integer channelCommissionType) {
        this.channelCommissionType = channelCommissionType;
    }

    public String getChannelCommission() {
        return channelCommission;
    }

    public void setChannelCommission(String channelCommission) {
        this.channelCommission = channelCommission;
    }

    public Integer getChannelGoodsId() {
        return channelGoodsId;
    }

    public void setChannelGoodsId(Integer channelGoodsId) {
        this.channelGoodsId = channelGoodsId;
    }
}
