package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 2/1/2016.
 */
public class AskUsQuestionsObject implements Serializable{

    public ArrayList<QuestionAnswerObject> all;

    public String next_url;

    public int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<QuestionAnswerObject> getAll() {
        return all;
    }

    public void setAll(ArrayList<QuestionAnswerObject> all) {
        this.all = all;
    }

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }
}
