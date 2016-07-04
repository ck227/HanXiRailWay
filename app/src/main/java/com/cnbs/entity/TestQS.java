package com.cnbs.entity;


/**
 * 测试的题目id和答案的实体类
 * Created by Administrator on 2016/1/13.
 */
public class TestQS {

    private int id;

    private int type;

    private String answer;

    private int is_right;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getIs_right() {
        return is_right;
    }

    public void setIs_right(int is_right) {
        this.is_right = is_right;
    }
}
