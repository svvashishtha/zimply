package com.application.zimplyshop.baseobjects;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 10/10/2015.
 */
public class OrderList {

    private ArrayList<OrderItemObj> orders;

    private String next_url;

    public String getNext_url() {
        return next_url;
    }

    public void setNext_url(String next_url) {
        this.next_url = next_url;
    }

    public ArrayList<OrderItemObj> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<OrderItemObj> orders) {
        this.orders = orders;
    }
}
