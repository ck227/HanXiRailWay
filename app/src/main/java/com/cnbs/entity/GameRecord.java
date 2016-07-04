package com.cnbs.entity;

/**
 * Created by Administrator on 2016/1/26.
 */
public class GameRecord {


    /**
     * break_id : 1
     * right_count : 10
     * type_id : 10
     */

    private int break_id;
    private int right_count;
    private int type_id;

    public void setBreak_id(int break_id) {
        this.break_id = break_id;
    }

    public void setRight_count(int right_count) {
        this.right_count = right_count;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public int getBreak_id() {
        return break_id;
    }

    public int getRight_count() {
        return right_count;
    }

    public int getType_id() {
        return type_id;
    }
}
