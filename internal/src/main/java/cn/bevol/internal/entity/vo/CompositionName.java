package cn.bevol.internal.entity.vo;

import java.io.Serializable;

/**
 * Created by Rc. on 2017-05-16.
 */
public class CompositionName implements Serializable {
    private String title;
    private String english;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
