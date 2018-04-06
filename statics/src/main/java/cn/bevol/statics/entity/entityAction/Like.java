package cn.bevol.statics.entity.entityAction;

/**
 * 喜欢
 *
 * @author hualong
 */
public class Like extends EntityActionBase {

    private Integer type; //1喜欢 2不喜欢

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
