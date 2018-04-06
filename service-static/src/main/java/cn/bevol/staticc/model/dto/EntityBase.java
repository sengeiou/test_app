package cn.bevol.staticc.model.dto;

import java.io.Serializable;

/**
 * MYSQL使用的实体基类
 *
 * @author ruanchen
 */
public class EntityBase implements Serializable {

    private Long id;

    private Long entityId;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
	
}
