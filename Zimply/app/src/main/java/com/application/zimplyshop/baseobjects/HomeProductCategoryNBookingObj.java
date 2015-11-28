package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/27/2015.
 */
public class HomeProductCategoryNBookingObj {

    private ArrayList<CategoryObject> product_category;

    private ArrayList<LatestBookingObject> latest_bookings;

    public ArrayList<CategoryObject> getProduct_category() {
        return product_category;
    }

    public void setProduct_category(ArrayList<CategoryObject> product_category) {
        this.product_category = product_category;
    }

    public ArrayList<LatestBookingObject> getLatest_bookings() {
        if(latest_bookings == null){
            latest_bookings = new ArrayList<>();
        }
        return latest_bookings;
    }

    public void setLatest_bookings(ArrayList<LatestBookingObject> latest_bookings) {
        this.latest_bookings = latest_bookings;
    }
}
