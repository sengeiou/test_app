package cn.bevol.app.entity.dto;

import cn.bevol.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by mysens on 17-6-5.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DirtyCompositionDTO {
    private Long id;
    @JsonIgnore
    private Integer[] idsArr;
    private String name;
    private Integer pid;
    private String crdate;
    private String tstamp;
    private Integer deleteStatus;
    private Integer alias;
    private String cmName;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtil.sanitizedName(name);
        setCmName(name);
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getCrdate() {
        return crdate;
    }

    public void setCrdate(String crdate) {
        this.crdate = crdate;
    }

    public String getTstamp() {
        return tstamp;
    }

    public void setTstamp(String tstamp) {
        this.tstamp = tstamp;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deletedStatus) {
        this.deleteStatus = deletedStatus;
    }

    public Integer getAlias() {
        return alias;
    }

    public void setAlias(Integer alias) {
        this.alias = alias;
    }

    public String getCmName() {
        return cmName;
    }

    public void setCmName(String cmName) {
        this.cmName = StringUtil.formatStandardName(cmName);
    }

    public Integer[] getIdsArr() {
        return idsArr;
    }

    public void setIdsArr(Integer[] idsArr) {
        this.idsArr = idsArr;
    }
}
