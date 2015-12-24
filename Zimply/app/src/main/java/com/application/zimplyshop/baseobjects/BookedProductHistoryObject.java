package com.application.zimplyshop.baseobjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/15/2015.
 */
public class BookedProductHistoryObject implements Serializable{

    private ArrayList<LatestBookingObject> books;

    private String next_url;

    public ArrayList<LatestBookingObject> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<LatestBookingObject> books) {
        this.books = books;
    }

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }
}
