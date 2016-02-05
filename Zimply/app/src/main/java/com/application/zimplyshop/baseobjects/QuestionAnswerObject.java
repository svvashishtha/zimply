package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 2/1/2016.
 */
public class QuestionAnswerObject implements Serializable{

    public QuestionObject ques;

    public ArrayList<AnswerObject> ans;

    public QuestionObject getQues() {
        return ques;
    }

    public void setQues(QuestionObject ques) {
        this.ques = ques;
    }

    public ArrayList<AnswerObject> getAns() {
        return ans;
    }

    public void setAns(ArrayList<AnswerObject> ans) {
        this.ans = ans;
    }
}
