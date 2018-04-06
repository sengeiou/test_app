package cn.bevol.staticc.model.entity;

import java.util.List;

/**
 * Created by Rc. on 2017/2/17.
 */
public class EntityUserPart {
    /**
     * 标签信息
     */
    private List<Integer> tags;
    private Long pEntityId;

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public Long getpEntityId() {
        return pEntityId;
    }

    public void setpEntityId(Long pEntityId) {
        this.pEntityId = pEntityId;
    }
}
