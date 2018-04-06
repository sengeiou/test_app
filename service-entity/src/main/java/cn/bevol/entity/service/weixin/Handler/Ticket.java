package cn.bevol.entity.service.weixin.Handler;

import com.io97.utils.DateUtils;

import java.io.Serializable;

/**
 * Created by mysens on 17-3-31.
 */
public class Ticket implements Serializable {
    private Integer errcode; //错误码
    private String errmsg; //错误消息
    private String ticket;
    private Integer expiresIn;
    private Long createTime;

    public Ticket(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public Ticket(Integer errcode, String errmsg, String ticket, Integer expiresIn) {
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.ticket = ticket;
        this.expiresIn = expiresIn;
        createTime = (long) DateUtils.nowInSeconds();
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getticket() {
        return ticket;
    }

    public void setticket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 是否超时，微信默认7200s超时
     * @return true-超时；false-没有超时
     */
    public boolean isExpires(){
        long now = DateUtils.nowInSeconds();
        return now - this.createTime - 10 >= this.expiresIn; //预留 10s
    }
}
