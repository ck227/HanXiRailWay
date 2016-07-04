package com.cnbs.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 闯关
 * Created by Administrator on 2016/1/14.
 */
public class Game implements Parcelable {


    /**
     * break_id : 1
     * break_name : 关卡一
     * decision_count : 5
     * description : 12,18,24
     * job_id : 40
     * multi_choice_count : 5
     * rank_id : 5
     * right_count : 0
     * single_choice_count : 5
     * time_limit : 5
     * type_id : 0
     */

    private int break_id;
    private String break_name;
    private int decision_count;
    private String description;
    private int job_id;
    private int multi_choice_count;
    private int rank_id;
    private int right_count;
    private int single_choice_count;
    private int time_limit;
    private int type_id;

    public void setBreak_id(int break_id) {
        this.break_id = break_id;
    }

    public void setBreak_name(String break_name) {
        this.break_name = break_name;
    }

    public void setDecision_count(int decision_count) {
        this.decision_count = decision_count;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public void setMulti_choice_count(int multi_choice_count) {
        this.multi_choice_count = multi_choice_count;
    }

    public void setRank_id(int rank_id) {
        this.rank_id = rank_id;
    }

    public void setRight_count(int right_count) {
        this.right_count = right_count;
    }

    public void setSingle_choice_count(int single_choice_count) {
        this.single_choice_count = single_choice_count;
    }

    public void setTime_limit(int time_limit) {
        this.time_limit = time_limit;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public int getBreak_id() {
        return break_id;
    }

    public String getBreak_name() {
        return break_name;
    }

    public int getDecision_count() {
        return decision_count;
    }

    public String getDescription() {
        return description;
    }

    public int getJob_id() {
        return job_id;
    }

    public int getMulti_choice_count() {
        return multi_choice_count;
    }

    public int getRank_id() {
        return rank_id;
    }

    public int getRight_count() {
        return right_count;
    }

    public int getSingle_choice_count() {
        return single_choice_count;
    }

    public int getTime_limit() {
        return time_limit;
    }

    public int getType_id() {
        return type_id;
    }

    public int geSum() {
        return single_choice_count + multi_choice_count + decision_count;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.break_id);
        dest.writeString(this.break_name);
        dest.writeInt(this.decision_count);
        dest.writeString(this.description);
        dest.writeInt(this.job_id);
        dest.writeInt(this.multi_choice_count);
        dest.writeInt(this.rank_id);
        dest.writeInt(this.right_count);
        dest.writeInt(this.single_choice_count);
        dest.writeInt(this.time_limit);
        dest.writeInt(this.type_id);
    }

    public Game() {
    }

    protected Game(Parcel in) {
        this.break_id = in.readInt();
        this.break_name = in.readString();
        this.decision_count = in.readInt();
        this.description = in.readString();
        this.job_id = in.readInt();
        this.multi_choice_count = in.readInt();
        this.rank_id = in.readInt();
        this.right_count = in.readInt();
        this.single_choice_count = in.readInt();
        this.time_limit = in.readInt();
        this.type_id = in.readInt();
    }

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        public Game createFromParcel(Parcel source) {
            return new Game(source);
        }

        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
}
