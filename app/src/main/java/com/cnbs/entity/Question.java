package com.cnbs.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/1/7.
 */
public class Question implements Parcelable {

    private int id;

    private int type;

    private String title;

    private String AnswerA;

    private String AnswerB;

    private String AnswerC;

    private String AnswerD;

    private String rightAnswer;//单选或多选

    private int isRight;//判断题

    private String myAnswer;

    private String myNote;


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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnswerA() {
        return AnswerA;
    }

    public void setAnswerA(String answerA) {
        AnswerA = answerA;
    }

    public String getAnswerB() {
        return AnswerB;
    }

    public void setAnswerB(String answerB) {
        AnswerB = answerB;
    }

    public String getAnswerC() {
        return AnswerC;
    }

    public void setAnswerC(String answerC) {
        AnswerC = answerC;
    }

    public String getAnswerD() {
        return AnswerD;
    }

    public void setAnswerD(String answerD) {
        AnswerD = answerD;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public int getIsRight() {
        return isRight;
    }

    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public String getMyAnswer() {
        return myAnswer;
    }

    public void setMyAnswer(String myAnswer) {
        this.myAnswer = myAnswer;
    }

    public String getMyNote() {
        return myNote;
    }

    public void setMyNote(String myNote) {
        this.myNote = myNote;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.title);
        dest.writeString(this.AnswerA);
        dest.writeString(this.AnswerB);
        dest.writeString(this.AnswerC);
        dest.writeString(this.AnswerD);
        dest.writeString(this.rightAnswer);
        dest.writeInt(this.isRight);
        dest.writeString(this.myAnswer);
        dest.writeString(this.myNote);
    }

    public Question() {
    }

    protected Question(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.title = in.readString();
        this.AnswerA = in.readString();
        this.AnswerB = in.readString();
        this.AnswerC = in.readString();
        this.AnswerD = in.readString();
        this.rightAnswer = in.readString();
        this.isRight = in.readInt();
        this.myAnswer = in.readString();
        this.myNote = in.readString();
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
