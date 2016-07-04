package com.cnbs.entity;

/**
 * Created by Administrator on 2016/1/14.
 */
public class TestResult {

    private String end_time;

    private String user_answer;

    private int right_amount;

    private String desc;//显示职称信息？

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getUser_answer() {
        return user_answer;
    }

    public void setUser_answer(String user_answer) {
        this.user_answer = user_answer;
    }

    public int getRight_amount() {
        return right_amount;
    }

    public void setRight_amount(int right_amount) {
        this.right_amount = right_amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
