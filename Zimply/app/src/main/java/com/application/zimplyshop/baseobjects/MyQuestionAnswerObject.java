package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 2/5/2016.
 */
public class MyQuestionAnswerObject implements Serializable{
    public QuestionObject ques;

    public ArrayList<AnswerObject> ans;

    public BaseProductListObject product;

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

    public BaseProductListObject getProduct() {
        return product;
    }

    public void setProduct(BaseProductListObject product) {
        this.product = product;
    }
}
