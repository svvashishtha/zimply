package com.application.zimplyshop.db;

import android.content.Context;

import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.ProductObject;

import java.util.ArrayList;

public class RecentProductsDBWrapper {

    public static RecentProductsDBManager helper;

    public static void Initialize(Context context) {
        helper = new RecentProductsDBManager(context);
    }

    public static int addProduct(HomeProductObj location, int userId, long timestamp) {
        return helper.addProduct(location, userId, timestamp);
    }

    public static ArrayList<HomeProductObj> getProducts(int userId) {
        return helper.getProducts(userId);
    }
}