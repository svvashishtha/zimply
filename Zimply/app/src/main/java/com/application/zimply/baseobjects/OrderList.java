package com.application.zimply.baseobjects;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 10/10/2015.
 */
public class OrderList {

    private ArrayList<OrderItemObj> orders;

    public ArrayList<OrderItemObj> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<OrderItemObj> orders) {
        this.orders = orders;
    }
}
