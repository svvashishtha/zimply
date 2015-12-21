package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/2/2015.
 */
public class MyWishListObject {

    private ArrayList<FavouriteObject> favourite;

    private String next_url;

    public ArrayList<FavouriteObject> getFavourite() {
        if(favourite == null)
            favourite = new ArrayList<>();
        return favourite;
    }

    public void setFavourite(ArrayList<FavouriteObject> favourite) {
        this.favourite = favourite;
    }

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }
}
