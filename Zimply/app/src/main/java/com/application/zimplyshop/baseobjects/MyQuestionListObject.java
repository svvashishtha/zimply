package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 2/5/2016.
 */
public class MyQuestionListObject implements Serializable{

    public int count;

    public String next_url;

    public ArrayList<MyQuestionAnswerObject> all;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }

    public ArrayList<MyQuestionAnswerObject> getAll() {
        return all;
    }

    public void setAll(ArrayList<MyQuestionAnswerObject> all) {
        this.all = all;
    }
}
