package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/2/2015.
 */
public class MyWishListObject {

    private ArrayList<HomeProductObj> favourite;

    private String next_url;

    public ArrayList<HomeProductObj> getFavourite() {
        return favourite;
    }

    public void setFavourite(ArrayList<HomeProductObj> favourite) {
        this.favourite = favourite;
    }

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }
}
