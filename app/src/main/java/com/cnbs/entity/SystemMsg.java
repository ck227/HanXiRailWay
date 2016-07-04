package com.cnbs.entity;

/**
 * Created by Administrator on 2016/1/22.
 */
public class SystemMsg {


    /**
     * content : 新的APP已经发布啦，大家赶紧更新
     * id : 1
     * isVisble : 1
     * time : 2016年01月22日
     * title : 系统消息
     */

    private String content;
    private int id;
    private int isVisble;
    private String time;
    private String title;

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsVisble(int isVisble) {
        this.isVisble = isVisble;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public int getIsVisble() {
        return isVisble;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }
}
