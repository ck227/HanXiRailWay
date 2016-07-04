package com.cnbs.entity;

/**
 * Created by Administrator on 2016/1/19.
 */
public class UpdateDecision {


    /**
     * answer : true
     * content : 运输生产单位的人员当班严禁穿高、中跟鞋，以防扭伤。
     * id : 11583
     * jobId : 40
     * typeId : 3
     * valid : true
     */

    private boolean answer;
    private String content;
    private int id;
    private int jobId;
    private int typeId;
    private boolean valid;

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isAnswer() {
        return answer;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public int getJobId() {
        return jobId;
    }

    public int getTypeId() {
        return typeId;
    }

    public boolean isValid() {
        return valid;
    }
}
