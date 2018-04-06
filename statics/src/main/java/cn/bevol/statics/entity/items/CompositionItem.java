package cn.bevol.statics.entity.items;

import java.util.List;

/**
 * Created by Rc. on 2017/2/10.
 * 成分查询返回实体
 */
public class CompositionItem {
    private List<String> useds;//使用目的
    private String active;//活性成分
    private String title;//标题
    private String safety; //安全风险
    private String acneRisk;//致痘风险

    public List<String> getUseds() {
        return useds;
    }

    public void setUseds(List<String> useds) {
        this.useds = useds;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }

    public String getAcneRisk() {
        return acneRisk;
    }

    public void setAcneRisk(String acneRisk) {
        this.acneRisk = acneRisk;
    }
}
