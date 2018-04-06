package cn.bevol.staticc.model.entity;

/**
 * Created by mysens on 17-3-15.
 * 对应产品扩展表hq_goods_ext！！！！！！！
 */
public class GoodsExt {
    private Integer id;
    private String defCps;
    private String gcCps;
    private String mfjCps;
    private String defExtCps;
    private String cpsType;
    private Long goodsId;
    private Integer allowComment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDefCps() {
        return defCps;
    }

    public void setDefCps(String defCps) {
        this.defCps = defCps;
    }

    public String getGcCps() {
        return gcCps;
    }

    public void setGcCps(String gcCps) {
        this.gcCps = gcCps;
    }

    public String getMfjCps() {
        return mfjCps;
    }

    public void setMfjCps(String mfjCps) {
        this.mfjCps = mfjCps;
    }

    public String getDefExtCps() {
        return defExtCps;
    }

    public void setDefExtCps(String defExtCps) {
        this.defExtCps = defExtCps;
    }

    public String getCpsType() {
        return cpsType;
    }

    public void setCpsType(String cpsType) {
        this.cpsType = cpsType;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Integer allowComment) {
        this.allowComment = allowComment;
    }
}
