package com.application.zimplyshop.db;

import android.content.Context;

import com.application.zimplyshop.baseobjects.RecentSearchObject;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 1/19/2016.
 */
public class RecentSearchesDBWrapper {
    public static RecentSearchesDBManager helper;

    public static void Initialize(Context context) {
        helper = new RecentSearchesDBManager(context);
    }

    public static int addProduct(RecentSearchObject tag, int userId, long timestamp) {
        return helper.addProduct(tag, userId, timestamp);
    }

    public static int removeProducts(int userId) {
        return helper.removeUsers(userId);
    }

    public static ArrayList<RecentSearchObject> getProducts(int userId) {
        return helper.getProducts(userId);
    }
}
