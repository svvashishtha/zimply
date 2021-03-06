package com.application.zimply.db;

import android.content.Context;

import com.application.zimply.baseobjects.HomeProductObj;

import java.util.ArrayList;

public class RecentProductsDBWrapper {

    public static RecentProductsDBManager helper;

    public static void Initialize(Context context) {
        helper = new RecentProductsDBManager(context);
    }

    public static int addProduct(HomeProductObj location, int userId, int wishId, long timestamp) {
        return helper.addProduct(location, userId, wishId, timestamp);
    }

    public static ArrayList<HomeProductObj> getProducts(int userId) {
        return helper.getProducts(userId);
    }
}