package cn.bevol.staticc.model.entity;

/**
 * Created by Rc. on 2017/2/21.
 */
public class UserPartDetail {
    /**
     * 1、实体
     * 2、文字
     * 3、图片
     * [{type:"1",id:"14","tname":"goods"},{type:"2",content:"3332"},{type:"3",image:"sss"}]
     */
    private Integer type;
    private String tname ;
    private String title;
    private String mid;
    private String content;

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
