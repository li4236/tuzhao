package com.tuzhao.info;

/**
 * Created by juncoder on 2018/11/7.
 */
public class FilterInfo {

    private int type;

    private boolean choose;

    private String content;

    public FilterInfo(int type, boolean choose, String content) {
        this.type = type;
        this.choose = choose;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
