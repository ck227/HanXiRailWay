package com.cnbs.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/1/15.
 */
public class UpdateChoice {


    /**
     * answer : BC
     * choiceContent : 8月18日，大庆站组织加格达奇—哈尔滨K7050次（新空）旅客出站时，一旅客持8月17日加格达奇—大庆新空硬座快速卧代用票1张，发现漏收空调费，应（）
     * choiceNum : 4
     * choiceParse :
     * id : 8782
     * jobId : 6
     * list : [{"answer":false,"choiceId":8782,"id":35125,"itemContent":"不予追究","itemNumber":"A","priority":1},{"answer":false,"choiceId":8782,"id":35126,"itemContent":"补收加格达奇\u2014大庆间的空调费","itemNumber":"B","priority":2},{"answer":false,"choiceId":8782,"id":35127,"itemContent":"不收手续费","itemNumber":"C","priority":3},{"answer":false,"choiceId":8782,"id":35128,"itemContent":"核收手续费","itemNumber":"D","priority":4}]
     * typeId : 2
     * valid : true
     */

    private String answer;
    private String choiceContent;
    private int choiceNum;
    private String choiceParse;
    private int id;
    private int jobId;
    private int typeId;
    private boolean valid;
    /**
     * answer : false
     * choiceId : 8782
     * id : 35125
     * itemContent : 不予追究
     * itemNumber : A
     * priority : 1
     */

    private List<ListEntity> list;

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setChoiceContent(String choiceContent) {
        this.choiceContent = choiceContent;
    }

    public void setChoiceNum(int choiceNum) {
        this.choiceNum = choiceNum;
    }

    public void setChoiceParse(String choiceParse) {
        this.choiceParse = choiceParse;
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

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public String getAnswer() {
        return answer;
    }

    public String getChoiceContent() {
        return choiceContent;
    }

    public int getChoiceNum() {
        return choiceNum;
    }

    public String getChoiceParse() {
        return choiceParse;
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

    public List<ListEntity> getList() {
        return list;
    }

    public static class ListEntity {
        private boolean answer;
        private int choiceId;
        private int id;
        private String itemContent;
        private String itemNumber;
        private int priority;

        public void setAnswer(boolean answer) {
            this.answer = answer;
        }

        public void setChoiceId(int choiceId) {
            this.choiceId = choiceId;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setItemContent(String itemContent) {
            this.itemContent = itemContent;
        }

        public void setItemNumber(String itemNumber) {
            this.itemNumber = itemNumber;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public boolean isAnswer() {
            return answer;
        }

        public int getChoiceId() {
            return choiceId;
        }

        public int getId() {
            return id;
        }

        public String getItemContent() {
            return itemContent;
        }

        public String getItemNumber() {
            return itemNumber;
        }

        public int getPriority() {
            return priority;
        }
    }
}
